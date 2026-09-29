package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainTab

@Composable
fun GoalBottomNav(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("goal_bottom_navigation"),
        containerColor = DarkSurface,
        contentColor = Color.White,
        tonalElevation = 8.dp
    ) {
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
            label = { Text("Home", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoalGreen,
                selectedTextColor = GoalGreen,
                indicatorColor = Color(0x2200E676),
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
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
            label = { Text("Shorts", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoalGreen,
                selectedTextColor = GoalGreen,
                indicatorColor = Color(0x2200E676),
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )

        // Create (+)
        NavigationBarItem(
            modifier = Modifier.testTag("nav_create"),
            selected = selectedTab == MainTab.CREATE,
            onClick = { onTabSelected(MainTab.CREATE) },
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(6.dp, CircleShape)
                        .background(GoalGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create Media",
                        tint = DarkBg,
                        modifier = Modifier.size(24.dp)
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
            label = { Text("Live", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoalGreen,
                selectedTextColor = GoalGreen,
                indicatorColor = Color(0x2200E676),
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
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
            label = { Text("Profile", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoalGreen,
                selectedTextColor = GoalGreen,
                indicatorColor = Color(0x2200E676),
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
    }
}
