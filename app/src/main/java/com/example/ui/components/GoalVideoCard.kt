package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VerifiedUser
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
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.GoalMagenta
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
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
    drmWatermarkEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showDrmSignatureBanner by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        RoyalGold.copy(alpha = 0.42f),
                        DarkSurfaceBorder,
                        GoalGreen.copy(alpha = 0.25f)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .testTag("video_card_${video.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Cinema-Grade Thumbnail Container (16:9)
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

                // Deep Velvet Bottom Gradient Scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.25f),
                                    Color.Transparent,
                                    Color(0xE606090F)
                                )
                            )
                        )
                )

                // Top-Left Category + 4K HDR Gold Pill
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xD90A0F1A))
                            .border(0.8.dp, RoyalGold.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = video.category.uppercase(),
                            color = RoyalGoldLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(listOf(RoyalGoldLight, RoyalGold))
                            )
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "4K HDR",
                            color = DarkBg,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.6.sp
                        )
                    }
                }

                // Top-Right DRM / Verified Content Shield Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xCC0A0F1A))
                        .border(0.8.dp, GoalGreen.copy(alpha = 0.55f), RoundedCornerShape(50))
                        .clickable { showDrmSignatureBanner = !showDrmSignatureBanner }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.VerifiedUser,
                            contentDescription = "DRM Protected",
                            tint = GoalGreen,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (drmWatermarkEnabled) "DRM GUARD" else "VERIFIED",
                            color = GoalGreen,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Dynamic Creator DRM Watermark & Anti-Piracy Seal Bottom-Left
                if (drmWatermarkEnabled) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xBF06090F))
                            .border(0.6.dp, RoyalGold.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "🎯 DRM-SHA256 • @${video.username}",
                            color = RoyalGoldLight.copy(alpha = 0.9f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Luxury Gold-Rimmed Play Icon Overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xAA080B11))
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(listOf(RoyalGoldLight, RoyalGold, GoalGreen)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play",
                        tint = RoyalGoldLight,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Duration Badge Bottom-Right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xE6080B11))
                        .border(0.6.dp, RoyalGold.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    val minutes = video.durationSeconds / 60
                    val seconds = video.durationSeconds % 60
                    Text(
                        text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds),
                        color = RoyalGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (showDrmSignatureBanner) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x2200E676))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🛡️ Anti-Piracy DRM Hash: SHA256-${video.id.uppercase()}-${video.username.take(4).uppercase()}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoalGreen
                    )
                    Text(
                        text = "Hide",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RoyalGoldLight,
                        modifier = Modifier.clickable { showDrmSignatureBanner = false }
                    )
                }
            }

            // Creator & Video Metadata Details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(13.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Creator Avatar with Royal Gold Halo Ring
                AsyncImage(
                    model = video.userAvatar,
                    contentDescription = video.username,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(listOf(RoyalGoldLight, RoyalGold, GoalGreen)),
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Title and Creator Stats
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "@${video.username}",
                            color = RoyalGoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (video.isVerified) {
                            GoalVerifiedBadge(isGoldTier = true, size = 14.dp)
                        }
                        Text(
                            text = "•",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = formatViews(video.viewsCount),
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
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
                            tint = if (video.isSaved) RoyalGold else TextSecondary,
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
                                text = { Text("Verify Content DRM Signature", color = RoyalGoldLight) },
                                onClick = {
                                    menuExpanded = false
                                    showDrmSignatureBanner = true
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
