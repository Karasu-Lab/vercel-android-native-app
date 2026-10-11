package com.karasu256.vcnative.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

@Immutable
data class NeuBarStyle(
    val shape: Shape = RectangleShape,
    val depth: NeuDepth = NeuElevation().medium,
)

internal val LocalNeuBarStyle = staticCompositionLocalOf { NeuBarStyle() }
