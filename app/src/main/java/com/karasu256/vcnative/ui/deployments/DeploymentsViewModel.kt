package com.karasu256.vcnative.ui.deployments

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.model.DeploymentDto
import com.karasu256.vcnative.data.model.DeploymentState
import com.karasu256.vcnative.data.model.isInProgress
import com.karasu256.vcnative.data.remote.DeploymentStatusTracker
import com.karasu256.vcnative.data.remote.VercelApi
import com.karasu256.vcnative.R
import com.karasu256.vcnative.data.repository.ApiKeyRepository
import com.karasu256.vcnative.data.repository.DeploymentRepository
import com.karasu256.vcnative.data.repository.ProjectFilterRepository
import com.karasu256.vcnative.ui.components.Refreshable
import com.ramcosta.composedestinations.generated.destinations.ProjectDeploymentsScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeploymentsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val apiKeyRepository: ApiKeyRepository,
    private val api: VercelApi,
    private val statusTracker: DeploymentStatusTracker,
    private val projectFilterRepository: ProjectFilterRepository,
    private val deploymentRepository: DeploymentRepository,
) : ViewModel(), Refreshable {

    private val _uiState = MutableStateFlow(DeploymentsUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DeploymentsEvent>()
    val events = _events.asSharedFlow()

    private val fixedProjectId: String? =
        runCatching { ProjectDeploymentsScreenDestination.argsFrom(savedStateHandle).projectId }.getOrNull()

    private var apiKey: String? = null
    private var nextCursor: Long? = null
    private var projectId: String? = fixedProjectId
    private var loadJob: Job? = null
    private val followJobs = mutableMapOf<String, Job>()
    private val avatars = mutableMapOf<String, ImageBitmap>()
    private val requestedAvatars = mutableSetOf<String>()

    init {
        viewModelScope.launch {
            val key = apiKeyRepository.read() ?: return@launch
            apiKey = key
            if (fixedProjectId != null) {
                reload()
            } else {
                launch {
                    projectFilterRepository.selected.collect { project ->
                        projectId = project?.id
                        _uiState.update { it.copy(filterProject = project) }
                        reload()
                    }
                }
            }

            while (true) {
                delay(5000)
                val state = _uiState.value
                if (state.isRefreshing) continue
                val known = state.newDeployments + state.deployments
                val latest = known.maxOfOrNull { it.createdAt } ?: continue
                val found = try {
                    api.getDeployments(key, since = latest, projectId = projectId).deployments
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    continue
                }
                val knownIds = known.map { it.id }.toSet()
                val fresh = found.map { it.toDeploymentInfo() }.filter { it.id !in knownIds }
                if (fresh.isEmpty()) continue
                _uiState.update {
                    it.copy(newDeployments = (fresh + it.newDeployments).sortedByDescending { deployment -> deployment.createdAt })
                }
                loadAvatars(fresh)
            }
        }

        viewModelScope.launch {
            while (true) {
                delay(1000)
                val now = System.currentTimeMillis()
                _uiState.update { state ->
                    state.copy(
                        deployments = state.deployments.map { deployment ->
                            if (deployment.state.isInProgress) {
                                deployment.copy(elapsedSeconds = (now - deployment.startedAt) / 1000)
                            } else {
                                deployment
                            }
                        }
                    )
                }
            }
        }
    }

    override fun refresh() {
        if (_uiState.value.isRefreshing) return
        reload()
    }

    private fun reload() {
        val key = apiKey ?: return
        loadJob?.cancel()
        followJobs.values.forEach { it.cancel() }
        followJobs.clear()
        nextCursor = null
        _uiState.update {
            it.copy(isRefreshing = true, isLoadingMore = false, hasMore = true, newDeployments = emptyList())
        }
        loadJob = viewModelScope.launch { loadPage(key, reset = true) }
    }

    fun showNewDeployments() {
        val fresh = _uiState.value.newDeployments
        if (fresh.isEmpty()) return
        _uiState.update { it.copy(deployments = fresh + it.deployments, newDeployments = emptyList()) }
        fresh.filter { it.state.isInProgress }.forEach { follow(it.id) }
    }

    fun loadMore() {
        val key = apiKey ?: return
        val current = _uiState.value
        if (current.isLoadingMore || current.isRefreshing || !current.hasMore) return
        _uiState.update { it.copy(isLoadingMore = true) }
        loadJob = viewModelScope.launch { loadPage(key, reset = false) }
    }

    private suspend fun loadPage(apiKey: String, reset: Boolean) {
        val page = try {
            api.getDeployments(apiKey, until = nextCursor, projectId = projectId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
        if (page == null) {
            _uiState.update { it.copy(isLoadingMore = false, isRefreshing = false) }
            return
        }

        nextCursor = page.pagination?.next
        val existing = if (reset) emptyList() else _uiState.value.deployments
        val loadedIds = existing.map { it.id }.toSet()
        val deployments = page.deployments
            .map { it.toDeploymentInfo() }
            .filter { it.id !in loadedIds }
        _uiState.update {
            it.copy(
                deployments = existing + deployments,
                isLoadingMore = false,
                isRefreshing = false,
                hasMore = nextCursor != null,
            )
        }

        deployments.filter { it.state.isInProgress }.forEach { follow(it.id) }
        loadAvatars(deployments)
    }

    fun requestAction(action: DeploymentAction) {
        if (_uiState.value.isActionRunning) return
        if (action is DeploymentAction.Rollback && !action.deployment.canRollback) {
            viewModelScope.launch { _events.emit(DeploymentsEvent.Message(R.string.error_rollback_unavailable)) }
            return
        }
        _uiState.update { it.copy(pendingAction = action) }
    }

    fun dismissAction() {
        if (_uiState.value.isActionRunning) return
        _uiState.update { it.copy(pendingAction = null) }
    }

    fun confirmAction() {
        val state = _uiState.value
        val action = state.pendingAction ?: return
        if (state.isActionRunning) return
        _uiState.update { it.copy(isActionRunning = true) }
        viewModelScope.launch {
            val messageRes = when (action) {
                is DeploymentAction.Rollback -> rollback(action.deployment)
                is DeploymentAction.Cancel -> cancel(action.deployment)
                is DeploymentAction.Delete -> delete(action.deployment)
            }
            _uiState.update { it.copy(pendingAction = null, isActionRunning = false) }
            _events.emit(DeploymentsEvent.Message(messageRes))
        }
    }

    private suspend fun rollback(deployment: DeploymentInfo): Int {
        val projectId = deployment.projectId ?: return R.string.error_rollback_deployment
        val succeeded = deploymentRepository.rollback(projectId, deployment.id)
        return if (succeeded) R.string.rollback_deployment_started else R.string.error_rollback_deployment
    }

    private suspend fun cancel(deployment: DeploymentInfo): Int {
        val state = deploymentRepository.cancel(deployment.id) ?: return R.string.error_cancel_deployment
        followJobs.remove(deployment.id)?.cancel()
        val now = System.currentTimeMillis()
        _uiState.update {
            it.copy(
                deployments = it.deployments.map { item ->
                    if (item.id != deployment.id) return@map item
                    item.copy(
                        state = state,
                        finishedAt = now,
                        elapsedSeconds = elapsedSeconds(item.startedAt, now, state.isInProgress),
                    )
                }
            )
        }
        return R.string.deployment_canceled
    }

    private suspend fun delete(deployment: DeploymentInfo): Int {
        if (!deploymentRepository.delete(deployment.id)) return R.string.error_delete_deployment
        followJobs.remove(deployment.id)?.cancel()
        _uiState.update { state ->
            state.copy(
                deployments = state.deployments.filter { it.id != deployment.id },
                newDeployments = state.newDeployments.filter { it.id != deployment.id },
            )
        }
        return R.string.deployment_deleted
    }

    private fun loadAvatars(deployments: List<DeploymentInfo>) {
        deployments.flatMap { it.creators }
            .map { it.username }
            .filter { requestedAvatars.add(it) }
            .forEach { username ->
                viewModelScope.launch {
                    val avatar = try {
                        api.getAvatarBitmap(username, 64)?.asImageBitmap()
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        null
                    }
                    if (avatar == null) {
                        requestedAvatars.remove(username)
                        return@launch
                    }
                    avatars[username] = avatar
                    val withAvatar = { deployment: DeploymentInfo ->
                        deployment.copy(
                            creators = deployment.creators.map { creator ->
                                if (creator.username == username) creator.copy(avatar = avatar) else creator
                            }
                        )
                    }
                    _uiState.update { state ->
                        state.copy(
                            deployments = state.deployments.map(withAvatar),
                            newDeployments = state.newDeployments.map(withAvatar),
                        )
                    }
                }
            }
    }

    private fun follow(id: String) {
        followJobs[id]?.cancel()
        followJobs[id] = viewModelScope.launch {
            statusTracker.track(id).collect { status ->
                _uiState.update { state ->
                    state.copy(
                        deployments = state.deployments.map { deployment ->
                            if (deployment.id != id) return@map deployment
                            val startedAt = status.buildingAt ?: deployment.startedAt
                            deployment.copy(
                                state = status.readyState,
                                startedAt = startedAt,
                                finishedAt = status.ready,
                                elapsedSeconds = elapsedSeconds(startedAt, status.ready, status.readyState.isInProgress),
                            )
                        }
                    )
                }
            }
            followJobs.remove(id)
        }
    }

    private fun DeploymentDto.toDeploymentInfo(): DeploymentInfo {
        val startedAt = buildingAt ?: created
        val state = if (readyState != DeploymentState.Unknown) readyState else this.state
        return DeploymentInfo(
            id = uid,
            projectId = projectId,
            projectName = name,
            target = target,
            description = meta?.githubCommitMessage
                ?: meta?.gitlabCommitMessage
                ?: meta?.bitbucketCommitMessage
                ?: url,
            branch = meta?.githubCommitRef ?: meta?.gitlabCommitRef ?: meta?.bitbucketCommitRef,
            createdAt = created,
            creators = listOfNotNull(
                creator?.username?.let { DeploymentCreator(username = it, avatar = avatars[it]) }
            ),
            state = state,
            startedAt = startedAt,
            finishedAt = ready,
            elapsedSeconds = elapsedSeconds(startedAt, ready, state.isInProgress),
        )
    }

    private fun elapsedSeconds(startedAt: Long, finishedAt: Long?, inProgress: Boolean): Long? = when {
        inProgress -> (System.currentTimeMillis() - startedAt) / 1000
        finishedAt != null -> (finishedAt - startedAt) / 1000
        else -> null
    }
}
