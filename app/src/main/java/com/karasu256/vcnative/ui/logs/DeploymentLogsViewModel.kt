package com.karasu256.vcnative.ui.logs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.model.RequestLogRowDto
import com.karasu256.vcnative.data.model.RuntimeLogDto
import com.karasu256.vcnative.data.model.RuntimeLogLevel
import com.karasu256.vcnative.data.model.RuntimeLogSource
import com.karasu256.vcnative.data.remote.VercelApi
import com.karasu256.vcnative.data.repository.ApiKeyRepository
import com.karasu256.vcnative.ui.components.Refreshable
import com.ramcosta.composedestinations.generated.destinations.DeploymentLogsScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

private const val MaxLogs = 1000
private const val MaxHistoryRows = 100
private const val ReconnectDelayMillis = 3000L
private const val HistoryRangeMillis = 24 * 60 * 60 * 1000L
private val IsoPatterns = listOf("yyyy-MM-dd'T'HH:mm:ss.SSSX", "yyyy-MM-dd'T'HH:mm:ssX")

data class LogItem(
    val id: String,
    val timestamp: Long,
    val level: RuntimeLogLevel,
    val source: RuntimeLogSource,
    val domain: String?,
    val method: String?,
    val path: String?,
    val statusCode: Int?,
    val message: String,
    val isTruncated: Boolean,
)

data class DeploymentLogsUiState(
    val logs: List<LogItem>? = null,
    val isRefreshing: Boolean = false,
    val hasError: Boolean = false,
)

@HiltViewModel
class DeploymentLogsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val apiKeyRepository: ApiKeyRepository,
    private val api: VercelApi,
) : ViewModel(), Refreshable {

    private val args = DeploymentLogsScreenDestination.argsFrom(savedStateHandle)

    private val _uiState = MutableStateFlow(DeploymentLogsUiState())
    val uiState = _uiState.asStateFlow()

    private val seen = mutableSetOf<String>()
    private var sequence = 0L

    private var apiKey: String? = null

    init {
        viewModelScope.launch {
            val key = apiKeyRepository.read() ?: return@launch
            apiKey = key
            launch { follow(key) }
            loadHistory(key)
        }
    }

    override fun refresh() {
        val key = apiKey ?: return
        if (_uiState.value.isRefreshing) return
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch { loadHistory(key) }
    }

    private suspend fun loadHistory(apiKey: String) {
        val history = try {
            val ownerId = api.getProject(apiKey, args.projectId).accountId
                ?: throw IllegalStateException("accountId is missing")
            val endDate = System.currentTimeMillis()
            val startDate = endDate - HistoryRangeMillis
            val rows = mutableListOf<RequestLogRowDto>()
            var page = 0
            do {
                val response = api.getRequestLogs(
                    apiKey = apiKey,
                    projectId = args.projectId,
                    ownerId = ownerId,
                    deploymentId = args.deploymentId,
                    page = page++,
                    startDate = startDate,
                    endDate = endDate,
                )
                rows += response.rows
            } while (response.hasMoreRows && rows.size < MaxHistoryRows)
            rows.map { it.toLogItem() }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
        _uiState.update { state ->
            val current = state.logs.orEmpty()
            val known = current.map { it.id }.toSet()
            val merged = (current + history.orEmpty().filter { it.id !in known })
                .sortedByDescending { it.timestamp }
                .take(MaxLogs)
            state.copy(
                logs = merged,
                isRefreshing = false,
                hasError = history == null && merged.isEmpty(),
            )
        }
    }

    private suspend fun follow(apiKey: String) {
        while (true) {
            try {
                api.runtimeLogs(apiKey, args.projectId, args.deploymentId).collect(::append)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // reconnect below
            }
            delay(ReconnectDelayMillis)
        }
    }

    private fun RequestLogRowDto.toLogItem(): LogItem {
        val worst = logs.maxByOrNull { it.level.severity() }
        return LogItem(
            id = requestId ?: "${timestamp}:${sequence++}",
            timestamp = parseTimestamp(timestamp),
            level = worst?.level ?: RuntimeLogLevel.Info,
            source = when (events.firstOrNull()?.source) {
                "serverless", "lambda" -> RuntimeLogSource.Serverless
                "edge-function" -> RuntimeLogSource.EdgeFunction
                "edge-middleware", "middleware" -> RuntimeLogSource.EdgeMiddleware
                else -> RuntimeLogSource.Request
            },
            domain = domain?.takeIf { it.isNotBlank() },
            method = requestMethod?.takeIf { it.isNotBlank() },
            path = requestPath?.takeIf { it.isNotBlank() },
            statusCode = statusCode?.takeIf { it > 0 },
            message = logs.joinToString("\n") { it.message.trimEnd('\n') },
            isTruncated = logs.any { it.messageTruncated },
        )
    }

    private fun RuntimeLogLevel.severity(): Int = when (this) {
        RuntimeLogLevel.Fatal -> 4
        RuntimeLogLevel.Error -> 3
        RuntimeLogLevel.Warning -> 2
        RuntimeLogLevel.Info -> 1
        else -> 0
    }

    private fun parseTimestamp(element: JsonElement?): Long {
        val raw = (element as? JsonPrimitive)?.content ?: return System.currentTimeMillis()
        raw.toLongOrNull()?.let { return it }
        for (pattern in IsoPatterns) {
            val parsed = runCatching {
                SimpleDateFormat(pattern, Locale.US)
                    .apply { timeZone = TimeZone.getTimeZone("UTC") }
                    .parse(raw)
            }.getOrNull()
            if (parsed != null) return parsed.time
        }
        return System.currentTimeMillis()
    }

    private fun append(entry: RuntimeLogDto) {
        if (entry.source == RuntimeLogSource.Delimiter) return
        val rowId = entry.rowId
        if (rowId != null && !seen.add(rowId)) return
        val item = LogItem(
            id = rowId ?: "${entry.timestampInMs}:${sequence++}",
            timestamp = entry.timestampInMs,
            level = entry.level,
            source = entry.source,
            domain = entry.domain?.takeIf { it.isNotBlank() },
            method = entry.requestMethod?.takeIf { it.isNotBlank() },
            path = entry.requestPath?.takeIf { it.isNotBlank() },
            statusCode = entry.responseStatusCode?.takeIf { it > 0 },
            message = entry.message,
            isTruncated = entry.messageTruncated,
        )
        _uiState.update { state ->
            val current = state.logs.orEmpty()
            val index = current.indexOfFirst { it.timestamp <= item.timestamp }
                .takeIf { it >= 0 } ?: current.size
            val logs = current.toMutableList().apply { add(index, item) }
            state.copy(logs = logs.take(MaxLogs), hasError = false)
        }
    }
}
