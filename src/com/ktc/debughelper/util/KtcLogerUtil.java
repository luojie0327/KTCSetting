package com.ktc.debughelper.util;

import android.util.Log;

/**
 * TODO KTC打印log日志
 *
 * @author Arvin
 * @Time 2018-1-10 上午09:02:10
 */
public class KtcLogerUtil {

    private static final String TAG = "KtcLogerUtil";
    public static boolean enableLog = false;

    public static void setLogerEnable(boolean toEnable) {
        enableLog = toEnable;
    }

    /**
     * TODO i信息
     *
     * @param tag , msg
     * @return void
     */
    public static void I(String tag, String msg) {
        if (enableLog) {
            Log.i(tag, msg);
        }
    }

    /**
     * TODO d信息
     *
     * @param tag , msg
     * @return void
     */
    public static void D(String tag, String msg) {
        if (enableLog) {
            Log.d(tag, msg);
        }
    }

    /**
     * TODO W信息
     *
     * @param tag , msg
     * @return void
     */
    public static void W(String tag, String msg) {
        if (enableLog) {
            Log.w(tag, msg);
        }
    }

    /**
     * TODO E信息
     *
     * @param tag , msg
     * @return void
     */
    public static void E(String tag, String msg) {
        if (enableLog) {
            Log.e(tag, msg);
        }
    }

    /**
     * TODO info信息,不受控于enableLog
     *
     * @param tag , msg
     * @return void
     */
    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }

}
