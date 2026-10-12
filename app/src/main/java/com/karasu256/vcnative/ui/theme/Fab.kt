package com.karasu256.vcnative.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.ui.components.NeuButton

@Immutable
data class NeuFabStyle(
    val size: Dp = 56.dp,
    val shape: Shape = RoundedCornerShape(16.dp),
    val depth: NeuDepth = NeuElevation().high,
    val contentPadding: PaddingValues = PaddingValues(0.dp),
)

internal val LocalNeuFabStyle = staticCompositionLocalOf { NeuFabStyle() }

@Composable
fun ActionFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val style = NeuTheme.fab
    NeuButton(
        onClick = onClick,
        modifier = modifier.size(style.size),
        contentPadding = style.contentPadding,
        shape = style.shape,
        depth = style.depth,
        content = content,
    )
}
