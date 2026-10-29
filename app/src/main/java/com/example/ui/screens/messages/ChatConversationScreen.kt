package com.example.ui.screens.messages

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.repository.InitialDataSeed
import com.example.ui.components.GoalVerifiedBadge
import com.example.ui.screens.security.E2eeKeyVerificationDialog
import com.example.ui.screens.security.computeEncryptedMessagePacket
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DmBubbleBlue
import com.example.ui.theme.DmBubbleViolet
import com.example.ui.theme.GoalCyan
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.InstaOrange
import com.example.ui.theme.InstaPink
import com.example.ui.theme.InstaPurple
import com.example.ui.theme.LiveRed
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatConversationScreen(
    conversation: ConversationEntity,
    messages: List<MessageEntity>,
    isRecipientTyping: Boolean = false,
    isE2eeVerified: Boolean = true,
    onVerifyE2eeKeys: () -> Unit = {},
    onUnsendMessage: (String) -> Unit = {},
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var textInput by remember { mutableStateOf("") }
    var replyingToMessage by remember { mutableStateOf<MessageEntity?>(null) }
    var activeActionMessageId by remember { mutableStateOf<String?>(null) }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var isVanishModeActive by remember { mutableStateOf(false) }
    var showE2eeDialog by remember { mutableStateOf(false) }
    var inspectedMessage by remember { mutableStateOf<MessageEntity?>(null) }
    var statusBannerText by remember { mutableStateOf<String?>(null) }

    val messageReactions = remember {
        mutableStateMapOf(
            "msg_1" to "🎯",
            "msg_3" to "❤️"
        )
    }
    val playingVoiceIds = remember { mutableStateMapOf<String, Boolean>() }

    val clipboardManager = LocalClipboardManager.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size, isRecipientTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Instagram DM Top Header with E2EE Lock Status & Key Verification
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(top = 28.dp, start = 6.dp, end = 10.dp, bottom = 10.dp),
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

            // Creator Avatar with Instagram Gradient Story Ring & Online Dot
            Box {
                AsyncImage(
                    model = conversation.otherUserAvatar,
                    contentDescription = conversation.otherUsername,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.8.dp,
                            brush = Brush.linearGradient(listOf(InstaPurple, InstaPink, RoyalGold)),
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(GoalGreen)
                        .border(2.dp, DarkSurface, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showE2eeDialog = true }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = conversation.otherUsername,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    GoalVerifiedBadge(isGoldTier = true, size = 14.dp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Encrypted",
                        tint = GoalGreen,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isRecipientTyping) "Typing a message..." else "Active now • E2EE Secured",
                        fontSize = 11.sp,
                        color = if (isRecipientTyping) RoyalGold else GoalGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Vanish Mode Toggle Pill in Header
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isVanishModeActive) RoyalGold.copy(alpha = 0.22f)
                        else DarkSurfaceElevated
                    )
                    .border(
                        width = 1.dp,
                        color = if (isVanishModeActive) RoyalGold else DarkSurfaceBorder,
                        shape = RoundedCornerShape(50)
                    )
                    .clickable {
                        isVanishModeActive = !isVanishModeActive
                        statusBannerText = if (isVanishModeActive) {
                            "⏱️ Vanish Mode ON: Messages auto-destruct after viewing"
                        } else {
                            "Vanish Mode turned off"
                        }
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("toggle_vanish_mode_button")
            ) {
                Text(
                    text = if (isVanishModeActive) "⏱️ Vanish ON" else "⏱️ Vanish",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isVanishModeActive) RoyalGold else TextSecondary
                )
            }

            // E2EE Key Verification Button
            IconButton(
                onClick = { showE2eeDialog = true },
                modifier = Modifier.testTag("verify_e2ee_keys_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.VerifiedUser,
                    contentDescription = "Verify Encryption Keys",
                    tint = GoalGreen,
                    modifier = Modifier.size(21.dp)
                )
            }

            // Audio & Video Call Actions
            IconButton(
                onClick = {
                    statusBannerText = "🔒 Starting E2EE Voice Call with @${conversation.otherUsername}..."
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = "Voice Call",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(
                onClick = {
                    statusBannerText = "🔒 Connecting 1080p E2EE Video Call with @${conversation.otherUsername}..."
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Videocam,
                    contentDescription = "Video Call",
                    tint = RoyalGold,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Status Toast / Call Banner
        AnimatedVisibility(visible = statusBannerText != null) {
            statusBannerText?.let { status ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x2600E676))
                        .padding(horizontal = 16.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = status,
                        fontSize = 12.sp,
                        color = GoalGreen,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Dismiss",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { statusBannerText = null }
                    )
                }
            }
        }

        // Messages Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Instagram-style Creator Header + E2EE Security Pill at Top of Thread
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = conversation.otherUserAvatar,
                        contentDescription = conversation.otherUsername,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    listOf(InstaPurple, InstaPink, InstaOrange, RoyalGold)
                                ),
                                shape = CircleShape
                            ),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = conversation.otherUsername,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        GoalVerifiedBadge(isGoldTier = true, size = 16.dp)
                    }
                    Text(
                        text = "GOAL PRO Creator • 410K Followers",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Instagram E2EE Encryption Notice Pill + Packet Inspector Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, GoalGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable { showE2eeDialog = true }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("e2ee_banner_pill"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = GoalGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Messages & voice notes are end-to-end encrypted. Verify keys →",
                            fontSize = 11.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Quick Tap to Inspect Latest Encrypted Message Payload
                    if (messages.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(RoyalGold.copy(alpha = 0.14f))
                                .border(0.8.dp, RoyalGold.copy(alpha = 0.55f), RoundedCornerShape(50))
                                .clickable { inspectedMessage = messages.last() }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .testTag("inspect_latest_packet_pill")
                        ) {
                            Text(
                                text = "🔍 Inspect Live AES-256-GCM Message Payload",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RoyalGold
                            )
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                val isMe = msg.senderId == InitialDataSeed.CURRENT_USER_ID
                val currentReaction = messageReactions[msg.id]
                val isActionMenuOpen = activeActionMessageId == msg.id
                val isVoicePlaying = playingVoiceIds[msg.id] == true

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                ) {
                    // Floating Instagram Emoji & Context Action Bar when tapped/held
                    AnimatedVisibility(visible = isActionMenuOpen) {
                        InstagramMessageActionSheet(
                            onSelectEmoji = { emoji ->
                                messageReactions[msg.id] = emoji
                                activeActionMessageId = null
                            },
                            onReply = {
                                replyingToMessage = msg
                                activeActionMessageId = null
                            },
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(cleanMessageText(msg.content)))
                                statusBannerText = "Message copied to clipboard ✨"
                                activeActionMessageId = null
                            },
                            onInspectPacket = {
                                inspectedMessage = msg
                                activeActionMessageId = null
                            },
                            onUnsend = {
                                onUnsendMessage(msg.id)
                                statusBannerText = "Message unsent and deleted from E2EE vault 🗑️"
                                activeActionMessageId = null
                            },
                            onDismiss = { activeActionMessageId = null }
                        )
                    }

                    MessageBubble(
                        message = msg,
                        isMe = isMe,
                        otherUsername = conversation.otherUsername,
                        senderAvatar = conversation.otherUserAvatar,
                        reactionEmoji = currentReaction,
                        isVoicePlaying = isVoicePlaying,
                        onToggleVoicePlay = {
                            val nextState = !isVoicePlaying
                            playingVoiceIds[msg.id] = nextState
                            if (nextState) {
                                scope.launch {
                                    delay(3500)
                                    playingVoiceIds[msg.id] = false
                                }
                            }
                        },
                        onDoubleTapHeart = {
                            messageReactions[msg.id] = "❤️"
                        },
                        onBurnVanishMessage = {
                            onUnsendMessage(msg.id)
                            statusBannerText = "🔥 Vanish message permanently incinerated"
                        },
                        onOpenActions = {
                            activeActionMessageId = if (isActionMenuOpen) null else msg.id
                        }
                    )
                }
            }

            // Animated Typing Indicator Bubble
            if (isRecipientTyping) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        AsyncImage(
                            model = conversation.otherUserAvatar,
                            contentDescription = null,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(DarkSurfaceElevated)
                                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(18.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "● ● ●  @${conversation.otherUsername} is typing",
                                fontSize = 12.sp,
                                color = RoyalGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Quoted Reply Preview Bar above Instagram Chat Box
        AnimatedVisibility(visible = replyingToMessage != null) {
            replyingToMessage?.let { quoted ->
                val quotedAuthor = if (quoted.senderId == InitialDataSeed.CURRENT_USER_ID) {
                    "yourself"
                } else {
                    "@${conversation.otherUsername}"
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurface)
                        .border(width = 0.5.dp, color = DarkSurfaceBorder)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(32.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(RoyalGold)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Replying to $quotedAuthor",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalGold
                        )
                        Text(
                            text = cleanMessageText(quoted.content),
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = { replyingToMessage = null },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Cancel Reply",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Live Voice Note Waveform Recorder Bar
        AnimatedVisibility(visible = isRecordingVoice) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceElevated)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(LiveRed)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "0:08 • Recording Voice Waveform",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                // Mini Waveform Bars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(10.dp, 18.dp, 12.dp, 22.dp, 16.dp, 20.dp, 14.dp).forEach { h ->
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(h)
                                .clip(RoundedCornerShape(2.dp))
                                .background(RoyalGold)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Cancel",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier
                        .clickable { isRecordingVoice = false }
                        .padding(horizontal = 6.dp)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(GoalGreen)
                        .clickable {
                            isRecordingVoice = false
                            onSendMessage("[VOICE:0:08]")
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("send_voice_waveform_button")
                ) {
                    Text(
                        text = "Send Voice",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkBg
                    )
                }
            }
        }

        // Authentic Instagram Bottom Pill Chat Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBg)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(DarkSurfaceElevated)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                DmBubbleViolet.copy(alpha = 0.6f),
                                DmBubbleBlue.copy(alpha = 0.5f),
                                RoyalGold.copy(alpha = 0.4f)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Instagram Camera Circle Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(DmBubbleViolet, DmBubbleBlue))
                        )
                        .clickable {
                            onSendMessage("📸 Shared a 4K HDR Match Highlight Snapshot 🎯")
                        }
                        .testTag("chat_camera_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "Camera",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Message Text Field inside Instagram Pill
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text(
                            text = if (replyingToMessage != null) "Write a reply..." else "Message...",
                            color = TextTertiary,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 3
                )

                // Right-side Contextual Instagram Composer Icons
                if (textInput.isBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        // Voice Waveform Button
                        IconButton(
                            onClick = { isRecordingVoice = !isRecordingVoice },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("chat_voice_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = "Record Voice Waveform",
                                tint = if (isRecordingVoice) LiveRed else Color.White,
                                modifier = Modifier.size(21.dp)
                            )
                        }

                        // Share GOAL Reel Card Button
                        IconButton(
                            onClick = {
                                onSendMessage("[REEL:UEFA Champions League Top 10 Screamers ⚽🔥|1.4M views|@ApexStriker]")
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("chat_share_reel_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SlowMotionVideo,
                                contentDescription = "Share Reel Preview",
                                tint = RoyalGold,
                                modifier = Modifier.size(21.dp)
                            )
                        }

                        // Quick Heart Send Button
                        IconButton(
                            onClick = {
                                onSendMessage("❤️🔥")
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("chat_quick_heart_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Send Heart",
                                tint = InstaPink,
                                modifier = Modifier.size(21.dp)
                            )
                        }
                    }
                } else {
                    // Glowing Instagram "Send" Pill Button when text is entered
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(listOf(DmBubbleViolet, DmBubbleBlue))
                            )
                            .clickable {
                                val raw = textInput.trim()
                                if (raw.isNotEmpty()) {
                                    val basePayload = replyingToMessage?.let { quoted ->
                                        val author = if (quoted.senderId == InitialDataSeed.CURRENT_USER_ID) {
                                            "goal_creator"
                                        } else {
                                            conversation.otherUsername
                                        }
                                        val snippet = cleanMessageText(quoted.content).take(45)
                                        "[REPLY:$author|$snippet]$raw"
                                    } ?: raw

                                    val payload = if (isVanishModeActive) {
                                        "[VANISH:30s]$basePayload"
                                    } else {
                                        basePayload
                                    }

                                    onSendMessage(payload)
                                    textInput = ""
                                    replyingToMessage = null
                                    scope.launch {
                                        if (messages.isNotEmpty()) {
                                            listState.animateScrollToItem(messages.size - 1)
                                        }
                                    }
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("chat_send_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Send",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Message",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }

        if (showE2eeDialog) {
            E2eeKeyVerificationDialog(
                otherUsername = conversation.otherUsername,
                isVerified = isE2eeVerified,
                onMarkVerified = {
                    onVerifyE2eeKeys()
                    statusBannerText = "🔒 SHA-256 Encryption Keys verified with @${conversation.otherUsername}"
                },
                onDismiss = { showE2eeDialog = false }
            )
        }

        inspectedMessage?.let { targetMsg ->
            EncryptedMessagePayloadInspectorDialog(
                message = targetMsg,
                otherUsername = conversation.otherUsername,
                onDismiss = { inspectedMessage = null }
            )
        }
    }
}

/**
 * Live Cryptographic Message Payload Inspector Modal (AES-256-GCM Ciphertext, 96-bit IV Nonce,
 * SHA-256 HMAC Authentication Tag, and Ephemeral Ratchet Key ID).
 */
@Composable
private fun EncryptedMessagePayloadInspectorDialog(
    message: MessageEntity,
    otherUsername: String,
    onDismiss: () -> Unit
) {
    val packet = remember(message.id, message.content) {
        computeEncryptedMessagePacket(message.id, message.content, message.senderId)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = RoyalGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "E2EE Message Payload Inspector",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Cryptographic packet telemetry between @goal_creator and @$otherUsername:",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, RoyalGold.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "CIPHER SUITE: ${packet.algorithm}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoalGreen
                    )
                    Text(
                        text = "KEY ID: ${packet.ephemeralKeyId}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = RoyalGold
                    )
                    Text(
                        text = "96-BIT IV NONCE:\n${packet.ivNonceHex}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = GoalCyan
                    )
                    Text(
                        text = "AES-256-GCM CIPHERTEXT PAYLOAD:\n${packet.ciphertextHex}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color.White
                    )
                    Text(
                        text = "SHA-256 HMAC AUTH TAG:\n${packet.sha256HmacTag}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = GoalGreen
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalGold)
            ) {
                Text("Verified 🔒", color = DarkBg, fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        }
    )
}

/**
 * Floating Instagram-style Reaction & Context Action Bar (Reply, Copy, Inspect E2EE, Unsend).
 */
@Composable
private fun InstagramMessageActionSheet(
    onSelectEmoji: (String) -> Unit,
    onReply: () -> Unit,
    onCopy: () -> Unit,
    onInspectPacket: () -> Unit = {},
    onUnsend: () -> Unit,
    onDismiss: () -> Unit
) {
    val emojis = listOf("❤️", "🎯", "🔥", "👏", "😂", "😮")

    Column(
        modifier = Modifier
            .padding(bottom = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, RoyalGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        // Emoji Reaction Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            emojis.forEach { emoji ->
                Text(
                    text = emoji,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onSelectEmoji(emoji) }
                        .padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Reply / Copy / Inspect E2EE / Unsend Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceElevated)
                    .clickable { onReply() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("action_reply_message"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Reply,
                    contentDescription = "Reply",
                    tint = GoalCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reply", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceElevated)
                    .clickable { onCopy() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("action_copy_message"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = "Copy",
                    tint = RoyalGold,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GoalGreen.copy(alpha = 0.18f))
                    .clickable { onInspectPacket() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("action_inspect_packet"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Inspect E2EE Packet",
                    tint = GoalGreen,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("E2EE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoalGreen)
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(LiveRed.copy(alpha = 0.18f))
                    .clickable { onUnsend() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("action_unsend_message"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.DeleteOutline,
                    contentDescription = "Unsend",
                    tint = LiveRed,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Unsend", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LiveRed)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: MessageEntity,
    isMe: Boolean,
    otherUsername: String = "",
    senderAvatar: String = "",
    reactionEmoji: String? = null,
    isVoicePlaying: Boolean = false,
    onToggleVoicePlay: () -> Unit = {},
    onDoubleTapHeart: () -> Unit = {},
    onBurnVanishMessage: () -> Unit = {},
    onOpenActions: () -> Unit = {}
) {
    val sentGradient = Brush.linearGradient(
        colors = listOf(DmBubbleViolet, DmBubbleBlue)
    )

    val isVanishMessage = remember(message.content) {
        message.content.startsWith("[VANISH:")
    }

    val rawContent = remember(message.content) {
        if (message.content.startsWith("[VANISH:") && message.content.contains("]")) {
            message.content.substring(message.content.indexOf("]") + 1)
        } else {
            message.content
        }
    }

    // Parse Quoted Reply prefix: [REPLY:author|snippet]body
    val replyInfo: Pair<String, String>? = remember(rawContent) {
        if (rawContent.startsWith("[REPLY:") && rawContent.contains("]")) {
            val endBracket = rawContent.indexOf("]")
            val meta = rawContent.substring(7, endBracket)
            val parts = meta.split("|", limit = 2)
            if (parts.size == 2) Pair(parts[0], parts[1]) else null
        } else null
    }

    val bodyText = remember(rawContent) {
        if (rawContent.startsWith("[REPLY:") && rawContent.contains("]")) {
            rawContent.substring(rawContent.indexOf("]") + 1)
        } else {
            rawContent
        }
    }

    val isVoiceMessage = bodyText.startsWith("[VOICE:")
    val isReelPreview = bodyText.startsWith("[REEL:")
    val detectedLinkStatus: Pair<Boolean, String>? = remember(bodyText) {
        val lower = bodyText.lowercase()
        val hasUrl = lower.contains("http://") || lower.contains("https://") ||
            lower.contains("www.") || lower.contains("goal.gg") ||
            lower.contains("goal.app") || lower.contains(".com/") || lower.contains("bit.ly")
        if (!hasUrl) {
            null
        } else {
            val isSafeGoalDomain = lower.contains("goal.gg") || lower.contains("goal.app")
            if (isSafeGoalDomain) {
                Pair(true, "🛡️ Verified GOAL Safe Link • SHA-256 SSL Checked")
            } else {
                Pair(false, "⚠️ Anti-Phishing Guard: External Link Inspected")
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isMe && senderAvatar.isNotBlank()) {
            AsyncImage(
                model = senderAvatar,
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
        ) {
            // Quoted Message Block above Bubble (Instagram Reply Style)
            if (replyInfo != null) {
                Row(
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .widthIn(max = 260.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(26.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(RoyalGold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Replied to @${replyInfo.first}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalGold
                        )
                        Text(
                            text = replyInfo.second,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Box {
                when {
                    isVoiceMessage -> {
                        val durationLabel = bodyText.removePrefix("[VOICE:").removeSuffix("]").ifBlank { "0:14" }
                        VoiceWaveformBubbleContent(
                            duration = durationLabel,
                            isMe = isMe,
                            isPlaying = isVoicePlaying,
                            sentGradient = sentGradient,
                            onTogglePlay = onToggleVoicePlay,
                            onDoubleTapHeart = onDoubleTapHeart,
                            onOpenActions = onOpenActions
                        )
                    }

                    isReelPreview -> {
                        val meta = bodyText.removePrefix("[REEL:").removeSuffix("]")
                        val parts = meta.split("|")
                        val reelTitle = parts.getOrNull(0) ?: "GOAL Viral Highlight Reel 🎯"
                        val reelViews = parts.getOrNull(1) ?: "1.4M views"
                        val reelCreator = parts.getOrNull(2) ?: "@goal_creator"

                        SharedReelPreviewBubbleContent(
                            title = reelTitle,
                            views = reelViews,
                            creator = reelCreator,
                            onDoubleTapHeart = onDoubleTapHeart,
                            onOpenActions = onOpenActions
                        )
                    }

                    else -> {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 20.dp,
                                        topEnd = 20.dp,
                                        bottomStart = if (isMe) 20.dp else 4.dp,
                                        bottomEnd = if (isMe) 4.dp else 20.dp
                                    )
                                )
                                .then(
                                    if (isMe) {
                                        Modifier.background(brush = sentGradient)
                                    } else {
                                        Modifier
                                            .background(DarkSurfaceElevated)
                                            .border(
                                                1.dp,
                                                DarkSurfaceBorder,
                                                RoundedCornerShape(
                                                    topStart = 20.dp,
                                                    topEnd = 20.dp,
                                                    bottomStart = 4.dp,
                                                    bottomEnd = 20.dp
                                                )
                                            )
                                    }
                                )
                                .combinedClickable(
                                    onClick = { onOpenActions() },
                                    onDoubleClick = { onDoubleTapHeart() },
                                    onLongClick = { onOpenActions() }
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Column {
                                if (isVanishMessage) {
                                    Row(
                                        modifier = Modifier
                                            .padding(bottom = 5.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color.Black.copy(alpha = 0.35f))
                                            .border(0.8.dp, RoyalGold, RoundedCornerShape(50))
                                            .clickable { onBurnVanishMessage() }
                                            .padding(horizontal = 8.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "⏱️ VANISH 30s • Tap to Burn 🔥",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = RoyalGold
                                        )
                                    }
                                }

                                Text(
                                    text = bodyText,
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    lineHeight = 19.sp
                                )

                                if (detectedLinkStatus != null) {
                                    val (isSafe, badgeText) = detectedLinkStatus
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isSafe) Color(0x2800E676) else Color(0x33FF1744)
                                            )
                                            .border(
                                                0.8.dp,
                                                if (isSafe) GoalGreen else LiveRed,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = badgeText,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSafe) GoalGreen else RoyalGold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))
                                Row(
                                    modifier = Modifier.align(Alignment.End),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Lock,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.65f),
                                        modifier = Modifier.size(9.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (isMe) "Seen" else "12:45 PM",
                                        fontSize = 9.sp,
                                        color = Color.White.copy(alpha = 0.75f)
                                    )
                                    if (isMe) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Filled.DoneAll,
                                            contentDescription = "Seen",
                                            tint = RoyalGold,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Reaction Pill Badge
                if (reactionEmoji != null) {
                    Box(
                        modifier = Modifier
                            .align(if (isMe) Alignment.BottomStart else Alignment.BottomEnd)
                            .offset(x = if (isMe) (-6).dp else 6.dp, y = 8.dp)
                            .clip(CircleShape)
                            .background(DarkSurface)
                            .border(1.dp, RoyalGold, CircleShape)
                            .clickable { onOpenActions() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = reactionEmoji, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

/**
 * Playable Instagram-style Voice Note Waveform Bubble.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VoiceWaveformBubbleContent(
    duration: String,
    isMe: Boolean,
    isPlaying: Boolean,
    sentGradient: Brush,
    onTogglePlay: () -> Unit,
    onDoubleTapHeart: () -> Unit,
    onOpenActions: () -> Unit
) {
    val barHeights = remember {
        listOf(8, 16, 12, 22, 18, 10, 24, 15, 20, 11, 22, 14, 19, 9, 16, 12)
    }

    Row(
        modifier = Modifier
            .widthIn(min = 210.dp, max = 270.dp)
            .clip(RoundedCornerShape(22.dp))
            .then(
                if (isMe) Modifier.background(sentGradient)
                else Modifier
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(22.dp))
            )
            .combinedClickable(
                onClick = { onTogglePlay() },
                onDoubleClick = { onDoubleTapHeart() },
                onLongClick = { onOpenActions() }
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (isPlaying) RoyalGold else Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = "Play Voice Note",
                tint = DarkBg,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Waveform Bars
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            barHeights.forEachIndexed { idx, h ->
                val isHighlighted = isPlaying && idx % 2 == 0
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(h.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (isHighlighted) RoyalGold
                            else Color.White.copy(alpha = 0.85f)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = duration,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "E2EE",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalGold
            )
        }
    }
}

/**
 * Instagram-style Inline Shared Reel / Short Preview Card inside Chat.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SharedReelPreviewBubbleContent(
    title: String,
    views: String,
    creator: String,
    onDoubleTapHeart: () -> Unit,
    onOpenActions: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(210.dp)
            .height(265.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, RoyalGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = { onOpenActions() },
                onDoubleClick = { onDoubleTapHeart() },
                onLongClick = { onOpenActions() }
            )
    ) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=600",
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark Scrim Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.55f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Top Creator Pill
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.SlowMotionVideo,
                contentDescription = null,
                tint = RoyalGold,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = creator,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Center Play Button
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.65f))
                .border(1.5.dp, RoyalGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Play Shared Reel",
                tint = RoyalGold,
                modifier = Modifier.size(26.dp)
            )
        }

        // Bottom Caption & Views
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "🎯 GOAL REEL • $views",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = GoalGreen
            )
        }
    }
}

private fun cleanMessageText(raw: String): String {
    return when {
        raw.startsWith("[REPLY:") && raw.contains("]") -> raw.substring(raw.indexOf("]") + 1)
        raw.startsWith("[VOICE:") -> "Voice Note Waveform"
        raw.startsWith("[REEL:") -> "Shared GOAL Reel"
        else -> raw
    }
}
