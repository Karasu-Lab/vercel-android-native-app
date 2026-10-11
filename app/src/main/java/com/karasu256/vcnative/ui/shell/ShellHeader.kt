package com.karasu256.vcnative.ui.shell

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.layout
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.components.NeuButton
import com.karasu256.vcnative.ui.components.NeuButtonVariant
import com.karasu256.vcnative.ui.components.NeuSurface
import com.karasu256.vcnative.ui.components.NeuText
import com.karasu256.vcnative.ui.theme.LocalNeuContentColor
import com.karasu256.vcnative.ui.theme.NeuTheme
import kotlin.math.roundToInt

@Composable
fun ShellHeader(
    title: String,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    collapseFraction: Float = 0f,
) {
    val spacing = NeuTheme.spacing

    NeuSurface(
        modifier = modifier.fillMaxWidth(),
        shape = NeuTheme.bar.shape,
        depth = NeuTheme.bar.depth,
    ) {
        Column(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
        Box(
            modifier = Modifier
                .clipToBounds()
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val height = (placeable.height * (1f - collapseFraction)).roundToInt()
                    layout(placeable.width, height) {
                        placeable.place(0, height - placeable.height)
                    }
                },
        ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(
                    WindowInsets.systemBars
                        .union(WindowInsets.displayCutout)
                        .only(WindowInsetsSides.Horizontal)
                )
                .padding(start = spacing.lg, end = spacing.lg, bottom = spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NeuButton(
                onClick = onMenuClick,
                modifier = Modifier.size(48.dp),
                variant = NeuButtonVariant.Neutral,
                contentPadding = PaddingValues(0.dp),
            ) {
                Image(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.open_menu),
                    colorFilter = ColorFilter.tint(LocalNeuContentColor.current),
                )
            }
            NeuText(
                text = title,
                style = NeuTheme.typography.title,
                maxLines = 1,
            )
        }
        }
        }
    }
}
