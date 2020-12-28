package com.ktc.setting.view.others.tts;

import android.content.Intent;
import android.view.View;

import com.ktc.setting.R;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.ButtonSettingView;
import com.ktc.setting.view.custom.SettingViewContainer;
import com.ktc.setting.view.custom.SwitchSettingView;
import com.ktc.setting.view.others.OthersActivity;

import java.util.Locale;

public class TTSFragment extends BaseFragment implements ButtonSettingView.OnButtonClickListener,
        SwitchSettingView.OnSwitchListener {

    private static final String TAG = TTSFragment.class.getSimpleName();
    private SettingViewContainer ttsContainer;
    private ButtonSettingView ttsLanguageBtn;
    private SwitchSettingView ttsSpeakSwitch;
    private TTSManager mTTSManager;

    @Override
    protected int getRes() {
        return R.layout.fragment_tts;
    }

    @Override
    protected int getTitle() {
        return R.string.str_others_tts_title;
    }

    @Override
    protected void initView(View view) {
        ttsContainer = (SettingViewContainer) view.findViewById(R.id.tts_container);
        ttsLanguageBtn = (ButtonSettingView) view.findViewById(R.id.tts_language_btn);
        ttsSpeakSwitch = (SwitchSettingView) view.findViewById(R.id.tts_speak_switch);
    }

    @Override
    protected void setFocus() {
        ttsContainer.setNewFocus(ttsLanguageBtn);
    }

    @Override
    protected void initData() {
        mTTSManager = ((OthersActivity) getActivity()).mTTSManager;
        ttsSpeakSwitch.setValueArray(getResources().getStringArray(R.array.str_array_common_switch));
        ttsSpeakSwitch.setIndex(TTSTool.isTTSEnable(getContext()) ? 0 : 1);
        refreshUI();
    }

    @Override
    protected void addListener() {
        ttsLanguageBtn.setOnButtonClickListener(this);
        ttsSpeakSwitch.setOnSwitchListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tts_language_btn:
                mActivity.newFragment(new TTSLanguageFragment());
                break;
        }
    }

    @Override
    public void onSwitch(View view, int index) {
        switch (view.getId()) {
            case R.id.tts_speak_switch:
                TTSTool.setTTSStatus(getContext(), index == 0);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mTTSManager.onActivityResult(requestCode, resultCode, data);
    }

    public void refreshUI() {
        Locale currentLocale = mTTSManager.getCurrentTTSLanguage();
        if (currentLocale != null) {
            ttsLanguageBtn.setValue(currentLocale.getDisplayName());
        } else {
            ttsLanguageBtn.setValue("");
        }
    }

}
