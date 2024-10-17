package uz.unnarsx.komarugram.core.helpers

import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.URLSpan
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import org.telegram.messenger.LocaleController
import org.telegram.messenger.LocaleController.getString
import org.telegram.messenger.R
import org.telegram.ui.Components.URLSpanNoUnderline
import org.telegram.ui.LauncherIconController
import uz.unnarsx.komarugram.core.configs.komarugramAppearanceConfig
import uz.unnarsx.komarugram.core.configs.komarugramChatsConfig
import uz.unnarsx.komarugram.core.configs.komarugramCameraConfig
import uz.unnarsx.komarugram.core.configs.komarugramCoreConfig
import uz.unnarsx.komarugram.core.configs.komarugramExperimentalConfig
import java.util.Calendar
import java.util.Date

object CGResourcesHelper {

    /** About app start **/
    fun getAppName(): String {
        if (komarugramCoreConfig.isStandaloneStableBuild() || komarugramCoreConfig.isPlayStoreBuild()) {
            return "komarugram"
        } else if (komarugramCoreConfig.isStandaloneBetaBuild()) {
            return "komarugram Beta"
        } else if (komarugramCoreConfig.isStandalonePremiumBuild()) {
            return "komarugram Premium"
        } else if (komarugramCoreConfig.isDevBuild()) {
            return "komarugram Dev"
        }
        return getString(R.string.CG_AppName)
    }

    fun getBuildType(): String {
        if (komarugramCoreConfig.isStandaloneStableBuild()) {
            return getString(R.string.UP_BTRelease)
        } else if (komarugramCoreConfig.isPlayStoreBuild()) {
            return "Play Store"
        } else if (komarugramCoreConfig.isStandaloneBetaBuild()) {
            return getString(R.string.UP_BTBeta)
        } else if (komarugramCoreConfig.isStandalonePremiumBuild()) {
            return "Premium"
        } else if (komarugramCoreConfig.isDevBuild()) {
            return "Dev"
        }
        return "Unknown"
    }

    @Suppress("DEPRECATION")
    fun getAbiCode(): String {
        var abi = ""
        try {
            when (ApplicationLoader.applicationContext.packageManager.getPackageInfo(
                ApplicationLoader.applicationContext.packageName, 0).versionCode % 10) {
                1, 3 -> abi = "armeabi-v7a"
                2, 4 -> abi = "x86"
                5, 7 -> abi = "arm64-v8a"
                6, 8 -> abi = "x86_64"
                0, 9 -> abi = "universal"
            }
        } catch (e: java.lang.Exception) {
            FileLog.e(e)
        }
        return abi
    }
    /** About app finish **/

    /** Camera start **/
    @JvmStatic
    fun getCameraName(): String { // Crashlytics.java:\ Camera type
        return when (komarugramCameraConfig.cameraType) {
            komarugramCameraConfig.TELEGRAM_CAMERA -> "Telegram"
            komarugramCameraConfig.CAMERA_X -> "CameraX"
            komarugramCameraConfig.CAMERA_2 -> "Camera 2 (Telegram)"
            else -> getString(R.string.CP_CameraTypeSystem)
        }
    }

