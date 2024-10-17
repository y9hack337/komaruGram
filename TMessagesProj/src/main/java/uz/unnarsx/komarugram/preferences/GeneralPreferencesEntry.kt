package uz.unnarsx.komarugram.preferences

import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.LocaleController.getString
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.BaseFragment
import uz.unnarsx.komarugram.core.configs.komarugramCoreConfig
import uz.unnarsx.komarugram.core.helpers.AppRestartHelper
import uz.unnarsx.komarugram.preferences.tgkit.preference.category
import uz.unnarsx.komarugram.preferences.tgkit.preference.contract
import uz.unnarsx.komarugram.preferences.tgkit.preference.switch
import uz.unnarsx.komarugram.preferences.tgkit.preference.tgKitScreen

class GeneralPreferencesEntry : BasePreferencesEntry {
    override fun getPreferences(bf: BaseFragment) = tgKitScreen(getString(R.string.AP_Header_General)) {
        category(getString(R.string.AP_Header_General)) {
            switch {
                title = getString(R.string.CP_NoRounding)
                description = getString(R.string.CP_NoRoundingSummary)

                contract({
                    return@contract komarugramCoreConfig.noRounding
                }) {
                    komarugramCoreConfig.noRounding = it
                }
            }
            switch {
                title = getString(R.string.AP_SystemEmoji)
                contract({
                    return@contract komarugramCoreConfig.systemEmoji
                }) {
                    komarugramCoreConfig.systemEmoji = it
                }
            }
            switch {
                title = getString(R.string.AP_SystemFonts)

                contract({
                    return@contract komarugramCoreConfig.systemFonts
                }) {
                    komarugramCoreConfig.systemFonts = it
                    AndroidUtilities.clearTypefaceCache()
                    AppRestartHelper.createRestartBulletin(bf)
                }
            }
            switch {
                title = getString(R.string.AP_Old_Notification_Icon)
                contract({
                    return@contract komarugramCoreConfig.oldNotificationIcon
                }) {
                    komarugramCoreConfig.oldNotificationIcon = it
                    AppRestartHelper.createRestartBulletin(bf)
                }
            }
        }

        category(getString(R.string.CP_PremAndAnim_Header)) {
            switch {
                title = getString(R.string.CP_HideStories)

                contract({
                    return@contract komarugramCoreConfig.hideStories
                }) {
                    komarugramCoreConfig.hideStories = it
                }
            }
            switch {
                title = getString(R.string.CP_CustomWallpapers)
                description = getString(R.string.CP_CustomWallpapers_Desc)

                contract({
                    return@contract komarugramCoreConfig.customWallpapers
                }) {
                    komarugramCoreConfig.customWallpapers = it
                }
            }
            switch {
                title = getString(R.string.CP_DisableAnimAvatars)

                contract({
                    return@contract komarugramCoreConfig.disableAnimatedAvatars
                }) {
                    komarugramCoreConfig.disableAnimatedAvatars = it
                    AppRestartHelper.createRestartBulletin(bf)
                }
            }
            switch {
                title = getString(R.string.CP_DisableReactionsOverlay)
                description = getString(R.string.CP_DisableReactionsOverlay_Desc)

                contract({
                    return@contract komarugramCoreConfig.disableReactionsOverlay
                }) {
                    komarugramCoreConfig.disableReactionsOverlay = it
                    AppRestartHelper.createRestartBulletin(bf)
                }
            }
            switch {
                title = getString(R.string.CP_DisableReactionAnim)
                description = getString(R.string.CP_DisableReactionAnim_Desc)

                contract({
                    return@contract komarugramCoreConfig.disableReactionAnim
                }) {
                    komarugramCoreConfig.disableReactionAnim = it
                    AppRestartHelper.createRestartBulletin(bf)
                }
            }
            switch {
                title = getString(R.string.CP_DisablePremStickAnim)
                description = getString(R.string.CP_DisablePremStickAnim_Desc)

                contract({
                    return@contract komarugramCoreConfig.disablePremStickAnim
                }) {
                    komarugramCoreConfig.disablePremStickAnim = it
                    AppRestartHelper.createRestartBulletin(bf)
                }
            }
            switch {
                title = getString(R.string.CP_DisablePremStickAutoPlay)
                description = getString(R.string.CP_DisablePremStickAutoPlay_Desc)

                contract({
                    return@contract komarugramCoreConfig.disablePremStickAutoPlay
                }) {
                    komarugramCoreConfig.disablePremStickAutoPlay = it
                    AppRestartHelper.createRestartBulletin(bf)
                }
            }
            switch {
                title = getString(R.string.CP_HideSendAsChannel)
                description = getString(R.string.CP_HideSendAsChannelDesc)

                contract({
                    return@contract komarugramCoreConfig.hideSendAsChannel
                }) {
                    komarugramCoreConfig.hideSendAsChannel = it
                }
            }

        }
    }
}
