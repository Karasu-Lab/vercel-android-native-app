package com.karasu256.vcnative.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.repository.ApiKeyRepository
import com.karasu256.vcnative.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val user = runCatching { userRepository.restore() }.getOrNull()
            if (user == null) logout() else _uiState.value = HomeUiState.Ready(user.username)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.write(null)
            apiKeyRepository.write(null)
            _events.send(HomeEvent.LoggedOut)
        }
    }
}
