package com.ktc.setting.view.network.wifi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ktc.setting.R;
import com.ktc.setting.helper.NetworkHelper;
import com.ktc.setting.model.WifiItem;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.IpInputView;
import com.ktc.setting.view.custom.SettingViewContainer;
import com.ktc.setting.view.custom.SwitchSettingView;
import com.ktc.setting.view.custom.ToastFactory;

public class EditWifiFragment extends BaseFragment {

    private static final String ARG_ITEM = "item_arg";
    private NetworkHelper mNetworkHelper;
    private WifiItem mItem;
    private boolean mClickConnect = false;

    private TextView mTitle;
    private SwitchSettingView mHdcpSwitch;
    private SettingViewContainer mContainer;
    private IpInputView mIpInput;
    private IpInputView mSubnetInput;
    private IpInputView mGatewayInput;
    private IpInputView mDns1Input;
    private IpInputView mDns2Input;
    private Button mConnectBt;
    private Button mDisconnectBt;
    private Button mForgetBt;

    public static EditWifiFragment newInstance(WifiItem item) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_ITEM, item);
        EditWifiFragment fragment = new EditWifiFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mItem = bundle.getParcelable(ARG_ITEM);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getTargetFragment() != null) {
            Intent i = new Intent();
            i.putExtra(WifiFragment.EXTRA_CONNECT, mClickConnect);
            i.putExtra(WifiFragment.EXTRA_ITEM, mClickConnect ? mItem : null);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        }
    }

    @Override
    protected int getRes() {
        return R.layout.fragment_wifi_edit;
    }

    @Override
    protected int getTitle() {
        return R.string.title_wifi;
    }

    @Override
    protected void initView(View view) {
        mTitle = (TextView) view.findViewById(R.id.edit_ethernet_title);
        mHdcpSwitch = (SwitchSettingView) view.findViewById(R.id.edit_wifi_switch);
        mContainer = (SettingViewContainer) view.findViewById(R.id.edit_container);
        mIpInput = (IpInputView) view.findViewById(R.id.edit_ip);
        mSubnetInput = (IpInputView) view.findViewById(R.id.edit_subnet);
        mGatewayInput = (IpInputView) view.findViewById(R.id.edit_gateway);
        mDns1Input = (IpInputView) view.findViewById(R.id.edit_dns1);
        mDns2Input = (IpInputView) view.findViewById(R.id.edit_dns2);
        mConnectBt = (Button) view.findViewById(R.id.button_connect);
        mDisconnectBt = (Button) view.findViewById(R.id.button_disconnect);
        mForgetBt = (Button) view.findViewById(R.id.button_forget);
    }

    @Override
    protected void setFocus() {
        mContainer.setNewFocus(mConnectBt);
        mConnectBt.requestFocus();
        mConnectBt.requestFocusFromTouch();
    }

    @Override
    protected void initData() {
        mNetworkHelper = NetworkHelper.getInstance(getActivity());

        mTitle.setText(mItem.getSSID());

        boolean isStatic = mNetworkHelper.isWifiStaticIP();
        mHdcpSwitch.setIndex(isStatic ? 1 : 0);
        mIpInput.setAddress(mNetworkHelper.getWifiIp());
        mSubnetInput.setAddress(mNetworkHelper.getWifiSubnetMask());
        mGatewayInput.setAddress(mNetworkHelper.getWifiGateWay());
        mDns1Input.setAddress(mNetworkHelper.getWifiDns1());
        mDns2Input.setAddress(mNetworkHelper.getWifiDns2());

        if (!isStatic) {
            mIpInput.setEnabled(false);
            mSubnetInput.setEnabled(false);
            mGatewayInput.setEnabled(false);
            mDns1Input.setEnabled(false);
            mDns2Input.setEnabled(false);
        }
    }

    @Override
    protected void addListener() {
        mHdcpSwitch.setOnSwitchListener(new SwitchSettingView.OnSwitchListener() {
            @Override
            public void onSwitch(View view, int index) {
                if (index == 0) {
                    mIpInput.setEnabled(false);
                    mSubnetInput.setEnabled(false);
                    mGatewayInput.setEnabled(false);
                    mDns1Input.setEnabled(false);
                    mDns2Input.setEnabled(false);
                } else {
                    mIpInput.setEnabled(true);
                    mSubnetInput.setEnabled(true);
                    mGatewayInput.setEnabled(true);
                    mDns1Input.setEnabled(true);
                    mDns2Input.setEnabled(true);
                }
            }
        });

        mConnectBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean needStatic = mHdcpSwitch.getIndex() == 1;
                if (isSame()) {
                    mClickConnect = false;
                } else {
                    if (!needStatic) {
                        mNetworkHelper.saveWifiAsHdcp();
                    } else {
                        String ip = mIpInput.getAddress();
                        String subnet = mSubnetInput.getAddress();
                        String gateway = mGatewayInput.getAddress();
                        String dns1 = mDns1Input.getAddress();
                        String dns2 = mDns2Input.getAddress();

                        if (NetworkHelper.matchIP(ip) && NetworkHelper.matchIP(subnet)
                                && NetworkHelper.matchIP(gateway) && NetworkHelper.matchIP(dns1)) {
                            mNetworkHelper.saveWifiAsStatic(ip, subnet, gateway, dns1, dns2);
                        } else {
                            ToastFactory.showToast(mActivity, mActivity.getString(R.string.network_invalid_input)
                                    , Toast.LENGTH_SHORT);
                            return;
                        }
                    }
                    mItem.setConnectedState(WifiItem.STATE_CONNECTING);
                    mItem.setSaved(true);
                    mClickConnect = true;
                }
                mActivity.getFragmentManager().popBackStack();
            }
        });

        mDisconnectBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNetworkHelper.disconnectWifi();
                mClickConnect = true;
                mActivity.getFragmentManager().popBackStack();
            }
        });

        mForgetBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNetworkHelper.forgetWifi(mItem.getSSID(), mItem.getSecurity());
                mClickConnect = true;
                mActivity.getFragmentManager().popBackStack();
            }
        });
    }

    private boolean isSame() {
        boolean needStatic = mHdcpSwitch.getIndex() == 1;
        boolean isStatic = mNetworkHelper.isWifiStaticIP();
        if (needStatic != isStatic) {
            return false;
        }

        if (!isStatic) {
            return true;
        }

        return isIpSame();
    }

    private boolean isIpSame() {
        return mIpInput.isSameAddress(mNetworkHelper.getWifiIp())
                && mSubnetInput.getAddress().equals(mNetworkHelper.getWifiSubnetMask())
                && mGatewayInput.isSameAddress(mNetworkHelper.getWifiGateWay())
                && mDns1Input.isSameAddress(mNetworkHelper.getWifiDns1())
                && mDns2Input.isSameAddress(mNetworkHelper.getWifiDns2());
    }
}
