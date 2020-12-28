package com.ktc.setting.view.others.bluetooth;

import android.app.DialogFragment;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ktc.setting.R;
import com.ktc.setting.helper.DestinyUtil;
import com.ktc.setting.view.others.OthersActivity;
import com.ktc.setting.view.others.bluetooth.internal.BluetoothDevicePairer;

public class ConnectDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "ConnectDialogFragment";
    private Button cancel;
    private Button ok;
    private boolean isConnect = false;
    private BluetoothDevicePairer mPairer;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothA2dp mBluetoothA2dp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_fragment_bluetooth_connect, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
        addListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = DestinyUtil.dp2px(getContext(), 533);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }

    private void initView(View view) {
        isConnect = getArguments().getBoolean("isConnect");
        TextView connectTitle = (TextView) view.findViewById(R.id.dialog_connect_title);
        cancel = (Button) view.findViewById(R.id.dialog_connect_cancel_btn);
        ok = (Button) view.findViewById(R.id.dialog_connect_ok_btn);
        if (!isConnect) {
            connectTitle.setText(getResources().getString(R.string.str_others_bluetooth_connect_title));
            cancel.setText(getResources().getString(R.string.str_others_bluetooth_connect_cancel));
            ok.setText(getResources().getString(R.string.str_others_bluetooth_connect_ok));
        } else {
            connectTitle.setText(getResources().getString(R.string.str_others_bluetooth_disconnect_title));
            cancel.setText(getResources().getString(R.string.str_others_bluetooth_disconnect_cancel));
            ok.setText(getResources().getString(R.string.str_others_bluetooth_disconnect_ok));
        }
    }

    private void initData() {
        mBluetoothDevice = getArguments().getParcelable("device");
        mPairer = new BluetoothDevicePairer(getContext(), null);
        mBluetoothA2dp = ((OthersActivity) getActivity()).mBluetoothA2dp;
    }

    private void addListener() {
        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPairer != null) {
            mPairer.setListener(null);
            mPairer.dispose();
            mPairer = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_connect_cancel_btn:
                if (!isConnect) {
                    mPairer.unpairDevice(mBluetoothDevice);
                    dismiss();
                } else {
                    dismiss();
                }
                break;
            case R.id.dialog_connect_ok_btn:
                int deviceClass = mBluetoothDevice.getBluetoothClass().getMajorDeviceClass();
		Log.d(TAG,"dissconnect deviceclass =" + deviceClass);
                if (!isConnect) {
                    if (deviceClass == BluetoothClass.Device.Major.AUDIO_VIDEO) {
                        BluetoothConnectManager.openConnection(mBluetoothA2dp, mBluetoothDevice);
                    }
                    dismiss();
                } else {
                    if (deviceClass == BluetoothClass.Device.Major.AUDIO_VIDEO) {
			Log.d(TAG, "deviceClass = AUDIO_VIDEO");
                        if (mBluetoothA2dp != null) {
                            BluetoothConnectManager.closeConnection(mBluetoothA2dp, mBluetoothDevice);
                        }
                    }
                    if (deviceClass == BluetoothClass.Device.Major.PERIPHERAL){
                        if (mBluetoothA2dp != null) {
                            mPairer.unpairDevice(mBluetoothDevice);
                        }
                    }
                    dismiss();
                }
                break;
        }
    }
}
