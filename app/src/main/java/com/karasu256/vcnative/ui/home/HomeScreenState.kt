package com.karasu256.vcnative.ui.home

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Ready(val username: String) : HomeUiState
}

sealed interface HomeEvent {
    data object LoggedOut : HomeEvent
}
