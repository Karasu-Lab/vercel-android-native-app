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

val DeploymentState.isInProgress: Boolean
    get() = this == DeploymentState.Queued ||
        this == DeploymentState.Initializing ||
        this == DeploymentState.Building

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
    val projectId: String? = null,
    val target: String? = null,
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
    val githubCommitRef: String? = null,
    val githubCommitSha: String? = null,
    val githubCommitAuthorName: String? = null,
    val githubRepo: String? = null,
    val githubOrg: String? = null,
    val gitlabCommitRef: String? = null,
    val bitbucketCommitRef: String? = null,
)

@Serializable
data class DeploymentDetailDto(
    val id: String,
    val name: String,
    val url: String? = null,
    val readyState: DeploymentState = DeploymentState.Unknown,
    val target: String? = null,
    val createdAt: Long,
    val buildingAt: Long? = null,
    val ready: Long? = null,
    val alias: List<String> = emptyList(),
    val regions: List<String> = emptyList(),
    val creator: DeploymentCreatorDto? = null,
    val meta: DeploymentMetaDto? = null,
    val team: DeploymentTeamDto? = null,
)

@Serializable
data class DeploymentTeamDto(
    val id: String,
    val name: String? = null,
    val slug: String? = null,
)

@Serializable
data class DeploymentStatusDto(
    val id: String,
    val readyState: DeploymentState = DeploymentState.Unknown,
    val createdAt: Long? = null,
    val buildingAt: Long? = null,
    val ready: Long? = null,
)

@Serializable
data class DeleteDeploymentResponseDto(
    val uid: String,
    val state: String,
)
