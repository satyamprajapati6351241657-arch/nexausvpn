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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.ServerType
import com.example.models.VpnServer
import com.example.servers.ServerRepository
import com.example.ui.components.ServerCard
import com.example.ui.dialogs.AdminServerManagementDialog
import com.example.ui.dialogs.ServerDetailDialog
import com.example.ui.theme.CardStrokeNavy
import com.example.ui.theme.CardSurfaceNavy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepSpaceNavy
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

enum class ServerSortMode(val label: String) {
    FASTEST("Fastest Ping"),
    LOWEST_LOAD("Lowest Load"),
    NAME("Alphabetical")
}

@Composable
fun ServerListScreen(
    onNavigateBack: () -> Unit,
    onNavigatePremium: () -> Unit,
    isUserPremium: Boolean
) {
    val allServers by ServerRepository.servers.collectAsState()
    val selectedServer by ServerRepository.selectedServer.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Free, 2: VIP, 3: Favorites
    var selectedCountryFilter by remember { mutableStateOf<String?>(null) }
    var sortMode by remember { mutableStateOf(ServerSortMode.FASTEST) }

    var serverForDetails by remember { mutableStateOf<VpnServer?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }

    // Distinct countries for filter chips
    val countries = remember(allServers) {
        listOf("All Locations") + allServers.map { it.country }.distinct()
    }

    // Filtered and sorted servers
    val filteredServers = remember(allServers, searchQuery, selectedTab, selectedCountryFilter, sortMode) {
        var list = allServers

        // Tab filter
        when (selectedTab) {
            1 -> list = list.filter { it.serverType == ServerType.FREE }
            2 -> list = list.filter { it.serverType == ServerType.PREMIUM }
            3 -> list = list.filter { it.isFavorite }
        }

        // Country filter
        if (selectedCountryFilter != null && selectedCountryFilter != "All Locations") {
            list = list.filter { it.country.equals(selectedCountryFilter, ignoreCase = true) }
        }

        // Search query
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            list = list.filter {
                it.country.lowercase().contains(q) ||
                it.city.lowercase().contains(q) ||
                it.serverName.lowercase().contains(q) ||
                it.protocol.displayName.lowercase().contains(q)
            }
        }

        // Sorting
        when (sortMode) {
            ServerSortMode.FASTEST -> list.sortedBy { it.ping }
            ServerSortMode.LOWEST_LOAD -> list.sortedBy { it.load }
            ServerSortMode.NAME -> list.sortedBy { it.city }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceNavy)
            .testTag("server_list_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(CardSurfaceNavy)
                            .testTag("server_list_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimaryDark
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Global Server Network",
                            color = TextPrimaryDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${allServers.size} Locations Available",
                            color = TextSecondaryDark,
                            fontSize = 11.sp
                        )
                    }
                }

                // Add / Import custom config button
                IconButton(
                    onClick = { showImportDialog = true },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CyberCyan.copy(alpha = 0.15f))
                        .border(1.dp, CyberCyan.copy(alpha = 0.5f), CircleShape)
                        .testTag("server_list_import_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Import Server",
                        tint = CyberCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search country, city, protocol...", color = TextMutedDark, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMutedDark, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CardStrokeNavy,
                        focusedContainerColor = CardSurfaceNavy,
                        unfocusedContainerColor = CardSurfaceNavy,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("server_search_input")
                )
            }

            // Tabs: All, Free, VIP, Favorites
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DeepSpaceNavy,
                contentColor = CyberCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CyberCyan,
                        height = 2.dp
                    )
                },
                divider = { Box(modifier = Modifier.height(1.dp).background(CardStrokeNavy)) }
            ) {
                val tabLabels = listOf("All", "Free", "VIP", "Favorites")
                tabLabels.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) CyberCyan else TextMutedDark,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            // Filter Chips and Sort Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(countries) { country ->
                        val isSelected = (selectedCountryFilter == country) || (selectedCountryFilter == null && country == "All Locations")
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else CardSurfaceNavy)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) CyberCyan else CardStrokeNavy,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    selectedCountryFilter = if (country == "All Locations") null else country
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = country,
                                color = if (isSelected) CyberCyan else TextSecondaryDark,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Sort toggle button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardSurfaceNavy)
                        .clickable {
                            sortMode = when (sortMode) {
                                ServerSortMode.FASTEST -> ServerSortMode.LOWEST_LOAD
                                ServerSortMode.LOWEST_LOAD -> ServerSortMode.NAME
                                ServerSortMode.NAME -> ServerSortMode.FASTEST
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Sort",
                            tint = CyberCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = sortMode.label,
                            color = TextSecondaryDark,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Server List
            if (filteredServers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No servers found",
                            color = TextPrimaryDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try clearing your search query or country filter.",
                            color = TextMutedDark,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredServers, key = { it.serverId }) { server ->
                        ServerCard(
                            server = server,
                            isSelected = selectedServer.serverId == server.serverId,
                            onSelect = {
                                if (server.serverType == ServerType.PREMIUM && !isUserPremium) {
                                    onNavigatePremium()
                                } else {
                                    ServerRepository.selectServer(server)
                                    onNavigateBack()
                                }
                            },
                            onFavoriteToggle = {
                                ServerRepository.toggleFavorite(server.serverId)
                            },
                            onInfoClick = {
                                serverForDetails = server
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }

        // Server Details Modal
        serverForDetails?.let { srv ->
            ServerDetailDialog(
                server = srv,
                onDismiss = { serverForDetails = null },
                onSelectAndConnect = {
                    ServerRepository.selectServer(srv)
                    serverForDetails = null
                    onNavigateBack()
                }
            )
        }

        // Import Custom Server Configuration Modal
        if (showImportDialog) {
            AdminServerManagementDialog(
                onDismiss = { showImportDialog = false },
                onServerImported = {
                    showImportDialog = false
                    onNavigateBack()
                }
            )
        }
    }
}
