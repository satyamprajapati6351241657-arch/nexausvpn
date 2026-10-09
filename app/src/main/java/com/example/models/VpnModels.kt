package com.example.models

enum class VpnProtocol(val displayName: String, val defaultPort: Int, val description: String) {
    WIREGUARD("WireGuard", 51820, "Next-gen ultra-fast, modern crypto (ChaCha20-Poly1305)"),
    OPENVPN_UDP("OpenVPN (UDP)", 1194, "High-speed encrypted UDP tunnel (AES-256-GCM)"),
    OPENVPN_TCP("OpenVPN (TCP)", 443, "Reliable bypass for restrictive firewalls & proxies"),
    IKEV2("IKEv2/IPSec", 500, "Fast reconnection for mobile networks & cellular handover")
}

enum class ServerType(val title: String) {
    FREE("Free"),
    PREMIUM("VIP Premium")
}

enum class ServerStatus(val label: String) {
    ONLINE("Online"),
    HIGH_LOAD("High Load"),
    MAINTENANCE("Maintenance"),
    OFFLINE("Offline")
}

enum class VpnState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    CONNECTION_FAILED
}

data class VpnConnectionStats(
    val state: VpnState = VpnState.DISCONNECTED,
    val durationSeconds: Long = 0L,
    val downloadSpeedBps: Long = 0L,
    val uploadSpeedBps: Long = 0L,
    val totalDownloadedBytes: Long = 0L,
    val totalUploadedBytes: Long = 0L,
    val virtualIp: String = "10.8.0.2",
    val encryptionCipher: String = "AES-256-GCM / ChaCha20",
    val errorMessage: String? = null,
    val connectedServer: VpnServer? = null
)

data class VpnServer(
    val serverId: String,
    val serverName: String,
    val country: String,
    val countryCode: String,
    val city: String,
    val flagEmoji: String,
    val protocol: VpnProtocol,
    val host: String,
    val port: Int,
    val configuration: String, // .ovpn / wireguard .conf / JSON config
    val serverType: ServerType,
    val enabled: Boolean = true,
    val serverStatus: ServerStatus = ServerStatus.ONLINE,
    val ping: Int, // ms
    val load: Int, // 0-100%
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class UserProfile(
    val userId: String,
    val email: String,
    val displayName: String,
    val isPremium: Boolean,
    val subscriptionPlan: String,
    val expiresAt: Long,
    val dataUsedBytes: Long,
    val dataLimitBytes: Long, // 0 means unlimited
    val createdAt: Long
)

data class AppSettings(
    val autoConnect: Boolean = false,
    val connectOnAppStart: Boolean = false,
    val killSwitchEnabled: Boolean = true,
    val selectedProtocol: VpnProtocol = VpnProtocol.WIREGUARD,
    val splitTunneling: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val isDarkMode: Boolean = true,
    val appLanguage: String = "English",
    val dnsProvider: String = "Cloudflare (1.1.1.1)",
    val adminModeEnabled: Boolean = true
)
