package com.karasu256.vcnative.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.ui.components.NeuCard
import com.karasu256.vcnative.ui.components.NeuSurface
import com.karasu256.vcnative.ui.components.NeuSurfaceStyle

@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    NeuSurface(
        modifier = modifier,
        shape = RectangleShape,
        style = NeuSurfaceStyle.Flat,
        content = content,
    )
}

@Composable
fun DrawerSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    NeuSurface(
        modifier = modifier,
        shape = RectangleShape,
        content = content,
    )
}

@Composable
fun Panel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    NeuSurface(modifier = modifier, content = content)
}

@Composable
fun ContentCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    NeuCard(modifier = modifier, content = content)
}

@Composable
fun ImagePlaceholder(
    modifier: Modifier = Modifier,
) {
    NeuSurface(
        modifier = modifier,
        shape = NeuTheme.shapes.lg,
        style = NeuSurfaceStyle.Inset,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = NeuTheme.colors.onSurfaceMuted,
            )
        }
    }
}
