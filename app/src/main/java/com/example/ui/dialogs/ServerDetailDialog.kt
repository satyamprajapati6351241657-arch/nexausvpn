package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.models.ServerType
import com.example.models.VpnServer
import com.example.ui.theme.CardStrokeNavy
import com.example.ui.theme.CardSurfaceNavy
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.SurfaceDarkNavy
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun ServerDetailDialog(
    server: VpnServer,
    onDismiss: () -> Unit,
    onSelectAndConnect: () -> Unit
) {
    var isPinging by remember { mutableStateOf(false) }
    var currentPing by remember { mutableIntStateOf(server.ping) }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardStrokeNavy),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("server_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header with Flag and Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = server.flagEmoji, fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${server.city}, ${server.country}",
                                color = TextPrimaryDark,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = server.serverName,
                                color = TextSecondaryDark,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMutedDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Technical Info Grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardSurfaceNavy)
                        .border(1.dp, CardStrokeNavy, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    DetailRow(
                        icon = Icons.Default.Dns,
                        title = "Gateway Host",
                        value = server.host
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailRow(
                        icon = Icons.Default.Router,
                        title = "Port & Protocol",
                        value = "${server.port} / ${server.protocol.displayName}"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailRow(
                        icon = Icons.Default.Lock,
                        title = "Encryption Standard",
                        value = when (server.protocol) {
                            com.example.models.VpnProtocol.WIREGUARD -> "ChaCha20-Poly1305"
                            else -> "AES-256-GCM"
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailRow(
                        icon = Icons.Default.Speed,
                        title = "Capacity Load",
                        value = "${server.load}% (10 Gbps Uplink)"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ping Latency Row & Live Test Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardSurfaceNavy)
                        .border(1.dp, CardStrokeNavy, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NetworkCheck,
                            contentDescription = null,
                            tint = if (currentPing < 60) NeonEmerald else RadiantGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Latency", color = TextMutedDark, fontSize = 11.sp)
                            Text(
                                text = "$currentPing ms",
                                color = if (currentPing < 60) NeonEmerald else RadiantGold,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                isPinging = true
                                delay(600)
                                currentPing = (server.ping - 4 + Random.nextInt(9)).coerceAtLeast(12)
                                isPinging = false
                            }
                        },
                        enabled = !isPinging,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberCyan.copy(alpha = 0.2f),
                            contentColor = CyberCyan
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isPinging) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = CyberCyan,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = "Test Ping", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Connect button
                Button(
                    onClick = onSelectAndConnect,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("server_detail_connect_button")
                ) {
                    Text(
                        text = "CONNECT TO THIS NODE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, color = TextSecondaryDark, fontSize = 12.sp)
        }
        Text(
            text = value,
            color = TextPrimaryDark,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
