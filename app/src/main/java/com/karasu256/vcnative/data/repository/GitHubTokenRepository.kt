package com.karasu256.vcnative.data.repository

import com.karasu256.vcnative.data.local.GitHubTokenDao
import com.karasu256.vcnative.data.local.GitHubTokenEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubTokenRepository @Inject constructor(
    private val dao: GitHubTokenDao,
) : DataStoreRepository<String>() {
    override suspend fun read(): String? = dao.get()

    override suspend fun write(value: String?) {
        if (value == null) dao.deleteAll() else dao.upsert(GitHubTokenEntity(value = value))
    }
}
