package com.ktc.debughelper.ui.acty.baseInfo;


import android.app.ActivityManagerNative;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;

import com.android.internal.app.LocalePicker;
import com.ktc.debughelper.bean.SingleChoiceBean;
import com.ktc.debughelper.ui.acty.BaseDialogActivity;
import com.ktc.debughelper.ui.adapter.SingleChoiceAdapter;
import com.ktc.debughelper.util.LocaleComparator;
import com.ktc.debughelper.view.KItemDecoration;
import com.ktc.setting.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageActivity extends BaseDialogActivity<Void, List<SingleChoiceBean>> {
    private ArrayAdapter<LocalePicker.LocaleInfo> mLocales;

    @Override
    public List<SingleChoiceBean> loadData() {
        List<SingleChoiceBean> dataList = new ArrayList<>();
        try {
            Locale currentLocale = ActivityManagerNative.getDefault().getConfiguration().locale;
            mLocales = LocalePicker.constructAdapter(this);
            mLocales.sort(new LocaleComparator());
            for (int i = 0; i < mLocales.getCount(); i++) {
                Locale localeInfo = mLocales.getItem(i).getLocale();
                dataList.add(new SingleChoiceBean(localeInfo.getDisplayName(localeInfo), localeInfo.equals(currentLocale)));
            }
        } catch (RemoteException e) {
        }
        return dataList;
    }

    @Override
    public void performDataToUi(List<SingleChoiceBean> result) {
        RecyclerView recyclerView = createRecycleView();
        recyclerView.setPadding(16, 16, 16, 16);
        SingleChoiceAdapter adapter = new SingleChoiceAdapter(context, result, R.layout.item_sigle_choice);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new KItemDecoration(8, 8, 8, 8));
        mContainerFL.addView(recyclerView);
        adapter.setOnItemClickListener(new SingleChoiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, int itemId) {
                LocalePicker.updateLocale(mLocales.getItem(position).getLocale());
                finish();
            }
        });
    }

    @Override
    public Boolean beforeLoadUi() {
        mDialogTitleTv.setText(getString(R.string.str_base_language));
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