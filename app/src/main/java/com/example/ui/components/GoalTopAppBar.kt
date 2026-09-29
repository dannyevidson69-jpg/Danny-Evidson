package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.GoalCyan
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.LiveRed

@Composable
fun GoalTopAppBar(
    unreadMessagesCount: Int = 1,
    onSearchClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBg)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Brand Logo & Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(GoalGreen, GoalCyan)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "GOAL",
                    tint = DarkBg,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "GOAL",
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                letterSpacing = 1.5.sp,
                color = Color.White
            )
            Box(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .clip(CircleShape)
                    .background(Color(0x3300E676))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "PRO",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoalGreen
                )
            }
        }

        // Search Action
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier.testTag("top_bar_search")
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = Color.White
            )
        }

        // Messages Action with Badge
        IconButton(
            onClick = onMessagesClick,
            modifier = Modifier.testTag("top_bar_messages")
        ) {
            BadgedBox(
                badge = {
                    if (unreadMessagesCount > 0) {
                        Badge(
                            containerColor = LiveRed,
                            contentColor = Color.White,
                            modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                        ) {
                            Text(text = "$unreadMessagesCount", fontSize = 10.sp)
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ChatBubbleOutline,
                    contentDescription = "Messages",
                    tint = Color.White
                )
            }
        }

        // Notifications
        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier.testTag("top_bar_notifications")
        ) {
            Icon(
                imageVector = Icons.Filled.NotificationsNone,
                contentDescription = "Notifications",
                tint = Color.White
            )
        }
    }
}
