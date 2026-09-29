package com.example.ui.screens.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.VideoEntity
import com.example.ui.components.GoalVideoCard
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    searchQuery: String,
    searchResults: List<VideoEntity>,
    onQueryChange: (String) -> Unit,
    onVideoClick: (VideoEntity) -> Unit,
    onLikeVideo: (VideoEntity) -> Unit,
    onSaveVideo: (VideoEntity) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val trendingTags = listOf("Champions League", "Unreal Engine 5", "Clutch 1v4", "Nutmeg", "FPV Drone", "Tutorial")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Search Input Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, start = 8.dp, end = 16.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("search_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                placeholder = { Text("Search videos, shorts, creators...", color = TextTertiary, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = GoalGreen,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_input_field"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated,
                    focusedBorderColor = GoalGreen,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )
        }

        if (searchQuery.isBlank()) {
            // Trending Search Suggestions
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Trending Searches on GOAL",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    trendingTags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkSurfaceElevated)
                                .clickable { onQueryChange(tag) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        } else {
            // Results
            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No videos matching \"$searchQuery\"",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "${searchResults.size} results for \"$searchQuery\"",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    items(searchResults, key = { it.id }) { video ->
                        GoalVideoCard(
                            video = video,
                            onClick = { onVideoClick(video) },
                            onLikeClick = { onLikeVideo(video) },
                            onSaveClick = { onSaveVideo(video) }
                        )
                    }
                }
            }
        }
    }
}
