package com.example.data.model

import com.example.data.local.entity.CommentEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VideoEntity

/**
 * Extension mapper functions to facilitate bidirectional conversion
 * between PostgreSQL domain models and Room local persistence entities.
 */

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    username = username,
    email = email,
    passwordHash = passwordHash,
    avatarUrl = avatarUrl,
    bio = bio,
    isCreator = isCreator,
    createdAt = createdAt
)

fun UserEntity.toDomain(): User = User(
    id = id,
    username = username,
    email = email,
    passwordHash = passwordHash,
    avatarUrl = avatarUrl,
    bio = bio,
    isCreator = isCreator,
    createdAt = createdAt
)

fun Video.toEntity(
    username: String = "",
    userAvatar: String = "",
    category: String = "All",
    isLiked: Boolean = false,
    isSaved: Boolean = false,
    audioTrackTitle: String = "Original Sound - GOAL Audio",
    hashtags: String = "#goal #highlights"
): VideoEntity = VideoEntity(
    id = id,
    userId = userId,
    username = username,
    userAvatar = userAvatar,
    isVerified = true,
    title = title,
    description = description ?: "",
    type = if (type.equals("short", ignoreCase = true)) "short" else "standard",
    videoUrl = videoUrl,
    thumbnailUrl = thumbnailUrl ?: "",
    viewsCount = viewsCount,
    likesCount = likesCount,
    commentsCount = commentsCount,
    durationSeconds = durationSeconds,
    category = category,
    createdAt = createdAt,
    isLiked = isLiked,
    isSaved = isSaved,
    audioTrackTitle = audioTrackTitle,
    hashtags = hashtags
)

fun VideoEntity.toDomain(): Video = Video(
    id = id,
    userId = userId,
    title = title,
    description = description.ifBlank { null },
    type = if (type.lowercase() == "short") "short" else "standard",
    videoUrl = videoUrl,
    thumbnailUrl = thumbnailUrl.ifBlank { null },
    viewsCount = viewsCount,
    likesCount = likesCount,
    commentsCount = commentsCount,
    durationSeconds = durationSeconds,
    createdAt = createdAt
)

fun Comment.toEntity(
    username: String = "GOAL User",
    userAvatar: String = "",
    likesCount: Long = 0L,
    isLiked: Boolean = false
): CommentEntity = CommentEntity(
    id = id,
    videoId = videoId,
    userId = userId,
    username = username,
    userAvatar = userAvatar,
    content = content,
    createdAt = createdAt,
    likesCount = likesCount,
    isLiked = isLiked
)

fun CommentEntity.toDomain(): Comment = Comment(
    id = id,
    userId = userId,
    videoId = videoId,
    content = content,
    createdAt = createdAt
)
