package uz.unnarsx.komarugram.preferences

import android.media.MediaPlayer
import android.view.HapticFeedbackConstants
import androidx.core.util.Pair
import org.telegram.messenger.LocaleController.getString
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.BaseFragment
import uz.unnarsx.komarugram.core.configs.komarugramChatsConfig
import uz.unnarsx.komarugram.core.VibrateUtil
import uz.unnarsx.komarugram.core.helpers.AppRestartHelper
import uz.unnarsx.komarugram.preferences.helpers.AlertDialogSwitchers
import uz.unnarsx.komarugram.preferences.tgkit.preference.category
import uz.unnarsx.komarugram.preferences.tgkit.preference.contract
import uz.unnarsx.komarugram.preferences.tgkit.preference.hint
import uz.unnarsx.komarugram.preferences.tgkit.preference.list
import uz.unnarsx.komarugram.preferences.tgkit.preference.slider
import uz.unnarsx.komarugram.preferences.tgkit.preference.switch
import uz.unnarsx.komarugram.preferences.tgkit.preference.textIcon
import uz.unnarsx.komarugram.preferences.tgkit.preference.tgKitScreen
import uz.unnarsx.komarugram.preferences.tgkit.preference.types.TGKitSliderPreference.TGSLContract
import uz.unnarsx.komarugram.preferences.tgkit.preference.types.TGKitTextIconRow

