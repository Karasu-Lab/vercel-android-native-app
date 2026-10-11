package com.karasu256.vcnative.ui.deployments.detail

import androidx.compose.ui.graphics.ImageBitmap
import com.karasu256.vcnative.data.model.DeploymentState
import com.karasu256.vcnative.ui.deployments.DeploymentCreator

data class DeploymentDetail(
    val id: String,
    val projectName: String,
    val siteUrl: String?,
    val state: DeploymentState,
    val target: String?,
    val branch: String?,
    val commitMessage: String?,
    val commitSha: String?,
    val creator: DeploymentCreator?,
    val createdAt: Long,
    val startedAt: Long,
    val finishedAt: Long?,
    val elapsedSeconds: Long?,
    val domains: List<String>,
    val regions: List<String>,
)

data class DeploymentDetailUiState(
    val detail: DeploymentDetail? = null,
    val thumbnail: ImageBitmap? = null,
    val isThumbnailLoading: Boolean = true,
)
