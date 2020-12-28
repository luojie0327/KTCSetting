package com.ktc.setting.view.others.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;


public class BluetoothEventHandler {

    private static final String TAG = BluetoothEventHandler.class.getSimpleName();
    private Context mContext;
    private Handler mHandler;


    public BluetoothEventHandler(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        registerStateReceiver();
        registerChangeReceiver();
    }

    public void release() {
        unRegisterReceiver();
    }

    private void registerStateReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mBluetoothStateReceiver, intentFilter);
    }

    private void registerChangeReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        mContext.registerReceiver(mBluetoothChangeReceiver, intentFilter);
    }

    private void unRegisterReceiver() {
        mContext.unregisterReceiver(mBluetoothStateReceiver);
        mContext.unregisterReceiver(mBluetoothChangeReceiver);
    }

    private BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            break;
                        case BluetoothAdapter.STATE_ON:
                        case BluetoothAdapter.STATE_BLE_ON:
                            mHandler.sendEmptyMessage(Constants.STATE_ON);
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            mHandler.sendEmptyMessage(Constants.STATE_OFF);
                            break;
                    }
                    break;
            }
        }
    };

    private BroadcastReceiver mBluetoothChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;
            Message message = mHandler.obtainMessage();
            message.obj = intent;
            switch (intent.getAction()) {
                case BluetoothDevice.ACTION_FOUND:
                    message.what = Constants.ACTION_FOUND;
                    mHandler.sendMessage(message);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    mHandler.sendEmptyMessage(Constants.ACTION_DISCOVERY_FINISHED);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    mHandler.sendEmptyMessage(Constants.ACTION_DISCOVERY_STARTED);
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    message.what = Constants.ACTION_BOND_STATE_CHANGED;
                    mHandler.sendMessage(message);
                    break;
                case BluetoothDevice.ACTION_PAIRING_REQUEST:
                    message.what = Constants.ACTION_PAIRING_REQUEST;
                    mHandler.sendMessage(message);
                    break;
                case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    mHandler.sendEmptyMessage(Constants.ACTION_CONNECTION_STATE_CHANGED);
                    break;
            }
        }
    };
}
