package com.ktc.setting.view.network.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ktc.setting.helper.NetworkHelper;

public class BootReceiver extends BroadcastReceiver {

    private static final String ACTION_STR_BOOT_COMPLETED = "android.intent.action.STR_BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        Log.d("dingyl", "action : " + action);
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)
                || action.equals(ACTION_STR_BOOT_COMPLETED)) {
            //NetworkHelper.getInstance(context).setWifiStatusBoot();
        }
    }
}
