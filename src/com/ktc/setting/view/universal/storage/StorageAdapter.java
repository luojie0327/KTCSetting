package com.ktc.setting.view.universal.storage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ktc.setting.R;
import com.ktc.setting.view.custom.SelectSettingView;

import java.util.List;

public class StorageAdapter extends RecyclerView.Adapter<StorageAdapter.ViewHolder> implements SelectSettingView.OnItemSelectListener {

    private List<DiskInfo> mDiskInfoList;
    private Context mContext;
    private SelectSettingView.OnItemSelectListener mOnItemSelectListener;

    public StorageAdapter(Context context, List<DiskInfo> diskInfoList) {
        mContext = context;
        mDiskInfoList = diskInfoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.storage_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mSelectSettingView.setSelectTitle(mDiskInfoList.get(position).getName());
        holder.mSelectSettingView.setSelectContent(getContentDescription(mDiskInfoList.get(position)));
        holder.mSelectSettingView.setOnItemSelectListener(this);
    }

    @Override
    public int getItemCount() {
        return mDiskInfoList.size();
    }

    public void setOnItemSelectListener(SelectSettingView.OnItemSelectListener onItemSelectListener) {
        mOnItemSelectListener = onItemSelectListener;
    }

    @Override
    public void onSelect(boolean isCheck) {
        mOnItemSelectListener.onSelect(isCheck);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        SelectSettingView mSelectSettingView;

        ViewHolder(View itemView) {
            super(itemView);
            mSelectSettingView = (SelectSettingView) itemView.findViewById(R.id.storage_item_select);
        }
    }

    private String getContentDescription(DiskInfo diskInfo) {
        return mContext.getResources().getString(R.string.str_universal_storage_free) + " " + StorageUtil.getFileSizeDescription
                (diskInfo.getAvailSpace()) + "/" + StorageUtil.getTotalSpaceDescription(diskInfo);
    }

}
