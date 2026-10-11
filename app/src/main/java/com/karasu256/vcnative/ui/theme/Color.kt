package com.karasu256.vcnative.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class NeuColors(
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val surface: Color,
    val onSurface: Color,
    val onSurfaceMuted: Color,
    val field: Color,
    val outline: Color,
    val disabled: Color,
    val success: Color,
    val warning: Color,
    val danger: Color,
    val shadowLight: Color,
    val shadowDark: Color,
    val isDark: Boolean,
)

internal val NeuLightColors = NeuColors(
    primary = Color(0xFF006666),
    onPrimary = Color(0xFFF1F2F5),
    secondary = Color(0xFFF1F2F5),
    surface = Color(0xFFE7E5E4),
    onSurface = Color(0xFF1E2938),
    onSurfaceMuted = Color(0xFF4A5565),
    field = Color(0xFFF1F2F5),
    outline = Color(0xFF857F7C),
    disabled = Color(0xFF7D7774),
    success = Color(0xFF00752B),
    warning = Color(0xFF8A5200),
    danger = Color(0xFFC8003A),
    shadowLight = Color(0xD9FFFFFF),
    shadowDark = Color(0x99A8A29E),
    isDark = false,
)

internal val NeuDarkColors = NeuColors(
    primary = Color(0xFF4FC2C2),
    onPrimary = Color(0xFF1E2938),
    secondary = Color(0xFF1F1E1D),
    surface = Color(0xFF2A2827),
    onSurface = Color(0xFFE7E5E4),
    onSurfaceMuted = Color(0xFFB5B0AD),
    field = Color(0xFF1F1E1D),
    outline = Color(0xFF7F7976),
    disabled = Color(0xFF7F7976),
    success = Color(0xFF3DD47A),
    warning = Color(0xFFFFB547),
    danger = Color(0xFFFF6B8D),
    shadowLight = Color(0x4D57524F),
    shadowDark = Color(0xCC121110),
    isDark = true,
)

internal val LocalNeuColors = staticCompositionLocalOf { NeuLightColors }

val LocalNeuContentColor = compositionLocalOf { NeuLightColors.onSurface }
