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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.auth.AuthRepository
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CardStrokeNavy
import com.example.ui.theme.CardSurfaceNavy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepSpaceNavy
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.SurfaceDarkNavy
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PricingPlan(
    val id: String,
    val title: String,
    val price: String,
    val period: String,
    val savingsBadge: String?,
    val isBestValue: Boolean
)

@Composable
fun PremiumScreen(
    onNavigateBack: () -> Unit,
    onUpgradeCompleted: () -> Unit
) {
    val plans = listOf(
        PricingPlan("plan_1m", "Monthly VIP", "$9.99", "/ month", null, false),
        PricingPlan("plan_12m", "Annual Pro", "$3.99", "/ month ($47.88/yr)", "SAVE 60%", true),
        PricingPlan("plan_life", "Lifetime Pass", "$59.99", "one-time payment", "BEST VALUE", false)
    )

    var selectedPlanIndex by remember { mutableIntStateOf(1) }
    var isProcessing by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceNavy)
            .testTag("premium_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
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
                        .testTag("premium_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // VIP Gold Crown Badge
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(RadiantGold.copy(alpha = 0.35f), Color.Transparent)
                        )
                    )
                    .border(2.dp, RadiantGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = RadiantGold,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Upgrade to Nexus VIP",
                color = TextPrimaryDark,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Unlock high-speed 10Gbps dedicated tunnels & global servers",
                color = TextSecondaryDark,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 22.dp)
            )

            // Features Checklist Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardSurfaceNavy)
                    .border(1.dp, CardStrokeNavy, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                FeatureRow(Icons.Default.Speed, "10 Gbps Turbo Speeds", "No bandwidth throttling or speed limits")
                Spacer(modifier = Modifier.height(12.dp))
                FeatureRow(Icons.Default.Tv, "Streaming & Gaming Optimized", "Bypass geo-restrictions for 4K streaming")
                Spacer(modifier = Modifier.height(12.dp))
                FeatureRow(Icons.Default.Public, "100+ Global Server Locations", "Exclusive VIP locations worldwide")
                Spacer(modifier = Modifier.height(12.dp))
                FeatureRow(Icons.Default.Lock, "Double Encryption & Tor over VPN", "Multi-hop routing for absolute anonymity")
                Spacer(modifier = Modifier.height(12.dp))
                FeatureRow(Icons.Default.ElectricBolt, "Ad & Malware Blocker", "Integrated DNS threat filtration")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Plan Options
            plans.forEachIndexed { index, plan ->
                val isSelected = selectedPlanIndex == index
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) RadiantGold.copy(alpha = 0.12f) else CardSurfaceNavy)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) RadiantGold else CardStrokeNavy,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedPlanIndex = index }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp,
                                        if (isSelected) RadiantGold else TextMutedDark,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(RadiantGold)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = plan.title,
                                        color = TextPrimaryDark,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (plan.savingsBadge != null) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(RadiantGold)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = plan.savingsBadge,
                                                color = Color.Black,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = plan.period,
                                    color = TextSecondaryDark,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Text(
                            text = plan.price,
                            color = if (isSelected) RadiantGold else TextPrimaryDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subscribe Button
            Button(
                onClick = {
                    scope.launch {
                        isProcessing = true
                        delay(1000) // Modular payment gateway hook
                        val chosenPlan = plans[selectedPlanIndex]
                        AuthRepository.upgradeToPremium(chosenPlan.title)
                        isProcessing = false
                        showSuccessDialog = true
                    }
                },
                enabled = !isProcessing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RadiantGold,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("premium_upgrade_button")
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = "UPGRADE NOW • 30-DAY GUARANTEE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Cancel anytime. Modular payment gateway ready for Google Play Billing / Stripe.",
                color = TextMutedDark,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Success Dialog
        if (showSuccessDialog) {
            Dialog(onDismissRequest = {
                showSuccessDialog = false
                onUpgradeCompleted()
            }) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RadiantGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(RadiantGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = RadiantGold,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "VIP Access Activated!",
                            color = TextPrimaryDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "You now have unlimited access to all VIP global nodes, high-speed encrypted tunnels, and ad-free browsing.",
                            color = TextSecondaryDark,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                showSuccessDialog = false
                                onUpgradeCompleted()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RadiantGold, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("START BROWSING", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(RadiantGold.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = RadiantGold,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                color = TextPrimaryDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextSecondaryDark,
                fontSize = 11.sp
            )
        }
    }
}
