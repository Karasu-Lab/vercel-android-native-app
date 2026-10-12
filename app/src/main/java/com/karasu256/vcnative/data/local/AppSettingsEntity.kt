package com.karasu256.vcnative.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = SINGLE_ID,
    val biometricLockEnabled: Boolean,
    val networkLoggingEnabled: Boolean,
) {
    companion object {
        const val SINGLE_ID = 0
    }
}
