package com.karasu256.vcnative.ui.detail

/**
 * Single, exhaustive representation of everything [DetailScreen] can render, following the
 * same unidirectional data flow (UDF) pattern as [com.karasu256.vcnative.ui.home.HomeUiState].
 */
sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Content(val id: Int) : DetailUiState
}

/** User intents that [DetailScreen] can dispatch to [DetailViewModel]. */
sealed interface DetailEvent {
    data class LoadDetail(val id: Int) : DetailEvent
}
