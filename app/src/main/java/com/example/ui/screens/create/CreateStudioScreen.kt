package com.example.ui.screens.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoalAmber
import com.example.ui.theme.GoalCyan
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.TranscodeStep

@Composable
fun CreateStudioScreen(
    isTranscoding: Boolean,
    transcodeSteps: List<TranscodeStep>,
    onUploadVideo: (title: String, description: String, category: String, isShort: Boolean) -> Unit,
    onStartLiveStream: (title: String, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Video Upload, 1: Go Live

    // Form fields for Video Upload
    var videoTitle by remember { mutableStateOf("") }
    var videoDescription by remember { mutableStateOf("") }
    var isShortFormat by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Football") }

    // Form fields for Live Stream
    var liveTitle by remember { mutableStateOf("") }
    var liveCategory by remember { mutableStateOf("Gaming") }
    var generatedStreamKey by remember { mutableStateOf("live_sk_${(1000..9999).random()}_rtmp_goal") }

    val categories = listOf("Football", "Gaming", "Tactics", "Esports", "Skills", "Creator")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(GoalGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CloudUpload,
                    contentDescription = "Upload",
                    tint = DarkBg,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Creator Studio Ingest",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "FFmpeg Transcode Pipeline & Broadcast",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = GoalGreen
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = "Upload Video",
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 0) GoalGreen else TextSecondary
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = "Broadcast Live",
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 1) LiveRed else TextSecondary
                    )
                }
            )
        }

        // Tab Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (selectedTab == 0) {
                // Video Upload Section
                Text(
                    text = "Video Format",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Standard Video Option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (!isShortFormat) Color(0x3300E676) else DarkSurface)
                            .border(
                                width = 1.dp,
                                color = if (!isShortFormat) GoalGreen else DarkSurfaceBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { isShortFormat = false }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Movie,
                                contentDescription = "Standard",
                                tint = if (!isShortFormat) GoalGreen else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Standard (16:9)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (!isShortFormat) Color.White else TextSecondary
                            )
                        }
                    }

                    // Short Reel Option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isShortFormat) Color(0x3300E676) else DarkSurface)
                            .border(
                                width = 1.dp,
                                color = if (isShortFormat) GoalGreen else DarkSurfaceBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { isShortFormat = true }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.PlayCircle,
                                contentDescription = "Shorts",
                                tint = if (isShortFormat) GoalGreen else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Short (9:16)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isShortFormat) Color.White else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title Input
                Text(
                    text = "Video Title",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = videoTitle,
                    onValueChange = { videoTitle = it },
                    placeholder = { Text("e.g., Impossible Free Kick Top Bins vs Arsenal", color = TextTertiary, fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_title_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = GoalGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Description Input
                Text(
                    text = "Description & Tags",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = videoDescription,
                    onValueChange = { videoDescription = it },
                    placeholder = { Text("Add video details, chapters, social links and hashtags...", color = TextTertiary, fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("upload_description_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = GoalGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Category Chips
                Text(
                    text = "Category",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.take(4).forEach { cat ->
                        val isSelected = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) GoalGreen else DarkSurfaceElevated)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) DarkBg else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Transcoding Pipeline Status Box
                AnimatedVisibility(visible = isTranscoding) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = GoalGreen,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "FFmpeg Video Transcoding Pipeline",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = GoalGreen
                                    )
                                }
                                Text(
                                    text = "libx264 • aac",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            transcodeSteps.forEach { step ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = step.resolution,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (step.isComplete) GoalGreen else TextPrimary,
                                        modifier = Modifier.width(90.dp)
                                    )
                                    LinearProgressIndicator(
                                        progress = { step.progress },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = GoalGreen,
                                        trackColor = DarkSurfaceBorder
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    if (step.isComplete) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = "Done",
                                            tint = GoalGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Text(
                                            text = step.bitrate,
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Submit Button
                Button(
                    onClick = {
                        val title = if (videoTitle.isNotBlank()) videoTitle else "New Epic GOAL Video"
                        val desc = if (videoDescription.isNotBlank()) videoDescription else "Uploaded via GOAL creator studio."
                        onUploadVideo(title, desc, selectedCategory, isShortFormat)
                    },
                    enabled = !isTranscoding,
                    colors = ButtonDefaults.buttonColors(containerColor = GoalGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("upload_submit_button")
                ) {
                    if (isTranscoding) {
                        Text("Transcoding & Encoding Video...", color = DarkBg, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Transcode & Publish to GOAL", color = DarkBg, fontWeight = FontWeight.Bold)
                    }
                }

            } else {
                // Broadcast Live Tab
                Text(
                    text = "Broadcast Room Configuration",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Configure your OBS / RTMP encoder to broadcast to GOAL Live",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Stream Title",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = liveTitle,
                    onValueChange = { liveTitle = it },
                    placeholder = { Text("e.g. FIFA Finals Overtime Watch Party!", color = TextTertiary, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = LiveRed,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // RTMP Endpoint URL
                Text(
                    text = "Server RTMP Ingest URL",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurfaceElevated)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "rtmp://ingest.goal.tv:1935/live",
                        fontSize = 13.sp,
                        color = GoalCyan,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stream Key
                Text(
                    text = "Stream Key (Keep secret)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurfaceElevated)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = generatedStreamKey,
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy Stream Key",
                            tint = GoalGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val title = if (liveTitle.isNotBlank()) liveTitle else "Live with @goal_creator"
                        onStartLiveStream(title, liveCategory)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LiveRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("start_live_broadcast_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Sensors,
                        contentDescription = "Go Live",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Launch Live Stream Now",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
