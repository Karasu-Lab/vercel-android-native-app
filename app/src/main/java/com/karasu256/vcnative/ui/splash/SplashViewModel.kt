package com.karasu256.vcnative.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.repository.ApiKeyRepository
import com.karasu256.vcnative.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _events = Channel<SplashEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val event = try {
                if (apiKeyRepository.restore() != null && userRepository.restore() != null) {
                    SplashEvent.NavigateHome
                } else {
                    SplashEvent.NavigateMain
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: ClientRequestException) {
                apiKeyRepository.write(null)
                SplashEvent.NavigateMain
            } catch (e: Exception) {
                SplashEvent.NavigateMain
            }
            _events.send(event)
        }
    }
}
