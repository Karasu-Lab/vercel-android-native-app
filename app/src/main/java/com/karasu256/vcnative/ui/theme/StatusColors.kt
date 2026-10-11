package com.karasu256.vcnative.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class NeuStatusColors(
    val neutral: Color,
    val progress: Color,
    val error: Color,
    val success: Color,
)

fun neuStatusColorsOf(isDark: Boolean): NeuStatusColors = if (isDark) {
    NeuStatusColors(
        neutral = Color(0xFF9E9E9E),
        progress = Color(0xFFFFB74D),
        error = Color(0xFFEF5350),
        success = Color(0xFF66BB6A),
    )
} else {
    NeuStatusColors(
        neutral = Color(0xFF8A8A8A),
        progress = Color(0xFFF57C00),
        error = Color(0xFFD32F2F),
        success = Color(0xFF2E7D32),
    )
}

internal val LocalNeuStatusColors = staticCompositionLocalOf { neuStatusColorsOf(false) }
