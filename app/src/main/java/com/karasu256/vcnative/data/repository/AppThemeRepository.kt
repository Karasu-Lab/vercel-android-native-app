package com.karasu256.vcnative.data.repository

import com.karasu256.vcnative.data.local.AppThemeDao
import com.karasu256.vcnative.data.local.AppThemeEntity
import com.karasu256.vcnative.data.model.AppTheme
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppThemeRepository @Inject constructor(
    private val dao: AppThemeDao,
) : DataStoreRepository<AppTheme>() {
    override suspend fun read(): AppTheme? =
        dao.get()?.let { value -> AppTheme.entries.firstOrNull { it.name == value } }

    override suspend fun write(value: AppTheme?) {
        if (value == null) dao.deleteAll() else dao.upsert(AppThemeEntity(value = value.name))
    }
}
