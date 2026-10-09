package com.example.vpn

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.VpnService
import androidx.activity.result.ActivityResultLauncher
import com.example.models.VpnConnectionStats
import com.example.models.VpnServer
import com.example.models.VpnState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object VpnConnectionManager {

    private val _connectionStats = MutableStateFlow(VpnConnectionStats())
    val connectionStats: StateFlow<VpnConnectionStats> = _connectionStats.asStateFlow()

    private var pendingServerToConnect: VpnServer? = null

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val activeNetwork = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Checks if VpnService permission is required.
     * Returns an Intent if permission dialog needs to be displayed, or null if already granted.
     */
    fun checkVpnPermission(context: Context): Intent? {
        return VpnService.prepare(context)
    }

    /**
     * Triggers connection. If permission is needed, launches the permission contract.
     * Otherwise starts NexusVpnService immediately.
     */
    fun startConnection(
        context: Context,
        server: VpnServer,
        permissionLauncher: ActivityResultLauncher<Intent>? = null
    ): Boolean {
        if (!server.enabled) {
            _connectionStats.value = _connectionStats.value.copy(
                state = VpnState.CONNECTION_FAILED,
                errorMessage = "Selected server is currently disabled by administrator."
            )
            return false
        }

        if (!isNetworkAvailable(context)) {
            _connectionStats.value = _connectionStats.value.copy(
                state = VpnState.CONNECTION_FAILED,
                errorMessage = "Network is unavailable. Please check your Wi-Fi or cellular connection."
            )
            return false
        }

        pendingServerToConnect = server
        val prepareIntent = checkVpnPermission(context)
        if (prepareIntent != null) {
            if (permissionLauncher != null) {
                permissionLauncher.launch(prepareIntent)
                return false
            } else if (context is Activity) {
                context.startActivity(prepareIntent)
                return false
            }
        }

        // Permission already granted, proceed directly
        dispatchConnectService(context, server)
        return true
    }

    fun onVpnPermissionResult(context: Context, granted: Boolean) {
        val server = pendingServerToConnect
        if (granted && server != null) {
            dispatchConnectService(context, server)
        } else {
            _connectionStats.value = _connectionStats.value.copy(
                state = VpnState.CONNECTION_FAILED,
                errorMessage = "VPN permission is required to create a secure encrypted tunnel."
            )
        }
        pendingServerToConnect = null
    }

    private fun dispatchConnectService(context: Context, server: VpnServer) {
        _connectionStats.value = VpnConnectionStats(
            state = VpnState.CONNECTING,
            connectedServer = server
        )

        val serviceIntent = Intent(context, NexusVpnService::class.java).apply {
            action = NexusVpnService.ACTION_CONNECT
            putExtra(NexusVpnService.EXTRA_SERVER_ID, server.serverId)
            putExtra(NexusVpnService.EXTRA_SERVER_NAME, server.serverName)
            putExtra(NexusVpnService.EXTRA_SERVER_COUNTRY, server.country)
            putExtra(NexusVpnService.EXTRA_SERVER_CITY, server.city)
            putExtra(NexusVpnService.EXTRA_SERVER_HOST, server.host)
            putExtra(NexusVpnService.EXTRA_SERVER_PORT, server.port)
            putExtra(NexusVpnService.EXTRA_SERVER_PROTOCOL, server.protocol.name)
            putExtra(NexusVpnService.EXTRA_SERVER_TYPE, server.serverType.name)
            putExtra(NexusVpnService.EXTRA_SERVER_CONFIG, server.configuration)
            putExtra(NexusVpnService.EXTRA_SERVER_FLAG, server.flagEmoji)
            putExtra(NexusVpnService.EXTRA_SERVER_PING, server.ping)
        }

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } catch (e: Exception) {
            _connectionStats.value = _connectionStats.value.copy(
                state = VpnState.CONNECTION_FAILED,
                errorMessage = e.message ?: "Unable to launch VPN service"
            )
        }
    }

    fun stopConnection(context: Context) {
        val serviceIntent = Intent(context, NexusVpnService::class.java).apply {
            action = NexusVpnService.ACTION_DISCONNECT
        }
        try {
            context.startService(serviceIntent)
        } catch (_: Exception) {}
        onServiceDisconnected()
    }

    // Called by NexusVpnService callbacks
    fun onServiceConnecting(server: VpnServer) {
        _connectionStats.value = _connectionStats.value.copy(
            state = VpnState.CONNECTING,
            connectedServer = server,
            errorMessage = null
        )
    }

    fun onServiceConnected(server: VpnServer) {
        _connectionStats.value = _connectionStats.value.copy(
            state = VpnState.CONNECTED,
            connectedServer = server,
            errorMessage = null
        )
    }

    fun onServiceConnectionFailed(error: String) {
        _connectionStats.value = _connectionStats.value.copy(
            state = VpnState.CONNECTION_FAILED,
            errorMessage = error
        )
    }

    fun onServiceDisconnected() {
        _connectionStats.value = VpnConnectionStats(
            state = VpnState.DISCONNECTED,
            errorMessage = null
        )
    }

    fun updateStats(
        durationSeconds: Long,
        downSpeed: Long,
        upSpeed: Long,
        totalDown: Long,
        totalUp: Long
    ) {
        val current = _connectionStats.value
        if (current.state == VpnState.CONNECTED) {
            _connectionStats.value = current.copy(
                durationSeconds = durationSeconds,
                downloadSpeedBps = downSpeed,
                uploadSpeedBps = upSpeed,
                totalDownloadedBytes = totalDown,
                totalUploadedBytes = totalUp
            )
        }
    }

    fun formatSpeed(bytesPerSec: Long): String {
        return when {
            bytesPerSec >= 1_000_000 -> String.format("%.1f MB/s", bytesPerSec / 1_000_000.0)
            bytesPerSec >= 1_000 -> String.format("%.1f KB/s", bytesPerSec / 1_000.0)
            else -> "$bytesPerSec B/s"
        }
    }

    fun formatDataSize(bytes: Long): String {
        return when {
            bytes >= 1_073_741_824L -> String.format("%.2f GB", bytes / 1_073_741_824.0)
            bytes >= 1_048_576L -> String.format("%.1f MB", bytes / 1_048_576.0)
            bytes >= 1_024L -> String.format("%.1f KB", bytes / 1_024.0)
            else -> "$bytes B"
        }
    }

    fun formatDuration(durationSeconds: Long): String {
        val hours = durationSeconds / 3600
        val mins = (durationSeconds % 3600) / 60
        val secs = durationSeconds % 60
        return String.format("%02d:%02d:%02d", hours, mins, secs)
    }
}
