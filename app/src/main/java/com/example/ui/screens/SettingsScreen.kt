package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.VpnProtocol
import com.example.settings.SettingsRepository
import com.example.ui.theme.CardStrokeNavy
import com.example.ui.theme.CardSurfaceNavy
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepSpaceNavy
import com.example.ui.theme.SurfaceDarkNavy
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateHelp: () -> Unit,
    onNavigateAbout: () -> Unit,
    onNavigatePrivacy: () -> Unit,
    onNavigateTerms: () -> Unit
) {
    val settings by SettingsRepository.settings.collectAsState()
    var showProtocolPicker by remember { mutableStateOf(false) }
    var showDnsPicker by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    val languages = listOf("English", "Español", "Deutsch", "Français", "日本語", "Hindi")
    val dnsProviders = listOf(
        "Cloudflare (1.1.1.1)",
        "Google (8.8.8.8)",
        "AdGuard DNS (Anti-Malware)",
        "Quad9 (9.9.9.9)"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceNavy)
            .testTag("settings_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CardSurfaceNavy)
                        .testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimaryDark
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Settings",
                    color = TextPrimaryDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Tunnel & Security
            SettingsSectionHeader("TUNNEL & SECURITY")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardSurfaceNavy)
                    .border(1.dp, CardStrokeNavy, RoundedCornerShape(16.dp))
            ) {
                // Kill Switch
                SettingsSwitchRow(
                    icon = Icons.Default.Shield,
                    title = "Kill Switch",
                    subtitle = "Block internet traffic if VPN disconnects unexpectedly",
                    checked = settings.killSwitchEnabled,
                    onCheckedChange = { SettingsRepository.updateKillSwitch(it) }
                )

                SettingsDivider()

                // Auto-Connect on untrusted Wi-Fi
                SettingsSwitchRow(
                    icon = Icons.Default.Wifi,
                    title = "Auto-Connect on Wi-Fi",
                    subtitle = "Automatically secure untrusted wireless networks",
                    checked = settings.autoConnect,
                    onCheckedChange = { SettingsRepository.updateAutoConnect(it) }
                )

                SettingsDivider()

                // Connect on App Start
                SettingsSwitchRow(
                    icon = Icons.Default.Power,
                    title = "Connect on App Launch",
                    subtitle = "Instantly initiate VPN tunnel when opened",
                    checked = settings.connectOnAppStart,
                    onCheckedChange = { SettingsRepository.updateConnectOnStart(it) }
                )

                SettingsDivider()

                // Protocol Selector
                SettingsActionRow(
                    icon = Icons.Default.Router,
                    title = "VPN Protocol",
                    value = settings.selectedProtocol.displayName,
                    onClick = { showProtocolPicker = true }
                )

                SettingsDivider()

                // DNS Provider
                SettingsActionRow(
                    icon = Icons.Default.Dns,
                    title = "Secure DNS Provider",
                    value = settings.dnsProvider,
                    onClick = { showDnsPicker = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Preferences
            SettingsSectionHeader("APP PREFERENCES")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardSurfaceNavy)
                    .border(1.dp, CardStrokeNavy, RoundedCornerShape(16.dp))
            ) {
                // Notifications
                SettingsSwitchRow(
                    icon = Icons.Default.Notifications,
                    title = "Connection Notifications",
                    subtitle = "Show live duration and quick disconnect controls",
                    checked = settings.notificationsEnabled,
                    onCheckedChange = { SettingsRepository.updateNotifications(it) }
                )

                SettingsDivider()

                // Dark Mode
                SettingsSwitchRow(
                    icon = Icons.Default.DarkMode,
                    title = "Cyber Dark Theme",
                    subtitle = "Optimized for OLED displays & eye comfort",
                    checked = settings.isDarkMode,
                    onCheckedChange = { SettingsRepository.updateDarkMode(it) }
                )

                SettingsDivider()

                // Language
                SettingsActionRow(
                    icon = Icons.Default.Language,
                    title = "Language",
                    value = settings.appLanguage,
                    onClick = { showLanguagePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Information & Legal
            SettingsSectionHeader("INFORMATION & SUPPORT")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardSurfaceNavy)
                    .border(1.dp, CardStrokeNavy, RoundedCornerShape(16.dp))
            ) {
                SettingsNavigationRow(Icons.AutoMirrored.Filled.Help, "Help & FAQs", onNavigateHelp)
                SettingsDivider()
                SettingsNavigationRow(Icons.Default.Info, "About NexusVPN", onNavigateAbout)
                SettingsDivider()
                SettingsNavigationRow(Icons.Default.PrivacyTip, "Zero-Logs Privacy Policy", onNavigatePrivacy)
                SettingsDivider()
                SettingsNavigationRow(Icons.Default.Description, "Terms of Service", onNavigateTerms)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Clear App Data
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardSurfaceNavy)
                    .border(1.dp, CardStrokeNavy, RoundedCornerShape(14.dp))
                    .clickable { showClearDataDialog = true }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = null,
                            tint = CrimsonRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Clear Cache & Reset Settings",
                            color = CrimsonRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Protocol Picker Dialog
        if (showProtocolPicker) {
            AlertDialog(
                onDismissRequest = { showProtocolPicker = false },
                containerColor = SurfaceDarkNavy,
                title = { Text("Select VPN Protocol", color = TextPrimaryDark) },
                text = {
                    Column {
                        VpnProtocol.values().forEach { proto ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        SettingsRepository.updateProtocol(proto)
                                        showProtocolPicker = false
                                    }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = proto.displayName,
                                        color = if (settings.selectedProtocol == proto) CyberCyan else TextPrimaryDark,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = proto.description, color = TextSecondaryDark, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showProtocolPicker = false }) {
                        Text("Close", color = CyberCyan)
                    }
                }
            )
        }

        // DNS Picker Dialog
        if (showDnsPicker) {
            AlertDialog(
                onDismissRequest = { showDnsPicker = false },
                containerColor = SurfaceDarkNavy,
                title = { Text("Select DNS Provider", color = TextPrimaryDark) },
                text = {
                    Column {
                        dnsProviders.forEach { dns ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        SettingsRepository.updateDns(dns)
                                        showDnsPicker = false
                                    }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dns,
                                    color = if (settings.dnsProvider == dns) CyberCyan else TextPrimaryDark,
                                    fontSize = 14.sp,
                                    fontWeight = if (settings.dnsProvider == dns) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDnsPicker = false }) {
                        Text("Close", color = CyberCyan)
                    }
                }
            )
        }

        // Language Picker Dialog
        if (showLanguagePicker) {
            AlertDialog(
                onDismissRequest = { showLanguagePicker = false },
                containerColor = SurfaceDarkNavy,
                title = { Text("Select Language", color = TextPrimaryDark) },
                text = {
                    Column {
                        languages.forEach { lang ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        SettingsRepository.updateLanguage(lang)
                                        showLanguagePicker = false
                                    }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = lang,
                                    color = if (settings.appLanguage == lang) CyberCyan else TextPrimaryDark,
                                    fontSize = 14.sp,
                                    fontWeight = if (settings.appLanguage == lang) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLanguagePicker = false }) {
                        Text("Close", color = CyberCyan)
                    }
                }
            )
        }

        // Clear Data Confirm
        if (showClearDataDialog) {
            AlertDialog(
                onDismissRequest = { showClearDataDialog = false },
                containerColor = SurfaceDarkNavy,
                title = { Text("Clear App Data?", color = TextPrimaryDark) },
                text = {
                    Text(
                        "This will reset all VPN preferences and session cache to their defaults.",
                        color = TextSecondaryDark
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            SettingsRepository.clearAppData()
                            showClearDataDialog = false
                        }
                    ) {
                        Text("Reset", color = CrimsonRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDataDialog = false }) {
                        Text("Cancel", color = TextSecondaryDark)
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = TextMutedDark,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
    )
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(CardStrokeNavy)
    )
}

@Composable
private fun SettingsSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, color = TextPrimaryDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, color = TextSecondaryDark, fontSize = 11.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = CyberCyan,
                uncheckedThumbColor = TextMutedDark,
                uncheckedTrackColor = CardStrokeNavy
            )
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, color = TextPrimaryDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(12.dp))
        }
    }
}

@Composable
private fun SettingsNavigationRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, color = TextPrimaryDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(12.dp))
    }
}
