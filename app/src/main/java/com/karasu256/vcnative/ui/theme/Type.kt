package com.karasu256.vcnative.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.karasu256.vcnative.R

val SpaceMono = FontFamily(
    Font(R.font.space_mono_regular, FontWeight.Normal),
    Font(R.font.space_mono_bold, FontWeight.Bold),
)

@OptIn(ExperimentalTextApi::class)
val JetBrainsMono = FontFamily(
    Font(
        R.font.jetbrains_mono,
        FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400)),
    ),
    Font(
        R.font.jetbrains_mono,
        FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500)),
    ),
    Font(
        R.font.jetbrains_mono,
        FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700)),
    ),
)

@Immutable
data class NeuTypography(
    val display: TextStyle,
    val headline: TextStyle,
    val title: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
    val button: TextStyle,
    val labelCaps: TextStyle,
)

internal val DefaultNeuTypography = NeuTypography(
    display = TextStyle(
        fontFamily = SpaceMono,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.02).em,
    ),
    headline = TextStyle(
        fontFamily = SpaceMono,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    title = TextStyle(
        fontFamily = SpaceMono,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = SpaceMono,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = SpaceMono,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    button = TextStyle(
        fontFamily = SpaceMono,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    labelCaps = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.08.em,
    ),
)

internal val LocalNeuTypography = staticCompositionLocalOf { DefaultNeuTypography }

val LocalNeuTextStyle = compositionLocalOf { DefaultNeuTypography.bodyMedium }
