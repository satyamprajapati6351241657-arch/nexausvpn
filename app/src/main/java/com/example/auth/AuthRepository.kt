package com.example.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.models.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.util.UUID

object AuthRepository {

    private const val PREFS_NAME = "nexus_vpn_auth_prefs"
    private const val KEY_USER_ID = "key_user_id"
    private const val KEY_EMAIL = "key_email"
    private const val KEY_NAME = "key_name"
    private const val KEY_PASSWORD_HASH = "key_password_hash"
    private const val KEY_IS_PREMIUM = "key_is_premium"
    private const val KEY_SUBSCRIPTION_PLAN = "key_subscription_plan"
    private const val KEY_EXPIRES_AT = "key_expires_at"
    private const val KEY_DATA_USED = "key_data_used"
    private const val KEY_CREATED_AT = "key_created_at"
    private const val KEY_IS_LOGGED_IN = "key_is_logged_in"

    private var prefs: SharedPreferences? = null

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isLoggedIn = prefs?.getBoolean(KEY_IS_LOGGED_IN, false) ?: false
        if (isLoggedIn) {
            val user = loadUserFromPrefs()
            _currentUser.value = user
        }
    }

    private fun loadUserFromPrefs(): UserProfile? {
        val p = prefs ?: return null
        val email = p.getString(KEY_EMAIL, null) ?: return null
        val id = p.getString(KEY_USER_ID, UUID.randomUUID().toString()) ?: UUID.randomUUID().toString()
        val name = p.getString(KEY_NAME, "Cyber User") ?: "Cyber User"
        val isPremium = p.getBoolean(KEY_IS_PREMIUM, false)
        val plan = p.getString(KEY_SUBSCRIPTION_PLAN, if (isPremium) "VIP Pro Annual" else "Free Shield") ?: "Free Shield"
        val expiresAt = p.getLong(KEY_EXPIRES_AT, System.currentTimeMillis() + 30L * 24 * 3600 * 1000)
        val dataUsed = p.getLong(KEY_DATA_USED, 2_450_000_000L) // 2.45 GB
        val createdAt = p.getLong(KEY_CREATED_AT, System.currentTimeMillis() - 7L * 24 * 3600 * 1000)

        return UserProfile(
            userId = id,
            email = email,
            displayName = name,
            isPremium = isPremium,
            subscriptionPlan = plan,
            expiresAt = expiresAt,
            dataUsedBytes = dataUsed,
            dataLimitBytes = if (isPremium) 0L else 100_000_000_000L, // 100 GB for free, unlimited for VIP
            createdAt = createdAt
        )
    }

    fun login(email: String, pass: String): Result<UserProfile> {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isEmpty() || !cleanEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        if (pass.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        }

        val p = prefs ?: return Result.failure(IllegalStateException("Auth system not initialized."))
        val storedHash = p.getString(KEY_PASSWORD_HASH, null)
        val storedEmail = p.getString(KEY_EMAIL, null)
        val enteredHash = hashPassword(pass)

        // If registered user exists and matches, or first time demo login
        val user = if (storedEmail == cleanEmail && storedHash == enteredHash) {
            loadUserFromPrefs() ?: createDefaultUser(cleanEmail, "Nexus User")
        } else if (storedEmail == null) {
            // First time login with new credentials
            val newUser = createDefaultUser(cleanEmail, cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() })
            saveUserToPrefs(newUser, enteredHash)
            newUser
        } else {
            return Result.failure(IllegalArgumentException("Invalid email or password combination."))
        }

        p.edit().putBoolean(KEY_IS_LOGGED_IN, true).apply()
        _currentUser.value = user
        return Result.success(user)
    }

    fun signUp(name: String, email: String, pass: String, confirmPass: String): Result<UserProfile> {
        val cleanName = name.trim()
        val cleanEmail = email.trim().lowercase()
        if (cleanName.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please enter your display name."))
        }
        if (cleanEmail.isEmpty() || !cleanEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        if (pass.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        }
        if (pass != confirmPass) {
            return Result.failure(IllegalArgumentException("Passwords do not match."))
        }

        val user = createDefaultUser(cleanEmail, cleanName)
        val hash = hashPassword(pass)
        saveUserToPrefs(user, hash)
        prefs?.edit()?.putBoolean(KEY_IS_LOGGED_IN, true)?.apply()
        _currentUser.value = user
        return Result.success(user)
    }

    fun guestLogin(): UserProfile {
        val guestUser = UserProfile(
            userId = "guest_" + UUID.randomUUID().toString().take(6),
            email = "guest@nexusvpn.local",
            displayName = "Guest Explorer",
            isPremium = false,
            subscriptionPlan = "Free Tier",
            expiresAt = System.currentTimeMillis() + 14L * 24 * 3600 * 1000,
            dataUsedBytes = 512_000_000L,
            dataLimitBytes = 50_000_000_000L,
            createdAt = System.currentTimeMillis()
        )
        saveUserToPrefs(guestUser, hashPassword("guest123"))
        prefs?.edit()?.putBoolean(KEY_IS_LOGGED_IN, true)?.apply()
        _currentUser.value = guestUser
        return guestUser
    }

    fun forgotPassword(email: String): Result<String> {
        val clean = email.trim().lowercase()
        if (clean.isEmpty() || !clean.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email."))
        }
        return Result.success("Password reset instructions have been dispatched to $clean. Please check your inbox.")
    }

    fun updateProfile(name: String): Result<Boolean> {
        val current = _currentUser.value ?: return Result.failure(IllegalStateException("No user logged in"))
        val updated = current.copy(displayName = name.trim())
        _currentUser.value = updated
        prefs?.edit()?.putString(KEY_NAME, name.trim())?.apply()
        return Result.success(true)
    }

    fun upgradeToPremium(plan: String): Result<Boolean> {
        val current = _currentUser.value ?: return Result.failure(IllegalStateException("No user logged in"))
        val updated = current.copy(
            isPremium = true,
            subscriptionPlan = plan,
            expiresAt = System.currentTimeMillis() + 365L * 24 * 3600 * 1000,
            dataLimitBytes = 0L
        )
        _currentUser.value = updated
        prefs?.edit()
            ?.putBoolean(KEY_IS_PREMIUM, true)
            ?.putString(KEY_SUBSCRIPTION_PLAN, plan)
            ?.putLong(KEY_EXPIRES_AT, updated.expiresAt)
            ?.apply()
        return Result.success(true)
    }

    fun logout() {
        prefs?.edit()?.putBoolean(KEY_IS_LOGGED_IN, false)?.apply()
        _currentUser.value = null
    }

    private fun createDefaultUser(email: String, name: String): UserProfile {
        return UserProfile(
            userId = "usr_" + UUID.randomUUID().toString().take(8),
            email = email,
            displayName = name,
            isPremium = false,
            subscriptionPlan = "Free Tier",
            expiresAt = System.currentTimeMillis() + 30L * 24 * 3600 * 1000,
            dataUsedBytes = 1_250_000_000L,
            dataLimitBytes = 100_000_000_000L,
            createdAt = System.currentTimeMillis()
        )
    }

    private fun saveUserToPrefs(user: UserProfile, passHash: String) {
        prefs?.edit()
            ?.putString(KEY_USER_ID, user.userId)
            ?.putString(KEY_EMAIL, user.email)
            ?.putString(KEY_NAME, user.displayName)
            ?.putString(KEY_PASSWORD_HASH, passHash)
            ?.putBoolean(KEY_IS_PREMIUM, user.isPremium)
            ?.putString(KEY_SUBSCRIPTION_PLAN, user.subscriptionPlan)
            ?.putLong(KEY_EXPIRES_AT, user.expiresAt)
            ?.putLong(KEY_DATA_USED, user.dataUsedBytes)
            ?.putLong(KEY_CREATED_AT, user.createdAt)
            ?.apply()
    }

    private fun hashPassword(password: String): String {
        val salt = "nexus_vpn_secure_salt_#99!"
        val bytes = (password + salt).toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
