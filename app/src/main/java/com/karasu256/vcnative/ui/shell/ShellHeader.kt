package com.karasu256.vcnative.ui.shell

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.components.NeuButton
import com.karasu256.vcnative.ui.components.NeuButtonVariant
import com.karasu256.vcnative.ui.components.NeuSurface
import com.karasu256.vcnative.ui.components.NeuText
import com.karasu256.vcnative.ui.theme.LocalNeuContentColor
import com.karasu256.vcnative.ui.theme.NeuTheme

@Composable
fun ShellHeader(
    title: String,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = NeuTheme.spacing

    NeuSurface(
        modifier = modifier.fillMaxWidth(),
        shape = RectangleShape,
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(
                    WindowInsets.systemBars
                        .union(WindowInsets.displayCutout)
                        .only(WindowInsetsSides.Horizontal)
                )
                .padding(horizontal = spacing.lg),
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
