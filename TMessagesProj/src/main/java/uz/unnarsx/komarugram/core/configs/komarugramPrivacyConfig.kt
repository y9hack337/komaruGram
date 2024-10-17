package uz.unnarsx.komarugram.core.configs

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.telegram.messenger.ApplicationLoader
import uz.unnarsx.komarugram.chats.helpers.ChatsPasswordHelper
import uz.unnarsx.komarugram.core.helpers.FirebaseAnalyticsHelper
import uz.unnarsx.komarugram.helpers.komarugramToasts
import uz.unnarsx.komarugram.preferences.boolean

object komarugramPrivacyConfig: CoroutineScope by CoroutineScope(
    context = SupervisorJob() + Dispatchers.Main.immediate
) {

    private val sharedPreferences: SharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE)

    /** Privacy start **/
    var hideProxySponsor by sharedPreferences.boolean("SP_NoProxyPromo", true)
    var googleAnalytics by sharedPreferences.boolean("SP_GoogleAnalytics", ApplicationLoader.checkPlayServices())
    /** Privacy finish **/

    /** Passcode lock start **/
    var askForPasscodeBeforeOpenChat by sharedPreferences.boolean("askForPasscodeBeforeOpenChat", false)
    private var tweakPasscodeChatsArray by sharedPreferences.boolean("tweakPasscodeChatsArray", false)
    var askPasscodeBeforeDelete by sharedPreferences.boolean("SP_AskPinBeforeDelete", false)
    var allowSystemPasscode by sharedPreferences.boolean("SP_AllowSystemPasscode", false)
    /** Passcode lock finish **/

    init {
        komarugramToasts.init(sharedPreferences)

        launch {
            if (googleAnalytics && ApplicationLoader.checkPlayServices()) {
                FirebaseAnalyticsHelper.start(ApplicationLoader.applicationContext)
                FirebaseAnalyticsHelper.trackEvent("cg_start", Bundle.EMPTY)
                if (komarugramCoreConfig.isDevBuild() || komarugramDebugConfig.showRPCErrors) {
                    Toast.makeText(ApplicationLoader.applicationContext, "cg_start", Toast.LENGTH_SHORT).show()
                }
            }

            if (!tweakPasscodeChatsArray) {
                val arr: ArrayList<String?> = ArrayList()
                arr.add("0")
                ChatsPasswordHelper.saveArrayList(arr, ChatsPasswordHelper.Passcode_Array)
                tweakPasscodeChatsArray = true
            }
        }
    }

}