package com.karasu256.vcnative.ui.shell

import androidx.compose.ui.graphics.ImageBitmap
import com.karasu256.vcnative.data.model.VercelUser

enum class ShellSheet {
    User,
    Theme,
}

data class ShellUiState(
    val user: VercelUser? = null,
    val avatar: ImageBitmap? = null,
    val sheet: ShellSheet? = null,
)

sealed interface ShellEvent {
    data object LoggedOut : ShellEvent
}
