package com.karasu256.vcnative.ui.home

import androidx.annotation.StringRes

data class MainUiState(
    val apiKey: String = "",
    val isLoading: Boolean = false,
    @StringRes val errorRes: Int? = null,
)

sealed interface MainEvent {
    data object NavigateHome : MainEvent
}
