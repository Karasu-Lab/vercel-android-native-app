package com.karasu256.vcnative.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.karasu256.vcnative.ui.components.NeuSurface

@Immutable
data class NeuBarStyle(
    val shape: Shape = RectangleShape,
    val depth: NeuDepth = NeuElevation().medium,
)

internal val LocalNeuBarStyle = staticCompositionLocalOf { NeuBarStyle() }

@Composable
fun BarSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val style = NeuTheme.bar
    NeuSurface(
        modifier = modifier,
        shape = style.shape,
        depth = style.depth,
        content = content,
    )
}
