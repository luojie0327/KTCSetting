package com.ktc.debughelper.ui.acty.baseInfo;


import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import com.ktc.debughelper.ui.acty.BaseDialogActivity;
import com.ktc.debughelper.ui.adapter.BaseInfoTextAdapter;
import com.ktc.debughelper.util.BaseInfoUtil;
import com.ktc.setting.R;

import java.util.ArrayList;
import java.util.List;

public class ScreenInfoActivity extends BaseDialogActivity<Void, List<String>> {


    private String getScreenPix() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        return width + " x " + height;
    }

    @Override
    public List<String> loadData() {
        List<String> dataList = new ArrayList<>();
        dataList.add(getString(R.string.str_base_screen_number) + (": ") + (BaseInfoUtil.getPanelName()));
        dataList.add(getString(R.string.str_base_screen_density) + (": ") + (BaseInfoUtil.getLcd_Density()));
        dataList.add(getString(R.string.str_base_screen_density_osd) + (": ") + (getScreenPix()));
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
        mDialogTitleTv.setText(getString(R.string.str_base_screen_info));
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