package com.example.ui.screens.security

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Copyright
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
import kotlinx.coroutines.delay
import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

data class SecurityAuditEvent(
    val id: String,
    val title: String,
    val subtitle: String,
    val timestamp: String,
    val severity: String // "SAFE", "BLOCKED", "VERIFIED", "INTRUDER"
)

data class DeviceSessionItem(
    val id: String,
    val deviceName: String,
    val location: String,
    val isCurrent: Boolean
)

data class SecurityUiState(
    val vaultPin: String = "1234",
    val decoyPin: String = "9999",
    val isDecoyModeActive: Boolean = false,
    val failedPinAttempts: Int = 0,
    val intruderAlertsCount: Int = 0,
    val isAppVaultLocked: Boolean = false,
    val isDmPinEnabled: Boolean = true,
    val isWalletPinEnabled: Boolean = true,
    val isVaultUnlockedSession: Boolean = false,
    val isTwoFactorEnabled: Boolean = true,
    val isAntiPhishingEnabled: Boolean = true,
    val screenshotBlockerEnabled: Boolean = true,
    val stealthModeEnabled: Boolean = false,
    val creatorDrmEnabled: Boolean = true,
    val isFirewallActive: Boolean = true,
    val blockedMitmProbes: Int = 18,
    val autoLockPolicy: String = "Immediate", // "Immediate", "30s", "1m", "5m"
    val isIbanWhitelistLocked: Boolean = true,
    val whitelistedBanks: List<String> = listOf(
        "Chase Premier Checking •••• 4892 [VERIFIED]",
        "Stripe Express Global •••• 9012 [WHITELISTED]"
    ),
    val isEmergencyLockdown: Boolean = false,
    val verifiedConversationIds: Set<String> = setOf("conv_1"),
    val activeSessions: List<DeviceSessionItem> = listOf(
        DeviceSessionItem("sess_1", "Android 15 • Pixel 9 Pro (Current)", "Mumbai, IN • AES-256 Tunnel", true),
        DeviceSessionItem("sess_2", "MacBook Pro M4 • GOAL Studio Web", "London, UK • OAuth2 Verified", false)
    ),
    val auditEvents: List<SecurityAuditEvent> = listOf(
        SecurityAuditEvent(
            id = "sec_fw",
            title = "Live Network Firewall & TLS 1.3 Pinning",
            subtitle = "Zero-Trust DoH tunnel active • 18 tracker & MITM probes blocked",
            timestamp = "Just now",
            severity = "VERIFIED"
        ),
        SecurityAuditEvent(
            id = "sec_0",
            title = "Hardware Screenshot Shield Active",
            subtitle = "Android Window FLAG_SECURE enforced for private vaults & wallet",
            timestamp = "2 mins ago",
            severity = "VERIFIED"
        ),
        SecurityAuditEvent(
            id = "sec_1",
            title = "Blocked Suspicious Login Attempt",
            subtitle = "Unrecognized IP (185.220.101.42) stopped by 2FA & Zero-Trust Shield",
            timestamp = "14 mins ago",
            severity = "BLOCKED"
        ),
        SecurityAuditEvent(
            id = "sec_2",
            title = "E2EE SHA-256 Session Keys Rotated",
            subtitle = "Diffie-Hellman ratchet updated for all 5 Direct Message channels",
            timestamp = "1 hour ago",
            severity = "VERIFIED"
        ),
        SecurityAuditEvent(
            id = "sec_3",
            title = "Creator Wallet IBAN Whitelist Guard Active",
            subtitle = "Payouts restricted to verified Chase •••• 4892 & 4-Digit PIN",
            timestamp = "Today, 09:30 AM",
            severity = "SAFE"
        )
    )
) {
    val protectionScore: Float
        get() {
            if (isEmergencyLockdown) return 99.9f
            var score = 70f
            if (isDmPinEnabled) score += 5f
            if (isWalletPinEnabled) score += 5f
            if (isTwoFactorEnabled) score += 4f
            if (isAntiPhishingEnabled) score += 4f
            if (screenshotBlockerEnabled) score += 4f
            if (creatorDrmEnabled) score += 3f
            if (isFirewallActive) score += 3f
            if (stealthModeEnabled) score += 1.9f
            return score.coerceAtMost(99.9f)
        }
}

/**
 * Generates deterministic cryptographic SHA-256 safety numbers and hex fingerprint
 * for End-to-End Encrypted chat verification.
 */
