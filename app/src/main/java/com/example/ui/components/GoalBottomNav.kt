package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainTab

@Composable
fun GoalBottomNav(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .border(
                width = 0.8.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        RoyalGold.copy(alpha = 0.25f),
                        RoyalGoldLight.copy(alpha = 0.55f),
                        GoalGreen.copy(alpha = 0.35f),
                        RoyalGold.copy(alpha = 0.25f)
                    )
                ),
                shape = RectangleShape
            )
            .testTag("goal_bottom_navigation"),
        containerColor = Color(0xFF0A0E17),
        contentColor = Color.White,
        tonalElevation = 12.dp
    ) {
        val navColors = NavigationBarItemDefaults.colors(
            selectedIconColor = RoyalGoldLight,
            selectedTextColor = RoyalGoldLight,
            indicatorColor = Color(0x28F59E0B),
            unselectedIconColor = TextSecondary,
            unselectedTextColor = TextSecondary
        )

        // Home
        NavigationBarItem(
            modifier = Modifier.testTag("nav_home"),
            selected = selectedTab == MainTab.HOME,
            onClick = { onTabSelected(MainTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == MainTab.HOME) FontWeight.ExtraBold else FontWeight.Medium
                )
            },
            colors = navColors
        )

        // Shorts
        NavigationBarItem(
            modifier = Modifier.testTag("nav_shorts"),
            selected = selectedTab == MainTab.SHORTS,
            onClick = { onTabSelected(MainTab.SHORTS) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == MainTab.SHORTS) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircleOutline,
                    contentDescription = "Shorts"
                )
            },
            label = {
                Text(
                    text = "Shorts",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == MainTab.SHORTS) FontWeight.ExtraBold else FontWeight.Medium
                )
            },
            colors = navColors
        )

        // Create (+) — Luxury Royal Gold & Emerald Glowing Button
        NavigationBarItem(
            modifier = Modifier.testTag("nav_create"),
            selected = selectedTab == MainTab.CREATE,
            onClick = { onTabSelected(MainTab.CREATE) },
            icon = {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(10.dp, CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(RoyalGoldLight, RoyalGold, GoalGreen)
                            ),
                            shape = CircleShape
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create Media",
                        tint = DarkBg,
                        modifier = Modifier.size(26.dp)
                    )
                }
            },
            label = {},
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        // Live
        NavigationBarItem(
            modifier = Modifier.testTag("nav_live"),
            selected = selectedTab == MainTab.LIVE,
            onClick = { onTabSelected(MainTab.LIVE) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == MainTab.LIVE) Icons.Filled.LiveTv else Icons.Outlined.LiveTv,
                    contentDescription = "Live"
                )
            },
            label = {
                Text(
                    text = "Live",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == MainTab.LIVE) FontWeight.ExtraBold else FontWeight.Medium
                )
            },
            colors = navColors
        )

        // Profile
        NavigationBarItem(
            modifier = Modifier.testTag("nav_profile"),
            selected = selectedTab == MainTab.PROFILE,
            onClick = { onTabSelected(MainTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == MainTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = {
                Text(
                    text = "Profile",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == MainTab.PROFILE) FontWeight.ExtraBold else FontWeight.Medium
                )
            },
            colors = navColors
        )
    }
}
