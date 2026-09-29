package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.VideoEntity
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.GoalMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun GoalVideoCard(
    video: VideoEntity,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onTipClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("video_card_${video.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Thumbnail container (16:9)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(DarkSurfaceElevated)
            ) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Bottom gradient scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC000000)),
                                startY = 180f
                            )
                        )
                )

                // Category tag top-left
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xCC111520))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = video.category,
                        color = GoalGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Play icon overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0x77000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Duration badge bottom-right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xDD000000))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    val minutes = video.durationSeconds / 60
                    val seconds = video.durationSeconds % 60
                    Text(
                        text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Info Details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Creator Avatar
                AsyncImage(
                    model = video.userAvatar,
                    contentDescription = video.username,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Title and stats
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = video.username,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (video.isVerified) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Verified Creator",
                                tint = GoalGreen,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = "•",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = formatViews(video.viewsCount),
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // Actions: Like, Save, More
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("like_button_${video.id}")
                    ) {
                        Icon(
                            imageVector = if (video.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (video.isLiked) GoalMagenta else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onSaveClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("save_button_${video.id}")
                    ) {
                        Icon(
                            imageVector = if (video.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (video.isSaved) GoalGreen else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "More",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(DarkSurfaceElevated)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Tip / Super Chat Creator", color = TextPrimary) },
                                onClick = {
                                    menuExpanded = false
                                    onTipClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Share Link", color = TextPrimary) },
                                onClick = {
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatViews(count: Long): String {
    return when {
        count >= 1_000_000 -> String.format(Locale.getDefault(), "%.1fM views", count / 1_000_000.0)
        count >= 1_000 -> String.format(Locale.getDefault(), "%.1fK views", count / 1_000.0)
        else -> "$count views"
    }
}
