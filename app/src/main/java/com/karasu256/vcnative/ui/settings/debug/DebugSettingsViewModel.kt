package com.karasu256.vcnative.ui.settings.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.repository.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugSettingsViewModel @Inject constructor(
    private val settingsRepository: AppSettingsRepository,
) : ViewModel() {

    val networkLoggingEnabled: StateFlow<Boolean> = settingsRepository.settings
        .map { it.networkLoggingEnabled }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = settingsRepository.settings.value.networkLoggingEnabled,
        )

    fun setNetworkLoggingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.update { it.copy(networkLoggingEnabled = enabled) }
        }
    }
}