fun computeSha256SafetyFingerprint(usernameA: String, usernameB: String): Pair<String, String> {
    val sortedPair = listOf(usernameA.lowercase(), usernameB.lowercase()).sorted().joinToString(":goal_e2ee_v2:")
    val digest = MessageDigest.getInstance("SHA-256").digest(sortedPair.toByteArray())
    val hexString = digest.take(16).joinToString("") { "%02X".format(it) }
        .chunked(4)
        .joinToString(" : ")

    // 12 groups of 5 numeric digits (Signal/Instagram E2EE safety number format)
    val numericGroups = (0 until 12).map { idx ->
        val b1 = digest[(idx * 2) % digest.size].toInt() and 0xFF
        val b2 = digest[(idx * 2 + 1) % digest.size].toInt() and 0xFF
        val num = abs((b1 shl 8) or b2) % 100000
        "%05d".format(num)
    }.chunked(4).joinToString("\n") { it.joinToString("   ") }

    return Pair(numericGroups, hexString)
}

/**
 * Computes deterministic AES-256-GCM simulated packet telemetry (ciphertext hex, 96-bit IV nonce,
 * and SHA-256 HMAC authentication tag) for the Encrypted Message Payload Inspector in Chat.
 */
data class EncryptedMessagePacketInfo(
    val algorithm: String,
    val ivNonceHex: String,
    val ciphertextHex: String,
    val sha256HmacTag: String,
    val ephemeralKeyId: String
)

fun computeEncryptedMessagePacket(messageId: String, content: String, senderId: String): EncryptedMessagePacketInfo {
    val digest = MessageDigest.getInstance("SHA-256")
        .digest("$messageId:$senderId:$content:goal_aes256_gcm".toByteArray())
    val ivHex = digest.take(12).joinToString("") { "%02X".format(it) }.chunked(4).joinToString(" ")
    val cipherBytes = MessageDigest.getInstance("SHA-256")
        .digest("CIPHER:$content:$messageId".toByteArray())
    val cipherHex = cipherBytes.take(24).joinToString("") { "%02X".format(it) }.chunked(8).joinToString("  ")
    val hmacHex = digest.takeLast(16).joinToString("") { "%02X".format(it) }.chunked(4).joinToString(":")
    val keyId = "X25519-RATCHET-${digest.take(3).joinToString("") { "%02X".format(it) }}"
    return EncryptedMessagePacketInfo(
        algorithm = "AES-256-GCM + Curve25519 Double Ratchet",
        ivNonceHex = ivHex,
        ciphertextHex = cipherHex,
        sha256HmacTag = hmacHex,
        ephemeralKeyId = keyId
    )
}

