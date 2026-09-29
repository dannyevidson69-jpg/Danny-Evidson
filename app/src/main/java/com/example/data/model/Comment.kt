package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Room Entity and Kotlin data class mirroring the PostgreSQL `comments` table:
 *
 * ```sql
 * CREATE TABLE comments (
 *     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 *     user_id UUID REFERENCES users(id) ON DELETE CASCADE,
 *     video_id UUID REFERENCES videos(id) ON DELETE CASCADE,
 *     content TEXT NOT NULL,
 *     created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
 * );
 * ```
 */
@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Video::class,
            parentColumns = ["id"],
            childColumns = ["video_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["video_id"])
    ]
)
data class Comment(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "video_id")
    val videoId: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
