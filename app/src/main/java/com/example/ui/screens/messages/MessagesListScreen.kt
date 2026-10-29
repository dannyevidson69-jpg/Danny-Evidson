package com.example.ui.screens.messages

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.ConversationEntity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import com.example.ui.components.GoalPremiumLogo
import com.example.ui.components.GoalVerifiedBadge
import com.example.ui.screens.security.PinLockVaultDialog
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DmBubbleBlue
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

private data class CreatorNoteItem(
    val username: String,
    val avatarUrl: String,
    val noteText: String,
    val isOnline: Boolean = true,
    val conversationId: String? = null
)

@Composable
fun MessagesListScreen(
    conversations: List<ConversationEntity>,
    myNoteText: String = "Streaming UCL Finals 🎯🔥",
    onUpdateMyNote: (String) -> Unit = {},
    vaultPin: String = "1234",
    decoyPin: String = "9999",
    isDecoyModeActive: Boolean = false,
    isDmPinEnabled: Boolean = true,
    isVaultUnlocked: Boolean = false,
    onUnlockVault: () -> Unit = {},
    onDecoyUnlock: () -> Unit = {},
    onIntruderAttempt: () -> Unit = {},
    onOpenSecurityShield: () -> Unit = {},
    onSelectConversation: (ConversationEntity) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryTab by remember { mutableIntStateOf(0) } // 0: Primary, 1: General, 2: Locked Vault
    var showEditNoteDialog by remember { mutableStateOf(false) }
    var showPinLockModal by remember { mutableStateOf(false) }
    var pendingLockedConv by remember { mutableStateOf<ConversationEntity?>(null) }

    val creatorNotes = remember(conversations) {
        listOf(
            CreatorNoteItem(
                username = "ApexStriker",
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                noteText = "1M Views! 🔥🎯",
                isOnline = true,
                conversationId = "conv_1"
            ),
            CreatorNoteItem(
                username = "FreestyleKing",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                noteText = "Wembley Collab ⚽",
                isOnline = true,
                conversationId = "conv_2"
            ),
            CreatorNoteItem(
                username = "CyberEngineLab",
                avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
                noteText = "UE5.5 8K Demo 🎮",
                isOnline = true,
                conversationId = "conv_3"
            ),
            CreatorNoteItem(
                username = "ESportsPrime",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                noteText = "Finals Tonight 🏆",
                isOnline = true,
                conversationId = "conv_4"
            ),
            CreatorNoteItem(
                username = "MotionFX",
                avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150",
                noteText = "New Gold LUT ✨",
                isOnline = false,
                conversationId = "conv_5"
            )
        )
    }

    val filteredConversations = remember(conversations, searchQuery, selectedCategoryTab, isDecoyModeActive) {
        val bySearch = if (searchQuery.isBlank()) {
            conversations
        } else {
            conversations.filter {
                it.otherUsername.contains(searchQuery, ignoreCase = true) ||
                    it.lastMessage.contains(searchQuery, ignoreCase = true)
            }
        }
        when (selectedCategoryTab) {
            0 -> if (isDecoyModeActive) bySearch.filterIndexed { index, _ -> index >= 2 } else bySearch
            1 -> bySearch.filterIndexed { index, _ -> index >= 2 }
            else -> if (isDecoyModeActive) emptyList() else bySearch.take(2)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Instagram-style Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, start = 8.dp, end = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("messages_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                GoalPremiumLogo(size = 26.dp, animated = false)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "goal_creator",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                GoalVerifiedBadge(isGoldTier = true, size = 16.dp)
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Switch Account",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onOpenSecurityShield,
                modifier = Modifier.testTag("open_security_shield_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = "Security Shield Center",
                    tint = GoalGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(
                onClick = { showPinLockModal = true },
                modifier = Modifier.testTag("dm_pin_lock_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Vault PIN Lock",
                    tint = RoyalGold,
                    modifier = Modifier.size(21.dp)
                )
            }
            IconButton(onClick = { showEditNoteDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.EditNote,
                    contentDescription = "New Message or Note",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Instagram-style Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search Direct Messages or creators...",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search DMs",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated,
                    focusedBorderColor = RoyalGold.copy(alpha = 0.6f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dm_search_bar")
            )
        }

        // Instagram Notes & Active Now Carousel
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Current User's Note Item
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(76.dp)
                        .clickable { showEditNoteDialog = true }
                        .testTag("my_creator_note_button")
                ) {
                    Box(contentAlignment = Alignment.TopCenter) {
                        // Avatar with '+' badge
                        Box(
                            modifier = Modifier
                                .padding(top = 18.dp)
                                .size(64.dp)
                        ) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                                contentDescription = "Your Note",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.linearGradient(listOf(RoyalGold, GoalGreen)),
                                        shape = CircleShape
                                    ),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(RoyalGold)
                                    .border(2.dp, DarkBg, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Update Note",
                                    tint = DarkBg,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }

                        // Floating Note Bubble
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceElevated)
                                .border(1.dp, RoyalGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = myNoteText,
                                fontSize = 9.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Your note",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }

            // Other Active Creators Notes
            items(creatorNotes) { note ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(76.dp)
                        .clickable {
                            val matchingConv = conversations.firstOrNull { it.id == note.conversationId }
                                ?: conversations.firstOrNull()
                            if (matchingConv != null) {
                                onSelectConversation(matchingConv)
                            }
                        }
                ) {
                    Box(contentAlignment = Alignment.TopCenter) {
                        Box(
                            modifier = Modifier
                                .padding(top = 18.dp)
                                .size(64.dp)
                        ) {
                            AsyncImage(
                                model = note.avatarUrl,
                                contentDescription = note.username,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.linearGradient(
                                            listOf(InstaPurple, InstaPink, InstaOrange, RoyalGold)
                                        ),
                                        shape = CircleShape
                                    )
                                    .padding(2.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            if (note.isOnline) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(15.dp)
                                        .clip(CircleShape)
                                        .background(GoalGreen)
                                        .border(2.5.dp, DarkBg, CircleShape)
                                )
                            }
                        }

                        // Floating Note Bubble
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceElevated)
                                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = note.noteText,
                                fontSize = 9.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = note.username,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Security Shield Banner inside DM Inbox
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, GoalGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .clickable { onOpenSecurityShield() }
                .padding(horizontal = 12.dp, vertical = 7.dp)
                .testTag("dm_security_shield_banner"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = null,
                    tint = GoalGreen,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "E2EE AES-256 & PIN Vault Protection Active",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Text(
                text = "Shield →",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RoyalGold
            )
        }

        // Instagram Primary / General / Locked Vault Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                "Primary (${conversations.size})",
                "General",
                "Locked 🔐"
            )
            tabs.forEachIndexed { index, label ->
                val isSelected = selectedCategoryTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) Color.White else DarkSurfaceElevated
                        )
                        .clickable {
                            if (index == 2 && isDmPinEnabled && !isVaultUnlocked) {
                                showPinLockModal = true
                            } else {
                                selectedCategoryTab = index
                            }
                        }
                        .padding(vertical = 8.dp)
                        .testTag("dm_tab_$index"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) DarkBg else TextSecondary
                    )
                }
            }
        }

        // Conversations List
        if (filteredConversations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Chat,
                        contentDescription = "No chats",
                        tint = TextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("No messages found", color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("conversations_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredConversations, key = { it.id }) { conv ->
                    ConversationItemCard(
                        conversation = conv,
                        onClick = { onSelectConversation(conv) }
                    )
                }
            }
        }

        // PIN Lock Vault Dialog
        if (showPinLockModal) {
            PinLockVaultDialog(
                title = "Unlock Private DM Vault 🔐",
                subtitle = "Enter your 4-digit Security PIN to access Locked Direct Messages",
                expectedPin = vaultPin,
                decoyPin = decoyPin,
                onUnlocked = {
                    onUnlockVault()
                    showPinLockModal = false
                    selectedCategoryTab = 2
                    pendingLockedConv?.let {
                        onSelectConversation(it)
                        pendingLockedConv = null
                    }
                },
                onDecoyUnlocked = {
                    onDecoyUnlock()
                },
                onIntruderAttempt = {
                    onIntruderAttempt()
                },
                onDismiss = {
                    showPinLockModal = false
                    pendingLockedConv = null
                }
            )
        }

        // Dialog to update user's own Instagram-style Note
        if (showEditNoteDialog) {
            var noteDraft by remember { mutableStateOf(myNoteText) }
            AlertDialog(
                onDismissRequest = { showEditNoteDialog = false },
                containerColor = DarkSurface,
                title = {
                    Text(
                        text = "Share a Creator Note 🎯",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 17.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Followers and creators in your DM inbox will see this floating thought bubble for 24 hours.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = noteDraft,
                            onValueChange = { if (it.length <= 40) noteDraft = it },
                            label = { Text("Your thought (max 40 chars)") },
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
                            onUpdateMyNote(noteDraft)
                            showEditNoteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold)
                    ) {
                        Text("Share Note", color = DarkBg, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditNoteDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
fun ConversationItemCard(
    conversation: ConversationEntity,
    onClick: () -> Unit
) {
    val hasUnread = conversation.unreadCount > 0
    val ringBrush = if (hasUnread) {
        Brush.linearGradient(listOf(InstaPurple, InstaPink, InstaOrange, RoyalGold))
    } else {
        Brush.linearGradient(listOf(DarkSurfaceBorder, DarkSurfaceBorder))
    }

    val relativeTime = when (conversation.id) {
        "conv_1" -> "2m"
        "conv_2" -> "18m"
        "conv_3" -> "2h"
        "conv_4" -> "6h"
        else -> "1d"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (hasUnread) DarkSurface else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 10.dp)
            .testTag("conversation_item_${conversation.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with Instagram Gradient Story Ring & Online Dot
        Box {
            AsyncImage(
                model = conversation.otherUserAvatar,
                contentDescription = conversation.otherUsername,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(2.dp, ringBrush, CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceElevated),
                contentScale = ContentScale.Crop
            )
            // Online Presence Indicator
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(15.dp)
                    .clip(CircleShape)
                    .background(GoalGreen)
                    .border(2.5.dp, DarkBg, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.otherUsername,
                    fontWeight = if (hasUnread) FontWeight.ExtraBold else FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                GoalVerifiedBadge(
                    isGoldTier = conversation.id == "conv_1" || conversation.id == "conv_2",
                    size = 14.dp
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = conversation.lastMessage,
                    fontSize = 13.sp,
                    color = if (hasUnread) Color.White else TextSecondary,
                    fontWeight = if (hasUnread) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    text = " • $relativeTime",
                    fontSize = 12.sp,
                    color = if (hasUnread) GoalCyan else TextTertiary
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (hasUnread) {
            Badge(
                containerColor = DmBubbleBlue,
                contentColor = Color.White
            ) {
                Text(
                    text = "${conversation.unreadCount}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Icon(
            imageVector = Icons.Filled.CameraAlt,
            contentDescription = "Send Snap Video",
            tint = if (hasUnread) Color.White else TextTertiary,
            modifier = Modifier.size(20.dp)
        )
    }
}
