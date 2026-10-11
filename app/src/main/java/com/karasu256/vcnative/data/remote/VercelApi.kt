package com.karasu256.vcnative.data.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.caverock.androidsvg.SVG
import com.karasu256.vcnative.data.model.DeploymentStatusDto
import com.karasu256.vcnative.data.model.DeploymentsResponseDto
import com.karasu256.vcnative.data.model.VercelEvent
import com.karasu256.vcnative.data.model.VercelUser
import com.karasu256.vcnative.data.model.VercelUserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
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
    ): DeploymentsResponseDto =
        client.get("$BASE_URL/v6/deployments") {
            bearerAuth(apiKey)
            parameter("limit", limit)
            until?.let { parameter("until", it) }
        }.body()

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

    suspend fun getAvatarBitmap(username: String, size: Int): Bitmap? {
        val bytes = client.get(AVATAR_URL) {
            parameter("u", username)
            parameter("s", size)
        }.body<ByteArray>()
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
