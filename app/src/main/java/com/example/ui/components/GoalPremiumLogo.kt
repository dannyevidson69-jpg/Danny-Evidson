package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DarkBg
import com.example.ui.theme.GoalCyan
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldDeep
import com.example.ui.theme.RoyalGoldLight

/**
 * Flagship GOAL Target-Bullseye "G" Monogram Logo (🎯 + G + Play).
 * Renders a precision concentric Royal Gold target bullseye integrated with a sleek 'G'
 * crossbar and an Electric Emerald play crystal in the center.
 */
@Composable
fun GoalPremiumLogo(
    size: Dp = 38.dp,
    animated: Boolean = true,
    useRasterEmblem: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "goal_logo_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (animated) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = if (animated) 0.65f else 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val goldBrush = Brush.linearGradient(
        colors = listOf(RoyalGoldLight, RoyalGold, RoyalGoldDeep, RoyalGold)
    )
    val emeraldBrush = Brush.linearGradient(
        colors = listOf(GoalGreen, GoalCyan)
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(pulseScale),
        contentAlignment = Alignment.Center
    ) {
        if (useRasterEmblem) {
            Image(
                painter = painterResource(id = R.drawable.img_goal_premium_logo_1793257804922),
                contentDescription = "GOAL Premium Target-G Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .border(1.5.dp, goldBrush, CircleShape)
            )
        } else {
            Canvas(modifier = Modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val center = Offset(w / 2f, h / 2f)
                val maxRadius = w / 2f

                // Deep Obsidian Circular Base
                drawCircle(
                    color = DarkBg,
                    radius = maxRadius,
                    center = center
                )

                // Subtle Ambient Royal Gold Halo
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GoalGreen.copy(alpha = glowAlpha * 0.6f),
                            RoyalGold.copy(alpha = glowAlpha * 0.35f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = maxRadius
                    ),
                    radius = maxRadius,
                    center = center
                )

                // Outer Bullseye Target Ring (Concentric Ring 1 - G Outer Arc)
                val outerStroke = w * 0.085f
                drawArc(
                    brush = goldBrush,
                    startAngle = 32f,
                    sweepAngle = 305f,
                    useCenter = false,
                    topLeft = Offset(outerStroke, outerStroke),
                    size = Size(w - outerStroke * 2, h - outerStroke * 2),
                    style = Stroke(width = outerStroke, cap = StrokeCap.Round)
                )

                // G Horizontal Bullseye Crossbar
                drawLine(
                    brush = goldBrush,
                    start = Offset(w * 0.54f, h * 0.50f),
                    end = Offset(w - outerStroke * 0.95f, h * 0.50f),
                    strokeWidth = outerStroke * 0.95f,
                    cap = StrokeCap.Round
                )

                // Concentric Bullseye Ring 2 (Middle Target Ring)
                val midInset = w * 0.23f
                val midStroke = w * 0.065f
                drawArc(
                    brush = goldBrush,
                    startAngle = 20f,
                    sweepAngle = 320f,
                    useCenter = false,
                    topLeft = Offset(midInset, midInset),
                    size = Size(w - midInset * 2, h - midInset * 2),
                    style = Stroke(width = midStroke, cap = StrokeCap.Round)
                )

                // Precision Target Crosshair Ticks (North, South, West)
                val tickLength = w * 0.08f
                val tickStroke = w * 0.045f
                // Top tick
                drawLine(
                    brush = goldBrush,
                    start = Offset(center.x, outerStroke * 0.8f),
                    end = Offset(center.x, outerStroke * 0.8f + tickLength),
                    strokeWidth = tickStroke,
                    cap = StrokeCap.Round
                )
                // Bottom tick
                drawLine(
                    brush = goldBrush,
                    start = Offset(center.x, h - outerStroke * 0.8f - tickLength),
                    end = Offset(center.x, h - outerStroke * 0.8f),
                    strokeWidth = tickStroke,
                    cap = StrokeCap.Round
                )
                // Left tick
                drawLine(
                    brush = goldBrush,
                    start = Offset(outerStroke * 0.8f, center.y),
                    end = Offset(outerStroke * 0.8f + tickLength, center.y),
                    strokeWidth = tickStroke,
                    cap = StrokeCap.Round
                )

                // Inner glowing Emerald ring around the Bullseye Core
                drawCircle(
                    brush = emeraldBrush,
                    radius = w * 0.19f,
                    center = center,
                    style = Stroke(width = w * 0.03f)
                )

                // Center Play Crystal Triangle (Bullseye Core)
                val playPath = Path().apply {
                    moveTo(w * 0.43f, h * 0.37f)
                    lineTo(w * 0.62f, h * 0.50f)
                    lineTo(w * 0.43f, h * 0.63f)
                    close()
                }
                drawPath(
                    path = playPath,
                    brush = emeraldBrush
                )
            }
        }
    }
}

/**
 * Verified Creator Seal Badge with Gold / Emerald styling.
 */
@Composable
fun GoalVerifiedBadge(
    isGoldTier: Boolean = true,
    size: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = Icons.Filled.Verified,
        contentDescription = "Verified GOAL Creator",
        tint = if (isGoldTier) RoyalGold else GoalCyan,
        modifier = modifier.size(size)
    )
}

/**
 * Luxury PRO Pill Badge displayed next to the GOAL wordmark or creator handle.
 */
@Composable
fun GoalProPillBadge(
    text: String = "PRO",
    modifier: Modifier = Modifier
) {
    val goldGradient = Brush.linearGradient(
        colors = listOf(RoyalGoldLight, RoyalGold, RoyalGoldDeep)
    )
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0x26FFD700))
            .border(1.dp, goldGradient, RoundedCornerShape(50))
            .padding(horizontal = 7.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🎯",
            fontSize = 8.sp
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.8.sp,
            color = RoyalGold
        )
    }
}
