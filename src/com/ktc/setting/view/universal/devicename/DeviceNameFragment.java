package com.ktc.setting.view.universal.devicename;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ktc.setting.R;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.ToastFactory;


public class DeviceNameFragment extends BaseFragment
        implements View.OnClickListener, OnDeviceNameChangeListener {

    private static final String TAG = DeviceNameFragment.class.getSimpleName();
    private EditText nameEditText;
    private Button saveBtn;
    private DeviceNameManager mDeviceNameManager;

    @Override
    protected int getRes() {
        return R.layout.fragment_device_name_edit;
    }

    @Override
    protected int getTitle() {
        return R.string.str_universal_device_name_title;
    }

    @Override
    protected void initView(View view) {
        nameEditText = (EditText) view.findViewById(R.id.universal_device_name_edit_text);
        saveBtn = (Button) view.findViewById(R.id.universal_device_name_save_btn);
    }

    @Override
    protected void setFocus() {
        nameEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                nameEditText.requestFocus();
            }
        },300);
        nameEditText.setSelection(nameEditText.getText().toString().length());
    }

    @Override
    protected void initData() {
        mDeviceNameManager = new DeviceNameManager(getContext().getApplicationContext());
        mDeviceNameManager.setOnDeviceNameChangeListener(this);
        nameEditText.setText(mDeviceNameManager.getDeviceName());
    }

    @Override
    protected void addListener() {
        saveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.universal_device_name_save_btn:
                String deviceName = nameEditText.getText().toString();
                if (TextUtils.isEmpty(deviceName)) {
                    ToastFactory.showToast(mActivity, mActivity.getString(R.string.str_universal_device_name_empty)
                            , Toast.LENGTH_SHORT);
                } else {
                    mDeviceNameManager.setDeviceName(deviceName);
                }
                break;
        }
    }

    @Override
    public void onSuccess() {
        ToastFactory.showToast(mActivity, mActivity.getString(R.string.str_universal_device_name_save_success)
                , Toast.LENGTH_SHORT);
    }

    @Override
    public void onFailed() {
        ToastFactory.showToast(mActivity, mActivity.getString(R.string.str_universal_device_name_save_failed)
                , Toast.LENGTH_SHORT);
    }
}
