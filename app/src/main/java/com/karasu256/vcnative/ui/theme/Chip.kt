package com.karasu256.vcnative.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class NeuChipStyle(
    val shape: Shape = RoundedCornerShape(50),
    val contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
    val depth: NeuDepth = NeuElevation().low,
)

internal val LocalNeuChipStyle = staticCompositionLocalOf { NeuChipStyle() }
