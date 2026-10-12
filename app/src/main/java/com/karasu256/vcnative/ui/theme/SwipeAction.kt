package com.karasu256.vcnative.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SwipeActionBackground(
    icon: ImageVector,
    label: String,
    alignment: Alignment.Horizontal,
    destructive: Boolean,
    modifier: Modifier = Modifier,
) {
    val statusColors = NeuTheme.statusColors
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(if (destructive) statusColors.error else statusColors.progress)
            .padding(horizontal = NeuTheme.spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(NeuTheme.spacing.sm, alignment),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White)
        Text(text = label, color = Color.White)
    }
}
