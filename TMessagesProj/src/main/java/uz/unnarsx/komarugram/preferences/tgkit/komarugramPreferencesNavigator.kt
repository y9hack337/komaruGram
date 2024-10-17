package uz.unnarsx.komarugram.preferences.tgkit

import uz.unnarsx.komarugram.preferences.AppearancePreferencesEntry
import uz.unnarsx.komarugram.preferences.ChatsPreferencesEntry
import uz.unnarsx.komarugram.preferences.DebugPreferencesEntry
import uz.unnarsx.komarugram.preferences.GeneralPreferencesEntry
import uz.unnarsx.komarugram.preferences.MainPreferencesEntry
import uz.unnarsx.komarugram.preferences.PrivacyAndSecurityPreferencesEntry
import uz.unnarsx.komarugram.preferences.AboutPreferencesEntry
import uz.unnarsx.komarugram.preferences.DonatePreferenceEntry
import uz.unnarsx.komarugram.preferences.tgkit.TGKitSettingsFragment

object komarugramPreferencesNavigator {
    @JvmStatic
    fun createMainMenu() = TGKitSettingsFragment(MainPreferencesEntry())
    fun createGeneral() = TGKitSettingsFragment(GeneralPreferencesEntry())
    fun createAppearance() = TGKitSettingsFragment(AppearancePreferencesEntry())
    fun createChats() = TGKitSettingsFragment(ChatsPreferencesEntry())
    fun createPrivacyAndSecurity() = TGKitSettingsFragment(PrivacyAndSecurityPreferencesEntry())
    fun createDonate() = TGKitSettingsFragment(DonatePreferenceEntry())
    fun createDebug() = TGKitSettingsFragment(DebugPreferencesEntry())
    fun createAbout() = TGKitSettingsFragment(AboutPreferencesEntry())
}