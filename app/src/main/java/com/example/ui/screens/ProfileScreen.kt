package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.auth.AuthRepository
import com.example.ui.theme.CardStrokeNavy
import com.example.ui.theme.CardSurfaceNavy
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepSpaceNavy
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.SurfaceDarkNavy
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.vpn.VpnConnectionManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigatePremium: () -> Unit,
    onLogout: () -> Unit
) {
    val user by AuthRepository.currentUser.collectAsState()
    var showEditNameDialog by remember { mutableStateOf(false) }
    var newDisplayName by remember { mutableStateOf(user?.displayName ?: "") }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceNavy)
            .testTag("profile_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Top Bar
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
                        .testTag("profile_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimaryDark
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "My Profile",
                    color = TextPrimaryDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // User Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardSurfaceNavy)
                    .border(1.dp, CardStrokeNavy, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(if (user?.isPremium == true) RadiantGold.copy(alpha = 0.2f) else CyberCyan.copy(alpha = 0.2f))
                        .border(2.dp, if (user?.isPremium == true) RadiantGold else CyberCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (user?.isPremium == true) Icons.Default.WorkspacePremium else Icons.Default.Person,
                        contentDescription = null,
                        tint = if (user?.isPremium == true) RadiantGold else CyberCyan,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user?.displayName ?: "Cyber Explorer",
                        color = TextPrimaryDark,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            newDisplayName = user?.displayName ?: ""
                            showEditNameDialog = true
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Name",
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = user?.email ?: "guest@nexusvpn.local",
                    color = TextSecondaryDark,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Plan Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (user?.isPremium == true) RadiantGold.copy(alpha = 0.18f) else CyberCyan.copy(alpha = 0.15f))
                        .border(1.dp, if (user?.isPremium == true) RadiantGold.copy(alpha = 0.6f) else CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (user?.isPremium == true) RadiantGold else CyberCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = user?.subscriptionPlan ?: "Free Tier",
                            color = if (user?.isPremium == true) RadiantGold else CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Subscription & Account Details Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardSurfaceNavy)
                    .border(1.dp, CardStrokeNavy, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Text(
                    text = "SUBSCRIPTION DETAILS",
                    color = TextMutedDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileInfoRow(
                    icon = Icons.Default.CalendarToday,
                    title = "Plan Validity",
                    value = "Expires ${dateFormat.format(Date(user?.expiresAt ?: System.currentTimeMillis()))}"
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProfileInfoRow(
                    icon = Icons.Default.DataUsage,
                    title = "Monthly Data Used",
                    value = VpnConnectionManager.formatDataSize(user?.dataUsedBytes ?: 0L)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Data limit bar
                if (user?.isPremium != true) {
                    val used = user?.dataUsedBytes ?: 1_250_000_000L
                    val limit = user?.dataLimitBytes ?: 100_000_000_000L
                    val progress = (used.toFloat() / limit.toFloat()).coerceIn(0f, 1f)

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = CyberCyan,
                        trackColor = CardStrokeNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "100 GB Free Monthly Limit",
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )
                } else {
                    Text(
                        text = "Unlimited High-Speed VIP Bandwidth",
                        color = NeonEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (user?.isPremium != true) {
                Button(
                    onClick = onNavigatePremium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RadiantGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("UPGRADE TO VIP PRO", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Logout Button
            OutlinedButton(
                onClick = {
                    AuthRepository.logout()
                    onLogout()
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonRed),
                border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("profile_logout_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout",
                    tint = CrimsonRed,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LOGOUT",
                    fontWeight = FontWeight.Bold,
                    color = CrimsonRed,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Edit Name Dialog
        if (showEditNameDialog) {
            AlertDialog(
                onDismissRequest = { showEditNameDialog = false },
                containerColor = SurfaceDarkNavy,
                title = { Text("Edit Display Name", color = TextPrimaryDark) },
                text = {
                    OutlinedTextField(
                        value = newDisplayName,
                        onValueChange = { newDisplayName = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CardStrokeNavy,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newDisplayName.isNotBlank()) {
                                AuthRepository.updateProfile(newDisplayName)
                            }
                            showEditNameDialog = false
                        }
                    ) {
                        Text("Save", color = CyberCyan, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditNameDialog = false }) {
                        Text("Cancel", color = TextSecondaryDark)
                    }
                }
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
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
            Text(text = title, color = TextSecondaryDark, fontSize = 13.sp)
        }
        Text(
            text = value,
            color = TextPrimaryDark,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
