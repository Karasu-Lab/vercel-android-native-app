package com.karasu256.vcnative.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface EnvironmentVariableDao {
    @Query("SELECT * FROM environment_variables WHERE scope = :scope ORDER BY position")
    suspend fun get(scope: String): List<EnvironmentVariableEntity>

    @Insert
    suspend fun insertAll(variables: List<EnvironmentVariableEntity>)

    @Query("DELETE FROM environment_variables WHERE scope = :scope")
    suspend fun deleteScope(scope: String)

    @Query("DELETE FROM environment_variables")
    suspend fun deleteAll()

    @Transaction
    suspend fun replace(scope: String, variables: List<EnvironmentVariableEntity>) {
        deleteScope(scope)
        insertAll(variables)
    }
}
