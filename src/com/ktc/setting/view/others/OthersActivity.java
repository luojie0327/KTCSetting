package com.ktc.setting.view.others;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.provider.Settings;
import android.os.SystemProperties;

import com.ktc.setting.view.base.BaseActivity;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.others.account.AccountFragment;
import com.ktc.setting.view.others.bluetooth.BluetoothFragment;
import com.ktc.setting.view.others.tts.TTSManager;

public class OthersActivity extends BaseActivity {

    public TTSManager mTTSManager;
    public BluetoothA2dp mBluetoothA2dp;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected BaseFragment getFragment() {
        mTTSManager = new TTSManager(this);
		if("true".equals(SystemProperties.get("persist.sys.bluetooth"))){
			initBluetoothProxy();
		}
        String action = getIntent().getAction();
        if (Settings.ACTION_BLUETOOTH_SETTINGS.equals(action)) {
            return new BluetoothFragment();
        } else if (Settings.ACTION_ADD_ACCOUNT.equals(action)
                || Settings.ACTION_SYNC_SETTINGS.equals(action)
                || "android.settings.ACCOUNT_SYNC_SETTINGS".equals(action)) {
            return new AccountFragment();
        }

        return new OthersFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mTTSManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTTSManager.release();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothA2dp != null){
        mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP,mBluetoothA2dp);
    }
	}
    private void initBluetoothProxy() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.getProfileProxy(this, mListener, BluetoothProfile.A2DP);
    }

    private BluetoothProfile.ServiceListener mListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.A2DP) {
                mBluetoothA2dp = null;
            }
        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.A2DP) {
                mBluetoothA2dp = (BluetoothA2dp) proxy;
            }
        }
    };
}
