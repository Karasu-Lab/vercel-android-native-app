package com.karasu256.vcnative.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface AppThemeDao {
    @Query("SELECT value FROM app_theme WHERE id = ${AppThemeEntity.SINGLE_ID}")
    suspend fun get(): String?

    @Upsert
    suspend fun upsert(entity: AppThemeEntity)

    @Query("DELETE FROM app_theme")
    suspend fun deleteAll()
}
