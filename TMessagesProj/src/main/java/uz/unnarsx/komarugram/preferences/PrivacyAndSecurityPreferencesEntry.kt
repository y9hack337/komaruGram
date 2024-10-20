package uz.unnarsx.komarugram.preferences

import android.os.Build
import android.os.Environment
import android.widget.Toast
import org.telegram.messenger.LocaleController.getString
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.BaseFragment
import uz.unnarsx.komarugram.core.helpers.AppRestartHelper
import uz.unnarsx.komarugram.core.CGBiometricPrompt
import uz.unnarsx.komarugram.core.configs.komarugramPrivacyConfig
import uz.unnarsx.komarugram.preferences.tgkit.preference.category
import uz.unnarsx.komarugram.preferences.tgkit.preference.contract
import uz.unnarsx.komarugram.preferences.tgkit.preference.switch
import uz.unnarsx.komarugram.preferences.tgkit.preference.textIcon
import uz.unnarsx.komarugram.preferences.tgkit.preference.tgKitScreen
import uz.unnarsx.komarugram.preferences.tgkit.preference.types.TGKitTextIconRow
import java.io.File

class PrivacyAndSecurityPreferencesEntry : BasePreferencesEntry {
    override fun getPreferences(bf: BaseFragment) = tgKitScreen(getString(R.string.SP_Category_PrivacyAndSecurity)) {
        category(getString(R.string.SP_Header_Privacy)) {
            switch {
                title = getString(R.string.SP_NoProxyPromo)

                contract({
                    return@contract komarugramPrivacyConfig.hideProxySponsor
                }) {
                    komarugramPrivacyConfig.hideProxySponsor = it
                    bf.parentLayout.rebuildAllFragmentViews(false, false)
                }
            }
            switch {
                title = getString(R.string.SP_GoogleAnalytics)
                description = getString(R.string.SP_GoogleAnalytics_Desc)

                contract({
                    return@contract komarugramPrivacyConfig.googleAnalytics
                }) {
                    komarugramPrivacyConfig.googleAnalytics = it
                    AppRestartHelper.createRestartBulletin(bf)
                }
            }

            textIcon {
                title = getString(R.string.SP_CleanOld)

                listener = TGKitTextIconRow.TGTIListener {
                    val file = File(Environment.getExternalStorageDirectory(), "Telegram")
                    file.deleteRecursively()
                    Toast.makeText(bf.parentActivity, getString(R.string.SP_RemovedS), Toast.LENGTH_SHORT).show()
                }
            }
        }

        val askPasscode = Build.VERSION.SDK_INT >= 23
        if (askPasscode) {
            category(getString(R.string.Passcode)) {
                switch {
                    title = getString(R.string.SP_AskPinForChats)
                    description = getString(R.string.SP_AskPinForChats_Desc)

                    contract({
                        return@contract komarugramPrivacyConfig.askForPasscodeBeforeOpenChat
                    }) {
                        CGBiometricPrompt.prompt(bf.parentActivity) {
                            komarugramPrivacyConfig.askForPasscodeBeforeOpenChat = it
                            bf.parentLayout.rebuildAllFragmentViews(true, true)
                            AppRestartHelper.createRestartBulletin(bf)
                        }
                    }
                }
                switch {
                    title = getString(R.string.SP_AskPinBeforeDelete)
                    description = getString(R.string.SP_AskPinBeforeDelete_Desc)

                    contract({
                        return@contract komarugramPrivacyConfig.askPasscodeBeforeDelete
                    }) {
                        komarugramPrivacyConfig.askPasscodeBeforeDelete = it
                        AppRestartHelper.createRestartBulletin(bf)
                    }
                }
                switch {
                    title = getString(R.string.SP_AllowUseSystemPasscode)
                    description = getString(R.string.SP_AllowUseSystemPasscode_Desc)

                    contract({
                        return@contract komarugramPrivacyConfig.allowSystemPasscode
                    }) {
                        komarugramPrivacyConfig.allowSystemPasscode = it
                    }
                }
                textIcon {
                    title = getString(R.string.SP_TestFingerprint)
                    icon = R.drawable.fingerprint

                    listener = TGKitTextIconRow.TGTIListener {
                        CGBiometricPrompt.prompt(bf.parentActivity) { AppRestartHelper.createDebugSuccessBulletin(bf) }
                    }
                }
            }
        }

        category(getString(R.string.SP_Category_Account)) {
            textIcon {
                title = getString(R.string.SP_DeleteAccount)
                icon = R.drawable.msg_delete
                listener = TGKitTextIconRow.TGTIListener {
                    DeleteAccountDialog.showDeleteAccountDialog(bf)
                }
            }
        }

    }
}