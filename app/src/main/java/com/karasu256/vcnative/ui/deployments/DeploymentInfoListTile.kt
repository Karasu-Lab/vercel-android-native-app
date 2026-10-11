package com.karasu256.vcnative.ui.deployments

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.data.model.DeploymentState
import com.karasu256.vcnative.ui.shell.UserAvatar
import com.karasu256.vcnative.ui.theme.NeuTheme

@Composable
fun DeploymentInfoListTile(
    deployment: DeploymentInfo,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(
                text = deployment.projectName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        modifier = modifier,
        supportingContent = deployment.description?.let { description ->
            {
                Text(
                    text = description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        trailingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(NeuTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                deployment.elapsedSeconds?.let { seconds ->
                    Text(
                        text = formatElapsed(seconds),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                DeploymentStateIndicator(state = deployment.state)
                deployment.creators.forEach { creator ->
                    UserAvatar(avatar = creator.avatar, size = 24.dp)
                }
            }
        },
    )
}

@Composable
private fun DeploymentStateIndicator(
    state: DeploymentState,
    modifier: Modifier = Modifier,
) {
    val statusColors = NeuTheme.statusColors
    val description = stringResource(state.labelRes)
    val indicatorModifier = modifier
        .size(12.dp)
        .semantics { contentDescription = description }

    when (state) {
        DeploymentState.Initializing -> CircularProgressIndicator(
            modifier = indicatorModifier,
            color = statusColors.neutral,
            trackColor = Color.Transparent,
            strokeWidth = 2.dp,
        )
        DeploymentState.Building -> CircularProgressIndicator(
            modifier = indicatorModifier,
            color = statusColors.progress,
            trackColor = Color.Transparent,
            strokeWidth = 2.dp,
        )
        DeploymentState.Error -> Box(indicatorModifier.background(statusColors.error, CircleShape))
        DeploymentState.Ready -> Box(indicatorModifier.background(statusColors.success, CircleShape))
        DeploymentState.Queued,
        DeploymentState.Canceled,
        DeploymentState.Unknown -> Box(indicatorModifier.background(statusColors.neutral, CircleShape))
    }
}

private fun formatElapsed(seconds: Long): String {
    val minutes = seconds / 60
    val remainder = seconds % 60
    return if (minutes > 0) "${minutes}m ${remainder}s" else "${remainder}s"
}

@get:StringRes
private val DeploymentState.labelRes: Int
    get() = when (this) {
        DeploymentState.Queued -> R.string.deployment_state_queued
        DeploymentState.Initializing -> R.string.deployment_state_initializing
        DeploymentState.Building -> R.string.deployment_state_building
        DeploymentState.Error -> R.string.deployment_state_error
        DeploymentState.Ready -> R.string.deployment_state_ready
        DeploymentState.Canceled -> R.string.deployment_state_canceled
        DeploymentState.Unknown -> R.string.deployment_state_unknown
    }
