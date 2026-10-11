package com.karasu256.vcnative.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_theme")
data class AppThemeEntity(
    @PrimaryKey val id: Int = SINGLE_ID,
    val value: String,
) {
    companion object {
        const val SINGLE_ID = 0
    }
}
