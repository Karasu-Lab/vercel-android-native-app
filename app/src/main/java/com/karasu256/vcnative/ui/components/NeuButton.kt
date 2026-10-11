package com.karasu256.vcnative.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.ui.theme.LocalNeuContentColor
import com.karasu256.vcnative.ui.theme.LocalNeuTextStyle
import com.karasu256.vcnative.ui.theme.NeuTheme

enum class NeuButtonVariant { Primary, Neutral }

@Composable
fun NeuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: NeuButtonVariant = NeuButtonVariant.Primary,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    val colors = NeuTheme.colors
    val shape = NeuTheme.shapes.md
    val spacing = NeuTheme.spacing
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val focused by source.collectIsFocusedAsState()

    val containerColor = when {
        !enabled -> colors.surface
        variant == NeuButtonVariant.Primary -> colors.primary
        else -> colors.surface
    }
    val contentColor = when {
        !enabled -> colors.disabled
        variant == NeuButtonVariant.Primary -> colors.onPrimary
        else -> colors.onSurface
    }
    val surfaceStyle = when {
        !enabled -> NeuSurfaceStyle.Flat
        pressed -> NeuSurfaceStyle.Inset
        else -> NeuSurfaceStyle.Raised
    }

    Row(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .neuFocusRing(focused, shape, colors.primary)
            .neuSurface(surfaceStyle, shape, containerColor, NeuTheme.elevation.low, colors)
            .then(if (enabled) Modifier else Modifier.border(1.dp, colors.disabled, shape))
            .clickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = spacing.xl, vertical = spacing.md),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(
            LocalNeuContentColor provides contentColor,
            LocalNeuTextStyle provides NeuTheme.typography.button,
        ) {
            content()
        }
    }
}
