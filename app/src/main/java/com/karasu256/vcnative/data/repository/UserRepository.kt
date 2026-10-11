package com.karasu256.vcnative.data.repository

import com.karasu256.vcnative.data.local.UserDao
import com.karasu256.vcnative.data.model.VercelUser
import com.karasu256.vcnative.data.remote.VercelApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val dao: UserDao,
    api: VercelApi,
    apiKeyRepository: ApiKeyRepository,
) : CacheRepository<VercelUser>(
    source = { apiKeyRepository.read()?.let { api.getUser(it) } },
) {
    override suspend fun read(): VercelUser? = dao.get()

    override suspend fun write(value: VercelUser?) {
        if (value == null) dao.deleteAll() else dao.replace(value)
    }
}
