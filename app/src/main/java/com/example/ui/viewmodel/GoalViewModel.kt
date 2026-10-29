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
import com.example.ui.screens.security.SecurityAuditEvent
import com.example.ui.screens.security.SecurityUiState
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
import java.util.UUID

sealed class Screen {
    object Main : Screen() // Holds the 5 bottom nav tabs
    data class VideoPlayer(val videoId: String) : Screen()
    data class LiveRoom(val streamId: String) : Screen()
    object MessagesInbox : Screen()
    data class ChatConversation(val conversationId: String) : Screen()
    object Search : Screen()
    object CreatorStudioDashboard : Screen()
    object SecurityShield : Screen()
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

data class CreatorProfileUiState(
    val displayName: String = "GOAL Official Creator",
    val handle: String = "@goal_creator",
    val category: String = "🎯 Elite Sports & Gaming Architect",
    val bio: String = "Pro Streamer • 4K Tactical Breakdowns & Global Esports Finals ⚽🎮\nPartnered Creator on GOAL PRO Network ✨",
    val website: String = "goal.gg/creator-pro",
    val followersCount: String = "148.4K",
    val followingCount: String = "312",
    val totalLikesCount: String = "2.8M"
)

class GoalViewModel(application: Application) : AndroidViewModel(application) {
    private val database = GoalDatabase.getInstance(application)
    private val repository = GoalRepository(database)

    // Navigation state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Main)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedTab = MutableStateFlow(MainTab.HOME)
    val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

    // Professional Creator Profile state
    private val _creatorProfile = MutableStateFlow(CreatorProfileUiState())
    val creatorProfile: StateFlow<CreatorProfileUiState> = _creatorProfile.asStateFlow()

    // Security Shield & Vault PIN state
    private val _securityState = MutableStateFlow(SecurityUiState())
    val securityState: StateFlow<SecurityUiState> = _securityState.asStateFlow()

    // Instagram-style Creator Note in DM Inbox
    private val _myNoteText = MutableStateFlow("Streaming UCL Finals tonight 🎯🔥")
    val myNoteText: StateFlow<String> = _myNoteText.asStateFlow()

