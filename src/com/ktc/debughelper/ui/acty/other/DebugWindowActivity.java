package com.ktc.debughelper.ui.acty.other;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.ktc.debughelper.logcat.KtcFloatInfoService;
import com.ktc.debughelper.ui.acty.BaseDialogActivity;
import com.ktc.debughelper.util.BaseInfoUtil;
import com.ktc.setting.R;

public class DebugWindowActivity extends BaseDialogActivity<Void, Boolean> {
    private View baseView;
    private Switch mDebugStatusSw;

    @Override
    public Boolean loadData() {
        return BaseInfoUtil.isServiceRunning(context, "com.ktc.debughelper.logcat.KtcFloatInfoService");
    }

    @Override
    public void performDataToUi(Boolean result) {
        mDebugStatusSw.setChecked(result);
        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDebugStatusSw.isChecked()) {
                    //to off
                    sendBroadcast(new Intent("ktc.test.stop.floatinfo"));
                } else {
                    startService(new Intent(DebugWindowActivity.this, KtcFloatInfoService.class));
                }
                mDebugStatusSw.setChecked(!mDebugStatusSw.isChecked());
            }
        });
    }

    @Override
    public Boolean beforeLoadUi() {
        mDialogTitleTv.setText(getString(R.string.str_other_debug_window));
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
        mDebugNameTv.setText(getString(R.string.str_other_debug_window));
        return true;
    }
}