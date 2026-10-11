package com.karasu256.vcnative.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.ui.theme.NeuTheme

@Composable
fun NeuTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
) {
    val colors = NeuTheme.colors
    val typography = NeuTheme.typography
    val spacing = NeuTheme.spacing
    val shape = NeuTheme.shapes.md
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val isError = errorText != null

    val borderColor = when {
        !enabled -> colors.disabled
        isError -> colors.danger
        focused -> colors.primary
        else -> colors.outline
    }
    val borderWidth = if (enabled && (isError || focused)) 2.dp else 1.dp

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.semantics { if (errorText != null) error(errorText) },
        enabled = enabled,
        textStyle = typography.bodyMedium.copy(
            color = if (enabled) colors.onSurface else colors.disabled
        ),
        cursorBrush = SolidColor(colors.primary),
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = source,
        decorationBox = { innerTextField ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                if (label != null) {
                    NeuText(
                        text = label.uppercase(),
                        style = typography.labelCaps,
                        color = colors.onSurfaceMuted,
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .neuSurface(
                            NeuSurfaceStyle.Inset,
                            shape,
                            colors.field,
                            NeuTheme.elevation.low,
                            colors
                        )
                        .border(borderWidth, borderColor, shape)
                        .padding(horizontal = spacing.lg, vertical = spacing.md),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty() && placeholder != null) {
                        NeuText(
                            text = placeholder,
                            style = typography.bodyMedium,
                            color = colors.onSurfaceMuted,
                            maxLines = 1,
                        )
                    }
                    innerTextField()
                }
                val helper = errorText ?: supportingText
                if (helper != null) {
                    NeuText(
                        text = helper,
                        style = typography.bodySmall,
                        color = if (isError) colors.danger else colors.onSurfaceMuted,
                    )
                }
            }
        }
    )
}
