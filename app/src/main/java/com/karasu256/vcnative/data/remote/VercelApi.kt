package com.karasu256.vcnative.data.remote

import com.karasu256.vcnative.data.model.VercelUser
import com.karasu256.vcnative.data.model.VercelUserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VercelApi @Inject constructor(
    private val client: HttpClient,
) {
    suspend fun getUser(apiKey: String): VercelUser =
        client.get("$BASE_URL/v2/user") { bearerAuth(apiKey) }
            .body<VercelUserResponse>()
            .user

    companion object {
        const val BASE_URL = "https://api.vercel.com"
    }
}
