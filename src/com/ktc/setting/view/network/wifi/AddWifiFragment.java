package com.ktc.setting.view.network.wifi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ktc.setting.R;
import com.ktc.setting.helper.NetworkHelper;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.LittleSwitchView;
import com.ktc.setting.view.custom.TitleEditView;
import com.ktc.setting.view.custom.ToastFactory;

public class AddWifiFragment extends BaseFragment {

    private EditText mNameInput;
    private LittleSwitchView mSecuritySwitch;
    private TitleEditView mPasswordInput;
    private Button mSaveButton;

    private boolean mClickConnect;
    private NetworkHelper mNetworkHelper;

    @Override
    protected int getRes() {
        return R.layout.fragment_wifi_add;
    }

    @Override
    protected int getTitle() {
        return R.string.title_add_wifi;
    }

    @Override
    protected void initView(final View view) {
        mNameInput = (EditText) view.findViewById(R.id.et_wifi_name);
        mSecuritySwitch = (LittleSwitchView) view.findViewById(R.id.switch_wifi_security);
        mPasswordInput = (TitleEditView) view.findViewById(R.id.input_wifi_pwd);
        mSaveButton = (Button) view.findViewById(R.id.button_save);
    }

    @Override
    protected void setFocus() {
        mNameInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                mNameInput.requestFocus();
                mNameInput.requestFocusFromTouch();
                InputMethodManager manager = ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                if (manager != null) {
                    manager.showSoftInput(mNameInput, 0);
                }
            }
        }, 300);
    }

    @Override
    protected void initData() {
        mNetworkHelper = NetworkHelper.getInstance(mActivity);
    }

    @Override
    protected void addListener() {
        mSecuritySwitch.setOnSwitchListener(new LittleSwitchView.OnSwitchListener() {
            @Override
            public void onSwitch(View view, int index) {
                mPasswordInput.setEnabled(index != 0);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mNameInput.getText().toString().trim();
                String password = mPasswordInput.getValue().trim();
                int security = mSecuritySwitch.getIndex();

                if (name.length() > 0
                        && (security == 0 || password.length() >= 8)) {
                    mNetworkHelper.connectHideWifi(name, security, password);
                    mClickConnect = true;
                    mActivity.getFragmentManager().popBackStack();
                } else {
                    ToastFactory.showToast(mActivity, mActivity.getString(R.string.network_invalid_input)
                            , Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getTargetFragment() != null) {
            Intent i = new Intent();
            i.putExtra(WifiFragment.EXTRA_CONNECT, mClickConnect);
            i.putExtra(WifiFragment.EXTRA_ITEM, (Parcelable) null);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        }
    }
}
