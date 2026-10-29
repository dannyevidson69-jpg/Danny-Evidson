package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.data.local.entity.LiveStreamEntity
import com.example.data.local.entity.VideoEntity
import com.example.ui.components.GoalProPillBadge
import com.example.ui.components.GoalTopAppBar
import com.example.ui.components.GoalVerifiedBadge
import com.example.ui.components.GoalVideoCard
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoalCyan
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.InstaPink
import com.example.ui.theme.InstaPurple
import com.example.ui.theme.LiveRed
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

private data class VipCreatorBadgeItem(
    val username: String,
    val badgeLabel: String,
    val avatarUrl: String,
    val isLive: Boolean
)

@Composable
fun HomeScreen(
    videos: List<VideoEntity>,
    liveStreams: List<LiveStreamEntity>,
    selectedCategory: String,
    unreadMessagesCount: Int,
    protectionScore: Float = 98.0f,
    isEmergencyLockdown: Boolean = false,
    stealthModeEnabled: Boolean = false,
    screenshotBlockerEnabled: Boolean = true,
    creatorDrmEnabled: Boolean = true,
    onCategorySelected: (String) -> Unit,
    onVideoClick: (VideoEntity) -> Unit,
    onLiveClick: (LiveStreamEntity) -> Unit,
    onSearchClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onOpenSecurityShield: () -> Unit = {},
    onQuickLockApp: () -> Unit = {},
    onToggleStealthMode: () -> Unit = {},
    onLikeVideo: (VideoEntity) -> Unit,
    onSaveVideo: (VideoEntity) -> Unit,
    onTipVideo: (VideoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Football", "Gaming", "Tactics", "Esports", "Skills", "Creator")
    val featuredLive = liveStreams.firstOrNull { it.status == "live" }

    val vipCreators = remember {
        listOf(
            VipCreatorBadgeItem(
                "ApexStriker",
                "VIP PRO",
                "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300",
                true
            ),
            VipCreatorBadgeItem(
                "TacticalMind",
                "GOLD TIER",
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
                false
            ),
            VipCreatorBadgeItem(
                "NeonEsports",
                "4K ARENA",
                "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300",
                true
            ),
            VipCreatorBadgeItem(
                "BellaVlogs",
                "CREATOR+",
                "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=300",
                false
            ),
            VipCreatorBadgeItem(
                "GoalMaster",
                "FOUNDER",
                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300",
                false
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Luxury Royal Gold & Fortress Security App Bar
        GoalTopAppBar(
            unreadMessagesCount = unreadMessagesCount,
            protectionScore = protectionScore,
            isEmergencyLockdown = isEmergencyLockdown,
            onSearchClick = onSearchClick,
            onMessagesClick = onMessagesClick,
            onSecurityShieldClick = onOpenSecurityShield,
            onQuickLockAppClick = onQuickLockApp
        )

        // Category Filter Chips with Brushed Royal Gold Active State
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = category.equals(selectedCategory, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) {
                                Brush.linearGradient(listOf(RoyalGoldLight, RoyalGold))
                            } else {
                                Brush.linearGradient(listOf(DarkSurfaceElevated, DarkSurfaceElevated))
                            }
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) RoyalGoldLight else DarkSurfaceBorder,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onCategorySelected(category) }
                        .padding(horizontal = 15.dp, vertical = 7.dp)
                        .testTag("category_chip_$category")
                ) {
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                        color = if (isSelected) DarkBg else TextPrimary
                    )
                }
            }
        }

        // Feed content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_video_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Luxury Security Status & VIP Creator Lounge Header
            if (selectedCategory == "All") {
                item {
                    LuxurySecurityAndVipHeader(
                        protectionScore = protectionScore,
                        isEmergencyLockdown = isEmergencyLockdown,
                        stealthModeEnabled = stealthModeEnabled,
                        screenshotBlockerEnabled = screenshotBlockerEnabled,
                        vipCreators = vipCreators,
                        onOpenSecurityShield = onOpenSecurityShield,
                        onToggleStealthMode = onToggleStealthMode,
                        onSelectCreatorChat = onMessagesClick
                    )
                }
            }

            // Featured Hero Live Stream Banner
            if (featuredLive != null && selectedCategory == "All") {
                item {
                    FeaturedLiveHeroCard(
                        liveStream = featuredLive,
                        onClick = { onLiveClick(featuredLive) }
                    )
                }
            }

            // Standard Videos
            items(videos, key = { it.id }) { video ->
                GoalVideoCard(
                    video = video,
                    onClick = { onVideoClick(video) },
                    onLikeClick = { onLikeVideo(video) },
                    onSaveClick = { onSaveVideo(video) },
                    onTipClick = { onTipVideo(video) },
                    drmWatermarkEnabled = creatorDrmEnabled
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun LuxurySecurityAndVipHeader(
    protectionScore: Float,
    isEmergencyLockdown: Boolean,
    stealthModeEnabled: Boolean,
    screenshotBlockerEnabled: Boolean,
    vipCreators: List<VipCreatorBadgeItem>,
    onOpenSecurityShield: () -> Unit,
    onToggleStealthMode: () -> Unit,
    onSelectCreatorChat: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Fortress Security Status Bar Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        if (isEmergencyLockdown) listOf(LiveRed, RoyalGold)
                        else listOf(RoyalGoldLight, RoyalGold, GoalGreen)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onOpenSecurityShield() }
                .testTag("home_security_lounge_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(RoyalGold.copy(alpha = 0.28f), GoalGreen.copy(alpha = 0.25f))
                            )
                        )
                        .border(1.dp, RoyalGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = RoyalGoldLight,
                        modifier = Modifier.size(19.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isEmergencyLockdown) {
                                "🚨 Fortress Lockdown Active"
                            } else {
                                "🛡️ GOAL Fortress Shield • ${protectionScore}%"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        if (screenshotBlockerEnabled) {
                            Icon(
                                imageVector = Icons.Filled.NoPhotography,
                                contentDescription = "Screenshot Blocker On",
                                tint = GoalGreen,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                    Text(
                        text = "SHA-256 E2EE DMs • Wallet PIN Guard • Anti-Phishing Active",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Quick Stealth Mode Pill Toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (stealthModeEnabled) GoalCyan.copy(alpha = 0.22f)
                            else DarkSurfaceElevated
                        )
                        .border(
                            width = 1.dp,
                            color = if (stealthModeEnabled) GoalCyan else RoyalGold.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(50)
                        )
                        .clickable { onToggleStealthMode() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("home_stealth_toggle"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (stealthModeEnabled) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Stealth Mode",
                            tint = if (stealthModeEnabled) GoalCyan else RoyalGoldLight,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (stealthModeEnabled) "Stealth ON" else "Stealth",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (stealthModeEnabled) GoalCyan else RoyalGoldLight
                        )
                    }
                }
            }
        }

        // VIP Verified Creators Strip
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(vipCreators, key = { it.username }) { vip ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(74.dp)
                        .clickable { onSelectCreatorChat() }
                ) {
                    Box(contentAlignment = Alignment.BottomCenter) {
                        Box(
                            modifier = Modifier
                                .size(62.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        if (vip.isLive) {
                                            listOf(RoyalGoldLight, InstaPink, InstaPurple)
                                        } else {
                                            listOf(RoyalGoldLight, RoyalGold, GoalGreen)
                                        }
                                    )
                                )
                                .padding(2.2.dp)
                        ) {
                            AsyncImage(
                                model = vip.avatarUrl,
                                contentDescription = vip.username,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(DarkSurfaceElevated),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (vip.isLive) LiveRed else DarkSurfaceElevated
                                )
                                .border(0.8.dp, RoyalGold, RoundedCornerShape(6.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = vip.badgeLabel,
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Black,
                                color = if (vip.isLive) Color.White else RoyalGoldLight
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = vip.username,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        GoalVerifiedBadge(isGoldTier = true, size = 11.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedLiveHeroCard(
    liveStream: LiveStreamEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(listOf(RoyalGoldLight, RoyalGold, LiveRed)),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .testTag("featured_hero_banner"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            AsyncImage(
                model = liveStream.bannerUrl,
                contentDescription = liveStream.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Cinema Vignette Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Transparent,
                                Color(0xEB06090F)
                            )
                        )
                    )
            )

            // Live Pill, Royal VIP Arena Badge & Viewer Counter
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LiveRed)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "● LIVE ARENA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.8.sp
                    )
                }

                GoalProPillBadge(text = "VIP 4K STREAM")

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xCC080B11))
                        .border(0.6.dp, RoyalGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${liveStream.viewerCount / 1000}K watching",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalGoldLight
                    )
                }
            }

            // Bottom Title & Verified Creator
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = liveStream.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "@${liveStream.username}",
                        fontSize = 12.sp,
                        color = RoyalGoldLight,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    GoalVerifiedBadge(isGoldTier = true, size = 13.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${liveStream.category} • E2EE Chat Protected",
                        fontSize = 11.sp,
                        color = GoalGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
