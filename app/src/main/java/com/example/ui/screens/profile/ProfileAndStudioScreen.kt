package com.example.ui.screens.profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.CreatorBalanceEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.VideoEntity
import com.example.ui.components.GoalVideoCard
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
import java.util.Locale

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
    modifier: Modifier = Modifier
) {
    var selectedTopTab by remember { mutableIntStateOf(0) } // 0: Channel & Videos, 1: Creator Studio & Wallet
    var selectedContentTab by remember { mutableIntStateOf(0) } // 0: My Videos, 1: Saved Collection
    var showPayoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Switch between Channel View & Creator Studio
        TabRow(
            selectedTabIndex = selectedTopTab,
            containerColor = DarkSurface,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTopTab]),
                    color = GoalGreen
                )
            },
            modifier = Modifier.padding(top = 28.dp)
        ) {
            Tab(
                selected = selectedTopTab == 0,
                onClick = { selectedTopTab = 0 },
                text = {
                    Text(
                        text = "My Channel",
                        fontWeight = if (selectedTopTab == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTopTab == 0) GoalGreen else TextSecondary
                    )
                },
                modifier = Modifier.testTag("tab_my_channel")
            )
            Tab(
                selected = selectedTopTab == 1,
                onClick = { selectedTopTab = 1 },
                text = {
                    Text(
                        text = "Creator Studio & Wallet",
                        fontWeight = if (selectedTopTab == 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTopTab == 1) GoalGreen else TextSecondary
                    )
                },
                modifier = Modifier.testTag("tab_creator_studio")
            )
        }

        if (selectedTopTab == 0) {
            // Channel & Videos Feed
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Header Profile Info
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                            contentDescription = "My Avatar",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(2.dp, GoalGreen, CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "@goal_creator",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Verified Creator",
                                    tint = GoalGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Pro Streamer & Sports Video Architect ⚽🎮",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "128K Subscribers",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GoalCyan
                                )
                                Text(
                                    text = "34 Videos",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Secondary Subtabs (Uploaded vs Saved)
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
                        onSaveClick = { onSaveVideo(video) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else {
            // Creator Studio & Monetization / Wallet View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Wallet Balance Card
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("creator_balance_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.AccountBalance,
                                        contentDescription = "Wallet",
                                        tint = GoalGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Creator Wallet & Earnings",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                }
                                Text(
                                    text = "USD ($)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoalCyan
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Available Balance",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                    val avail = creatorBalance?.availableBalance ?: 4850.75
                                    Text(
                                        text = String.format(Locale.getDefault(), "$%.2f", avail),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GoalGreen
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Pending Payout",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                    val pend = creatorBalance?.pendingBalance ?: 1240.00
                                    Text(
                                        text = String.format(Locale.getDefault(), "$%.2f", pend),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoalAmber
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { showPayoutDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GoalGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("request_payout_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Payment,
                                    contentDescription = "Payout",
                                    tint = DarkBg,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Request Bank Payout",
                                    color = DarkBg,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 28-Day Performance Metrics Grid
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Last 28 Days Analytics",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AnalyticsMetricCard(
                            title = "Total Views",
                            value = "3.4M",
                            change = "+18.2%",
                            isPositive = true,
                            modifier = Modifier.weight(1f)
                        )
                        AnalyticsMetricCard(
                            title = "Watch Time",
                            value = "89.4K hrs",
                            change = "+24.0%",
                            isPositive = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AnalyticsMetricCard(
                            title = "Subscribers",
                            value = "+12,480",
                            change = "+14.6%",
                            isPositive = true,
                            modifier = Modifier.weight(1f)
                        )
                        AnalyticsMetricCard(
                            title = "Net Revenue",
                            value = "$2,840.50",
                            change = "+31.5%",
                            isPositive = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Recent Transactions Ledger
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Monetization Transactions Ledger",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(transactions, key = { it.id }) { tx ->
                    TransactionRow(transaction = tx)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Payout Request Modal
        if (showPayoutDialog) {
            PayoutModalDialog(
                availableBalance = creatorBalance?.availableBalance ?: 4850.75,
                onDismiss = { showPayoutDialog = false },
                onConfirmPayout = { amount ->
                    onRequestPayout(amount)
                    showPayoutDialog = false
                }
            )
        }
    }
}

@Composable
fun AnalyticsMetricCard(
    title: String,
    value: String,
    change: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isPositive) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                    contentDescription = null,
                    tint = if (isPositive) GoalGreen else LiveRed,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = change,
                    fontSize = 10.sp,
                    color = if (isPositive) GoalGreen else LiveRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TransactionRow(transaction: TransactionEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (icon, tint) = when (transaction.type) {
                "tip" -> Pair(Icons.Filled.MonetizationOn, GoalAmber)
                "subscription" -> Pair(Icons.Filled.CardMembership, GoalCyan)
                else -> Pair(Icons.Filled.AccountBalance, GoalGreen)
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = transaction.type,
                    tint = tint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = "${transaction.type.uppercase()} • ${transaction.status}",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }

            val prefix = if (transaction.type == "payout") "-" else "+"
            val color = if (transaction.type == "payout") LiveRed else GoalGreen
            Text(
                text = String.format(Locale.getDefault(), "%s$%.2f", prefix, transaction.amount),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun PayoutModalDialog(
    availableBalance: Double,
    onDismiss: () -> Unit,
    onConfirmPayout: (Double) -> Unit
) {
    var amountInput by remember { mutableStateOf("1000.00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "Request Bank Transfer",
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Transfer earnings directly to your verified bank account (****4892).",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = String.format(Locale.getDefault(), "Available to withdraw: $%.2f", availableBalance),
                    fontSize = 12.sp,
                    color = GoalGreen,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Payout Amount ($)") },
                    modifier = Modifier.fillMaxWidth(),
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountInput.toDoubleOrNull() ?: 0.0
                    onConfirmPayout(amount)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoalGreen)
            ) {
                Text("Confirm Payout", color = DarkBg, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
