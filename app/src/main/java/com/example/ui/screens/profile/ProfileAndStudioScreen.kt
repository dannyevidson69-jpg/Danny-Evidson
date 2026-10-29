package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.entity.CreatorBalanceEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.VideoEntity
import com.example.ui.components.GoalPremiumLogo
import com.example.ui.components.GoalProPillBadge
import com.example.ui.components.GoalVerifiedBadge
import com.example.ui.components.GoalVideoCard
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoalCyan
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.InstaOrange
import com.example.ui.theme.InstaPink
import com.example.ui.theme.InstaPurple
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldDeep
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CreatorProfileUiState

private data class ProfileHighlightItem(
    val emoji: String,
    val title: String,
    val subtitle: String
)

@Composable
fun ProfileAndStudioScreen(
    videos: List<VideoEntity>,
    savedVideos: List<VideoEntity>,
    creatorBalance: CreatorBalanceEntity?,
    transactions: List<TransactionEntity>,
    onVideoClick: (VideoEntity) -> Unit,
    onLikeVideo: (VideoEntity) -> Unit,
    onSaveVideo: (VideoEntity) -> Unit,
    onRequestPayout: (Double) -> Unit,
    shortsVideos: List<VideoEntity> = emptyList(),
    creatorProfile: CreatorProfileUiState = CreatorProfileUiState(),
    vaultPin: String = "1234",
    decoyPin: String = "9999",
    isDecoyModeActive: Boolean = false,
    whitelistedBanks: List<String> = listOf(
        "Chase Premier Checking •••• 4892 [VERIFIED]",
        "Stripe Express Global •••• 9012 [WHITELISTED]"
    ),
    onAddWhitelistedBank: (String) -> Unit = {},
    creatorDrmEnabled: Boolean = true,
    isWalletPinEnabled: Boolean = false,
    isVaultUnlocked: Boolean = true,
    stealthModeEnabled: Boolean = false,
    onToggleStealthMode: () -> Unit = {},
    onUnlockVault: () -> Unit = {},
    onDecoyUnlock: () -> Unit = {},
    onIntruderAttempt: () -> Unit = {},
    onOpenSecurityShield: (() -> Unit)? = null,
    onOpenSeparateAnalytics: (() -> Unit)? = null,
    onEditProfile: ((String, String, String, String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedTopTab by remember { mutableIntStateOf(0) } // 0: My Channel (Pro Profile), 1: Creator Studio & Analytics
    var selectedContentTab by remember { mutableIntStateOf(0) } // 0: Uploaded, 1: Saved, 2: Shorts
    var showEditProfileDialog by remember { mutableStateOf(false) }

    val highlights = remember {
        listOf(
            ProfileHighlightItem("🎯", "Best Goals", "1.4M views"),
            ProfileHighlightItem("🏆", "UCL Final", "890K views"),
            ProfileHighlightItem("🎮", "UE5.5 Rig", "Pro Setup"),
            ProfileHighlightItem("⚡", "Shorts", "Viral Clips"),
            ProfileHighlightItem("💎", "VIP Perks", "Members")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Top Mode Switcher: Professional Profile vs Dedicated Analytics & Studio
        TabRow(
            selectedTabIndex = selectedTopTab,
            containerColor = DarkSurface,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTopTab]),
                    color = RoyalGold
                )
            },
            modifier = Modifier.padding(top = 28.dp)
        ) {
            Tab(
                selected = selectedTopTab == 0,
                onClick = { selectedTopTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "My Channel",
                            fontWeight = if (selectedTopTab == 0) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (selectedTopTab == 0) RoyalGold else TextSecondary
                        )
                    }
                },
                modifier = Modifier.testTag("tab_my_channel")
            )
            Tab(
                selected = selectedTopTab == 1,
                onClick = { selectedTopTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.BarChart,
                            contentDescription = null,
                            tint = if (selectedTopTab == 1) RoyalGold else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Creator Studio & Wallet",
                            fontWeight = if (selectedTopTab == 1) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (selectedTopTab == 1) RoyalGold else TextSecondary
                        )
                    }
                },
                modifier = Modifier.testTag("tab_creator_studio")
            )
        }

        if (selectedTopTab == 0) {
            // Professional Creator Profile Feed
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))

                    // Top Handle Row with Verified Seal & Brand Emblem
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GoalPremiumLogo(size = 28.dp, animated = true)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = creatorProfile.handle,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            GoalVerifiedBadge(isGoldTier = true, size = 18.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            GoalProPillBadge(text = "CREATOR PRO")
                        }

                        // Quick Analytics Launch Pill
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(DarkSurfaceElevated)
                                .border(1.dp, RoyalGold.copy(alpha = 0.5f), RoundedCornerShape(50))
                                .clickable {
                                    if (onOpenSeparateAnalytics != null) {
                                        onOpenSeparateAnalytics()
                                    } else {
                                        selectedTopTab = 1
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .testTag("open_separate_analytics_pill"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Analytics,
                                contentDescription = "Separate Analytics",
                                tint = RoyalGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Analytics",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Instagram Pro Profile Row: Avatar + 3 Key Metrics
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Luxury Gradient Ring Avatar with Target-G Corner Badge
                        Box {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                                contentDescription = "My Avatar",
                                modifier = Modifier
                                    .size(82.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.5.dp,
                                        brush = Brush.linearGradient(
                                            listOf(RoyalGoldLight, RoyalGold, GoalGreen, GoalCyan)
                                        ),
                                        shape = CircleShape
                                    )
                                    .padding(3.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(DarkBg)
                                    .border(1.5.dp, RoyalGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🎯", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        // 3-Column Stats (Instagram / Big Platform Pro Style)
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStatColumn(
                                value = "${videos.size + shortsVideos.size + 26}",
                                label = "Posts"
                            )
                            ProfileStatColumn(
                                value = creatorProfile.followersCount,
                                label = "Subscribers"
                            )
                            ProfileStatColumn(
                                value = creatorProfile.totalLikesCount,
                                label = "Total Likes"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Creator Name, Category Tag, Bio & Link
                    Text(
                        text = creatorProfile.displayName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = creatorProfile.category,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RoyalGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = creatorProfile.bio,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Link,
                            contentDescription = "Website",
                            tint = GoalCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = creatorProfile.website,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoalCyan
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "128K Subscribers",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GoalGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dedicated Instagram-style "Professional Dashboard (Separate Analytics)" Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    listOf(RoyalGold.copy(alpha = 0.7f), GoalGreen.copy(alpha = 0.5f))
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                if (onOpenSeparateAnalytics != null) {
                                    onOpenSeparateAnalytics()
                                } else {
                                    selectedTopTab = 1
                                }
                            }
                            .testTag("professional_dashboard_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Professional Dashboard",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(GoalGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "+18.2% Reach",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoalGreen
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "3.4M accounts reached in the last 28 days • Tap for separate Analytics & Wallet",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Open Analytics",
                                tint = RoyalGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action Buttons Row (Edit Profile / Separate Analytics / Share)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceElevated)
                                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
                                .clickable { showEditProfileDialog = true }
                                .padding(vertical = 9.dp)
                                .testTag("edit_profile_button"),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Profile",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Edit Profile",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(listOf(RoyalGold, RoyalGoldDeep))
                                )
                                .clickable {
                                    if (onOpenSeparateAnalytics != null) {
                                        onOpenSeparateAnalytics()
                                    } else {
                                        selectedTopTab = 1
                                    }
                                }
                                .padding(vertical = 9.dp)
                                .testTag("view_analytics_button"),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.BarChart,
                                contentDescription = "View Analytics",
                                tint = DarkBg,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Pro Analytics",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DarkBg
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Story Highlights Carousel
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(highlights) { item ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(64.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = 1.5.dp,
                                            brush = Brush.linearGradient(
                                                listOf(RoyalGold, InstaPink, InstaPurple)
                                            ),
                                            shape = CircleShape
                                        )
                                        .padding(3.dp)
                                        .clip(CircleShape)
                                        .background(DarkSurfaceElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = item.emoji, fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Content Subtabs (Uploaded Videos vs Saved Videos)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedContentTab == 0) GoalGreen else DarkSurfaceElevated)
                                .clickable { selectedContentTab = 0 }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Uploaded Videos (${videos.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedContentTab == 0) DarkBg else TextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedContentTab == 1) GoalGreen else DarkSurfaceElevated)
                                .clickable { selectedContentTab = 1 }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Saved Videos (${savedVideos.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedContentTab == 1) DarkBg else TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                val currentDisplayList = if (selectedContentTab == 0) videos else savedVideos

                items(currentDisplayList, key = { it.id }) { video ->
                    GoalVideoCard(
                        video = video,
                        onClick = { onVideoClick(video) },
                        onLikeClick = { onLikeVideo(video) },
                        onSaveClick = { onSaveVideo(video) },
                        drmWatermarkEnabled = creatorDrmEnabled
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else {
            // Dedicated Analytics & Creator Studio View
            AnalyticsDashboardContent(
                creatorBalance = creatorBalance,
                transactions = transactions,
                onRequestPayout = onRequestPayout,
                vaultPin = vaultPin,
                decoyPin = decoyPin,
                isDecoyModeActive = isDecoyModeActive,
                whitelistedBanks = whitelistedBanks,
                onAddWhitelistedBank = onAddWhitelistedBank,
                isWalletPinEnabled = isWalletPinEnabled,
                isVaultUnlocked = isVaultUnlocked,
                stealthModeEnabled = stealthModeEnabled,
                onToggleStealthMode = onToggleStealthMode,
                onUnlockVault = onUnlockVault,
                onDecoyUnlock = onDecoyUnlock,
                onIntruderAttempt = onIntruderAttempt,
                onOpenSecurityShield = onOpenSecurityShield,
                showStandaloneTopBar = false
            )
        }

        // Interactive Edit Profile Dialog
        if (showEditProfileDialog) {
            var nameDraft by remember { mutableStateOf(creatorProfile.displayName) }
            var categoryDraft by remember { mutableStateOf(creatorProfile.category) }
            var bioDraft by remember { mutableStateOf(creatorProfile.bio) }
            var webDraft by remember { mutableStateOf(creatorProfile.website) }

            AlertDialog(
                onDismissRequest = { showEditProfileDialog = false },
                containerColor = DarkSurface,
                title = {
                    Text(
                        text = "Edit Professional Profile 🎯",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nameDraft,
                            onValueChange = { nameDraft = it },
                            label = { Text("Display Name") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated,
                                focusedBorderColor = RoyalGold,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = categoryDraft,
                            onValueChange = { categoryDraft = it },
                            label = { Text("Creator Category") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated,
                                focusedBorderColor = RoyalGold,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = bioDraft,
                            onValueChange = { bioDraft = it },
                            label = { Text("Bio") },
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated,
                                focusedBorderColor = RoyalGold,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = webDraft,
                            onValueChange = { webDraft = it },
                            label = { Text("Website Link") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated,
                                focusedBorderColor = RoyalGold,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onEditProfile?.invoke(nameDraft, categoryDraft, bioDraft, webDraft)
                            showEditProfileDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold)
                    ) {
                        Text("Save Profile", color = DarkBg, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditProfileDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun ProfileStatColumn(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}
