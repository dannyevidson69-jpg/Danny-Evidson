# Restore Clean Earlier GOAL Version (Revert Premium Gold & Extra Security Locks)

We will remove the changes introduced by the *"Strong and powerful security and protection provided is goal app. Goal app ke ander primium look hona chahiye"* updates and restore the clean, earlier **GOAL** design (`phele wala`).

---

## 1. What Will Be Removed / Reverted

1. **Home Feed & Top App Bar Cleanup (`HomeScreen.kt` & `GoalTopAppBar.kt`)**:
   - Remove the **Fortress Security Lounge Card**, **Stealth Toggle**, and **VIP Creators Strip** (`LuxurySecurityAndVipHeader`) from the Home feed.
   - Remove the **Quick App Lock** button, **Full-Screen App Security Vault Gate Overlay**, and `FORTRESS E2EE` subtitle from the Top App Bar.
   - Restore the clean **GOAL Top App Bar** with the `🎯` Target-G logo, `GOAL` title, Search button, Direct Messages inbox button (with unread badge), and Notifications icon.
   - Restore the clean **Electric Emerald (`GoalGreen`)** category filter chips on the Home screen.

2. **Restore Original Clean Video Cards (`GoalVideoCard.kt`)**:
   - Remove the gold hairline borders, `4K HDR` gold badges, `DRM GUARD` pills, and `🎯 DRM-SHA256` watermark overlays.
   - Restore the clean 16:9 video thumbnail card with category tag, duration pill, play button overlay, creator avatar, verified badge, view count, and Like/Save/Tip actions.

3. **Restore Original Clean Bottom Navigation Bar (`GoalBottomNav.kt`)**:
   - Remove the gold top border and gold active states.
   - Restore the clean dark surface bottom navigation bar with **Electric Emerald (`GoalGreen`)** active tab indicators and clean central `+` creation button.

4. **Remove Extra Security Clutter (`MainActivity.kt`, `ChatConversationScreen.kt`, `AnalyticsDashboardScreen.kt`, `MessagesListScreen.kt`)**:
   - Remove the full-screen App Lock Gate, Decoy PIN mode, 3-strike Intruder lockout alarm, Vanish mode header pill, Packet Inspector chip, Stealth balance blur, and IBAN whitelist clutter.
   - Keep the clean **Instagram-style Direct Messages Inbox & Pill Chat Box** (voice waveforms, reply quotes, shared Reel previews, double-tap heart, E2EE key verification) and the **Professional Creator Profile with Separate Analytics Dashboard**.

---

## 2. Files to Update
- `/app/src/main/java/com/example/ui/components/GoalTopAppBar.kt` — Restore clean GOAL header.
- `/app/src/main/java/com/example/ui/components/GoalBottomNav.kt` — Restore original clean bottom navigation bar.
- `/app/src/main/java/com/example/ui/components/GoalVideoCard.kt` — Restore original clean video card layout.
- `/app/src/main/java/com/example/ui/screens/home/HomeScreen.kt` — Remove security header & VIP strip; restore clean Home feed.
- `/app/src/main/java/com/example/ui/screens/messages/ChatConversationScreen.kt` & `MessagesListScreen.kt` — Remove Vanish mode, packet inspector chip, and decoy mode; keep clean Instagram DMs.
- `/app/src/main/java/com/example/ui/screens/profile/AnalyticsDashboardScreen.kt` & `ProfileAndStudioScreen.kt` — Restore clean Creator Wallet & Analytics without stealth blur or IBAN lock clutter.
- `/app/src/main/java/com/example/MainActivity.kt` & `GoalViewModel.kt` — Remove `FLAG_SECURE` window lock and full-screen app gate overlay.
