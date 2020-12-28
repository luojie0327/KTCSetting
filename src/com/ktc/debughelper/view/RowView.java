package com.ktc.debughelper.view;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ktc.setting.R;

import java.util.List;

public class RowView<T extends RecyclerView.Adapter> extends LinearLayout {
    T adapter;
    private View baseView;
    private PredictRecyclerView mRowContentRv;
    private TextView mRowTitleTv;

    public RowView(Context context) {
        this(context, null);
    }

    public RowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        baseView = LayoutInflater.from(context).inflate(R.layout.row, this, false);
        mRowContentRv = (PredictRecyclerView) baseView.findViewById(R.id.mRowContentRv);
        mRowTitleTv = (TextView) baseView.findViewById(R.id.mRowTitleTv);
        this.addView(baseView);
        //must put it back to addView,in case of null pointer
        FocusLinearLayoutManager layoutManager = new FocusLinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRowContentRv.setLayoutManager(layoutManager);
        mRowContentRv.addItemDecoration(new KItemDecoration(4, 4, 4, 4));
        mRowContentRv.setItemAnimator(null);
    }


    public RowView init(String title, T adapter) {
        mRowTitleTv.setText(title);
        this.adapter = adapter;
        adapter.setHasStableIds(true); //if need the item id
        mRowContentRv.setAdapter(adapter);
        return this;
    }

    public RecyclerView.Adapter<RecyclerView.ViewHolder> getAdapter() {
        return adapter;
    }

    public static abstract class RowDataSet<T> {
        public abstract List<T> getRowDataSet();

        public abstract String getRowTitle();
    }
}