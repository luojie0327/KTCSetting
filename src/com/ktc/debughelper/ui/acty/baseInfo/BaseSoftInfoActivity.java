package com.ktc.debughelper.ui.acty.baseInfo;


import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.View;

import com.ktc.debughelper.ui.acty.BaseDialogActivity;
import com.ktc.debughelper.ui.adapter.BaseInfoTextAdapter;
import com.ktc.debughelper.util.BaseInfoUtil;
import com.ktc.setting.R;

import java.util.ArrayList;
import java.util.List;

public class BaseSoftInfoActivity extends BaseDialogActivity<Void, List<String>> {


    @Override
    public List<String> loadData() {
        List<String> dataList = new ArrayList<>();
        dataList.add(getString(R.string.str_base_soft_anversion) + (" : ") + (android.os.Build.VERSION.RELEASE));
        dataList.add(getString(R.string.str_base_soft_mmVersion) + (" : ") + (BaseInfoUtil.getPropValue("ktc.ver.android")));
        dataList.add(getString(R.string.str_base_soft_kernelVersion) + (" : ") + (BaseInfoUtil.getPropValue("ktc.ver.kernel")));
        dataList.add(getString(R.string.str_base_soft_mBootVersion) + (" : ") + (BaseInfoUtil.getPropValue("ktc.ver.mboot")));
        dataList.add(getString(R.string.str_base_soft_superNovaVersion) + (" : ") + (BaseInfoUtil.getPropValue("ktc.ver.supv")));
        dataList.add(getString(R.string.str_base_soft_compileDate) + (" : ") + (BaseInfoUtil.getPropValue("ro.build.date")));
        dataList.add(getString(R.string.str_base_soft_romSpace) + (" : ") + (Formatter.formatFileSize(context, BaseInfoUtil.getRomSpace())));
        dataList.add(getString(R.string.str_base_soft_macAddress) + (" : ") + (BaseInfoUtil.getMacAddress()));
        return dataList;
    }

    @Override
    public void performDataToUi(List<String> result) {
        RecyclerView recyclerView = createRecycleView();
        recyclerView.setAdapter(new BaseInfoTextAdapter(context, result, R.layout.item_base_info_text, false));
        mContainerFL.addView(recyclerView);
    }

    @Override
    public Boolean beforeLoadUi() {
        mDialogTitleTv.setText(getString(R.string.str_base_soft_info));
        mActionTv.setText(getString(R.string.str_base_dialog_exit));
        mActionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return true;
    }
}