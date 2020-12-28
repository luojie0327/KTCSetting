package com.ktc.debughelper.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ktc.debughelper.bean.SingleChoiceBean;
import com.ktc.setting.R;

import java.util.List;

public class SingleChoiceAdapter extends
        RvBaseAdapter<SingleChoiceBean, SingleChoiceAdapter.ViewHolder> {
    int checkedIndex = -1;

    public SingleChoiceAdapter(Context context, List<SingleChoiceBean> datas, int layoutId) {
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        holder.mItemNameTv.setText(datas.get(position).getItemName());
        holder.mCurRb.setChecked(datas.get(position).getCurItemSelected());
        if (datas.get(position).getCurItemSelected()) {
            checkedIndex = position;
        }
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    holder.itemView.setTranslationZ(10f);
                    holder.itemView.setSelected(true);
                } else {
                    holder.itemView.setTranslationZ(0f);
                    holder.itemView.setSelected(false);
                }
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mItemNameTv;
        RadioButton mCurRb;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemNameTv = (TextView) itemView.findViewById(R.id.mItemNameTv);
            mCurRb = (RadioButton) itemView.findViewById(R.id.mCurRb);
        }
    }
}