package com.karasu256.vcnative.data.model

enum class EnvironmentTarget(val apiValue: String) {
    Production("production"),
    Preview("preview"),
    Development("development");

    companion object {
        fun fromApi(value: String): EnvironmentTarget? = entries.firstOrNull { it.apiValue == value }
    }
}

data class EnvironmentVariable(
    val id: String,
    val key: String,
    val value: String?,
    val isSecret: Boolean,
    val targets: Set<EnvironmentTarget>,
    val linkedProjectIds: Set<String> = emptySet(),
    val isShared: Boolean = false,
)

data class EnvironmentVariableInput(
    val key: String,
    val value: String,
    val isSecret: Boolean,
    val targets: Set<EnvironmentTarget>,
    val linkedProjectIds: Set<String> = emptySet(),
    val sharedVariable: EnvironmentVariable? = null,
)
