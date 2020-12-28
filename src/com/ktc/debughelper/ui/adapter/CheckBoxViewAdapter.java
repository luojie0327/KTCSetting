package com.ktc.debughelper.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.ktc.debughelper.bean.CheckBoxBean;
import com.ktc.setting.R;

import java.util.List;

public class CheckBoxViewAdapter extends
        RvBaseAdapter<CheckBoxBean, CheckBoxViewAdapter.ViewHolder> {

    OnItemClickListener listener;

    public CheckBoxViewAdapter(Context context, List<CheckBoxBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mCbNameTv.setText(datas.get(position).getTitle());
        holder.mStatusSw.setChecked(datas.get(position).getChecked());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position, holder.mStatusSw.isChecked());
            }
        });
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.view_anim_big);
                    //                    holder.itemView.startAnimation(animation);
                    holder.itemView.setTranslationZ(10f);
                    holder.itemView.setSelected(true);
                } else {
                    //                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.view_anim_small);
                    //                    holder.itemView.startAnimation(animation);
                    holder.itemView.setTranslationZ(0f);
                    holder.itemView.setSelected(false);
                }
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(int position, boolean isChecked);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mCbNameTv;
        Switch mStatusSw;

        public ViewHolder(View itemView) {
            super(itemView);
            mCbNameTv = (TextView) itemView.findViewById(R.id.mCbNameTv);
            mStatusSw = (Switch) itemView.findViewById(R.id.mStatusSw);
        }
    }
}
