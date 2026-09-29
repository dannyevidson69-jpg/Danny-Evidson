package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.GoalDatabase
import com.example.data.local.entity.CommentEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.CreatorBalanceEntity
import com.example.data.local.entity.LiveStreamEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.VideoEntity
import com.example.data.repository.GoalRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Main : Screen() // Holds the 5 bottom nav tabs
    data class VideoPlayer(val videoId: String) : Screen()
    data class LiveRoom(val streamId: String) : Screen()
    data class ChatConversation(val conversationId: String) : Screen()
    object Search : Screen()
    object CreatorStudioDashboard : Screen()
}

enum class MainTab {
    HOME,
    SHORTS,
    CREATE,
    LIVE,
    PROFILE
}

data class TranscodeStep(
    val resolution: String,
    val bitrate: String,
    val progress: Float, // 0.0 to 1.0
    val isComplete: Boolean = false
)

class GoalViewModel(application: Application) : AndroidViewModel(application) {
    private val database = GoalDatabase.getInstance(application)
    private val repository = GoalRepository(database)

    // Navigation state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Main)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedTab = MutableStateFlow(MainTab.HOME)
    val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

    // Video streams
    val standardVideos: StateFlow<List<VideoEntity>> = repository.standardVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shortsVideos: StateFlow<List<VideoEntity>> = repository.shortsVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedVideos: StateFlow<List<VideoEntity>> = repository.savedVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val liveStreams: StateFlow<List<LiveStreamEntity>> = repository.liveStreams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val conversations: StateFlow<List<ConversationEntity>> = repository.conversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val creatorBalance: StateFlow<CreatorBalanceEntity?> = repository.creatorBalance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions: StateFlow<List<TransactionEntity>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Category filter for Home
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val filteredStandardVideos: StateFlow<List<VideoEntity>> = combine(
        standardVideos,
        _selectedCategory
    ) { videos, cat ->
        if (cat == "All") videos else videos.filter { it.category.equals(cat, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently playing video
    private val _activeVideoId = MutableStateFlow<String?>(null)
    val activeVideoId: StateFlow<String?> = _activeVideoId.asStateFlow()

    private val _activeVideo = MutableStateFlow<VideoEntity?>(null)
    val activeVideo: StateFlow<VideoEntity?> = _activeVideo.asStateFlow()

    // Comments for active video
    private val _activeComments = MutableStateFlow<List<CommentEntity>>(emptyList())
    val activeComments: StateFlow<List<CommentEntity>> = _activeComments.asStateFlow()

    // Active Live Stream
    private val _activeLiveStream = MutableStateFlow<LiveStreamEntity?>(null)
    val activeLiveStream: StateFlow<LiveStreamEntity?> = _activeLiveStream.asStateFlow()

    // Active Chat Conversation
    private val _activeConversation = MutableStateFlow<ConversationEntity?>(null)
    val activeConversation: StateFlow<ConversationEntity?> = _activeConversation.asStateFlow()

    private val _conversationMessages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val conversationMessages: StateFlow<List<MessageEntity>> = _conversationMessages.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<VideoEntity>> = repository.allVideos
        .combine(_searchQuery) { videos, q ->
            if (q.isBlank()) emptyList()
            else videos.filter {
                it.title.contains(q, ignoreCase = true) ||
                it.description.contains(q, ignoreCase = true) ||
                it.category.contains(q, ignoreCase = true) ||
                it.username.contains(q, ignoreCase = true)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Toast/Snackbar notifications
    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents: SharedFlow<String> = _uiEvents.asSharedFlow()

    // Transcoding simulation state for Creator Upload
    private val _isTranscoding = MutableStateFlow(false)
    val isTranscoding: StateFlow<Boolean> = _isTranscoding.asStateFlow()

    private val _transcodeSteps = MutableStateFlow<List<TranscodeStep>>(emptyList())
    val transcodeSteps: StateFlow<List<TranscodeStep>> = _transcodeSteps.asStateFlow()

    fun selectTab(tab: MainTab) {
        _selectedTab.value = tab
        _currentScreen.value = Screen.Main
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        when (screen) {
            is Screen.VideoPlayer -> loadVideo(screen.videoId)
            is Screen.LiveRoom -> loadLiveStream(screen.streamId)
            is Screen.ChatConversation -> loadConversation(screen.conversationId)
            else -> {}
        }
    }

    fun navigateBack(): Boolean {
        return if (_currentScreen.value !is Screen.Main) {
            _currentScreen.value = Screen.Main
            true
        } else {
            false
        }
    }

    fun loadVideo(videoId: String) {
        _activeVideoId.value = videoId
        viewModelScope.launch {
            repository.recordView(videoId)
            repository.getVideoById(videoId).collect { video ->
                _activeVideo.value = video
            }
        }
        viewModelScope.launch {
            repository.getCommentsForVideo(videoId).collect { comments ->
                _activeComments.value = comments
            }
        }
    }

    fun loadLiveStream(streamId: String) {
        val stream = liveStreams.value.firstOrNull { it.id == streamId }
        _activeLiveStream.value = stream
    }

    fun loadConversation(convId: String) {
        val conv = conversations.value.firstOrNull { it.id == convId }
        _activeConversation.value = conv
        viewModelScope.launch {
            repository.getMessagesForConversation(convId).collect { msgs ->
                _conversationMessages.value = msgs
            }
        }
    }

    fun toggleLike(video: VideoEntity) {
        viewModelScope.launch {
            repository.toggleLike(video)
        }
    }

    fun toggleSave(video: VideoEntity) {
        viewModelScope.launch {
            repository.toggleSave(video)
            val msg = if (!video.isSaved) "Saved to your GOAL collection" else "Removed from collection"
            _uiEvents.emit(msg)
        }
    }

    fun addComment(videoId: String, content: String) {
        viewModelScope.launch {
            repository.addComment(videoId, content)
            _uiEvents.emit("Comment posted!")
        }
    }

    fun toggleCommentLike(comment: CommentEntity) {
        viewModelScope.launch {
            repository.toggleCommentLike(comment)
        }
    }

    fun sendMessage(conversationId: String, receiverId: String, content: String) {
        viewModelScope.launch {
            repository.sendMessage(conversationId, receiverId, content)

            // Simulate incoming socket reply from recipient after a brief delay
            delay(1200)
            val autoReplies = listOf(
                "Appreciate the support! Keep watching GOAL streams 🚀",
                "Working on the next video breakdown now! Stay tuned.",
                "Let's collab soon! Ping me when you stream.",
                "Awesome play! Let's get that highlight featured on GOAL!"
            )
            val replyText = autoReplies.random()
            repository.sendMessage(conversationId, "current_user", replyText)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Ingest & FFmpeg Transcoding Pipeline simulation
    fun startUploadWithTranscode(
        title: String,
        description: String,
        category: String,
        isShort: Boolean
    ) {
        viewModelScope.launch {
            _isTranscoding.value = true
            val steps = listOf(
                TranscodeStep("360p (800k)", "640x360", 0f),
                TranscodeStep("480p (1400k)", "854x480", 0f),
                TranscodeStep("720p (2800k)", "1280x720", 0f),
                TranscodeStep("1080p (5000k)", "1920x1080", 0f),
                TranscodeStep("Thumbnail", "1280x720 JPEG", 0f)
            )
            _transcodeSteps.value = steps

            for (i in steps.indices) {
                for (p in 1..5) {
                    delay(120)
                    _transcodeSteps.value = _transcodeSteps.value.mapIndexed { index, step ->
                        if (index == i) step.copy(progress = p / 5f, isComplete = (p == 5))
                        else step
                    }
                }
            }

            delay(200)
            val newVid = repository.uploadVideo(
                title = title,
                description = description,
                type = if (isShort) "short" else "standard",
                category = category,
                durationSeconds = if (isShort) 30 else 320
            )

            _isTranscoding.value = false
            _uiEvents.emit("Video published successfully! Added to GOAL feed.")
            if (isShort) {
                selectTab(MainTab.SHORTS)
            } else {
                selectTab(MainTab.HOME)
                navigateTo(Screen.VideoPlayer(newVid.id))
            }
        }
    }

    fun scheduleLiveStream(title: String, category: String) {
        viewModelScope.launch {
            val key = repository.scheduleLiveStream(title, category)
            _uiEvents.emit("Broadcast Room Initialized! Stream Key: $key")
            selectTab(MainTab.LIVE)
        }
    }

    fun sendTip(creatorName: String, amount: Double) {
        viewModelScope.launch {
            repository.sendTip(creatorName, amount)
            _uiEvents.emit("Sent $$amount Super Chat tip to @$creatorName! ⚽🎉")
        }
    }

    fun requestPayout(amount: Double) {
        viewModelScope.launch {
            val success = repository.requestPayout(amount)
            if (success) {
                _uiEvents.emit("Payout request for $$amount submitted to processing.")
            } else {
                _uiEvents.emit("Insufficient balance for requested payout.")
            }
        }
    }
}
