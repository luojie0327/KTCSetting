package com.ktc.setting.view.universal.devicename;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class DeviceNameManager {

    private static final String TAG = DeviceNameManager.class.getSimpleName();
    private Context mContext;
    private OnDeviceNameChangeListener mOnDeviceNameChangeListener;
    public static final String ACTION_DEVICE_NAME_UPDATE =
            "com.android.tv.settings.name.DeviceManager.DEVICE_NAME_UPDATE";

    public DeviceNameManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public void setOnDeviceNameChangeListener(OnDeviceNameChangeListener onDeviceNameChangeListener) {
        mOnDeviceNameChangeListener = onDeviceNameChangeListener;
    }

    public String getDeviceName() {
 //       return SystemProperties.get("persist.sys.ktc.board.device","KTC");
	return SystemProperties.get("persist.sys.ktc.device.name","KTC");
    }

    public void setDeviceName(String name) {
        try {
            //Settings.Global.putString(mContext.getContentResolver(), Settings.Global.DEVICE_NAME, name);
            //SystemProperties.set("persist.sys.ktc.board.device",name);
		SystemProperties.set("persist.sys.ktc.device.name",name);
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            if (btAdapter != null) {
                btAdapter.setName(name);
            } else {
                Log.v(TAG, "Bluetooth adapter is null. Running on device without bluetooth?");
            }
            LocalBroadcastManager.getInstance(mContext)
                    .sendBroadcast(new Intent(ACTION_DEVICE_NAME_UPDATE));
            if (mOnDeviceNameChangeListener != null) mOnDeviceNameChangeListener.onSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            if (mOnDeviceNameChangeListener != null) mOnDeviceNameChangeListener.onFailed();
        }
    }

}
