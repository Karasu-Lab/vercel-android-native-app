package com.karasu256.vcnative.data.repository

import com.karasu256.vcnative.data.local.EnvironmentVariableDao
import com.karasu256.vcnative.data.local.EnvironmentVariableEntity
import com.karasu256.vcnative.data.model.EnvironmentTarget
import com.karasu256.vcnative.data.model.EnvironmentVariable

/**
 * Cache of one list of environment variables, identified by [scope]. Values of secret variables
 * are never stored.
 */
class EnvironmentVariableCache(
    private val dao: EnvironmentVariableDao,
    private val scope: String,
    source: suspend () -> List<EnvironmentVariable>?,
) : CacheRepository<List<EnvironmentVariable>>(source) {

    override suspend fun read(): List<EnvironmentVariable>? =
        dao.get(scope).map { it.toModel() }.takeIf { it.isNotEmpty() }

    override suspend fun write(value: List<EnvironmentVariable>?) {
        if (value == null) {
            dao.deleteScope(scope)
        } else {
            dao.replace(scope, value.mapIndexed { index, variable -> variable.toEntity(index) })
        }
    }

    private fun EnvironmentVariable.toEntity(position: Int) = EnvironmentVariableEntity(
        scope = scope,
        id = id,
        position = position,
        key = key,
        value = value.takeUnless { isSecret },
        isSecret = isSecret,
        targets = targets.joinToString(SEPARATOR) { it.apiValue },
        linkedProjectIds = linkedProjectIds.joinToString(SEPARATOR),
        isShared = isShared,
    )

    private fun EnvironmentVariableEntity.toModel() = EnvironmentVariable(
        id = id,
        key = key,
        value = value,
        isSecret = isSecret,
        targets = targets.split(SEPARATOR)
            .mapNotNull(EnvironmentTarget::fromApi)
            .toSet(),
        linkedProjectIds = linkedProjectIds.split(SEPARATOR).filter { it.isNotEmpty() }.toSet(),
        isShared = isShared,
    )

    private companion object {
        const val SEPARATOR = ","
    }
}
