package com.ktc.setting.view.restore;

import android.app.FragmentManager;
import android.content.Intent;
import android.view.View;

import com.ktc.setting.R;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.ButtonSettingView;
import com.ktc.setting.view.custom.SettingViewContainer;

public class UpdateFragment extends BaseFragment implements ButtonSettingView.OnButtonClickListener {

    private SettingViewContainer updateContainer;
    private ButtonSettingView systemRestoreBtn;
    private ButtonSettingView networkUpgradeBtn;
    private ButtonSettingView localeUpgradeBtn;
    private boolean isCurrentSystemLock = false;
	 private boolean isHotelResetLock = false;
	private static final String OTA_ACTION = "android.intent.action.KTC_OTA_NETWORK";
    private static final String LOCAL_UPDATE_ACTION = "android.intent.action.KTC_OTA_LOCAL";
	
    @Override
    protected int getRes() {
        return R.layout.fragment_update;
    }

    @Override
    protected int getTitle() {
        return R.string.title_restore;
    }

    @Override
    protected void initView(View view) {
        updateContainer = (SettingViewContainer) view.findViewById(R.id.update_container);
        systemRestoreBtn = (ButtonSettingView) view.findViewById(R.id.update_system_restore_btn);
        networkUpgradeBtn = (ButtonSettingView) view.findViewById(R.id.update_network_upgrade_btn);
        localeUpgradeBtn = (ButtonSettingView) view.findViewById(R.id.update_locale_upgrade_btn);
    }

    @Override
    protected void setFocus() {
        updateContainer.setNewFocus(systemRestoreBtn);
    }

    @Override
    protected void initData() {
        isCurrentSystemLock = PasswordManager.getInstance(getContext()).isSystemLock();
		isHotelResetLock = PasswordManager.getInstance(getContext()).isHotelResetLockOn();
    }

    @Override
    protected void addListener() {
        systemRestoreBtn.setOnButtonClickListener(this);
        networkUpgradeBtn.setOnButtonClickListener(this);
        localeUpgradeBtn.setOnButtonClickListener(this);
    }

    @Override
    public void onClick(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        switch (view.getId()) {
            case R.id.update_system_restore_btn:
                if (isCurrentSystemLock || isHotelResetLock) {
                    new PasswordDialogFragment().show(fragmentManager, "password_fragment");
                } else {
                    new RestoreDialogFragment().show(fragmentManager, "restore_fragment");
                }
                break;
            case R.id.update_network_upgrade_btn:
				Intent otaIntent = new Intent();
				otaIntent.setAction(OTA_ACTION);
				getActivity().startActivity(otaIntent);
                break;
            case R.id.update_locale_upgrade_btn:
                Intent localeIntent = new Intent();
                localeIntent.setAction(LOCAL_UPDATE_ACTION);
                getActivity().startActivity(localeIntent);
                break;
        }
    }
}
