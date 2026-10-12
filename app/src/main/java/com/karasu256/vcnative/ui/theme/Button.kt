package com.karasu256.vcnative.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.ui.components.NeuButton
import com.karasu256.vcnative.ui.components.NeuButtonVariant

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    content: @Composable RowScope.() -> Unit,
) {
    NeuButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        loading = loading,
        variant = NeuButtonVariant.Primary,
        content = content,
    )
}

@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    NeuButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = NeuButtonVariant.Neutral,
        content = content,
    )
}

@Composable
fun DropdownButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    SecondaryButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
    }
}

@Composable
fun ToolbarButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NeuButton(
        onClick = onClick,
        modifier = modifier.size(ToolbarButtonSize),
        variant = NeuButtonVariant.Neutral,
        contentPadding = PaddingValues(0.dp),
        content = content,
    )
}

@Composable
fun TabButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NeuButton(
        onClick = onClick,
        modifier = modifier.size(ToolbarButtonSize),
        variant = if (selected) NeuButtonVariant.Primary else NeuButtonVariant.Neutral,
        contentPadding = PaddingValues(0.dp),
        content = content,
    )
}

@Composable
fun PillButton(
    onClick: () -> Unit,
    icons: List<ImageVector>,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val style = NeuTheme.linkButton
    NeuButton(
        onClick = onClick,
        modifier = modifier,
        variant = NeuButtonVariant.Primary,
        contentPadding = style.contentPadding,
        shape = style.shape,
    ) {
        icons.forEachIndexed { index, icon ->
            Icon(
                imageVector = icon,
                contentDescription = if (index == icons.lastIndex) contentDescription else null,
                modifier = Modifier.size(style.iconSize),
            )
        }
    }
}

private val ToolbarButtonSize = 48.dp
