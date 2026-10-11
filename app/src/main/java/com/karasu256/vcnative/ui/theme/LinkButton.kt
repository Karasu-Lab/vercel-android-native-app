package com.karasu256.vcnative.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class NeuLinkButtonStyle(
    val shape: Shape = RoundedCornerShape(50),
    val contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    val iconSize: Dp = 20.dp,
)

internal val LocalNeuLinkButtonStyle = staticCompositionLocalOf { NeuLinkButtonStyle() }
