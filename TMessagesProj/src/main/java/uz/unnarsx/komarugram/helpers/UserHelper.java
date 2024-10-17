package uz.unnarsx.komarugram.helpers;

import static org.telegram.messenger.LocaleController.getString;

import android.app.Activity;
import android.content.Intent;

import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BaseController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Objects;

import uz.unnarsx.komarugram.Extra;
import uz.unnarsx.komarugram.core.updater.UpdaterUtils;

public class UserHelper extends BaseController {

    private static final UserHelper[] Instance = new UserHelper[UserConfig.MAX_ACCOUNT_COUNT];

    public UserHelper(int num) {
        super(num);
    }

    public static UserHelper getInstance(int num) {
        UserHelper localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (UserHelper.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    Instance[num] = localInstance = new UserHelper(num);
                }
            }
        }
        return localInstance;
    }

    public static final DispatchQueue regDateQueue = new DispatchQueue("regDateQueue");

    private final String uri = "";
    private final String secret = "";
    public String type, date;
    private StringBuilder formattedDate;

    public interface OnResponseNotReceived {
        void run();
    }

    public interface OnResponseReceived {
        void run();
    }

}
