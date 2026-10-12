package com.karasu256.vcnative.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
enum class RuntimeLogLevel {
    @SerialName("trace") Trace,
    @SerialName("debug") Debug,
    @SerialName("info") Info,
    @SerialName("warning") Warning,
    @SerialName("error") Error,
    @SerialName("fatal") Fatal,
    Unknown,
}

@Serializable
enum class RuntimeLogSource {
    @SerialName("delimiter") Delimiter,
    @SerialName("edge-function") EdgeFunction,
    @SerialName("edge-middleware") EdgeMiddleware,
    @SerialName("request") Request,
    @SerialName("serverless") Serverless,
    Unknown,
}

@Serializable
data class RuntimeLogDto(
    val domain: String? = null,
    val level: RuntimeLogLevel = RuntimeLogLevel.Unknown,
    val message: String = "",
    val messageTruncated: Boolean = false,
    val requestMethod: String? = null,
    val requestPath: String? = null,
    val responseStatusCode: Int? = null,
    val rowId: String? = null,
    val source: RuntimeLogSource = RuntimeLogSource.Unknown,
    val timestampInMs: Long = 0,
)

@Serializable
data class RequestLogsResponseDto(
    val rows: List<RequestLogRowDto> = emptyList(),
    val hasMoreRows: Boolean = false,
)

@Serializable
data class RequestLogRowDto(
    val requestId: String? = null,
    val timestamp: JsonElement? = null,
    val deploymentId: String? = null,
    val domain: String? = null,
    val requestMethod: String? = null,
    val requestPath: String? = null,
    val statusCode: Int? = null,
    val logs: List<RequestLogMessageDto> = emptyList(),
    val events: List<RequestLogEventDto> = emptyList(),
)

@Serializable
data class RequestLogMessageDto(
    val level: RuntimeLogLevel = RuntimeLogLevel.Info,
    val message: String = "",
    val messageTruncated: Boolean = false,
)

@Serializable
data class RequestLogEventDto(
    val source: String? = null,
)
