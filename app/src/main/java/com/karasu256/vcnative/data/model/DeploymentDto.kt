package com.karasu256.vcnative.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DeploymentState {
    @SerialName("QUEUED") Queued,
    @SerialName("INITIALIZING") Initializing,
    @SerialName("BUILDING") Building,
    @SerialName("ERROR") Error,
    @SerialName("READY") Ready,
    @SerialName("CANCELED") Canceled,
    Unknown,
}

@Serializable
data class DeploymentsResponseDto(
    val deployments: List<DeploymentDto>,
    val pagination: PaginationDto? = null,
)

@Serializable
data class PaginationDto(
    val count: Int? = null,
    val next: Long? = null,
    val prev: Long? = null,
)

@Serializable
data class DeploymentDto(
    val uid: String,
    val name: String,
    val url: String? = null,
    val created: Long,
    val state: DeploymentState = DeploymentState.Unknown,
    val readyState: DeploymentState = DeploymentState.Unknown,
    val creator: DeploymentCreatorDto? = null,
    val meta: DeploymentMetaDto? = null,
    val buildingAt: Long? = null,
    val ready: Long? = null,
)

@Serializable
data class DeploymentCreatorDto(
    val uid: String,
    val username: String? = null,
)

@Serializable
data class DeploymentMetaDto(
    val githubCommitMessage: String? = null,
    val gitlabCommitMessage: String? = null,
    val bitbucketCommitMessage: String? = null,
)

@Serializable
data class DeploymentStatusDto(
    val id: String,
    val readyState: DeploymentState = DeploymentState.Unknown,
    val createdAt: Long? = null,
    val buildingAt: Long? = null,
    val ready: Long? = null,
)
