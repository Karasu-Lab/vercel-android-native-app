package com.karasu256.vcnative.ui.deployments

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.model.DeploymentDto
import com.karasu256.vcnative.data.model.DeploymentState
import com.karasu256.vcnative.data.remote.VercelApi
import com.karasu256.vcnative.data.repository.ApiKeyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeploymentsViewModel @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val api: VercelApi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeploymentsUiState())
    val uiState = _uiState.asStateFlow()

    private var apiKey: String? = null
    private var nextCursor: Long? = null
    private var loadJob: Job? = null
    private val followJobs = mutableMapOf<String, Job>()
    private val avatars = mutableMapOf<String, ImageBitmap>()
    private val requestedAvatars = mutableSetOf<String>()

    init {
        viewModelScope.launch {
            apiKey = apiKeyRepository.read() ?: return@launch
            loadMore()
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

    fun refresh() {
        val key = apiKey ?: return
        if (_uiState.value.isRefreshing) return
        loadJob?.cancel()
        followJobs.values.forEach { it.cancel() }
        followJobs.clear()
        nextCursor = null
        _uiState.update { it.copy(isRefreshing = true, isLoadingMore = false, hasMore = true) }
        loadJob = viewModelScope.launch { loadPage(key, reset = true) }
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
            api.getDeployments(apiKey, until = nextCursor)
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

        deployments.filter { it.state.isInProgress }.forEach { follow(apiKey, it.id) }

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
                    _uiState.update { state ->
                        state.copy(
                            deployments = state.deployments.map { deployment ->
                                deployment.copy(
                                    creators = deployment.creators.map { creator ->
                                        if (creator.username == username) creator.copy(avatar = avatar) else creator
                                    }
                                )
                            }
                        )
                    }
                }
            }
    }

    private fun follow(apiKey: String, id: String) {
        followJobs[id]?.cancel()
        followJobs[id] = viewModelScope.launch {
            while (true) {
                try {
                    api.deploymentEvents(apiKey, id).collect {
                        val current = _uiState.value.deployments.firstOrNull { it.id == id }
                        if (current?.state != DeploymentState.Building) {
                            refreshStatus(apiKey, id)
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    delay(3000)
                }
                val inProgress = try {
                    refreshStatus(apiKey, id)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    true
                }
                if (!inProgress) break
                delay(3000)
            }
            followJobs.remove(id)
        }
    }

    private suspend fun refreshStatus(apiKey: String, id: String): Boolean {
        val status = api.getDeploymentStatus(apiKey, id)
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
        return status.readyState.isInProgress
    }

    private fun DeploymentDto.toDeploymentInfo(): DeploymentInfo {
        val startedAt = buildingAt ?: created
        val state = if (readyState != DeploymentState.Unknown) readyState else this.state
        return DeploymentInfo(
            id = uid,
            projectName = name,
            description = meta?.githubCommitMessage
                ?: meta?.gitlabCommitMessage
                ?: meta?.bitbucketCommitMessage
                ?: url,
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
