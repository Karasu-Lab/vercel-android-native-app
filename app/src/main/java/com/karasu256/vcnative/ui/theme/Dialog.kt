package com.karasu256.vcnative.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.karasu256.vcnative.ui.components.NeuButton
import com.karasu256.vcnative.ui.components.NeuButtonVariant

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    isRunning: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Text(
                text = message,
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
                    Text(text = confirmLabel)
                }
                NeuButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isRunning,
                    variant = NeuButtonVariant.Neutral,
                ) {
                    Text(text = dismissLabel)
                }
            }
        },
    )
}
