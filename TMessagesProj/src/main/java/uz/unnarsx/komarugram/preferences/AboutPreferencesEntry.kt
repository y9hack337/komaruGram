package uz.unnarsx.komarugram.preferences

import android.os.Bundle
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.BuildConfig
import org.telegram.messenger.BuildVars
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.messenger.browser.Browser
import org.telegram.ui.ActionBar.BaseFragment
import org.telegram.ui.ChatActivity
import org.telegram.ui.Components.BulletinFactory
import uz.unnarsx.komarugram.komaruGramConfig
import uz.unnarsx.komarugram.Extra
import uz.unnarsx.komarugram.core.crashlytics.Crashlytics
import uz.unnarsx.komarugram.misc.Constants
import uz.unnarsx.komarugram.core.helpers.CGResourcesHelper
import uz.unnarsx.komarugram.preferences.tgkit.komaruGramPreferencesNavigator
import uz.unnarsx.komarugram.preferences.tgkit.preference.category
import uz.unnarsx.komarugram.preferences.tgkit.preference.textDetail
import uz.unnarsx.komarugram.preferences.tgkit.preference.textIcon
import uz.unnarsx.komarugram.preferences.tgkit.preference.tgKitScreen
import uz.unnarsx.komarugram.preferences.tgkit.preference.types.TGKitTextDetailRow
import uz.unnarsx.komarugram.preferences.tgkit.preference.types.TGKitTextIconRow
import uz.unnarsx.komarugram.core.updater.UpdaterBottomSheet
import uz.unnarsx.komarugram.core.updater.UpdaterUtils

class AboutPreferencesEntry : BasePreferencesEntry {
    override fun getPreferences(bf: BaseFragment) = tgKitScreen(LocaleController.getString("CGP_Header_About", R.string.CGP_Header_About)) {
        category(LocaleController.getString("Info", R.string.Info)) {
            textDetail {
                title = CGResourcesHelper.getAppName() + " " + Constants.CG_VERSION + " | " + "Telegram v" + BuildVars.BUILD_VERSION_STRING
                detail = LocaleController.getString("CGP_About_Desc", R.string.CGP_About_Desc)

                listener = TGKitTextDetailRow.TGTDListener {
                    Browser.openUrl(bf.parentActivity, "https://github.com/hack337-LZT/komaruGram")
                }
            }

            textDetail {
                icon = R.drawable.sync_outline_28
                title = LocaleController.getString("UP_Category_Updates", R.string.UP_Category_Updates)
                detail = UpdaterUtils.getLastCheckUpdateTime()

                listener = TGKitTextDetailRow.TGTDListener {
                    if (komaruGramConfig.isPlayStoreBuild()) {
                        komaruGramConfig.lastUpdateCheckTime = System.currentTimeMillis()
                        detail = UpdaterUtils.getLastCheckUpdateTime()

                        Browser.openUrl(bf.context, Extra.PLAYSTORE_APP_URL)
                    } else if (komaruGramConfig.isPremiumBuild()) {
                        // Fuckoff :)
                    } else {
                        UpdaterBottomSheet.showAlert(bf.context, bf, false, null)
                    }
                }
            }

            textIcon {
                icon = R.drawable.bug_solar
                title = LocaleController.getString("CG_CopyReportDetails", R.string.CG_CopyReportDetails)

                listener = TGKitTextIconRow.TGTIListener {
                    AndroidUtilities.addToClipboard(Crashlytics.getReportMessage().toString() + "\n\n#bug")
                    BulletinFactory.of(bf).createErrorBulletin(LocaleController.getString("CG_ReportDetailsCopied", R.string.CG_ReportDetailsCopied)).show()
                }
            }
            textIcon {
                icon = R.drawable.test_tube_solar
                title = "Debug // WIP"

                listener = TGKitTextIconRow.TGTIListener {
                    it.presentFragment(komaruGramPreferencesNavigator.createDebug())
                }
            }
        }

        category(LocaleController.getString("CGP_Links", R.string.CGP_Links)) {
            textIcon {
                icon = R.drawable.msg_channel_solar
                title = LocaleController.getString("CGP_ToChannel", R.string.CGP_ToChannel)
                value = "@komaruGram"

                listener = TGKitTextIconRow.TGTIListener {
                    Browser.openUrl(bf.parentActivity, "https://t.me/komarugram")
                }
            }
            textIcon {
                icon = R.drawable.msg_discuss_solar
                title = LocaleController.getString("CGP_ToChat", R.string.CGP_ToChat)
                value = "@komaruGramChat"

                listener = TGKitTextIconRow.TGTIListener {
                    Browser.openUrl(bf.parentActivity, "https://t.me/komarugram")
                }
            }
            if (!komaruGramConfig.isPremiumBuild()) {
                textIcon {
                    icon = R.drawable.github_logo_white
                    title = LocaleController.getString("CGP_Source", R.string.CGP_Source)

                    value = if (komaruGramConfig.isBetaBuild() || komaruGramConfig.isDevBuild()) {
                        "GitHub"
                    } else {
                        "commit " + BuildConfig.GIT_COMMIT_HASH
                    }

                    listener = TGKitTextIconRow.TGTIListener {
                        if (komaruGramConfig.isBetaBuild() || komaruGramConfig.isDevBuild()) {
                            Browser.openUrl(bf.parentActivity, "https://github.com/y9hack337/komaruGram/")
                        } else {
                            Browser.openUrl(bf.parentActivity, "https://github.com/y9hack337/komaruGram/commit/" + BuildConfig.GIT_COMMIT_HASH)
                        }
                    }
                }
            }
            textIcon {
                icon = R.drawable.msg_translate_solar
                title = LocaleController.getString("CGP_Crowdin", R.string.CGP_Crowdin)
                value = "Crowdin"

                listener = TGKitTextIconRow.TGTIListener {
                    Browser.openUrl(bf.parentActivity, "https://crowdin.com/")
                }
            }

            if (komaruGramConfig.isPlayStoreBuild()) {
                textIcon {
                    icon = R.drawable.msg2_policy
                    title = LocaleController.getString("PrivacyPolicy", R.string.PrivacyPolicy)

                    listener = TGKitTextIconRow.TGTIListener {
                        Browser.openUrl(bf.parentActivity, "https://github.com/y9hack337/komaruGram")
                    }
                }
            }
            textIcon {
                icon = R.drawable.heart_angle_solar
                title = LocaleController.getString("DP_Donate", R.string.DP_Donate)

                listener = TGKitTextIconRow.TGTIListener {
                    it.presentFragment(komaruGramPreferencesNavigator.createDonate())
                }
            }
        }

    }
}