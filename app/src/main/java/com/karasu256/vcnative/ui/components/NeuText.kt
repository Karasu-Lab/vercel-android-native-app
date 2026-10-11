package com.karasu256.vcnative.ui.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.karasu256.vcnative.ui.theme.LocalNeuContentColor
import com.karasu256.vcnative.ui.theme.LocalNeuTextStyle

@Composable
fun NeuText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalNeuTextStyle.current,
    textAlign: TextAlign = TextAlign.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val textColor = color.takeOrElse { style.color.takeOrElse { LocalNeuContentColor.current } }
    BasicText(
        text = text,
        modifier = modifier,
        style = style.merge(TextStyle(color = textColor, textAlign = textAlign)),
        overflow = overflow,
        softWrap = maxLines != 1,
        maxLines = maxLines,
    )
}
