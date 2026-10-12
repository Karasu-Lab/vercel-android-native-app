package com.karasu256.vcnative.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

@Serializable
data class EnvironmentVariablesResponseDto(
    val envs: List<EnvironmentVariableDto> = emptyList(),
)

@Serializable
data class SharedEnvironmentVariablesResponseDto(
    val data: List<EnvironmentVariableDto> = emptyList(),
)

@Serializable
data class EnvironmentVariableDto(
    val id: String = "",
    val key: String = "",
    val value: String? = null,
    val type: String? = null,
    val target: JsonElement? = null,
    val projectId: JsonElement? = null,
)

@Serializable
data class EnvironmentVariableRequestDto(
    val key: String? = null,
    val value: String? = null,
    val type: String? = null,
    val target: List<String>? = null,
    val projectIdUpdates: ProjectIdUpdatesDto? = null,
)

@Serializable
data class ProjectIdUpdatesDto(
    val link: List<String>? = null,
    val unlink: List<String>? = null,
)

@Serializable
data class SharedEnvironmentVariableCreateRequestDto(
    val evs: List<SharedEnvironmentVariableEntryDto>,
    val type: String,
    val target: List<String>,
    val projectId: List<String>,
)

@Serializable
data class SharedEnvironmentVariableEntryDto(
    val key: String,
    val value: String,
)

@Serializable
data class SharedEnvironmentVariableUpdateRequestDto(
    val updates: Map<String, EnvironmentVariableRequestDto>,
)

@Serializable
data class SharedEnvironmentVariableDeleteRequestDto(
    val ids: List<String>,
)

const val ENV_TYPE_PLAIN = "plain"
const val ENV_TYPE_ENCRYPTED = "encrypted"
const val ENV_TYPE_SENSITIVE = "sensitive"

fun EnvironmentVariableDto.toEnvironmentVariable(): EnvironmentVariable {
    val names = when (val element = target) {
        is JsonArray -> element.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
        is JsonPrimitive -> listOfNotNull(element.contentOrNull)
        else -> emptyList()
    }
    return EnvironmentVariable(
        id = id,
        key = key,
        value = value.takeIf { type == ENV_TYPE_PLAIN },
        isSecret = type == ENV_TYPE_SENSITIVE,
        targets = names.mapNotNull(EnvironmentTarget::fromApi).toSet(),
        linkedProjectIds = (projectId as? JsonArray)
            ?.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
            .orEmpty()
            .toSet(),
    )
}
