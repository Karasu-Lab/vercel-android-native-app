package com.karasu256.vcnative.ui.deployments.create

import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.R
import com.karasu256.vcnative.data.model.Branch
import com.karasu256.vcnative.data.repository.BranchRepository
import com.karasu256.vcnative.data.repository.DeploymentRepository
import com.karasu256.vcnative.data.repository.GitHubAuthRepository
import com.karasu256.vcnative.data.repository.ProjectFilterRepository
import com.karasu256.vcnative.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateDeploymentViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val branchRepository: BranchRepository,
    private val deploymentRepository: DeploymentRepository,
    private val gitHubAuthRepository: GitHubAuthRepository,
    private val projectFilterRepository: ProjectFilterRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateDeploymentUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<CreateDeploymentEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var branchJob: Job? = null
    private var commitJob: Job? = null

    init {
        viewModelScope.launch {
            projectRepository.projects.collect { projects ->
                val items = projects?.map { ProjectItem(it.project, it.avatar?.asImageBitmap()) }
                _uiState.update { state ->
                    state.copy(
                        projects = items,
                        selectedProject = state.selectedProject?.let { selected ->
                            items?.firstOrNull { it.project.id == selected.project.id } ?: selected
                        },
                    )
                }
            }
        }
        viewModelScope.launch {
            gitHubAuthRepository.restore()
            gitHubAuthRepository.isAuthenticated.collect { linked ->
                val changed = _uiState.value.isGitHubLinked != linked
                _uiState.update { it.copy(isGitHubLinked = linked) }
                if (changed) _uiState.value.selectedProject?.let { selectProject(it) }
            }
        }
    }

    fun openSheet() {
        _uiState.update { it.copy(isSheetOpen = true, errorRes = null) }
        viewModelScope.launch { projectRepository.load() }
        val filter = projectFilterRepository.selected.value ?: return
        if (_uiState.value.selectedProject?.project?.id == filter.id) return
        val item = _uiState.value.projects?.firstOrNull { it.project.id == filter.id } ?: ProjectItem(filter)
        selectProject(item)
    }

    fun dismissSheet() {
        _uiState.update { it.copy(isSheetOpen = false, isProjectDialogOpen = false, isGitHubSheetOpen = false) }
    }

    fun openProjectDialog() {
        _uiState.update { it.copy(isProjectDialogOpen = true) }
    }

    fun dismissProjectDialog() {
        _uiState.update { it.copy(isProjectDialogOpen = false) }
    }

    fun selectProject(project: ProjectItem) {
        branchJob?.cancel()
        commitJob?.cancel()
        _uiState.update {
            it.copy(
                selectedProject = project,
                isProjectDialogOpen = false,
                branches = null,
                selectedBranch = null,
                branchCommit = null,
                isBranchCommitLoading = false,
            )
        }
        branchJob = viewModelScope.launch {
            val branches = branchRepository.getBranches(project.project)
            _uiState.update { it.copy(branches = branches) }
        }
    }

    fun toggleBranch(branch: Branch) {
        val project = _uiState.value.selectedProject ?: return
        commitJob?.cancel()
        if (_uiState.value.selectedBranch?.name == branch.name) {
            _uiState.update { it.copy(selectedBranch = null, branchCommit = null, isBranchCommitLoading = false) }
            return
        }
        _uiState.update {
            it.copy(selectedBranch = branch, branchCommit = branch.commit, isBranchCommitLoading = branch.commit == null)
        }
        if (branch.commit != null) return
        commitJob = viewModelScope.launch {
            val commit = branchRepository.getCommit(project.project, branch)
            _uiState.update { it.copy(branchCommit = commit, isBranchCommitLoading = false) }
        }
    }

    fun requestGitHubLink() {
        viewModelScope.launch {
            _events.send(CreateDeploymentEvent.OpenUri(gitHubAuthRepository.authorizationUri()))
        }
    }

    fun openGitHubSheet() {
        _uiState.update { it.copy(isGitHubSheetOpen = true) }
    }

    fun dismissGitHubSheet() {
        _uiState.update { it.copy(isGitHubSheetOpen = false) }
    }

    fun unlinkGitHub() {
        viewModelScope.launch {
            gitHubAuthRepository.unlink()
            _uiState.update { it.copy(isGitHubSheetOpen = false) }
        }
    }

    fun deploy() {
        val state = _uiState.value
        if (!state.canDeploy || state.isDeploying) return
        val project = state.selectedProject ?: return
        val branch = state.selectedBranch ?: return
        _uiState.update { it.copy(isDeploying = true, errorRes = null) }
        viewModelScope.launch {
            val succeeded = deploymentRepository.create(
                project = project.project,
                branch = branch,
                sha = state.branchCommit?.sha ?: branch.sha,
            )
            if (succeeded) {
                _events.send(CreateDeploymentEvent.Deployed)
                _uiState.update { it.copy(isDeploying = false, selectedBranch = null, branchCommit = null) }
            } else {
                _uiState.update { it.copy(isDeploying = false, errorRes = R.string.error_create_deployment) }
            }
        }
    }
}
