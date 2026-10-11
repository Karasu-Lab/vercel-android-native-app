package com.karasu256.vcnative.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_keys")
data class ApiKeyEntity(
    @PrimaryKey val id: Int = SINGLE_ID,
    val value: String,
) {
    companion object {
        const val SINGLE_ID = 0
    }
}
