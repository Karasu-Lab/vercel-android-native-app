package com.karasu256.vcnative.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.karasu256.vcnative.data.model.VercelUser

@Database(
    entities = [VercelUser::class, EnvironmentVariableEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun environmentVariableDao(): EnvironmentVariableDao

    companion object {
        const val NAME = "cache.db"
    }
}
