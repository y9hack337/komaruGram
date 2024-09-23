package uz.unnarsx.cherrygram.core.configs

import android.app.Activity
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.MessagesController
import org.telegram.messenger.UserConfig
import uz.unnarsx.cherrygram.core.helpers.FirebaseRemoteConfigHelper
import uz.unnarsx.cherrygram.helpers.CherrygramToasts
import uz.unnarsx.cherrygram.preferences.boolean
import uz.unnarsx.cherrygram.preferences.long

object CherrygramCoreConfig: CoroutineScope by CoroutineScope(
    context = SupervisorJob() + Dispatchers.Main.immediate
) {

    private val sharedPreferences: SharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE)

    fun putBoolean(key: String, value: Boolean) {
        val preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun putStringForUserPrefs(key: String, value: String) {
        val preferences = MessagesController.getMainSettings(UserConfig.selectedAccount)
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /** General start **/
    var noRounding by sharedPreferences.boolean("CP_NoRounding", false)
    var systemEmoji by sharedPreferences.boolean("AP_SystemEmoji", false)
    var systemFonts by sharedPreferences.boolean("AP_SystemFonts", true)
    var oldNotificationIcon by sharedPreferences.boolean("AP_Old_Notification_Icon", false)
    /** General finish **/

    /** Animations and Premium Features start **/
    var hideStories by sharedPreferences.boolean("CP_HideStories", false)
    var customWallpapers by sharedPreferences.boolean("CP_CustomWallpapers", true)
    var disableAnimatedAvatars by sharedPreferences.boolean("CP_DisableAnimAvatars", false)
    var disableReactionsOverlay by sharedPreferences.boolean("CP_DisableReactionsOverlay", false)
    var disableReactionAnim by sharedPreferences.boolean("CP_DisableReactionAnim", false)
    var disablePremStickAnim by sharedPreferences.boolean("CP_DisablePremStickAnim", false)
    var disablePremStickAutoPlay by sharedPreferences.boolean("CP_DisablePremStickAutoPlay", false)
    var hideSendAsChannel by sharedPreferences.boolean("CP_HideSendAsChannel", false)
    /** Animations and Premium Features finish **/

    /** OTA start **/
    var installBetas by sharedPreferences.boolean("CG_Install_Beta_Ver", isStandaloneBetaBuild())
    fun toggleInstallBetas() {
        installBetas = !installBetas
        putBoolean("CG_Install_Beta_Ver", installBetas)
    }

    var autoOTA by sharedPreferences.boolean("CG_Auto_OTA", isStandaloneStableBuild() || isStandaloneBetaBuild() || isDevBuild())
    fun toggleAutoOTA() {
        autoOTA = !autoOTA
        putBoolean("CG_Auto_OTA", autoOTA)
    }

    var lastUpdateCheckTime by sharedPreferences.long("CG_LastUpdateCheckTime", 0)
    var updateScheduleTimestamp by sharedPreferences.long("CG_UpdateScheduleTimestamp", 0)
    /** OTA finish **/

    /** Launch icons (Telegram Chats Settings) start **/
    var filterLauncherIcon by sharedPreferences.boolean("AP_Filter_Launcher_Icon", false)
    fun toggleAppIconFilter() { // Telegram chats settings
        filterLauncherIcon = !filterLauncherIcon
        putBoolean("AP_Filter_Launcher_Icon", filterLauncherIcon)
    }
    /** Launch icons (Telegram Chats Settings) finish **/

    /** Cherrygram build types start **/
    fun isStandaloneStableBuild(): Boolean {
        return ApplicationLoader.isStandaloneBuild() && !isDevBuild() && !isStandalonePremiumBuild() && !isStandaloneBetaBuild()
    }

    fun isStandaloneBetaBuild(): Boolean {
        return false
    }

    fun isDevBuild(): Boolean {
        return false
    }

    fun isStandalonePremiumBuild(): Boolean {
        return false
    }

    fun isPlayStoreBuild(): Boolean {
        return !ApplicationLoader.isStandaloneBuild()
    }
    /** Cherrygram build types finish **/

    init {
        CherrygramToasts.init(sharedPreferences)

        launch {
            if (ApplicationLoader.checkPlayServices()) FirebaseRemoteConfigHelper.initRemoteConfig()
        }
    }

}
