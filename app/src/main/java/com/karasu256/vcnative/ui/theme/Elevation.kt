package com.karasu256.vcnative.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@Immutable
data class NeuDepth(
    val distance: Dp,
    val blur: Dp,
)

@Immutable
data class NeuElevation(
    val low: NeuDepth = NeuDepth(distance = 3.dp, blur = 6.dp),
    val medium: NeuDepth = NeuDepth(distance = 6.dp, blur = 12.dp),
    val high: NeuDepth = NeuDepth(distance = 10.dp, blur = 20.dp),
)

internal val LocalNeuElevation = staticCompositionLocalOf { NeuElevation() }

fun Modifier.neuRaised(
    shape: Shape,
    depth: NeuDepth,
    shadowLight: Color,
    shadowDark: Color,
): Modifier = this
    .dropShadow(
        shape = shape,
        shadow = Shadow(
            radius = depth.blur,
            color = shadowLight,
            offset = DpOffset(-depth.distance, -depth.distance),
        ),
    )
    .dropShadow(
        shape = shape,
        shadow = Shadow(
            radius = depth.blur,
            color = shadowDark,
            offset = DpOffset(depth.distance, depth.distance),
        ),
    )

fun Modifier.neuInset(
    shape: Shape,
    depth: NeuDepth,
    shadowLight: Color,
    shadowDark: Color,
): Modifier = this
    .innerShadow(
        shape = shape,
        shadow = Shadow(
            radius = depth.blur,
            color = shadowDark,
            offset = DpOffset(depth.distance, depth.distance),
        ),
    )
    .innerShadow(
        shape = shape,
        shadow = Shadow(
            radius = depth.blur,
            color = shadowLight,
            offset = DpOffset(-depth.distance, -depth.distance),
        ),
    )
