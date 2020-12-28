package com.ktc.debughelper.ui.acty.baseInfo;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ktc.debughelper.factory.NetWorkInfoFactory;
import com.ktc.debughelper.ui.acty.BaseDialogActivity;
import com.ktc.debughelper.ui.adapter.BaseInfoTextAdapter;
import com.ktc.setting.R;

import java.util.List;

public class NetWorkActivity extends BaseDialogActivity<Void, List<String>> {


    private NetWorkInfoFactory netWorkFactory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        netWorkFactory = new NetWorkInfoFactory(this);
    }

    @Override
    public List<String> loadData() {
        return netWorkFactory.constructorNetWorkInfo();
    }

    @Override
    public void performDataToUi(List<String> result) {
        if (result == null || result.size() == 0) {
            TextView contentTv = createCenterTextView();
            contentTv.setText("请确认网络是否正确连通");
            mContainerFL.addView(contentTv);
        } else {
            RecyclerView recyclerView = createRecycleView();
            recyclerView.setAdapter(new BaseInfoTextAdapter(context, result, R.layout.item_base_info_text, false));
            mContainerFL.addView(recyclerView);
        }
    }

    @Override
    public Boolean beforeLoadUi() {
        mDialogTitleTv.setText(getString(R.string.str_base_network));
        mActionTv.setText(getString(R.string.str_network_setting));
        mActionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setAction("android.settings.SETTINGS");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
            }
        });
        return true;
    }
}