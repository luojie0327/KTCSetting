package com.ktc.setting.view.network.ethernet;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ktc.setting.R;
import com.ktc.setting.helper.NetworkHelper;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.IpInputView;
import com.ktc.setting.view.custom.SettingViewContainer;
import com.ktc.setting.view.custom.ToastFactory;

public class EthernetIpFragment extends BaseFragment {

    private NetworkHelper mNetworkHelper;

    private SettingViewContainer mContainer;
    private IpInputView mIpInput;
    private IpInputView mSubnetInput;
    private IpInputView mGatewayInput;
    private IpInputView mDns1Input;
    private IpInputView mDns2Input;
    private Button mBtOk;
    private Button mBtCancel;

    @Override
    protected int getRes() {
        return R.layout.fragment_ethernet_ip;
    }

    @Override
    protected int getTitle() {
        return R.string.title_connect_manually;
    }

    @Override
    protected void initView(View view) {
        mContainer = (SettingViewContainer) view.findViewById(R.id.edit_container);
        mIpInput = (IpInputView) view.findViewById(R.id.edit_ip);
        mSubnetInput = (IpInputView) view.findViewById(R.id.edit_subnet);
        mGatewayInput = (IpInputView) view.findViewById(R.id.edit_gateway);
        mDns1Input = (IpInputView) view.findViewById(R.id.edit_dns1);
        mDns2Input = (IpInputView) view.findViewById(R.id.edit_dns2);
        mBtCancel = (Button) view.findViewById(R.id.button_cancel);
        mBtOk = (Button) view.findViewById(R.id.button_ok);
    }

    @Override
    protected void setFocus() {
        mContainer.setNewFocus(mIpInput);
        //mIpInput.requestFocus();
        //mIpInput.requestFocusFromTouch();
    }

    @Override
    protected void initData() {
        mNetworkHelper = NetworkHelper.getInstance(mActivity);

        mIpInput.setAddress(mNetworkHelper.getEthernetIp());
        mGatewayInput.setAddress(mNetworkHelper.getEthernetGateway());
        mSubnetInput.setAddress(mNetworkHelper.getEthernetSubnet());
        mDns1Input.setAddress(mNetworkHelper.getEthernetDns1());
        mDns2Input.setAddress(mNetworkHelper.getEthernetDns2());
    }

    @Override
    protected void addListener() {
        mBtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.getFragmentManager().popBackStack();
            }
        });

        mBtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = mIpInput.getAddress();
                String subnet = mSubnetInput.getAddress();
                String gateway = mGatewayInput.getAddress();
                String dns1 = mDns1Input.getAddress();
                String dns2 = mDns2Input.getAddress();

                if (NetworkHelper.matchIP(ip) && NetworkHelper.matchIP(subnet)
                        && NetworkHelper.matchIP(gateway) && NetworkHelper.matchIP(dns1)) {
                    mNetworkHelper.saveEthernetAsStatic(ip, subnet, gateway, dns1, dns2);
                    mActivity.getFragmentManager().popBackStack();
                } else {
                    ToastFactory.showToast(mActivity, mActivity.getString(R.string.network_invalid_input)
                            , Toast.LENGTH_SHORT);
                }
            }
        });
    }
}
