package com.karasu256.vcnative.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class VercelEventType {
    @SerialName("command") Command,
    @SerialName("stdout") Stdout,
    @SerialName("stderr") Stderr,
    @SerialName("delimiter") Delimiter,
    @SerialName("alias-assigned") AliasAssigned,
    Unknown,
}

@Serializable
data class VercelEvent(
    val type: VercelEventType = VercelEventType.Unknown,
    val deploymentId: String? = null,
    val id: String? = null,
    val created: Long? = null,
    val date: Long? = null,
    val text: String? = null,
    val serial: String? = null,
    val info: VercelEventInfo? = null,
)

@Serializable
data class VercelEventInfo(
    val type: String? = null,
    val name: String? = null,
    val entrypoint: String? = null,
)
