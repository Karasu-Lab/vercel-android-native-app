package com.karasu256.vcnative.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.R
import com.karasu256.vcnative.data.remote.VercelApi
import com.karasu256.vcnative.data.repository.ApiKeyRepository
import com.karasu256.vcnative.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val userRepository: UserRepository,
    private val api: VercelApi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<MainEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val apiKey = apiKeyRepository.read() ?: return@launch
            _uiState.update { it.copy(apiKey = apiKey) }
        }
    }

    fun onApiKeyChange(apiKey: String) {
        _uiState.update { it.copy(apiKey = apiKey) }
    }

    fun login() {
        if (_uiState.value.isLoading) return
        val key = _uiState.value.apiKey.trim()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorRes = null) }
            try {
                apiKeyRepository.write(key)
                val user = api.getUser(key)
                userRepository.write(user)
                _events.send(MainEvent.NavigateShell)
            } catch (e: CancellationException) {
                throw e
            } catch (e: ClientRequestException) {
                apiKeyRepository.write(null)
                _uiState.update { it.copy(errorRes = R.string.error_invalid_api_key) }
            } catch (e: Exception) {
                apiKeyRepository.write(null)
                _uiState.update { it.copy(errorRes = R.string.error_login_failed) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
