package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CommentEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.CreatorBalanceEntity
import com.example.data.local.entity.LiveStreamEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
}

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY createdAt DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE type = :type ORDER BY createdAt DESC")
    fun getVideosByType(type: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    fun getVideoById(id: String): Flow<VideoEntity?>

    @Query("SELECT * FROM videos WHERE isSaved = 1 ORDER BY createdAt DESC")
    fun getSavedVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchVideos(query: String): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Query("UPDATE videos SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :videoId")
    suspend fun updateLike(videoId: String, isLiked: Boolean, likesCount: Long)

    @Query("UPDATE videos SET isSaved = :isSaved WHERE id = :videoId")
    suspend fun updateSaved(videoId: String, isSaved: Boolean)

    @Query("UPDATE videos SET commentsCount = commentsCount + 1 WHERE id = :videoId")
    suspend fun incrementCommentsCount(videoId: String)

    @Query("UPDATE videos SET viewsCount = viewsCount + 1 WHERE id = :videoId")
    suspend fun incrementViewsCount(videoId: String)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY createdAt DESC")
    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("UPDATE comments SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :id")
    suspend fun updateCommentLike(id: String, isLiked: Boolean, likesCount: Long)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM messages WHERE conversationId = :convId ORDER BY createdAt ASC")
    fun getMessagesForConversation(convId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<ConversationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("UPDATE conversations SET lastMessage = :lastMessage, updatedAt = :updatedAt WHERE id = :convId")
    suspend fun updateConversation(convId: String, lastMessage: String, updatedAt: Long)
}

@Dao
interface LiveStreamDao {
    @Query("SELECT * FROM live_streams ORDER BY startedAt DESC")
    fun getAllLiveStreams(): Flow<List<LiveStreamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveStreams(streams: List<LiveStreamEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveStream(stream: LiveStreamEntity)
}

@Dao
interface CreatorDao {
    @Query("SELECT * FROM creator_balances WHERE userId = :userId LIMIT 1")
    fun getBalance(userId: String): Flow<CreatorBalanceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalance(balance: CreatorBalanceEntity)

    @Query("UPDATE creator_balances SET availableBalance = :available, pendingBalance = :pending WHERE userId = :userId")
    suspend fun updateBalance(userId: String, available: Double, pending: Double)

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTransactions(userId: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)
}
