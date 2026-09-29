package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Room Entity and Kotlin data class mirroring the PostgreSQL `videos` table:
 *
 * ```sql
 * CREATE TABLE videos (
 *     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 *     user_id UUID REFERENCES users(id) ON DELETE CASCADE,
 *     title VARCHAR(255) NOT NULL,
 *     description TEXT,
 *     type video_type NOT NULL,
 *     video_url TEXT NOT NULL,
 *     thumbnail_url TEXT,
 *     views_count BIGINT DEFAULT 0,
 *     likes_count BIGINT DEFAULT 0,
 *     comments_count BIGINT DEFAULT 0,
 *     duration_seconds INT NOT NULL,
 *     created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
 * );
 * ```
 */
@Entity(
    tableName = "videos",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"])
    ]
)
data class Video(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "type")
    val type: String = "standard", // 'standard' or 'short' mirroring PostgreSQL video_type ENUM

    @ColumnInfo(name = "video_url")
    val videoUrl: String,

    @ColumnInfo(name = "thumbnail_url")
    val thumbnailUrl: String? = null,

    @ColumnInfo(name = "views_count")
    val viewsCount: Long = 0L,

    @ColumnInfo(name = "likes_count")
    val likesCount: Long = 0L,

    @ColumnInfo(name = "comments_count")
    val commentsCount: Long = 0L,

    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    @Ignore
    val videoType: VideoType = if (type.equals("short", ignoreCase = true)) VideoType.SHORT else VideoType.STANDARD

    @Ignore
    constructor(
        id: String = UUID.randomUUID().toString(),
        userId: String,
        title: String,
        description: String? = null,
        type: VideoType,
        videoUrl: String,
        thumbnailUrl: String? = null,
        viewsCount: Long = 0L,
        likesCount: Long = 0L,
        commentsCount: Long = 0L,
        durationSeconds: Int = 0,
        createdAt: Long = System.currentTimeMillis()
    ) : this(
        id = id,
        userId = userId,
        title = title,
        description = description,
        type = if (type == VideoType.SHORT) "short" else "standard",
        videoUrl = videoUrl,
        thumbnailUrl = thumbnailUrl,
        viewsCount = viewsCount,
        likesCount = likesCount,
        commentsCount = commentsCount,
        durationSeconds = durationSeconds,
        createdAt = createdAt
    )
}
