package com.karasu256.vcnative.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class NeuSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
)

@Immutable
data class NeuShapes(
    val sm: RoundedCornerShape = RoundedCornerShape(4.dp),
    val md: RoundedCornerShape = RoundedCornerShape(8.dp),
    val lg: RoundedCornerShape = RoundedCornerShape(24.dp),
)

internal val LocalNeuSpacing = staticCompositionLocalOf { NeuSpacing() }
internal val LocalNeuShapes = staticCompositionLocalOf { NeuShapes() }
