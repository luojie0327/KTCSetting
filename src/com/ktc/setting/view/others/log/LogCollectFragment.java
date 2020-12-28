package com.ktc.setting.view.others.log;

import android.view.View;

import com.ktc.setting.R;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.ButtonSettingView;
import com.ktc.setting.view.custom.SettingViewContainer;

public class LogCollectFragment extends BaseFragment implements ButtonSettingView.OnButtonClickListener {

    private SettingViewContainer logContainer;
    private ButtonSettingView uploadBtn;
    private ButtonSettingView localeCollectBtn;

    @Override
    protected int getRes() {
        return R.layout.fragment_log_collect;
    }

    @Override
    protected int getTitle() {
        return R.string.str_others_log_title;
    }

    @Override
    protected void initView(View view) {
        logContainer = (SettingViewContainer) view.findViewById(R.id.log_container);
        uploadBtn = (ButtonSettingView) view.findViewById(R.id.log_upload_btn);
        localeCollectBtn = (ButtonSettingView) view.findViewById(R.id.log_locale_collect_btn);
    }

    @Override
    protected void setFocus() {
        logContainer.setNewFocus(uploadBtn);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void addListener() {
        uploadBtn.setOnButtonClickListener(this);
        localeCollectBtn.setOnButtonClickListener(this);
    }

    //TODO for log
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.log_upload_btn:
                break;
            case R.id.log_locale_collect_btn:
                break;
        }
    }
}
