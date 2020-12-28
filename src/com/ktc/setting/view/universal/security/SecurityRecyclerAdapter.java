package com.ktc.setting.view.universal.security;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ktc.setting.R;
import com.ktc.setting.view.custom.SwitchSettingView;

import java.util.List;


public class SecurityRecyclerAdapter extends RecyclerView.Adapter<SecurityRecyclerAdapter.ViewHolder>
        implements SwitchSettingView.OnSwitchListener {

    private Context context;
    private List<ApplicationInfo> appEntries;
    private SecurityManager mSecurityManager;
    private RecyclerView mRecyclerView;

    public SecurityRecyclerAdapter(Context context, SecurityManager securityManager
            , RecyclerView recyclerView) {
        this.context = context;
        mSecurityManager = securityManager;
        appEntries = mSecurityManager.getInstallerPackages();
        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.security_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.securitySwitch.setValueArray(context.getResources().getStringArray(R.array.str_array_common_switch));
        viewHolder.securitySwitch.setTitle(appEntries.get(i).loadLabel(context.getPackageManager()).toString());
        viewHolder.securitySwitch.setIndex(mSecurityManager.canInstall(appEntries.get(i)) ? 0 : 1);
        viewHolder.securitySwitch.setOnSwitchListener(this);
    }

    @Override
    public int getItemCount() {
        if (appEntries == null) {
            return 0;
        }
        return appEntries.size();
    }

    @Override
    public void onSwitch(View view, int index) {
        int position = getCurrentPosition();
        ApplicationInfo currentInfo = appEntries.get(position);
        mSecurityManager.setCanInstall(currentInfo, index == 0);
    }

    private int getCurrentPosition() {
        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
            if (mRecyclerView.getChildAt(i).hasFocus()) {
                return mRecyclerView.getChildPosition(mRecyclerView.getChildAt(i));
            }
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        SwitchSettingView securitySwitch;

        ViewHolder(View itemView) {
            super(itemView);
            securitySwitch = (SwitchSettingView) itemView.findViewById(R.id.security_switch);
        }
    }

}
