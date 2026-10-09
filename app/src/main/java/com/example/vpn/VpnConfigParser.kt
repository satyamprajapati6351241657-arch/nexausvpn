package com.example.vpn

import com.example.models.VpnProtocol
import java.util.Locale

data class ParsedVpnConfig(
    val protocol: VpnProtocol,
    val host: String,
    val port: Int,
    val rawConfig: String,
    val cipher: String = "AES-256-GCM",
    val mtu: Int = 1500,
    val isInlineCertPresent: Boolean = false,
    val details: Map<String, String> = emptyMap()
)

data class ConfigValidationResult(
    val isValid: Boolean,
    val parsedConfig: ParsedVpnConfig? = null,
    val errorMessage: String? = null
)

object VpnConfigParser {

    fun validateAndParse(rawContent: String): ConfigValidationResult {
        val trimmed = rawContent.trim()
        if (trimmed.isEmpty()) {
            return ConfigValidationResult(isValid = false, errorMessage = "Configuration content is empty.")
        }

        return when {
            trimmed.contains("[Interface]", ignoreCase = true) || trimmed.contains("[Peer]", ignoreCase = true) -> {
                parseWireGuardConfig(trimmed)
            }
            trimmed.contains("client", ignoreCase = true) || trimmed.contains("dev tun", ignoreCase = true) || trimmed.contains("remote ", ignoreCase = true) -> {
                parseOpenVpnConfig(trimmed)
            }
            else -> {
                // Check if it's a host:port line or fallback
                parseGenericConfig(trimmed)
            }
        }
    }

    private fun parseWireGuardConfig(content: String): ConfigValidationResult {
        var host = "127.0.0.1"
        var port = 51820
        var hasPrivateKey = false
        var hasPublicKey = false
        val details = mutableMapOf<String, String>()

        val lines = content.lines()
        for (line in lines) {
            val clean = line.trim()
            if (clean.startsWith("#") || clean.startsWith(";")) continue
            val parts = clean.split("=", limit = 2)
            if (parts.size == 2) {
                val key = parts[0].trim().lowercase(Locale.ROOT)
                val value = parts[1].trim()
                details[key] = value

                when (key) {
                    "privatekey" -> hasPrivateKey = true
                    "publickey" -> hasPublicKey = true
                    "endpoint" -> {
                        val endpointParts = value.split(":")
                        if (endpointParts.isNotEmpty()) {
                            host = endpointParts[0].trim()
                        }
                        if (endpointParts.size > 1) {
                            port = endpointParts[1].trim().toIntOrNull() ?: 51820
                        }
                    }
                    "address" -> details["virtual_ip"] = value
                }
            }
        }

        val parsed = ParsedVpnConfig(
            protocol = VpnProtocol.WIREGUARD,
            host = host,
            port = port,
            rawConfig = content,
            cipher = "ChaCha20-Poly1305",
            details = details
        )

        return ConfigValidationResult(isValid = true, parsedConfig = parsed)
    }

    private fun parseOpenVpnConfig(content: String): ConfigValidationResult {
        var host = "127.0.0.1"
        var port = 1194
        var protocol = VpnProtocol.OPENVPN_UDP
        var cipher = "AES-256-GCM"
        val details = mutableMapOf<String, String>()

        val lines = content.lines()
        for (line in lines) {
            val clean = line.trim()
            if (clean.startsWith("#") || clean.startsWith(";")) continue

            val tokens = clean.split("\\s+".toRegex())
            if (tokens.isEmpty()) continue

            when (tokens[0].lowercase(Locale.ROOT)) {
                "remote" -> {
                    if (tokens.size > 1) host = tokens[1]
                    if (tokens.size > 2) port = tokens[2].toIntOrNull() ?: 1194
                    if (tokens.size > 3) {
                        protocol = if (tokens[3].lowercase(Locale.ROOT).contains("tcp")) {
                            VpnProtocol.OPENVPN_TCP
                        } else {
                            VpnProtocol.OPENVPN_UDP
                        }
                    }
                }
                "proto" -> {
                    if (tokens.size > 1) {
                        protocol = if (tokens[1].lowercase(Locale.ROOT).contains("tcp")) {
                            VpnProtocol.OPENVPN_TCP
                        } else {
                            VpnProtocol.OPENVPN_UDP
                        }
                    }
                }
                "cipher", "data-ciphers" -> {
                    if (tokens.size > 1) cipher = tokens[1]
                }
                "port" -> {
                    if (tokens.size > 1) port = tokens[1].toIntOrNull() ?: 1194
                }
            }
        }

        val hasCa = content.contains("<ca>", ignoreCase = true)
        val hasKey = content.contains("<key>", ignoreCase = true)

        details["cipher"] = cipher
        details["inline_certs"] = (hasCa && hasKey).toString()

        val parsed = ParsedVpnConfig(
            protocol = protocol,
            host = host,
            port = port,
            rawConfig = content,
            cipher = cipher,
            isInlineCertPresent = hasCa,
            details = details
        )

        return ConfigValidationResult(isValid = true, parsedConfig = parsed)
    }

    private fun parseGenericConfig(content: String): ConfigValidationResult {
        // Fallback for simple Host:Port or IKEv2 configuration
        val lines = content.lines().map { it.trim() }.filter { it.isNotEmpty() && !it.startsWith("#") }
        if (lines.isEmpty()) {
            return ConfigValidationResult(isValid = false, errorMessage = "Unrecognized VPN configuration format. Please provide valid OpenVPN (.ovpn) or WireGuard (.conf) format.")
        }

        val firstLine = lines.first()
        val parts = firstLine.split(":")
        val host = parts[0]
        val port = if (parts.size > 1) parts[1].toIntOrNull() ?: 500 else 500

        val parsed = ParsedVpnConfig(
            protocol = VpnProtocol.IKEV2,
            host = host,
            port = port,
            rawConfig = content,
            cipher = "AES-256-CBC"
        )
        return ConfigValidationResult(isValid = true, parsedConfig = parsed)
    }
}
