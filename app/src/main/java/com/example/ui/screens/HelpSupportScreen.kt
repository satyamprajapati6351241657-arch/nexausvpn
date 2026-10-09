package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardStrokeNavy
import com.example.ui.theme.CardSurfaceNavy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepSpaceNavy
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

data class FaqItem(val question: String, val answer: String)

@Composable
fun HelpSupportScreen(
    onNavigateBack: () -> Unit
) {
    val faqs = listOf(
        FaqItem(
            "How do I import my own custom VPN server configuration?",
            "Tap on 'Change Server' or open the Server List screen, then tap the '+' (Add) button in the top right. Paste your OpenVPN (.ovpn) or WireGuard (.conf) configuration text. NexusVPN will automatically parse the host, port, protocol, and encryption parameters!"
        ),
        FaqItem(
            "What does the Kill Switch do?",
            "If your Wi-Fi or cellular connection drops unexpectedly while using NexusVPN, the Kill Switch immediately blocks all outbound internet traffic to prevent your real IP address and unencrypted data from leaking."
        ),
        FaqItem(
            "Which VPN protocol should I select?",
            "WireGuard offers the highest speeds, lowest ping, and latest modern cryptography (ChaCha20). If you are behind a strict corporate or school firewall that blocks UDP traffic, switch to OpenVPN (TCP) on port 443 in Settings."
        ),
        FaqItem(
            "Does NexusVPN keep browsing logs?",
            "No. NexusVPN operates under a strict Zero-Logs policy. We do not store or monitor browsing activity, DNS queries, origin IP addresses, or connection timestamps."
        )
    )

    var ticketSubject by remember { mutableStateOf("") }
    var ticketMessage by remember { mutableStateOf("") }
    var ticketSent by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceNavy)
            .testTag("help_support_screen")
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
                        .testTag("help_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimaryDark
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Help & Support",
                    color = TextPrimaryDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "FREQUENTLY ASKED QUESTIONS",
                color = TextMutedDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            faqs.forEach { faq ->
                FaqAccordion(faq = faq)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contact Support Form
            Text(
                text = "SUBMIT SUPPORT TICKET",
                color = TextMutedDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardSurfaceNavy)
                    .border(1.dp, CardStrokeNavy, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                if (ticketSent) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ticket submitted! Our cybersecurity support engineers will respond within 24 hours.",
                            color = NeonEmerald,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = ticketSubject,
                        onValueChange = { ticketSubject = it },
                        label = { Text("Issue Subject") },
                        placeholder = { Text("e.g. Connection handshake timeout", color = TextMutedDark) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CardStrokeNavy,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ticketMessage,
                        onValueChange = { ticketMessage = it },
                        label = { Text("Describe the problem") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CardStrokeNavy,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (ticketSubject.isNotBlank() && ticketMessage.isNotBlank()) {
                                ticketSent = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SUBMIT TICKET", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun FaqAccordion(faq: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurfaceNavy)
            .border(1.dp, CardStrokeNavy, RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = faq.question,
                color = TextPrimaryDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = faq.answer,
                    color = TextSecondaryDark,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
