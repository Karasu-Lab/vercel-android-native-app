package com.karasu256.vcnative.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class NeuFabStyle(
    val size: Dp = 56.dp,
    val shape: Shape = RoundedCornerShape(16.dp),
    val depth: NeuDepth = NeuElevation().high,
    val contentPadding: PaddingValues = PaddingValues(0.dp),
)

internal val LocalNeuFabStyle = staticCompositionLocalOf { NeuFabStyle() }
