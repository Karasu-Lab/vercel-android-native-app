package com.karasu256.vcnative.ui.activitys

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.repository.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AppLockState {
    Checking,
    Locked,
    Unlocked,
}

@HiltViewModel
class AppLockViewModel @Inject constructor(
    private val settingsRepository: AppSettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AppLockState.Checking)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val enabled = settingsRepository.restore()?.biometricLockEnabled == true
            _state.value = if (enabled) AppLockState.Locked else AppLockState.Unlocked
        }
    }

    fun unlock() {
        _state.value = AppLockState.Unlocked
    }
}
