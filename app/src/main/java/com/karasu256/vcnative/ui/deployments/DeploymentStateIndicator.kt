package com.karasu256.vcnative.ui.deployments

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.data.model.DeploymentState
import com.karasu256.vcnative.ui.theme.NeuTheme

@Composable
fun DeploymentStateIndicator(
    state: DeploymentState,
    modifier: Modifier = Modifier,
    size: Dp = 12.dp,
) {
    val statusColors = NeuTheme.statusColors
    val description = stringResource(state.labelRes)
    val indicatorModifier = modifier
        .size(size)
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

@get:StringRes
val DeploymentState.labelRes: Int
    get() = when (this) {
        DeploymentState.Queued -> R.string.deployment_state_queued
        DeploymentState.Initializing -> R.string.deployment_state_initializing
        DeploymentState.Building -> R.string.deployment_state_building
        DeploymentState.Error -> R.string.deployment_state_error
        DeploymentState.Ready -> R.string.deployment_state_ready
        DeploymentState.Canceled -> R.string.deployment_state_canceled
        DeploymentState.Unknown -> R.string.deployment_state_unknown
    }
