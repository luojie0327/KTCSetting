package com.ktc.setting.view.others.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ktc.setting.R;
import com.ktc.setting.view.custom.ButtonSettingView;
import com.ktc.setting.view.others.OthersActivity;

import java.util.List;

public class BluetoothPairedAdapter extends RecyclerView.Adapter<BluetoothPairedAdapter.ViewHolder> {

    private Context mContext;
    private List<BluetoothDevice> mDevices;
    private OnConnectItemClickListener mConnectItemClickListener;

    public BluetoothPairedAdapter(Context context, List<BluetoothDevice> devices) {
        mContext = context;
        mDevices = devices;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bluetooth_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final boolean bondState = BluetoothConnectManager.getConnectState(((OthersActivity) mContext)
                .mBluetoothA2dp, mDevices.get(position));
        final BluetoothDevice device = mDevices.get(position);
        if (TextUtils.isEmpty(mDevices.get(position).getName())) {
            holder.itemBtn.setTitle(mContext.getResources().getString(R.string.str_others_bluetooth_device_name_default));
        } else {
            holder.itemBtn.setTitle(mDevices.get(position).getName());
        }
        if (bondState) {
            holder.itemBtn.setValue(mContext.getString(R.string.str_others_bluetooth_paired_connect));
        } else {
            holder.itemBtn.setValue(mContext.getString(R.string.str_others_bluetooth_paired_disconnect));
        }
        holder.itemBtn.setOnButtonClickListener(new ButtonSettingView.OnButtonClickListener() {
            @Override
            public void onClick(View view) {
                if (mConnectItemClickListener != null) {
                    mConnectItemClickListener.onItemClick(view, bondState, device);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mDevices == null) {
            return 0;
        }
        return mDevices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ButtonSettingView itemBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            itemBtn = (ButtonSettingView) itemView.findViewById(R.id.bluetooth_item_btn);
        }
    }

    public void setOnConnectItemClickListener(OnConnectItemClickListener onConnectItemClickListener) {
        mConnectItemClickListener = onConnectItemClickListener;
    }

    public interface OnConnectItemClickListener {
        void onItemClick(View view, boolean isConnect, BluetoothDevice bluetoothDevice);
    }
}
