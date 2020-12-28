package com.ktc.setting.view.others.tts;

import android.content.Context;

//TODO for tts
public class TTSTool {
    public static boolean isTTSEnable(Context context) {
        /*return Settings.Secure.getInt(context.getContentResolver(),
                Settings.Secure.ACCESSIBILITY_SPEAK_IN_DTV, 0) == 1;*/
        return false;
    }

    public static void setTTSStatus(Context context, boolean enabled) {
        /*Settings.Secure.putInt(context.getContentResolver(),
                Settings.Secure.ACCESSIBILITY_SPEAK_IN_DTV, enabled ? 1 : 0);*/
    }
}
