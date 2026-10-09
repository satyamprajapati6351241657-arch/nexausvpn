package com.example.ads

import android.app.Activity
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AdNetwork(val title: String) {
    NONE("Ad Placeholder Mode"),
    ADMOB("Google AdMob"),
    APPLOVIN("AppLovin MAX"),
    UNITY("Unity Ads"),
    CUSTOM("Custom Ad Provider")
}

data class AdConfig(
    val adNetwork: AdNetwork = AdNetwork.NONE,
    val bannerAdUnitId: String = "ca-app-pub-placeholder/banner",
    val interstitialAdUnitId: String = "ca-app-pub-placeholder/interstitial",
    val rewardedAdUnitId: String = "ca-app-pub-placeholder/rewarded",
    val isAdsEnabledForFreeUsers: Boolean = true,
    val minIntervalBetweenInterstitialsMs: Long = 60_000L // Prevent ad spam
)

object AdsManager {

    private val _adConfig = MutableStateFlow(AdConfig())
    val adConfig: StateFlow<AdConfig> = _adConfig.asStateFlow()

    private var lastInterstitialShownTime = 0L

    fun configureAdNetwork(
        network: AdNetwork,
        bannerId: String,
        interstitialId: String,
        rewardedId: String
    ) {
        _adConfig.value = _adConfig.value.copy(
            adNetwork = network,
            bannerAdUnitId = bannerId,
            interstitialAdUnitId = interstitialId,
            rewardedAdUnitId = rewardedId
        )
    }

    /**
     * Safe Interstitial trigger.
     * Guaranteed NOT to block critical VPN actions (like Disconnect).
     */
    fun showInterstitialIfAppropriate(
        context: Context,
        isUserPremium: Boolean,
        actionTag: String,
        onAdDismissed: () -> Unit
    ) {
        if (isUserPremium || !_adConfig.value.isAdsEnabledForFreeUsers) {
            onAdDismissed()
            return
        }

        val now = System.currentTimeMillis()
        if (now - lastInterstitialShownTime < _adConfig.value.minIntervalBetweenInterstitialsMs) {
            // Respect cool down period
            onAdDismissed()
            return
        }

        lastInterstitialShownTime = now

        // If actual SDK is connected later, call its show method here.
        // For now, cleanly invoke dismissal callback without interrupting user flow.
        onAdDismissed()
    }

    /**
     * Rewarded Ad Trigger: e.g. "Watch a video to unlock 1 hour of VIP Server access"
     */
    fun showRewardedAd(
        activity: Activity,
        isUserPremium: Boolean,
        onRewardEarned: (rewardAmount: Int) -> Unit,
        onAdClosed: () -> Unit
    ) {
        if (isUserPremium) {
            onRewardEarned(1)
            onAdClosed()
            return
        }

        // When user configures ad network, real SDK loader attaches here.
        // For now, simulate clean rewarded interaction container:
        onRewardEarned(1)
        onAdClosed()
    }
}
