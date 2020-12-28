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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.debughelper.logcat.WorkTaskInfo;
import com.ktc.debughelper.logcat.WorkTaskService;
import com.ktc.debughelper.util.ConfigConstant;
import com.ktc.debughelper.util.KtcFileUtil;
import com.ktc.debughelper.util.LogUtil;
import com.ktc.debughelper.util.SharedPreferencesUtil;
import com.ktc.debughelper.util.Tools;
import com.ktc.setting.R;

public class KtcDebugHomeActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "KtcHomeActivity";
    private Context mContext;

    private boolean hasMyUserLogcat = false;

    private LinearLayout ll_path_choose;
    private RadioGroup rg_path_choose;
    private RadioButton rb_path_usb, rb_path_data;

    private RelativeLayout rl_btn;
    private Button btn_cancel;
    private Button btn_stop;
    private TextView txt_tip, txt_count;

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

    private static final int MSG_COUNT_DOWN = 0x01;
    private static final int MSG_COUNT_DOWN_STOP = 0x02;
    private static final int MSG_UPDTAE_UI = 0x03;
    private int MAX_COUNT_DOWN = 3;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == MSG_COUNT_DOWN) {
                if (MAX_COUNT_DOWN < 0) {
                    txt_count.setVisibility(View.GONE);
                    txt_count.setText("");

                    if (mWorkTaskBinder != null) {// start printLog
                        //删除旧文件
                        String logFilePath = isSaveToData() ? getFilesDir().getPath() + "/KtcTvLog.log"
                                : KtcFileUtil.getKtcLogReportFolder(KtcDebugHomeActivity.this) + "KtcTvLog.log";
						/*File logFile = new File(logFilePath);
						if(logFile != null && logFile.exists()){
							logFile.delete();
						}*/
                        mWorkTaskBinder.startPrintLog(logFilePath);
						SharedPreferencesUtil.getInstance().putValues(
								new SharedPreferencesUtil.ContentValue(ConfigConstant.KEY_LOG_BENGINING, true));
                    }
                    finish();
                } else {
                    txt_count.setVisibility(View.VISIBLE);
                    txt_count.setText(MAX_COUNT_DOWN + "");
                    MAX_COUNT_DOWN--;
                    sendEmptyMessageDelayed(MSG_COUNT_DOWN, 1000);
                }
            } else if (what == MSG_COUNT_DOWN_STOP) {
                if (MAX_COUNT_DOWN < 0) {
                    txt_count.setVisibility(View.GONE);
                    txt_count.setText("");

                    Tools.killMyUserLogcat(mContext);
                    runOnUiThread(new Runnable() {
						@Override
						public void run() {
							unbindService(mServiceConnection);
                            if (mWorkTaskService != null) {
                                mWorkTaskService.unRegisterCallBack(mWorkTaskCallBack);
                            }
						}
					});
					SharedPreferencesUtil.getInstance().putValues(
							new SharedPreferencesUtil.ContentValue(ConfigConstant.KEY_LOG_BENGINING, false));
                    ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    mActivityManager.forceStopPackage(mContext.getPackageName());
                    finish();
                } else {
                    txt_count.setVisibility(View.VISIBLE);
                    txt_count.setText(MAX_COUNT_DOWN + "");
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
        setContentView(R.layout.activity_debug_home);
        mContext = this;
		bindWorkTaskService();

        new InitUITask().execute();
        findViews();
    }

    /**
     * 初始化控件
     */
    private void findViews() {

        txt_tip = (TextView) findViewById(R.id.txt_tip);
        txt_count = (TextView) findViewById(R.id.txt_count);

        ll_path_choose = (LinearLayout) findViewById(R.id.ll_path_choose);
        rg_path_choose = (RadioGroup) findViewById(R.id.rg_path_choose);
        rb_path_usb = (RadioButton) findViewById(R.id.rb_path_usb);
        rb_path_data = (RadioButton) findViewById(R.id.rb_path_data);

        rl_btn = (RelativeLayout) findViewById(R.id.rl_btn);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_stop = (Button) findViewById(R.id.btn_stop);

//        switchSavePath(true);//save to data
        boolean isSaveToData = isSaveToData();
        rb_path_usb.setChecked(!isSaveToData);
        rb_path_data.setChecked(isSaveToData);

        if (hasMyUserLogcat) {
            btn_cancel.setVisibility(View.VISIBLE);
            btn_stop.setVisibility(View.VISIBLE);
            btn_stop.requestFocus();

            ll_path_choose.setVisibility(View.GONE);

            txt_tip.setText(getResources().getString(R.string.str_tip_doing));
            btn_stop.setText(getResources().getString(R.string.str_stop));
        } else {
            btn_cancel.setVisibility(View.VISIBLE);
            btn_stop.setVisibility(View.VISIBLE);
            btn_stop.requestFocus();

            ll_path_choose.setVisibility(View.VISIBLE);

            txt_tip.setText(getResources().getString(R.string.str_tip_start));
            btn_stop.setText(getResources().getString(R.string.str_start));
        }

        btn_cancel.setOnClickListener(this);
        btn_stop.setOnClickListener(this);

        rl_btn.setVisibility(View.VISIBLE);

        rg_path_choose.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_path_usb:
                        switchSavePath(false);
                        break;
                    case R.id.rb_path_data:
                        switchSavePath(true);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        rl_btn.setVisibility(View.VISIBLE);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                finish();
                break;

            case R.id.btn_stop:
                if (hasMyUserLogcat) {
                    txt_tip.setText(isSaveToData() ? R.string.str_tip_data_finish : R.string.str_tip_usb_finish);

                    rl_btn.setVisibility(View.INVISIBLE);
                    mHandler.sendEmptyMessage(MSG_COUNT_DOWN_STOP);
                } else {
                    rl_btn.setVisibility(View.INVISIBLE);
                    ll_path_choose.setVisibility(View.GONE);
                    mHandler.sendEmptyMessage(MSG_COUNT_DOWN);
                }
                break;

            default:
                break;
        }
    }

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
		Log.d(TAG, "onDestroy");
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

        rb_path_data.setChecked(toData);
        rb_path_usb.setChecked(!toData);
    }

}
