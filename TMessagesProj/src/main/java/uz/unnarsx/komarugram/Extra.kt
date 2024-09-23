package uz.unnarsx.komarugram

import android.app.Activity
import org.telegram.ui.ActionBar.BaseFragment

object Extra {
    // https://core.telegram.org/api/obtaining_api_id
    const val APP_ID = 20402099
    const val APP_HASH = "133853bc532929a1f0177dec429ae40f"

    // https://developers.google.com/identity/sms-retriever/verify#computing_your_apps_hash_string
    const val SMS_HASH = ""

    const val PLAYSTORE_APP_URL = ""

    const val PACKAGE_HASH = ""

    fun getRegistrationDate(fragment: BaseFragment, parentActivity: Activity, userId: Long): String {
        return "2023-10-27";
    }

    fun addBirthdayToCalendar(parentActivity: Activity, userId: Long) {

    }
}
