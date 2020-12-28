package com.ktc.setting.view.others;

import android.app.FragmentManager;
import android.view.View;
import android.os.SystemProperties;

import com.ktc.setting.R;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.ButtonSettingView;
import com.ktc.setting.view.custom.SettingViewContainer;
import com.ktc.setting.view.custom.SwitchSettingView;
import com.ktc.setting.view.others.account.AccountFragment;
import com.ktc.setting.view.others.bluetooth.BluetoothFragment;
import com.ktc.setting.view.others.log.LogCollectFragment;
import com.ktc.setting.view.others.reboot.RebootDialogFragment;
import com.ktc.setting.view.others.sound.SystemSoundTool;
import com.ktc.setting.view.others.tts.TTSFragment;
import com.ktc.setting.view.others.tts.TTSTool;

public class OthersFragment extends BaseFragment implements ButtonSettingView.OnButtonClickListener,
        SwitchSettingView.OnSwitchListener, View.OnClickListener {

    private SettingViewContainer othersContainer;
    private ButtonSettingView bluetoothBtn;
    private ButtonSettingView ttsBtn;
    private ButtonSettingView accountBtn;
    private SwitchSettingView systemSoundSwitch;
    private ButtonSettingView logBtn;
    private ButtonSettingView rebootBtn;
    private String[] switchArray;

    @Override
    protected int getRes() {
        return R.layout.fragment_others;
    }

    @Override
    protected int getTitle() {
        return R.string.title_other;
    }

    @Override
    protected void initView(View view) {
        othersContainer = (SettingViewContainer) view.findViewById(R.id.others_container);
        bluetoothBtn = (ButtonSettingView) view.findViewById(R.id.others_bluetooth_btn);
        ttsBtn = (ButtonSettingView) view.findViewById(R.id.others_tts_btn);
        accountBtn = (ButtonSettingView) view.findViewById(R.id.others_account_btn);
        systemSoundSwitch = (SwitchSettingView) view.findViewById(R.id.others_system_sound_switch);
        logBtn = (ButtonSettingView) view.findViewById(R.id.others_log_btn);
        rebootBtn = (ButtonSettingView) view.findViewById(R.id.others_reboot_btn);
    }

    @Override
    protected void setFocus() {
        View newFocus = null;
        switch (mActivity.preFocusViewIdFirst) {
            case R.id.others_bluetooth_btn:
                newFocus = bluetoothBtn;
                break;
            case R.id.others_tts_btn:
                newFocus = ttsBtn;
                break;
            case R.id.others_account_btn:
                newFocus = accountBtn;
                break;
            case R.id.others_log_btn:
                newFocus = logBtn;
                break;
        }
        if (newFocus != null) {
            newFocus.requestFocus();
            othersContainer.setNewFocus(newFocus);
        } else {
            if("false".equals(SystemProperties.get("persist.sys.bluetooth"))){
                othersContainer.setNewFocus(accountBtn);
            }else{
                othersContainer.setNewFocus(bluetoothBtn);
            }
        }
    }

    @Override
    protected void initData() {
        switchArray = getResources().getStringArray(R.array.str_array_common_switch);
        systemSoundSwitch.setValueArray(switchArray);
        systemSoundSwitch.setIndex(SystemSoundTool.getSoundEffectsEnabled(getContext().getApplicationContext())
                ? 0 : 1);
        //TODO for tts button
        ttsBtn.setVisibility(View.GONE);
        ttsBtn.setValue(TTSTool.isTTSEnable(getContext()) ? switchArray[0] : switchArray[1]);

	bluetoothBtn.setVisibility("false".equals(SystemProperties.get("persist.sys.bluetooth")) ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void addListener() {
        bluetoothBtn.setOnButtonClickListener(this);
        accountBtn.setOnButtonClickListener(this);
        logBtn.setOnButtonClickListener(this);
        rebootBtn.setOnButtonClickListener(this);
        ttsBtn.setOnButtonClickListener(this);
        systemSoundSwitch.setOnSwitchListener(this);
    }

    @Override
    public void onClick(View view) {
        mActivity.preFocusViewIdFirst = view.getId();
        switch (view.getId()) {
            case R.id.others_bluetooth_btn:
                mActivity.newFragment(new BluetoothFragment());
                break;
            case R.id.others_tts_btn:
                mActivity.newFragment(new TTSFragment());
                break;
            case R.id.others_account_btn:
                mActivity.newFragment(new AccountFragment());
                break;
            case R.id.others_log_btn:
                mActivity.newFragment(new LogCollectFragment());
                break;
            case R.id.others_reboot_btn:
                FragmentManager fragmentManager = getFragmentManager();
                new RebootDialogFragment().show(fragmentManager, "reboot_fragment");
                break;
        }
    }

    @Override
    public void onSwitch(View view, int index) {
        switch (view.getId()) {
            case R.id.others_system_sound_switch:
                SystemSoundTool.setSoundEffectsEnabled(getContext().getApplicationContext(),
                        index == 0);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.preFocusViewIdFirst = 0;
    }
}
