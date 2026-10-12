package com.karasu256.vcnative.ui.deployments

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.components.ListItemSwipeAction
import com.karasu256.vcnative.ui.components.SwipeDirection
import com.karasu256.vcnative.ui.components.SwipeableListItem
import com.karasu256.vcnative.ui.components.alignment
import com.karasu256.vcnative.ui.theme.SwipeActionBackground

private class DeploymentSwipeAction(
    val action: DeploymentAction,
    direction: SwipeDirection,
    private val onAction: (DeploymentAction) -> Unit,
) : ListItemSwipeAction(direction) {
    override fun execute() = onAction(action)
}

@Composable
fun SwipeableDeploymentListTile(
    deployment: DeploymentInfo,
    onClick: () -> Unit,
    onAction: (DeploymentAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val actions = remember(deployment, onAction) {
        listOf(
            DeploymentSwipeAction(DeploymentAction.Rollback(deployment), SwipeDirection.LeftToRight, onAction),
            DeploymentSwipeAction(deployment.endToStartAction(), SwipeDirection.RightToLeft, onAction),
        )
    }

    SwipeableListItem(
        actions = actions,
        modifier = modifier,
        background = { swipeAction ->
            SwipeActionBackground(
                icon = swipeAction.action.icon,
                label = stringResource(swipeAction.action.labelRes),
                alignment = swipeAction.direction.alignment,
                destructive = swipeAction.action !is DeploymentAction.Rollback,
            )
        },
    ) {
        DeploymentInfoListTile(deployment = deployment, onClick = onClick)
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
