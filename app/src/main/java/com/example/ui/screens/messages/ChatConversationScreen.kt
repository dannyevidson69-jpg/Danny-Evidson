package com.example.ui.screens.messages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.repository.InitialDataSeed
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import kotlinx.coroutines.launch

@Composable
fun ChatConversationScreen(
    conversation: ConversationEntity,
    messages: List<MessageEntity>,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(top = 28.dp, start = 8.dp, end = 16.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("chat_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            AsyncImage(
                model = conversation.otherUserAvatar,
                contentDescription = conversation.otherUsername,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "@${conversation.otherUsername}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Active now • Socket connected",
                    fontSize = 11.sp,
                    color = GoalGreen
                )
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isMe = msg.senderId == InitialDataSeed.CURRENT_USER_ID
                MessageBubble(message = msg, isMe = isMe)
            }
        }

        // Message Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Type a message...", color = TextTertiary, fontSize = 13.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated,
                    focusedBorderColor = GoalGreen,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        onSendMessage(textInput.trim())
                        textInput = ""
                        scope.launch {
                            if (messages.isNotEmpty()) {
                                listState.animateScrollToItem(messages.size - 1)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (textInput.isNotBlank()) GoalGreen else DarkSurfaceBorder)
                    .testTag("chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Message",
                    tint = if (textInput.isNotBlank()) DarkBg else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: MessageEntity,
    isMe: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isMe) 14.dp else 2.dp,
                        bottomEnd = if (isMe) 2.dp else 14.dp
                    )
                )
                .background(if (isMe) GoalGreen else DarkSurfaceElevated)
                .padding(horizontal = 14.dp, vertical = 9.dp)
        ) {
            Column {
                Text(
                    text = message.content,
                    fontSize = 13.sp,
                    color = if (isMe) DarkBg else TextPrimary,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "12:45 PM",
                        fontSize = 9.sp,
                        color = if (isMe) Color(0x99000000) else TextTertiary
                    )
                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.DoneAll,
                            contentDescription = "Delivered",
                            tint = Color(0x99000000),
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }
        }
    }
}
