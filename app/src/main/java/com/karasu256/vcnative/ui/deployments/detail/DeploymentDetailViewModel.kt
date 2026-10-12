package com.karasu256.vcnative.ui.deployments.detail

import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.model.DeploymentDetailDto
import com.karasu256.vcnative.data.model.isInProgress
import com.karasu256.vcnative.data.remote.DeploymentStatusTracker
import com.karasu256.vcnative.data.remote.VercelApi
import com.karasu256.vcnative.data.repository.ApiKeyRepository
import com.karasu256.vcnative.ui.deployments.DeploymentCreator
import com.ramcosta.composedestinations.generated.destinations.DeploymentDetailScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeploymentDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val apiKeyRepository: ApiKeyRepository,
    private val api: VercelApi,
    private val statusTracker: DeploymentStatusTracker,
) : ViewModel() {

    private val deploymentId = DeploymentDetailScreenDestination.argsFrom(savedStateHandle).deploymentId

    private val _uiState = MutableStateFlow(DeploymentDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val apiKey = apiKeyRepository.read() ?: return@launch
            val dto = try {
                api.getDeploymentDetail(apiKey, deploymentId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isThumbnailLoading = false) }
                return@launch
            }
            _uiState.update { it.copy(detail = dto.toDeploymentDetail()) }

            dto.creator?.username?.let { username ->
                launch {
                    val avatar = try {
                        api.getAvatarBitmap(username, 64)?.asImageBitmap()
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        null
                    } ?: return@launch
                    _uiState.update { state ->
                        state.copy(
                            detail = state.detail?.copy(creator = state.detail.creator?.copy(avatar = avatar))
                        )
                    }
                }
            }

            if (dto.readyState.isInProgress) {
                statusTracker.track(deploymentId).transformWhile { status ->
                    emit(status)
                    status.readyState.isInProgress
                }.collect { status ->
                    _uiState.update { state ->
                        val detail = state.detail ?: return@update state
                        val startedAt = status.buildingAt ?: detail.startedAt
                        state.copy(
                            detail = detail.copy(
                                state = status.readyState,
                                startedAt = startedAt,
                                finishedAt = status.ready,
                                elapsedSeconds = elapsedSeconds(startedAt, status.ready, status.readyState.isInProgress),
                            )
                        )
                    }
                }
            }

            val thumbnail = try {
                api.getScreenshotBitmap(apiKey, deploymentId, dto.team?.id)?.asImageBitmap()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null
            }
            _uiState.update { it.copy(thumbnail = thumbnail, isThumbnailLoading = false) }
        }

        viewModelScope.launch {
            while (true) {
                delay(1000)
                val now = System.currentTimeMillis()
                _uiState.update { state ->
                    val detail = state.detail ?: return@update state
                    if (!detail.state.isInProgress) return@update state
                    state.copy(detail = detail.copy(elapsedSeconds = (now - detail.startedAt) / 1000))
                }
            }
        }
    }

    private fun DeploymentDetailDto.toDeploymentDetail(): DeploymentDetail {
        val startedAt = buildingAt ?: createdAt
        return DeploymentDetail(
            id = id,
            projectId = projectId,
            projectName = name,
            siteUrl = (alias.firstOrNull() ?: url)?.let { "https://$it" },
            state = readyState,
            target = target,
            branch = meta?.githubCommitRef ?: meta?.gitlabCommitRef ?: meta?.bitbucketCommitRef,
            commitMessage = meta?.githubCommitMessage ?: meta?.gitlabCommitMessage ?: meta?.bitbucketCommitMessage,
            commitSha = meta?.githubCommitSha?.take(7),
            creator = creator?.username?.let { DeploymentCreator(username = it) },
            createdAt = createdAt,
            startedAt = startedAt,
            finishedAt = ready,
            elapsedSeconds = elapsedSeconds(startedAt, ready, readyState.isInProgress),
            domains = alias,
            regions = regions,
        )
    }

    private fun elapsedSeconds(startedAt: Long, finishedAt: Long?, inProgress: Boolean): Long? = when {
        inProgress -> (System.currentTimeMillis() - startedAt) / 1000
        finishedAt != null -> (finishedAt - startedAt) / 1000
        else -> null
    }
}
