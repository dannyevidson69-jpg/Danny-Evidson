package com.example.ui.screens.shorts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.components.formatViews
import com.example.ui.theme.DarkBg
import com.example.ui.theme.GoalCyan
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.GoalMagenta
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortsScreen(
    shorts: List<VideoEntity>,
    comments: List<CommentEntity>,
    onLikeShort: (VideoEntity) -> Unit,
    onSaveShort: (VideoEntity) -> Unit,
    onAddComment: (String, String) -> Unit,
    onToggleCommentLike: (CommentEntity) -> Unit,
    onShareShort: (VideoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (shorts.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(DarkBg),
            contentAlignment = Alignment.Center
        ) {
            Text("No GOAL Shorts available right now.", color = Color.White)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { shorts.size })
    var showCommentsSheet by remember { mutableStateOf(false) }
    var activeShortForComments by remember { mutableStateOf<VideoEntity?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("shorts_vertical_pager")
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val short = shorts[page]
            ShortItemPage(
                short = short,
                isCurrentPage = pagerState.currentPage == page,
                onLikeClick = { onLikeShort(short) },
                onSaveClick = { onSaveShort(short) },
                onCommentsClick = {
                    activeShortForComments = short
                    showCommentsSheet = true
                },
                onShareClick = { onShareShort(short) }
            )
        }

        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Shorts",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x66000000))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Subscribed",
                        color = GoalGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Comments Bottom Sheet
        if (showCommentsSheet && activeShortForComments != null) {
            CommentsBottomSheet(
                comments = comments,
                sheetState = sheetState,
                onDismiss = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showCommentsSheet = false
                    }
                },
                onAddComment = { content ->
                    onAddComment(activeShortForComments!!.id, content)
                },
                onToggleLike = onToggleCommentLike
            )
        }
    }
}

@Composable
fun ShortItemPage(
    short: VideoEntity,
    isCurrentPage: Boolean,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCommentsClick: () -> Unit,
    onShareClick: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var playbackProgress by remember { mutableFloatStateOf(0.15f) }
    val rotationAngle = remember { Animatable(0f) }
    var isFollowing by remember { mutableStateOf(false) }

    // Simulated playback loop & audio disc rotation
    LaunchedEffect(isCurrentPage, isPlaying) {
        if (isCurrentPage && isPlaying) {
            launch {
                rotationAngle.animateTo(
                    targetValue = rotationAngle.value + 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(4000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
            while (isPlaying) {
                delay(300)
                playbackProgress = (playbackProgress + 0.02f)
                if (playbackProgress >= 1f) playbackProgress = 0f
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPlaying = !isPlaying
            }
    ) {
        // Thumbnail & Simulated Video Feed
        AsyncImage(
            model = short.thumbnailUrl,
            contentDescription = short.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient Scrim for text legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x55000000),
                            Color.Transparent,
                            Color(0xCC000000)
                        ),
                        startY = 0f,
                        endY = 1800f
                    )
                )
        )

        // Pause/Play indicator if paused
        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0x77000000)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Paused",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        // Right Action Bar (Favorite, Comments, Share, Bookmark, Audio Disc)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Like
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onLikeClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0x44000000))
                        .testTag("short_like_${short.id}")
                ) {
                    Icon(
                        imageVector = if (short.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (short.isLiked) GoalMagenta else Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatShortCount(short.likesCount),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Comments
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onCommentsClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0x44000000))
                        .testTag("short_comments_${short.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Comment,
                        contentDescription = "Comments",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${short.commentsCount}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Bookmark / Save
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0x44000000))
                        .testTag("short_bookmark_${short.id}")
                ) {
                    Icon(
                        imageVector = if (short.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (short.isSaved) GoalGreen else Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Save",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Share
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0x44000000))
                        .testTag("short_share_${short.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Share",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Spinning Audio Disc
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .rotate(rotationAngle.value)
                    .clip(CircleShape)
                    .background(Color(0xFF1E2333))
                    .border(2.dp, GoalGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = "Music",
                    tint = GoalCyan,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Creator Profile & Caption Overlay (Bottom-Left)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .padding(start = 16.dp, bottom = 36.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Creator info & follow button
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = short.userAvatar,
                    contentDescription = short.username,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, GoalGreen, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "@${short.username}",
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isFollowing) Color(0x55FFFFFF) else GoalGreen)
                        .clickable { isFollowing = !isFollowing }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isFollowing) "Following" else "Follow",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFollowing) Color.White else DarkBg
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Caption
            Text(
                text = short.title,
                style = androidx.compose.ui.text.TextStyle(
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Hashtags
            Text(
                text = short.hashtags,
                style = androidx.compose.ui.text.TextStyle(
                    color = GoalCyan,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sound Label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x44000000))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = "Audio track",
                    tint = GoalGreen,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = short.audioTrackTitle,
                    color = Color.White,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }

        // Bottom Seek / Progress Bar
        LinearProgressIndicator(
            progress = { playbackProgress },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(3.dp),
            color = GoalGreen,
            trackColor = Color(0x33FFFFFF)
        )
    }
}

fun formatShortCount(count: Long): String {
    return when {
        count >= 1_000_000 -> String.format(Locale.getDefault(), "%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format(Locale.getDefault(), "%.1fK", count / 1_000.0)
        else -> "$count"
    }
}
