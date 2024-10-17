package uz.unnarsx.komarugram.core.helpers;

import java.util.ArrayList;

import uz.unnarsx.komarugram.misc.Constants;

public class LocalVerificationsHelper {
    private static final ArrayList<Long> DEFAULT_VERIFY_LIST = new ArrayList<>();
    private static final ArrayList<Long> HIDE_DELETE_ALL_BUTTON = new ArrayList<>();

    static {
        DEFAULT_VERIFY_LIST.add(Constants.komarugram_Channel);
        DEFAULT_VERIFY_LIST.add(Constants.komarugram_Support);
        DEFAULT_VERIFY_LIST.add(Constants.komarugram_APKs);
        DEFAULT_VERIFY_LIST.add(Constants.komarugram_Beta);
        DEFAULT_VERIFY_LIST.add(Constants.komarugram_Archive);

        HIDE_DELETE_ALL_BUTTON.add(Constants.komarugram_Support);
        HIDE_DELETE_ALL_BUTTON.add(1201287079L); // Abitur
    }

    public static ArrayList<Long> getVerify() {
        return DEFAULT_VERIFY_LIST;
    }

    public static ArrayList<Long> hideDeleteAll() {
        return HIDE_DELETE_ALL_BUTTON;
    }

}
