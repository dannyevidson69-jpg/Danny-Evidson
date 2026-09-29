package com.example.ui.screens.live

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.LiveStreamEntity
import com.example.ui.components.TipDialog
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

data class LiveChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val avatarUrl: String,
    val text: String,
    val isSuperChat: Boolean = false,
    val tipAmount: Double = 0.0,
    val badgeColor: Color = GoalGreen
)

@Composable
fun LiveRoomScreen(
    stream: LiveStreamEntity,
    onBack: () -> Unit,
    onSendTip: (Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var inputChat by remember { mutableStateOf("") }
    var viewerCount by remember { mutableIntStateOf(stream.viewerCount.toInt()) }
    var showTipDialog by remember { mutableStateOf(false) }
    val chatMessages = remember {
        mutableStateListOf(
            LiveChatMessage(username = "TacticalBeast", avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150", text = "LETS GOOO! Tokyo Dome crowd is hype! ⚽🔥"),
            LiveChatMessage(username = "Kira_Striker", avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150", text = "Who do you think wins this final??"),
            LiveChatMessage(username = "GoalSupporter99", avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150", text = "Rooting for the underdog comeback! Super Chat incoming!", isSuperChat = true, tipAmount = 50.0, badgeColor = GoalAmber),
            LiveChatMessage(username = "CyberSam", avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150", text = "Stream quality on GOAL is so smooth 60fps ✨")
        )
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Simulated WebSocket live chat incoming messages
    LaunchedEffect(Unit) {
        val simulatedUsers = listOf("AlexGamer", "Elena_FC", "ProSniper99", "NeymarFan", "DevNinja", "ChloeStream", "StadiumWave")
        val simulatedTexts = listOf(
            "WHAT A PLAY!! 🤯",
            "GOOOOOAAALLLL!!! 🔥⚽",
            "Insane curve on that ball!",
            "GG WP everyone in chat!",
            "Can't believe this overtime",
            "Drop the stream key for discord!"
        )

        while (true) {
            delay(2400)
            val randomUser = simulatedUsers.random()
            val randomText = simulatedTexts.random()
            val isSuper = (1..10).random() == 1
            chatMessages.add(
                LiveChatMessage(
                    username = randomUser,
                    avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                    text = randomText,
                    isSuperChat = isSuper,
                    tipAmount = if (isSuper) 10.0 else 0.0,
                    badgeColor = if (isSuper) GoalAmber else GoalGreen
                )
            )
            viewerCount += (-5..15).random()
            scope.launch {
                listState.animateScrollToItem(chatMessages.size - 1)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Live Video Feed Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = stream.bannerUrl,
                    contentDescription = stream.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Top Overlay Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                            .testTag("live_room_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // LIVE badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(LiveRed)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "LIVE",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Viewer count
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0x88000000))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.People,
                                contentDescription = "Viewers",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$viewerCount",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Stream Info Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = stream.userAvatar,
                    contentDescription = stream.username,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stream.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = "@${stream.username} • ${stream.category}",
                        fontSize = 11.sp,
                        color = GoalCyan
                    )
                }

                // Tip Super Chat Button
                IconButton(
                    onClick = { showTipDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFB300))
                        .testTag("live_tip_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.MonetizationOn,
                        contentDescription = "Super Chat Tip",
                        tint = GoalAmber,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Live Chat Messages Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(DarkBg)
                    .padding(horizontal = 12.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatMessages, key = { it.id }) { msg ->
                        LiveChatBubble(message = msg)
                    }
                }
            }

            // Reaction Emojis quick tap row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("🔥", "⚽", "🚀", "❤️", "👏", "🎉").forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                            .clickable {
                                chatMessages.add(
                                    LiveChatMessage(
                                        username = "goal_creator",
                                        avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                                        text = emoji
                                    )
                                )
                                scope.launch {
                                    listState.animateScrollToItem(chatMessages.size - 1)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 16.sp)
                    }
                }
            }

            // Chat Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputChat,
                    onValueChange = { inputChat = it },
                    placeholder = { Text("Send a message in live chat...", color = TextTertiary, fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("live_chat_input"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = GoalGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputChat.isNotBlank()) {
                            chatMessages.add(
                                LiveChatMessage(
                                    username = "goal_creator",
                                    avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                                    text = inputChat.trim()
                                )
                            )
                            inputChat = ""
                            scope.launch {
                                listState.animateScrollToItem(chatMessages.size - 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (inputChat.isNotBlank()) GoalGreen else DarkSurfaceElevated)
                        .testTag("live_chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputChat.isNotBlank()) DarkBg else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Tip Super Chat Dialog
        if (showTipDialog) {
            TipDialog(
                creatorName = stream.username,
                onDismiss = { showTipDialog = false },
                onSendTip = { amount, msg ->
                    onSendTip(amount, msg)
                    chatMessages.add(
                        LiveChatMessage(
                            username = "goal_creator",
                            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                            text = if (msg.isNotBlank()) msg else "Sent a $$amount Super Chat tip! ⚽🔥",
                            isSuperChat = true,
                            tipAmount = amount,
                            badgeColor = GoalAmber
                        )
                    )
                    scope.launch {
                        listState.animateScrollToItem(chatMessages.size - 1)
                    }
                }
            )
        }
    }
}

@Composable
fun LiveChatBubble(message: LiveChatMessage) {
    if (message.isSuperChat) {
        // Highlighted Super Chat Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF261D05))
                .border(1.dp, GoalAmber, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = message.avatarUrl,
                            contentDescription = message.username,
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "@${message.username}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = GoalAmber
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(GoalAmber)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$${message.tipAmount.toInt()} Super Chat",
                            color = DarkBg,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    } else {
        // Standard Live Message
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = message.avatarUrl,
                contentDescription = message.username,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceElevated),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = message.username,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
