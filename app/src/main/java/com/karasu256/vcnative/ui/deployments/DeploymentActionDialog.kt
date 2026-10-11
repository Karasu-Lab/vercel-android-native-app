package com.karasu256.vcnative.ui.deployments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.components.NeuButton
import com.karasu256.vcnative.ui.components.NeuButtonVariant
import com.karasu256.vcnative.ui.components.NeuText
import com.karasu256.vcnative.ui.theme.NeuTheme

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
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(titleRes),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Text(
                text = stringResource(messageRes, action.deployment.projectName),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
            )
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(NeuTheme.spacing.md),
            ) {
                NeuButton(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth(),
                    loading = isRunning,
                ) {
                    NeuText(text = stringResource(R.string.confirm))
                }
                NeuButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isRunning,
                    variant = NeuButtonVariant.Neutral,
                ) {
                    NeuText(text = stringResource(R.string.cancel))
                }
            }
        },
    )
}
