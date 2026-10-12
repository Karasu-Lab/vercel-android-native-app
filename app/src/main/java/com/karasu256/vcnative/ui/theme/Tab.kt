package com.karasu256.vcnative.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.ui.components.NeuSurface
import com.karasu256.vcnative.ui.components.NeuSurfaceStyle

@Immutable
data class NeuTabStyle(
    val trackShape: Shape = RoundedCornerShape(50),
    val tabShape: Shape = RoundedCornerShape(50),
    val trackDepth: NeuDepth = NeuElevation().low,
    val tabDepth: NeuDepth = NeuElevation().low,
    val trackPadding: Dp = 4.dp,
    val minHeight: Dp = 48.dp,
    val tabPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    val slide: AnimationSpec<Dp> = tween(220),
    val colorChange: AnimationSpec<Color> = tween(220),
)

internal val LocalNeuTabStyle = staticCompositionLocalOf { NeuTabStyle() }

@Composable
fun TabBar(
    selectedIndex: Int,
    tabCount: Int,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val style = NeuTheme.tab
    val spacing = NeuTheme.spacing
    val tabHeight = style.minHeight - style.trackPadding * 2

    NeuSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.lg, vertical = spacing.sm),
        shape = style.trackShape,
        style = NeuSurfaceStyle.Inset,
        depth = style.trackDepth,
    ) {
        BoxWithConstraints(modifier = Modifier.padding(style.trackPadding)) {
            val tabWidth = maxWidth / tabCount.coerceAtLeast(1)
            val indicatorOffset by animateDpAsState(
                targetValue = tabWidth * selectedIndex,
                animationSpec = style.slide,
                label = "tab-indicator",
            )

            NeuSurface(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(tabWidth)
                    .height(tabHeight),
                shape = style.tabShape,
                style = NeuSurfaceStyle.Raised,
                depth = style.tabDepth,
            ) {}
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}

@Composable
fun RowScope.TabItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val style = NeuTheme.tab
    val colors = NeuTheme.colors
    val contentColor by animateColorAsState(
        targetValue = if (selected) colors.primary else colors.onSurfaceMuted,
        animationSpec = style.colorChange,
        label = "tab-content-color",
    )

    CompositionLocalProvider(LocalTextStyle provides NeuTheme.typography.button) {
        Box(
            modifier = modifier
                .weight(1f)
                .clip(style.tabShape)
                .selectable(selected = selected, role = Role.Tab, onClick = onClick)
                .heightIn(min = style.minHeight - style.trackPadding * 2)
                .padding(style.tabPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
