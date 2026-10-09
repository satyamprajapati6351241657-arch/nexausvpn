package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.models.ServerType
import com.example.models.VpnProtocol
import com.example.servers.ServerRepository
import com.example.vpn.VpnConfigParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app_name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("NexusVPN", appName)
  }

  @Test
  fun `vpn config parser correctly parses wireguard config`() {
    val wgConfig = """
      [Interface]
      PrivateKey = aGVsbG93b3JsZHByaXZhdGVrZXlleGFtcGxlZGF0YTEyMw==
      Address = 10.8.0.2/24
      DNS = 1.1.1.1

      [Peer]
      PublicKey = b3BlbnNzbGFsbG93ZWRwdWJsaWNrZXlleGFtcGxlNDU2Nzg=
      Endpoint = vpn.nexus.net:51820
      AllowedIPs = 0.0.0.0/0
    """.trimIndent()

    val result = VpnConfigParser.validateAndParse(wgConfig)
    assertTrue(result.isValid)
    val parsed = result.parsedConfig
    assertNotNull(parsed)
    assertEquals(VpnProtocol.WIREGUARD, parsed?.protocol)
    assertEquals("vpn.nexus.net", parsed?.host)
    assertEquals(51820, parsed?.port)
  }

  @Test
  fun `vpn config parser correctly parses openvpn config`() {
    val ovpnConfig = """
      client
      dev tun
      proto udp
      remote node1.nexusvpn.net 1194
      resolv-retry infinite
      nobind
      cipher AES-256-GCM
    """.trimIndent()

    val result = VpnConfigParser.validateAndParse(ovpnConfig)
    assertTrue(result.isValid)
    val parsed = result.parsedConfig
    assertNotNull(parsed)
    assertEquals(VpnProtocol.OPENVPN_UDP, parsed?.protocol)
    assertEquals("node1.nexusvpn.net", parsed?.host)
    assertEquals(1194, parsed?.port)
  }

  @Test
  fun `server repository has populated server list and custom server import`() {
    val servers = ServerRepository.servers.value
    assertTrue("Default servers should be populated", servers.size >= 10)

    val firstServer = servers.first()
    assertEquals("First server must be free tier", ServerType.FREE, firstServer.serverType)
    assertTrue("First server must be high fast (ping <= 20ms)", firstServer.ping <= 20)
    assertTrue("First server must be low load (load <= 20%)", firstServer.load <= 20)
    assertEquals(VpnProtocol.WIREGUARD, firstServer.protocol)

    val customConfig = """
      [Interface]
      PrivateKey = bG9jYWxwcml2YXRla2V5MTIzNDU2Nzg5MDEyMzQ1Njc4OTA=
      Address = 10.8.0.99/24
      [Peer]
      PublicKey = cHVibGlja2V5MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NQ==
      Endpoint = custom-vpn.example.com:51820
      AllowedIPs = 0.0.0.0/0
    """.trimIndent()

    val importResult = ServerRepository.importCustomServer(
      name = "Custom Frankfurt Node",
      country = "Germany",
      countryCode = "DE",
      city = "Frankfurt",
      flag = "🇩🇪",
      rawConfig = customConfig,
      serverType = ServerType.FREE
    )

    assertTrue("Custom server should import successfully", importResult.isSuccess)
    val importedServer = importResult.getOrNull()
    assertNotNull(importedServer)
    assertEquals("Custom Frankfurt Node", importedServer?.serverName)
    assertEquals("custom-vpn.example.com", importedServer?.host)
    assertEquals(ServerRepository.selectedServer.value.serverId, importedServer?.serverId)
  }
}
