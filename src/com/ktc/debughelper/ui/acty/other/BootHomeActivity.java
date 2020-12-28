package com.ktc.debughelper.ui.acty.other;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemProperties;

import com.ktc.debughelper.logcat.WorkTaskInfo;
import com.ktc.debughelper.logcat.WorkTaskService;
import com.ktc.debughelper.util.ConfigConstant;
import com.ktc.debughelper.util.KtcFileUtil;
import com.ktc.debughelper.util.KtcLogerUtil;
import com.ktc.debughelper.util.LogUtil;
import com.ktc.debughelper.util.SharedPreferencesUtil;
import com.ktc.debughelper.util.Tools;
import com.ktc.setting.R;

import java.io.File;

public class BootHomeActivity extends Activity {

    private static final String TAG = BootHomeActivity.class.getSimpleName();
    private Context mContext;

    private boolean hasMyUserLogcat = false;

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
     * TODO 执行日志抓取脚本
     *
     * @return void
     */
    private void startKtcLog() {
        File file = new File("/system/bin/ktcLog.sh");
        if (file.exists()) {
            try {
                KtcLogerUtil.I(TAG, "ktcLog.sh  exist!!!");
                SystemProperties.set("ctl.start", "ktcLog");
            } catch (Exception e) {
                e.printStackTrace();
                KtcLogerUtil.I(TAG, "startKtcLog--Exception:  " + e.toString());
            }
        } else {
            KtcLogerUtil.I(TAG, "ktcLog.sh  not exist!!!");
        }
    }

    private static final int MSG_COUNT_DOWN = 0x01;
    private static final int MSG_COUNT_DOWN_STOP = 0x02;
    private static final int MSG_UPDTAE_UI = 0x03;
    private int MAX_COUNT_DOWN = -1;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == MSG_COUNT_DOWN) {
                if (MAX_COUNT_DOWN < 0) {
                    if (mWorkTaskBinder != null) {// start printLog
                        //删除旧文件
                        String logFilePath = isSaveToData() ? getFilesDir().getPath() + "/KtcTvLog.log"
                                : KtcFileUtil.getKtcLogReportFolder(BootHomeActivity.this) + "KtcTvLog.log";
						/*File logFile = new File(logFilePath);
						if(logFile != null && logFile.exists()){
							logFile.delete();
						}*/
                        mWorkTaskBinder.startPrintLog(logFilePath);
                        //						startKtcLog();
                    }
                    finish();
                } else {
                    MAX_COUNT_DOWN--;
                    sendEmptyMessageDelayed(MSG_COUNT_DOWN, 1000);
                }
            } else if (what == MSG_COUNT_DOWN_STOP) {
                if (MAX_COUNT_DOWN < 0) {

                    Tools.killMyUserLogcat(mContext);
                    ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    mActivityManager.forceStopPackage(mContext.getPackageName());
                    finish();
                } else {
                    MAX_COUNT_DOWN--;
                    sendEmptyMessageDelayed(MSG_COUNT_DOWN_STOP, 1000);
                }
            } else if (what == MSG_UPDTAE_UI) {
                findViews();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boothome);
        mContext = this;

        bindWorkTaskService();

        new InitUITask().execute();
        findViews();
    }

    /**
     * 初始化控件
     */
    private void findViews() {

        if (hasMyUserLogcat) {
            mHandler.sendEmptyMessage(MSG_COUNT_DOWN_STOP);
        } else {
            mHandler.sendEmptyMessage(MSG_COUNT_DOWN);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * @param null
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
    protected void onStop() {
        super.onStop();
        if (mHandler.hasMessages(MSG_COUNT_DOWN)) {
            mHandler.sendEmptyMessage(MSG_COUNT_DOWN);
        }

        if (mHandler.hasMessages(MSG_COUNT_DOWN_STOP)) {
            mHandler.sendEmptyMessage(MSG_COUNT_DOWN_STOP);
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        if (mWorkTaskService != null) {
            mWorkTaskService.unRegisterCallBack(mWorkTaskCallBack);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        return;
    }


    class InitUITask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            hasMyUserLogcat = Tools.hasMyUserLogcat(mContext);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mHandler.sendEmptyMessage(MSG_UPDTAE_UI);
        }
    }

    private boolean isSaveToData() {
        return SharedPreferencesUtil.getInstance().getBoolean(
                ConfigConstant.KEY_LOG_PATH);
    }

    private void switchSavePath(boolean toData) {
        SharedPreferencesUtil.getInstance().putValues(
                new SharedPreferencesUtil.ContentValue(ConfigConstant.KEY_LOG_PATH, toData));
    }

}
