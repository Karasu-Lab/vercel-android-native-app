package com.karasu256.vcnative.ui.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    val spacing = NeuTheme.spacing

    Text(
        text = title,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = spacing.lg, end = spacing.lg, top = spacing.lg, bottom = spacing.sm),
        style = NeuTheme.typography.labelCaps,
        color = NeuTheme.colors.onSurfaceMuted,
    )
}
