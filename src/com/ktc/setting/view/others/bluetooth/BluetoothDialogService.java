package com.ktc.setting.view.others.bluetooth;

import android.app.Dialog;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ktc.setting.R;

import java.util.Locale;


public class BluetoothDialogService extends Service implements View.OnClickListener {

    private static final String TAG = BluetoothDialogService.class.getSimpleName();
    private int mType;
    private BluetoothDevice mDevice;
    private String mPairingKey;
    private Dialog mPairDialog;

    public BluetoothDialogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "start service");
        registerReceiver();
        mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        mType = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);
        int pKey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY, BluetoothDevice.ERROR);

        switch (mType) {
            case BluetoothDevice.PAIRING_VARIANT_PIN:
            case BluetoothDevice.PAIRING_VARIANT_PIN_16_DIGITS:
            case BluetoothDevice.PAIRING_VARIANT_PASSKEY:
                break;
            case BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION:
                if (pKey == BluetoothDevice.ERROR) {
                    Log.e(TAG, "Invalid Confirmation Passkey received, not showing any dialog");
                    return super.onStartCommand(intent, flags, startId);
                }
                mPairingKey = String.format(Locale.US, "%06d", pKey);
                break;
            case BluetoothDevice.PAIRING_VARIANT_CONSENT:
            case BluetoothDevice.PAIRING_VARIANT_OOB_CONSENT:
                break;
            case BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY:
            case BluetoothDevice.PAIRING_VARIANT_DISPLAY_PIN:
                if (BluetoothDevice.ERROR == pKey) {
                    Log.e(TAG, "Invalid Confirmation Passkey or PIN received, not showing any dialog");
                    return super.onStartCommand(intent, flags, startId);
                }
                if (mType == BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY) {
                    mPairingKey = String.format("%06d", pKey);
                } else {
                    mPairingKey = String.format("%04d", pKey);
                }
                if (mType == BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY) {
                    mDevice.setPairingConfirmation(true);
                } else if (mType == BluetoothDevice.PAIRING_VARIANT_DISPLAY_PIN) {
                    byte[] pinBytes = BluetoothDevice.convertPinToBytes(mPairingKey);
                    mDevice.setPin(pinBytes);
                }
                break;
            default:
                Log.e(TAG, "Incorrect pairing type received, not showing any dialog");
        }
        startPairingDialog(mDevice, mPairingKey, mType);
        return super.onStartCommand(intent, flags, startId);
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_CANCEL);
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        registerReceiver(receiver, intentFilter);
    }

    private void startPairingDialog(BluetoothDevice device, String pairingKey, int type) {
        View parent = LayoutInflater.from(this).inflate(
                R.layout.dialog_pair, null);
        mPairDialog = new Dialog(getBaseContext());
        mPairDialog.setContentView(parent);
        mPairDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPairDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        TextView title = (TextView) parent.findViewById(R.id.dialog_pair_title);
        TextView content = (TextView) parent.findViewById(R.id.dialog_pair_content);
        Button okBtn = (Button) parent.findViewById(R.id.dialog_pair_ok_btn);
        Button cancelBtn = (Button) parent.findViewById(R.id.dialog_pair_cancel_btn);
        title.setText(String.format(getString(R.string.str_others_bluetooth_pair_title), device.getName()));
        content.setText(String.format(getString(R.string.str_others_bluetooth_pair_content), pairingKey));
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        mPairDialog.show();
        WindowManager.LayoutParams lp = mPairDialog.getWindow().getAttributes();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mPairDialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "stop service");
        super.onDestroy();
        cancelDialog();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;
            switch (intent.getAction()) {
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                            BluetoothDevice.ERROR);
                    if (bondState == BluetoothDevice.BOND_BONDED ||
                            bondState == BluetoothDevice.BOND_NONE) {
                        cancelDialog();
                    }
                    break;
                case BluetoothDevice.ACTION_PAIRING_CANCEL:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device == null || device.equals(mDevice)) {
                        cancelDialog();
                    }
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_pair_ok_btn:
                onPair(mPairingKey);
                cancelDialog();
                break;
            case R.id.dialog_pair_cancel_btn:
                onCancel();
                cancelDialog();
                break;
        }
    }

    private void cancelDialog() {
        if (mPairDialog != null && mPairDialog.isShowing()) {
            mPairDialog.dismiss();
        }
    }

    private void onPair(String value) {
        switch (mType) {
            case BluetoothDevice.PAIRING_VARIANT_PIN:
            case BluetoothDevice.PAIRING_VARIANT_PIN_16_DIGITS:
                byte[] pinBytes = BluetoothDevice.convertPinToBytes(value);
                if (pinBytes == null) {
                    return;
                }
                mDevice.setPin(pinBytes);
                break;
            case BluetoothDevice.PAIRING_VARIANT_PASSKEY:
                int passkey = Integer.parseInt(value);
                mDevice.setPasskey(passkey);
                break;
            case BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION:
            case BluetoothDevice.PAIRING_VARIANT_CONSENT:
                mDevice.setPairingConfirmation(true);
                break;
            case BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY:
            case BluetoothDevice.PAIRING_VARIANT_DISPLAY_PIN:
                // Do nothing.
                break;
            case BluetoothDevice.PAIRING_VARIANT_OOB_CONSENT:
                mDevice.setRemoteOutOfBandData();
                break;
            default:
                Log.e(TAG, "Incorrect pairing type received");
        }
    }

    private void onCancel() {
        mDevice.cancelPairingUserInput();
    }
}
