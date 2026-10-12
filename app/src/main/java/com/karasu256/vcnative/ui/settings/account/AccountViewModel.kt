package com.karasu256.vcnative.ui.settings.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.repository.ApiKeyRepository
import com.karasu256.vcnative.data.repository.UserProfileRepository
import com.karasu256.vcnative.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AccountEvent {
    data object LoggedOut : AccountEvent
}

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository,
) : ViewModel() {

    val uiState = userProfileRepository.profile

    private val _events = Channel<AccountEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch { userProfileRepository.load() }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.write(null)
            apiKeyRepository.write(null)
            userProfileRepository.clear()
            _events.send(AccountEvent.LoggedOut)
        }
    }
}
