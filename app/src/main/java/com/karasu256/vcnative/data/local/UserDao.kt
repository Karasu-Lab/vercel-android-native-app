package com.karasu256.vcnative.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.karasu256.vcnative.data.model.VercelUser

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun get(): VercelUser?

    @Upsert
    suspend fun upsert(user: VercelUser)

    @Query("DELETE FROM users")
    suspend fun deleteAll()

    @Transaction
    suspend fun replace(user: VercelUser) {
        deleteAll()
        upsert(user)
    }
}
