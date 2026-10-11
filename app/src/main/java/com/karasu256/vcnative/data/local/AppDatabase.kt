package com.karasu256.vcnative.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ApiKeyEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun apiKeyDao(): ApiKeyDao

    companion object {
        const val NAME = "app.db"
    }
}
