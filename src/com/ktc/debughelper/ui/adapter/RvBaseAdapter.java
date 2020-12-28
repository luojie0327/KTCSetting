package com.ktc.debughelper.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.ktc.setting.R;

import java.util.List;

public abstract class RvBaseAdapter<T, V extends RecyclerView.ViewHolder> extends
        RecyclerView.Adapter<V> {
    Context context;
    List<T> datas;
    int layoutId;
    OnItemClickListener listener;

    public RvBaseAdapter(Context context, List<T> datas, int layoutId) {
        this.context = context;
        this.datas = datas;
        this.layoutId = layoutId;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(final V holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position, (int) getItemId(position));
            }
        });
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.view_anim_big);
                    holder.itemView.startAnimation(animation);
                    holder.itemView.setTranslationZ(10f);
                    holder.itemView.setSelected(true);
                } else {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.view_anim_small);
                    holder.itemView.startAnimation(animation);
                    holder.itemView.setTranslationZ(0f);
                    holder.itemView.setSelected(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position, int itemId);
    }

}