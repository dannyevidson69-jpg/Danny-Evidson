package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val email: String,
    val passwordHash: String = "",
    val avatarUrl: String? = null,
    val bio: String? = null,
    val isCreator: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val username: String,
    val userAvatar: String,
    val isVerified: Boolean = true,
    val title: String,
    val description: String,
    val type: String, // "standard" or "short"
    val videoUrl: String,
    val thumbnailUrl: String,
    val viewsCount: Long,
    val likesCount: Long,
    val commentsCount: Long,
    val durationSeconds: Int,
    val category: String,
    val createdAt: Long,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val audioTrackTitle: String = "Original Sound - GOAL Audio",
    val hashtags: String = "#goal #highlights #trending"
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val videoId: String,
    val userId: String,
    val username: String,
    val userAvatar: String,
    val content: String,
    val createdAt: Long,
    val likesCount: Long = 0,
    val isLiked: Boolean = false
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val otherUserId: String,
    val otherUsername: String,
    val otherUserAvatar: String,
    val lastMessage: String,
    val updatedAt: Long,
    val unreadCount: Int = 0
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val isRead: Boolean = false,
    val createdAt: Long
)

@Entity(tableName = "live_streams")
data class LiveStreamEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val username: String,
    val userAvatar: String,
    val title: String,
    val streamKey: String,
    val status: String, // "scheduled", "live", "ended"
    val viewerCount: Long,
    val startedAt: Long,
    val bannerUrl: String,
    val category: String
)

@Entity(tableName = "creator_balances")
data class CreatorBalanceEntity(
    @PrimaryKey val userId: String,
    val availableBalance: Double,
    val pendingBalance: Double,
    val currency: String = "USD"
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val amount: Double,
    val type: String, // "subscription", "tip", "payout"
    val status: String, // "completed", "processing"
    val title: String,
    val createdAt: Long
)
