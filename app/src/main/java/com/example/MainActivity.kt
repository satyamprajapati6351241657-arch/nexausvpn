package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.auth.AuthRepository
import com.example.settings.SettingsRepository
import com.example.ui.dialogs.AiDiagnosticsDialog
import com.example.ui.navigation.Screen
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.ForgotPasswordScreen
import com.example.ui.screens.HelpSupportScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PremiumScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ServerListScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SignUpScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TermsScreen
import com.example.ui.theme.NexusVpnTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by SettingsRepository.settings.collectAsState()
            val user by AuthRepository.currentUser.collectAsState()

            // Request Notification permission for Android 13+ foreground service
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { /* Permission response handled */ }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val hasNotificationPermission = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                    if (!hasNotificationPermission) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            NexusVpnTheme(darkTheme = settings.isDarkMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        NexusVpnAppNavigation(
                            isUserPremium = user?.isPremium == true
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NexusVpnAppNavigation(
    isUserPremium: Boolean
) {
    val navController = rememberNavController()
    var showAiDiagnostics by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateNext = { isLoggedIn ->
                    if (isLoggedIn) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onNavigateForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onNavigateLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateServers = { navController.navigate(Screen.ServerList.route) },
                onNavigatePremium = { navController.navigate(Screen.Premium.route) },
                onNavigateProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateSettings = { navController.navigate(Screen.Settings.route) },
                onOpenAiDiagnostics = { showAiDiagnostics = true }
            )
        }

        composable(Screen.ServerList.route) {
            ServerListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigatePremium = { navController.navigate(Screen.Premium.route) },
                isUserPremium = isUserPremium
            )
        }

        composable(Screen.Premium.route) {
            PremiumScreen(
                onNavigateBack = { navController.popBackStack() },
                onUpgradeCompleted = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigatePremium = { navController.navigate(Screen.Premium.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateHelp = { navController.navigate(Screen.HelpSupport.route) },
                onNavigateAbout = { navController.navigate(Screen.About.route) },
                onNavigatePrivacy = { navController.navigate(Screen.PrivacyPolicy.route) },
                onNavigateTerms = { navController.navigate(Screen.Terms.route) }
            )
        }

        composable(Screen.HelpSupport.route) {
            HelpSupportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Terms.route) {
            TermsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }

    if (showAiDiagnostics) {
        AiDiagnosticsDialog(
            onDismiss = { showAiDiagnostics = false },
            onServerSelected = {
                showAiDiagnostics = false
            }
        )
    }
}
