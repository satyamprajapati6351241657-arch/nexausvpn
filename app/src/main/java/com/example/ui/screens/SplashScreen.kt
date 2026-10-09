package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.auth.AuthRepository
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepSpaceNavy
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateNext: (isLoggedIn: Boolean) -> Unit
) {
    val scale = remember { Animatable(0.7f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.05f,
            animationSpec = tween(700, easing = FastOutSlowInEasing)
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(300)
        )
        delay(600)
        val isLoggedIn = AuthRepository.currentUser.value != null
        onNavigateNext(isLoggedIn)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepSpaceNavy,
                        ElectricBlue.copy(alpha = 0.35f),
                        DeepSpaceNavy
                    )
                )
            )
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(scale.value)
                    .clip(CircleShape)
                    .background(CyberCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vpn_launcher_icon_1790996409145),
                    contentDescription = "NexusVPN Logo",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "NexusVPN",
                color = TextPrimaryDark,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Encrypted Tunnels • Global Nodes",
                color = CyberCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = CyberCyan,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
