package com.example.data.repository

import com.example.data.local.entity.CommentEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.CreatorBalanceEntity
import com.example.data.local.entity.LiveStreamEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VideoEntity

object InitialDataSeed {
    const val CURRENT_USER_ID = "usr_current_user_101"
    const val CURRENT_USERNAME = "goal_creator"

    fun getInitialVideos(): List<VideoEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            VideoEntity(
                id = "vid_std_1",
                userId = "usr_striker_9",
                username = "ApexStriker",
                userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                isVerified = true,
                title = "UEFA Champions League: Top 10 Screamer Goals of the Season 2026",
                description = "Relive the absolute best long-range strikes, curling freekicks, and volleys from the group stage to the epic final in London!",
                type = "standard",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=800",
                viewsCount = 1420500,
                likesCount = 89400,
                commentsCount = 2840,
                durationSeconds = 642, // 10:42
                category = "Football",
                createdAt = now - 3600000 * 6,
                isLiked = true,
                isSaved = true
            ),
            VideoEntity(
                id = "vid_std_2",
                userId = "usr_cyber_dev",
                username = "CyberEngineLab",
                userAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
                isVerified = true,
                title = "Unreal Engine 5.5 Photoreal Stadium Tech Demo 8K 60FPS",
                description = "Breaking down our custom dynamic crowd rendering and volumetric grass physics engine built for modern next-gen gaming consoles.",
                type = "standard",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800",
                viewsCount = 780400,
                likesCount = 54100,
                commentsCount = 1120,
                durationSeconds = 854, // 14:14
                category = "Gaming",
                createdAt = now - 3600000 * 18
            ),
            VideoEntity(
                id = "vid_std_3",
                userId = "usr_tactics_pro",
                username = "TikiTakaTactics",
                userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                isVerified = false,
                title = "The Inverted Fullback Revolution: Masterclass Tactical Breakdown",
                description = "How top managers in Europe are overloading the midfield with inverted fullbacks and creating numerical superiority in the half-spaces.",
                type = "standard",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1431324155629-1a6deb1dec8d?w=800",
                viewsCount = 312000,
                likesCount = 27800,
                commentsCount = 890,
                durationSeconds = 720, // 12:00
                category = "Tactics",
                createdAt = now - 3600000 * 36
            ),
            VideoEntity(
                id = "vid_std_4",
                userId = "usr_esports_prime",
                username = "ESportsPrime",
                userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                isVerified = true,
                title = "Global Invitational 2026 Finals: Highlights & Winning Moment",
                description = "All the pulse-pounding overtime rounds and game-winning snipes that decided the $2,000,000 champion.",
                type = "standard",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800",
                viewsCount = 945000,
                likesCount = 76200,
                commentsCount = 3410,
                durationSeconds = 1120,
                category = "Esports",
                createdAt = now - 3600000 * 48
            ),
            // Shorts
            VideoEntity(
                id = "vid_sh_1",
                userId = "usr_freestyle_king",
                username = "FreestyleKing",
                userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                isVerified = true,
                title = "Wait for that crazy curving outside boot shot! 🤯⚽",
                description = "Physics took a vacation on this angle! Could your keeper save this? Let me know in comments 👇",
                type = "short",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=800",
                viewsCount = 3890000,
                likesCount = 284500,
                commentsCount = 4820,
                durationSeconds = 24,
                category = "Skills",
                createdAt = now - 3600000 * 2,
                audioTrackTitle = "Beat of the Stadium (Original GOAL Mix)",
                hashtags = "#goal #football #curler #impossible #skills"
            ),
            VideoEntity(
                id = "vid_sh_2",
                userId = "usr_clutch_god",
                username = "ClutchGod",
                userAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                isVerified = true,
                title = "1 HP 1v4 Clutch to win the tournament match point!! 🎯🔥",
                description = "My hands were literally shaking after this round. Greatest play of my competitive career.",
                type = "short",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800",
                viewsCount = 1940000,
                likesCount = 189200,
                commentsCount = 3120,
                durationSeconds = 38,
                category = "Gaming",
                createdAt = now - 3600000 * 12,
                audioTrackTitle = "Adrenaline Rush Trap Beat #9",
                hashtags = "#gaming #clutch #esports #1hp #epicwin"
            ),
            VideoEntity(
                id = "vid_sh_3",
                userId = "usr_drone_pilot",
                username = "AeroFPV",
                userAvatar = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=150",
                isVerified = false,
                title = "Speed diving through the roof of a 90,000 seat stadium! 🏎️💨",
                description = "Full manual acro drone dive at 140km/h directly between the crossbar and floodlights!",
                type = "short",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackSeeTheWorld.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800",
                viewsCount = 4210000,
                likesCount = 412000,
                commentsCount = 6540,
                durationSeconds = 29,
                category = "Extreme",
                createdAt = now - 3600000 * 20,
                audioTrackTitle = "Cyber Neon Drift (Synthwave)",
                hashtags = "#fpv #drone #stadium #speed #rush"
            ),
            VideoEntity(
                id = "vid_sh_4",
                userId = "usr_edit_wizard",
                username = "MotionFX_Studio",
                userAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150",
                isVerified = true,
                title = "3 Smooth Motion Tracking Transitions You Need To Know! ✂️⚡",
                description = "Level up your GOAL video shorts using these free optical flow techniques!",
                type = "short",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=800",
                viewsCount = 890000,
                likesCount = 92000,
                commentsCount = 1420,
                durationSeconds = 45,
                category = "Creator",
                createdAt = now - 3600000 * 30,
                audioTrackTitle = "Tutorial LoFi Beats #3",
                hashtags = "#creator #videoedit #tutorial #transitions"
            )
        )
    }

    fun getInitialComments(): List<CommentEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            CommentEntity(
                id = "comm_1",
                videoId = "vid_std_1",
                userId = "usr_fan_1",
                username = "StrikerFan2026",
                userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                content = "That number 3 goal defied all laws of aerodynamics! Pure magic 🔥",
                createdAt = now - 1800000,
                likesCount = 342,
                isLiked = true
            ),
            CommentEntity(
                id = "comm_2",
                videoId = "vid_std_1",
                userId = "usr_fan_2",
                username = "TacticalBeast",
                userAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
                content = "GOAL platform's video bitrate and 60fps clarity is insane. Great recap video!",
                createdAt = now - 3600000,
                likesCount = 128
            ),
            CommentEntity(
                id = "comm_3",
                videoId = "vid_sh_1",
                userId = "usr_fan_3",
                username = "Roberto_Curl",
                userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                content = "Roberto Carlos would be proud of that curl! 🌪️⚽",
                createdAt = now - 900000,
                likesCount = 894,
                isLiked = true
            ),
            CommentEntity(
                id = "comm_4",
                videoId = "vid_sh_1",
                userId = "usr_fan_4",
                username = "GoalkeeperNightmare",
                userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                content = "As a goalkeeper, this gives me sleepless nights haha",
                createdAt = now - 2400000,
                likesCount = 210
            )
        )
    }

    fun getInitialLiveStreams(): List<LiveStreamEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            LiveStreamEntity(
                id = "live_1",
                userId = "usr_goal_esports",
                username = "GOAL_ESports_Live",
                userAvatar = "https://images.unsplash.com/photo-1566492031773-4f4e44671857?w=150",
                title = "FIFA Global Cup Grand Finals - LIVE Broadcast from Tokyo Dome",
                streamKey = "live_sk_7721_tokyo_goal_live",
                status = "live",
                viewerCount = 38450,
                startedAt = now - 3600000 * 2,
                bannerUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800",
                category = "Football"
            ),
            LiveStreamEntity(
                id = "live_2",
                userId = "usr_pro_striker",
                username = "Marcus_Apex",
                userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                title = "Rank 1 Predator Grind! Drops & Super Chat Tips Active 🚀",
                streamKey = "live_sk_8829_apex_predator",
                status = "live",
                viewerCount = 14820,
                startedAt = now - 3600000,
                bannerUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800",
                category = "Gaming"
            ),
            LiveStreamEntity(
                id = "live_3",
                userId = "usr_dj_stream",
                username = "CyberRhythm",
                userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                title = "Late Night Electronic Vibes + Stadium Anthem Beats Live Set",
                streamKey = "live_sk_9910_dj_cyber",
                status = "live",
                viewerCount = 6930,
                startedAt = now - 5400000,
                bannerUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=800",
                category = "Music"
            ),
            LiveStreamEntity(
                id = "live_4",
                userId = "usr_tactics_pro",
                username = "TikiTakaTactics",
                userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                title = "Post-Match Tactical Analysis & Q&A with Pro Coaches",
                streamKey = "live_sk_1102_tactics_qa",
                status = "scheduled",
                viewerCount = 1420,
                startedAt = now + 7200000,
                bannerUrl = "https://images.unsplash.com/photo-1431324155629-1a6deb1dec8d?w=800",
                category = "Tactics"
            )
        )
    }

    fun getInitialConversations(): List<ConversationEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            ConversationEntity(
                id = "conv_1",
                otherUserId = "usr_striker_9",
                otherUsername = "ApexStriker",
                otherUserAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                lastMessage = "Bro that highlights edit on GOAL reached 1M views! 🔥",
                updatedAt = now - 180000,
                unreadCount = 1
            ),
            ConversationEntity(
                id = "conv_2",
                otherUserId = "usr_freestyle_king",
                otherUsername = "FreestyleKing",
                otherUserAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                lastMessage = "Let's do a collab short this weekend in London.",
                updatedAt = now - 3600000,
                unreadCount = 0
            ),
            ConversationEntity(
                id = "conv_3",
                otherUserId = "usr_cyber_dev",
                otherUsername = "CyberEngineLab",
                otherUserAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
                lastMessage = "Transcoding pipeline output looks super sharp at 1080p60!",
                updatedAt = now - 86400000,
                unreadCount = 0
            )
        )
    }

    fun getInitialMessages(): List<MessageEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            MessageEntity(
                id = "msg_1",
                conversationId = "conv_1",
                senderId = "usr_striker_9",
                receiverId = CURRENT_USER_ID,
                content = "Hey man, loved your latest breakdown video!",
                isRead = true,
                createdAt = now - 3600000
            ),
            MessageEntity(
                id = "msg_2",
                conversationId = "conv_1",
                senderId = CURRENT_USER_ID,
                receiverId = "usr_striker_9",
                content = "Thanks! The community feedback has been incredible.",
                isRead = true,
                createdAt = now - 1800000
            ),
            MessageEntity(
                id = "msg_3",
                conversationId = "conv_1",
                senderId = "usr_striker_9",
                receiverId = CURRENT_USER_ID,
                content = "Bro that highlights edit on GOAL reached 1M views! 🔥",
                isRead = false,
                createdAt = now - 180000
            )
        )
    }

    fun getInitialBalance(): CreatorBalanceEntity {
        return CreatorBalanceEntity(
            userId = CURRENT_USER_ID,
            availableBalance = 4850.75,
            pendingBalance = 1240.00,
            currency = "USD"
        )
    }

    fun getInitialTransactions(): List<TransactionEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            TransactionEntity(
                id = "tx_1",
                userId = CURRENT_USER_ID,
                amount = 250.00,
                type = "tip",
                status = "completed",
                title = "Super Chat Tip from @TacticalBeast during Finals stream",
                createdAt = now - 3600000 * 2
            ),
            TransactionEntity(
                id = "tx_2",
                userId = CURRENT_USER_ID,
                amount = 1450.50,
                type = "subscription",
                status = "completed",
                title = "Monthly Channel Member Subscriptions (290 Members)",
                createdAt = now - 86400000 * 2
            ),
            TransactionEntity(
                id = "tx_3",
                userId = CURRENT_USER_ID,
                amount = 100.00,
                type = "tip",
                status = "completed",
                title = "Goal Boost Tip from @Roberto_Curl on Viral Short",
                createdAt = now - 86400000 * 4
            ),
            TransactionEntity(
                id = "tx_4",
                userId = CURRENT_USER_ID,
                amount = 2500.00,
                type = "payout",
                status = "completed",
                title = "Payout to Bank Account (****4892)",
                createdAt = now - 86400000 * 10
            )
        )
    }

    fun getInitialUsers(): List<UserEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            UserEntity(
                id = CURRENT_USER_ID,
                username = CURRENT_USERNAME,
                email = "creator@goal.platform",
                passwordHash = "argon2id_hash_placeholder_1",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                bio = "Official GOAL Creator & Video Producer. Streaming live matches and tactical masterclasses.",
                isCreator = true,
                createdAt = now - 86400000L * 90
            ),
            UserEntity(
                id = "usr_striker_9",
                username = "ApexStriker",
                email = "striker@goal.platform",
                passwordHash = "argon2id_hash_placeholder_2",
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                bio = "Football analyst and highlight junkie. Top bins only! ⚽🎯",
                isCreator = true,
                createdAt = now - 86400000L * 120
            ),
            UserEntity(
                id = "usr_cyber_dev",
                username = "TechGoalie",
                email = "techgoalie@goal.platform",
                passwordHash = "argon2id_hash_placeholder_3",
                avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
                bio = "Sports tech engineer exploring high-framerate motion tracking in esports.",
                isCreator = true,
                createdAt = now - 86400000L * 60
            ),
            UserEntity(
                id = "usr_fan_alex",
                username = "Alex_Midfielder",
                email = "alex@goal.platform",
                passwordHash = "argon2id_hash_placeholder_4",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                bio = "Tactics nerd & weekend footballer.",
                isCreator = false,
                createdAt = now - 86400000L * 30
            )
        )
    }
}
