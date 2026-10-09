package com.example.servers

import com.example.models.ServerStatus
import com.example.models.ServerType
import com.example.models.VpnProtocol
import com.example.models.VpnServer
import com.example.vpn.VpnConfigParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object ServerRepository {

    private val defaultServers = listOf(
        VpnServer(
            serverId = "server_000_fast_free",
            serverName = "Fast Turbo 01",
            country = "United States",
            countryCode = "US",
            city = "New York",
            flagEmoji = "🇺🇸",
            protocol = VpnProtocol.WIREGUARD,
            host = "fast-turbo.nexusvpn.net",
            port = 51820,
            configuration = """
                [Interface]
                PrivateKey = aGlnaGZhc3R0dXJib3ByaXZhdGVrZXlleGFtcGxlMTIzNDU2Nzg=
                Address = 10.8.0.2/24
                DNS = 1.1.1.1, 8.8.8.8

                [Peer]
                PublicKey = dHVyYm9mYXN0cHVibGlja2V5bmV4dXN2cG4xMjM0NTY3ODkwMTI=
                Endpoint = fast-turbo.nexusvpn.net:51820
                AllowedIPs = 0.0.0.0/0
                PersistentKeepalive = 25
            """.trimIndent(),
            serverType = ServerType.FREE,
            enabled = true,
            serverStatus = ServerStatus.ONLINE,
            ping = 16,
            load = 14,
            isFavorite = true
        ),
        VpnServer(
            serverId = "server_001_de_fra",
            serverName = "Frankfurt 01",
            country = "Germany",
            countryCode = "DE",
            city = "Frankfurt",
            flagEmoji = "🇩🇪",
            protocol = VpnProtocol.WIREGUARD,
            host = "fra-node1.nexusvpn.net",
            port = 51820,
            configuration = """
                [Interface]
                PrivateKey = aGVsbG93b3JsZHByaXZhdGVrZXlleGFtcGxlZGF0YTEyMw==
                Address = 10.8.0.2/24
                DNS = 1.1.1.1, 8.8.8.8

                [Peer]
                PublicKey = b3BlbnNzbGFsbG93ZWRwdWJsaWNrZXlleGFtcGxlNDU2Nzg=
                Endpoint = fra-node1.nexusvpn.net:51820
                AllowedIPs = 0.0.0.0/0
                PersistentKeepalive = 25
            """.trimIndent(),
            serverType = ServerType.FREE,
            enabled = true,
            serverStatus = ServerStatus.ONLINE,
            ping = 38,
            load = 42,
            isFavorite = true
        ),
        VpnServer(
            serverId = "server_002_us_nyc",
            serverName = "New York 02",
            country = "United States",
            countryCode = "US",
            city = "New York",
            flagEmoji = "🇺🇸",
            protocol = VpnProtocol.OPENVPN_UDP,
            host = "nyc-node2.nexusvpn.net",
            port = 1194,
            configuration = """
                client
                dev tun
                proto udp
                remote nyc-node2.nexusvpn.net 1194
                resolv-retry infinite
                nobind
                persist-key
                persist-tun
                cipher AES-256-GCM
                auth SHA256
                key-direction 1
                verb 3
                <ca>
                -----BEGIN CERTIFICATE-----
                MIIBkTCB+wIBADANBgkqhkiG9w0BAQsFADASMRAwDgYDVQQDDAdOZXh1c0NBMCAX
                DTI2MDEwMTAwMDAwMFoYDzIwNzYwMTAxMDAwMDAwWjASMRAwDgYDVQQDDAdOZXh1
                -----END CERTIFICATE-----
                </ca>
            """.trimIndent(),
            serverType = ServerType.FREE,
            enabled = true,
            serverStatus = ServerStatus.ONLINE,
            ping = 82,
            load = 65,
            isFavorite = false
        ),
        VpnServer(
            serverId = "server_003_gb_lon",
            serverName = "London 01 VIP",
            country = "United Kingdom",
            countryCode = "GB",
            city = "London",
            flagEmoji = "🇬🇧",
            protocol = VpnProtocol.WIREGUARD,
            host = "lon-vip1.nexusvpn.net",
            port = 51820,
            configuration = """
                [Interface]
                PrivateKey = bG9uZG9ucHJpdmF0ZWtleWV4YW1wbGVub2RlMTIzNDU2Nzg=
                Address = 10.8.0.3/24
                DNS = 1.1.1.1

                [Peer]
                PublicKey = dmlwZ2JwdWJsaWNrZXlub2RlbmV4dXN2cG40NTY3ODkwMTI=
                Endpoint = lon-vip1.nexusvpn.net:51820
                AllowedIPs = 0.0.0.0/0
            """.trimIndent(),
            serverType = ServerType.PREMIUM,
            enabled = true,
            serverStatus = ServerStatus.ONLINE,
            ping = 24,
            load = 28,
            isFavorite = true
        ),
        VpnServer(
            serverId = "server_004_jp_tyo",
            serverName = "Tokyo 01 VIP",
            country = "Japan",
            countryCode = "JP",
            city = "Tokyo",
            flagEmoji = "🇯🇵",
            protocol = VpnProtocol.WIREGUARD,
            host = "tyo-node1.nexusvpn.net",
            port = 51820,
            configuration = """
                [Interface]
                PrivateKey = dG9reW9wcml2YXRla2V5bm9kZXR5bzAxMjM0NTY3ODkwMTI=
                Address = 10.8.0.4/24
                DNS = 1.1.1.1

                [Peer]
                PublicKey = dHlvMDAxcHVibGlja2V5bmV4dXN2cG50b2t5bzg5MDEyMzQ=
                Endpoint = tyo-node1.nexusvpn.net:51820
                AllowedIPs = 0.0.0.0/0
            """.trimIndent(),
            serverType = ServerType.PREMIUM,
            enabled = true,
            serverStatus = ServerStatus.ONLINE,
            ping = 115,
            load = 31,
            isFavorite = false
        ),
        VpnServer(
            serverId = "server_005_in_bom",
            serverName = "Mumbai 01",
            country = "India",
            countryCode = "IN",
            city = "Mumbai",
            flagEmoji = "🇮🇳",
            protocol = VpnProtocol.OPENVPN_UDP,
            host = "bom-node1.nexusvpn.net",
            port = 1194,
            configuration = """
                client
                dev tun
                proto udp
                remote bom-node1.nexusvpn.net 1194
                cipher AES-256-GCM
                nobind
                persist-key
                persist-tun
            """.trimIndent(),
            serverType = ServerType.FREE,
            enabled = true,
            serverStatus = ServerStatus.ONLINE,
            ping = 45,
            load = 58,
            isFavorite = true
        ),
        VpnServer(
            serverId = "server_006_sg_sin",
            serverName = "Singapore 01",
            country = "Singapore",
            countryCode = "SG",
            city = "Singapore",
            flagEmoji = "🇸🇬",
            protocol = VpnProtocol.WIREGUARD,
            host = "sin-node1.nexusvpn.net",
            port = 51820,
            configuration = """
                [Interface]
                PrivateKey = c2luZ2Fwb3JlcHJpdmF0ZWtleTEyMzQ1Njc4OTAxMjM0NQ==
                Address = 10.8.0.6/24
                DNS = 1.1.1.1

                [Peer]
                PublicKey = c2lucHVibGlja2V5bmV4dXN2cG42Nzg5MDEyMzQ1Njc4OTA=
                Endpoint = sin-node1.nexusvpn.net:51820
                AllowedIPs = 0.0.0.0/0
            """.trimIndent(),
            serverType = ServerType.FREE,
            enabled = true,
            serverStatus = ServerStatus.ONLINE,
            ping = 52,
            load = 40,
            isFavorite = false
        ),
        VpnServer(
            serverId = "server_007_nl_ams",
            serverName = "Amsterdam 02 VIP",
            country = "Netherlands",
            countryCode = "NL",
            city = "Amsterdam",
            flagEmoji = "🇳🇱",
            protocol = VpnProtocol.OPENVPN_TCP,
            host = "ams-p2p.nexusvpn.net",
            port = 443,
            configuration = """
                client
                dev tun
                proto tcp
                remote ams-p2p.nexusvpn.net 443
                cipher AES-256-GCM
                resolv-retry infinite
                nobind
            """.trimIndent(),
            serverType = ServerType.PREMIUM,
            enabled = true,
            serverStatus = ServerStatus.ONLINE,
            ping = 32,
            load = 22,
            isFavorite = true
        ),
        VpnServer(
            serverId = "server_008_ca_tor",
            serverName = "Toronto 01",
            country = "Canada",
            countryCode = "CA",
            city = "Toronto",
            flagEmoji = "🇨🇦",
            protocol = VpnProtocol.WIREGUARD,
            host = "tor-node1.nexusvpn.net",
            port = 51820,
            configuration = """
                [Interface]
                PrivateKey = dG9yb250b3ByaXZhdGVrZXkxMjM0NTY3ODkwMTIzNDU2Nw==
                Address = 10.8.0.8/24
                DNS = 1.1.1.1

                [Peer]
                PublicKey = dG9ycHVibGlja2V5bmV4dXN2cG44OTAxMjM0NTY3ODkwMTI=
                Endpoint = tor-node1.nexusvpn.net:51820
                AllowedIPs = 0.0.0.0/0
            """.trimIndent(),
            serverType = ServerType.FREE,
            enabled = true,
            serverStatus = ServerStatus.ONLINE,
            ping = 78,
            load = 51,
            isFavorite = false
        ),
        VpnServer(
            serverId = "server_009_au_syd",
            serverName = "Sydney 01 VIP",
            country = "Australia",
            countryCode = "AU",
            city = "Sydney",
            flagEmoji = "🇦🇺",
            protocol = VpnProtocol.WIREGUARD,
            host = "syd-node1.nexusvpn.net",
            port = 51820,
            configuration = """
                [Interface]
                PrivateKey = c3lkbmV5cHJpdmF0ZWtleTEyMzQ1Njc4OTAxMjM0NTY3OA==
                Address = 10.8.0.9/24
                DNS = 1.1.1.1

                [Peer]
                PublicKey = c3lkcHVibGlja2V5bmV4dXN2cG4wMTIzNDU2Nzg5MDEyMzQ=
                Endpoint = syd-node1.nexusvpn.net:51820
                AllowedIPs = 0.0.0.0/0
            """.trimIndent(),
            serverType = ServerType.PREMIUM,
            enabled = true,
            serverStatus = ServerStatus.ONLINE,
            ping = 160,
            load = 34,
            isFavorite = false
        ),
        VpnServer(
            serverId = "server_010_ch_zrh",
            serverName = "Zurich 01 VIP",
            country = "Switzerland",
            countryCode = "CH",
            city = "Zurich",
            flagEmoji = "🇨🇭",
            protocol = VpnProtocol.WIREGUARD,
            host = "zrh-secure.nexusvpn.net",
            port = 51820,
            configuration = """
                [Interface]
                PrivateKey = enVyaWNocHJpdmF0ZWtleTEyMzQ1Njc4OTAxMjM0NTY3OA==
                Address = 10.8.0.10/24
                DNS = 1.1.1.1

                [Peer]
                PublicKey = enJocHVibGlja2V5bmV4dXN2cG4yMzQ1Njc4OTAxMjM0NTY=
                Endpoint = zrh-secure.nexusvpn.net:51820
                AllowedIPs = 0.0.0.0/0
            """.trimIndent(),
            serverType = ServerType.PREMIUM,
            enabled = true,
            serverStatus = ServerStatus.ONLINE,
            ping = 30,
            load = 18,
            isFavorite = false
        ),
        VpnServer(
            serverId = "server_011_fr_par",
            serverName = "Paris 01",
            country = "France",
            countryCode = "FR",
            city = "Paris",
            flagEmoji = "🇫🇷",
            protocol = VpnProtocol.OPENVPN_UDP,
            host = "par-node1.nexusvpn.net",
            port = 1194,
            configuration = """
                client
                dev tun
                proto udp
                remote par-node1.nexusvpn.net 1194
                cipher AES-256-GCM
                nobind
            """.trimIndent(),
            serverType = ServerType.FREE,
            enabled = true,
            serverStatus = ServerStatus.ONLINE,
            ping = 40,
            load = 48,
            isFavorite = false
        ),
        VpnServer(
            serverId = "server_012_br_sao",
            serverName = "Sao Paulo 01 VIP",
            country = "Brazil",
            countryCode = "BR",
            city = "Sao Paulo",
            flagEmoji = "🇧🇷",
            protocol = VpnProtocol.IKEV2,
            host = "sao-node1.nexusvpn.net",
            port = 500,
            configuration = "sao-node1.nexusvpn.net:500",
            serverType = ServerType.PREMIUM,
            enabled = true,
            serverStatus = ServerStatus.ONLINE,
            ping = 135,
            load = 29,
            isFavorite = false
        )
    )

    private val _servers = MutableStateFlow<List<VpnServer>>(defaultServers)
    val servers: StateFlow<List<VpnServer>> = _servers.asStateFlow()

    private val _selectedServer = MutableStateFlow<VpnServer>(defaultServers.first())
    val selectedServer: StateFlow<VpnServer> = _selectedServer.asStateFlow()

    fun selectServer(server: VpnServer) {
        _selectedServer.value = server
    }

    fun toggleFavorite(serverId: String) {
        _servers.value = _servers.value.map { server ->
            if (server.serverId == serverId) {
                val updated = server.copy(isFavorite = !server.isFavorite)
                if (_selectedServer.value.serverId == serverId) {
                    _selectedServer.value = updated
                }
                updated
            } else {
                server
            }
        }
    }

    // ----------------------------------------------------
    // Admin / Custom Server Configuration Integration
    // ----------------------------------------------------

    /**
     * Integrates user provided VPN server configuration (.ovpn, WireGuard .conf, etc.)
     * into the server list and selects it.
     */
    fun importCustomServer(
        name: String,
        country: String,
        countryCode: String,
        city: String,
        flag: String,
        rawConfig: String,
        serverType: ServerType = ServerType.FREE,
        isCustom: Boolean = true
    ): Result<VpnServer> {
        val validation = VpnConfigParser.validateAndParse(rawConfig)
        if (!validation.isValid) {
            return Result.failure(IllegalArgumentException(validation.errorMessage ?: "Invalid configuration file"))
        }

        val parsed = validation.parsedConfig ?: return Result.failure(IllegalArgumentException("Failed to parse configuration"))

        val newServer = VpnServer(
            serverId = "custom_" + UUID.randomUUID().toString().take(8),
            serverName = name.ifBlank { "${city.ifBlank { country }} Custom Node" },
            country = country.ifBlank { "Custom Location" },
            countryCode = countryCode.ifBlank { "UN" },
            city = city.ifBlank { "Gateway" },
            flagEmoji = flag.ifBlank { "🌐" },
            protocol = parsed.protocol,
            host = parsed.host,
            port = parsed.port,
            configuration = rawConfig,
            serverType = serverType,
            enabled = true,
            serverStatus = ServerStatus.ONLINE,
            ping = (20..65).random(),
            load = (10..30).random(),
            isFavorite = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        _servers.value = listOf(newServer) + _servers.value
        _selectedServer.value = newServer
        return Result.success(newServer)
    }

    fun addServer(server: VpnServer) {
        _servers.value = listOf(server) + _servers.value
    }

    fun updateServer(updatedServer: VpnServer) {
        _servers.value = _servers.value.map {
            if (it.serverId == updatedServer.serverId) updatedServer else it
        }
        if (_selectedServer.value.serverId == updatedServer.serverId) {
            _selectedServer.value = updatedServer
        }
    }

    fun deleteServer(serverId: String): Boolean {
        if (_servers.value.size <= 1) return false
        _servers.value = _servers.value.filter { it.serverId != serverId }
        if (_selectedServer.value.serverId == serverId) {
            _selectedServer.value = _servers.value.first()
        }
        return true
    }

    fun toggleServerEnabled(serverId: String) {
        _servers.value = _servers.value.map {
            if (it.serverId == serverId) it.copy(enabled = !it.enabled) else it
        }
    }
}
