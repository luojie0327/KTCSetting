package com.ktc.setting.view.restore;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

public class PasswordManager {

    private volatile static PasswordManager instance;

    private Context mContext;
    private ContentResolver mContentResolver;
    private final static String KEY_SYSTEM_LOCK_OPEN = "SYSTEM_LOCK_OPEN";
    private final static String KEY_SYSTEM_LOCK_PWD = "SYSTEM_LOCK_PWD";
	private final static String HOTEL_MENU_ON_OFF = "HOTEL_MENU_ON_OFF";
	private final static String HOTEL_RESET_ON_OFF = "HOTEL_RESET_ON_OFF";
    private final static String DEFAULT_PASSWORD = "0000";

    private PasswordManager(Context context) {
        mContext = context;
        mContentResolver = mContext.getContentResolver();
    }

    public static PasswordManager getInstance(Context context) {
        if (instance == null) {
            synchronized (PasswordManager.class) {
                if (instance == null) {
                    instance = new PasswordManager(context);
                }
            }
        }
        return instance;
    }

    public boolean isSystemLock() {
        boolean result = false;
        try {
            result = Settings.Global.getInt(mContentResolver, KEY_SYSTEM_LOCK_OPEN) == 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
	
	public boolean isHotelResetLockOn(){
        if((Settings.Global.getInt(mContentResolver, HOTEL_MENU_ON_OFF,0)==1)
        &&(Settings.Global.getInt(mContentResolver, HOTEL_RESET_ON_OFF,1)==1)){
            return true;
        }
        return false;
    }

    public String getCurrentPassword() {
        String password = Settings.Global.getString(mContentResolver, KEY_SYSTEM_LOCK_PWD);
        if (TextUtils.isEmpty(password)) {
            password = DEFAULT_PASSWORD;
        }
        return password;
    }
}
