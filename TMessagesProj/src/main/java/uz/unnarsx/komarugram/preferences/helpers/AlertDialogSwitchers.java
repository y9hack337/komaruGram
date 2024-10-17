package uz.unnarsx.komarugram.preferences.helpers;

import static org.telegram.messenger.LocaleController.getString;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.LayoutHelper;

import java.util.Random;

import uz.unnarsx.komarugram.core.configs.komarugramAppearanceConfig;
import uz.unnarsx.komarugram.core.configs.komarugramChatsConfig;

public class AlertDialogSwitchers {

    public static void showChatActionsAlert(BaseFragment fragment) {
        if (fragment.getParentActivity() == null) {
            return;
        }
        Context context = fragment.getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.CP_ChatMenuShortcuts));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
        linearLayoutInviteContainer.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        int count = 6;
        for (int a = 0; a < count; a++) {
            TextCell textCell = new TextCell(context, 23, false, true, fragment.getResourceProvider());
            switch (a) {
                case 0: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.CG_JumpToBeginning), komarugramChatsConfig.INSTANCE.getShortcut_JumpToBegin(), R.drawable.ic_upward, false);
                    break;
                }
                case 1: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.CG_DeleteAllFromSelf), komarugramChatsConfig.INSTANCE.getShortcut_DeleteAll(), R.drawable.msg_delete, false);
                    break;
                }
                case 2: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.SavedMessages), komarugramChatsConfig.INSTANCE.getShortcut_SavedMessages(), R.drawable.msg_saved, false);
                    break;
                }
                case 3: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.BlurInChat), komarugramChatsConfig.INSTANCE.getShortcut_Blur(), R.drawable.msg_theme, true);
                    break;
                }
                case 4: {
                    textCell.setTextAndCheckAndIcon("Telegram Browser", komarugramChatsConfig.INSTANCE.getShortcut_Browser(), R.drawable.msg_language, true);
                    break;
                }
                case 5: {
                    textCell.checkBox.setVisibility(View.INVISIBLE);
                    textCell.setTextAndIcon(getString(R.string.CP_AdminActions), R.drawable.msg_admins, false);
                    break;
                }
            }
            textCell.setTag(a);
            textCell.setBackground(Theme.getSelectorDrawable(false));
            linearLayoutInviteContainer.addView(textCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            textCell.setOnClickListener(v2 -> {
                Integer tag = (Integer) v2.getTag();
                switch (tag) {
                    case 0: {
                        komarugramChatsConfig.INSTANCE.toggleShortcutJumpToBegin();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShortcut_JumpToBegin());
                        break;
                    }
                    case 1: {
                        komarugramChatsConfig.INSTANCE.toggleShortcutDeleteAll();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShortcut_DeleteAll());
                        break;
                    }
                    case 2: {
                        komarugramChatsConfig.INSTANCE.toggleShortcutSavedMessages();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShortcut_SavedMessages());
                        break;
                    }
                    case 3: {
                        komarugramChatsConfig.INSTANCE.toggleShortcutBlur();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShortcut_Blur());
                        break;
                    }
                    case 4: {
                        komarugramChatsConfig.INSTANCE.toggleShortcutBrowser();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShortcut_Browser());
                        break;
                    }
                    case 5: {
                        showAdminActionsAlert(fragment);
                        break;
                    }
                }
                fragment.getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
            });
        }
        builder.setPositiveButton(getString(R.string.OK), null);
        builder.setView(linearLayout);
        fragment.showDialog(builder.create());
    }

    public static void showAdminActionsAlert(BaseFragment fragment) {
        if (fragment.getParentActivity() == null) {
            return;
        }
        Context context = fragment.getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.CP_AdminActions));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
        linearLayoutInviteContainer.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        int count = 6;
        for (int a = 0; a < count; a++) {
            TextCell textCell = new TextCell(context, 23, false, true, fragment.getResourceProvider());
            switch (a) {
                case 0: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.Reactions), komarugramChatsConfig.INSTANCE.getAdmins_Reactions(), R.drawable.msg_reactions2, true);
                    break;
                }
                case 1: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.ChannelPermissions), komarugramChatsConfig.INSTANCE.getAdmins_Permissions(), R.drawable.msg_permissions, true);
                    break;
                }
                case 2: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.ChannelAdministrators), komarugramChatsConfig.INSTANCE.getAdmins_Administrators(), R.drawable.msg_admins, true);
                    break;
                }
                case 3: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.ChannelMembers), komarugramChatsConfig.INSTANCE.getAdmins_Members(), R.drawable.msg_groups, true);
                    break;
                }
                case 4: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.StatisticsAndBoosts), komarugramChatsConfig.INSTANCE.getAdmins_Statistics(), R.drawable.msg_stats, true);
                    break;
                }
                case 5: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.EventLog), komarugramChatsConfig.INSTANCE.getAdmins_RecentActions(), R.drawable.msg_log, true);
                    break;
                }
            }
            textCell.setTag(a);
            textCell.setBackground(Theme.getSelectorDrawable(false));
            linearLayoutInviteContainer.addView(textCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            textCell.setOnClickListener(v2 -> {
                Integer tag = (Integer) v2.getTag();
                switch (tag) {
                    case 0: {
                        komarugramChatsConfig.INSTANCE.toggleAdminsReactions();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getAdmins_Reactions());
                        break;
                    }
                    case 1: {
                        komarugramChatsConfig.INSTANCE.toggleAdminsPermissions();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getAdmins_Permissions());
                        break;
                    }
                    case 2: {
                        komarugramChatsConfig.INSTANCE.toggleAdminsAdministrators();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getAdmins_Administrators());
                        break;
                    }
                    case 3: {
                        komarugramChatsConfig.INSTANCE.toggleAdminsMembers();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getAdmins_Members());
                        break;
                    }
                    case 4: {
                        komarugramChatsConfig.INSTANCE.toggleAdminsStatistics();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getAdmins_Statistics());
                        break;
                    }
                    case 5: {
                        komarugramChatsConfig.INSTANCE.toggleAdminsRecentActions();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getAdmins_RecentActions());
                        break;
                    }
                }
                fragment.getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
            });
        }
        builder.setPositiveButton(getString(R.string.OK), null);
        builder.setView(linearLayout);
        fragment.showDialog(builder.create());
    }

    public static void showDirectShareAlert(BaseFragment fragment) {
        if (fragment.getParentActivity() == null) {
            return;
        }
        Context context = fragment.getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.DirectShare));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
        linearLayoutInviteContainer.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        int count = 6;
        for (int a = 0; a < count; a++) {
            TextCell textCell = new TextCell(context, 23, false, true, fragment.getResourceProvider());
            switch (a) {
                case 0: {
                    int[] drawableIDs = {R.drawable.msg_replace_solar, R.drawable.large_repost_story, R.drawable.msg_forward_replace_solar, R.drawable.msg_menu_stories};
                    int storyIcon = drawableIDs[new Random().nextInt(4)];

                    textCell.setTextAndCheckAndIcon(getString(R.string.RepostToStory), komarugramChatsConfig.INSTANCE.getShareDrawStoryButton(), storyIcon, true);
                    break;
                }
                case 1: {
                    textCell.setTextAndCheck(getString(R.string.FilterChats), komarugramChatsConfig.INSTANCE.getUsersDrawShareButton(), false);
                    break;
                }
                case 2: {
                    textCell.setTextAndCheck(getString(R.string.FilterGroups), komarugramChatsConfig.INSTANCE.getSupergroupsDrawShareButton(), false);
                    break;
                }
                case 3: {
                    textCell.setTextAndCheck(getString(R.string.FilterChannels), komarugramChatsConfig.INSTANCE.getChannelsDrawShareButton(), false);
                    break;
                }
                case 4: {
                    textCell.setTextAndCheck(getString(R.string.FilterBots), komarugramChatsConfig.INSTANCE.getBotsDrawShareButton(), false);
                    break;
                }
                case 5: {
                    textCell.setTextAndCheck(getString(R.string.StickersName), komarugramChatsConfig.INSTANCE.getStickersDrawShareButton(), false);
                    break;
                }
            }
            textCell.setTag(a);
            textCell.setBackground(Theme.getSelectorDrawable(false));
            linearLayoutInviteContainer.addView(textCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            textCell.setOnClickListener(v2 -> {
                Integer tag = (Integer) v2.getTag();
                switch (tag) {
                    case 0: {
                        komarugramChatsConfig.INSTANCE.toggleShareDrawStoryButton();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShareDrawStoryButton());
                        break;
                    }
                    case 1: {
                        komarugramChatsConfig.INSTANCE.toggleUsersDrawShareButton();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getUsersDrawShareButton());
                        break;
                    }
                    case 2: {
                        komarugramChatsConfig.INSTANCE.toggleSupergroupsDrawShareButton();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getSupergroupsDrawShareButton());
                        break;
                    }
                    case 3: {
                        komarugramChatsConfig.INSTANCE.toggleChannelsDrawShareButton();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getChannelsDrawShareButton());
                        break;
                    }
                    case 4: {
                        komarugramChatsConfig.INSTANCE.toggleBotsDrawShareButton();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getBotsDrawShareButton());
                        break;
                    }
                    case 5: {
                        komarugramChatsConfig.INSTANCE.toggleStickersDrawShareButton();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getStickersDrawShareButton());
                        break;
                    }
                }
                fragment.getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
            });
        }
        builder.setPositiveButton(getString(R.string.OK), null);
        builder.setView(linearLayout);
        fragment.showDialog(builder.create());
    }

    public static void showDrawerIconsAlert(BaseFragment fragment) {
        if (fragment.getParentActivity() == null) {
            return;
        }
        Context context = fragment.getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.AP_DrawerButtonsCategory));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
        linearLayoutInviteContainer.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        int count = 10;
        for (int a = 0; a < count; a++) {
            TextCell textCell = new TextCell(context, 23, false, true, fragment.getResourceProvider());
            switch (a) {
                case 0: {
                    UserConfig me = UserConfig.getInstance(UserConfig.selectedAccount);
                    textCell.setTextAndCheckAndIcon(
                            me.getEmojiStatus() != null ? getString(R.string.ChangeEmojiStatus) : getString(R.string.SetEmojiStatus),
                            komarugramAppearanceConfig.INSTANCE.getChangeStatusDrawerButton(),
                            me.getEmojiStatus() != null ? R.drawable.msg_status_edit : R.drawable.msg_status_set,
                            false
                    );
                    break;
                }
                /*case 1: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.ProfileStories), komarugramAppearanceConfig.INSTANCE.getMyStoriesDrawerButton(), R.drawable.msg_menu_stories, true);
                    break;
                }*/
                case 1: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.MyProfile), komarugramAppearanceConfig.INSTANCE.getMyProfileDrawerButton(), R.drawable.left_status_profile, true);
                    break;
                }
                case 2: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.NewGroup), komarugramAppearanceConfig.INSTANCE.getCreateGroupDrawerButton(), R.drawable.msg_groups, false);
                    break;
                }
                case 3: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.NewChannel), komarugramAppearanceConfig.INSTANCE.getCreateChannelDrawerButton(), R.drawable.msg_channel, false);
                    break;
                }
                case 4: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.Contacts), komarugramAppearanceConfig.INSTANCE.getContactsDrawerButton(), R.drawable.msg_contacts, false);
                    break;
                }
                case 5: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.Calls), komarugramAppearanceConfig.INSTANCE.getCallsDrawerButton(), R.drawable.msg_calls, false);
                    break;
                }
                case 6: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.SavedMessages), komarugramAppearanceConfig.INSTANCE.getSavedMessagesDrawerButton(), R.drawable.msg_saved, false);
                    break;
                }
                case 7: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.ArchivedChats), komarugramAppearanceConfig.INSTANCE.getArchivedChatsDrawerButton(), R.drawable.msg_archive, false);
                    break;
                }
                case 8: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.AuthAnotherClient), komarugramAppearanceConfig.INSTANCE.getScanQRDrawerButton(), R.drawable.msg_qrcode, false);
                    break;
                }
                case 9: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.CGP_AdvancedSettings), komarugramAppearanceConfig.INSTANCE.getCGPreferencesDrawerButton(), R.drawable.msg_settings, false);
                    break;
                }
            }
            textCell.setTag(a);
            textCell.setBackground(Theme.getSelectorDrawable(false));
            linearLayoutInviteContainer.addView(textCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            textCell.setOnClickListener(v2 -> {
                Integer tag = (Integer) v2.getTag();
                switch (tag) {
                    case 0: {
                        komarugramAppearanceConfig.INSTANCE.toggleChangeStatusDrawerButton();
                        textCell.setChecked(komarugramAppearanceConfig.INSTANCE.getChangeStatusDrawerButton());
                        break;
                    }
                    /*case 1: {
                        komarugramAppearanceConfig.INSTANCE.toggleMyStoriesDrawerButton();
                        textCell.setChecked(komarugramAppearanceConfig.INSTANCE.getMyStoriesDrawerButton());
                        break;
                    }*/
                    case 1: {
                        komarugramAppearanceConfig.INSTANCE.toggleMyProfileDrawerButton();
                        textCell.setChecked(komarugramAppearanceConfig.INSTANCE.getMyProfileDrawerButton());
                        break;
                    }
                    case 2: {
                        komarugramAppearanceConfig.INSTANCE.toggleCreateGroupDrawerButton();
                        textCell.setChecked(komarugramAppearanceConfig.INSTANCE.getCreateGroupDrawerButton());
                        break;
                    }
                    case 3: {
                        komarugramAppearanceConfig.INSTANCE.toggleCreateChannelDrawerButton();
                        textCell.setChecked(komarugramAppearanceConfig.INSTANCE.getCreateChannelDrawerButton());
                        break;
                    }
                    case 4: {
                        komarugramAppearanceConfig.INSTANCE.toggleContactsDrawerButton();
                        textCell.setChecked(komarugramAppearanceConfig.INSTANCE.getContactsDrawerButton());
                        break;
                    }
                    case 5: {
                        komarugramAppearanceConfig.INSTANCE.toggleCallsDrawerButton();
                        textCell.setChecked(komarugramAppearanceConfig.INSTANCE.getCallsDrawerButton());
                        break;
                    }
                    case 6: {
                        komarugramAppearanceConfig.INSTANCE.toggleSavedMessagesDrawerButton();
                        textCell.setChecked(komarugramAppearanceConfig.INSTANCE.getSavedMessagesDrawerButton());
                        break;
                    }
                    case 7: {
                        komarugramAppearanceConfig.INSTANCE.toggleArchivedChatsDrawerButton();
                        textCell.setChecked(komarugramAppearanceConfig.INSTANCE.getArchivedChatsDrawerButton());
                        break;
                    }
                    case 8: {
                        komarugramAppearanceConfig.INSTANCE.toggleScanQRDrawerButton();
                        textCell.setChecked(komarugramAppearanceConfig.INSTANCE.getScanQRDrawerButton());
                        break;
                    }
                    case 9: {
                        komarugramAppearanceConfig.INSTANCE.toggleCGPreferencesDrawerButton();
                        textCell.setChecked(komarugramAppearanceConfig.INSTANCE.getCGPreferencesDrawerButton());
                        break;
                    }
                }
                fragment.getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
            });
        }
        builder.setPositiveButton(getString(R.string.OK), null);
        builder.setView(linearLayout);
        fragment.showDialog(builder.create());
    }

    public static void showChatMenuIconsAlert(BaseFragment fragment) {
        if (fragment.getParentActivity() == null) {
            return;
        }
        Context context = fragment.getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.CP_MessageMenu));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
        linearLayoutInviteContainer.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        int count = 11;
        for (int a = 0; a < count; a++) {
            TextCell textCell = new TextCell(context, 23, false, true, fragment.getResourceProvider());
            switch (a) {
                case 0: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.SaveForNotifications), komarugramChatsConfig.INSTANCE.getShowSaveForNotifications(), R.drawable.msg_tone_add, false);
                    break;
                }
                case 1: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.Reply), komarugramChatsConfig.INSTANCE.getShowReply(), R.drawable.menu_reply, false);
                    break;
                }
                case 2: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.CG_CopyPhoto), komarugramChatsConfig.INSTANCE.getShowCopyPhoto(), R.drawable.msg_copy, false);
                    break;
                }
                case 3: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.CG_CopyPhotoAsSticker), komarugramChatsConfig.INSTANCE.getShowCopyPhotoAsSticker(), R.drawable.msg_sticker, false);
                    break;
                }
                case 4: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.CG_ClearFromCache), komarugramChatsConfig.INSTANCE.getShowClearFromCache(), R.drawable.clear_cache, false);
                    break;
                }
                case 5: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.Forward), komarugramChatsConfig.INSTANCE.getShowForward(), R.drawable.msg_forward, false);
                    break;
                }
                case 6: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.Forward) + getString(R.string.CG_Without_Authorship), komarugramChatsConfig.INSTANCE.getShowForwardWoAuthorship(), R.drawable.msg_forward, false);
                    break;
                }
                case 7: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.CG_ViewUserHistory), komarugramChatsConfig.INSTANCE.getShowViewHistory(), R.drawable.msg_recent, false);
                    break;
                }
                case 8: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.CG_ToSaved), komarugramChatsConfig.INSTANCE.getShowSaveMessage(), R.drawable.msg_saved, false);
                    break;
                }
                case 9: {
                    textCell.setTextAndCheckAndIcon(getString(R.string.ReportChat), komarugramChatsConfig.INSTANCE.getShowReport(), R.drawable.msg_report, false);
                    break;
                }
                case 10: {
                    textCell.setTextAndCheckAndIcon("JSON", komarugramChatsConfig.INSTANCE.getShowJSON(), R.drawable.msg_info, false);
                    break;
                }
            }
            textCell.setTag(a);
            textCell.setBackground(Theme.getSelectorDrawable(false));
            linearLayoutInviteContainer.addView(textCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            textCell.setOnClickListener(v2 -> {
                Integer tag = (Integer) v2.getTag();
                switch (tag) {
                    case 0: {
                        komarugramChatsConfig.INSTANCE.toggleShowSaveForNotifications();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShowSaveForNotifications());
                        break;
                    }
                    case 1: {
                        komarugramChatsConfig.INSTANCE.toggleShowReply();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShowReply());
                        break;
                    }
                    case 2: {
                        komarugramChatsConfig.INSTANCE.toggleShowCopyPhoto();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShowCopyPhoto());
                        break;
                    }
                    case 3: {
                        komarugramChatsConfig.INSTANCE.toggleShowCopyPhotoAsSticker();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShowCopyPhotoAsSticker());
                        break;
                    }
                    case 4: {
                        komarugramChatsConfig.INSTANCE.toggleShowClearFromCache();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShowClearFromCache());
                        break;
                    }
                    case 5: {
                        komarugramChatsConfig.INSTANCE.toggleShowForward();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShowForward());
                        break;
                    }
                    case 6: {
                        komarugramChatsConfig.INSTANCE.toggleShowForwardWoAuthorship();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShowForwardWoAuthorship());
                        break;
                    }
                    case 7: {
                        komarugramChatsConfig.INSTANCE.toggleShowViewHistory();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShowViewHistory());
                        break;
                    }
                    case 8: {
                        komarugramChatsConfig.INSTANCE.toggleShowSaveMessage();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShowSaveMessage());
                        break;
                    }
                    case 9: {
                        komarugramChatsConfig.INSTANCE.toggleShowReport();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShowReport());
                        break;
                    }
                    case 10: {
                        komarugramChatsConfig.INSTANCE.toggleShowJSON();
                        textCell.setChecked(komarugramChatsConfig.INSTANCE.getShowJSON());
                        break;
                    }
                }
                fragment.getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
            });
        }
        builder.setPositiveButton(getString(R.string.OK), null);
        builder.setView(linearLayout);
        fragment.showDialog(builder.create());
    }

}
