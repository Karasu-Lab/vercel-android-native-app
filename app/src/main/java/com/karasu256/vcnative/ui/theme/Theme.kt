package com.karasu256.vcnative.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember

object NeuTheme {
    val colors: NeuColors
        @Composable @ReadOnlyComposable get() = LocalNeuColors.current

    val typography: NeuTypography
        @Composable @ReadOnlyComposable get() = LocalNeuTypography.current

    val shapes: NeuShapes
        @Composable @ReadOnlyComposable get() = LocalNeuShapes.current

    val spacing: NeuSpacing
        @Composable @ReadOnlyComposable get() = LocalNeuSpacing.current

    val elevation: NeuElevation
        @Composable @ReadOnlyComposable get() = LocalNeuElevation.current
}

@Composable
fun NeuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) NeuDarkColors else NeuLightColors
    val selectionColors = remember(colors) {
        TextSelectionColors(
            handleColor = colors.primary,
            backgroundColor = colors.primary.copy(alpha = 0.3f),
        )
    }

    CompositionLocalProvider(
        LocalNeuColors provides colors,
        LocalNeuTypography provides DefaultNeuTypography,
        LocalNeuShapes provides NeuShapes(),
        LocalNeuSpacing provides NeuSpacing(),
        LocalNeuElevation provides NeuElevation(),
        LocalNeuContentColor provides colors.onSurface,
        LocalNeuTextStyle provides DefaultNeuTypography.bodyMedium,
        LocalTextSelectionColors provides selectionColors,
        content = content
    )
}
