package com.karasu256.vcnative.ui.deployments

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.ImageBitmap
import com.karasu256.vcnative.R
import com.karasu256.vcnative.data.model.DeploymentState
import com.karasu256.vcnative.data.model.isInProgress
import com.karasu256.vcnative.data.model.Project

data class DeploymentCreator(
    val username: String,
    val avatar: ImageBitmap? = null,
)

data class DeploymentInfo(
    val id: String,
    val projectId: String?,
    val projectName: String,
    val target: String?,
    val description: String?,
    val branch: String?,
    val createdAt: Long,
    val creators: List<DeploymentCreator>,
    val state: DeploymentState,
    val startedAt: Long,
    val finishedAt: Long?,
    val elapsedSeconds: Long?,
) {
    val canRollback: Boolean
        get() = projectId != null && target == "production" && state == DeploymentState.Ready
}

enum class DeploymentTab(@StringRes val labelRes: Int) {
    All(R.string.deployment_tab_all),
    Production(R.string.environment_production),
    Preview(R.string.environment_preview),
    Development(R.string.deployment_tab_development),
    ;

    fun accepts(deployment: DeploymentInfo): Boolean = when (this) {
        All -> true
        Production -> deployment.target == "production"
        Preview -> deployment.target == null || deployment.target == "preview"
        Development -> deployment.target == "development"
    }
}

sealed interface DeploymentAction {
    val deployment: DeploymentInfo

    data class Rollback(override val deployment: DeploymentInfo) : DeploymentAction

    data class Cancel(override val deployment: DeploymentInfo) : DeploymentAction

    data class Delete(override val deployment: DeploymentInfo) : DeploymentAction
}

fun DeploymentInfo.endToStartAction(): DeploymentAction =
    if (state.isInProgress) DeploymentAction.Cancel(this) else DeploymentAction.Delete(this)

data class DeploymentsUiState(
    val deployments: List<DeploymentInfo> = emptyList(),
    val newDeployments: List<DeploymentInfo> = emptyList(),
    val filterProject: Project? = null,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val hasMore: Boolean = true,
    val pendingAction: DeploymentAction? = null,
    val isActionRunning: Boolean = false,
)

sealed interface DeploymentsEvent {
    data class Message(@StringRes val messageRes: Int) : DeploymentsEvent
}
