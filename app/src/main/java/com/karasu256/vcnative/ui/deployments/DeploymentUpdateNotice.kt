package com.karasu256.vcnative.ui.deployments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.components.NeuSurface
import com.karasu256.vcnative.ui.components.NeuText
import com.karasu256.vcnative.ui.theme.NeuTheme

@Composable
fun DeploymentUpdateNotice(
    visible: Boolean,
    branch: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
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
            Row(
                modifier = Modifier
                    .clip(shape)
                    .clickable(role = Role.Button, onClick = onClick)
                    .padding(horizontal = spacing.lg, vertical = spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = stringResource(R.string.new_deployment),
                    modifier = Modifier.size(20.dp),
                    tint = NeuTheme.colors.primary,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.CallSplit,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = NeuTheme.colors.onSurface,
                )
                if (branch != null) {
                    NeuText(
                        text = branch,
                        style = NeuTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
