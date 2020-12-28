package com.ktc.setting.view.network.ethernet;

import android.view.View;

import com.ktc.setting.R;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.ButtonSettingView;
import com.ktc.setting.view.custom.SettingViewContainer;

public class EditEthernetFragment extends BaseFragment {

    private SettingViewContainer mContainer;
    private ButtonSettingView mBtProxy;
    private ButtonSettingView mBtIp;

    @Override
    protected int getRes() {
        return R.layout.fragment_ethernet_edit;
    }

    @Override
    protected int getTitle() {
        return R.string.title_ethernet;
    }

    @Override
    protected void initView(View view) {
        mContainer = (SettingViewContainer) view.findViewById(R.id.edit_ethernet_container);
        mBtProxy = (ButtonSettingView) view.findViewById(R.id.button_ethernet_proxy);
        mBtIp = (ButtonSettingView) view.findViewById(R.id.button_ethernet_ip);
    }

    @Override
    protected void setFocus() {
        View newFocus = null;
        switch (mActivity.preFocusViewIdThird) {
            case R.id.button_ethernet_proxy:
                newFocus = mBtProxy;
                break;
            case R.id.button_ethernet_ip:
                newFocus = mBtIp;
                break;
            default:
                newFocus = mBtProxy;
                break;
        }

        mContainer.setNewFocus(newFocus);
        newFocus.requestFocus();
        newFocus.requestFocusFromTouch();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void addListener() {
        mBtProxy.setOnButtonClickListener(new ButtonSettingView.OnButtonClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.preFocusViewIdThird = R.id.button_ethernet_proxy;
                mActivity.newFragment(new EthernetProxyFragment());
            }
        });

        mBtIp.setOnButtonClickListener(new ButtonSettingView.OnButtonClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.preFocusViewIdThird = R.id.button_ethernet_ip;
                mActivity.newFragment(new EthernetIpFragment());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.preFocusViewIdThird = 0;
    }
}
