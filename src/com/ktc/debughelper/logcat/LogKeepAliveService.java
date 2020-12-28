package com.ktc.debughelper.logcat;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.debughelper.util.ConfigConstant;
import com.ktc.debughelper.util.KtcFileUtil;
import com.ktc.debughelper.util.LogUtil;
import com.ktc.debughelper.util.SharedPreferencesUtil;
import com.ktc.debughelper.util.Tools;
import com.ktc.setting.R;


/**
 * @author Arvin
 * @TODO 双服务守护下载进程，实现进程保活
 * @Date 2019.2.14
 */
public class LogKeepAliveService extends Service {
    private static final String TAG = "LogKeepAliveService";
    private static Context mContext;
    private static String log_path = null;


    private RelativeLayout mFloatLayout;
    private LayoutParams wmParams;
    private WindowManager mWindowManager;
    private TextView txt_memInfo;
    private static final int MSG_UPDATE_STATUS = 0x01;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LogUtil.i(TAG, "---handleMessage---:   " + msg);
            switch (msg.what) {
                case MSG_UPDATE_STATUS:
                    txt_memInfo.setText(String.valueOf(KtcFileUtil.getFileSize(log_path)) + "B");
                    sendEmptyMessageDelayed(MSG_UPDATE_STATUS, 2000);
                    break;

                default:
                    break;
            }
        }
    };

    private WorkTaskService mWorkTaskService;
    private WorkTaskService.WorkTaskBinder mWorkTaskBinder;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.i(TAG, "----onServiceConnected----");
            mWorkTaskBinder = (WorkTaskService.WorkTaskBinder) service;

            mWorkTaskService = mWorkTaskBinder.getServiceSelf();
            mWorkTaskService.registerCallBack(mWorkTaskCallBack);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mWorkTaskService != null) {
                mWorkTaskService.unRegisterCallBack(mWorkTaskCallBack);
            }
            LogUtil.i(TAG, "警告：服务连接断开!");
        }
    };

    /**
     * @TODO 绑定WorkTaskService
     */
    private void bindWorkTaskService() {
        LogUtil.i(TAG, "----bindWorkTaskService---");
        Intent mIntent = new Intent(mContext, WorkTaskService.class);
        if (!Tools.isServiceWorking(mContext, WorkTaskService.class.getName())) {
            startService(mIntent);
        }
        bindService(mIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * @TODO DownloadService信息回传接口
     * @param null
     */
    private WorkTaskService.CallBack mWorkTaskCallBack = new WorkTaskService.CallBack() {

        @Override
        public void postWorkTaskInfo(WorkTaskInfo mWorkTaskInfo) {
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_NOT_STICKY;
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        createFloatView();
        registerMonitorReceiver(mContext);
        bindWorkTaskService();
        LogUtil.i(TAG, "---onCreate---");
    }

    private void createFloatView() {
        wmParams = new LayoutParams();
        wmParams.width = LayoutParams.MATCH_PARENT;
        wmParams.height = LayoutParams.MATCH_PARENT;
        wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(this);
        mFloatLayout = (RelativeLayout) inflater.inflate(R.layout.activity_loginfo, null);
        mWindowManager.addView(mFloatLayout, wmParams);

        wmParams.flags = LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowManager.updateViewLayout(mFloatLayout, wmParams);

        txt_memInfo = (TextView) mFloatLayout.findViewById(R.id.txt_logsize);

        log_path = isSaveToData() ? getFilesDir().getPath() + "/KtcTvLog.log" : KtcFileUtil.getKtcLogReportFolder(LogKeepAliveService.this) + "KtcTvLog.log";
        mHandler.sendEmptyMessage(MSG_UPDATE_STATUS);
    }

    /**
     * @param context
     * @TODO 注册系统时间变化广播接收器(每分钟)
     */
    private void registerMonitorReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        context.registerReceiver(TimeTickReceiver, intentFilter);
    }

    /**
     * 定义网络状态变化监听器
     */
    private BroadcastReceiver TimeTickReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context mContext, Intent mIntent) {
            String mAction = mIntent.getAction();
            LogUtil.i(TAG, "mAction:  " + mAction);
            switch (mAction) {
                case Intent.ACTION_TIME_TICK:
                    if (!Tools.isServiceWorking(mContext, WorkTaskService.class.getName())) {
                        LogUtil.i(TAG, "WorkTaskService   isNotServiceWorking");

                        Intent intent = new Intent(mContext, WorkTaskService.class);
                        mContext.startService(intent);
                    } else {
                        LogUtil.i(TAG, "WorkTaskService   isServiceWorking");
                        if (mWorkTaskBinder != null && !mWorkTaskBinder.isPrintLogWorking()) {
                            mWorkTaskBinder.startPrintLog(log_path);
                        }
                    }
                    break;

                default:
                    break;
            }
        }

    };

    @Override
    public void onDestroy() {
        mContext.unregisterReceiver(TimeTickReceiver);
        unbindService(mServiceConnection);
        if (mWorkTaskService != null) {
            mWorkTaskService.unRegisterCallBack(mWorkTaskCallBack);
        }

        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
        if (mHandler.hasMessages(MSG_UPDATE_STATUS)) {
            mHandler.removeMessages(MSG_UPDATE_STATUS);
        }
        super.onDestroy();
    }

    private boolean isSaveToData() {
        return SharedPreferencesUtil.getInstance().getBoolean(ConfigConstant.KEY_LOG_PATH);
    }

}