package com.karasu256.vcnative.ui.logs

import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.Panel
import com.karasu256.vcnative.ui.theme.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogEntrySheet(
    item: LogItem,
    onDismissRequest: () -> Unit,
) {
    val spacing = NeuTheme.spacing
    val context = LocalContext.current
    var copied by remember(item.id) { mutableStateOf(false) }
    val output = item.message.ifBlank { stringResource(R.string.log_no_output) }

    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg)
                .padding(bottom = spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.routeTitle(),
                    modifier = Modifier.weight(1f),
                    style = NeuTheme.typography.title,
                )
                item.statusCode?.let { code ->
                    Text(
                        text = code.toString(),
                        color = statusCodeColor(code),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            LogMeta(label = stringResource(R.string.label_time), value = formatLogTime(item.timestamp, withDate = true))
            LogMeta(label = stringResource(R.string.label_level), value = item.level.name)
            LogMeta(label = stringResource(R.string.label_source), value = item.source.label)
            item.domain?.let { LogMeta(label = stringResource(R.string.label_domain), value = it) }

            Text(
                text = stringResource(R.string.log_output),
                style = NeuTheme.typography.labelCaps,
                color = NeuTheme.colors.onSurfaceMuted,
            )
            Panel(modifier = Modifier.fillMaxWidth()) {
                SelectionContainer {
                    Text(
                        text = output,
                        modifier = Modifier
                            .heightIn(max = 320.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(spacing.md),
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
            PrimaryButton(
                onClick = {
                    context.getSystemService(ClipboardManager::class.java)
                        ?.setPrimaryClip(ClipData.newPlainText(item.routeTitle(), item.message))
                    copied = true
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = item.message.isNotEmpty(),
            ) {
                Text(text = stringResource(if (copied) R.string.copied else R.string.copy_output))
            }
        }
    }
}

@Composable
private fun LogMeta(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(NeuTheme.spacing.md)) {
        Text(
            text = label,
            color = NeuTheme.colors.onSurfaceMuted,
        )
        Text(text = value)
    }
}
