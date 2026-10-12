package com.karasu256.vcnative.data.repository

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.karasu256.vcnative.data.model.VercelUser
import com.karasu256.vcnative.data.remote.VercelApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

data class UserProfile(
    val user: VercelUser? = null,
    val avatar: ImageBitmap? = null,
)

@Singleton
class UserProfileRepository @Inject constructor(
    private val userRepository: UserRepository,
    private val api: VercelApi,
) {
    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val mutex = Mutex()

    suspend fun load() = mutex.withLock {
        val user = _profile.value.user
            ?: runCatching { userRepository.restore() }.getOrNull()
            ?: return@withLock
        _profile.update { it.copy(user = user) }
        if (_profile.value.avatar == null) {
            val avatar = runCatching { api.getAvatarBitmap(user.username, AVATAR_SIZE) }
                .getOrNull()
                ?.asImageBitmap()
            _profile.update { it.copy(avatar = avatar) }
        }
    }

    fun clear() {
        _profile.value = UserProfile()
    }

    private companion object {
        const val AVATAR_SIZE = 256
    }
}
