package com.karasu256.vcnative.data.repository

import com.karasu256.vcnative.data.model.Branch
import com.karasu256.vcnative.data.model.CreateDeploymentRequestDto
import com.karasu256.vcnative.data.model.GitSourceDto
import com.karasu256.vcnative.data.model.Project
import com.karasu256.vcnative.data.remote.VercelApi
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeploymentRepository @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val api: VercelApi,
) {
    suspend fun create(project: Project, branch: Branch, sha: String?): Boolean {
        val apiKey = apiKeyRepository.read() ?: return false
        val repoId = project.repoId ?: return false
        return try {
            api.createDeployment(
                apiKey,
                CreateDeploymentRequestDto(
                    name = project.name,
                    project = project.id,
                    target = if (branch.name == project.productionBranch) "production" else null,
                    gitSource = GitSourceDto(
                        type = "github",
                        repoId = repoId,
                        ref = branch.name,
                        sha = sha,
                    ),
                ),
            )
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }
}
