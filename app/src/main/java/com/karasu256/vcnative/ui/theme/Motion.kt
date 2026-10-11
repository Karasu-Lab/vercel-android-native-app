package com.karasu256.vcnative.ui.theme

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class NeuMotion(
    val contentChange: ContentTransform =
        (fadeIn(tween(220, delayMillis = 90)) + scaleIn(tween(220, delayMillis = 90), initialScale = 0.8f))
            .togetherWith(fadeOut(tween(90)) + scaleOut(tween(90), targetScale = 0.8f)),
    val barsSettle: AnimationSpec<Float> = tween(220),
    val noticeEnter: EnterTransition = slideInVertically(tween(220)) { -it } + fadeIn(tween(220)),
    val noticeExit: ExitTransition = slideOutVertically(tween(220)) { -it } + fadeOut(tween(220)),
)

internal val LocalNeuMotion = staticCompositionLocalOf { NeuMotion() }
