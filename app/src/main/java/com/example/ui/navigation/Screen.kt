package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object ServerList : Screen("server_list")
    object Premium : Screen("premium")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object HelpSupport : Screen("help_support")
    object About : Screen("about")
    object PrivacyPolicy : Screen("privacy_policy")
    object Terms : Screen("terms")
}
