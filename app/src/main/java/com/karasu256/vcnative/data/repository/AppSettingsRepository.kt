package com.karasu256.vcnative.data.repository

import com.karasu256.vcnative.data.local.AppSettingsDao
import com.karasu256.vcnative.data.local.AppSettingsEntity
import com.karasu256.vcnative.data.model.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsRepository @Inject constructor(
    private val dao: AppSettingsDao,
) : DataStoreRepository<AppSettings>() {

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    override suspend fun read(): AppSettings? = dao.get()?.let { entity ->
        AppSettings(
            biometricLockEnabled = entity.biometricLockEnabled,
            networkLoggingEnabled = entity.networkLoggingEnabled,
        )
    }

    override suspend fun write(value: AppSettings?) {
        if (value == null) {
            dao.deleteAll()
        } else {
            dao.upsert(
                AppSettingsEntity(
                    biometricLockEnabled = value.biometricLockEnabled,
                    networkLoggingEnabled = value.networkLoggingEnabled,
                )
            )
        }
        _settings.value = value ?: AppSettings()
    }

    override suspend fun restore(): AppSettings? =
        super.restore().also { _settings.value = it ?: AppSettings() }

    suspend fun update(transform: (AppSettings) -> AppSettings) {
        write(transform(read() ?: AppSettings()))
    }
}
