package com.karasu256.vcnative.di

import android.util.Log
import com.karasu256.vcnative.data.repository.AppSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideHttpClient(json: Json, settings: AppSettingsRepository): HttpClient = HttpClient(CIO) {
        engine { requestTimeout = 0 }
        expectSuccess = true
        install(ContentNegotiation) { json(json) }
        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) {
                    if (settings.settings.value.networkLoggingEnabled) {
                        Log.d(LOG_TAG, message)
                    }
                }
            }
        }
    }

    private const val LOG_TAG = "Ktor"
}
