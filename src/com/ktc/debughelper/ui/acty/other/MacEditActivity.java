package com.ktc.debughelper.ui.acty.other;


import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ktc.debughelper.ui.acty.BaseDialogActivity;
import com.ktc.debughelper.util.BaseInfoUtil;
import com.ktc.setting.R;

public class MacEditActivity extends BaseDialogActivity<Void, Void> {

    private EditText mInputMacEt;

    @Override
    public Void loadData() {
        return null;
    }

    @Override
    public void performDataToUi(Void aVoid) {
    }

    @Override
    public Boolean beforeLoadUi() {
        mDialogTitleTv.setText(getString(R.string.str_other_mac_edit_title));
        mActionTv.setText(getString(R.string.str_mac_edit_sure));
        View baseView = View.inflate(context, R.layout.layout_mac_edit, null);
        mContainerFL.addView(baseView);

        TextView mCurTip = (TextView) baseView.findViewById(R.id.mCurTip);
        EditText mCurMacEt = (EditText) baseView.findViewById(R.id.mCurMacEt);
        final TextView mInputTip = (TextView) baseView.findViewById(R.id.mInputTip);
        mInputMacEt = (EditText) baseView.findViewById(R.id.mInputMacEt);

        mCurTip.setText(getString(R.string.str_mac_edit_last));
        mInputTip.setText(getString(R.string.str_mac_edit_input));
        mCurMacEt.setText(BaseInfoUtil.getMacAddress());

        mInputMacEt.setText("");
        mActionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMacAddress = mInputMacEt.getText().toString();
                if (newMacAddress.length() != 12) { //just use the basic vertify mad address
                    mKToast.showToast("please input the correct mac address");
                    return;
                }
                BaseInfoUtil.setMacAddress(splitToMacAddress(newMacAddress));
                finish();
            }
        });
        return false;
    }

    private String splitToMacAddress(String newMacAddress) {
        return newMacAddress.substring(0, 2) + ":"
                + newMacAddress.substring(2, 4) + ":"
                + newMacAddress.substring(4, 6) + ":"
                + newMacAddress.substring(6, 8) + ":"
                + newMacAddress.substring(8, 10) + ":"
                + newMacAddress.substring(10, 12);
    }
}