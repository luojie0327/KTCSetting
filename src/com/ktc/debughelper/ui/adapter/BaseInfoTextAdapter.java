package com.ktc.debughelper.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ktc.setting.R;

import java.util.List;

public class BaseInfoTextAdapter extends RvBaseAdapter<String, BaseInfoTextAdapter.ViewHolder> {
    private boolean needFocus = false;

    public BaseInfoTextAdapter(Context context, List<String> datas, int layoutId, boolean needFocus) {
        super(context, datas, layoutId);
        this.needFocus = needFocus;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        if (needFocus) {
            view.setFocusable(true);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextTv.setText(datas.get(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextTv;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextTv = (TextView) itemView.findViewById(R.id.mTextTv);
        }
    }
}