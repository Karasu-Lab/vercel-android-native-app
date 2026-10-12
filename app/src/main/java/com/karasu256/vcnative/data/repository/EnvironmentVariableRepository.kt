package com.karasu256.vcnative.data.repository

import com.karasu256.vcnative.data.local.EnvironmentVariableDao
import com.karasu256.vcnative.data.model.ENV_TYPE_ENCRYPTED
import com.karasu256.vcnative.data.model.ENV_TYPE_SENSITIVE
import com.karasu256.vcnative.data.model.EnvironmentVariable
import com.karasu256.vcnative.data.model.EnvironmentVariableInput
import com.karasu256.vcnative.data.model.EnvironmentVariableRequestDto
import com.karasu256.vcnative.data.model.ProjectIdUpdatesDto
import com.karasu256.vcnative.data.model.SharedEnvironmentVariableCreateRequestDto
import com.karasu256.vcnative.data.model.SharedEnvironmentVariableDeleteRequestDto
import com.karasu256.vcnative.data.model.SharedEnvironmentVariableEntryDto
import com.karasu256.vcnative.data.model.SharedEnvironmentVariableUpdateRequestDto
import com.karasu256.vcnative.data.model.toEnvironmentVariable
import com.karasu256.vcnative.data.remote.VercelApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lists are read from the cache unless [refresh] is requested. Any change invalidates the
 * affected cache so that the next read goes to the network.
 */
@Singleton
class EnvironmentVariableRepository @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val api: VercelApi,
    private val dao: EnvironmentVariableDao,
) {
    suspend fun projectVariables(projectId: String, refresh: Boolean = false): List<EnvironmentVariable> {
        val cache = EnvironmentVariableCache(dao, projectScope(projectId)) {
            loadProjectVariables(projectId)
        }
        return if (refresh) {
            loadProjectVariables(projectId).also { cache.write(it) }
        } else {
            checkNotNull(cache.restore())
        }
    }

    suspend fun createProject(projectId: String, input: EnvironmentVariableInput) {
        api.createProjectEnv(
            apiKey(),
            projectId,
            EnvironmentVariableRequestDto(
                key = input.key,
                value = input.value,
                type = input.type,
                target = input.targetNames,
            ),
        )
        dao.deleteScope(projectScope(projectId))
    }

    suspend fun updateProject(projectId: String, id: String, input: EnvironmentVariableInput) {
        api.updateProjectEnv(apiKey(), projectId, id, input.toUpdateRequest())
        dao.deleteScope(projectScope(projectId))
    }

    suspend fun deleteProject(projectId: String, id: String) {
        api.deleteProjectEnv(apiKey(), projectId, id)
        dao.deleteScope(projectScope(projectId))
    }

    suspend fun sharedVariables(refresh: Boolean = false): List<EnvironmentVariable> {
        val cache = EnvironmentVariableCache(dao, SHARED_SCOPE) { loadSharedVariables() }
        return if (refresh) {
            loadSharedVariables().also { cache.write(it) }
        } else {
            checkNotNull(cache.restore())
        }
    }

    suspend fun createShared(input: EnvironmentVariableInput) {
        api.createSharedEnv(
            apiKey(),
            SharedEnvironmentVariableCreateRequestDto(
                evs = listOf(SharedEnvironmentVariableEntryDto(key = input.key, value = input.value)),
                type = input.type,
                target = input.targetNames,
                projectId = input.linkedProjectIds.toList(),
            ),
        )
        dao.deleteAll()
    }

    suspend fun updateShared(variable: EnvironmentVariable, input: EnvironmentVariableInput) {
        val link = (input.linkedProjectIds - variable.linkedProjectIds).toList()
        val unlink = (variable.linkedProjectIds - input.linkedProjectIds).toList()
        val projectIdUpdates = if (link.isEmpty() && unlink.isEmpty()) {
            null
        } else {
            ProjectIdUpdatesDto(
                link = link.takeIf { it.isNotEmpty() },
                unlink = unlink.takeIf { it.isNotEmpty() },
            )
        }
        api.updateSharedEnv(
            apiKey(),
            SharedEnvironmentVariableUpdateRequestDto(
                updates = mapOf(
                    variable.id to input.toUpdateRequest().copy(projectIdUpdates = projectIdUpdates)
                ),
            ),
        )
        dao.deleteAll()
    }

    suspend fun linkSharedToProject(shared: EnvironmentVariable, projectId: String) {
        api.updateSharedEnv(
            apiKey(),
            SharedEnvironmentVariableUpdateRequestDto(
                updates = mapOf(
                    shared.id to EnvironmentVariableRequestDto(
                        projectIdUpdates = ProjectIdUpdatesDto(link = listOf(projectId)),
                    )
                ),
            ),
        )
        dao.deleteAll()
    }

    suspend fun unlinkSharedFromProject(id: String, projectId: String) {
        api.unlinkSharedEnv(apiKey(), id, projectId)
        dao.deleteAll()
    }

    suspend fun deleteShared(id: String) {
        api.deleteSharedEnv(apiKey(), SharedEnvironmentVariableDeleteRequestDto(ids = listOf(id)))
        dao.deleteAll()
    }

    private suspend fun loadProjectVariables(projectId: String): List<EnvironmentVariable> {
        val apiKey = apiKey()
        val own = api.getProjectEnvs(apiKey, projectId).map { it.toEnvironmentVariable() }
        val ownKeys = own.map { it.key }.toSet()
        val linked = api.getSharedEnvs(apiKey, projectId)
            .map { it.toEnvironmentVariable().copy(isShared = true) }
            .filter { it.key !in ownKeys }
        return (own + linked).withValues { variable ->
            if (variable.isShared) {
                api.getSharedEnv(apiKey, variable.id).value
            } else {
                api.getProjectEnv(apiKey, projectId, variable.id).value
            }
        }
    }

    private suspend fun loadSharedVariables(): List<EnvironmentVariable> {
        val apiKey = apiKey()
        return api.getSharedEnvs(apiKey)
            .map { it.toEnvironmentVariable() }
            .withValues { variable -> api.getSharedEnv(apiKey, variable.id).value }
    }

    private suspend fun List<EnvironmentVariable>.withValues(
        fetch: suspend (EnvironmentVariable) -> String?,
    ): List<EnvironmentVariable> = coroutineScope {
        map { variable ->
            async {
                if (variable.isSecret || variable.value != null) {
                    variable
                } else {
                    variable.copy(value = fetch(variable))
                }
            }
        }.awaitAll()
    }

    private suspend fun apiKey(): String =
        checkNotNull(apiKeyRepository.read()) { "API key is not set" }

    private fun projectScope(projectId: String) = "project:$projectId"

    private val EnvironmentVariableInput.type: String
        get() = if (isSecret) ENV_TYPE_SENSITIVE else ENV_TYPE_ENCRYPTED

    private val EnvironmentVariableInput.targetNames: List<String>
        get() = targets.map { it.apiValue }

    private fun EnvironmentVariableInput.toUpdateRequest() = EnvironmentVariableRequestDto(
        key = key,
        value = value.takeIf { it.isNotEmpty() },
        type = type,
        target = targetNames,
    )

    private companion object {
        const val SHARED_SCOPE = "shared"
    }
}
