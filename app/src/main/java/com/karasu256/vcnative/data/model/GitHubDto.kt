package com.karasu256.vcnative.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubTokenResponseDto(
    @SerialName("access_token") val accessToken: String? = null,
    val error: String? = null,
)

@Serializable
data class GitHubBranchDto(
    val name: String,
    val commit: GitHubBranchCommitDto,
)

@Serializable
data class GitHubBranchCommitDto(
    val sha: String,
)

@Serializable
data class GitHubCommitDto(
    val sha: String,
    val commit: GitHubCommitDetailDto,
    val author: GitHubUserDto? = null,
)

@Serializable
data class GitHubCommitDetailDto(
    val message: String,
    val author: GitHubCommitAuthorDto? = null,
)

@Serializable
data class GitHubCommitAuthorDto(
    val name: String? = null,
    val date: String? = null,
)

@Serializable
data class GitHubUserDto(
    val login: String,
)
