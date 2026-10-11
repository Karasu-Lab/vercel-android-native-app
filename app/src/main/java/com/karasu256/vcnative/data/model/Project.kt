package com.karasu256.vcnative.data.model

import android.graphics.Bitmap

data class ProjectWithAvatar(
    val project: Project,
    val avatar: Bitmap? = null,
)

data class Project(
    val id: String,
    val name: String,
    val avatarHash: String?,
    val repoId: Long?,
    val org: String?,
    val repo: String?,
    val productionBranch: String?,
)

data class Branch(
    val name: String,
    val sha: String?,
    val commit: BranchCommit?,
)

data class BranchCommit(
    val message: String?,
    val sha: String?,
    val author: String?,
    val committedAt: Long?,
)
