package com.karasu256.vcnative.ui.logs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.R
import com.karasu256.vcnative.data.model.RuntimeLogSource
import com.karasu256.vcnative.ui.components.RefreshBox
import com.karasu256.vcnative.ui.shell.ShellGraph
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.Skeleton
import com.ramcosta.composedestinations.annotation.Destination
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Destination<ShellGraph>
@Composable
fun DeploymentLogsScreen(
    projectId: String,
    deploymentId: String,
    title: String,
    viewModel: DeploymentLogsViewModel = hiltViewModel(),
) {
    DeploymentLogsContent(viewModel = viewModel)
}

@Composable
fun DeploymentLogsContent(
    viewModel: DeploymentLogsViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selected by remember { mutableStateOf<LogItem?>(null) }

    selected?.let { item ->
        LogEntrySheet(item = item, onDismissRequest = { selected = null })
    }

    val logs = uiState.logs

    RefreshBox(
        refreshable = viewModel,
        isRefreshing = uiState.isRefreshing,
        modifier = modifier.fillMaxSize(),
    ) {
        when {
            logs == null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                repeat(6) {
                    Skeleton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = NeuTheme.spacing.lg, vertical = NeuTheme.spacing.sm)
                            .height(64.dp)
                    )
                }
            }

            logs.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(NeuTheme.spacing.lg),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(
                        if (uiState.hasError) R.string.error_load_logs else R.string.no_logs
                    ),
                )
            }

            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items = logs, key = { it.id }) { item ->
                    LogListTile(item = item, onClick = { selected = item })
                }
            }
        }
    }
}

@Composable
private fun LogListTile(
    item: LogItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        overlineContent = { Text(text = formatLogTime(item.timestamp, withDate = false)) },
        headlineContent = {
            Text(
                text = item.routeTitle(),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(
                text = item.message.ifBlank { stringResource(R.string.log_no_output) },
                fontFamily = FontFamily.Monospace,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        trailingContent = item.statusCode?.let { code ->
            {
                Text(
                    text = code.toString(),
                    color = statusCodeColor(code),
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        modifier = modifier.clickable(onClick = onClick),
    )
}

internal fun LogItem.routeTitle(): String {
    val route = listOfNotNull(method, path).joinToString(" ")
    return route.ifBlank { source.label }
}

internal val RuntimeLogSource.label: String
    get() = when (this) {
        RuntimeLogSource.EdgeFunction -> "Edge function"
        RuntimeLogSource.EdgeMiddleware -> "Edge middleware"
        RuntimeLogSource.Request -> "Request"
        RuntimeLogSource.Serverless -> "Serverless"
        RuntimeLogSource.Delimiter, RuntimeLogSource.Unknown -> "Log"
    }

@Composable
internal fun statusCodeColor(code: Int): Color {
    val colors = NeuTheme.statusColors
    return when (code) {
        in 100..399 -> colors.success
        in 400..499 -> colors.progress
        in 500..599 -> colors.error
        else -> colors.neutral
    }
}

internal fun formatLogTime(timestamp: Long, withDate: Boolean): String {
    val pattern = if (withDate) "yyyy-MM-dd HH:mm:ss" else "HH:mm:ss"
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timestamp))
}
