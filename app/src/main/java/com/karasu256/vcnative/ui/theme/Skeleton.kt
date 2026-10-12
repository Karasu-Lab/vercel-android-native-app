package com.karasu256.vcnative.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.karasu256.vcnative.ui.components.NeuSkeleton

@Composable
fun Skeleton(
    modifier: Modifier = Modifier,
    shape: Shape = NeuTheme.shapes.md,
) {
    NeuSkeleton(modifier = modifier, shape = shape)
}
