package com.karasu256.vcnative.data.repository

import android.graphics.Bitmap
import com.karasu256.vcnative.data.model.Project
import com.karasu256.vcnative.data.remote.VercelApi
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val api: VercelApi,
) {
    suspend fun getProjects(): List<Project> {
        val apiKey = apiKeyRepository.read() ?: return emptyList()
        return try {
            api.getProjects(apiKey).map { project ->
                Project(
                    id = project.id,
                    name = project.name,
                    avatarHash = project.avatar,
                    repoId = project.link?.repoId,
                    org = project.link?.org,
                    repo = project.link?.repo,
                    productionBranch = project.link?.productionBranch,
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAvatar(project: Project, size: Int): Bitmap? {
        val hash = project.avatarHash ?: return null
        return try {
            api.getAvatarBitmapByHash(hash, size)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }
}
