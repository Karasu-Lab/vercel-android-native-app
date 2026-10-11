package com.karasu256.vcnative.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.ui.theme.LocalNeuContentColor
import com.karasu256.vcnative.ui.theme.LocalNeuTextStyle
import com.karasu256.vcnative.ui.theme.NeuDepth
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.neuInset
import com.karasu256.vcnative.ui.theme.neuRaised

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
    val depth = NeuTheme.elevation.medium
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val focused by source.collectIsFocusedAsState()

    val raise by animateFloatAsState(if (enabled && !pressed) 1f else 0f, label = "raise")
    val sink by animateFloatAsState(if (enabled && pressed) 1f else 0f, label = "sink")

    val contentColor = when {
        !enabled -> colors.disabled
        variant == NeuButtonVariant.Primary -> colors.primary
        else -> colors.onSurface
    }
    val tone = maxOf(raise, sink)
    val highlight = lerp(colors.surface, colors.shadowLight.opaque, 0.35f * tone)
    val lowlight = lerp(colors.surface, colors.shadowDark.opaque, 0.12f * tone)
    val face = Brush.linearGradient(
        if (sink > raise) listOf(lowlight, colors.surface, highlight) else listOf(highlight, colors.surface, lowlight)
    )

    Row(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .neuFocusRing(focused, shape, colors.primary)
            .then(
                if (raise > 0f) {
                    Modifier.neuRaised(shape, depth * raise, colors.shadowLight, colors.shadowDark)
                } else {
                    Modifier
                }
            )
            .background(face, shape)
            .then(
                if (sink > 0f) {
                    Modifier.neuInset(shape, depth * sink, colors.shadowLight, colors.shadowDark)
                } else {
                    Modifier
                }
            )
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

private operator fun NeuDepth.times(fraction: Float) = NeuDepth(
    distance = distance * fraction,
    blur = blur * fraction,
)

private val Color.opaque get() = copy(alpha = 1f)
