package com.example.ui.screens.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.LiveStreamEntity
import com.example.data.local.entity.VideoEntity
import com.example.ui.components.GoalTopAppBar
import com.example.ui.components.GoalVideoCard
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoalCyan
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    videos: List<VideoEntity>,
    liveStreams: List<LiveStreamEntity>,
    selectedCategory: String,
    unreadMessagesCount: Int,
    onCategorySelected: (String) -> Unit,
    onVideoClick: (VideoEntity) -> Unit,
    onLiveClick: (LiveStreamEntity) -> Unit,
    onSearchClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onLikeVideo: (VideoEntity) -> Unit,
    onSaveVideo: (VideoEntity) -> Unit,
    onTipVideo: (VideoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Football", "Gaming", "Tactics", "Esports", "Skills", "Creator")
    val featuredLive = liveStreams.firstOrNull { it.status == "live" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // App Bar
        GoalTopAppBar(
            unreadMessagesCount = unreadMessagesCount,
            onSearchClick = onSearchClick,
            onMessagesClick = onMessagesClick
        )

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = category.equals(selectedCategory, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) GoalGreen else DarkSurfaceElevated)
                        .clickable { onCategorySelected(category) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("category_chip_$category")
                ) {
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
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
                    onTipClick = { onTipVideo(video) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
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
            .clickable { onClick() }
            .testTag("featured_hero_banner"),
        shape = RoundedCornerShape(16.dp),
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

            // Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xDD000000)),
                            startY = 100f
                        )
                    )
            )

            // Live Pill & Viewer Counter
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(LiveRed)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "LIVE NOW",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.8.sp
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xAA000000))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${liveStream.viewerCount / 1000}K viewers",
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            }

            // Bottom title & creator
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = liveStream.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "@${liveStream.username}",
                        fontSize = 12.sp,
                        color = GoalCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${liveStream.category}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
