package com.example.ui.screens.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CreatorBalanceEntity
import com.example.data.local.entity.TransactionEntity
import com.example.ui.components.GoalPremiumLogo
import com.example.ui.components.GoalProPillBadge
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoalAmber
import com.example.ui.theme.GoalCyan
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.LiveRed
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldDeep
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.example.ui.screens.security.PinLockVaultDialog

private data class PeriodAnalyticsSnapshot(
    val label: String,
    val views: String,
    val viewsDelta: String,
    val watchHours: String,
    val watchDelta: String,
    val subscribers: String,
    val subsDelta: String,
    val revenue: String,
    val revenueDelta: String,
    val chartBars: List<Float>,
    val chartLabels: List<String>,
    val accountsReached: String,
    val engagementRate: String
)

@Composable
fun AnalyticsDashboardContent(
    creatorBalance: CreatorBalanceEntity?,
    transactions: List<TransactionEntity>,
    onRequestPayout: (Double) -> Unit,
    vaultPin: String = "1234",
    decoyPin: String = "9999",
    isDecoyModeActive: Boolean = false,
    whitelistedBanks: List<String> = listOf(
        "Chase Premier Checking •••• 4892 [VERIFIED]",
        "Stripe Express Global •••• 9012 [WHITELISTED]"
    ),
    onAddWhitelistedBank: (String) -> Unit = {},
    isWalletPinEnabled: Boolean = false,
    isVaultUnlocked: Boolean = true,
    stealthModeEnabled: Boolean = false,
    onToggleStealthMode: () -> Unit = {},
    onUnlockVault: () -> Unit = {},
    onDecoyUnlock: () -> Unit = {},
    onIntruderAttempt: () -> Unit = {},
    onOpenSecurityShield: (() -> Unit)? = null,
    showStandaloneTopBar: Boolean = false,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (showStandaloneTopBar && onBack != null) {
        BackHandler { onBack() }
    }

    var selectedPeriodIndex by remember { mutableIntStateOf(1) } // 0: 7D, 1: 28D, 2: 90D
    var showPayoutDialog by remember { mutableStateOf(false) }
    var showWalletPinDialog by remember { mutableStateOf(false) }
    var selectedBankIdx by remember { mutableIntStateOf(0) }

    val periods = remember {
        listOf(
            PeriodAnalyticsSnapshot(
                label = "7 Days",
                views = "940.2K",
                viewsDelta = "+22.4%",
                watchHours = "24.8K hrs",
                watchDelta = "+19.1%",
                subscribers = "+3,820",
                subsDelta = "+16.8%",
                revenue = "$890.40",
                revenueDelta = "+28.5%",
                chartBars = listOf(0.48f, 0.62f, 0.55f, 0.78f, 0.84f, 0.92f, 1.0f),
                chartLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                accountsReached = "1.12M",
                engagementRate = "14.8%"
            ),
            PeriodAnalyticsSnapshot(
                label = "28 Days",
                views = "3.4M",
                viewsDelta = "+18.2%",
                watchHours = "89.4K hrs",
                watchDelta = "+24.0%",
                subscribers = "+12,480",
                subsDelta = "+14.6%",
                revenue = "$2,840.50",
                revenueDelta = "+31.5%",
                chartBars = listOf(0.42f, 0.58f, 0.51f, 0.69f, 0.77f, 0.88f, 0.96f),
                chartLabels = listOf("W1", "W2", "W3", "W4", "W5", "W6", "Now"),
                accountsReached = "3.42M",
                engagementRate = "12.6%"
            ),
            PeriodAnalyticsSnapshot(
                label = "90 Days",
                views = "11.8M",
                viewsDelta = "+46.9%",
                watchHours = "312.0K hrs",
                watchDelta = "+52.3%",
                subscribers = "+44,910",
                subsDelta = "+39.4%",
                revenue = "$9,420.80",
                revenueDelta = "+64.2%",
                chartBars = listOf(0.30f, 0.45f, 0.52f, 0.64f, 0.75f, 0.89f, 1.0f),
                chartLabels = listOf("M1", "M1.5", "M2", "M2.5", "M3", "Peak", "Now"),
                accountsReached = "9.85M",
                engagementRate = "15.2%"
            )
        )
    }

    val activeSnapshot = periods[selectedPeriodIndex]

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        if (showStandaloneTopBar && onBack != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(top = 28.dp, start = 8.dp, end = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("analytics_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Profile",
                        tint = Color.White
                    )
                }
                GoalPremiumLogo(size = 28.dp, animated = false)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Professional Insights",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        GoalProPillBadge(text = "STUDIO")
                    }
                    Text(
                        text = "Real-time Creator Telemetry & Revenue Ledger",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Timeframe Selector Bar (7 Days / 28 Days / 90 Days)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Last 28 Days Analytics",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "${activeSnapshot.accountsReached} accounts reached • ${activeSnapshot.engagementRate} engagement",
                            fontSize = 11.sp,
                            color = RoyalGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    periods.forEachIndexed { idx, period ->
                        val isSelected = selectedPeriodIndex == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) {
                                        Brush.linearGradient(listOf(RoyalGold, RoyalGoldDeep))
                                    } else {
                                        Brush.linearGradient(listOf(DarkSurfaceElevated, DarkSurfaceElevated))
                                    }
                                )
                                .clickable { selectedPeriodIndex = idx }
                                .padding(vertical = 8.dp)
                                .testTag("analytics_period_${period.label}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = period.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) DarkBg else TextSecondary
                            )
                        }
                    }
                }
            }

            // 4 Key Performance Metrics Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AnalyticsMetricCard(
                        title = "Total Views",
                        value = activeSnapshot.views,
                        change = activeSnapshot.viewsDelta,
                        isPositive = true,
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsMetricCard(
                        title = "Watch Time",
                        value = activeSnapshot.watchHours,
                        change = activeSnapshot.watchDelta,
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
                        value = activeSnapshot.subscribers,
                        change = activeSnapshot.subsDelta,
                        isPositive = true,
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsMetricCard(
                        title = "Net Revenue",
                        value = activeSnapshot.revenue,
                        change = activeSnapshot.revenueDelta,
                        isPositive = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Custom Canvas Views & Watch-Time Growth Chart Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.TrendingUp,
                                    contentDescription = "Growth",
                                    tint = RoyalGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Views & Reach Velocity (${activeSnapshot.label})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = "PEAK +34%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoalGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                        ) {
                            val barCount = activeSnapshot.chartBars.size
                            val totalSpacing = size.width * 0.32f
                            val barWidth = (size.width - totalSpacing) / barCount
                            val gap = totalSpacing / (barCount + 1)

                            activeSnapshot.chartBars.forEachIndexed { i, ratio ->
                                val barHeight = size.height * ratio.coerceIn(0.15f, 1f)
                                val x = gap + i * (barWidth + gap)
                                val y = size.height - barHeight
                                val isHighlight = i == barCount - 1 || i == barCount - 2

                                drawRoundRect(
                                    brush = if (isHighlight) {
                                        Brush.verticalGradient(listOf(RoyalGoldLight, RoyalGold, GoalGreen))
                                    } else {
                                        Brush.verticalGradient(
                                            listOf(
                                                GoalCyan.copy(alpha = 0.75f),
                                                DarkSurfaceElevated
                                            )
                                        )
                                    },
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(12f, 12f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            activeSnapshot.chartLabels.forEach { lbl ->
                                Text(
                                    text = lbl,
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Audience Demographics & Retention Breakdown
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Public,
                                contentDescription = "Audience",
                                tint = GoalCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Top Audience Geographies & Retention",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val geos = listOf(
                            Triple("India • Mumbai / Delhi", 0.42f, "42%"),
                            Triple("United Kingdom • London", 0.26f, "26%"),
                            Triple("United States • New York", 0.19f, "19%"),
                            Triple("Brazil & Europe • Other", 0.13f, "13%")
                        )

                        geos.forEach { (region, progress, pct) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = region, fontSize = 12.sp, color = TextPrimary)
                                Text(
                                    text = pct,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalGold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = GoalGreen,
                                trackColor = DarkSurfaceElevated
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }

            // Creator Wallet Balance Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                listOf(RoyalGold.copy(alpha = 0.6f), GoalGreen.copy(alpha = 0.5f))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
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
                                    tint = RoyalGold,
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = onToggleStealthMode,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .testTag("wallet_stealth_blur_toggle")
                                ) {
                                    Icon(
                                        imageVector = if (stealthModeEnabled) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = "Toggle Stealth Balance Blur",
                                        tint = if (stealthModeEnabled) GoalCyan else RoyalGoldLight,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                GoalProPillBadge(text = if (stealthModeEnabled) "STEALTH" else "USD ($)")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isDecoyModeActive) "Available Balance (Safe Mode)" else "Available Balance",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                val avail = if (isDecoyModeActive) 42.80 else (creatorBalance?.availableBalance ?: 4850.75)
                                Text(
                                    text = if (stealthModeEnabled) "$••,•••.••" else String.format(Locale.getDefault(), "$%.2f", avail),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (stealthModeEnabled) GoalCyan else GoalGreen
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Pending Payout",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                val pend = if (isDecoyModeActive) 12.00 else (creatorBalance?.pendingBalance ?: 1240.00)
                                Text(
                                    text = if (stealthModeEnabled) "$•,•••.••" else String.format(Locale.getDefault(), "$%.2f", pend),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalGold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Payout Whitelist & Bank IBAN Lock Guard Pill
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurface)
                                .border(1.dp, GoalGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "IBAN Lock",
                                    tint = GoalGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "IBAN Whitelist Guard: ${whitelistedBanks.getOrElse(selectedBankIdx) { whitelistedBanks.first() }}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                            }
                            GoalProPillBadge(text = "LOCKED")
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (isWalletPinEnabled && !isVaultUnlocked) {
                                    showWalletPinDialog = true
                                } else {
                                    showPayoutDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
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
                                text = "Request Whitelisted Bank Payout",
                                color = DarkBg,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // Recent Transactions Ledger Header
            item {
                Text(
                    text = "Monetization Transactions Ledger",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
            }

            val displayedTransactions = if (isDecoyModeActive) transactions.take(1) else transactions
            items(displayedTransactions, key = { it.id }) { tx ->
                TransactionRow(transaction = tx)
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (showWalletPinDialog) {
            PinLockVaultDialog(
                title = "Verify Creator Wallet PIN 🔐",
                subtitle = "Enter your 4-digit Security PIN to unlock Whitelisted Bank Payouts",
                expectedPin = vaultPin,
                decoyPin = decoyPin,
                onUnlocked = {
                    onUnlockVault()
                    showWalletPinDialog = false
                    showPayoutDialog = true
                },
                onDecoyUnlocked = {
                    onDecoyUnlock()
                },
                onIntruderAttempt = {
                    onIntruderAttempt()
                },
                onDismiss = { showWalletPinDialog = false }
            )
        }

        if (showPayoutDialog) {
            PayoutModalDialog(
                availableBalance = if (isDecoyModeActive) 42.80 else (creatorBalance?.availableBalance ?: 4850.75),
                whitelistedBanks = whitelistedBanks,
                selectedBankIdx = selectedBankIdx,
                onSelectBank = { selectedBankIdx = it },
                onAddWhitelistedBank = onAddWhitelistedBank,
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
        modifier = modifier.border(1.dp, DarkSurfaceBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
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
                    text = "$change vs prev. period",
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
        shape = RoundedCornerShape(12.dp),
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
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.18f)),
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
    whitelistedBanks: List<String> = listOf("Chase Premier Checking •••• 4892 [VERIFIED]"),
    selectedBankIdx: Int = 0,
    onSelectBank: (Int) -> Unit = {},
    onAddWhitelistedBank: (String) -> Unit = {},
    onDismiss: () -> Unit,
    onConfirmPayout: (Double) -> Unit
) {
    var amountInput by remember { mutableStateOf("1000.00") }
    var newIbanInput by remember { mutableStateOf("") }
    var showAddIbanField by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = null,
                    tint = GoalGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Whitelisted IBAN Bank Payout",
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Payout Whitelist & IBAN Lock Guard is active. Transfers are strictly restricted to pre-verified accounts:",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                whitelistedBanks.forEachIndexed { idx, bank ->
                    val isSelected = idx == selectedBankIdx
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) RoyalGold.copy(alpha = 0.16f) else DarkSurfaceElevated)
                            .border(
                                1.dp,
                                if (isSelected) RoyalGold else DarkSurfaceBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectBank(idx) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = if (isSelected) RoyalGold else GoalGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = bank,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) RoyalGoldLight else TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (showAddIbanField) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newIbanInput,
                            onValueChange = { newIbanInput = it },
                            label = { Text("New Bank / IBAN (e.g. HSBC •••• 7741)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated,
                                focusedBorderColor = GoalGreen,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        TextButton(
                            onClick = {
                                if (newIbanInput.isNotBlank()) {
                                    onAddWhitelistedBank(newIbanInput)
                                    newIbanInput = ""
                                    showAddIbanField = false
                                }
                            }
                        ) {
                            Text("Verify", color = GoalGreen, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                } else {
                    Text(
                        text = "+ Whitelist New Verified Bank IBAN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoalCyan,
                        modifier = Modifier
                            .clickable { showAddIbanField = true }
                            .padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = String.format(Locale.getDefault(), "Available to withdraw: $%.2f", availableBalance),
                    fontSize = 12.sp,
                    color = GoalGreen,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Payout Amount ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
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
                    val amount = amountInput.toDoubleOrNull() ?: 0.0
                    onConfirmPayout(amount)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalGold)
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