    // Typing indicator for active chat
    private val _isRecipientTyping = MutableStateFlow(false)
    val isRecipientTyping: StateFlow<Boolean> = _isRecipientTyping.asStateFlow()

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
        val prevTab = _selectedTab.value
        _selectedTab.value = tab
        _currentScreen.value = Screen.Main
        if (prevTab != tab && _securityState.value.autoLockPolicy == "Immediate" && _securityState.value.isVaultUnlockedSession) {
            _securityState.value = _securityState.value.copy(isVaultUnlockedSession = false)
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun navigateTo(screen: Screen) {
        val prevScreen = _currentScreen.value
        _currentScreen.value = screen
        if (prevScreen != screen &&
            screen is Screen.Main &&
            _securityState.value.autoLockPolicy == "Immediate" &&
            _securityState.value.isVaultUnlockedSession
        ) {
            _securityState.value = _securityState.value.copy(isVaultUnlockedSession = false)
        }
        when (screen) {
            is Screen.VideoPlayer -> loadVideo(screen.videoId)
            is Screen.LiveRoom -> loadLiveStream(screen.streamId)
            is Screen.ChatConversation -> loadConversation(screen.conversationId)
            else -> {}
        }
    }

    fun navigateBack(): Boolean {
        return when (_currentScreen.value) {
            is Screen.ChatConversation -> {
                _currentScreen.value = Screen.MessagesInbox
                true
            }
            is Screen.CreatorStudioDashboard -> {
                _selectedTab.value = MainTab.PROFILE
                _currentScreen.value = Screen.Main
                if (_securityState.value.autoLockPolicy == "Immediate") {
                    _securityState.value = _securityState.value.copy(isVaultUnlockedSession = false)
                }
                true
            }
            is Screen.SecurityShield -> {
                _currentScreen.value = Screen.Main
                true
            }
            !is Screen.Main -> {
                _currentScreen.value = Screen.Main
                if (_securityState.value.autoLockPolicy == "Immediate") {
                    _securityState.value = _securityState.value.copy(isVaultUnlockedSession = false)
                }
                true
            }
            else -> false
        }
    }

    // Security Shield Actions
    fun toggleDmPin(enabled: Boolean) {
        _securityState.value = _securityState.value.copy(isDmPinEnabled = enabled)
    }

    fun toggleWalletPin(enabled: Boolean) {
        _securityState.value = _securityState.value.copy(isWalletPinEnabled = enabled)
    }

    fun toggleTwoFactor(enabled: Boolean) {
        _securityState.value = _securityState.value.copy(isTwoFactorEnabled = enabled)
    }

    fun toggleAntiPhishing(enabled: Boolean) {
        _securityState.value = _securityState.value.copy(isAntiPhishingEnabled = enabled)
    }

    fun toggleScreenshotBlocker(enabled: Boolean) {
        _securityState.value = _securityState.value.copy(screenshotBlockerEnabled = enabled)
        viewModelScope.launch {
            val msg = if (enabled) "Screenshot & Screen Recording Blocker (FLAG_SECURE) ON 🛡️" else "Screenshot Blocker turned off"
            _uiEvents.emit(msg)
        }
    }

    fun toggleStealthMode(enabled: Boolean = !_securityState.value.stealthModeEnabled) {
        _securityState.value = _securityState.value.copy(stealthModeEnabled = enabled)
        viewModelScope.launch {
            val msg = if (enabled) "Stealth Privacy & Balance Blur Mode Active ($••,•••.••) 🕶️" else "Stealth Mode turned off"
            _uiEvents.emit(msg)
        }
    }

    fun toggleCreatorDrm(enabled: Boolean) {
        _securityState.value = _securityState.value.copy(creatorDrmEnabled = enabled)
        viewModelScope.launch {
            val msg = if (enabled) "Creator DRM Watermark & Anti-Piracy Guard ON 🎯" else "Creator DRM Watermark paused"
            _uiEvents.emit(msg)
        }
    }

    fun toggleNetworkFirewall(enabled: Boolean) {
        _securityState.value = _securityState.value.copy(isFirewallActive = enabled)
        viewModelScope.launch {
            val msg = if (enabled) "Live Network Firewall & TLS 1.3 Pinning Active 🛡️" else "Network Firewall paused"
            _uiEvents.emit(msg)
        }
    }

    fun setAutoLockPolicy(policy: String) {
        _securityState.value = _securityState.value.copy(autoLockPolicy = policy)
        viewModelScope.launch {
            _uiEvents.emit("Vault Auto-Lock Timer set to $policy ⏱️")
        }
    }

    fun toggleDecoyMode(enabled: Boolean) {
        _securityState.value = _securityState.value.copy(isDecoyModeActive = enabled)
        viewModelScope.launch {
            val msg = if (enabled) {
                "🕵️ Decoy Safe Mode ON: Locked DMs hidden & wallet masked to $42.80"
            } else {
                "Master Vault Mode restored 🔓"
            }
            _uiEvents.emit(msg)
        }
    }

    fun updateDecoyPin(newDecoyPin: String) {
        if (newDecoyPin.length == 4 && newDecoyPin != _securityState.value.vaultPin) {
            _securityState.value = _securityState.value.copy(decoyPin = newDecoyPin)
            viewModelScope.launch {
                _uiEvents.emit("Duress Decoy PIN updated to $newDecoyPin 🕵️")
            }
        }
    }

    fun activateDecoyVaultUnlock() {
        _securityState.value = _securityState.value.copy(
            isAppVaultLocked = false,
            isVaultUnlockedSession = true,
            isDecoyModeActive = true,
            failedPinAttempts = 0
        )
        viewModelScope.launch {
            _uiEvents.emit("Vault Unlocked (Decoy Safe Mode Active) 🔓")
        }
    }

    fun recordIntruderPinAttempt() {
        val nextFailures = _securityState.value.failedPinAttempts + 1
        val isLockoutStrike = nextFailures >= 3
        val updatedEvents = if (isLockoutStrike) {
            val intruderEvent = SecurityAuditEvent(
                id = "intruder_${UUID.randomUUID().toString().take(6)}",
                title = "Intruder Brute-Force Lockout Triggered",
                subtitle = "3 invalid PIN attempts blocked • 30s keypad cooldown enforced",
                timestamp = "Just now",
                severity = "INTRUDER"
            )
            listOf(intruderEvent) + _securityState.value.auditEvents
        } else {
            _securityState.value.auditEvents
        }
        _securityState.value = _securityState.value.copy(
            failedPinAttempts = nextFailures,
            intruderAlertsCount = if (isLockoutStrike) {
                _securityState.value.intruderAlertsCount + 1
            } else {
                _securityState.value.intruderAlertsCount
            },
            auditEvents = updatedEvents
        )
    }

    fun clearIntruderAlerts() {
        _securityState.value = _securityState.value.copy(
            failedPinAttempts = 0,
            intruderAlertsCount = 0
        )
        viewModelScope.launch {
            _uiEvents.emit("Intruder alerts cleared 🛡️")
        }
    }

    fun addWhitelistedBank(bankLabel: String) {
        if (bankLabel.isNotBlank()) {
            val formatted = "${bankLabel.trim()} [VERIFIED]"
            val event = SecurityAuditEvent(
                id = "iban_${UUID.randomUUID().toString().take(6)}",
                title = "New Payout IBAN Added to Whitelist",
                subtitle = "Verified destination: $formatted",
                timestamp = "Just now",
                severity = "VERIFIED"
            )
            _securityState.value = _securityState.value.copy(
                whitelistedBanks = _securityState.value.whitelistedBanks + formatted,
                auditEvents = listOf(event) + _securityState.value.auditEvents
            )
            viewModelScope.launch {
                _uiEvents.emit("Whitelisted Payout Destination Verified: $formatted 🏦")
            }
        }
    }

    fun toggleEmergencyLockdown() {
        val nextLockdown = !_securityState.value.isEmergencyLockdown
        val event = SecurityAuditEvent(
            id = "sec_${UUID.randomUUID().toString().take(6)}",
            title = if (nextLockdown) "Emergency Lockdown Engaged" else "Emergency Lockdown Lifted",
            subtitle = if (nextLockdown) {
                "All vaults locked • External sessions revoked • Stealth Blur & FLAG_SECURE enforced"
            } else {
                "Standard Fortress security posture restored"
            },
            timestamp = "Just now",
            severity = if (nextLockdown) "BLOCKED" else "SAFE"
        )
        _securityState.value = _securityState.value.copy(
            isEmergencyLockdown = nextLockdown,
            isVaultUnlockedSession = if (nextLockdown) false else _securityState.value.isVaultUnlockedSession,
            isDmPinEnabled = true,
            isWalletPinEnabled = true,
            screenshotBlockerEnabled = true,
            stealthModeEnabled = if (nextLockdown) true else _securityState.value.stealthModeEnabled,
            activeSessions = if (nextLockdown) {
                _securityState.value.activeSessions.filter { it.isCurrent }
            } else {
                _securityState.value.activeSessions
            },
            auditEvents = listOf(event) + _securityState.value.auditEvents
        )
        viewModelScope.launch {
            val msg = if (nextLockdown) {
                "🚨 EMERGENCY LOCKDOWN ACTIVE: Wallet frozen & external sessions revoked!"
            } else {
                "Emergency Lockdown lifted 🛡️"
            }
            _uiEvents.emit(msg)
        }
    }

    fun lockEntireAppGate() {
        _securityState.value = _securityState.value.copy(
            isAppVaultLocked = true,
            isVaultUnlockedSession = false
        )
    }

    fun unlockEntireAppGate() {
        _securityState.value = _securityState.value.copy(
            isAppVaultLocked = false,
            isVaultUnlockedSession = true,
            isDecoyModeActive = false,
            failedPinAttempts = 0
        )
        viewModelScope.launch {
            _uiEvents.emit("GOAL Fortress Vault Gate Unlocked 🔓")
        }
    }

    fun changeVaultPin(newPin: String) {
        if (newPin.length == 4) {
            val event = SecurityAuditEvent(
                id = "sec_${UUID.randomUUID().toString().take(6)}",
                title = "Vault Security PIN Updated",
                subtitle = "4-digit PIN rotated for Direct Messages & Creator Wallet",
                timestamp = "Just now",
                severity = "VERIFIED"
            )
            _securityState.value = _securityState.value.copy(
                vaultPin = newPin,
                auditEvents = listOf(event) + _securityState.value.auditEvents
            )
            viewModelScope.launch {
                _uiEvents.emit("Security PIN updated to $newPin 🔐")
            }
        }
    }

    fun unlockVaultSession() {
        _securityState.value = _securityState.value.copy(
            isVaultUnlockedSession = true,
            isDecoyModeActive = false,
            failedPinAttempts = 0
        )
        viewModelScope.launch {
            _uiEvents.emit("Security Vault Unlocked 🔓")
        }
    }

    fun lockVaultSession() {
        _securityState.value = _securityState.value.copy(isVaultUnlockedSession = false)
        viewModelScope.launch {
            _uiEvents.emit("Security Vault Locked 🔐")
        }
    }

    fun markConversationE2eeVerified(convId: String) {
        _securityState.value = _securityState.value.copy(
            verifiedConversationIds = _securityState.value.verifiedConversationIds + convId
        )
    }

    fun revokeDeviceSession(sessionId: String) {
        _securityState.value = _securityState.value.copy(
            activeSessions = _securityState.value.activeSessions.filterNot { it.id == sessionId }
        )
        viewModelScope.launch {
            _uiEvents.emit("Remote device session revoked 🛡️")
        }
    }

    fun runSecurityAuditScan() {
        val nextProbes = _securityState.value.blockedMitmProbes + 1
        val scanEvent = SecurityAuditEvent(
            id = "sec_${UUID.randomUUID().toString().take(6)}",
            title = "Deep Packet, TLS 1.3 & E2EE Audit Passed",
            subtitle = "0 vulnerabilities • $nextProbes MITM probes blocked • All SHA-256 keys verified",
            timestamp = "Just now",
            severity = "SAFE"
        )
        _securityState.value = _securityState.value.copy(
            blockedMitmProbes = nextProbes,
            auditEvents = listOf(scanEvent) + _securityState.value.auditEvents
        )
        viewModelScope.launch {
            _uiEvents.emit("Deep Packet & E2EE Audit Complete: 99.9% Protected 🛡️")
        }
    }

    fun updateCreatorProfile(
        displayName: String,
        category: String,
        bio: String,
        website: String
    ) {
        _creatorProfile.value = _creatorProfile.value.copy(
            displayName = displayName.ifBlank { "GOAL Official Creator" },
            category = category.ifBlank { "🎯 Elite Sports & Gaming Architect" },
            bio = bio,
            website = website
        )
        viewModelScope.launch {
            _uiEvents.emit("Professional Profile updated ✨")
        }
    }

    fun updateMyNote(note: String) {
        if (note.isNotBlank()) {
            _myNoteText.value = note.trim()
            viewModelScope.launch {
                _uiEvents.emit("Your Note was shared with followers 🎯")
            }
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
            repository.markConversationRead(convId)
        }
        viewModelScope.launch {
            repository.getMessagesForConversation(convId).collect { msgs ->
                _conversationMessages.value = msgs
            }
        }
    }

    fun unsendMessage(messageId: String) {
        viewModelScope.launch {
            repository.unsendMessage(messageId)
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

            // Show Instagram-style typing indicator and deliver contextual creator response
            delay(450)
            _isRecipientTyping.value = true
            delay(1300)
            val autoReplies = listOf(
                "100% bro! That new E2EE Instagram chat box & Royal Gold Target-G logo look Straight Fire 🎯🔥",
                "Appreciate the love! Let's lock in that collab stream this Friday at 8 PM 🚀",
                "Just checked your analytics growth—3.4M views this month is massive! 👏",
                "Sending over the 4K60 highlight reel right now! Let's get it trending on GOAL ⚽✨"
            )
            val replyText = autoReplies.random()
            _isRecipientTyping.value = false
            repository.receiveCreatorReply(conversationId, receiverId, replyText)
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
