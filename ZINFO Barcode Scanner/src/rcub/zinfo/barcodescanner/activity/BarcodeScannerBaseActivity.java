package rcub.zinfo.barcodescanner.activity;

import android.support.v7.app.AppCompatActivity;

import java.util.Date;

/**
 * Created by ivan.radojevic on 11.05.2015..
 * Activity koji predstavlja osnovu za sve ostale i koji cuva zajednicke promenljive.
 */
public abstract class BarcodeScannerBaseActivity extends AppCompatActivity {

    /**
     * Sluzi za prepoznavanje iz kog se @Activity dobija rezultat
     */
    public static final int ACTIVITY_SCAN = 0;
    public static final int ACTIVITY_MAIN = 1;

    public static String MY_PREFS_NAME = "prefs";

    private static String username;
    private static String password;
    private static Date sessionTime;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        BarcodeScannerBaseActivity.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        BarcodeScannerBaseActivity.password = password;
    }

    public static Date getSessionTime() {
        return sessionTime;
    }

    public static void setSessionTime(Date sessionTime) {
        BarcodeScannerBaseActivity.sessionTime = sessionTime;
    }
}
