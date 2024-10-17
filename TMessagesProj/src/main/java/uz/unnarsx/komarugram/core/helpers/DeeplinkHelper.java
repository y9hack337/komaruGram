package uz.unnarsx.komarugram.core.helpers;

import android.content.Intent;
import android.net.Uri;

import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.LaunchActivity;

import java.util.Locale;

import uz.unnarsx.komarugram.Extra;
import uz.unnarsx.komarugram.core.configs.komarugramCoreConfig;
import uz.unnarsx.komarugram.core.updater.UpdaterBottomSheet;
import uz.unnarsx.komarugram.preferences.CameraPreferencesEntry;
import uz.unnarsx.komarugram.preferences.ExperimentalPreferencesEntry;
import uz.unnarsx.komarugram.preferences.drawer.DrawerPreferencesEntry;
import uz.unnarsx.komarugram.preferences.tgkit.komarugramPreferencesNavigator;

public class DeeplinkHelper {

    public static void processDeepLink(Uri uri, BaseFragment fragment, Callback callback, Runnable unknown, Browser.Progress progress) {
        if (uri == null) {
            unknown.run();
            return;
        }
        var segments = uri.getPathSegments();
        if (segments.isEmpty() || segments.size() > 2) {
            unknown.run();
            return;
        }

        if (segments.size() == 1) {
            var segment = segments.get(0).toLowerCase(Locale.US);
            switch (segment) {
                case "cg_settings", "cg_main" -> fragment = komarugramPreferencesNavigator.createMainMenu();
                case "cg_about" -> fragment = komarugramPreferencesNavigator.INSTANCE.createAbout();
                case "cg_appearance" -> fragment = komarugramPreferencesNavigator.INSTANCE.createAppearance();
                case "cg_camera", "cg_cam" -> fragment = new CameraPreferencesEntry();
                case "cg_chats" -> fragment = komarugramPreferencesNavigator.INSTANCE.createChats();
                case "cg_debug" -> fragment = komarugramPreferencesNavigator.INSTANCE.createDebug();
                case "cg_donate", "cg_donates" -> fragment = komarugramPreferencesNavigator.INSTANCE.createDonate();
                case "cg_drawer" -> fragment = new DrawerPreferencesEntry();
                case "cg_experimental" -> fragment = new ExperimentalPreferencesEntry();
                case "cg_general" -> fragment = komarugramPreferencesNavigator.INSTANCE.createGeneral();
                case "cg_premium" -> {
                    // Fuckoff :)
                    unknown.run();
                    return;
                }
                case "cg_privacy", "cg_security" -> fragment = komarugramPreferencesNavigator.INSTANCE.createPrivacyAndSecurity();
                case "cg_restart", "cg_reboot", "restart", "reboot" -> {
                    AppRestartHelper.triggerRebirth(fragment.getContext(), new Intent(fragment.getContext(), LaunchActivity.class));
                    return;
                }
                case "cg_update", "cg_upgrade", "update", "upgrade" -> {
                    if (komarugramCoreConfig.INSTANCE.isPlayStoreBuild()) {
                        Browser.openUrl(fragment.getContext(), Extra.PLAYSTORE_APP_URL);
                        return;
                    } else if (komarugramCoreConfig.INSTANCE.isStandalonePremiumBuild()) {
                        // Fuckoff :)
                        unknown.run();
                        return;
                    } else {
                        LaunchActivity.instance.checkCherryUpdate(progress);
                        return;
                    }
                }
                case "cg_updates", "updates" -> {
                    if (komarugramCoreConfig.INSTANCE.isPlayStoreBuild()) {
                        Browser.openUrl(fragment.getContext(), Extra.PLAYSTORE_APP_URL);
                    } else if (komarugramCoreConfig.INSTANCE.isStandalonePremiumBuild()) {
                        // Fuckoff :)
                        unknown.run();
                        return;
                    } else if (!komarugramCoreConfig.INSTANCE.isStandalonePremiumBuild()) {
                        UpdaterBottomSheet.showAlert(fragment.getContext(), fragment, false, null);
                    }
                    return;
                }
                case "cg_username_limits" -> {
                    fragment.showDialog(new LimitReachedBottomSheet(fragment, fragment.getContext(), LimitReachedBottomSheet.TYPE_PUBLIC_LINKS, fragment.getCurrentAccount(), fragment.getResourceProvider()));
                    return;
                }
                default -> {
                    unknown.run();
                    return;
                }
            }
        }
        callback.presentFragment(fragment);
    }

    public interface Callback {
        void presentFragment(BaseFragment fragment);
    }

}
