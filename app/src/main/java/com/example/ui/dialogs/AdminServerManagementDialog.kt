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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.servers.ServerRepository
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
import com.example.vpn.VpnConfigParser

@Composable
fun AdminServerManagementDialog(
    onDismiss: () -> Unit,
    onServerImported: () -> Unit
) {
    var serverName by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("United States") }
    var city by remember { mutableStateOf("Los Angeles") }
    var flag by remember { mutableStateOf("🇺🇸") }
    var serverType by remember { mutableStateOf(ServerType.FREE) }
    var rawConfigText by remember { mutableStateOf("") }

    var validationMessage by remember { mutableStateOf<String?>(null) }
    var isConfigValid by remember { mutableStateOf(false) }

    fun validateInput() {
        if (rawConfigText.isBlank()) {
            validationMessage = "Please paste or enter your VPN server configuration."
            isConfigValid = false
            return
        }
        val result = VpnConfigParser.validateAndParse(rawConfigText)
        if (result.isValid) {
            val parsed = result.parsedConfig
            validationMessage = "Valid ${parsed?.protocol?.displayName} config! Host: ${parsed?.host}:${parsed?.port}"
            isConfigValid = true
        } else {
            validationMessage = result.errorMessage ?: "Invalid format. Expected .ovpn or WireGuard .conf."
            isConfigValid = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardStrokeNavy),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("admin_import_server_dialog")
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
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Import VPN Server",
                            color = TextPrimaryDark,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Paste your OpenVPN (.ovpn) or WireGuard (.conf) configuration below to instantly add it to your server list.",
                    color = TextSecondaryDark,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Server Name & Flag
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = serverName,
                        onValueChange = { serverName = it },
                        label = { Text("Server Name") },
                        placeholder = { Text("e.g. Frankfurt 02") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CardStrokeNavy,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = flag,
                        onValueChange = { flag = it },
                        label = { Text("Flag") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CardStrokeNavy,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        modifier = Modifier.width(70.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Country & City
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = country,
                        onValueChange = { country = it },
                        label = { Text("Country") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CardStrokeNavy,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CardStrokeNavy,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Server Tier (Free vs VIP)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Server Type:", color = TextSecondaryDark, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = serverType == ServerType.FREE,
                            onClick = { serverType = ServerType.FREE },
                            colors = RadioButtonDefaults.colors(selectedColor = CyberCyan, unselectedColor = TextMutedDark)
                        )
                        Text(text = "Free", color = TextPrimaryDark, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = serverType == ServerType.PREMIUM,
                            onClick = { serverType = ServerType.PREMIUM },
                            colors = RadioButtonDefaults.colors(selectedColor = RadiantGold, unselectedColor = TextMutedDark)
                        )
                        Text(text = "VIP", color = RadiantGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Preset Fillers for easy testing
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            rawConfigText = """
                                [Interface]
                                PrivateKey = aGVsbG93b3JsZHByaXZhdGVrZXlleGFtcGxlMTIzNDU=
                                Address = 10.8.0.50/24
                                DNS = 1.1.1.1

                                [Peer]
                                PublicKey = b3BlbnNzbGFsbG93ZWRwdWJsaWNrZXlleGFtcGxlNzg5MDE=
                                Endpoint = vpn.example.com:51820
                                AllowedIPs = 0.0.0.0/0
                            """.trimIndent()
                            validateInput()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("WireGuard .conf", fontSize = 11.sp, color = CyberCyan)
                    }

                    OutlinedButton(
                        onClick = {
                            rawConfigText = """
                                client
                                dev tun
                                proto udp
                                remote vpn.example.com 1194
                                cipher AES-256-GCM
                                nobind
                                persist-key
                                persist-tun
                            """.trimIndent()
                            validateInput()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("OpenVPN .ovpn", fontSize = 11.sp, color = CyberCyan)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Raw Config Text Area
                OutlinedTextField(
                    value = rawConfigText,
                    onValueChange = {
                        rawConfigText = it
                        validateInput()
                    },
                    label = { Text("Configuration Payload") },
                    placeholder = { Text("[Interface]\nPrivateKey = ...\nEndpoint = ...", color = TextMutedDark, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .testTag("admin_config_text_input"),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TextPrimaryDark
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CardStrokeNavy,
                        focusedContainerColor = CardSurfaceNavy,
                        unfocusedContainerColor = CardSurfaceNavy
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Validation Feedback
                if (validationMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isConfigValid) NeonEmerald.copy(alpha = 0.15f) else CrimsonRed.copy(alpha = 0.15f))
                            .border(1.dp, if (isConfigValid) NeonEmerald.copy(alpha = 0.4f) else CrimsonRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isConfigValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isConfigValid) NeonEmerald else CrimsonRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = validationMessage ?: "",
                                color = if (isConfigValid) NeonEmerald else CrimsonRed,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Submit button
                Button(
                    onClick = {
                        val result = ServerRepository.importCustomServer(
                            name = serverName,
                            country = country,
                            countryCode = "US",
                            city = city,
                            flag = flag,
                            rawConfig = rawConfigText,
                            serverType = serverType,
                            isCustom = true
                        )
                        if (result.isSuccess) {
                            onServerImported()
                        } else {
                            validationMessage = result.exceptionOrNull()?.message ?: "Failed to import server"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("admin_save_server_button")
                ) {
                    Text(
                        text = "ADD SERVER TO NETWORK",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
