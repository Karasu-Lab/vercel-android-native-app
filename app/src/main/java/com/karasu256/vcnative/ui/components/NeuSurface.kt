package com.karasu256.vcnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.ui.theme.LocalNeuContentColor
import com.karasu256.vcnative.ui.theme.NeuColors
import com.karasu256.vcnative.ui.theme.NeuDepth
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.neuInset
import com.karasu256.vcnative.ui.theme.neuRaised

enum class NeuSurfaceStyle { Raised, Inset, Flat }

@Composable
fun NeuSurface(
    modifier: Modifier = Modifier,
    shape: Shape = NeuTheme.shapes.md,
    color: Color = NeuTheme.colors.surface,
    contentColor: Color = NeuTheme.colors.onSurface,
    style: NeuSurfaceStyle = NeuSurfaceStyle.Raised,
    depth: NeuDepth = NeuTheme.elevation.medium,
    content: @Composable () -> Unit
) {
    val colors = NeuTheme.colors
    Box(
        modifier = modifier.neuSurface(style, shape, color, depth, colors),
        propagateMinConstraints = true,
    ) {
        CompositionLocalProvider(
            LocalNeuContentColor provides contentColor,
            LocalContentColor provides contentColor,
        ) {
            content()
        }
    }
}

@Composable
fun NeuCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    NeuSurface(
        modifier = modifier,
        shape = NeuTheme.shapes.lg,
        depth = NeuTheme.elevation.medium,
    ) {
        Column(
            modifier = Modifier.padding(NeuTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(NeuTheme.spacing.md),
            content = content
        )
    }
}

internal fun Modifier.neuSurface(
    style: NeuSurfaceStyle,
    shape: Shape,
    color: Color,
    depth: NeuDepth,
    colors: NeuColors,
): Modifier = when (style) {
    NeuSurfaceStyle.Raised -> this
        .neuRaised(shape, depth, colors.shadowLight, colors.shadowDark)
        .background(color, shape)

    NeuSurfaceStyle.Inset -> this
        .background(color, shape)
        .neuInset(shape, depth, colors.shadowLight, colors.shadowDark)

    NeuSurfaceStyle.Flat -> this.background(color, shape)
}

internal fun Modifier.neuFocusRing(
    focused: Boolean,
    shape: Shape,
    color: Color,
    width: Dp = 2.dp,
    gap: Dp = 2.dp,
): Modifier = drawWithContent {
    drawContent()
    if (focused) {
        val inset = (gap + width / 2).toPx()
        val outline = shape.createOutline(
            Size(size.width + inset * 2, size.height + inset * 2),
            layoutDirection,
            this
        )
        translate(-inset, -inset) {
            drawOutline(outline, color, style = Stroke(width.toPx()))
        }
    }
}
