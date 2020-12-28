package com.ktc.debughelper.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.debughelper.bean.OtaInfoBean;
import com.ktc.setting.R;

import java.util.List;

public class OtaInfoAdapter extends RvBaseAdapter<OtaInfoBean, OtaInfoAdapter.ViewHolder> {
    private boolean needFocus = false;

    public OtaInfoAdapter(Context context, List<OtaInfoBean> datas, int layoutId, boolean needFocus) {
        super(context, datas, layoutId);
        this.needFocus = needFocus;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (datas.get(position).getTitle()) {
            holder.itemView.setFocusable(false);
            holder.mHeadTitle.setVisibility(View.VISIBLE);
            holder.mContentLL.setVisibility(View.GONE);
            holder.mHeadTitle.setText(datas.get(position).getFieldName());
        } else {
            holder.itemView.setFocusable(true);
            holder.mHeadTitle.setVisibility(View.GONE);
            holder.mContentLL.setVisibility(View.VISIBLE);
            holder.mContentFieldNameTv.setText(datas.get(position).getFieldName());
            holder.mContentFieldDescTv.setText(datas.get(position).getFieldDesc());
            holder.mContentFieldValueTv.setText(datas.get(position).getFieldValue());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, layoutId, null);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mHeadTitle;
        RelativeLayout mContentLL;
        TextView mContentFieldNameTv;
        TextView mContentFieldDescTv;
        TextView mContentFieldValueTv;

        public ViewHolder(View itemView) {
            super(itemView);
            mHeadTitle = (TextView) itemView.findViewById(R.id.mHeadTitle);
            mContentLL = (RelativeLayout) itemView.findViewById(R.id.mContentLL);
            mContentFieldNameTv = (TextView) itemView.findViewById(R.id.mContentFieldNameTv);
            mContentFieldDescTv = (TextView) itemView.findViewById(R.id.mContentFieldDescTv);
            mContentFieldValueTv = (TextView) itemView.findViewById(R.id.mContentFieldValueTv);
        }
    }
}