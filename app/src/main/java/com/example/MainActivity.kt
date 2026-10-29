package com.example

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.GoalBottomNav
import com.example.ui.screens.create.CreateStudioScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.live.LiveRoomScreen
import com.example.ui.screens.live.LiveStreamsScreen
import com.example.ui.screens.messages.ChatConversationScreen
import com.example.ui.screens.messages.MessagesListScreen
import com.example.ui.screens.player.VideoPlayerScreen
import com.example.ui.screens.profile.AnalyticsDashboardContent
import com.example.ui.screens.profile.ProfileAndStudioScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.security.AppSecurityVaultGateOverlay
import com.example.ui.screens.security.SecurityShieldScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GoalViewModel
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                GoalApp()
            }
        }
    }
}

@Composable
fun GoalApp(viewModel: GoalViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    val filteredStandardVideos by viewModel.filteredStandardVideos.collectAsStateWithLifecycle()
    val standardVideos by viewModel.standardVideos.collectAsStateWithLifecycle()
    val shortsVideos by viewModel.shortsVideos.collectAsStateWithLifecycle()
    val savedVideos by viewModel.savedVideos.collectAsStateWithLifecycle()
    val liveStreams by viewModel.liveStreams.collectAsStateWithLifecycle()
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val creatorBalance by viewModel.creatorBalance.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val creatorProfile by viewModel.creatorProfile.collectAsStateWithLifecycle()
    val securityState by viewModel.securityState.collectAsStateWithLifecycle()
    val myNoteText by viewModel.myNoteText.collectAsStateWithLifecycle()
    val isRecipientTyping by viewModel.isRecipientTyping.collectAsStateWithLifecycle()

    val activeVideo by viewModel.activeVideo.collectAsStateWithLifecycle()
    val activeComments by viewModel.activeComments.collectAsStateWithLifecycle()
    val activeLiveStream by viewModel.activeLiveStream.collectAsStateWithLifecycle()
    val activeConversation by viewModel.activeConversation.collectAsStateWithLifecycle()
    val conversationMessages by viewModel.conversationMessages.collectAsStateWithLifecycle()

    val isTranscoding by viewModel.isTranscoding.collectAsStateWithLifecycle()
    val transcodeSteps by viewModel.transcodeSteps.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Enforce Android Hardware Screenshot & Screen Recording Blocker (FLAG_SECURE)
    LaunchedEffect(securityState.screenshotBlockerEnabled, securityState.isEmergencyLockdown) {
        val activity = context as? Activity
        if (securityState.screenshotBlockerEnabled || securityState.isEmergencyLockdown) {
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val unreadCount = conversations.sumOf { it.unreadCount }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (currentScreen is Screen.Main && !securityState.isAppVaultLocked) {
                GoalBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = { viewModel.selectTab(it) },
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBg)
        ) {
            when (currentScreen) {
                is Screen.Main -> {
                    when (selectedTab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                videos = filteredStandardVideos,
                                liveStreams = liveStreams,
                                selectedCategory = selectedCategory,
                                unreadMessagesCount = unreadCount,
                                protectionScore = securityState.protectionScore,
                                isEmergencyLockdown = securityState.isEmergencyLockdown,
                                stealthModeEnabled = securityState.stealthModeEnabled,
                                screenshotBlockerEnabled = securityState.screenshotBlockerEnabled,
                                creatorDrmEnabled = securityState.creatorDrmEnabled,
                                onCategorySelected = { viewModel.selectCategory(it) },
                                onVideoClick = { video ->
                                    viewModel.navigateTo(Screen.VideoPlayer(video.id))
                                },
                                onLiveClick = { stream ->
                                    viewModel.navigateTo(Screen.LiveRoom(stream.id))
                                },
                                onSearchClick = {
                                    viewModel.navigateTo(Screen.Search)
                                },
                                onMessagesClick = {
                                    viewModel.navigateTo(Screen.MessagesInbox)
                                },
                                onOpenSecurityShield = {
                                    viewModel.navigateTo(Screen.SecurityShield)
                                },
                                onQuickLockApp = {
                                    viewModel.lockEntireAppGate()
                                },
                                onToggleStealthMode = {
                                    viewModel.toggleStealthMode()
                                },
                                onLikeVideo = { viewModel.toggleLike(it) },
                                onSaveVideo = { viewModel.toggleSave(it) },
                                onTipVideo = { viewModel.sendTip(it.username, 5.0) }
                            )
                        }

                        MainTab.SHORTS -> {
                            com.example.ui.screens.shorts.ShortsScreen(
                                shorts = shortsVideos,
                                comments = activeComments,
                                onLikeShort = { viewModel.toggleLike(it) },
                                onSaveShort = { viewModel.toggleSave(it) },
                                onAddComment = { videoId, text ->
                                    viewModel.addComment(videoId, text)
                                },
                                onToggleCommentLike = { viewModel.toggleCommentLike(it) },
                                onShareShort = {}
                            )
                        }

                        MainTab.CREATE -> {
                            CreateStudioScreen(
                                isTranscoding = isTranscoding,
                                transcodeSteps = transcodeSteps,
                                onUploadVideo = { title, desc, cat, isShort ->
                                    viewModel.startUploadWithTranscode(title, desc, cat, isShort)
                                },
                                onStartLiveStream = { title, cat ->
                                    viewModel.scheduleLiveStream(title, cat)
                                }
                            )
                        }

                        MainTab.LIVE -> {
                            LiveStreamsScreen(
                                liveStreams = liveStreams,
                                onSelectStream = { stream ->
                                    viewModel.navigateTo(Screen.LiveRoom(stream.id))
                                },
                                onStartBroadcasting = {
                                    viewModel.selectTab(MainTab.CREATE)
                                }
                            )
                        }

                        MainTab.PROFILE -> {
                            ProfileAndStudioScreen(
                                videos = standardVideos,
                                savedVideos = savedVideos,
                                shortsVideos = shortsVideos,
                                creatorBalance = creatorBalance,
                                transactions = transactions,
                                creatorProfile = creatorProfile,
                                vaultPin = securityState.vaultPin,
                                decoyPin = securityState.decoyPin,
                                isDecoyModeActive = securityState.isDecoyModeActive,
                                whitelistedBanks = securityState.whitelistedBanks,
                                onAddWhitelistedBank = { viewModel.addWhitelistedBank(it) },
                                creatorDrmEnabled = securityState.creatorDrmEnabled,
                                isWalletPinEnabled = securityState.isWalletPinEnabled,
                                isVaultUnlocked = securityState.isVaultUnlockedSession,
                                stealthModeEnabled = securityState.stealthModeEnabled,
                                onToggleStealthMode = { viewModel.toggleStealthMode() },
                                onUnlockVault = { viewModel.unlockVaultSession() },
                                onDecoyUnlock = { viewModel.activateDecoyVaultUnlock() },
                                onIntruderAttempt = { viewModel.recordIntruderPinAttempt() },
                                onOpenSecurityShield = {
                                    viewModel.navigateTo(Screen.SecurityShield)
                                },
                                onVideoClick = { video ->
                                    viewModel.navigateTo(Screen.VideoPlayer(video.id))
                                },
                                onLikeVideo = { viewModel.toggleLike(it) },
                                onSaveVideo = { viewModel.toggleSave(it) },
                                onRequestPayout = { amount ->
                                    viewModel.requestPayout(amount)
                                },
                                onOpenSeparateAnalytics = {
                                    viewModel.navigateTo(Screen.CreatorStudioDashboard)
                                },
                                onEditProfile = { name, cat, bio, web ->
                                    viewModel.updateCreatorProfile(name, cat, bio, web)
                                }
                            )
                        }
                    }
                }

                is Screen.MessagesInbox -> {
                    MessagesListScreen(
                        conversations = conversations,
                        myNoteText = myNoteText,
                        onUpdateMyNote = { viewModel.updateMyNote(it) },
                        vaultPin = securityState.vaultPin,
                        decoyPin = securityState.decoyPin,
                        isDecoyModeActive = securityState.isDecoyModeActive,
                        isDmPinEnabled = securityState.isDmPinEnabled,
                        isVaultUnlocked = securityState.isVaultUnlockedSession,
                        onUnlockVault = { viewModel.unlockVaultSession() },
                        onDecoyUnlock = { viewModel.activateDecoyVaultUnlock() },
                        onIntruderAttempt = { viewModel.recordIntruderPinAttempt() },
                        onOpenSecurityShield = {
                            viewModel.navigateTo(Screen.SecurityShield)
                        },
                        onSelectConversation = { conv ->
                            viewModel.navigateTo(Screen.ChatConversation(conv.id))
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.ChatConversation -> {
                    activeConversation?.let { conv ->
                        ChatConversationScreen(
                            conversation = conv,
                            messages = conversationMessages,
                            isRecipientTyping = isRecipientTyping,
                            isE2eeVerified = securityState.verifiedConversationIds.contains(conv.id),
                            onVerifyE2eeKeys = {
                                viewModel.markConversationE2eeVerified(conv.id)
                            },
                            onUnsendMessage = { msgId ->
                                viewModel.unsendMessage(msgId)
                            },
                            onSendMessage = { text ->
                                viewModel.sendMessage(conv.id, conv.otherUserId, text)
                            },
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                }

                is Screen.CreatorStudioDashboard -> {
                    AnalyticsDashboardContent(
                        creatorBalance = creatorBalance,
                        transactions = transactions,
                        onRequestPayout = { amount ->
                            viewModel.requestPayout(amount)
                        },
                        vaultPin = securityState.vaultPin,
                        decoyPin = securityState.decoyPin,
                        isDecoyModeActive = securityState.isDecoyModeActive,
                        whitelistedBanks = securityState.whitelistedBanks,
                        onAddWhitelistedBank = { viewModel.addWhitelistedBank(it) },
                        isWalletPinEnabled = securityState.isWalletPinEnabled,
                        isVaultUnlocked = securityState.isVaultUnlockedSession,
                        stealthModeEnabled = securityState.stealthModeEnabled,
                        onToggleStealthMode = { viewModel.toggleStealthMode() },
                        onUnlockVault = { viewModel.unlockVaultSession() },
                        onDecoyUnlock = { viewModel.activateDecoyVaultUnlock() },
                        onIntruderAttempt = { viewModel.recordIntruderPinAttempt() },
                        onOpenSecurityShield = {
                            viewModel.navigateTo(Screen.SecurityShield)
                        },
                        showStandaloneTopBar = true,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.SecurityShield -> {
                    SecurityShieldScreen(
                        securityState = securityState,
                        onToggleDmPin = { viewModel.toggleDmPin(it) },
                        onToggleWalletPin = { viewModel.toggleWalletPin(it) },
                        onToggleTwoFactor = { viewModel.toggleTwoFactor(it) },
                        onToggleAntiPhishing = { viewModel.toggleAntiPhishing(it) },
                        onToggleScreenshotBlocker = { viewModel.toggleScreenshotBlocker(it) },
                        onToggleStealthMode = { viewModel.toggleStealthMode(it) },
                        onToggleCreatorDrm = { viewModel.toggleCreatorDrm(it) },
                        onToggleFirewall = { viewModel.toggleNetworkFirewall(it) },
                        onSelectAutoLockPolicy = { viewModel.setAutoLockPolicy(it) },
                        onToggleDecoyMode = { viewModel.toggleDecoyMode(it) },
                        onUpdateDecoyPin = { viewModel.updateDecoyPin(it) },
                        onClearIntruderAlerts = { viewModel.clearIntruderAlerts() },
                        onToggleEmergencyLockdown = { viewModel.toggleEmergencyLockdown() },
                        onLockAppGateNow = { viewModel.lockEntireAppGate() },
                        onChangeVaultPin = { viewModel.changeVaultPin(it) },
                        onLockVaultNow = { viewModel.lockVaultSession() },
                        onRevokeSession = { viewModel.revokeDeviceSession(it) },
                        onRunSecurityScan = { viewModel.runSecurityAuditScan() },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.VideoPlayer -> {
                    activeVideo?.let { video ->
                        VideoPlayerScreen(
                            video = video,
                            comments = activeComments,
                            recommendedVideos = standardVideos,
                            onBack = { viewModel.navigateBack() },
                            onLikeVideo = { viewModel.toggleLike(it) },
                            onSaveVideo = { viewModel.toggleSave(it) },
                            onAddComment = { text ->
                                viewModel.addComment(video.id, text)
                            },
                            onToggleCommentLike = { viewModel.toggleCommentLike(it) },
                            onSendTip = { amount, _ ->
                                viewModel.sendTip(video.username, amount)
                            },
                            onSelectRecommended = { rec ->
                                viewModel.loadVideo(rec.id)
                            }
                        )
                    }
                }

                is Screen.LiveRoom -> {
                    activeLiveStream?.let { stream ->
                        LiveRoomScreen(
                            stream = stream,
                            onBack = { viewModel.navigateBack() },
                            onSendTip = { amount, _ ->
                                viewModel.sendTip(stream.username, amount)
                            }
                        )
                    }
                }

                is Screen.Search -> {
                    SearchScreen(
                        searchQuery = searchQuery,
                        searchResults = searchResults,
                        onQueryChange = { viewModel.setSearchQuery(it) },
                        onVideoClick = { video ->
                            viewModel.navigateTo(Screen.VideoPlayer(video.id))
                        },
                        onLikeVideo = { viewModel.toggleLike(it) },
                        onSaveVideo = { viewModel.toggleSave(it) },
                        onBack = { viewModel.navigateBack() }
                    )
                }
            }

            // Full-Screen Royal Gold & Obsidian App Security Vault Gate Overlay
            if (securityState.isAppVaultLocked) {
                AppSecurityVaultGateOverlay(
                    expectedPin = securityState.vaultPin,
                    decoyPin = securityState.decoyPin,
                    onUnlockSuccess = { viewModel.unlockEntireAppGate() },
                    onDecoyUnlock = { viewModel.activateDecoyVaultUnlock() },
                    onIntruderAttempt = { viewModel.recordIntruderPinAttempt() }
                )
            }
        }
    }
}
