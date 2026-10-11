package com.karasu256.vcnative.ui.deployments.create

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.ImageBitmap
import com.karasu256.vcnative.data.model.Branch
import com.karasu256.vcnative.data.model.BranchCommit
import com.karasu256.vcnative.data.model.Project

data class ProjectItem(
    val project: Project,
    val avatar: ImageBitmap? = null,
)

data class CreateDeploymentUiState(
    val isProjectDialogOpen: Boolean = false,
    val isGitHubSheetOpen: Boolean = false,
    val projects: List<ProjectItem>? = null,
    val selectedProject: ProjectItem? = null,
    val branches: List<Branch>? = null,
    val selectedBranch: Branch? = null,
    val branchCommit: BranchCommit? = null,
    val isBranchCommitLoading: Boolean = false,
    val isGitHubLinked: Boolean = false,
    val isDeploying: Boolean = false,
    @StringRes val errorRes: Int? = null,
) {
    val canDeploy: Boolean
        get() = selectedProject?.project?.repoId != null && selectedBranch != null
}

sealed interface CreateDeploymentEvent {
    data class OpenUri(val uri: Uri) : CreateDeploymentEvent

    data object Deployed : CreateDeploymentEvent
}
