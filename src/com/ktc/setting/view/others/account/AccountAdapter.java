package com.ktc.setting.view.others.account;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ktc.setting.R;
import com.ktc.setting.view.custom.SelectSettingView;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder>
        implements SelectSettingView.OnItemSelectListener {

    private Context mContext;
    private List<AccountBean> mAccountBeans;
    private OnItemSelectListener mOnItemSelectListener;

    public AccountAdapter(Context context, List<AccountBean> accountBeans) {
        mContext = context;
        mAccountBeans = accountBeans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.account_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.accountSelect.setSelectTitle(mAccountBeans.get(position).getAccount().name);
        holder.accountSelect.setOnItemSelectListener(this);
    }

    @Override
    public int getItemCount() {
        if (mAccountBeans != null) {
            return mAccountBeans.size();
        }
        return 0;
    }

    @Override
    public void onSelect(boolean isCheck) {
        if (mOnItemSelectListener != null) {
            mOnItemSelectListener.onItemSelect(isCheck);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        SelectSettingView accountSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            accountSelect = (SelectSettingView) itemView.findViewById(R.id.account_select);
        }
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        mOnItemSelectListener = onItemSelectListener;
    }

    public interface OnItemSelectListener {
        void onItemSelect(boolean isCheck);
    }
}
