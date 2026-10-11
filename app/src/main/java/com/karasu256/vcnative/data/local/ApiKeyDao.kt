package com.karasu256.vcnative.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ApiKeyDao {
    @Query("SELECT value FROM api_keys WHERE id = ${ApiKeyEntity.SINGLE_ID}")
    suspend fun get(): String?

    @Upsert
    suspend fun upsert(entity: ApiKeyEntity)

    @Query("DELETE FROM api_keys")
    suspend fun deleteAll()
}
