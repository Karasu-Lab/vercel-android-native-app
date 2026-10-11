package com.karasu256.vcnative.data.repository

import android.net.Uri
import android.util.Base64
import com.karasu256.vcnative.BuildConfig
import com.karasu256.vcnative.data.remote.GitHubApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubAuthRepository @Inject constructor(
    private val tokenRepository: GitHubTokenRepository,
    private val gitHubApi: GitHubApi,
) {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private var codeVerifier: String? = null
    private var state: String? = null

    suspend fun restore() {
        _isAuthenticated.value = tokenRepository.restore() != null
    }

    suspend fun read(): String? = tokenRepository.read()

    fun authorizationUri(): Uri {
        val verifier = randomUrlSafe(64)
        val newState = randomUrlSafe(16)
        codeVerifier = verifier
        state = newState
        val challenge = Base64.encodeToString(
            MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray()),
            Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP,
        )
        return Uri.parse(AUTHORIZE_URL).buildUpon()
            .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("state", newState)
            .appendQueryParameter("code_challenge", challenge)
            .appendQueryParameter("code_challenge_method", "S256")
            .build()
    }

    suspend fun handleCallback(uri: Uri): Boolean {
        if (!uri.toString().startsWith(REDIRECT_URI)) return false
        val code = uri.getQueryParameter("code") ?: return false
        val verifier = codeVerifier ?: return false
        if (uri.getQueryParameter("state") != state) return false
        codeVerifier = null
        state = null

        val token = try {
            gitHubApi.exchangeCode(code, verifier, REDIRECT_URI).accessToken
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        } ?: return false
        tokenRepository.write(token)
        _isAuthenticated.value = true
        return true
    }

    suspend fun unlink() {
        tokenRepository.read()?.let { token ->
            try {
                gitHubApi.revokeToken(token)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Unit
            }
        }
        tokenRepository.write(null)
        _isAuthenticated.value = false
    }

    private fun randomUrlSafe(byteCount: Int): String {
        val bytes = ByteArray(byteCount).also { SecureRandom().nextBytes(it) }
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }

    companion object {
        const val AUTHORIZE_URL = "https://github.com/login/oauth/authorize"
        const val REDIRECT_URI = "vcnative://auth/callback/github"
    }
}
