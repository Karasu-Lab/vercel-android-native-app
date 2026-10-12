package com.karasu256.vcnative.ui.deployments

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.theme.ConfirmDialog

@Composable
fun DeploymentActionDialog(
    action: DeploymentAction,
    isRunning: Boolean,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val (titleRes, messageRes) = when (action) {
        is DeploymentAction.Rollback -> R.string.confirm_rollback_title to R.string.confirm_rollback_message
        is DeploymentAction.Cancel -> R.string.confirm_cancel_title to R.string.confirm_cancel_message
        is DeploymentAction.Delete -> R.string.confirm_delete_title to R.string.confirm_delete_message
    }
    ConfirmDialog(
        title = stringResource(titleRes),
        message = stringResource(messageRes, action.deployment.projectName),
        confirmLabel = stringResource(R.string.confirm),
        dismissLabel = stringResource(R.string.cancel),
        onConfirm = onConfirm,
        onDismissRequest = onDismissRequest,
        isRunning = isRunning,
    )
}
