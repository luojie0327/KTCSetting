package com.ktc.setting.view.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ktc.setting.R;
import com.ktc.setting.helper.NetworkHelper;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.LittleSwitchView;
import com.ktc.setting.view.custom.TitleEditView;
import com.ktc.setting.view.custom.ToastFactory;

public class HotpotFragment extends BaseFragment {

    private NetworkHelper mNetworkHelper;
    private HotpotBroadcastReceiver mReceiver;

    private LittleSwitchView mHotpotSwitch;
    private RelativeLayout mNameContainer;
    private EditText mNameInput;
    private LittleSwitchView mSecuritySwitch;
    private TitleEditView mPasswordInput;
    private Button mSaveButton;
    private ProgressBar mProgressBar;

    @Override
    protected int getRes() {
        return R.layout.fragment_hotpot;
    }

    @Override
    protected int getTitle() {
        return R.string.title_hotpot;
    }

    @Override
    protected void initView(View view) {
        mHotpotSwitch = (LittleSwitchView) view.findViewById(R.id.switch_hotpot);
        mNameContainer = (RelativeLayout) view.findViewById(R.id.hotpot_name_container);
        mNameInput = (EditText) view.findViewById(R.id.et_hotpot_name);
        mSecuritySwitch = (LittleSwitchView) view.findViewById(R.id.switch_hotpot_security);
        mPasswordInput = (TitleEditView) view.findViewById(R.id.input_hotpot_pwd);
        mSaveButton = (Button) view.findViewById(R.id.button_save);
        mProgressBar = (ProgressBar) view.findViewById(R.id.hotpot_progress_bar);
    }

    @Override
    protected void setFocus() {
        mHotpotSwitch.requestFocus();
        mHotpotSwitch.requestFocusFromTouch();
    }

    @Override
    protected void initData() {
        mNetworkHelper = NetworkHelper.getInstance(mActivity);
        mReceiver = new HotpotBroadcastReceiver();

        if (mNetworkHelper.isHotpotOpen()) {
            mHotpotSwitch.setIndex(1);
            setEnable(true);
            mNameInput.setText(mNetworkHelper.getHotpotName());
            int security = mNetworkHelper.getHotpotSecurity();
            if (security == NetworkHelper.SECURITY_NONE) {
                mSecuritySwitch.setIndex(0);
                mPasswordInput.setEnabled(false);
            } else if (security == NetworkHelper.SECURITY_PSK) {
                mSecuritySwitch.setIndex(1);
                mPasswordInput.setEnabled(true);
                mPasswordInput.setValue(mNetworkHelper.getHotpotPassword());
            }
        } else {
            mHotpotSwitch.setIndex(0);
            setEnable(false);
            mNameInput.setText(mNetworkHelper.getHotpotName());
            mSecuritySwitch.setIndex(0);
            mPasswordInput.setEnabled(false);
        }
    }

    @Override
    protected void addListener() {
        mHotpotSwitch.setOnSwitchListener(new LittleSwitchView.OnSwitchListener() {
            @Override
            public void onSwitch(View view, int index) {
                if (index == 0) {
                    mNetworkHelper.setHotpotEnable(false);
                    mHotpotSwitch.setEnabled(false);
                    setEnable(false);
                    mPasswordInput.setEnabled(false);
                    mProgressBar.setVisibility(View.VISIBLE);
                } else if (index == 1) {
                    mNetworkHelper.setHotpotEnable(true);
                    mHotpotSwitch.setEnabled(false);
                    setEnable(false);
                    mPasswordInput.setEnabled(false);
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        mSecuritySwitch.setOnSwitchListener(new LittleSwitchView.OnSwitchListener() {
            @Override
            public void onSwitch(View view, int index) {
                mPasswordInput.setEnabled(index == 1);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = mNameInput.getText().toString().trim();
                final String password = mPasswordInput.getValue().trim();
                final int security = mSecuritySwitch.getIndex();
                if (name.length() > 0
                        && (security == 0 || (security == 1 && password.length() >= 8))
                        && password.length() < 64) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (security == 1) {
                                mNetworkHelper.setHotpotConfig(name, NetworkHelper.SECURITY_PSK, password);
                            } else {
                                mNetworkHelper.setHotpotConfig(name, NetworkHelper.SECURITY_NONE, null);
                            }
                        }
                    }).start();
                    mHotpotSwitch.setEnabled(false);
                    setEnable(false);
                    mPasswordInput.setEnabled(false);
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    ToastFactory.showToast(mActivity, mActivity.getString(R.string.network_invalid_input)
                            , Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
        mActivity.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mActivity.unregisterReceiver(mReceiver);
    }

    private void updateState(int state) {
        switch (state) {
            case WifiManager.WIFI_AP_STATE_DISABLING:
            case WifiManager.WIFI_AP_STATE_ENABLING:
                mHotpotSwitch.setEnabled(false);
                setEnable(false);
                mPasswordInput.setEnabled(false);
                mProgressBar.setVisibility(View.VISIBLE);
                break;

            case WifiManager.WIFI_AP_STATE_DISABLED:
                mProgressBar.setVisibility(View.GONE);
                mHotpotSwitch.setEnabled(true);
                mHotpotSwitch.requestFocus();
                mHotpotSwitch.setIndex(0);
                setEnable(false);
                mNameInput.setText(mNetworkHelper.getHotpotName());
                mPasswordInput.setEnabled(false);
                break;

            case WifiManager.WIFI_AP_STATE_ENABLED:
                mProgressBar.setVisibility(View.GONE);
                mHotpotSwitch.setEnabled(true);
                mHotpotSwitch.requestFocus();
                mHotpotSwitch.setIndex(1);
                setEnable(true);
                mNameInput.setText(mNetworkHelper.getHotpotName());
                int security = mNetworkHelper.getHotpotSecurity();
                if (security == NetworkHelper.SECURITY_NONE) {
                    mSecuritySwitch.setIndex(0);
                    mPasswordInput.setEnabled(false);
                } else if (security == NetworkHelper.SECURITY_PSK) {
                    mSecuritySwitch.setIndex(1);
                    mPasswordInput.setEnabled(true);
                    mPasswordInput.setValue(mNetworkHelper.getHotpotPassword());
                }
                break;

            default:
                mProgressBar.setVisibility(View.GONE);
                break;
        }
    }

    private void setEnable(boolean enable) {
        mSecuritySwitch.setEnabled(enable);
        mNameContainer.setAlpha(enable ? (float) 1.0 : (float) 0.5);
        mNameInput.setFocusable(enable);
        mNameInput.setFocusableInTouchMode(enable);
        mSaveButton.setFocusableInTouchMode(enable);
        mSaveButton.setFocusable(enable);
        mSaveButton.setClickable(enable);
        mSaveButton.setAlpha(enable ? (float) 1.0 : (float) 0.5);
    }

    private class HotpotBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_AP_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_AP_STATE, WifiManager.WIFI_AP_STATE_FAILED);
                updateState(state);
            }
        }
    }
}
