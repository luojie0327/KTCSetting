package com.ktc.setting.view.universal.security;

import android.content.Context;
import android.os.UserManager;
import android.provider.Settings;

/**
 * unknown source for Android M
 */
public class SecurityTool {

    public static boolean isNonMarketAppsAllowed(Context context) {
        return Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.INSTALL_NON_MARKET_APPS, 0) > 0;
    }

    public static void setNonMarketAppsAllowed(Context context, boolean enabled) {
        final UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
        if (um.hasUserRestriction(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES)) {
            return;
        }
        // Change the system setting
        Settings.Global.putInt(context.getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS,
                enabled ? 1 : 0);
    }
}
