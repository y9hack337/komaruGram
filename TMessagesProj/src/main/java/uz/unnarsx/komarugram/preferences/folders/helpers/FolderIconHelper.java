package uz.unnarsx.cherrygram.preferences.folders.helpers;

import static org.telegram.messenger.LocaleController.getString;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;

import java.util.LinkedHashMap;

import uz.unnarsx.cherrygram.core.configs.CherrygramAppearanceConfig;

public class FolderIconHelper {
    public static LinkedHashMap<String, Integer> folderIcons = new LinkedHashMap<>();

    static {
        folderIcons.put("\uD83D\uDC31", R.drawable.filter_cat_solar);
        folderIcons.put("\uD83D\uDCD5", R.drawable.filter_book_solar);
        folderIcons.put("\uD83D\uDCB0", R.drawable.filter_money_solar);
        folderIcons.put("\uD83C\uDFAE", R.drawable.filter_game_solar);
        folderIcons.put("\uD83D\uDCA1", R.drawable.filter_light_solar);
//        folderIcons.put("\uD83D\uDCA1", R.drawable.filter_light_solar2);
        folderIcons.put("\uD83D\uDC4C", R.drawable.filter_like_solar);
        folderIcons.put("\uD83C\uDFB5", R.drawable.filter_note_solar);
        folderIcons.put("\uD83C\uDFA8", R.drawable.filter_palette_solar);
        folderIcons.put("\u2708", R.drawable.filter_travel_solar);
        folderIcons.put("\u26BD", R.drawable.filter_sport_solar);
        folderIcons.put("\u2B50", R.drawable.filter_favorite_solar);
        folderIcons.put("\uD83C\uDF93", R.drawable.filter_study_solar);
        folderIcons.put("\uD83D\uDEEB", R.drawable.filter_airplane);
        folderIcons.put("\uD83D\uDC64", R.drawable.filter_private_solar);
        folderIcons.put("\uD83D\uDC65", R.drawable.filter_groups);
        folderIcons.put("\uD83D\uDCAC", R.drawable.filter_all);
        folderIcons.put("\u2705", R.drawable.filter_unread);
        folderIcons.put("\uD83E\uDD16", R.drawable.filter_bots_solar);
        folderIcons.put("\uD83D\uDC51", R.drawable.filter_crown);
        folderIcons.put("\uD83C\uDF39", R.drawable.filter_flower_solar);
        folderIcons.put("\uD83C\uDFE0", R.drawable.filter_home_solar);
        folderIcons.put("\u2764", R.drawable.filter_love_solar);
        folderIcons.put("\uD83C\uDFAD", R.drawable.filter_mask_solar);
        folderIcons.put("\uD83C\uDF78", R.drawable.filter_party_solar);
        folderIcons.put("\uD83D\uDCC8", R.drawable.filter_trade_solar);
        folderIcons.put("\uD83D\uDCBC", R.drawable.filter_work_solar);
        folderIcons.put("\uD83D\uDD14", R.drawable.filter_unmuted_solar);
        folderIcons.put("\uD83D\uDCE2", R.drawable.filter_channel);
        folderIcons.put("\uD83D\uDCC1", R.drawable.filter_custom_solar);
        folderIcons.put("\uD83D\uDCCB", R.drawable.filter_setup_solar);
    }

    public static String[] getEmoticonData(int newFilterFlags) {
        int flags = newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS;
        String newName = "";
        String newEmoticon = "";
        if ((flags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) == MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) {
            if ((newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
                newName = getString(R.string.FilterNameUnread);
                newEmoticon = "\u2705";
            } else if ((newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0) {
                newName = getString(R.string.FilterNameNonMuted);
                newEmoticon = "\uD83D\uDD14";
            }
        } else if ((flags & MessagesController.DIALOG_FILTER_FLAG_CONTACTS) != 0) {
            flags &= ~MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
            if (flags == 0) {
                newName = getString(R.string.FilterContacts);
                newEmoticon = "\uD83D\uDC64";
            } else if ((flags & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0) {
                flags &= ~MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                if (flags == 0) {
                    newName = getString(R.string.FilterContacts);
                    newEmoticon = "\uD83D\uDC64";
                }
            }
        } else if ((flags & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0) {
            flags &= ~MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
            if (flags == 0) {
                newName = getString(R.string.FilterNonContacts);
                newEmoticon = "\uD83D\uDC64";
            }
        } else if ((flags & MessagesController.DIALOG_FILTER_FLAG_GROUPS) != 0) {
            flags &= ~MessagesController.DIALOG_FILTER_FLAG_GROUPS;
            if (flags == 0) {
                newName = getString(R.string.FilterGroups);
                newEmoticon = "\uD83D\uDC65";
            }
        } else if ((flags & MessagesController.DIALOG_FILTER_FLAG_BOTS) != 0) {
            flags &= ~MessagesController.DIALOG_FILTER_FLAG_BOTS;
            if (flags == 0) {
                newName = getString(R.string.FilterBots);
                newEmoticon = "\uD83E\uDD16";
            }
        } else if ((flags & MessagesController.DIALOG_FILTER_FLAG_CHANNELS) != 0) {
            flags &= ~MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
            if (flags == 0) {
                newName = getString(R.string.FilterChannels);
                newEmoticon = "\uD83D\uDCE2";
            }
        }
        return new String[]{newName, newEmoticon};
    }

    public static int getIconWidth() {
        return AndroidUtilities.dp(28);
    }

    public static int getPadding() {
        if (CherrygramAppearanceConfig.INSTANCE.getTabMode() == CherrygramAppearanceConfig.TAB_TYPE_MIX) {
            return AndroidUtilities.dp(6);
        }
        return 0;
    }

    public static int getTotalIconWidth() {
        int result = 0;
        if (CherrygramAppearanceConfig.INSTANCE.getTabMode() != CherrygramAppearanceConfig.TAB_TYPE_TEXT) {
            result = getIconWidth() + getPadding();
        }
        return result;
    }

    public static int getPaddingTab() {
        if (CherrygramAppearanceConfig.INSTANCE.getTabMode() != CherrygramAppearanceConfig.TAB_TYPE_ICON ||
                CherrygramAppearanceConfig.INSTANCE.getTabStyle() >= CherrygramAppearanceConfig.TAB_STYLE_VKUI) {
            return AndroidUtilities.dp(32);
        }
        return AndroidUtilities.dp(16);
    }

    public static int getTabIcon(String emoji) {
        if (emoji != null) {
            var folderIcon = folderIcons.get(emoji);
            if (folderIcon != null) {
                return folderIcon;
            }
        }
        return R.drawable.filter_custom_solar;
    }
}