    @JvmStatic
    fun getCameraAdvise(): CharSequence {
        val advise: String = when (komarugramCameraConfig.cameraType) {
            komarugramCameraConfig.TELEGRAM_CAMERA -> getString(R.string.CP_DefaultCameraDesc)
            komarugramCameraConfig.CAMERA_X -> getString(R.string.CP_CameraXDesc)
            komarugramCameraConfig.CAMERA_2 -> getString(R.string.CP_Camera2Desc)
            else -> getString(R.string.CP_SystemCameraDesc)
        }

        val htmlParsed: Spannable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SpannableString(Html.fromHtml(advise, Html.FROM_HTML_MODE_LEGACY))
        } else {
            SpannableString(Html.fromHtml(advise))
        }
        return getUrlNoUnderlineText(htmlParsed)
    }

    @JvmStatic
    fun getCameraAspectRatio(): String { // CameraPreferences.java:\Camera aspect ratio
        return when (komarugramCameraConfig.cameraAspectRatio) {
            komarugramCameraConfig.Camera1to1 -> "1:1"
            komarugramCameraConfig.Camera4to3 -> "4:3"
            komarugramCameraConfig.Camera16to9 -> "16:9"
            else -> getString(R.string.Default)
        }
    }

    @JvmStatic
    fun getCameraXFpsRange(): String { //CameraPreferences.java:\CameraX FPS
        return when (komarugramCameraConfig.cameraXFpsRange) {
            komarugramCameraConfig.CameraXFpsRange25to30 -> "25-30"
            komarugramCameraConfig.CameraXFpsRange30to60 -> "30-60"
            komarugramCameraConfig.CameraXFpsRange60to60 -> "60-60"
            else -> getString("Default", R.string.Default)
        }
    }

    @JvmStatic
    fun getCameraXCameraEffect(): String { //CameraPreferences.java:\CameraX Camera effect
        return when (komarugramCameraConfig.cameraXCameraEffect) {
            komarugramCameraConfig.CONTROL_EFFECT_MODE_MONO -> "MONO"
            komarugramCameraConfig.CONTROL_EFFECT_MODE_NEGATIVE -> "NEGATIVE"
            komarugramCameraConfig.CONTROL_EFFECT_MODE_SOLARIZE -> "SOLARIZE"
            komarugramCameraConfig.CONTROL_EFFECT_MODE_SEPIA -> "SEPIA"
            komarugramCameraConfig.CONTROL_EFFECT_MODE_POSTERIZE -> "POSTERIZE"
            komarugramCameraConfig.CONTROL_EFFECT_MODE_WHITEBOARD -> "WHITEBOARD"
            komarugramCameraConfig.CONTROL_EFFECT_MODE_BLACKBOARD -> "BLACKBOARD"
            komarugramCameraConfig.CONTROL_EFFECT_MODE_AQUA -> "AQUA"
            else -> getString("Default", R.string.Default)
        }
    }

    @JvmStatic
    fun getExposureSliderPosition(): String { // CameraPreferences.java:\Exposure slider
        return when (komarugramCameraConfig.exposureSlider) {
//            komarugramCameraConfig.EXPOSURE_SLIDER_BOTTOM -> getString(R.string.CP_ZoomSliderPosition_Bottom)
            komarugramCameraConfig.EXPOSURE_SLIDER_RIGHT -> getString(R.string.CP_ZoomSliderPosition_Right)
//            komarugramCameraConfig.EXPOSURE_SLIDER_LEFT -> getString(R.string.CP_ZoomSliderPosition_Left)
            else -> getString(R.string.Disable)
        }
    }


    @JvmStatic
    fun getCameraCaptureTypeFront(): String { // CameraPreferences.java:\Camera capture type
        return when (komarugramCameraConfig.captureTypeFront) {
            komarugramCameraConfig.CaptureType_ImageCapture -> "ImageCapture"
            else -> "VideoCapture"
        }
    }

    @JvmStatic
    fun getCameraCaptureTypeBack(): String { // CameraPreferences.java:\Camera capture type
        return when (komarugramCameraConfig.captureTypeBack) {
            komarugramCameraConfig.CaptureType_ImageCapture -> "ImageCapture"
            else -> "VideoCapture"
        }
    }
    /** Camera finish **/

    /** Chats start **/
    @JvmStatic
    fun getLeftButtonText(): String {
        return when (komarugramChatsConfig.leftBottomButton) {
            komarugramChatsConfig.LEFT_BUTTON_REPLY -> getString(R.string.Reply)
            komarugramChatsConfig.LEFT_BUTTON_SAVE_MESSAGE -> getString(R.string.CG_ToSaved)
            komarugramChatsConfig.LEFT_BUTTON_DIRECT_SHARE -> getString(R.string.DirectShare)
            else -> AndroidUtilities.capitalize(getString(R.string.CG_Without_Authorship))
        }
    }

    @JvmStatic
    fun getLeftButtonDrawable(): Int {
        return when (komarugramChatsConfig.leftBottomButton) {
            komarugramChatsConfig.LEFT_BUTTON_REPLY -> R.drawable.input_reply
            komarugramChatsConfig.LEFT_BUTTON_SAVE_MESSAGE -> R.drawable.msg_saved
            komarugramChatsConfig.LEFT_BUTTON_DIRECT_SHARE -> R.drawable.msg_share
            else -> R.drawable.input_reply
        }
    }

    @JvmStatic
    fun getReplyIconDrawable(): Int {
        return when (komarugramChatsConfig.messageSlideAction) {
            komarugramChatsConfig.MESSAGE_SLIDE_ACTION_SAVE -> R.drawable.msg_saved_filled_solar
            komarugramChatsConfig.MESSAGE_SLIDE_ACTION_DIRECT_SHARE -> R.drawable.msg_share_filled
            komarugramChatsConfig.MESSAGE_SLIDE_ACTION_TRANSLATE -> R.drawable.msg_translate_filled_solar
            else -> R.drawable.filled_button_reply
        }
    }
    /** Chats finish **/

    /** Profile activity start **/
    fun getDCGeo(dcId: Int): String {
        return when (dcId) {
            1, 3 -> "USA (Miami)"
            2, 4 -> "NLD (Amsterdam)"
            5 -> "SGP (Singapore)"
            else -> "UNK (Unknown)"
        }
    }

    fun getDCName(dc: Int): String {
        return when (dc) {
            1 -> "Pluto"
            2 -> "Venus"
            3 -> "Aurora"
            4 -> "Vesta"
            5 -> "Flora"
            else -> getString(R.string.NumberUnknown)
        }
    }
    /** Profile activity finish **/

    /** Misc start **/
    @JvmStatic
    fun getProperNotificationIcon(): Int { // App notification icon
        return if (komarugramCoreConfig.oldNotificationIcon) {
            R.drawable.notification
        } else {
            return if (LauncherIconController.isEnabled(LauncherIconController.LauncherIcon.DARK_CHERRY_BRA)
                || LauncherIconController.isEnabled(LauncherIconController.LauncherIcon.WHITE_CHERRY_BRA)
                || LauncherIconController.isEnabled(LauncherIconController.LauncherIcon.VIOLET_SUNSET_CHERRY_BRA)
            )
                R.drawable.cg_notification_bra else R.drawable.cg_notification
        }
    }

    @JvmStatic
    fun getResidentNotificationIcon(): Int {
        return if (komarugramCoreConfig.oldNotificationIcon) R.drawable.cg_notification else R.drawable.notification
    }

    @JvmStatic
    fun getDownloadSpeedBoostText(): String { // ExperimentalPreferences.java:\Download speed boost
        return when (komarugramExperimentalConfig.downloadSpeedBoost) {
            komarugramExperimentalConfig.BOOST_NONE -> getString(R.string.EP_DownloadSpeedBoostNone)
            komarugramExperimentalConfig.BOOST_AVERAGE -> getString(R.string.EP_DownloadSpeedBoostAverage)
            else -> getString(R.string.EP_DownloadSpeedBoostExtreme)
        }
    }

    @JvmStatic
    fun getShowDcIdText(): String { // MessagesAndProfilesPreferencesEntry.java:\Show dc id
        return when (komarugramAppearanceConfig.showIDDC) {
            komarugramAppearanceConfig.ID_ONLY -> "ID"
            komarugramAppearanceConfig.ID_DC -> "ID + DC"
            else -> getString(R.string.Disable)
        }
    }

    fun createDateAndTime(date: Long): String {
        var dateAndTime = date
        try {
            dateAndTime *= 1000
            val rightNow = Calendar.getInstance()
            rightNow.timeInMillis = dateAndTime
            return String.format("%1\$s | %2\$s", LocaleController.getInstance().formatterYear.format(
                Date(dateAndTime)
            ),
                LocaleController.getInstance().formatterDay.format(Date(dateAndTime))
            )
        } catch (ignore: Exception) { }
        return "LOC_ERR"
    }

    fun createDateAndTimeForJSON(date: Long): String {
        var dateAndTime = date
        try {
            dateAndTime *= 1000
            val rightNow = Calendar.getInstance()
            rightNow.timeInMillis = dateAndTime
            return String.format("%1\$s | %2\$s", LocaleController.getInstance().formatterYear.format(
                Date(dateAndTime)
            ),
                LocaleController.getInstance().formatterDayWithSeconds.format(Date(dateAndTime))
            )
        } catch (ignore: Exception) { }
        return "LOC_ERR"
    }

    fun getUrlNoUnderlineText(charSequence: CharSequence): CharSequence {
        val spannable: Spannable = SpannableString(charSequence)
        val spans = spannable.getSpans(0, charSequence.length, URLSpan::class.java)
        for (urlSpan in spans) {
            var span = urlSpan
            val start = spannable.getSpanStart(span)
            val end = spannable.getSpanEnd(span)
            spannable.removeSpan(span)
            span = object : URLSpanNoUnderline(span.url) {
            }
            spannable.setSpan(span, start, end, 0)
        }
        return spannable
    }
    /** Misc finish **/
}