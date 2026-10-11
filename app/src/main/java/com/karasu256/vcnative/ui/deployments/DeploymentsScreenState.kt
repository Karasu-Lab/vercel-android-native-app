package com.karasu256.vcnative.ui.deployments

import androidx.compose.ui.graphics.ImageBitmap
import com.karasu256.vcnative.data.model.DeploymentState

data class DeploymentCreator(
    val username: String,
    val avatar: ImageBitmap? = null,
)

data class DeploymentInfo(
    val id: String,
    val projectName: String,
    val description: String?,
    val branch: String?,
    val createdAt: Long,
    val creators: List<DeploymentCreator>,
    val state: DeploymentState,
    val startedAt: Long,
    val finishedAt: Long?,
    val elapsedSeconds: Long?,
)

data class DeploymentsUiState(
    val deployments: List<DeploymentInfo> = emptyList(),
    val newDeployments: List<DeploymentInfo> = emptyList(),
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val hasMore: Boolean = true,
)

val DeploymentState.isInProgress: Boolean
    get() = this == DeploymentState.Queued ||
        this == DeploymentState.Initializing ||
        this == DeploymentState.Building
