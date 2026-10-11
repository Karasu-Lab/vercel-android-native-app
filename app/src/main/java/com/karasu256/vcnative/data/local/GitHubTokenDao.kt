package com.karasu256.vcnative.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface GitHubTokenDao {
    @Query("SELECT value FROM github_token WHERE id = ${GitHubTokenEntity.SINGLE_ID}")
    suspend fun get(): String?

    @Upsert
    suspend fun upsert(entity: GitHubTokenEntity)

    @Query("DELETE FROM github_token")
    suspend fun deleteAll()
}
