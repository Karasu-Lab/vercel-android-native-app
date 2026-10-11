package com.karasu256.vcnative.ui.shell

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf

@Immutable
data class ShellBars(
    val collapseFraction: Float = 0f,
    val bottomBarHeight: Int = 0,
)

val LocalShellBars = compositionLocalOf { ShellBars() }
