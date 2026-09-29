package com.example

import android.os.Bundle
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.GoalBottomNav
import com.example.ui.components.TipDialog
import com.example.ui.screens.create.CreateStudioScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.live.LiveRoomScreen
import com.example.ui.screens.live.LiveStreamsScreen
import com.example.ui.screens.messages.ChatConversationScreen
import com.example.ui.screens.messages.MessagesListScreen
import com.example.ui.screens.player.VideoPlayerScreen
import com.example.ui.screens.profile.ProfileAndStudioScreen
import com.example.ui.screens.search.SearchScreen
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
            if (currentScreen is Screen.Main) {
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
            when (val screen = currentScreen) {
                is Screen.Main -> {
                    when (selectedTab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                videos = filteredStandardVideos,
                                liveStreams = liveStreams,
                                selectedCategory = selectedCategory,
                                unreadMessagesCount = unreadCount,
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
                                    if (conversations.isNotEmpty()) {
                                        viewModel.navigateTo(Screen.ChatConversation(conversations.first().id))
                                    }
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
                                creatorBalance = creatorBalance,
                                transactions = transactions,
                                onVideoClick = { video ->
                                    viewModel.navigateTo(Screen.VideoPlayer(video.id))
                                },
                                onLikeVideo = { viewModel.toggleLike(it) },
                                onSaveVideo = { viewModel.toggleSave(it) },
                                onRequestPayout = { amount ->
                                    viewModel.requestPayout(amount)
                                }
                            )
                        }
                    }
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

                is Screen.ChatConversation -> {
                    activeConversation?.let { conv ->
                        ChatConversationScreen(
                            conversation = conv,
                            messages = conversationMessages,
                            onSendMessage = { text ->
                                viewModel.sendMessage(conv.id, conv.otherUserId, text)
                            },
                            onBack = { viewModel.navigateBack() }
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

                else -> {}
            }
        }
    }
}
