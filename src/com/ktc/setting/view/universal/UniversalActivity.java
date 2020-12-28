package com.ktc.setting.view.universal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;

import com.ktc.setting.helper.VersionUtil;
import com.ktc.setting.view.base.BaseActivity;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.universal.datetime.DateTimeFragment;
import com.ktc.setting.view.universal.datetime.observe.TimeObservation;
import com.ktc.setting.view.universal.security.SecurityFragment;
import com.ktc.setting.view.universal.storage.StorageFragment;

public class UniversalActivity extends BaseActivity {

    public TimeObservation mTimeObservation;

    @Override
    protected BaseFragment getFragment() {
        initObserve();

        String action = getIntent().getAction();
        if (Settings.ACTION_INTERNAL_STORAGE_SETTINGS.equals(action)
                || Settings.ACTION_MEMORY_CARD_SETTINGS.equals(action)
                || Intent.ACTION_MANAGE_PACKAGE_STORAGE.equals(action)
                || "android.os.storage.action.MANAGE_STORAGE".equals(action)) {
            return new StorageFragment();
        } else if (Settings.ACTION_SECURITY_SETTINGS.equals(action)
                || Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES.equals(action)
                && VersionUtil.isCurrentAndroidOreoOrHigher()) {
            return new SecurityFragment();
        } else if (Settings.ACTION_DATE_SETTINGS.equals(action)) {
            return new DateTimeFragment();
        }

        return new UniversalFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timeReceiver);
    }

    private void initObserve() {
        mTimeObservation = new TimeObservation();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        mIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        mIntentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        mIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(timeReceiver, mIntentFilter);
    }

    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())
                    || Intent.ACTION_TIME_CHANGED.equals(intent.getAction())
                    || Intent.ACTION_DATE_CHANGED.equals(intent.getAction())
                    || Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                mTimeObservation.notifyObservers(intent);
            }
        }
    };

}
