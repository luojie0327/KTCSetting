package com.ktc.setting.view.network;

import android.provider.Settings;

import com.ktc.setting.view.base.BaseActivity;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.network.wifi.WifiFragment;

public class NetworkActivity extends BaseActivity {

    @Override
    protected BaseFragment getFragment() {
        String action = getIntent().getAction();
        if (Settings.ACTION_WIFI_SETTINGS.equals(action)
                || "com.android.net.wifi.SETUP_WIFI_NETWORK".equals(action)
                || "com.android.net.wifi.CANVAS_SETUP_WIFI_NETWORK".equals(action)) {
            return new WifiFragment();
        }

        return new NetworkFragment();
    }
}
