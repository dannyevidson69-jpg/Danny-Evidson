package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.LiveRed
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.TextSecondary

@Composable
fun GoalTopAppBar(
    unreadMessagesCount: Int = 1,
    protectionScore: Float = 98.0f,
    isEmergencyLockdown: Boolean = false,
    onSearchClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    onSecurityShieldClick: () -> Unit = {},
    onQuickLockAppClick: () -> Unit = {},
    onLogoClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0A0F1A), DarkBg)
                )
            )
            .border(0.5.dp, RoyalGold.copy(alpha = 0.25f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Flagship Target-G Brand Logo & Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .clickable { onLogoClick() }
        ) {
            GoalPremiumLogo(
                size = 38.dp,
                animated = true,
                useRasterEmblem = false
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color.White, RoyalGoldLight, RoyalGold)
                                    ),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 21.sp,
                                    letterSpacing = 1.8.sp
                                )
                            ) {
                                append("GOAL")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    GoalProPillBadge(text = "ROYAL PRO")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = if (isEmergencyLockdown) LiveRed else GoalGreen,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isEmergencyLockdown) "LOCKDOWN ACTIVE" else "FORTRESS E2EE • ${protectionScore}%",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.0.sp,
                        color = if (isEmergencyLockdown) LiveRed else GoalGreen
                    )
                }
            }
        }

        // Security Shield Pill Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(DarkSurfaceElevated)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        if (isEmergencyLockdown) listOf(LiveRed, RoyalGold)
                        else listOf(RoyalGold, GoalGreen)
                    ),
                    shape = RoundedCornerShape(50)
                )
                .clickable { onSecurityShieldClick() }
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .testTag("top_bar_security_shield"),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = "Security Shield",
                    tint = if (isEmergencyLockdown) LiveRed else RoyalGoldLight,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "Shield",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // 1-Tap Quick App Vault Lock Button
        IconButton(
            onClick = onQuickLockAppClick,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(DarkSurfaceElevated)
                .border(1.dp, RoyalGold.copy(alpha = 0.55f), CircleShape)
                .testTag("top_bar_quick_lock")
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Quick Lock App Vault",
                tint = RoyalGoldLight,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Search Action
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(DarkSurfaceElevated)
                .border(0.5.dp, DarkSurfaceBorder, CircleShape)
                .testTag("top_bar_search")
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Notifications
        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(DarkSurfaceElevated)
                .border(0.5.dp, DarkSurfaceBorder, CircleShape)
                .testTag("top_bar_notifications")
        ) {
            BadgedBox(
                badge = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .offset(x = (-2).dp, y = 2.dp)
                            .clip(CircleShape)
                            .background(GoalGreen)
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.NotificationsNone,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Instagram-style Direct Messages Action (Paper Plane / Messenger Icon with Unread Badge)
        IconButton(
            onClick = onMessagesClick,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(DarkSurfaceElevated)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(RoyalGoldLight, RoyalGold, GoalGreen)),
                    shape = CircleShape
                )
                .testTag("top_bar_messages")
        ) {
            BadgedBox(
                badge = {
                    if (unreadMessagesCount > 0) {
                        Badge(
                            containerColor = LiveRed,
                            contentColor = Color.White,
                            modifier = Modifier.offset(x = (-2).dp, y = 2.dp)
                        ) {
                            Text(
                                text = "$unreadMessagesCount",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = "Instagram Direct Messages",
                    tint = RoyalGoldLight,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(-28f)
                        .offset(x = 1.dp, y = (-1).dp)
                )
            }
        }
    }
}
