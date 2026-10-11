package com.karasu256.vcnative.data.repository

import com.karasu256.vcnative.data.local.UserDao
import com.karasu256.vcnative.data.model.VercelUser
import com.karasu256.vcnative.data.remote.VercelApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val dao: UserDao,
    private val api: VercelApi,
    private val apiKeyRepository: ApiKeyRepository,
) : ICacheable<VercelUser> {
    override suspend fun read(): VercelUser? {
        dao.get()?.let { return it }
        val apiKey = apiKeyRepository.read() ?: return null
        return api.getUser(apiKey).also { write(it) }
    }

    override suspend fun write(value: VercelUser?) {
        if (value == null) dao.deleteAll() else dao.replace(value)
    }
}