class ChatsPreferencesEntry : BasePreferencesEntry {
    override fun getPreferences(bf: BaseFragment) = tgKitScreen(getString(R.string.CP_Header_Chats)) {
        category(getString(R.string.AccDescrStickers)) {
            switch {
                title = getString(R.string.CP_TimeOnStick)

                contract({
                    return@contract komarugramChatsConfig.hideStickerTime
                }) {
                    komarugramChatsConfig.hideStickerTime = it
                }
            }
        }
        category(getString(R.string.CP_Slider_StickerAmplifier)) {
            slider {
                contract = object : TGSLContract {
                    override fun setValue(value: Int) {
                        komarugramChatsConfig.slider_stickerAmplifier = value
                    }

                    override fun getPreferenceValue(): Int {
                        return komarugramChatsConfig.slider_stickerAmplifier
                    }

                    override fun getMin(): Int {
                        return 50
                    }

                    override fun getMax(): Int {
                        return 100
                    }
                }
            }

        }

        category(getString(R.string.CP_Header_Chats)) {
            textIcon {
                title = getString(R.string.CP_ChatMenuShortcuts)
                icon = R.drawable.msg_list
                listener = TGKitTextIconRow.TGTIListener {
                    AlertDialogSwitchers.showChatActionsAlert(bf)
                }
                divider = true
            }
            switch {
                title = getString(R.string.AP_CenterChatsTitle)
                contract({
                    return@contract komarugramChatsConfig.centerChatTitle
                }) {
                    komarugramChatsConfig.centerChatTitle = it
                    bf.parentLayout.rebuildAllFragmentViews(false, false)
                }
            }
            switch {
                title = getString(R.string.CP_UnreadBadgeOnBackButton)
                description = getString(R.string.CP_UnreadBadgeOnBackButton_Desc)

                contract({
                    return@contract komarugramChatsConfig.unreadBadgeOnBackButton
                }) {
                    komarugramChatsConfig.unreadBadgeOnBackButton = it
                }
            }
            switch {
                title = getString(R.string.CP_ConfirmCalls)

                contract({
                    return@contract komarugramChatsConfig.confirmCalls
                }) {
                    komarugramChatsConfig.confirmCalls = it
                }
            }
            switch {
                title = getString(R.string.CP_HideKbdOnScroll)

                contract({
                    return@contract komarugramChatsConfig.hideKeyboardOnScroll
                }) {
                    komarugramChatsConfig.hideKeyboardOnScroll = it
                    AppRestartHelper.createRestartBulletin(bf)
                }
            }
            switch {
                title = getString(R.string.CP_DisableSwipeToNext)
                description = getString(R.string.CP_DisableSwipeToNext_Desc)

                contract({
                    return@contract komarugramChatsConfig.disableSwipeToNext
                }) {
                    komarugramChatsConfig.disableSwipeToNext = it
                }
            }
            switch {
                title = getString(R.string.CP_HideMuteUnmuteButton)

                contract({
                    return@contract komarugramChatsConfig.hideMuteUnmuteButton
                }) {
                    komarugramChatsConfig.hideMuteUnmuteButton = it
                }
            }
        }

        category(getString(R.string.CP_Slider_RecentEmojisAmplifier)) {
            slider {
                contract = object : TGSLContract {
                    override fun setValue(value: Int) {
                        komarugramChatsConfig.slider_RecentEmojisAmplifier = value
                    }

                    override fun getPreferenceValue(): Int {
                        return komarugramChatsConfig.slider_RecentEmojisAmplifier
                    }

                    override fun getMin(): Int {
                        return 45
                    }

                    override fun getMax(): Int {
                        return 90
                    }
                }
            }
        }

        category(getString(R.string.CP_Slider_RecentStickersAmplifier)) {
            slider {
                contract = object : TGSLContract {
                    override fun setValue(value: Int) {
                        komarugramChatsConfig.slider_RecentStickersAmplifier = value
                    }

                    override fun getPreferenceValue(): Int {
                        return komarugramChatsConfig.slider_RecentStickersAmplifier
                    }

                    override fun getMin(): Int {
                        return 20
                    }

                    override fun getMax(): Int {
                        return 120
                    }
                }
            }
        }

        category(getString(R.string.CP_Header_Messages)) {
            textIcon {
                title = getString(R.string.DirectShare)
                icon = R.drawable.msg_share
                listener = TGKitTextIconRow.TGTIListener {
                    AlertDialogSwitchers.showDirectShareAlert(bf)
                }
                divider = true
            }
            textIcon {
                title = getString(R.string.CP_MessageMenu)
                icon = R.drawable.msg_list
                listener = TGKitTextIconRow.TGTIListener {
                    AlertDialogSwitchers.showChatMenuIconsAlert(bf)
                }
                divider = true
            }
            switch {
                title = getString(R.string.CP_DeleteForAll)
                description = getString(R.string.CP_DeleteForAll_Desc)

                contract({
                    return@contract komarugramChatsConfig.deleteForAll
                }) {
                    komarugramChatsConfig.deleteForAll = it
                }
            }
            switch {
                title = getString(R.string.CP_ForwardMsgDate)

                contract({
                    return@contract komarugramChatsConfig.msgForwardDate
                }) {
                    komarugramChatsConfig.msgForwardDate = it
                }
            }
            switch {
                title = getString(R.string.AP_ShowPencilIcon)
                contract({
                    return@contract komarugramChatsConfig.showPencilIcon
                }) {
                    komarugramChatsConfig.showPencilIcon = it
                }
            }
            list {
                title = getString(R.string.CP_LeftBottomButtonAction)

                contract({
                    return@contract listOf(
                        Pair(komarugramChatsConfig.LEFT_BUTTON_FORWARD_WO_AUTHORSHIP, getString(R.string.Forward) + getString(R.string.CG_Without_Authorship)),
                        Pair(komarugramChatsConfig.LEFT_BUTTON_REPLY, getString(R.string.Reply)),
                        Pair(komarugramChatsConfig.LEFT_BUTTON_SAVE_MESSAGE, getString(R.string.CG_ToSaved)),
                        Pair(komarugramChatsConfig.LEFT_BUTTON_DIRECT_SHARE, getString(R.string.DirectShare))
                    )
                }, {
                    return@contract when (komarugramChatsConfig.leftBottomButton) {
                        komarugramChatsConfig.LEFT_BUTTON_REPLY -> getString(R.string.Reply)
                        komarugramChatsConfig.LEFT_BUTTON_SAVE_MESSAGE -> getString(R.string.CG_ToSaved)
                        komarugramChatsConfig.LEFT_BUTTON_DIRECT_SHARE -> getString(R.string.DirectShare)
                        else -> getString(R.string.Forward) + getString(R.string.CG_Without_Authorship)
                    }
                }) {
                    komarugramChatsConfig.leftBottomButton = it
                }
            }
            list {
                title = getString(R.string.CP_DoubleTapAction)

                contract({
                    return@contract listOf(
                        Pair(komarugramChatsConfig.DOUBLE_TAP_ACTION_NONE, getString(R.string.Disable)),
                        Pair(komarugramChatsConfig.DOUBLE_TAP_ACTION_REACTION, getString(R.string.Reactions)),
                        Pair(komarugramChatsConfig.DOUBLE_TAP_ACTION_REPLY, getString(R.string.Reply)),
                        Pair(komarugramChatsConfig.DOUBLE_TAP_ACTION_SAVE, getString(R.string.CG_ToSaved)),
                        Pair(komarugramChatsConfig.DOUBLE_TAP_ACTION_EDIT, getString(R.string.Edit)),
                        Pair(komarugramChatsConfig.DOUBLE_TAP_ACTION_TRANSLATE, getString(R.string.TranslateMessage))
                    )
                }, {
                    return@contract when (komarugramChatsConfig.doubleTapAction) {
                        komarugramChatsConfig.DOUBLE_TAP_ACTION_REACTION -> getString(R.string.Reactions)
                        komarugramChatsConfig.DOUBLE_TAP_ACTION_REPLY -> getString(R.string.Reply)
                        komarugramChatsConfig.DOUBLE_TAP_ACTION_SAVE -> getString(R.string.CG_ToSaved)
                        komarugramChatsConfig.DOUBLE_TAP_ACTION_EDIT -> getString(R.string.Edit)
                        komarugramChatsConfig.DOUBLE_TAP_ACTION_TRANSLATE -> getString(R.string.TranslateMessage)
                        else -> getString(R.string.Disable)
                    }
                }) {
                    komarugramChatsConfig.doubleTapAction = it
                }
            }
            list {
                title = getString(R.string.CG_MsgSlideAction)

                contract({
                    return@contract listOf(
                        Pair(komarugramChatsConfig.MESSAGE_SLIDE_ACTION_REPLY, getString(R.string.Reply)),
                        Pair(komarugramChatsConfig.MESSAGE_SLIDE_ACTION_SAVE, getString(R.string.CG_ToSaved)),
                        Pair(komarugramChatsConfig.MESSAGE_SLIDE_ACTION_TRANSLATE, getString(R.string.TranslateMessage)),
                        Pair(komarugramChatsConfig.MESSAGE_SLIDE_ACTION_DIRECT_SHARE, getString(R.string.DirectShare))
                    )
                }, {
                    return@contract when (komarugramChatsConfig.messageSlideAction) {
                        komarugramChatsConfig.MESSAGE_SLIDE_ACTION_SAVE -> getString(R.string.CG_ToSaved)
                        komarugramChatsConfig.MESSAGE_SLIDE_ACTION_TRANSLATE -> getString(R.string.TranslateMessage)
                        komarugramChatsConfig.MESSAGE_SLIDE_ACTION_DIRECT_SHARE -> getString(R.string.DirectShare)
                        else -> getString(R.string.Reply)
                    }
                }) {
                    komarugramChatsConfig.messageSlideAction = it
                }
            }
        }

        category(getString(R.string.CP_Header_Record)) {
            switch {
                title = getString(R.string.EP_PhotosSize)

                contract({
                    return@contract komarugramChatsConfig.largePhotos
                }) {
                    komarugramChatsConfig.largePhotos = it
                    AppRestartHelper.createRestartBulletin(bf)
                }
            }
            switch {
                title = getString(R.string.CP_SpoilersOnMedia)

                contract({
                    return@contract komarugramChatsConfig.spoilersOnMedia
                }) {
                    komarugramChatsConfig.spoilersOnMedia = it
                }
            }
            switch {
                title = getString(R.string.CP_VoiceEnhancements)
                description = getString(R.string.CP_VoiceEnhancements_Desc)

                contract({
                    return@contract komarugramChatsConfig.voicesAgc
                }) {
                    komarugramChatsConfig.voicesAgc = it
                }
            }
            switch {
                title = getString(R.string.CP_PlayVideo)
                description = getString(R.string.CP_PlayVideo_Desc)

                contract({
                    return@contract komarugramChatsConfig.playVideoOnVolume
                }) {
                    komarugramChatsConfig.playVideoOnVolume = it
                }
            }
            switch {
                title = getString(R.string.CP_AutoPauseVideo)
                description = getString(R.string.CP_AutoPauseVideo_Desc)

                contract({
                    return@contract komarugramChatsConfig.autoPauseVideo
                }) {
                    komarugramChatsConfig.autoPauseVideo = it
                }
            }
            switch {
                title = getString(R.string.CP_DisableVibration)

                contract({
                    return@contract komarugramChatsConfig.disableVibration
                }) {
                    komarugramChatsConfig.disableVibration = it
                    AppRestartHelper.createRestartBulletin(bf)
                }
            }
        }

        category(getString(R.string.CP_VideoSeekDuration)) {
            slider {
                contract = object : TGSLContract {
                    override fun setValue(value: Int) {
                        komarugramChatsConfig.videoSeekDuration = value
                    }

                    override fun getPreferenceValue(): Int {
                        return komarugramChatsConfig.videoSeekDuration
                    }

                    override fun getMin(): Int {
                        return 0
                    }

                    override fun getMax(): Int {
                        return 25
                    }
                }
            }
        }

        category("GPT") {
            list {
                title = "Model API"

                contract({
                    return@contract listOf(
                        Pair(komarugramChatsConfig.CHATGPT, "ChatGPT-3.5"),
                        Pair(komarugramChatsConfig.GEMINI, "Gemini"),
                        Pair(komarugramChatsConfig.BLACKBOX, "Blackbox")
                    )
                }, {
                    return@contract when (komarugramChatsConfig.GPTMODEL) {
                        komarugramChatsConfig.GEMINI -> "Gemini"
                        komarugramChatsConfig.BLACKBOX -> "Blackbox"
                        else -> "ChatGPT-3.5"
                    }
                }) {
                    komarugramChatsConfig.GPTMODEL = it

                }
            }
        }

        category(getString(R.string.CP_Header_Notification)) {
            list {
                title = getString(R.string.CP_NotificationSound)

                contract({
                    return@contract listOf(
                        Pair(komarugramChatsConfig.NOTIF_SOUND_DISABLE, getString(R.string.Disable)),
                        Pair(komarugramChatsConfig.NOTIF_SOUND_DEFAULT, getString(R.string.Default)),
                        Pair(komarugramChatsConfig.NOTIF_SOUND_IOS, "IOS")
                    )
                }, {
                    return@contract when (komarugramChatsConfig.notificationSound) {
                        komarugramChatsConfig.NOTIF_SOUND_DEFAULT -> getString(R.string.Default)
                        komarugramChatsConfig.NOTIF_SOUND_IOS -> "IOS"
                        else -> getString(R.string.Disable)
                    }
                }) {
                    komarugramChatsConfig.notificationSound = it

                    var tone = 0
                    try {
                        if (komarugramChatsConfig.notificationSound == 1) {
                            tone = R.raw.sound_in
                        } else if (komarugramChatsConfig.notificationSound == 2) {
                            tone = R.raw.sound_in_ios
                        }
                        val mp: MediaPlayer = MediaPlayer.create(bf.context, tone)
                        mp.start()
                    } catch (ignore: Exception) { }

                    AppRestartHelper.createRestartBulletin(bf)
                }
            }
            list {
                title = getString(R.string.CP_VibrateInChats)

                contract({
                    return@contract listOf(
                        Pair(komarugramChatsConfig.VIBRATION_DISABLE, getString(R.string.Disable)),
                        Pair(komarugramChatsConfig.VIBRATION_CLICK, "1"),
                        Pair(komarugramChatsConfig.VIBRATION_WAVE_FORM, "2"),
                        Pair(komarugramChatsConfig.VIBRATION_KEYBOARD_TAP, "3"),
                        Pair(komarugramChatsConfig.VIBRATION_LONG, "4")
                    )
                }, {
                    return@contract when (komarugramChatsConfig.vibrateInChats) {
                        komarugramChatsConfig.VIBRATION_CLICK -> "1"
                        komarugramChatsConfig.VIBRATION_WAVE_FORM -> "2"
                        komarugramChatsConfig.VIBRATION_KEYBOARD_TAP -> "3"
                        komarugramChatsConfig.VIBRATION_LONG -> "4"
                        else -> getString(R.string.Disable)
                    }
                }) {
                    komarugramChatsConfig.vibrateInChats = it

                    try {
                        when (komarugramChatsConfig.vibrateInChats) {
                            komarugramChatsConfig.VIBRATION_CLICK -> {
                                VibrateUtil.makeClickVibration()
                            }
                            komarugramChatsConfig.VIBRATION_WAVE_FORM -> {
                                VibrateUtil.makeWaveVibration()
                            }
                            komarugramChatsConfig.VIBRATION_KEYBOARD_TAP -> {
                                VibrateUtil.vibrate(HapticFeedbackConstants.KEYBOARD_TAP.toLong())
                            }
                            komarugramChatsConfig.VIBRATION_LONG -> {
                                VibrateUtil.vibrate()
                            }
                        }
                    } catch (ignore: Exception) { }

                }
            }
            hint(getString(R.string.CP_VibrateInChats_Desc))

            switch {
                title = getString(R.string.CP_SilenceNonContacts)
                description = getString(R.string.CP_SilenceNonContacts_Desc)


                contract({
                    return@contract komarugramChatsConfig.silenceNonContacts
                }) {
                    komarugramChatsConfig.silenceNonContacts = it
                }
            }
        }
    }
}
