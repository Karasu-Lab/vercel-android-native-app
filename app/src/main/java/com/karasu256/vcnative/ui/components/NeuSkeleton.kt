package com.karasu256.vcnative.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import com.karasu256.vcnative.ui.theme.NeuTheme

@Composable
fun NeuSkeleton(
    modifier: Modifier = Modifier,
    shape: Shape = NeuTheme.shapes.md,
) {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = NeuTheme.motion.skeletonPulse,
    )

    Box(
        modifier = modifier
            .graphicsLayer { this.alpha = alpha }
            .background(NeuTheme.colors.field, shape)
    )
}
