package com.karasu256.vcnative.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ApiKeyEntity::class, AppThemeEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun apiKeyDao(): ApiKeyDao

    abstract fun appThemeDao(): AppThemeDao

    companion object {
        const val NAME = "app.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `app_theme` (`id` INTEGER NOT NULL, `value` TEXT NOT NULL, PRIMARY KEY(`id`))"
                )
            }
        }
    }
}
