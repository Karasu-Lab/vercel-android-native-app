package com.karasu256.vcnative.ui.deployments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.theme.NeuTheme
import kotlinx.coroutines.launch

@Composable
fun SwipeableDeploymentListTile(
    deployment: DeploymentInfo,
    onClick: () -> Unit,
    onAction: (DeploymentAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberSwipeToDismissBoxState()
    val scope = rememberCoroutineScope()

    SwipeToDismissBox(
        state = state,
        modifier = modifier,
        onDismiss = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> onAction(DeploymentAction.Rollback(deployment))
                SwipeToDismissBoxValue.EndToStart -> onAction(deployment.endToStartAction())
                SwipeToDismissBoxValue.Settled -> Unit
            }
            scope.launch { state.reset() }
        },
        backgroundContent = {
            when (state.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> SwipeBackground(
                    action = DeploymentAction.Rollback(deployment),
                    alignment = Alignment.Start,
                )
                SwipeToDismissBoxValue.EndToStart -> SwipeBackground(
                    action = deployment.endToStartAction(),
                    alignment = Alignment.End,
                )
                SwipeToDismissBoxValue.Settled -> Unit
            }
        },
    ) {
        DeploymentInfoListTile(deployment = deployment, onClick = onClick)
    }
}

@Composable
private fun SwipeBackground(
    action: DeploymentAction,
    alignment: Alignment.Horizontal,
) {
    val statusColors = NeuTheme.statusColors
    val color = when (action) {
        is DeploymentAction.Rollback -> statusColors.progress
        is DeploymentAction.Cancel,
        is DeploymentAction.Delete -> statusColors.error
    }
    val label = stringResource(action.labelRes)
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = NeuTheme.spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(NeuTheme.spacing.sm, alignment),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = action.icon, contentDescription = null, tint = Color.White)
        Text(text = label, color = Color.White)
    }
}

private val DeploymentAction.icon: ImageVector
    get() = when (this) {
        is DeploymentAction.Rollback -> Icons.Default.History
        is DeploymentAction.Cancel -> Icons.Default.Block
        is DeploymentAction.Delete -> Icons.Default.Delete
    }

internal val DeploymentAction.labelRes: Int
    get() = when (this) {
        is DeploymentAction.Rollback -> R.string.swipe_rollback
        is DeploymentAction.Cancel -> R.string.swipe_cancel
        is DeploymentAction.Delete -> R.string.swipe_delete
    }
