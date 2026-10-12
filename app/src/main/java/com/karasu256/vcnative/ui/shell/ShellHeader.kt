package com.karasu256.vcnative.ui.shell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.theme.BarSurface
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.ToolbarButton
import kotlin.math.roundToInt

@Composable
fun ShellHeader(
    title: String,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    collapseFraction: Float = 0f,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    val spacing = NeuTheme.spacing

    BarSurface(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
            Box(
                modifier = Modifier.clipToBounds().layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val height = (placeable.height * (1f - collapseFraction)).roundToInt()
                        layout(placeable.width, height) {
                            placeable.place(0, height - placeable.height)
                        }
                    },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().windowInsetsPadding(
                            WindowInsets.systemBars.union(WindowInsets.displayCutout)
                                .only(WindowInsetsSides.Horizontal)
                        ).padding(start = spacing.lg, end = spacing.lg, bottom = spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(spacing.lg),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ToolbarButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(R.string.open_menu),
                        )
                    }
                    Text(
                        text = title,
                        modifier = Modifier.weight(1f),
                        style = NeuTheme.typography.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    trailing?.invoke(this)
                }
            }
        }
    }
}
