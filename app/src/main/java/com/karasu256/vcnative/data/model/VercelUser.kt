package com.karasu256.vcnative.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class VercelUserResponse(
    val user: VercelUser,
)

@Serializable
@Entity(tableName = "users")
data class VercelUser(
    @PrimaryKey val id: String,
    val email: String,
    val name: String?,
    val username: String,
    val avatar: String?,
    val defaultTeamId: String?,
    val createdAt: Long? = null,
    val stagingPrefix: String? = null,
    val hasTrialAvailable: Boolean? = null,
    val isEnterpriseManaged: Boolean? = null,
    val limited: Boolean = false,
    @Embedded(prefix = "soft_block_") val softBlock: SoftBlock? = null,
)

@Serializable
data class SoftBlock(
    val blockedAt: Long,
    val reason: String,
    val blockedDueToOverageType: String? = null,
    val unpauseAt: Long? = null,
)
