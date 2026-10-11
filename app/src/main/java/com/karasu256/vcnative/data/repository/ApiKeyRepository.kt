package com.karasu256.vcnative.data.repository

import com.karasu256.vcnative.data.local.ApiKeyDao
import com.karasu256.vcnative.data.local.ApiKeyEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyRepository @Inject constructor(
    private val dao: ApiKeyDao,
) : IRepository<String> {
    override suspend fun read(): String? = dao.get()

    override suspend fun write(value: String?) {
        if (value == null) dao.deleteAll() else dao.upsert(ApiKeyEntity(value = value))
    }
}
