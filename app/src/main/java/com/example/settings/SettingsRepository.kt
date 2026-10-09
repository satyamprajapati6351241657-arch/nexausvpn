package com.example.settings

import android.content.Context
import android.content.SharedPreferences
import com.example.models.AppSettings
import com.example.models.VpnProtocol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SettingsRepository {

    private const val PREFS_NAME = "nexus_vpn_settings_prefs"
    private const val KEY_AUTO_CONNECT = "key_auto_connect"
    private const val KEY_CONNECT_ON_START = "key_connect_on_start"
    private const val KEY_KILL_SWITCH = "key_kill_switch"
    private const val KEY_PROTOCOL = "key_protocol"
    private const val KEY_SPLIT_TUNNELING = "key_split_tunneling"
    private const val KEY_NOTIFICATIONS = "key_notifications"
    private const val KEY_DARK_MODE = "key_dark_mode"
    private const val KEY_LANGUAGE = "key_language"
    private const val KEY_DNS = "key_dns"
    private const val KEY_ADMIN_MODE = "key_admin_mode"

    private var prefs: SharedPreferences? = null

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadSettings()
    }

    private fun loadSettings() {
        val p = prefs ?: return
        val protoStr = p.getString(KEY_PROTOCOL, VpnProtocol.WIREGUARD.name) ?: VpnProtocol.WIREGUARD.name
        val proto = try { VpnProtocol.valueOf(protoStr) } catch (_: Exception) { VpnProtocol.WIREGUARD }

        _settings.value = AppSettings(
            autoConnect = p.getBoolean(KEY_AUTO_CONNECT, false),
            connectOnAppStart = p.getBoolean(KEY_CONNECT_ON_START, false),
            killSwitchEnabled = p.getBoolean(KEY_KILL_SWITCH, true),
            selectedProtocol = proto,
            splitTunneling = p.getBoolean(KEY_SPLIT_TUNNELING, false),
            notificationsEnabled = p.getBoolean(KEY_NOTIFICATIONS, true),
            isDarkMode = p.getBoolean(KEY_DARK_MODE, true),
            appLanguage = p.getString(KEY_LANGUAGE, "English") ?: "English",
            dnsProvider = p.getString(KEY_DNS, "Cloudflare (1.1.1.1)") ?: "Cloudflare (1.1.1.1)",
            adminModeEnabled = p.getBoolean(KEY_ADMIN_MODE, true)
        )
    }

    fun updateAutoConnect(enabled: Boolean) {
        _settings.value = _settings.value.copy(autoConnect = enabled)
        prefs?.edit()?.putBoolean(KEY_AUTO_CONNECT, enabled)?.apply()
    }

    fun updateConnectOnStart(enabled: Boolean) {
        _settings.value = _settings.value.copy(connectOnAppStart = enabled)
        prefs?.edit()?.putBoolean(KEY_CONNECT_ON_START, enabled)?.apply()
    }

    fun updateKillSwitch(enabled: Boolean) {
        _settings.value = _settings.value.copy(killSwitchEnabled = enabled)
        prefs?.edit()?.putBoolean(KEY_KILL_SWITCH, enabled)?.apply()
    }

    fun updateProtocol(protocol: VpnProtocol) {
        _settings.value = _settings.value.copy(selectedProtocol = protocol)
        prefs?.edit()?.putString(KEY_PROTOCOL, protocol.name)?.apply()
    }

    fun updateSplitTunneling(enabled: Boolean) {
        _settings.value = _settings.value.copy(splitTunneling = enabled)
        prefs?.edit()?.putBoolean(KEY_SPLIT_TUNNELING, enabled)?.apply()
    }

    fun updateNotifications(enabled: Boolean) {
        _settings.value = _settings.value.copy(notificationsEnabled = enabled)
        prefs?.edit()?.putBoolean(KEY_NOTIFICATIONS, enabled)?.apply()
    }

    fun updateDarkMode(isDark: Boolean) {
        _settings.value = _settings.value.copy(isDarkMode = isDark)
        prefs?.edit()?.putBoolean(KEY_DARK_MODE, isDark)?.apply()
    }

    fun updateLanguage(lang: String) {
        _settings.value = _settings.value.copy(appLanguage = lang)
        prefs?.edit()?.putString(KEY_LANGUAGE, lang)?.apply()
    }

    fun updateDns(dns: String) {
        _settings.value = _settings.value.copy(dnsProvider = dns)
        prefs?.edit()?.putString(KEY_DNS, dns)?.apply()
    }

    fun updateAdminMode(enabled: Boolean) {
        _settings.value = _settings.value.copy(adminModeEnabled = enabled)
        prefs?.edit()?.putBoolean(KEY_ADMIN_MODE, enabled)?.apply()
    }

    fun clearAppData() {
        prefs?.edit()?.clear()?.apply()
        loadSettings()
    }
}
