package com.ktc.setting.helper;

import android.os.SystemProperties;
import android.util.Log;

public class LogUtil {

    private final static String TAG = "KTCSetting";

    private static boolean enable = false;
    private static LogUtil instance;

    private LogUtil() {
        enable = SystemProperties.getBoolean("setting.debug", false);
    }

    public static LogUtil getInstance() {
        if (instance == null) {
            instance = new LogUtil();
        }
        return instance;
    }

    public void d(String tag, String msg) {
        if (enable) {
            Log.d(tag, msg);
        }
    }

    public void w(String tag, String msg) {
        if (enable) {
            Log.w(tag, msg);
        }
    }

    public void e(String tag, String msg) {
        if (enable) {
            Log.e(tag, msg);
        }
    }

    public void d(String msg) {
        d(TAG, msg);
    }

    public void w(String msg) {
        w(TAG, msg);
    }

    public void e(String msg) {
        e(TAG, msg);
    }
}
