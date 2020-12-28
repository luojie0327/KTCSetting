package com.ktc.debughelper.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ktc.debughelper.bean.FunctionBean;
import com.ktc.setting.R;

import java.util.List;

public class RowDataAdapter extends RvBaseAdapter<FunctionBean, RowDataAdapter.ViewHolder> {


    public RowDataAdapter(Context context, List<FunctionBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, layoutId, null);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RowDataAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ((ViewHolder) holder).mRowItemTv.setText(datas.get(position).getName());
    }

    @Override
    public long getItemId(int position) {
        return datas.get(position).getId();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mRowItemTv;

        public ViewHolder(View itemView) {
            super(itemView);
            mRowItemTv = (TextView) itemView.findViewById(R.id.mRowItemTv);
        }
    }
}