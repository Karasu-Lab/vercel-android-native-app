package com.karasu256.vcnative.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

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

    val motion: NeuMotion
        @Composable @ReadOnlyComposable get() = LocalNeuMotion.current

    val bar: NeuBarStyle
        @Composable @ReadOnlyComposable get() = LocalNeuBarStyle.current

    val statusColors: NeuStatusColors
        @Composable @ReadOnlyComposable get() = LocalNeuStatusColors.current
}

@Composable
fun NeuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val scheme = remember(context, darkTheme) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme -> dynamicDarkColorScheme(context)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> dynamicLightColorScheme(context)
            darkTheme -> darkColorScheme()
            else -> lightColorScheme()
        }
    }
    val colors = remember(scheme, darkTheme) { neuColorsOf(scheme, darkTheme) }
    val statusColors = remember(darkTheme) { neuStatusColorsOf(darkTheme) }
    val shapes = remember { NeuShapes() }
    val materialColorScheme = remember(scheme, colors) {
        scheme.copy(
            primary = colors.primary,
            onPrimary = colors.onPrimary,
            background = colors.surface,
            onBackground = colors.onSurface,
            surface = colors.surface,
            surfaceContainerLow = colors.surface,
            onSurface = colors.onSurface,
            onSurfaceVariant = colors.onSurfaceMuted,
            outline = colors.outline,
            error = colors.danger,
        )
    }
    val materialShapes = remember(shapes) {
        Shapes(
            extraSmall = shapes.sm,
            small = shapes.sm,
            medium = shapes.md,
            large = shapes.lg,
            extraLarge = shapes.lg,
        )
    }
    val selectionColors = remember(colors) {
        TextSelectionColors(
            handleColor = colors.primary,
            backgroundColor = colors.primary.copy(alpha = 0.3f),
        )
    }

    MaterialTheme(
        colorScheme = materialColorScheme,
        shapes = materialShapes,
        typography = NeuMaterialTypography,
    ) {
        CompositionLocalProvider(
            LocalNeuColors provides colors,
            LocalNeuTypography provides DefaultNeuTypography,
            LocalNeuShapes provides shapes,
            LocalNeuSpacing provides NeuSpacing(),
            LocalNeuElevation provides NeuElevation(),
            LocalNeuMotion provides NeuMotion(),
            LocalNeuBarStyle provides NeuBarStyle(),
            LocalNeuStatusColors provides statusColors,
            LocalNeuContentColor provides colors.onSurface,
            LocalNeuTextStyle provides DefaultNeuTypography.bodyMedium,
            LocalTextSelectionColors provides selectionColors,
            content = content
        )
    }
}

private val NeuMaterialTypography = Typography(
    headlineSmall = DefaultNeuTypography.headline,
    titleLarge = DefaultNeuTypography.title,
    titleMedium = DefaultNeuTypography.button,
    bodyLarge = DefaultNeuTypography.bodyMedium,
    bodyMedium = DefaultNeuTypography.bodySmall,
    bodySmall = DefaultNeuTypography.bodySmall,
    labelLarge = DefaultNeuTypography.button,
    labelMedium = DefaultNeuTypography.labelCaps,
    labelSmall = DefaultNeuTypography.labelCaps,
)
