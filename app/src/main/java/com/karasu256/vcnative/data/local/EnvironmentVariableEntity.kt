package com.karasu256.vcnative.data.local

import androidx.room.Entity

@Entity(tableName = "environment_variables", primaryKeys = ["scope", "id"])
data class EnvironmentVariableEntity(
    val scope: String,
    val id: String,
    val position: Int,
    val key: String,
    val value: String?,
    val isSecret: Boolean,
    val targets: String,
    val linkedProjectIds: String,
    val isShared: Boolean,
)
