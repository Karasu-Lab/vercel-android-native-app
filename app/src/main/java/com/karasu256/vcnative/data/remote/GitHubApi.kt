package com.karasu256.vcnative.data.remote

import com.karasu256.vcnative.BuildConfig
import com.karasu256.vcnative.data.model.GitHubBranchDto
import com.karasu256.vcnative.data.model.GitHubCommitDto
import com.karasu256.vcnative.data.model.GitHubTokenResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.basicAuth
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubApi @Inject constructor(
    private val client: HttpClient,
) {
    suspend fun exchangeCode(code: String, codeVerifier: String, redirectUri: String): GitHubTokenResponseDto =
        client.post("https://github.com/login/oauth/access_token") {
            accept(ContentType.Application.Json)
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("client_id", BuildConfig.GITHUB_CLIENT_ID)
                        append("client_secret", BuildConfig.githubclientSecret)
                        append("code", code)
                        append("redirect_uri", redirectUri)
                        append("code_verifier", codeVerifier)
                    }
                )
            )
        }.body()

    suspend fun getBranches(token: String, owner: String, repo: String): List<GitHubBranchDto> =
        client.get("$API_URL/repos/$owner/$repo/branches") {
            bearerAuth(token)
            accept(ContentType.parse("application/vnd.github+json"))
            parameter("per_page", 100)
        }.body()

    suspend fun getCommit(token: String, owner: String, repo: String, ref: String): GitHubCommitDto =
        client.get("$API_URL/repos/$owner/$repo/commits/$ref") {
            bearerAuth(token)
            accept(ContentType.parse("application/vnd.github+json"))
        }.body()

    suspend fun revokeToken(token: String) {
        client.delete("$API_URL/applications/${BuildConfig.GITHUB_CLIENT_ID}/token") {
            basicAuth(BuildConfig.GITHUB_CLIENT_ID, BuildConfig.githubclientSecret)
            accept(ContentType.parse("application/vnd.github+json"))
            contentType(ContentType.Application.Json)
            setBody(mapOf("access_token" to token))
        }
    }

    companion object {
        const val API_URL = "https://api.github.com"
    }
}
