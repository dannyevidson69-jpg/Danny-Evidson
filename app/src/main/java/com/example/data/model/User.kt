package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Room Entity and Kotlin data class mirroring the PostgreSQL `users` table:
 *
 * ```sql
 * CREATE TABLE users (
 *     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 *     username VARCHAR(50) UNIQUE NOT NULL,
 *     email VARCHAR(255) UNIQUE NOT NULL,
 *     password_hash VARCHAR(255) NOT NULL,
 *     avatar_url TEXT,
 *     bio TEXT,
 *     is_creator BOOLEAN DEFAULT false,
 *     created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
 * );
 * ```
 */
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true)
    ]
)
data class User(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String = "",

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String? = null,

    @ColumnInfo(name = "bio")
    val bio: String? = null,

    @ColumnInfo(name = "is_creator")
    val isCreator: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