@Composable
fun SecurityShieldScreen(
    securityState: SecurityUiState,
    onToggleDmPin: (Boolean) -> Unit,
    onToggleWalletPin: (Boolean) -> Unit,
    onToggleTwoFactor: (Boolean) -> Unit,
    onToggleAntiPhishing: (Boolean) -> Unit,
    onToggleScreenshotBlocker: (Boolean) -> Unit = {},
    onToggleStealthMode: (Boolean) -> Unit = {},
    onToggleCreatorDrm: (Boolean) -> Unit = {},
    onToggleFirewall: (Boolean) -> Unit = {},
    onSelectAutoLockPolicy: (String) -> Unit = {},
    onToggleDecoyMode: (Boolean) -> Unit = {},
    onUpdateDecoyPin: (String) -> Unit = {},
    onClearIntruderAlerts: () -> Unit = {},
    onToggleEmergencyLockdown: () -> Unit = {},
    onLockAppGateNow: () -> Unit = {},
    onChangeVaultPin: (String) -> Unit,
    onLockVaultNow: () -> Unit,
    onRevokeSession: (String) -> Unit,
    onRunSecurityScan: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var showChangePinDialog by remember { mutableStateOf(false) }
    var showDecoyPinDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Luxury Royal Gold & Obsidian Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(DarkSurface, Color(0xFF141B2D), DarkSurface)
                    )
                )
                .border(0.5.dp, RoyalGold.copy(alpha = 0.35f))
                .padding(top = 28.dp, start = 8.dp, end = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("security_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = RoyalGoldLight
                )
            }
            GoalPremiumLogo(size = 30.dp, animated = true)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Fortress Security Center",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    GoalProPillBadge(
                        text = when {
                            securityState.isEmergencyLockdown -> "LOCKDOWN"
                            securityState.isDecoyModeActive -> "DECOY SAFE"
                            else -> "AES-256"
                        }
                    )
                }
                Text(
                    text = "Royal Gold Vault • Threat Radar • Intruder & DRM Guard",
                    fontSize = 11.sp,
                    color = if (securityState.isEmergencyLockdown) LiveRed else GoalGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Quick App Vault Lock Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(RoyalGold.copy(alpha = 0.16f))
                    .border(1.dp, RoyalGold, RoundedCornerShape(50))
                    .clickable { onLockAppGateNow() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("lock_entire_app_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Lock App Vault",
                        tint = RoyalGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Lock App",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RoyalGoldLight
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
            // Hero Security Score Gauge Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(listOf(RoyalGoldLight, RoyalGold, GoalGreen)),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Circular Protection Score Ring
                        Box(
                            modifier = Modifier.size(96.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val scoreRatio = (securityState.protectionScore / 100f).coerceIn(0f, 1f)
                            Canvas(modifier = Modifier.size(88.dp)) {
                                val stroke = 9.dp.toPx()
                                drawArc(
                                    color = DarkSurfaceBorder,
                                    startAngle = -210f,
                                    sweepAngle = 240f,
                                    useCenter = false,
                                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                                )
                                drawArc(
                                    brush = Brush.linearGradient(
                                        if (securityState.isEmergencyLockdown) {
                                            listOf(LiveRed, RoyalGold, RoyalGoldLight)
                                        } else {
                                            listOf(RoyalGoldLight, RoyalGold, GoalGreen)
                                        }
                                    ),
                                    startAngle = -210f,
                                    sweepAngle = 240f * scoreRatio,
                                    useCenter = false,
                                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Filled.Shield,
                                    contentDescription = null,
                                    tint = RoyalGoldLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "${securityState.protectionScore}%",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = if (securityState.isEmergencyLockdown) "LOCKDOWN" else "FORTRESS",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (securityState.isEmergencyLockdown) LiveRed else GoalGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (securityState.isEmergencyLockdown) {
                                    "🚨 Emergency Lockdown Active"
                                } else {
                                    "Military-Grade GOAL Protection"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "SHA-256 E2EE chat verification, Intruder Alarm, Decoy Vault, MITM Radar & DRM Watermark active.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = onRunSecurityScan,
                                    colors = ButtonDefaults.buttonColors(containerColor = GoalGreen),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .testTag("run_security_scan_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = null,
                                        tint = DarkBg,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Audit Scan",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkBg
                                    )
                                }

                                Button(
                                    onClick = { showChangePinDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .border(1.dp, RoyalGold, RoundedCornerShape(8.dp))
                                        .testTag("change_vault_pin_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Key,
                                        contentDescription = null,
                                        tint = RoyalGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "PIN: ${securityState.vaultPin}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalGold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Intruder Lockout & Brute-Force Defense Status Card
            item {
                val hasIntruders = securityState.intruderAlertsCount > 0 || securityState.failedPinAttempts > 0
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (hasIntruders) LiveRed else GoalGreen.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (hasIntruders) Color(0x24FF1744) else DarkSurface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    if (hasIntruders) LiveRed.copy(alpha = 0.22f)
                                    else GoalGreen.copy(alpha = 0.16f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (hasIntruders) Icons.Filled.GppBad else Icons.Filled.Security,
                                contentDescription = null,
                                tint = if (hasIntruders) LiveRed else GoalGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (hasIntruders) {
                                        "🚨 Intruder Brute-Force Alert (${securityState.intruderAlertsCount})"
                                    } else {
                                        "Intruder Lockout & 3-Strike Alarm Guard"
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = if (hasIntruders) {
                                    "Failed PIN attempts detected (${securityState.failedPinAttempts}). 30s lockout & telemetry logged."
                                } else {
                                    "Locks keypad for 30s after 3 wrong PINs & logs intruder device telemetry."
                                },
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                        if (hasIntruders) {
                            TextButton(onClick = onClearIntruderAlerts) {
                                Text(
                                    text = "Clear",
                                    color = RoyalGoldLight,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp
                                )
                            }
                        } else {
                            GoalProPillBadge(text = "ARMED")
                        }
                    }
                }
            }

            // Live Network Firewall & MITM Threat Radar Card
            item {
                LiveNetworkFirewallRadarCard(
                    isFirewallActive = securityState.isFirewallActive,
                    blockedMitmProbes = securityState.blockedMitmProbes,
                    onToggleFirewall = onToggleFirewall,
                    onRunPacketAudit = onRunSecurityScan
                )
            }

            // Decoy / Duress PIN Mode with Hidden Safe Vault Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                if (securityState.isDecoyModeActive) listOf(GoalCyan, RoyalGold)
                                else listOf(RoyalGold.copy(alpha = 0.55f), DarkSurfaceBorder)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(GoalCyan.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.VisibilityOff,
                                    contentDescription = null,
                                    tint = GoalCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Decoy PIN & Hidden Safe Vault",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    GoalProPillBadge(
                                        text = if (securityState.isDecoyModeActive) "DECOY ON" else "PIN: ${securityState.decoyPin}"
                                    )
                                }
                                Text(
                                    text = "Entering Decoy PIN (${securityState.decoyPin}) opens a sanitized vault hiding locked DMs & showing a $42.80 decoy wallet balance.",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                            Switch(
                                checked = securityState.isDecoyModeActive,
                                onCheckedChange = onToggleDecoyMode,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = DarkBg,
                                    checkedTrackColor = GoalCyan,
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = DarkSurfaceElevated
                                ),
                                modifier = Modifier.testTag("toggle_decoy_mode")
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (securityState.isDecoyModeActive) {
                                    "🕵️ Decoy Safe Mode Active: Real vaults & $4,850.75 balance hidden"
                                } else {
                                    "Master PIN: ${securityState.vaultPin} • Duress Decoy PIN: ${securityState.decoyPin}"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (securityState.isDecoyModeActive) GoalCyan else RoyalGoldLight
                            )
                            Text(
                                text = "Edit Decoy PIN →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RoyalGold,
                                modifier = Modifier
                                    .clickable { showDecoyPinDialog = true }
                                    .testTag("edit_decoy_pin_button")
                            )
                        }
                    }
                }
            }

            // One-Tap Emergency Lockdown Command Module
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (securityState.isEmergencyLockdown) LiveRed else RoyalGold.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (securityState.isEmergencyLockdown) {
                            Color(0x28FF1744)
                        } else {
                            DarkSurface
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (securityState.isEmergencyLockdown) LiveRed.copy(alpha = 0.25f)
                                    else RoyalGold.copy(alpha = 0.18f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.GppGood,
                                contentDescription = null,
                                tint = if (securityState.isEmergencyLockdown) LiveRed else RoyalGoldLight,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (securityState.isEmergencyLockdown) {
                                    "Emergency Lockdown Engaged"
                                } else {
                                    "One-Tap Emergency Lockdown"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Freezes wallet payouts, revokes external sessions, enables Stealth Blur & Screenshot Blocker.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onToggleEmergencyLockdown,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (securityState.isEmergencyLockdown) GoalGreen else LiveRed
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("emergency_lockdown_button")
                        ) {
                            Text(
                                text = if (securityState.isEmergencyLockdown) "Lift" else "Lockdown",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (securityState.isEmergencyLockdown) DarkBg else Color.White
                            )
                        }
                    }
                }
            }

            // Auto-Lock Session Timer Policy Selector
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = null,
                                tint = RoyalGoldLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Vault Auto-Lock Timer on Tab Switch / Idle",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Automatically re-locks unlocked DM & Wallet vaults when switching tabs",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        val policies = listOf("Immediate", "30s", "1m", "5m")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            policies.forEach { policy ->
                                val isSelected = securityState.autoLockPolicy == policy
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) {
                                                Brush.linearGradient(listOf(RoyalGoldLight, RoyalGold))
                                            } else {
                                                Brush.linearGradient(listOf(DarkSurfaceElevated, DarkSurfaceElevated))
                                            }
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) RoyalGoldLight else DarkSurfaceBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { onSelectAutoLockPolicy(policy) }
                                        .padding(vertical = 8.dp)
                                        .testTag("autolock_policy_$policy"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = policy,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) DarkBg else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Vault PIN & Zero-Trust Controls
            item {
                Text(
                    text = "Fortress Privacy, Content DRM & Zero-Trust Controls",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RoyalGoldLight
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        SecurityToggleRow(
                            icon = Icons.Filled.NoPhotography,
                            tint = RoyalGoldLight,
                            title = "Screenshot & Screen Recording Blocker",
                            subtitle = "Enforce Android hardware FLAG_SECURE across private vaults",
                            checked = securityState.screenshotBlockerEnabled,
                            onCheckedChange = onToggleScreenshotBlocker,
                            testTag = "toggle_screenshot_blocker"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        SecurityToggleRow(
                            icon = Icons.Filled.VisibilityOff,
                            tint = GoalCyan,
                            title = "Stealth Privacy & Balance Blur Mode",
                            subtitle = "Mask Creator Wallet earnings ($••,•••.••) & hide online status",
                            checked = securityState.stealthModeEnabled,
                            onCheckedChange = onToggleStealthMode,
                            testTag = "toggle_stealth_mode"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        SecurityToggleRow(
                            icon = Icons.Filled.Copyright,
                            tint = RoyalGold,
                            title = "Creator DRM Watermark & Anti-Piracy Guard",
                            subtitle = "Embed dynamic SHA-256 creator ownership watermark on videos",
                            checked = securityState.creatorDrmEnabled,
                            onCheckedChange = onToggleCreatorDrm,
                            testTag = "toggle_creator_drm"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        SecurityToggleRow(
                            icon = Icons.Filled.Lock,
                            tint = RoyalGold,
                            title = "PIN Lock for Private Direct Messages",
                            subtitle = "Require 4-digit Vault PIN (${securityState.vaultPin}) for Locked Chats",
                            checked = securityState.isDmPinEnabled,
                            onCheckedChange = onToggleDmPin,
                            testTag = "toggle_dm_pin"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        SecurityToggleRow(
                            icon = Icons.Filled.AccountBalance,
                            tint = GoalGreen,
                            title = "PIN Lock & IBAN Whitelist for Creator Wallet",
                            subtitle = "Restrict bank payouts to verified whitelisted IBANs with PIN",
                            checked = securityState.isWalletPinEnabled,
                            onCheckedChange = onToggleWalletPin,
                            testTag = "toggle_wallet_pin"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        SecurityToggleRow(
                            icon = Icons.Filled.Fingerprint,
                            tint = GoalCyan,
                            title = "2FA Hardware & Biometric Passkey Guard",
                            subtitle = "Block unauthorized logins from unknown devices",
                            checked = securityState.isTwoFactorEnabled,
                            onCheckedChange = onToggleTwoFactor,
                            testTag = "toggle_2fa"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        SecurityToggleRow(
                            icon = Icons.Filled.Phishing,
                            tint = GoalAmber,
                            title = "Real-Time Anti-Phishing & Link Scanner",
                            subtitle = "Inspect incoming DM links and block malicious domains",
                            checked = securityState.isAntiPhishingEnabled,
                            onCheckedChange = onToggleAntiPhishing,
                            testTag = "toggle_antiphishing"
                        )

                        if (securityState.isVaultUnlockedSession) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onLockVaultNow,
                                colors = ButtonDefaults.buttonColors(containerColor = LiveRed.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, LiveRed, RoundedCornerShape(10.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = null,
                                    tint = LiveRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Lock Vault Session Immediately",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Active Device Sessions
            item {
                Text(
                    text = "Active Authenticated Devices (${securityState.activeSessions.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            }

            items(securityState.activeSessions, key = { it.id }) { session ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (session.isCurrent) GoalGreen.copy(alpha = 0.18f)
                                    else RoyalGold.copy(alpha = 0.18f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Devices,
                                contentDescription = null,
                                tint = if (session.isCurrent) GoalGreen else RoyalGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = session.deviceName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = session.location,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        if (!session.isCurrent) {
                            TextButton(onClick = { onRevokeSession(session.id) }) {
                                Text(
                                    text = "Revoke",
                                    color = LiveRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            GoalProPillBadge(text = "ONLINE")
                        }
                    }
                }
            }

            // Live Threat & Security Audit Log
            item {
                Text(
                    text = "Security Audit & Threat Protection Log",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            }

            items(securityState.auditEvents, key = { it.id }) { event ->
                val (icon, tint) = when (event.severity) {
                    "INTRUDER", "BLOCKED" -> Pair(Icons.Filled.WarningAmber, LiveRed)
                    "VERIFIED" -> Pair(Icons.Filled.Key, RoyalGold)
                    else -> Pair(Icons.Filled.CheckCircle, GoalGreen)
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(tint.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = tint,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = event.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = event.timestamp,
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = event.subtitle,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (showChangePinDialog) {
            var newPinDraft by remember { mutableStateOf(securityState.vaultPin) }
            AlertDialog(
                onDismissRequest = { showChangePinDialog = false },
                containerColor = DarkSurface,
                title = {
                    Text(
                        text = "Update Master 4-Digit Security PIN 🔐",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 17.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "This Master PIN protects your App Vault Gate, Locked Direct Messages, and Creator Wallet bank payouts.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = newPinDraft,
                            onValueChange = { input ->
                                val digitsOnly = input.filter { it.isDigit() }.take(4)
                                newPinDraft = digitsOnly
                            },
                            label = { Text("4-Digit Master PIN") },
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
                            if (newPinDraft.length == 4) {
                                onChangeVaultPin(newPinDraft)
                                showChangePinDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold)
                    ) {
                        Text("Save PIN", color = DarkBg, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showChangePinDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }

        if (showDecoyPinDialog) {
            var decoyDraft by remember { mutableStateOf(securityState.decoyPin) }
            AlertDialog(
                onDismissRequest = { showDecoyPinDialog = false },
                containerColor = DarkSurface,
                title = {
                    Text(
                        text = "Configure Duress Decoy PIN 🕵️",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 17.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "If someone forces you to unlock GOAL, entering this 4-digit Decoy PIN opens a safe decoy view that hides Locked DMs and shows a $42.80 balance.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = decoyDraft,
                            onValueChange = { input ->
                                decoyDraft = input.filter { it.isDigit() }.take(4)
                            },
                            label = { Text("4-Digit Decoy PIN") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated,
                                focusedBorderColor = GoalCyan,
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
                            if (decoyDraft.length == 4 && decoyDraft != securityState.vaultPin) {
                                onUpdateDecoyPin(decoyDraft)
                                showDecoyPinDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoalCyan)
                    ) {
                        Text("Save Decoy PIN", color = DarkBg, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDecoyPinDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }
    }
}

/**
 * Interactive Live Network Firewall & MITM Threat Radar Card with animated sweep.
 */
@Composable
private fun LiveNetworkFirewallRadarCard(
    isFirewallActive: Boolean,
    blockedMitmProbes: Int,
    onToggleFirewall: (Boolean) -> Unit,
    onRunPacketAudit: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_sweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_angle"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(listOf(RoyalGold, GoalGreen, GoalCyan)),
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Animated Radar Sweep Graphic
                Box(
                    modifier = Modifier.size(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(68.dp)) {
                        val radius = size.minDimension / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)

                        // Concentric radar rings
                        drawCircle(
                            color = GoalGreen.copy(alpha = 0.25f),
                            radius = radius,
                            center = center,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                        drawCircle(
                            color = RoyalGold.copy(alpha = 0.25f),
                            radius = radius * 0.65f,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                        drawCircle(
                            color = GoalGreen.copy(alpha = 0.2f),
                            radius = radius * 0.32f,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )

                        if (isFirewallActive) {
                            val rad = Math.toRadians(sweepAngle.toDouble())
                            val endX = center.x + (radius * cos(rad)).toFloat()
                            val endY = center.y + (radius * sin(rad)).toFloat()
                            drawLine(
                                brush = Brush.linearGradient(listOf(RoyalGoldLight, GoalGreen)),
                                start = center,
                                end = Offset(endX, endY),
                                strokeWidth = 2.5.dp.toPx(),
                                cap = StrokeCap.Round
                            )

                            // Intercepted threat blips
                            drawCircle(
                                color = RoyalGoldLight,
                                radius = 3.dp.toPx(),
                                center = Offset(center.x + radius * 0.45f, center.y - radius * 0.35f)
                            )
                            drawCircle(
                                color = GoalGreen,
                                radius = 3.dp.toPx(),
                                center = Offset(center.x - radius * 0.4f, center.y + radius * 0.3f)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Filled.Radar,
                        contentDescription = null,
                        tint = if (isFirewallActive) GoalGreen else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Live Network Firewall & MITM Radar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "TLS 1.3 Certificate Pinning • DNS-over-HTTPS • $blockedMitmProbes MITM/Tracker Probes Blocked",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        GoalProPillBadge(text = "TLS 1.3 PINNED")
                        GoalProPillBadge(text = "$blockedMitmProbes BLOCKED")
                    }
                }

                Switch(
                    checked = isFirewallActive,
                    onCheckedChange = onToggleFirewall,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = DarkBg,
                        checkedTrackColor = GoalGreen,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = DarkSurfaceElevated
                    ),
                    modifier = Modifier.testTag("toggle_network_firewall")
                )
            }
        }
    }
}

@Composable
private fun SecurityToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = DarkBg,
                checkedTrackColor = GoalGreen,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = DarkSurfaceElevated
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

/**
 * Full-Screen Luxury Royal Gold & Obsidian App Security Vault Gate Overlay
 * Supports Master PIN, Duress Decoy PIN (`9999`), and 3-Strike Intruder Lockout Cooldown.
 */
@Composable
fun AppSecurityVaultGateOverlay(
    expectedPin: String,
    decoyPin: String = "9999",
    onUnlockSuccess: () -> Unit,
    onDecoyUnlock: () -> Unit = {},
    onIntruderAttempt: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var enteredPin by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }
    var localFailedCount by remember { mutableIntStateOf(0) }
    var lockoutSecondsRemaining by remember { mutableIntStateOf(0) }

    LaunchedEffect(lockoutSecondsRemaining) {
        if (lockoutSecondsRemaining > 0) {
            delay(1000)
            lockoutSecondsRemaining -= 1
        }
    }

    fun appendDigit(digit: String) {
        if (lockoutSecondsRemaining > 0) return
        if (enteredPin.length < 4) {
            hasError = false
            val updated = enteredPin + digit
            enteredPin = updated
            if (updated.length == 4) {
                when (updated) {
                    expectedPin -> {
                        localFailedCount = 0
                        onUnlockSuccess()
                    }
                    decoyPin -> {
                        localFailedCount = 0
                        onDecoyUnlock()
                    }
                    else -> {
                        hasError = true
                        enteredPin = ""
                        localFailedCount += 1
                        onIntruderAttempt()
                        if (localFailedCount >= 3) {
                            lockoutSecondsRemaining = 30
                            localFailedCount = 0
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF05070B), DarkBg, Color(0xFF101626))
                )
            )
            .padding(24.dp)
            .testTag("app_security_vault_gate"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        if (lockoutSecondsRemaining > 0) listOf(LiveRed, RoyalGold)
                        else listOf(RoyalGoldLight, RoyalGold, GoalGreen)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GoalPremiumLogo(size = 64.dp, animated = true)
                Spacer(modifier = Modifier.height(12.dp))
                GoalProPillBadge(
                    text = if (lockoutSecondsRemaining > 0) "INTRUDER LOCKOUT" else "FORTRESS VAULT GATE"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (lockoutSecondsRemaining > 0) {
                        "🚨 Intruder Alarm Active (${lockoutSecondsRemaining}s)"
                    } else {
                        "GOAL App Security Locked"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = if (lockoutSecondsRemaining > 0) LiveRed else Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (lockoutSecondsRemaining > 0) {
                        "3 failed PIN attempts detected! Device fingerprint logged to Security Audit Vault."
                    } else {
                        "AES-256-GCM & Screenshot Shield Active.\nMaster PIN ($expectedPin) or Decoy PIN ($decoyPin)."
                    },
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (lockoutSecondsRemaining > 0 || hasError) LiveRed.copy(alpha = 0.18f)
                            else RoyalGold.copy(alpha = 0.16f)
                        )
                        .border(
                            1.dp,
                            if (lockoutSecondsRemaining > 0 || hasError) LiveRed else RoyalGold.copy(alpha = 0.5f),
                            RoundedCornerShape(50)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when {
                            lockoutSecondsRemaining > 0 -> "Keypad Locked for ${lockoutSecondsRemaining}s • Biometric Passkey Available"
                            hasError -> "Invalid PIN! Strike $localFailedCount/3 • Master: $expectedPin"
                            else -> "Master PIN: $expectedPin • Decoy Safe PIN: $decoyPin"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (lockoutSecondsRemaining > 0 || hasError) LiveRed else RoyalGoldLight
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 4 Royal Gold PIN Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(
                                    if (hasError || lockoutSecondsRemaining > 0) LiveRed
                                    else if (isFilled) RoyalGold
                                    else DarkSurfaceElevated
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (isFilled) RoyalGoldLight else RoyalGold.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                val rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9")
                )
                rows.forEach { rowDigits ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowDigits.forEach { digit ->
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceElevated)
                                    .border(1.dp, RoyalGold.copy(alpha = 0.35f), CircleShape)
                                    .clickable { appendDigit(digit) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = digit,
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (lockoutSecondsRemaining > 0) TextSecondary else Color.White
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(GoalGreen.copy(alpha = 0.2f))
                            .border(1.5.dp, GoalGreen, CircleShape)
                            .clickable { onUnlockSuccess() }
                            .testTag("app_gate_biometric_unlock"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Fingerprint,
                            contentDescription = "Biometric Passkey Unlock",
                            tint = GoalGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                            .border(1.dp, RoyalGold.copy(alpha = 0.35f), CircleShape)
                            .clickable { appendDigit("0") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "0",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Black,
                            color = if (lockoutSecondsRemaining > 0) TextSecondary else Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                            .clickable {
                                if (enteredPin.isNotEmpty()) {
                                    enteredPin = enteredPin.dropLast(1)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = "Backspace",
                            tint = RoyalGoldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive 4-Digit Security PIN Pad Dialog protecting Locked Direct Messages & Creator Wallet.
 * Supports Master PIN, Duress Decoy PIN (`9999`), and 3-Strike Intruder Lockout Cooldown.
 */
@Composable
fun PinLockVaultDialog(
    title: String = "GOAL Security Vault Lock",
    subtitle: String = "Enter your 4-digit Security PIN to access protected Direct Messages & Creator Wallet",
    expectedPin: String = "1234",
    decoyPin: String = "9999",
    onUnlocked: () -> Unit,
    onDecoyUnlocked: () -> Unit = {},
    onIntruderAttempt: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }
    var localFailedCount by remember { mutableIntStateOf(0) }
    var lockoutSecondsRemaining by remember { mutableIntStateOf(0) }

    LaunchedEffect(lockoutSecondsRemaining) {
        if (lockoutSecondsRemaining > 0) {
            delay(1000)
            lockoutSecondsRemaining -= 1
        }
    }

    fun appendDigit(digit: String) {
        if (lockoutSecondsRemaining > 0) return
        if (enteredPin.length < 4) {
            hasError = false
            val updated = enteredPin + digit
            enteredPin = updated
            if (updated.length == 4) {
                when (updated) {
                    expectedPin -> {
                        localFailedCount = 0
                        onUnlocked()
                    }
                    decoyPin -> {
                        localFailedCount = 0
                        onDecoyUnlocked()
                        onUnlocked()
                    }
                    else -> {
                        hasError = true
                        enteredPin = ""
                        localFailedCount += 1
                        onIntruderAttempt()
                        if (localFailedCount >= 3) {
                            lockoutSecondsRemaining = 30
                            localFailedCount = 0
                        }
                    }
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        if (lockoutSecondsRemaining > 0) listOf(LiveRed, RoyalGold)
                        else listOf(RoyalGoldLight, RoyalGold, GoalGreen)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GoalPremiumLogo(size = 46.dp, animated = true)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (lockoutSecondsRemaining > 0) "🚨 Intruder Lockout (${lockoutSecondsRemaining}s)" else title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (lockoutSecondsRemaining > 0) LiveRed else Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (lockoutSecondsRemaining > 0) {
                        "3 wrong PIN tries detected. Intruder alert logged to Fortress Security Center."
                    } else {
                        subtitle
                    },
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0x22FFD700))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = when {
                            lockoutSecondsRemaining > 0 -> "Cooldown: ${lockoutSecondsRemaining}s remaining"
                            hasError -> "Incorrect PIN ($localFailedCount/3)! Master: $expectedPin"
                            else -> "Master PIN: $expectedPin • Decoy Safe PIN: $decoyPin"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasError || lockoutSecondsRemaining > 0) LiveRed else RoyalGold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4 PIN Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(
                                    if (hasError || lockoutSecondsRemaining > 0) LiveRed
                                    else if (isFilled) RoyalGold
                                    else DarkSurfaceElevated
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (isFilled) RoyalGoldLight else DarkSurfaceBorder,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Numeric Keypad Grid
                val rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9")
                )
                rows.forEach { rowDigits ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowDigits.forEach { digit ->
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceElevated)
                                    .border(1.dp, DarkSurfaceBorder, CircleShape)
                                    .clickable { appendDigit(digit) }
                                    .testTag("pin_digit_$digit"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = digit,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (lockoutSecondsRemaining > 0) TextSecondary else Color.White
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Bottom Row: Biometric Unlock, 0, Backspace
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(GoalGreen.copy(alpha = 0.18f))
                            .border(1.dp, GoalGreen, CircleShape)
                            .clickable { onUnlocked() }
                            .testTag("biometric_quick_unlock_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Fingerprint,
                            contentDescription = "Biometric Quick Unlock",
                            tint = GoalGreen,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                            .border(1.dp, DarkSurfaceBorder, CircleShape)
                            .clickable { appendDigit("0") }
                            .testTag("pin_digit_0"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "0",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (lockoutSecondsRemaining > 0) TextSecondary else Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                            .clickable {
                                if (enteredPin.isNotEmpty()) {
                                    enteredPin = enteredPin.dropLast(1)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = "Backspace",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = TextSecondary, fontSize = 13.sp)
                }
            }
        }
    }
}

/**
 * End-to-End Encryption (E2EE) SHA-256 Cryptographic Key Verification Modal.
 */
@Composable
fun E2eeKeyVerificationDialog(
    otherUsername: String,
    isVerified: Boolean,
    onMarkVerified: () -> Unit,
    onDismiss: () -> Unit
) {
    val (safetyNumbers, hexFingerprint) = remember(otherUsername) {
        computeSha256SafetyFingerprint("goal_creator", otherUsername)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = null,
                    tint = GoalGreen,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "End-to-End Encryption Keys",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Messages, voice notes, and shared Reels between @goal_creator and @$otherUsername are secured with AES-256-GCM & SHA-256 verification.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, RoyalGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "60-DIGIT SAFETY NUMBER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalGold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = safetyNumbers,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "SHA-256: $hexFingerprint",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = GoalCyan,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = if (isVerified) GoalGreen else RoyalGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isVerified) "Cryptographic Keys Verified & Trusted" else "Compare numbers with @$otherUsername to verify",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isVerified) GoalGreen else RoyalGold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onMarkVerified()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoalGreen)
            ) {
                Text(
                    text = if (isVerified) "Done" else "Verify Keys 🔒",
                    color = DarkBg,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        }
    )
}
