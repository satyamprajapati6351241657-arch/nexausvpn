package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.VpnState
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepSpaceNavy
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.StatusConnected
import com.example.ui.theme.StatusConnecting
import com.example.ui.theme.StatusDisconnected
import com.example.ui.theme.StatusFailed

@Composable
fun VpnConnectButton(
    state: VpnState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate_connecting"
    )

    val activeColor = when (state) {
        VpnState.CONNECTED -> NeonEmerald
        VpnState.CONNECTING -> RadiantGold
        VpnState.CONNECTION_FAILED -> CrimsonRed
        VpnState.DISCONNECTED -> CyberCyan
    }

    Box(
        modifier = modifier
            .size(220.dp)
            .testTag("vpn_connect_button_container"),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing ring when connecting or connected
        if (state == VpnState.CONNECTING || state == VpnState.CONNECTED) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(if (state == VpnState.CONNECTING) pulseScale else 1.08f)
                    .clip(CircleShape)
                    .background(activeColor.copy(alpha = if (state == VpnState.CONNECTING) pulseAlpha else 0.12f))
            )
        }

        // Radar sweep ring when connecting
        if (state == VpnState.CONNECTING) {
            Canvas(modifier = Modifier.size(190.dp)) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            activeColor.copy(alpha = 0.2f),
                            activeColor
                        )
                    ),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }

        // Inner Main Power Button
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            activeColor.copy(alpha = 0.28f),
                            DeepSpaceNavy
                        )
                    )
                )
                .border(
                    width = 3.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            activeColor,
                            activeColor.copy(alpha = 0.4f)
                        )
                    ),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
                .testTag("vpn_main_power_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = when (state) {
                        VpnState.CONNECTED -> Icons.Default.Shield
                        else -> Icons.Default.PowerSettingsNew
                    },
                    contentDescription = "VPN Power Toggle",
                    tint = activeColor,
                    modifier = Modifier.size(54.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when (state) {
                        VpnState.CONNECTED -> "CONNECTED"
                        VpnState.CONNECTING -> "CONNECTING"
                        VpnState.CONNECTION_FAILED -> "RETRY"
                        VpnState.DISCONNECTED -> "CONNECT"
                    },
                    color = activeColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}
