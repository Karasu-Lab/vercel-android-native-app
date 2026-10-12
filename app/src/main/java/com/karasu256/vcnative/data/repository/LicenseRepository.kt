package com.karasu256.vcnative.data.repository

import android.content.Context
import com.karasu256.vcnative.data.model.License
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LicenseRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun list(): List<License> = withContext(Dispatchers.IO) {
        context.assets.list(DIRECTORY).orEmpty().sorted().map { it.toLicense() }
    }

    suspend fun find(id: String): License = id.toLicense()

    suspend fun read(id: String): String? = withContext(Dispatchers.IO) {
        runCatching {
            context.assets.open("$DIRECTORY/$id").bufferedReader().use { it.readText() }
        }.getOrNull()
    }

    private fun String.toLicense() = License(id = this, name = substringBeforeLast('.'))

    private companion object {
        const val DIRECTORY = "licenses"
    }
}
