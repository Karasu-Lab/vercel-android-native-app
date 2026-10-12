package com.karasu256.vcnative.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.ui.components.NeuSurface
import com.karasu256.vcnative.ui.components.NeuSurfaceStyle

@Immutable
data class NeuChipStyle(
    val shape: Shape = RoundedCornerShape(50),
    val contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
    val depth: NeuDepth = NeuElevation().low,
)

internal val LocalNeuChipStyle = staticCompositionLocalOf { NeuChipStyle() }

@Composable
fun ToggleChip(
    label: String,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val style = NeuTheme.chip
    val colors = NeuTheme.colors
    NeuSurface(
        modifier = modifier,
        shape = style.shape,
        style = if (selected) NeuSurfaceStyle.Inset else NeuSurfaceStyle.Raised,
        depth = style.depth,
        contentColor = if (selected) colors.primary else colors.onSurface,
    ) {
        CompositionLocalProvider(LocalTextStyle provides NeuTheme.typography.bodySmall) {
            Text(
                text = label,
                modifier = Modifier
                    .clip(style.shape)
                    .toggleable(value = selected, role = Role.Checkbox) { onToggle() }
                    .padding(style.contentPadding),
                maxLines = 1,
            )
        }
    }
}

@Composable
fun ChipRow(
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(NeuTheme.chip.depth.blur),
        horizontalArrangement = Arrangement.spacedBy(NeuTheme.spacing.sm),
        content = content,
    )
}
