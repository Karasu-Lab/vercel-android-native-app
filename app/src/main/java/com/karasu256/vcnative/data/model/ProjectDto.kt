package com.karasu256.vcnative.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectsResponseDto(
    val projects: List<ProjectDto>,
    val pagination: PaginationDto? = null,
)

@Serializable
data class ProjectDto(
    val id: String,
    val name: String,
    val avatar: String? = null,
    val link: ProjectLinkDto? = null,
)

@Serializable
data class ProjectLinkDto(
    val type: String? = null,
    val org: String? = null,
    val repo: String? = null,
    val repoId: Long? = null,
    val productionBranch: String? = null,
)

@Serializable
data class CreateDeploymentRequestDto(
    val name: String,
    val project: String,
    val target: String? = null,
    val gitSource: GitSourceDto,
)

@Serializable
data class GitSourceDto(
    val type: String,
    val repoId: Long,
    val ref: String,
    val sha: String? = null,
)
