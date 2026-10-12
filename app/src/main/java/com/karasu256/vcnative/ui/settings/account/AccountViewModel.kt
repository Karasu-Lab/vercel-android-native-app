package com.karasu256.vcnative.ui.settings.account

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.model.VercelUser
import com.karasu256.vcnative.data.remote.VercelApi
import com.karasu256.vcnative.data.repository.ApiKeyRepository
import com.karasu256.vcnative.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountUiState(
    val user: VercelUser? = null,
    val avatar: ImageBitmap? = null,
)

sealed interface AccountEvent {
    data object LoggedOut : AccountEvent
}

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val userRepository: UserRepository,
    private val api: VercelApi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<AccountEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val user = runCatching { userRepository.restore() }.getOrNull() ?: return@launch
            _uiState.update { it.copy(user = user) }
            val avatar = runCatching { api.getAvatarBitmap(user.username, AVATAR_SIZE) }.getOrNull()
                ?.asImageBitmap()
            _uiState.update { it.copy(avatar = avatar) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.write(null)
            apiKeyRepository.write(null)
            _events.send(AccountEvent.LoggedOut)
        }
    }

    private companion object {
        const val AVATAR_SIZE = 256
    }
}
