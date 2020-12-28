package com.ktc.setting.view.network.wifi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.setting.R;
import com.ktc.setting.helper.NetworkHelper;
import com.ktc.setting.model.WifiItem;
import com.ktc.setting.view.base.BaseFragment;

public class ConnectWifiFragment extends BaseFragment {

    private static final String ARG_ITEM = "item_arg";
    private NetworkHelper mNetworkHelper;
    private WifiItem mItem;
    private boolean mClickConnect = false;

    private TextView mTitle;
    private RelativeLayout mPwdContainer;
    private EditText mEtPassword;
    private CheckBox mCbShowPwd;
    private Button mConnect;

    public static ConnectWifiFragment newInstance(WifiItem item) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_ITEM, item);
        ConnectWifiFragment fragment = new ConnectWifiFragment();
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
    protected int getRes() {
        return R.layout.fragment_wifi_connect;
    }

    @Override
    protected int getTitle() {
        return R.string.title_wifi;
    }

    @Override
    protected void initView(View view) {
        mTitle = (TextView) view.findViewById(R.id.connect_wifi_title);
        mPwdContainer = (RelativeLayout) view.findViewById(R.id.pwd_container);
        mEtPassword = (EditText) view.findViewById(R.id.et_password);
        mCbShowPwd = (CheckBox) view.findViewById(R.id.show_password);
        mConnect = (Button) view.findViewById(R.id.bt_connect_wifi);
    }

    @Override
    protected void setFocus() {
        if (mItem.getSecurity() == NetworkHelper.SECURITY_NONE) {
            mConnect.requestFocus();
        } else {
            mEtPassword.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mEtPassword.requestFocus();
                    InputMethodManager manager = ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null) {
                        manager.showSoftInput(mEtPassword, 0);
                    }
                }
            }, 300);
        }
    }

    @Override
    protected void initData() {
        mNetworkHelper = NetworkHelper.getInstance(getActivity());

        mTitle.setText(String.format(getString(R.string.network_connect_to), mItem.getSSID()));
        if (mItem.getSecurity() == NetworkHelper.SECURITY_NONE) {
            mPwdContainer.setVisibility(View.GONE);
        } else {
            setButtonEnable(mConnect, false);
        }
    }

    @Override
    protected void addListener() {
        mEtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        && mEtPassword.getText().length() >= 8) {
                    connectToWifi();
                }
                return false;
            }
        });

        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEtPassword.getText().length() < 8) {
                    setButtonEnable(mConnect, false);
                } else {
                    setButtonEnable(mConnect, true);
                }
            }
        });

        mCbShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToWifi();
            }
        });
    }

    private void connectToWifi() {
        mNetworkHelper.connectWifi(mItem.getSSID(), mItem.getSecurity(), mEtPassword.getText().toString());
        mItem.setConnectedState(WifiItem.STATE_CONNECTING);
        mItem.setSaved(true);
        mClickConnect = true;
        mActivity.getFragmentManager().popBackStack();
    }

    private void setButtonEnable(Button button, boolean enable) {
        button.setEnabled(enable);
        button.setClickable(enable);
        button.setFocusable(enable);
        button.setFocusableInTouchMode(enable);
        button.setAlpha(enable ? (float) 1.0 : (float) 0.5);
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
}
