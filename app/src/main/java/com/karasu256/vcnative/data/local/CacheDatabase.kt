package com.karasu256.vcnative.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.karasu256.vcnative.data.model.VercelUser

@Database(
    entities = [VercelUser::class],
    version = 1,
    exportSchema = false,
)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        const val NAME = "cache.db"
    }
}
