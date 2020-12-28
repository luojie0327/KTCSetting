package com.ktc.setting.view.universal;

import android.content.Intent;
import android.provider.Settings;
import android.view.View;

import com.ktc.setting.R;
import com.ktc.setting.helper.VersionUtil;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.ButtonSettingView;
import com.ktc.setting.view.custom.SettingViewContainer;
import com.ktc.setting.view.custom.SwitchSettingView;
import com.ktc.setting.view.universal.datetime.DateTimeFragment;
import com.ktc.setting.view.universal.datetime.DateTimeTool;
import com.ktc.setting.view.universal.datetime.observe.TimeObserver;
import com.ktc.setting.view.universal.devicename.DeviceNameFragment;
import com.ktc.setting.view.universal.devicename.DeviceNameManager;
import com.ktc.setting.view.universal.inputmethod.InputMethodManager;
import com.ktc.setting.view.universal.language.LanguageFragment;
import com.ktc.setting.view.universal.language.LanguageTool;
import com.ktc.setting.view.universal.security.SecurityFragment;
import com.ktc.setting.view.universal.security.SecurityTool;
import com.ktc.setting.view.universal.storage.StorageFragment;

import java.util.List;
import java.util.Locale;

public class UniversalFragment extends BaseFragment implements ButtonSettingView.OnButtonClickListener
        , SwitchSettingView.OnSwitchListener, TimeObserver {

    private static final String TAG = UniversalFragment.class.getSimpleName();

    private ButtonSettingView deviceNameBtn;
    private ButtonSettingView timeBtn;
    private ButtonSettingView languageBtn;
    private ButtonSettingView securityBtn;
    private ButtonSettingView storageBtn;
    private SwitchSettingView inputmethodSwitch;
    private SwitchSettingView securitySwitch;
    private SettingViewContainer universalContainer;
    private DeviceNameManager mDeviceNameManager;
    private InputMethodManager mInputMethodManager;
    private SecurityManager mSecurityManager;
    private List<String> inputmethodList;

    @Override
    protected int getRes() {
        return R.layout.fragment_universal_main;
    }

    @Override
    protected int getTitle() {
        return R.string.title_universal;
    }

    @Override
    protected void initView(View view) {
        deviceNameBtn = (ButtonSettingView) view.findViewById(R.id.universal_device_name_btn);
        timeBtn = (ButtonSettingView) view.findViewById(R.id.universal_time_btn);
        languageBtn = (ButtonSettingView) view.findViewById(R.id.universal_language_btn);
        securityBtn = (ButtonSettingView) view.findViewById(R.id.universal_security_btn);
        storageBtn = (ButtonSettingView) view.findViewById(R.id.universal_storage_btn);
        securitySwitch = (SwitchSettingView) view.findViewById(R.id.universal_security_switch);
        inputmethodSwitch = (SwitchSettingView) view.findViewById(R.id.universal_inputmethod_switch);
        universalContainer = (SettingViewContainer) view.findViewById(R.id.universal_container);
        initSecurity();
    }

    @Override
    protected void setFocus() {
        View newFocus = null;
        String action = mActivity.getIntent().getAction();
        if (Settings.ACTION_SECURITY_SETTINGS.equals(action)
                && !VersionUtil.isCurrentAndroidOreoOrHigher()) {
            newFocus = securitySwitch;
        } else if (Settings.ACTION_INPUT_METHOD_SETTINGS.equals(action)) {
            newFocus = inputmethodSwitch;
        }

        switch (mActivity.preFocusViewIdFirst) {
            case R.id.universal_device_name_btn:
                newFocus = deviceNameBtn;
                break;
            case R.id.universal_time_btn:
                newFocus = timeBtn;
                break;
            case R.id.universal_language_btn:
                newFocus = languageBtn;
                break;
            case R.id.universal_security_btn:
                newFocus = securityBtn;
                break;
            case R.id.universal_storage_btn:
                newFocus = storageBtn;
                break;
        }

        if (newFocus != null) {
            newFocus.requestFocus();
            universalContainer.setNewFocus(newFocus);
        } else {
            universalContainer.setNewFocus(deviceNameBtn);
        }
    }

    @Override
    protected void initData() {
        ((UniversalActivity) getActivity()).mTimeObservation.addObserver(this);
        mActivity.preFocusViewIdSecond = 0;
        mDeviceNameManager = new DeviceNameManager(getContext().getApplicationContext());
        mInputMethodManager = new InputMethodManager(getContext().getApplicationContext());
        mSecurityManager = new SecurityManager();
        refreshUI();
    }

    private void initSecurity() {
        if (VersionUtil.isCurrentAndroidOreoOrHigher()) {
            securitySwitch.setVisibility(View.GONE);
            securityBtn.setVisibility(View.VISIBLE);
        } else {
            securitySwitch.setVisibility(View.VISIBLE);
            securityBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void addListener() {
        deviceNameBtn.setOnButtonClickListener(this);
        timeBtn.setOnButtonClickListener(this);
        languageBtn.setOnButtonClickListener(this);
        securityBtn.setOnButtonClickListener(this);
        storageBtn.setOnButtonClickListener(this);
        inputmethodSwitch.setOnSwitchListener(this);
        securitySwitch.setOnSwitchListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUI();
        mActivity.preFocusViewIdFirst = 0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((UniversalActivity) getActivity()).mTimeObservation.removeObserver(this);
    }

    protected void refreshUI() {
        inputmethodList = mInputMethodManager.getImeNameList();
        deviceNameBtn.setValue(mDeviceNameManager.getDeviceName());
        timeBtn.setValue(DateTimeTool.getCurrentDate());
        languageBtn.setValue(LanguageTool.getDisplayName(getContext().getApplicationContext(), Locale.getDefault()));
        inputmethodSwitch.setValueArray(inputmethodList.toArray(new String[inputmethodList.size()]));
        inputmethodSwitch.setIndex(mInputMethodManager.getCurrentImeIndex());
        securitySwitch.setValueArray(getResources().getStringArray(R.array.str_array_common_switch));
        securitySwitch.setIndex(SecurityTool.isNonMarketAppsAllowed(getContext().getApplicationContext()) ? 1 : 0);
    }

    @Override
    public void onClick(View view) {
        mActivity.preFocusViewIdFirst = view.getId();
        switch (view.getId()) {
            case R.id.universal_device_name_btn:
                mActivity.newFragment(new DeviceNameFragment());
                break;
            case R.id.universal_time_btn:
                mActivity.newFragment(new DateTimeFragment());
                break;
            case R.id.universal_language_btn:
                mActivity.newFragment(new LanguageFragment());
                break;
            case R.id.universal_security_btn:
                mActivity.newFragment(new SecurityFragment());
                break;
            case R.id.universal_storage_btn:
                mActivity.newFragment(new StorageFragment());
                break;
        }
    }

    @Override
    public void onSwitch(View view, int index) {
        switch (view.getId()) {
            case R.id.universal_inputmethod_switch:
                mInputMethodManager.setCurrentIme(inputmethodList.get(index));
                break;
            case R.id.universal_security_switch:
                SecurityTool.setNonMarketAppsAllowed(getContext(), index == 1);
                break;
        }
    }

    @Override
    public void update(Intent intent) {
        if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction())) {
            refreshUI();
        }
    }
}
