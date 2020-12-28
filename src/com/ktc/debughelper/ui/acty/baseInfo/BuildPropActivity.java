package com.ktc.debughelper.ui.acty.baseInfo;


import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.ktc.debughelper.ui.acty.BaseDialogActivity;
import com.ktc.debughelper.ui.adapter.BaseInfoTextAdapter;
import com.ktc.debughelper.util.CloseUtil;
import com.ktc.setting.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class BuildPropActivity extends BaseDialogActivity<Void, List<String>> {

    @Override
    public List<String> loadData() {
        List<String> dataList = new ArrayList<>();
        BufferedReader bufferedReader = null;
        String fileName = "system/build.prop";
        if (Build.VERSION.SDK_INT >= 24) {
            fileName = "/vendor/build.prop";
        }
        try {
            bufferedReader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!TextUtils.isEmpty(line))
                    dataList.add(line);
            }
        } catch (Exception e) {
        } finally {
            CloseUtil.close(bufferedReader);
        }
        return dataList;
    }

    @Override
    public void performDataToUi(List<String> result) {
        RecyclerView recyclerView = createRecycleView();
        recyclerView.setAdapter(new BaseInfoTextAdapter(context, result, R.layout.item_base_info_text, true));
        mContainerFL.addView(recyclerView);
    }

    @Override
    public Boolean beforeLoadUi() {
        mDialogTitleTv.setText(getString(R.string.str_base_buildprop_title));
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