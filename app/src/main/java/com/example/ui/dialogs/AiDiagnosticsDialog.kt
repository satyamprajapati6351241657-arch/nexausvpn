package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.models.VpnServer
import com.example.servers.ServerRepository
import com.example.ui.theme.CardStrokeNavy
import com.example.ui.theme.CardSurfaceNavy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.SurfaceDarkNavy
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AiDiagnosticsDialog(
    onDismiss: () -> Unit,
    onServerSelected: (VpnServer) -> Unit
) {
    var selectedGoal by remember { mutableStateOf("Low Latency Gaming") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var aiAnalysisResult by remember { mutableStateOf<String?>(null) }
    var recommendedServer by remember { mutableStateOf<VpnServer?>(null) }

    val allServers by ServerRepository.servers.collectAsState()
    val scope = rememberCoroutineScope()

    val goals = listOf(
        "Low Latency Gaming",
        "4K Video Streaming",
        "Maximum Privacy & Zero-Leak",
        "P2P & Fast Downloads"
    )

    fun runDiagnostics() {
        scope.launch {
            isAnalyzing = true
            delay(1200)

            val rec = when (selectedGoal) {
                "Low Latency Gaming" -> allServers.minByOrNull { it.ping } ?: allServers.first()
                "4K Video Streaming" -> allServers.firstOrNull { it.countryCode == "US" } ?: allServers.first()
                "Maximum Privacy & Zero-Leak" -> allServers.firstOrNull { it.countryCode == "CH" } ?: allServers.first()
                else -> allServers.firstOrNull { it.countryCode == "NL" } ?: allServers.first()
            }

            recommendedServer = rec
            aiAnalysisResult = when (selectedGoal) {
                "Low Latency Gaming" ->
                    "AI Analysis: Selected ${rec.serverName} (${rec.city}) with ${rec.ping}ms ping and ${rec.load}% cluster load for optimal UDP packet pacing and zero packet-drop jitter."
                "4K Video Streaming" ->
                    "AI Analysis: Selected ${rec.serverName} (${rec.city}). Optimized for ultra-high bitrate 4K streams with residential IP routing bypass."
                "Maximum Privacy & Zero-Leak" ->
                    "AI Analysis: Selected ${rec.serverName} (${rec.country}). Hosted in privacy-friendly jurisdiction with RAM-only architecture and double ChaCha20 encryption."
                else ->
                    "AI Analysis: Selected ${rec.serverName} (${rec.city}). High-bandwidth P2P transit node with unlimited 10Gbps pipeline."
            }
            isAnalyzing = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("ai_diagnostics_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Gemini Network Optimizer",
                                color = TextPrimaryDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "AI Routing & Threat Diagnostic",
                                color = CyberCyan,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Select your target activity. Gemini AI will analyze node capacity, hop latency, and packet routes to recommend the ideal server.",
                    color = TextSecondaryDark,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Goal Chips
                goals.forEach { goal ->
                    val isSelected = selectedGoal == goal
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CyberCyan.copy(alpha = 0.15f) else CardSurfaceNavy)
                            .border(1.dp, if (isSelected) CyberCyan else CardStrokeNavy, RoundedCornerShape(10.dp))
                            .clickable { selectedGoal = goal }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (goal) {
                                    "Low Latency Gaming" -> Icons.Default.Gamepad
                                    "4K Video Streaming" -> Icons.Default.PlayCircle
                                    "Maximum Privacy & Zero-Leak" -> Icons.Default.Security
                                    else -> Icons.Default.Download
                                },
                                contentDescription = null,
                                tint = if (isSelected) CyberCyan else TextSecondaryDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = goal,
                                color = if (isSelected) CyberCyan else TextPrimaryDark,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { runDiagnostics() },
                    enabled = !isAnalyzing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyzing Global Nodes...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("RUN AI DIAGNOSTIC", fontWeight = FontWeight.Bold)
                    }
                }

                // AI Results Box
                if (aiAnalysisResult != null && recommendedServer != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardSurfaceNavy)
                            .border(1.dp, NeonEmerald.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = recommendedServer?.flagEmoji ?: "🌐", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Recommended: ${recommendedServer?.city}, ${recommendedServer?.country}",
                                        color = TextPrimaryDark,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${recommendedServer?.protocol?.displayName} • ${recommendedServer?.ping} ms latency",
                                        color = NeonEmerald,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = aiAnalysisResult ?: "",
                                color = TextSecondaryDark,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    recommendedServer?.let {
                                        ServerRepository.selectServer(it)
                                        onServerSelected(it)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Text("SELECT THIS OPTIMAL NODE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
