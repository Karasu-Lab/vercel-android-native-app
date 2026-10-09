package com.karasu256.vcnative.di

import android.app.Application
import androidx.room.Room
import com.karasu256.vcnative.data.dao.ExampleDao
import com.karasu256.vcnative.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application, callback: AppDatabase.Callback): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "local_database")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .addCallback(callback)
            .build()
    }

    @Provides
    fun provideExampleDao(database: AppDatabase): ExampleDao {
        return database.getExampleDao()
    }
}