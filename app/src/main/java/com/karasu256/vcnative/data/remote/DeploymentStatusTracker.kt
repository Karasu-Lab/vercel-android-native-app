package com.karasu256.vcnative.data.remote

import com.karasu256.vcnative.data.model.DeploymentStatusDto
import com.karasu256.vcnative.data.model.isInProgress
import com.karasu256.vcnative.data.repository.ApiKeyRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeploymentStatusTracker @Inject constructor(
    private val api: VercelApi,
    private val apiKeyRepository: ApiKeyRepository,
) {
    fun track(id: String): Flow<DeploymentStatusDto> = flow {
        val apiKey = apiKeyRepository.read() ?: return@flow
        while (true) {
            val status = try {
                api.getDeploymentStatus(apiKey, id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null
            }
            if (status != null) {
                emit(status)
                if (!status.readyState.isInProgress) return@flow
            }

            try {
                var lastRefresh = System.currentTimeMillis()
                api.deploymentEvents(apiKey, id).collect {
                    val now = System.currentTimeMillis()
                    if (now - lastRefresh >= 2000) {
                        lastRefresh = now
                        emit(api.getDeploymentStatus(apiKey, id))
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                delay(3000)
            }
        }
    }
}
