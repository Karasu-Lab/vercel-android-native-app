package com.karasu256.vcnative.data.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.caverock.androidsvg.SVG
import com.karasu256.vcnative.data.model.CreateDeploymentRequestDto
import com.karasu256.vcnative.data.model.DeleteDeploymentResponseDto
import com.karasu256.vcnative.data.model.DeploymentDetailDto
import com.karasu256.vcnative.data.model.DeploymentDto
import com.karasu256.vcnative.data.model.ProjectDto
import com.karasu256.vcnative.data.model.ProjectsResponseDto
import com.karasu256.vcnative.data.model.DeploymentStatusDto
import com.karasu256.vcnative.data.model.DeploymentsResponseDto
import com.karasu256.vcnative.data.model.VercelEvent
import com.karasu256.vcnative.data.model.VercelUser
import com.karasu256.vcnative.data.model.VercelUserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VercelApi @Inject constructor(
    private val client: HttpClient,
    private val json: Json,
) {
    suspend fun getUser(apiKey: String): VercelUser =
        client.get("$BASE_URL/v2/user") { bearerAuth(apiKey) }
            .body<VercelUserResponse>()
            .user

    suspend fun getDeployments(
        apiKey: String,
        limit: Int = 10,
        until: Long? = null,
        since: Long? = null,
        projectId: String? = null,
    ): DeploymentsResponseDto =
        client.get("$BASE_URL/v6/deployments") {
            bearerAuth(apiKey)
            parameter("limit", limit)
            projectId?.let { parameter("projectId", it) }
            until?.let { parameter("until", it) }
            since?.let { parameter("since", it) }
        }.body()

    suspend fun getDeploymentDetail(apiKey: String, id: String): DeploymentDetailDto =
        client.get("$BASE_URL/v13/deployments/$id") { bearerAuth(apiKey) }.body()

    suspend fun getScreenshotBitmap(apiKey: String, deploymentId: String, teamId: String?): Bitmap? {
        val bytes = client.get("$BASE_URL/screenshot") {
            bearerAuth(apiKey)
            parameter("deploymentId", deploymentId)
            teamId?.let { parameter("teamId", it) }
            parameter("withStatus", false)
        }.body<ByteArray>()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    suspend fun getDeploymentStatus(apiKey: String, id: String): DeploymentStatusDto =
        client.get("$BASE_URL/v13/deployments/$id") { bearerAuth(apiKey) }.body()

    fun deploymentEvents(apiKey: String, id: String): Flow<VercelEvent> = flow {
        client.prepareGet("$BASE_URL/v3/deployments/$id/events") {
            bearerAuth(apiKey)
            parameter("follow", 1)
            parameter("builds", 1)
        }.execute { response ->
            val channel = response.bodyAsChannel()
            while (true) {
                val line = channel.readUTF8Line() ?: break
                if (line.isBlank()) continue
                runCatching { json.decodeFromString<VercelEvent>(line) }
                    .getOrNull()
                    ?.let { emit(it) }
            }
        }
    }

    suspend fun getProjects(apiKey: String): List<ProjectDto> =
        client.get("$BASE_URL/v10/projects") {
            bearerAuth(apiKey)
            parameter("limit", 100)
        }.body<ProjectsResponseDto>().projects

    suspend fun getProjectDeployments(apiKey: String, projectId: String): List<DeploymentDto> =
        client.get("$BASE_URL/v6/deployments") {
            bearerAuth(apiKey)
            parameter("projectId", projectId)
            parameter("limit", 100)
        }.body<DeploymentsResponseDto>().deployments

    suspend fun createDeployment(apiKey: String, request: CreateDeploymentRequestDto) {
        client.post("$BASE_URL/v13/deployments") {
            bearerAuth(apiKey)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun cancelDeployment(apiKey: String, id: String): DeploymentStatusDto =
        client.patch("$BASE_URL/v12/deployments/$id/cancel") { bearerAuth(apiKey) }.body()

    suspend fun deleteDeployment(apiKey: String, id: String): DeleteDeploymentResponseDto =
        client.delete("$BASE_URL/v13/deployments/$id") { bearerAuth(apiKey) }.body()

    suspend fun rollbackDeployment(apiKey: String, projectId: String, deploymentId: String) {
        client.post("$BASE_URL/v1/projects/$projectId/rollback/$deploymentId") { bearerAuth(apiKey) }
    }

    suspend fun getAvatarBitmap(username: String, size: Int): Bitmap? =
        decodeAvatar(
            client.get(AVATAR_URL) {
                parameter("u", username)
                parameter("s", size)
            }.body(),
            size,
        )

    suspend fun getAvatarBitmapByHash(hash: String, size: Int): Bitmap? =
        decodeAvatar(
            client.get("$AVATAR_URL/$hash") { parameter("s", size) }.body(),
            size,
        )

    private fun decodeAvatar(bytes: ByteArray, size: Int): Bitmap? {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: runCatching {
            val svg = SVG.getFromString(bytes.decodeToString())
            Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).also {
                svg.renderToCanvas(Canvas(it))
            }
        }.getOrNull()
    }

    companion object {
        const val BASE_URL = "https://api.vercel.com"
        const val AVATAR_URL = "https://api.vercel.com/www/avatar"
    }
}
