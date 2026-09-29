package com.example.ui.screens.player

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.CommentEntity
import com.example.data.local.entity.VideoEntity
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.GoalVideoCard
import com.example.ui.components.TipDialog
import com.example.ui.components.formatViews
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoalAmber
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.GoalMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    video: VideoEntity,
    comments: List<CommentEntity>,
    recommendedVideos: List<VideoEntity>,
    onBack: () -> Unit,
    onLikeVideo: (VideoEntity) -> Unit,
    onSaveVideo: (VideoEntity) -> Unit,
    onAddComment: (String) -> Unit,
    onToggleCommentLike: (CommentEntity) -> Unit,
    onSendTip: (Double, String) -> Unit,
    onSelectRecommended: (VideoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var isPlaying by remember { mutableStateOf(true) }
    var currentSeconds by remember { mutableIntStateOf(34) }
    var sliderPosition by remember { mutableFloatStateOf(34f) }
    var showControls by remember { mutableStateOf(true) }
    var showQualityMenu by remember { mutableStateOf(false) }
    var selectedQuality by remember { mutableStateOf("1080p60") }
    var isSubscribed by remember { mutableStateOf(false) }
    var showTipDialog by remember { mutableStateOf(false) }
    var showCommentsSheet by remember { mutableStateOf(false) }
    var isDisliked by remember { mutableStateOf(false) }
    var descriptionExpanded by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Playback timer simulation
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000)
            if (currentSeconds < video.durationSeconds) {
                currentSeconds++
                sliderPosition = currentSeconds.toFloat()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Player Container (16:9)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
                    .clickable { showControls = !showControls }
                    .testTag("video_player_viewport")
            ) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Player Controls Overlay
                if (showControls) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x99000000))
                    ) {
                        // Top bar inside player
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.testTag("player_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box {
                                    IconButton(onClick = { showQualityMenu = true }) {
                                        Icon(
                                            imageVector = Icons.Filled.Settings,
                                            contentDescription = "Quality Settings",
                                            tint = Color.White
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = showQualityMenu,
                                        onDismissRequest = { showQualityMenu = false },
                                        modifier = Modifier.background(DarkSurfaceElevated)
                                    ) {
                                        listOf("1080p60 HD", "720p60 HD", "480p", "360p", "Auto").forEach { q ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = q,
                                                        color = if (selectedQuality.startsWith(q.take(4))) GoalGreen else TextPrimary,
                                                        fontWeight = if (selectedQuality.startsWith(q.take(4))) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                },
                                                onClick = {
                                                    selectedQuality = q
                                                    showQualityMenu = false
                                                }
                                            )
                                        }
                                    }
                                }

                                IconButton(onClick = {}) {
                                    Icon(
                                        imageVector = Icons.Filled.Fullscreen,
                                        contentDescription = "Fullscreen",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        // Center Playback Buttons (-10s, Play/Pause, +10s)
                        Row(
                            modifier = Modifier.align(Alignment.Center),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(28.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    currentSeconds = (currentSeconds - 10).coerceAtLeast(0)
                                    sliderPosition = currentSeconds.toFloat()
                                },
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Replay10,
                                    contentDescription = "Rewind 10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(34.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(GoalGreen)
                                    .clickable { isPlaying = !isPlaying }
                                    .testTag("player_play_pause_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = DarkBg,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    currentSeconds = (currentSeconds + 10).coerceAtMost(video.durationSeconds)
                                    sliderPosition = currentSeconds.toFloat()
                                },
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Forward10,
                                    contentDescription = "Forward 10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }

                        // Bottom scrubber & time
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(start = 12.dp, end = 12.dp, bottom = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val curMin = currentSeconds / 60
                                val curSec = currentSeconds % 60
                                val totMin = video.durationSeconds / 60
                                val totSec = video.durationSeconds % 60
                                Text(
                                    text = String.format(Locale.getDefault(), "%02d:%02d / %02d:%02d • %s", curMin, curSec, totMin, totSec, selectedQuality),
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }

                            Slider(
                                value = sliderPosition,
                                onValueChange = {
                                    sliderPosition = it
                                    currentSeconds = it.toInt()
                                },
                                valueRange = 0f..video.durationSeconds.toFloat(),
                                colors = SliderDefaults.colors(
                                    thumbColor = GoalGreen,
                                    activeTrackColor = GoalGreen,
                                    inactiveTrackColor = Color(0x66FFFFFF)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Scrollable Content Below Player
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Video Title
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = video.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = formatViews(video.viewsCount),
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(text = "•", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = "6 hours ago",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(text = "•", fontSize = 12.sp, color = TextSecondary)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(DarkSurfaceElevated)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = video.category,
                                fontSize = 11.sp,
                                color = GoalGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Creator Channel Bar
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = video.userAvatar,
                            contentDescription = video.username,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceElevated),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = video.username,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                if (video.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = GoalGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = "1.28M subscribers",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = { isSubscribed = !isSubscribed },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSubscribed) DarkSurfaceElevated else GoalGreen
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("subscribe_button")
                        ) {
                            Text(
                                text = if (isSubscribed) "Subscribed" else "Subscribe",
                                color = if (isSubscribed) TextSecondary else DarkBg,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Interactive Action Bar (Like, Dislike, Share, Save, Tip)
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Like Button
                        ActionPill(
                            icon = if (video.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            label = "${video.likesCount}",
                            tint = if (video.isLiked) GoalMagenta else TextPrimary,
                            onClick = { onLikeVideo(video) },
                            testTag = "player_like_action"
                        )

                        // Dislike Button
                        ActionPill(
                            icon = if (isDisliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                            label = "Dislike",
                            tint = if (isDisliked) TextPrimary else TextSecondary,
                            onClick = { isDisliked = !isDisliked },
                            testTag = "player_dislike_action"
                        )

                        // Share
                        ActionPill(
                            icon = Icons.Filled.Share,
                            label = "Share",
                            tint = TextPrimary,
                            onClick = {},
                            testTag = "player_share_action"
                        )

                        // Save
                        ActionPill(
                            icon = if (video.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            label = if (video.isSaved) "Saved" else "Save",
                            tint = if (video.isSaved) GoalGreen else TextPrimary,
                            onClick = { onSaveVideo(video) },
                            testTag = "player_save_action"
                        )

                        // Tip / Super Chat
                        ActionPill(
                            icon = Icons.Filled.MonetizationOn,
                            label = "Tip",
                            tint = GoalAmber,
                            onClick = { showTipDialog = true },
                            testTag = "player_tip_action"
                        )
                    }
                }

                // Description Box
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceElevated)
                            .clickable { descriptionExpanded = !descriptionExpanded }
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Description",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = video.description,
                                fontSize = 13.sp,
                                color = TextPrimary,
                                maxLines = if (descriptionExpanded) 20 else 2,
                                lineHeight = 18.sp
                            )
                            Text(
                                text = if (descriptionExpanded) "Show less" else "...more",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoalGreen
                            )
                        }
                    }
                }

                // Comments Snippet (tappable to open sheet)
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                            .clickable { showCommentsSheet = true }
                            .padding(12.dp)
                            .testTag("comments_preview_container")
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Comments • ${comments.size}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "View all",
                                    color = GoalGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            if (comments.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                val firstComment = comments.first()
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = firstComment.userAvatar,
                                        contentDescription = firstComment.username,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = firstComment.content,
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                // Up Next Header
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Up Next",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Recommended Videos
                items(recommendedVideos.filter { it.id != video.id }, key = { it.id }) { recVideo ->
                    GoalVideoCard(
                        video = recVideo,
                        onClick = { onSelectRecommended(recVideo) },
                        onLikeClick = { onLikeVideo(recVideo) },
                        onSaveClick = { onSaveVideo(recVideo) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Tip Modal
        if (showTipDialog) {
            TipDialog(
                creatorName = video.username,
                onDismiss = { showTipDialog = false },
                onSendTip = { amount, message ->
                    onSendTip(amount, message)
                }
            )
        }

        // Comments Bottom Sheet
        if (showCommentsSheet) {
            CommentsBottomSheet(
                comments = comments,
                sheetState = sheetState,
                onDismiss = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showCommentsSheet = false
                    }
                },
                onAddComment = onAddComment,
                onToggleLike = onToggleCommentLike
            )
        }
    }
}

@Composable
fun ActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurfaceElevated)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 7.dp)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(17.dp)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
    }
}
