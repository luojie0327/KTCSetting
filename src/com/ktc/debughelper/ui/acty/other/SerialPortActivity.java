package com.ktc.debughelper.ui.acty.other;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.ktc.debughelper.ui.acty.BaseDialogActivity;
import com.ktc.debughelper.util.BaseInfoUtil;
import com.ktc.setting.R;

public class SerialPortActivity extends BaseDialogActivity<Void, Boolean> {

    private View baseView;
    private Switch mDebugStatusSw;

    @Override
    public Boolean loadData() {
        return BaseInfoUtil.isSerialPortOpen();
    }

    @Override
    public void performDataToUi(Boolean result) {
        mDebugStatusSw.setChecked(result);
        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDebugStatusSw.isChecked()) {
                    //to off
                    BaseInfoUtil.setUartOnOff(false);
                } else {
                    BaseInfoUtil.setUartOnOff(true);
                }
                mDebugStatusSw.setChecked(!mDebugStatusSw.isChecked());
            }
        });
    }

    @Override
    public Boolean beforeLoadUi() {
        mDialogTitleTv.setText(getString(R.string.str_other_serial_port));
        mActionTv.setVisibility(View.GONE);
        LayoutInflater.from(context).inflate(R.layout.layout_debug_window, mContainerFL);
        mContainerFL.setFocusable(false);
        baseView = mContainerFL.getChildAt(0);
        TextView mDebugNameTv = (TextView) baseView.findViewById(R.id.mDebugNameTv);
        mDebugStatusSw = (Switch) baseView.findViewById(R.id.mDebugStatusSw);
        baseView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    baseView.setTranslationZ(10f);
                } else {
                    baseView.setTranslationZ(0f);
                }
            }
        });
        mDebugNameTv.setText(getString(R.string.str_other_serial_port));
        return true;
    }
}