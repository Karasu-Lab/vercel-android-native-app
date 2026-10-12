package com.karasu256.vcnative.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
    loading: Boolean = false,
    variant: NeuButtonVariant = NeuButtonVariant.Primary,
    interactionSource: MutableInteractionSource? = null,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = NeuTheme.spacing.xl,
        vertical = NeuTheme.spacing.md,
    ),
    shape: Shape = NeuTheme.shapes.md,
    depth: NeuDepth = NeuTheme.elevation.medium,
    content: @Composable RowScope.() -> Unit
) {
    val colors = NeuTheme.colors
    val spacing = NeuTheme.spacing
    val motion = NeuTheme.motion
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val focused by source.collectIsFocusedAsState()
    val active = enabled && !loading

    val raise by animateFloatAsState(if (active && !pressed) 1f else 0f, label = "raise")
    val sink by animateFloatAsState(if (active && pressed) 1f else 0f, label = "sink")

    val contentColor = when {
        !active -> colors.disabled
        variant == NeuButtonVariant.Primary -> colors.primary
        else -> colors.onSurface
    }
    val tone = maxOf(raise, sink)
    val highlight = lerp(colors.surface, colors.shadowLight.opaque, 0.35f * tone)
    val lowlight = lerp(colors.surface, colors.shadowDark.opaque, 0.12f * tone)
    val face = Brush.linearGradient(
        if (sink > raise) listOf(lowlight, colors.surface, highlight) else listOf(highlight, colors.surface, lowlight)
    )

    Box(
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
                enabled = active,
                role = Role.Button,
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(
            LocalNeuContentColor provides contentColor,
            LocalContentColor provides contentColor,
            LocalNeuTextStyle provides NeuTheme.typography.button,
            LocalTextStyle provides NeuTheme.typography.button,
        ) {
            AnimatedContent(
                targetState = loading,
                transitionSpec = { motion.contentChange },
                contentAlignment = Alignment.Center,
            ) { isLoading ->
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = colors.primary,
                        trackColor = Color.Transparent,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                        content = content,
                    )
                }
            }
        }
    }
}

private operator fun NeuDepth.times(fraction: Float) = NeuDepth(
    distance = distance * fraction,
    blur = blur * fraction,
)

private val Color.opaque get() = copy(alpha = 1f)
