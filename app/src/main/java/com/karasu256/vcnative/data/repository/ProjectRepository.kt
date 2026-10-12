package com.karasu256.vcnative.data.repository

import com.karasu256.vcnative.data.model.Project
import com.karasu256.vcnative.data.model.ProjectWithAvatar
import com.karasu256.vcnative.data.remote.VercelApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val api: VercelApi,
) {
    private val _projects = MutableStateFlow<List<ProjectWithAvatar>?>(null)
    val projects = _projects.asStateFlow()

    private val mutex = Mutex()

    suspend fun load(force: Boolean = false) = mutex.withLock {
        if (!force && _projects.value != null) return@withLock
        val apiKey = apiKeyRepository.read() ?: return@withLock
        val projects = try {
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
            if (_projects.value != null) return@withLock
            emptyList()
        }
        val loadedAvatars = _projects.value.orEmpty().associate { it.project.id to it.avatar }
        _projects.value = projects.map { ProjectWithAvatar(it, loadedAvatars[it.id]) }

        coroutineScope {
            projects.forEach { project ->
                if (loadedAvatars[project.id] != null) return@forEach
                val hash = project.avatarHash ?: return@forEach
                launch {
                    val avatar = try {
                        api.getAvatarBitmapByHash(hash, 64)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        null
                    } ?: return@launch
                    _projects.update { list ->
                        list?.map { if (it.project.id == project.id) it.copy(avatar = avatar) else it }
                    }
                }
            }
        }
    }
}
