package com.example

import android.app.Application
import com.example.auth.AuthRepository
import com.example.settings.SettingsRepository

class NexusVpnApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AuthRepository.init(this)
        SettingsRepository.init(this)
    }
}
