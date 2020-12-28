package com.ktc.debughelper.logcat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ktc.debughelper.ui.acty.other.BootHomeActivity;
import com.ktc.debughelper.util.ConfigConstant;
import com.ktc.debughelper.util.KtcLogerUtil;
import com.ktc.debughelper.util.SharedPreferencesUtil;
import com.ktc.debughelper.util.Tools;

/**
 * @author Arvin
 * @TODO 静态广播监听器
 * @Date 2019.1.25
 */
public class BootReceiver extends BroadcastReceiver {
    private final static String TAG = BootReceiver.class.getSimpleName();
    private static final String STR_BOOT_COMPLETED = "android.intent.action.STR_BOOT_COMPLETED";

    private Context mContext;

    @Override
    public void onReceive(final Context mContext, Intent intent) {
        this.mContext = mContext;
        String action = intent.getAction();
        KtcLogerUtil.I(TAG, "action：  " + action);

        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
            case STR_BOOT_COMPLETED:
            case Intent.ACTION_MEDIA_MOUNTED:
            	if (SharedPreferencesUtil.getInstance().getBoolean(ConfigConstant.KEY_LOG_BENGINING)) {
					final boolean isWorkTaskServiceRunning = Tools.isServiceWorking(mContext, WorkTaskService.class.getName());
					final boolean isLogKeepAliveServiceRunning = Tools.isServiceWorking(mContext, LogKeepAliveService.class.getName());
					if (!isWorkTaskServiceRunning && !isLogKeepAliveServiceRunning) {
						Intent mIntent = new Intent();
						mIntent.setClass(mContext, BootHomeActivity.class);
						mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						mContext.startActivity(mIntent);
						Toast.makeText(mContext, "日志收集服务已启动...", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(mContext, "日志收集服务正在运行中", Toast.LENGTH_LONG).show();
					}
				}
                break;

            default:
                break;
        }
    }

}


