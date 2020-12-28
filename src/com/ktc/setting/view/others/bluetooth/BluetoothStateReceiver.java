package com.ktc.setting.view.others.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.ktc.setting.R;
import com.ktc.setting.view.custom.ToastFactory;

public class BluetoothStateReceiver extends BroadcastReceiver {

    private static final String TAG = BluetoothStateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) return;
        switch (intent.getAction()) {
            case BluetoothDevice.ACTION_PAIRING_REQUEST:
                BluetoothDevice pairingDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int type = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT,
                        BluetoothDevice.ERROR);
                int pairingKey = BluetoothDevice.ERROR;
                Log.d(TAG, "pairing request: type = " + type + "pairing key = " + pairingKey);
                if (type == BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION ||
                        type == BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY ||
                        type == BluetoothDevice.PAIRING_VARIANT_DISPLAY_PIN) {
                    pairingKey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY,
                            BluetoothDevice.ERROR);
                }

                Intent dialogIntent = new Intent(context, BluetoothDialogService.class);
                dialogIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, pairingDevice);
                dialogIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, type);
                dialogIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_KEY, pairingKey);
                context.startService(dialogIntent);
                break;
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                BluetoothDevice bondDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (bondDevice == null) return;
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                        BluetoothDevice.ERROR);
                int preState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
                        BluetoothDevice.ERROR);
                Log.d(TAG, "bound state: " + bondState);
                if (bondState == BluetoothDevice.BOND_BONDING) return;
                if (bondState == BluetoothDevice.BOND_BONDED) {
                    ToastFactory.showToast(context, context.getString(R.string.str_others_bluetooth_bound_success)
                            , Toast.LENGTH_SHORT);
                } else if (bondState == BluetoothDevice.BOND_NONE) {
                    if (preState == BluetoothDevice.BOND_BONDING) {
                        ToastFactory.showToast(context, context.getString(R.string.str_others_bluetooth_pairing_pin_error_message)
                                , Toast.LENGTH_SHORT);
                    } else if (preState == BluetoothDevice.BOND_BONDED) {
                        ToastFactory.showToast(context, context.getString(R.string.str_others_bluetooth_unbound_success)
                                , Toast.LENGTH_SHORT);
                    }
                }
                Intent stopIntent = new Intent(context, BluetoothDialogService.class);
                context.stopService(stopIntent);
                break;
        }
    }
}
