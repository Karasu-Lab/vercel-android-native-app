package com.karasu256.vcnative.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp

@Immutable
data class NeuColors(
    val primary: Color,
    val onPrimary: Color,
    val surface: Color,
    val onSurface: Color,
    val onSurfaceMuted: Color,
    val field: Color,
    val outline: Color,
    val disabled: Color,
    val danger: Color,
    val shadowLight: Color,
    val shadowDark: Color,
    val isDark: Boolean,
)

fun neuColorsOf(scheme: ColorScheme, isDark: Boolean): NeuColors {
    val surface = scheme.surfaceContainer
    return NeuColors(
        primary = scheme.primary,
        onPrimary = scheme.onPrimary,
        surface = surface,
        onSurface = scheme.onSurface,
        onSurfaceMuted = scheme.onSurfaceVariant,
        field = scheme.surfaceContainerLow,
        outline = scheme.outline,
        disabled = scheme.onSurface.copy(alpha = 0.38f).compositeOver(surface),
        danger = scheme.error,
        shadowLight = if (isDark) {
            lerp(surface, Color.White, 0.08f)
        } else {
            lerp(surface, Color.White, 0.75f)
        },
        shadowDark = if (isDark) {
            lerp(surface, Color.Black, 0.55f)
        } else {
            lerp(surface, Color.Black, 0.2f).copy(alpha = 0.7f)
        },
        isDark = isDark,
    )
}

internal val LocalNeuColors = staticCompositionLocalOf { neuColorsOf(lightColorScheme(), false) }

val LocalNeuContentColor = compositionLocalOf { lightColorScheme().onSurface }
