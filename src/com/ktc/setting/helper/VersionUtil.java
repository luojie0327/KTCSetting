package com.ktc.setting.helper;

import android.os.Build;

public class VersionUtil {

    /**
     * 目前没有Android7.0的平台，如果某些改动是从Android7.0开始的，则需要换这个方法
     */
    public static boolean isCurrentHighApi() {
        return Build.VERSION.SDK_INT >= 26;
    }

    public static boolean isCurrentAndroidOreoOrHigher() {
        return Build.VERSION.SDK_INT >= 26;
    }
}
