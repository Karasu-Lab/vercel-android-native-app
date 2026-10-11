package com.karasu256.vcnative.ui.shell

import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

@HiltViewModel
class ShellViewModel @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val userRepository: UserRepository,
    private val api: VercelApi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShellUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<ShellEvent>(Channel.BUFFERED)
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

    fun showSheet(sheet: ShellSheet) {
        _uiState.update { it.copy(sheet = sheet) }
    }

    fun dismissSheet() {
        _uiState.update { it.copy(sheet = null) }
    }

    companion object {
        private const val AVATAR_SIZE = 128
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.write(null)
            apiKeyRepository.write(null)
            _uiState.update { it.copy(sheet = null) }
            _events.send(ShellEvent.LoggedOut)
        }
    }
}
