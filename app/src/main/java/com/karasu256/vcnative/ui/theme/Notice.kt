package com.karasu256.vcnative.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import com.karasu256.vcnative.ui.components.NeuSurface

@Composable
fun Notice(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val motion = NeuTheme.motion
    val shape = NeuTheme.shapes.pill
    val spacing = NeuTheme.spacing

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = motion.noticeEnter,
        exit = motion.noticeExit,
    ) {
        NeuSurface(shape = shape) {
            CompositionLocalProvider(LocalTextStyle provides NeuTheme.typography.bodySmall) {
                Row(
                    modifier = Modifier
                        .clip(shape)
                        .clickable(role = Role.Button, onClick = onClick)
                        .padding(horizontal = spacing.lg, vertical = spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    content = content,
                )
            }
        }
    }
}
