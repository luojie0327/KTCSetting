package com.ktc.setting.view.about;

import android.content.Intent;
import android.view.View;
import android.widget.ScrollView;

import com.ktc.debughelper.ui.acty.HomeActivity;
import com.ktc.setting.R;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.ButtonSettingView;
import com.ktc.setting.view.custom.SettingViewContainer;
import com.ktc.setting.view.universal.devicename.DeviceNameManager;

public class AboutFragment extends BaseFragment implements View.OnFocusChangeListener
        , ButtonSettingView.OnButtonClickListener {

    private ScrollView mScrollView;

    private SettingViewContainer aboutContainer;
    private ButtonSettingView deviceNameBtn;
    private ButtonSettingView modelNameBtn;
    private ButtonSettingView ddrInfoBtn;
    private ButtonSettingView androidVersionBtn;
    private ButtonSettingView systemVersionBtn;
    private ButtonSettingView updateTimeBtn;
    private ButtonSettingView ethernetMacBtn;
    private ButtonSettingView wifiMacBtn;
    private ButtonSettingView licenseBtn;
    private boolean isDown = false;
    private int mClickTime = 0;
    private ButtonSettingView otaVersionBtn;
    private LicenseManager mLicenseManager;

    @Override
    protected int getRes() {
        return R.layout.fragment_about;
    }

    @Override
    protected int getTitle() {
        return R.string.title_about;
    }

    @Override
    protected void initView(View view) {
        mScrollView = (ScrollView) view.findViewById(R.id.about_content);
        aboutContainer = (SettingViewContainer) view.findViewById(R.id.about_container);
        deviceNameBtn = (ButtonSettingView) view.findViewById(R.id.about_device_name_btn);
        modelNameBtn = (ButtonSettingView) view.findViewById(R.id.about_device_model_btn);
        ddrInfoBtn = (ButtonSettingView) view.findViewById(R.id.about_ddr_info_btn);
        androidVersionBtn = (ButtonSettingView) view.findViewById(R.id.about_android_version_btn);
        otaVersionBtn = (ButtonSettingView) view.findViewById(R.id.about_ota_version_btn);
        systemVersionBtn = (ButtonSettingView) view.findViewById(R.id.about_system_version_btn);
        updateTimeBtn = (ButtonSettingView) view.findViewById(R.id.about_update_time_btn);
        ethernetMacBtn = (ButtonSettingView) view.findViewById(R.id.about_ethernet_mac_btn);
        wifiMacBtn = (ButtonSettingView) view.findViewById(R.id.about_wifi_mac_btn);
        licenseBtn = (ButtonSettingView) view.findViewById(R.id.about_license_btn);
    }

    @Override
    protected void setFocus() {
        aboutContainer.setNewFocus(deviceNameBtn);
    }

    @Override
    protected void initData() {
        DeviceNameManager mDeviceNameManager = new DeviceNameManager(getContext());
        mLicenseManager = new LicenseManager(getContext());
        deviceNameBtn.setValue(mDeviceNameManager.getDeviceName());
        modelNameBtn.setValue(DeviceInfoTool.getMachineModel());
        ddrInfoBtn.setValue(DeviceInfoTool.getStorageAndMemory());
        androidVersionBtn.setValue(DeviceInfoTool.getAndroidVersion());
        otaVersionBtn.setValue(DeviceInfoTool.getSoftWareVersion());
        systemVersionBtn.setValue(DeviceInfoTool.getSoftWareVersion());
        updateTimeBtn.setValue(DeviceInfoTool.getSoftwareDate());
        ethernetMacBtn.setValue(DeviceInfoTool.getEthernetMacAddress());
        wifiMacBtn.setValue(DeviceInfoTool.getWirelessMacAddress(getContext()));
    }

    @Override
    protected void addListener() {
        ddrInfoBtn.setOnFocusChangeListener(this);
        ethernetMacBtn.setOnFocusChangeListener(this);
        licenseBtn.setOnButtonClickListener(this);

        deviceNameBtn.setOnButtonClickListener(new ButtonSettingView.OnButtonClickListener() {
            @Override
            public void onClick(View view) {
                mClickTime++;
                if (mClickTime >= 6) {
                    Intent intent = new Intent(mActivity, HomeActivity.class);
                    mActivity.startActivity(intent);
                    mClickTime = 0;
                }
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            if (v.getId() == R.id.about_ethernet_mac_btn && !isDown) {
                scrollToDown();
            } else if (v.getId() == R.id.about_ddr_info_btn && isDown) {
                scrollToUp();
            }
        }
    }

    private void scrollToUp() {
        mScrollView.smoothScrollTo(0, 0);
        isDown = false;
    }

    private void scrollToDown() {
        mScrollView.smoothScrollTo(0, mScrollView.getHeight());
        isDown = true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.about_license_btn:
                mLicenseManager.startLicense();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mClickTime = 0;
    }
}
