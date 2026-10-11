package com.karasu256.vcnative.di

import android.content.Context
import androidx.room.Room
import com.karasu256.vcnative.data.local.ApiKeyDao
import com.karasu256.vcnative.data.local.AppDatabase
import com.karasu256.vcnative.data.local.AppThemeDao
import com.karasu256.vcnative.data.local.CacheDatabase
import com.karasu256.vcnative.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            File(context.filesDir, AppDatabase.NAME).absolutePath,
        ).addMigrations(AppDatabase.MIGRATION_1_2).build()

    @Provides
    @Singleton
    fun provideCacheDatabase(@ApplicationContext context: Context): CacheDatabase =
        Room.databaseBuilder(
            context,
            CacheDatabase::class.java,
            File(context.cacheDir, CacheDatabase.NAME).absolutePath,
        ).fallbackToDestructiveMigration(dropAllTables = true).build()

    @Provides
    fun provideApiKeyDao(database: AppDatabase): ApiKeyDao = database.apiKeyDao()

    @Provides
    fun provideAppThemeDao(database: AppDatabase): AppThemeDao = database.appThemeDao()

    @Provides
    fun provideUserDao(database: CacheDatabase): UserDao = database.userDao()
}
