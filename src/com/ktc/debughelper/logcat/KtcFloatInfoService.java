package com.ktc.debughelper.logcat;


import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ktc.debughelper.bean.RecentAppInfo;
import com.ktc.debughelper.util.KtcAppManagerUtil;
import com.ktc.debughelper.util.KtcLogerUtil;
import com.ktc.debughelper.util.KtcSystemUtil;
import com.ktc.setting.R;

import java.util.List;


/**
 * @author Arvin
 * @TODO 系统信息显示
 * @since 2018.11.26
 */
public class KtcFloatInfoService extends Service {

    private static final String TAG = "KtcFloatInfoService";
    private static final int MSG_FOCUS_ENABLE = 0x01;
    private static final int MSG_FOCUS_DISABLE = 0x02;
    private static final int MSG_UPDATE_FLOATINFO = 0x03;
    private static final String KEY_GET_CPUINFO = "KEY_GET_CPUINFO";
    private static final String KEY_GET_MEMINFO = "KEY_GET_MEMINFO";
    private static final String KEY_GET_TASKS = "KEY_GET_TASKS";
    private static final String INTENT_TEST_STOP_FLOATINFO = "ktc.test.stop.floatinfo";
    private static final int UPDATE_INTERVAL = 3000;//3000ms
    private Service service;
    private LinearLayout mFloatLayout;
    private LayoutParams wmParams;
    private WindowManager mWindowManager;
    private TextView txt_memInfo, txt_cpuInfo, txt_tasks;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            KtcLogerUtil.I(TAG, "---handleMessage---:   " + msg);
            switch (msg.what) {
                case MSG_FOCUS_ENABLE://重获焦点
                    wmParams.flags = LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                    mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                    break;
                case MSG_UPDATE_FLOATINFO:
                    Bundle mBundle = msg.getData();
                    txt_cpuInfo.setText(mBundle.getString(KEY_GET_CPUINFO, "NO Data"));
                    txt_memInfo.setText(mBundle.getString(KEY_GET_MEMINFO, "NO Data"));
                    txt_tasks.setText(mBundle.getString(KEY_GET_TASKS, "NO Data"));
                    break;

                default:
                    break;
            }
        }
    };
    private KtcAppManagerUtil mKtcAppManagerUtil;
    private KtcSystemUtil mKtcSystemUtil;
    private List<RecentAppInfo> mRecentAppInfoList;
    private UpdateFloatInfoThread mUpdateFloatInfoThread;
    private boolean isAllowUpdate = true;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            KtcLogerUtil.I(TAG, "mBroadcastReceiver:  " + intent.getAction());
            if (intent.getAction().equals(INTENT_TEST_STOP_FLOATINFO)) {
                stopUpdateThread();
                stopSelf();
            }
        }
    };

    public KtcFloatInfoService() {
        service = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mKtcAppManagerUtil = KtcAppManagerUtil.getInstance(this);
        mKtcSystemUtil = KtcSystemUtil.getInstance(this);
        createFloatView();
        KtcLogerUtil.I(TAG, "---onCreate---");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        KtcLogerUtil.I(TAG, "---onStartCommand---");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
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
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.activity_floatinfo, null);
        mWindowManager.addView(mFloatLayout, wmParams);

        wmParams.flags = LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowManager.updateViewLayout(mFloatLayout, wmParams);

        findViews();
    }

    private void findViews() {

        txt_memInfo = (TextView) mFloatLayout.findViewById(R.id.txt_meminfo);
        txt_cpuInfo = (TextView) mFloatLayout.findViewById(R.id.txt_cpuinfo);
        txt_tasks = (TextView) mFloatLayout.findViewById(R.id.txt_tasks);

        mUpdateFloatInfoThread = new UpdateFloatInfoThread();
        mUpdateFloatInfoThread.start();

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(INTENT_TEST_STOP_FLOATINFO);
        registerReceiver(mBroadcastReceiver, mFilter);
    }

    @Override
    public void onDestroy() {
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
        stopUpdateThread();
        super.onDestroy();
    }

    private void stopUpdateThread() {
        isAllowUpdate = false;
        if (mUpdateFloatInfoThread != null) {
            mUpdateFloatInfoThread.interrupt();
            try {
                mUpdateFloatInfoThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                mUpdateFloatInfoThread.interrupt();
            }
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    private String getRecentTasks() {
        try {
            mRecentAppInfoList = mKtcAppManagerUtil.getRecentTasks();
            StringBuffer sb = new StringBuffer();
            for (RecentAppInfo mRecentAppInfo : mRecentAppInfoList) {
                sb.append(mRecentAppInfo.processName + "\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    ;

    /**
     * 获取当前CPU信息
     */
    private String getCPUInfo() {
        try {
            return mKtcSystemUtil.getCurCpuInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @return String
     * @TODO 获取系统当前的运行内存信息(含总运存及可用运存)
     */
    private String getSystemMemoryInfo() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统支持应用运行的可用运行内存  (MemFree+Buffers+Cached)
        //mi.totalMem; 当前系统的总运行内存
        double precent = (double) mi.availMem / (double) mi.totalMem;
        return "totalMem:  " + mi.totalMem + " : " + mi.totalMem / 1024 / 1024
                + "Mb\navailMem:  " + mi.availMem + " : " + mi.availMem / 1024 / 1024
                + "Mb\nprecent:  " + precent + " : " + (int) (precent * 100) + "%";
    }

    /**
     * 子线程：获取当前系统运行的相关信息
     */
    private class UpdateFloatInfoThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted() && isAllowUpdate) {
                isAllowUpdate = true;
                try {
                    Message msg = new Message();
                    msg.what = MSG_UPDATE_FLOATINFO;
                    Bundle mBundle = new Bundle();
                    mBundle.putString(KEY_GET_CPUINFO, getCPUInfo());
                    mBundle.putString(KEY_GET_MEMINFO, getSystemMemoryInfo());
                    mBundle.putString(KEY_GET_TASKS, getRecentTasks());
                    msg.setData(mBundle);
                    mHandler.sendMessage(msg);

                    sleep(UPDATE_INTERVAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
