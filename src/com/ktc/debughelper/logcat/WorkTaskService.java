package com.ktc.debughelper.logcat;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.ktc.debughelper.util.LogUtil;
import com.ktc.debughelper.util.Tools;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * @author Arvin
 * @TODO 启动服务执行下载任务
 * @Date 2019.2.15
 */
public class WorkTaskService extends Service {
    private static final String TAG = "WorkTaskService";
    private static Context mContext;
    private boolean hasStartPrintLog = false;

    private WeakReference<WorkTaskService> mWorkTaskService = new WeakReference<>(WorkTaskService.this);
    private static Process logcatProcess;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        registerMonitorReceiver(mContext);
    }

    private WorkTaskBinder mWorkTaskBinder = new WorkTaskBinder();

    public class WorkTaskBinder extends Binder {

        /**
         * 开始WorkTaskService
         *
         * @return void
         */
        public void startPrintLog(String logPath) {
            new LogCollectorThread(logPath).start();

            hasStartPrintLog = true;
            checkKeepService();
        }

        /**
         * 开始WorkTaskService
         *
         * @return void
         */
        public void stopPrintLog() {
            hasStartPrintLog = false;
            stopPrintLogWork();
        }


        /**
         * 是否正在输出log日志
         *
         * @return boolean
         */
        public boolean isPrintLogWorking() {
            return Tools.hasMyUserLogcat(mContext);
        }

        /**
         * 获取WorkTaskService对象
         *
         * @return WorkTaskService
         */
        public WorkTaskService getServiceSelf() {
            return mWorkTaskService.get();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    //-----------for  logcat print  start--------------

    /**
     * 日志收集
     * 1.杀死应用程序已开启的Logcat进程防止多个进程写入一个日志文件
     * 2.开启日志收集进程
     */
    class LogCollectorThread extends Thread {
        private String logPath;

        public LogCollectorThread(String logPath) {
            super("LogCollectorThread");
            this.logPath = logPath;
        }

        @Override
        public void run() {
            try {
                killLogcatProcess();
                doPrintLogWork(logPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void doPrintLogWork(String logPath) {
        LogUtil.i(TAG, "doPrintLogWork:  " + logPath);
        if (TextUtils.isEmpty(logPath)) {
            return;
        }
        try {
            File logParentDir = new File(logPath.substring(0, logPath.lastIndexOf("/")));
            File logFile = new File(logPath);
            if (!logParentDir.exists()) {
                logParentDir.mkdirs();
            }
            if (logParentDir.exists() && !logFile.exists()) {
                logFile.createNewFile();
            }
            LogUtil.i(TAG, "startPrintLog_logFile:  " + logFile.exists());

            List<String> commandList = new ArrayList<String>();

            commandList.add("logcat");
            commandList.add("-v");
            commandList.add("time");
            commandList.add("-f");
            commandList.add(logPath);

            //commandList.add("*:I");
            //commandList.add("*:E");// 过滤所有的错误信息
            // 过滤指定TAG的信息
            // commandList.add("MyAPP:V");
            // commandList.add("*:S");

            Runtime.getRuntime().exec("logcat -G 200M");
            logcatProcess = Runtime.getRuntime().exec(
                    commandList.toArray(new String[commandList.size()]));
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
            LogUtil.i(TAG, "startPrintLog:  " + localIOException.toString());
        }
    }

    private void stopPrintLogWork() {
        // TODO Auto-generated method stub
    }

    //-----------for  logcat print  end--------------

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mWorkTaskBinder;
    }


    /**
     * 提供给activity的接口 因为存在一个服务绑定多个activity的情况 所以监听接口采用list装起来
     */
    public interface CallBack {
        void postWorkTaskInfo(WorkTaskInfo mWorkTaskInfo);
    }

    private List<CallBack> callBacks = new LinkedList<>();

    public void registerCallBack(CallBack callBack) {
        if (callBacks != null) {
            callBacks.add(callBack);
        }
    }

    /**
     * 注销接口 false注销失败
     *
     * @param callBack
     * @return
     */
    public boolean unRegisterCallBack(CallBack callBack) {
        if (callBacks != null && callBacks.contains(callBack)) {
            return callBacks.remove(callBack);
        }
        return false;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (logcatProcess != null) {
            logcatProcess.destroy();
        }

        unregisterReceiver(TimeTickReceiver);
        mContext.stopService(new Intent(mContext, LogKeepAliveService.class));
        hasStartPrintLog = false;
        super.onDestroy();
    }


    /**
     * 关闭由本程序开启的logcat进程：
     * 根据用户名称杀死进程(如果是本程序进程开启的Logcat收集进程那么两者的USER一致)
     * 如果不关闭会有多个进程读取logcat日志缓存信息写入日志文件
     *
     * @return
     */
    private void killLogcatProcess() {
        if (logcatProcess != null) {
            logcatProcess.destroy();
        }

        Tools.killMyUserLogcat(mContext);
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
                    if (hasStartPrintLog) {
                        checkKeepService();
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private void checkKeepService() {
        if (!Tools.isServiceWorking(mContext, LogKeepAliveService.class.getName())) {
            LogUtil.i(TAG, "LogKeepAliveService   isNotServiceWorking");

            Intent intent = new Intent(mContext, LogKeepAliveService.class);
            mContext.startService(intent);
        } else {
            LogUtil.i(TAG, "LogKeepAliveService   isServiceWorking");
        }
    }

}
