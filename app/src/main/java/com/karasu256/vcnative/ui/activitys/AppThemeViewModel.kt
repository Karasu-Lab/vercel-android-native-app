package com.karasu256.vcnative.ui.activitys

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.model.AppTheme
import com.karasu256.vcnative.data.repository.AppThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppThemeViewModel @Inject constructor(
    private val repository: AppThemeRepository,
) : ViewModel() {

    private val _theme = MutableStateFlow(AppTheme.System)
    val theme = _theme.asStateFlow()

    init {
        viewModelScope.launch {
            repository.restore()?.let { _theme.value = it }
        }
    }

    fun select(theme: AppTheme) {
        _theme.value = theme
        viewModelScope.launch { repository.write(theme) }
    }
}
