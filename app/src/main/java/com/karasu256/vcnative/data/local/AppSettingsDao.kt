package com.karasu256.vcnative.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = ${AppSettingsEntity.SINGLE_ID}")
    suspend fun get(): AppSettingsEntity?

    @Upsert
    suspend fun upsert(entity: AppSettingsEntity)

    @Query("DELETE FROM app_settings")
    suspend fun deleteAll()
}
