package com.ktc.debughelper.util;

import android.util.Log;

/**
 * @author Arvin
 * @TOTO log日志输出类
 * @Date 2019.1.24
 * 
 * 在实际使用当中要注意,Log的TAG太长了,会输不出来.
 * 官方要求是不超过23个字符的。另外,使用Log工具不要使用v,d级别的,原因是v,d等低级别的log部分设备会过滤掉,导致无法输出
 */
public class LogUtil {

    public static final String FILTER = "KtcTvBug_";//方便使用logcat |grep 模糊过滤
    private static boolean debug = true;//标记是否开启日志记录

    public static void setDebug(boolean b){
        debug = b;
    }

    public static void v(String TAG, String msg) {
        if (debug){
        	Log.v(FILTER + TAG, msg);
        }
    }

    public static void d(String TAG, String msg) {
        if (debug){
            Log.d(FILTER + TAG, msg);
        }
    }

    public static void i(String TAG, String msg) {
        if (debug){
        	Log.i(FILTER + TAG, msg);
        }
    }

    public static void w(String TAG, String msg) {
        if (debug){
        	Log.w(FILTER + TAG, msg);
        }
    }

    public static void e(String TAG, String msg) {
        if (debug){
        	Log.e(FILTER + TAG, msg);
        }
    }

    public static void wtf(String TAG, String msg){
        if (debug){
        	Log.wtf(FILTER + TAG, msg);
        }
    }

}
