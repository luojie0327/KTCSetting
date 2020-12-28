package com.ktc.setting.view.network.ethernet;

import android.view.View;

import com.ktc.setting.R;
import com.ktc.setting.helper.NetworkHelper;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.ButtonSettingView;
import com.ktc.setting.view.custom.SettingViewContainer;
import com.ktc.setting.view.custom.SwitchSettingView;

public class EthernetFragment extends BaseFragment {

    private NetworkHelper mNetworkHelper;

    private SettingViewContainer mContainer;
    private SwitchSettingView mSwitch;
    private ButtonSettingView mButton;

    @Override
    protected int getRes() {
        return R.layout.fragment_ethernet;
    }

    @Override
    protected int getTitle() {
        return R.string.title_ethernet;
    }

    @Override
    protected void initView(View view) {
        mContainer = (SettingViewContainer) view.findViewById(R.id.ethernet_container);
        mSwitch = (SwitchSettingView) view.findViewById(R.id.switch_ethernet_hdcp);
        mButton = (ButtonSettingView) view.findViewById(R.id.button_ethernet_manually);
    }

    @Override
    protected void setFocus() {
        View newFocus = null;
        switch (mActivity.preFocusViewIdSecond) {
            case R.id.button_ethernet_manually:
                newFocus = mButton;
                break;
            default:
                newFocus = mSwitch;
                break;
        }

        mContainer.setNewFocus(newFocus);
        newFocus.requestFocus();
        newFocus.requestFocusFromTouch();
    }

    @Override
    protected void initData() {
        mNetworkHelper = NetworkHelper.getInstance(mActivity);

        boolean isAuto = mNetworkHelper.isEthernetAutoIP();
        mSwitch.setIndex(isAuto ? 1 : 0);
        mButton.setEnabled(!isAuto);
        mButton.setValue(isAuto ? R.string.network_off : R.string.network_on);
    }

    @Override
    protected void addListener() {
        mSwitch.setOnSwitchListener(new SwitchSettingView.OnSwitchListener() {
            @Override
            public void onSwitch(View view, int index) {
                if (index == 0) {
                    mButton.setEnabled(true);
                    mButton.setValue(R.string.network_on);
                    mNetworkHelper.saveEthernetAsStatic(mNetworkHelper.getEthernetIp(),
                            mNetworkHelper.getEthernetSubnet(), mNetworkHelper.getEthernetGateway(),
                            mNetworkHelper.getEthernetDns1(), mNetworkHelper.getEthernetDns2());
                } else {
                    mButton.setEnabled(false);
                    mButton.setValue(R.string.network_off);
                    mNetworkHelper.saveEthernetAsHdcp();
                }
            }
        });

        mButton.setOnButtonClickListener(new ButtonSettingView.OnButtonClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.preFocusViewIdSecond = R.id.button_ethernet_manually;
                mActivity.newFragment(new EditEthernetFragment());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.preFocusViewIdSecond = 0;
    }
}
