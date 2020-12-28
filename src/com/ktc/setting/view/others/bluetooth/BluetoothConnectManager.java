package com.ktc.setting.view.others.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;

public class BluetoothConnectManager {

    public static boolean getConnectState(BluetoothProfile bluetoothProfile, BluetoothDevice device) {
        if (bluetoothProfile == null) {
            return false;
        }
        int deviceClass = device.getBluetoothClass().getMajorDeviceClass();
        if (deviceClass == BluetoothClass.Device.Major.AUDIO_VIDEO) {
            return bluetoothProfile.getConnectionState(device) == BluetoothA2dp.STATE_CONNECTED;
        } else {
            return device.isConnected();
        }
    }

    public static void openConnection(BluetoothProfile bluetoothProfile, BluetoothDevice device) {
        if (bluetoothProfile == null) {
            return;
        }
        int deviceClass = device.getBluetoothClass().getMajorDeviceClass();
        if (deviceClass == BluetoothClass.Device.Major.AUDIO_VIDEO) {
            ((BluetoothA2dp) bluetoothProfile).connect(device);
        }
    }

    public static void closeConnection(BluetoothProfile bluetoothProfile, BluetoothDevice device) {
        if (bluetoothProfile == null) {
            return;
        }
        int deviceClass = device.getBluetoothClass().getMajorDeviceClass();
        if (deviceClass == BluetoothClass.Device.Major.AUDIO_VIDEO) {
            ((BluetoothA2dp) bluetoothProfile).disconnect(device);
        }
    }

}
