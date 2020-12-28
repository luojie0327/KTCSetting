package com.ktc.debughelper.ui.acty.baseInfo;


import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.View;

import com.ktc.debughelper.bean.CpuInfoBean;
import com.ktc.debughelper.ui.acty.BaseDialogActivity;
import com.ktc.debughelper.ui.adapter.BaseInfoTextAdapter;
import com.ktc.debughelper.util.BaseInfoUtil;
import com.ktc.debughelper.util.KtcFileUtil;
import com.ktc.setting.R;

import java.util.ArrayList;
import java.util.List;

public class HardWareInfoActivity extends BaseDialogActivity<Void, List<String>> {

    private Long getRomMemory() {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    @Override
    public List<String> loadData() {
        List<String> dataList = new ArrayList<>();
        dataList.add(getString(R.string.str_base_hardware_board) + (": ") + (BaseInfoUtil.getProjectName()));
        //        dataList.add(getString(R.string.str_base_hardware_chip).append(": "))
        dataList.add(
                getString(R.string.str_base_hardware_ram) + (": ") + (
                        Formatter.formatFileSize(
                                context,
                                getRomMemory()
                        )
                )
        );
        CpuInfoBean cpuInfoBean = KtcFileUtil.getCpuInfo();
        dataList.add(getString(R.string.str_base_hardware_cpu_core_count) + (": ") + (cpuInfoBean.getCpuCount()));
        dataList.add(getString(R.string.str_base_hardware_cpu_bit_count) + (": ") + (Build.CPU_ABI));
        //        dataList.add(getString(R.string.str_base_hardware_cpu_type).append(": "))
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
        mDialogTitleTv.setText(getString(R.string.str_base_hardware_info));
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