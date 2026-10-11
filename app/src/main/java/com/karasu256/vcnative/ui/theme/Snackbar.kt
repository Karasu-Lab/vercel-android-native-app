package com.karasu256.vcnative.ui.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class NeuSnackbarStyle(
    val shape: Shape = RoundedCornerShape(50),
    val depth: NeuDepth = NeuElevation().high,
    val contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
    val enter: EnterTransition = slideInVertically(tween(220)) { -it } + fadeIn(tween(220)),
    val exit: ExitTransition = slideOutVertically(tween(220)) { -it } + fadeOut(tween(220)),
    val dismissThreshold: Float = 0.5f,
    val dismissVelocity: Float = 1000f,
)

internal val LocalNeuSnackbarStyle = staticCompositionLocalOf { NeuSnackbarStyle() }
