package com.example.data.repository

import com.example.data.local.GoalDatabase
import com.example.data.local.entity.CommentEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.CreatorBalanceEntity
import com.example.data.local.entity.LiveStreamEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.VideoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class GoalRepository(
    private val database: GoalDatabase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val userDao = database.userDao()
    private val videoDao = database.videoDao()
    private val commentDao = database.commentDao()
    private val messageDao = database.messageDao()
    private val liveStreamDao = database.liveStreamDao()
    private val creatorDao = database.creatorDao()

    init {
        scope.launch {
            checkAndSeedDatabase()
        }
    }

    private suspend fun checkAndSeedDatabase() {
        val existingVideos = videoDao.getAllVideos().first()
        if (existingVideos.isEmpty()) {
            userDao.insertUsers(InitialDataSeed.getInitialUsers())
            videoDao.insertVideos(InitialDataSeed.getInitialVideos())
            commentDao.insertComments(InitialDataSeed.getInitialComments())
            liveStreamDao.insertLiveStreams(InitialDataSeed.getInitialLiveStreams())
            messageDao.insertConversations(InitialDataSeed.getInitialConversations())
            messageDao.insertMessages(InitialDataSeed.getInitialMessages())
            creatorDao.insertBalance(InitialDataSeed.getInitialBalance())
            creatorDao.insertTransactions(InitialDataSeed.getInitialTransactions())
        }
    }

    // User operations
    val allUsers: Flow<List<com.example.data.local.entity.UserEntity>> = userDao.getAllUsers()
    fun getUserById(id: String) = userDao.getUserById(id)
    suspend fun getUserByUsername(username: String) = userDao.getUserByUsername(username)
    suspend fun saveUser(user: com.example.data.local.entity.UserEntity) = userDao.insertUser(user)

    // Video streams
    val allVideos: Flow<List<VideoEntity>> = videoDao.getAllVideos()
    val standardVideos: Flow<List<VideoEntity>> = videoDao.getVideosByType("standard")
    val shortsVideos: Flow<List<VideoEntity>> = videoDao.getVideosByType("short")
    val savedVideos: Flow<List<VideoEntity>> = videoDao.getSavedVideos()

    fun getVideoById(id: String): Flow<VideoEntity?> = videoDao.getVideoById(id)

    fun searchVideos(query: String): Flow<List<VideoEntity>> = videoDao.searchVideos(query)

    suspend fun toggleLike(video: VideoEntity) {
        val newIsLiked = !video.isLiked
        val newLikesCount = if (newIsLiked) video.likesCount + 1 else (video.likesCount - 1).coerceAtLeast(0)
        videoDao.updateLike(video.id, newIsLiked, newLikesCount)
    }

    suspend fun toggleSave(video: VideoEntity) {
        videoDao.updateSaved(video.id, !video.isSaved)
    }

    suspend fun recordView(videoId: String) {
        videoDao.incrementViewsCount(videoId)
    }

    // Comments
    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>> =
        commentDao.getCommentsForVideo(videoId)

    suspend fun addComment(videoId: String, content: String) {
        if (content.isBlank()) return
        val comment = CommentEntity(
            id = "comm_${UUID.randomUUID()}",
            videoId = videoId,
            userId = InitialDataSeed.CURRENT_USER_ID,
            username = InitialDataSeed.CURRENT_USERNAME,
            userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            content = content.trim(),
            createdAt = System.currentTimeMillis()
        )
        commentDao.insertComment(comment)
        videoDao.incrementCommentsCount(videoId)
    }

    suspend fun toggleCommentLike(comment: CommentEntity) {
        val newLiked = !comment.isLiked
        val newLikes = if (newLiked) comment.likesCount + 1 else (comment.likesCount - 1).coerceAtLeast(0)
        commentDao.updateCommentLike(comment.id, newLiked, newLikes)
    }

    // Live Streams
    val liveStreams: Flow<List<LiveStreamEntity>> = liveStreamDao.getAllLiveStreams()

    suspend fun scheduleLiveStream(title: String, category: String): String {
        val streamKey = "live_sk_${(1000..9999).random()}_goal"
        val newStream = LiveStreamEntity(
            id = "live_${UUID.randomUUID()}",
            userId = InitialDataSeed.CURRENT_USER_ID,
            username = InitialDataSeed.CURRENT_USERNAME,
            userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            title = title,
            streamKey = streamKey,
            status = "live",
            viewerCount = 1,
            startedAt = System.currentTimeMillis(),
            bannerUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800",
            category = category
        )
        liveStreamDao.insertLiveStream(newStream)
        return streamKey
    }

    // Messages
    val conversations: Flow<List<ConversationEntity>> = messageDao.getAllConversations()

    fun getMessagesForConversation(convId: String): Flow<List<MessageEntity>> =
        messageDao.getMessagesForConversation(convId)

    suspend fun sendMessage(conversationId: String, receiverId: String, content: String) {
        if (content.isBlank()) return
        val now = System.currentTimeMillis()
        val message = MessageEntity(
            id = "msg_${UUID.randomUUID()}",
            conversationId = conversationId,
            senderId = InitialDataSeed.CURRENT_USER_ID,
            receiverId = receiverId,
            content = content.trim(),
            isRead = false,
            createdAt = now
        )
        messageDao.insertMessage(message)
        messageDao.updateConversation(conversationId, content.trim(), now)
    }

    // Creator Studio & Wallet
    val creatorBalance: Flow<CreatorBalanceEntity?> =
        creatorDao.getBalance(InitialDataSeed.CURRENT_USER_ID)

    val transactions: Flow<List<TransactionEntity>> =
        creatorDao.getTransactions(InitialDataSeed.CURRENT_USER_ID)

    suspend fun uploadVideo(
        title: String,
        description: String,
        type: String,
        category: String,
        durationSeconds: Int = 180
    ): VideoEntity {
        val newVideo = VideoEntity(
            id = "vid_${UUID.randomUUID().toString().take(8)}",
            userId = InitialDataSeed.CURRENT_USER_ID,
            username = InitialDataSeed.CURRENT_USERNAME,
            userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            isVerified = true,
            title = title,
            description = description,
            type = type,
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=800",
            viewsCount = 1,
            likesCount = 0,
            commentsCount = 0,
            durationSeconds = durationSeconds,
            category = category,
            createdAt = System.currentTimeMillis()
        )
        videoDao.insertVideo(newVideo)
        return newVideo
    }

    suspend fun requestPayout(amount: Double): Boolean {
        val currentBalance = creatorDao.getBalance(InitialDataSeed.CURRENT_USER_ID).first() ?: return false
        if (currentBalance.availableBalance < amount || amount <= 0.0) return false

        val newAvailable = currentBalance.availableBalance - amount
        creatorDao.updateBalance(InitialDataSeed.CURRENT_USER_ID, newAvailable, currentBalance.pendingBalance)

        val tx = TransactionEntity(
            id = "tx_${UUID.randomUUID()}",
            userId = InitialDataSeed.CURRENT_USER_ID,
            amount = amount,
            type = "payout",
            status = "processing",
            title = "Payout Request to Connected Account",
            createdAt = System.currentTimeMillis()
        )
        creatorDao.insertTransaction(tx)
        return true
    }

    suspend fun sendTip(creatorName: String, amount: Double) {
        val currentBalance = creatorDao.getBalance(InitialDataSeed.CURRENT_USER_ID).first() ?: return
        val newAvailable = (currentBalance.availableBalance + amount) // simulated incoming or ledger
        creatorDao.updateBalance(InitialDataSeed.CURRENT_USER_ID, newAvailable, currentBalance.pendingBalance)

        val tx = TransactionEntity(
            id = "tx_${UUID.randomUUID()}",
            userId = InitialDataSeed.CURRENT_USER_ID,
            amount = amount,
            type = "tip",
            status = "completed",
            title = "Tip sent to @$creatorName",
            createdAt = System.currentTimeMillis()
        )
        creatorDao.insertTransaction(tx)
    }
}
