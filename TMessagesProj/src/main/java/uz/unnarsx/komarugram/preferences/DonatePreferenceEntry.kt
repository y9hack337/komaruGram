package uz.unnarsx.komarugram.preferences

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.BaseFragment
import org.telegram.ui.ActionBar.Theme
import uz.unnarsx.komarugram.preferences.BasePreferencesEntry
import uz.unnarsx.komarugram.preferences.tgkit.preference.textIcon
import uz.unnarsx.komarugram.preferences.tgkit.preference.tgKitScreen
import uz.unnarsx.komarugram.preferences.tgkit.preference.*
import uz.unnarsx.komarugram.preferences.tgkit.preference.types.TGKitTextIconRow

class DonatePreferenceEntry : BasePreferencesEntry {
    override fun getPreferences(bf: BaseFragment) = tgKitScreen(LocaleController.getString("DP_Donate", R.string.DP_Donate)) {
        val isDarkMode: Boolean = !Theme.isCurrentThemeDay()
        category(LocaleController.getString("DP_Donate_Method", R.string.DP_Donate_Method)) {
            textIcon {
                icon = if (isDarkMode) R.drawable.card_ym_dark else R.drawable.card_ym_light
                title = "YooMoney (RUB)"

                listener = TGKitTextIconRow.TGTIListener {
                    AndroidUtilities.addToClipboard("4100116983696293")
                    Toast.makeText(bf.parentActivity, LocaleController.getString("CardNumberCopied", R.string.CardNumberCopied), Toast.LENGTH_SHORT).show()
                }
            }
        }
        category("Telegram Wallet") {
            textIcon {
                icon = if (isDarkMode) R.drawable.card_ton_dark else R.drawable.card_ton_light
                title = "TON Coin // Tonkeeper"
                divider = true

                listener = TGKitTextIconRow.TGTIListener {
                    AndroidUtilities.addToClipboard("UQBa1XcnAEjwQmSUsyvhFsy3GtVcqOIgI8b7YvYI8-buIGpv")
                    Toast.makeText(bf.parentActivity, LocaleController.getString("TextCopied", R.string.TextCopied), Toast.LENGTH_SHORT).show()
                }
            }
            textIcon {
                icon = if (isDarkMode) R.drawable.card_ton_dark else R.drawable.card_ton_light
                title = "TON Coin // @wallet"

                listener = TGKitTextIconRow.TGTIListener {
                    AndroidUtilities.addToClipboard("UQBa1XcnAEjwQmSUsyvhFsy3GtVcqOIgI8b7YvYI8-buIGpv")
                    Toast.makeText(bf.parentActivity, LocaleController.getString("TextCopied", R.string.TextCopied), Toast.LENGTH_SHORT).show()
                }
            }
            textIcon {
                icon = if (isDarkMode) R.drawable.card_not_dark else R.drawable.card_not_light
                title = "NOT Coin // @wallet"

                listener = TGKitTextIconRow.TGTIListener {
                    AndroidUtilities.addToClipboard("UQBa1XcnAEjwQmSUsyvhFsy3GtVcqOIgI8b7YvYI8-buIGpv")
                    Toast.makeText(bf.parentActivity, LocaleController.getString("TextCopied", R.string.TextCopied), Toast.LENGTH_SHORT).show()
                }
            }
            textIcon {
                icon = if (isDarkMode) R.drawable.card_usdt_dark else R.drawable.card_usdt_light
                title = "USDT (TRC20) // @wallet"

                listener = TGKitTextIconRow.TGTIListener {
                    AndroidUtilities.addToClipboard("TRLCEZBWPPnyeYXteb6Nw6rMqtyprsQkZi")
                    Toast.makeText(bf.parentActivity, LocaleController.getString("TextCopied", R.string.TextCopied), Toast.LENGTH_SHORT).show()
                }
            }
            textIcon {
                icon = if (isDarkMode) R.drawable.card_usdt_dark else R.drawable.card_usdt_light
                title = "USDT (BEP20) // @wallet"

                listener = TGKitTextIconRow.TGTIListener {
                    AndroidUtilities.addToClipboard("0x165829AF97323f80A6B2B9fa2f0c27366595B29E")
                    Toast.makeText(bf.parentActivity, LocaleController.getString("TextCopied", R.string.TextCopied), Toast.LENGTH_SHORT).show()
                }
            }
            textIcon {
                icon = if (isDarkMode) R.drawable.card_btc_dark else R.drawable.card_btc_light
                title = "Bitcoin (BTC) // @wallet"

                listener = TGKitTextIconRow.TGTIListener {
                    AndroidUtilities.addToClipboard("bc1qhda7ss7spdrpg8vnzv6elwpsuwhvdvy5uy5yj8")
                    Toast.makeText(bf.parentActivity, LocaleController.getString("TextCopied", R.string.TextCopied), Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}