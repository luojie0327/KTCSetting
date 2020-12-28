package com.ktc.setting.view.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.Toast;

import com.ktc.setting.R;
import com.ktc.setting.helper.NetworkHelper;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.ButtonSettingView;
import com.ktc.setting.view.custom.SettingViewContainer;
import com.ktc.setting.view.custom.ToastFactory;
import com.ktc.setting.view.network.ethernet.EthernetFragment;
import com.ktc.setting.view.network.wifi.WifiFragment;

public class NetworkFragment extends BaseFragment implements ButtonSettingView.OnButtonClickListener {

    private NetworkHelper mNetworkHelper;

    private SettingViewContainer mContainer;
    private ButtonSettingView mWifiButton;
    private ButtonSettingView mEthernetButton;
    private ButtonSettingView mHotpotButton;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshUI();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
        mActivity.registerReceiver(receiver, filter);

        mActivity.preFocusViewIdFirst = 0;
    }

    @Override
    protected int getRes() {
        return R.layout.fragment_network;
    }

    @Override
    protected int getTitle() {
        return R.string.title_network;
    }

    @Override
    public void onPause() {
        super.onPause();
        mActivity.unregisterReceiver(receiver);
    }

    @Override
    protected void initView(View view) {
        mContainer = (SettingViewContainer) view.findViewById(R.id.container_network);
        mWifiButton = (ButtonSettingView) view.findViewById(R.id.button_wifi);
        mEthernetButton = (ButtonSettingView) view.findViewById(R.id.button_ethernet);
        mHotpotButton = (ButtonSettingView) view.findViewById(R.id.button_hotpot);
    }

    @Override
    protected void setFocus() {
        View newFocus = null;
        switch (mActivity.preFocusViewIdFirst) {
            case R.id.button_wifi:
                newFocus = mWifiButton;
                break;
            case R.id.button_ethernet:
                newFocus = mEthernetButton;
                break;
            case R.id.button_hotpot:
                newFocus = mHotpotButton;
                break;
            default:
                newFocus = mWifiButton;
                break;
        }

        mContainer.setNewFocus(newFocus);
        newFocus.requestFocus();
        newFocus.requestFocusFromTouch();
    }

    @Override
    protected void initData() {
        mNetworkHelper = NetworkHelper.getInstance(getActivity());
        refreshUI();
    }

    private void refreshUI() {
        if (mNetworkHelper.isWifiConnected()) {
            mWifiButton.setValue(R.string.network_connected);
        } else if (mNetworkHelper.isWifiOpen()) {
            mWifiButton.setValue(R.string.network_not_connected);
        } else {
            mWifiButton.setValue(R.string.network_off);
        }

        if (mNetworkHelper.isEthernetConnected()) {
            mEthernetButton.setValue(R.string.network_connected);
        } else if (mNetworkHelper.isEthernetAvailable()) {
            mEthernetButton.setValue(R.string.network_not_connected);
        } else {
            mEthernetButton.setValue(R.string.network_off);
        }

        if (mNetworkHelper.isHotpotOpen()) {
            mHotpotButton.setValue(R.string.network_on);
        } else {
            mHotpotButton.setValue(R.string.network_off);
        }
    }

    @Override
    protected void addListener() {
        mWifiButton.setOnButtonClickListener(this);
        mEthernetButton.setOnButtonClickListener(this);
        mHotpotButton.setOnButtonClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mActivity.preFocusViewIdFirst = view.getId();
        switch (view.getId()) {
            case R.id.button_wifi:
                mActivity.newFragment(new WifiFragment());
                break;
            case R.id.button_ethernet:
                if (mNetworkHelper.isEthernetAvailable()) {
                    mActivity.newFragment(new EthernetFragment());
                } else {
                    ToastFactory.showToast(mActivity, mActivity.getString(R.string.network_ethernet_not_available)
                            , Toast.LENGTH_SHORT);
                }
                break;
            case R.id.button_hotpot:
                mActivity.newFragment(new HotpotFragment());
                break;
        }
    }
}
