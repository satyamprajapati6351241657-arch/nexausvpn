package com.example.ui.screens

import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ads.AdsManager
import com.example.auth.AuthRepository
import com.example.models.ServerType
import com.example.models.VpnState
import com.example.servers.ServerRepository
import com.example.ui.components.AdBannerContainer
import com.example.ui.components.SpeedMeterCard
import com.example.ui.components.VpnConnectButton
import com.example.ui.theme.CardStrokeNavy
import com.example.ui.theme.CardSurfaceNavy
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepSpaceNavy
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.vpn.VpnConnectionManager

@Composable
fun HomeScreen(
    onNavigateServers: () -> Unit,
    onNavigatePremium: () -> Unit,
    onNavigateProfile: () -> Unit,
    onNavigateSettings: () -> Unit,
    onOpenAiDiagnostics: () -> Unit
) {
    val context = LocalContext.current
    val user by AuthRepository.currentUser.collectAsState()
    val selectedServer by ServerRepository.selectedServer.collectAsState()
    val connectionStats by VpnConnectionManager.connectionStats.collectAsState()

    // VPN Permission Launcher
    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val granted = (result.resultCode == Activity.RESULT_OK)
        VpnConnectionManager.onVpnPermissionResult(context, granted)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceNavy)
            .testTag("home_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // User Profile Avatar / Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = onNavigateProfile)
                        .testTag("home_profile_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(CardSurfaceNavy)
                            .border(1.5.dp, CyberCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = CyberCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = user?.displayName ?: "Guest Explorer",
                            color = TextPrimaryDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (user?.isPremium == true) RadiantGold else CyberCyan)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (user?.isPremium == true) "VIP Pro Plan" else "Free Shield",
                                color = if (user?.isPremium == true) RadiantGold else TextSecondaryDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Action Icons (AI Smart Route, VIP Badge, Settings)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenAiDiagnostics,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CardSurfaceNavy)
                            .testTag("home_ai_diagnostics_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Diagnostics",
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (user?.isPremium != true) {
                        IconButton(
                            onClick = onNavigatePremium,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(RadiantGold.copy(alpha = 0.18f))
                                .border(1.dp, RadiantGold.copy(alpha = 0.5f), CircleShape)
                                .testTag("home_upgrade_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "Upgrade VIP",
                                tint = RadiantGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    IconButton(
                        onClick = onNavigateSettings,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CardSurfaceNavy)
                            .testTag("home_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextSecondaryDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hero Graphic & Encryption Status banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardStrokeNavy, RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vpn_hero_banner_1790996423425),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    DeepSpaceNavy.copy(alpha = 0.88f),
                                    DeepSpaceNavy.copy(alpha = 0.5f)
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = if (connectionStats.state == VpnState.CONNECTED) NeonEmerald else CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (connectionStats.state) {
                                    VpnState.CONNECTED -> "SECURE TUNNEL ACTIVE"
                                    VpnState.CONNECTING -> "HANDSHAKING ENCRYPTED TUNNEL"
                                    VpnState.CONNECTION_FAILED -> "TUNNEL OFFLINE"
                                    VpnState.DISCONNECTED -> "IP UNPROTECTED"
                                },
                                color = when (connectionStats.state) {
                                    VpnState.CONNECTED -> NeonEmerald
                                    VpnState.CONNECTING -> RadiantGold
                                    VpnState.CONNECTION_FAILED -> CrimsonRed
                                    VpnState.DISCONNECTED -> CyberCyan
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (connectionStats.state == VpnState.CONNECTED) {
                                "Encrypted via ${selectedServer.protocol.displayName} • ${VpnConnectionManager.formatDuration(connectionStats.durationSeconds)}"
                            } else {
                                "Connect to mask your real IP and browse securely"
                            },
                            color = TextPrimaryDark,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main VPN Connect Button
            VpnConnectButton(
                state = connectionStats.state,
                onClick = {
                    when (connectionStats.state) {
                        VpnState.CONNECTED -> {
                            VpnConnectionManager.stopConnection(context)
                            AdsManager.showInterstitialIfAppropriate(
                                context = context,
                                isUserPremium = user?.isPremium == true,
                                actionTag = "vpn_disconnect"
                            ) {}
                        }
                        VpnState.CONNECTING -> {
                            VpnConnectionManager.stopConnection(context)
                        }
                        VpnState.DISCONNECTED, VpnState.CONNECTION_FAILED -> {
                            VpnConnectionManager.startConnection(
                                context = context,
                                server = selectedServer,
                                permissionLauncher = vpnPermissionLauncher
                            )
                        }
                    }
                }
            )

            // Connection Duration (when connected)
            if (connectionStats.state == VpnState.CONNECTED) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = VpnConnectionManager.formatDuration(connectionStats.durationSeconds),
                    color = NeonEmerald,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
            }

            // Error display if failed
            if (connectionStats.state == VpnState.CONNECTION_FAILED && connectionStats.errorMessage != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CrimsonRed.copy(alpha = 0.15f))
                        .border(1.dp, CrimsonRed.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = CrimsonRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = connectionStats.errorMessage ?: "Connection failed",
                            color = CrimsonRed,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Selected Server Card selector
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardSurfaceNavy)
                    .border(1.dp, CardStrokeNavy, RoundedCornerShape(16.dp))
                    .clickable(onClick = onNavigateServers)
                    .padding(16.dp)
                    .testTag("home_select_server_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(CardStrokeNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = selectedServer.flagEmoji,
                                fontSize = 24.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${selectedServer.city}, ${selectedServer.country}",
                                    color = TextPrimaryDark,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (selectedServer.serverType == ServerType.PREMIUM) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(RadiantGold.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("VIP", color = RadiantGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedServer.serverName,
                                    color = TextSecondaryDark,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${selectedServer.ping} ms",
                                    color = if (selectedServer.ping < 60) NeonEmerald else RadiantGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Change",
                            color = CyberCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Select Server",
                            tint = CyberCyan,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Live Speed & Usage statistics
            SpeedMeterCard(stats = connectionStats)

            Spacer(modifier = Modifier.height(18.dp))

            // Advertisement container placeholder (free tier only)
            AdBannerContainer(
                isPremium = user?.isPremium == true,
                onUpgradeClick = onNavigatePremium
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
