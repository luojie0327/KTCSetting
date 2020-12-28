package com.ktc.setting.view.others.bluetooth;

import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.setting.R;
import com.ktc.setting.helper.DestinyUtil;
import com.ktc.setting.helper.SpaceItemDecoration;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.ButtonSettingView;
import com.ktc.setting.view.custom.MaxHeightRecyclerView;
import com.ktc.setting.view.custom.SettingViewContainer;
import com.ktc.setting.view.custom.SwitchSettingView;
import com.ktc.setting.view.others.bluetooth.internal.BluetoothDevicePairer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BluetoothFragment extends BaseFragment implements SwitchSettingView.OnSwitchListener
        , View.OnClickListener, BluetoothPairedAdapter.OnConnectItemClickListener
        , BluetoothCanPairAdapter.OnPairItemClickListener, BluetoothDevicePairer.EventListener {

    private static final String TAG = BluetoothFragment.class.getSimpleName();
    private SettingViewContainer bluetoothContainer;
    private SwitchSettingView bluetoothSwitch;
    private TextView pairedText;
    private TextView canPairText;
    private MaxHeightRecyclerView pairedListView;
    private MaxHeightRecyclerView canPairListView;
    private RelativeLayout scanBar;
    private Button scanBtn;
    private LinearLayout waitBar;
    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> pairedList;
    private List<BluetoothDevice> canPairList;
    private BluetoothPairedAdapter mPairedAdapter;
    private BluetoothCanPairAdapter mCanPairAdapter;
    private BluetoothEventHandler mBluetoothEventHandler;
    private HashMap<String, BluetoothDevice> mAvailableDevices;
    private BluetoothHandler mBluetoothHandler;
    private BluetoothDevicePairer mPairer;
    private boolean mPairing = false;
    private final Object mLock = new Object();
    private boolean mPairingSuccess = false;
    private boolean mPairingBluetooth = false;
    private int mPreviousStatus = BluetoothDevicePairer.STATUS_NONE;
    private static final String ADDRESS_NONE = "NONE";

    @Override
    protected int getRes() {
        return R.layout.fragment_bluetooth;
    }

    @Override
    protected int getTitle() {
        return R.string.str_others_bluetooth_title;
    }

    @Override
    protected void initView(View view) {
        bluetoothContainer = (SettingViewContainer) view.findViewById(R.id.bluetooth_container);
        bluetoothSwitch = (SwitchSettingView) view.findViewById(R.id.bluetooth_switch);
        pairedText = (TextView) view.findViewById(R.id.bluetooth_paired_text);
        canPairText = (TextView) view.findViewById(R.id.bluetooth_can_pair_text);
        pairedListView = (MaxHeightRecyclerView) view.findViewById(R.id.bluetooth_paired_list_view);
        canPairListView = (MaxHeightRecyclerView) view.findViewById(R.id.bluetooth_can_pair_list_view);
        scanBar = (RelativeLayout) view.findViewById(R.id.bluetooth_scan_bar);
        scanBtn = (Button) view.findViewById(R.id.bluetooth_scan_btn);
        waitBar = (LinearLayout) view.findViewById(R.id.bluetooth_wait_bar);
        bluetoothContainer.setClipChildren(true);
        bluetoothContainer.setClipToPadding(false);
        pairedListView.setLayoutManager(new LinearLayoutManager(getContext()));
        canPairListView.setLayoutManager(new LinearLayoutManager(getContext()));
        pairedListView.addItemDecoration(new SpaceItemDecoration(DestinyUtil.dp2px(getContext(), 2)));
        canPairListView.addItemDecoration(new SpaceItemDecoration(DestinyUtil.dp2px(getContext(), 2)));
    }

    @Override
    protected void setFocus() {
        bluetoothContainer.setNewFocus(bluetoothSwitch);
    }

    @Override
    protected void initData() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothHandler = new BluetoothHandler(this);
        mBluetoothEventHandler = new BluetoothEventHandler(getContext(), mBluetoothHandler);
        mPairer = new BluetoothDevicePairer(getActivity(), this);
        boolean isBluetoothEnable = mBluetoothAdapter.isEnabled();
        bluetoothSwitch.setValueArray(getResources().getStringArray(R.array.str_array_common_switch));
        bluetoothSwitch.setIndex(isBluetoothEnable ? 0 : 1);
        pairedList = new ArrayList<>(mBluetoothAdapter.getBondedDevices());
        mPairedAdapter = new BluetoothPairedAdapter(getContext(), pairedList);
        pairedListView.setAdapter(mPairedAdapter);
        setVisible(isBluetoothEnable);
        if (pairedList.size() == 0) {
            pairedListView.setVisibility(View.GONE);
        }
        if (isBluetoothEnable) {
            startScan();
        }
        startBluetoothPairer();
    }

    @Override
    protected void addListener() {
        bluetoothSwitch.setOnSwitchListener(this);
        scanBtn.setOnClickListener(this);
        mPairedAdapter.setOnConnectItemClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bluetooth_scan_btn:
                startScan();
                break;
        }
    }

    @Override
    public void onSwitch(View view, int index) {
        switch (view.getId()) {
            case R.id.bluetooth_switch:
                if (index == 0) {
                    waitBar.setVisibility(View.VISIBLE);
                    mBluetoothAdapter.enable();
                } else {
                    mBluetoothAdapter.disable();
                    waitBar.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothEventHandler.release();
        mBluetoothHandler.removeCallbacksAndMessages(null);
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        stopBluetoothPairer();
    }

    private void setVisible(boolean visible) {
        if (visible) {
            pairedText.setVisibility(View.VISIBLE);
            pairedListView.setVisibility(View.VISIBLE);
            canPairText.setVisibility(View.VISIBLE);
            canPairListView.setVisibility(View.VISIBLE);
            scanBtn.setVisibility(View.VISIBLE);
        } else {
            pairedText.setVisibility(View.GONE);
            pairedListView.setVisibility(View.GONE);
            canPairText.setVisibility(View.GONE);
            canPairListView.setVisibility(View.GONE);
            scanBtn.setVisibility(View.GONE);
            scanBar.setVisibility(View.GONE);
        }
    }

    private void startScan() {
        //let other devices can discover
        mBluetoothAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
        canPairListView.setVisibility(View.GONE);
        scanBtn.setVisibility(View.GONE);
        scanBar.setVisibility(View.VISIBLE);
        mBluetoothAdapter.startDiscovery();
    }

    private void stopScan() {
        canPairListView.setVisibility(View.VISIBLE);
        scanBtn.setVisibility(View.VISIBLE);
        scanBar.setVisibility(View.GONE);
        if (mCanPairAdapter != null) {
            mCanPairAdapter.notifyDataSetChanged();
        }
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    public void onItemClick(View view, boolean isConnect, BluetoothDevice device) {
        FragmentManager fragmentManager = getFragmentManager();
        ConnectDialogFragment connectDialogFragment = new ConnectDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isConnect", isConnect);
        bundle.putParcelable("device", device);
        connectDialogFragment.setArguments(bundle);
        connectDialogFragment.show(fragmentManager, "connect_dialog");
    }

    private void refreshCanpairList(BluetoothDevice device) {
        if (canPairList == null || mCanPairAdapter == null) {
            return;
        }
        BluetoothDevice needRemove = null;
        for (BluetoothDevice bd : canPairList) {
            if (bd.getAddress().equals(device.getAddress())) {
                needRemove = bd;
                break;
            }
        }
        if (needRemove != null) {
            canPairList.remove(needRemove);
            mCanPairAdapter.notifyDataSetChanged();
            refreshFocus();
        }
    }

    @Override
    public void onPairItemClick(View view, BluetoothDevice device) {
        if (mPairer != null && !mPairer.isInProgress() && !mPairing) {
            if (view instanceof ButtonSettingView) {
                refreshFocus();
                ((ButtonSettingView) view).setEnabled(false);
            }
            mPairer.startPairing(device);
        }
    }


    private void startBluetoothPairer() {
        stopBluetoothPairer();
        mPairer = new BluetoothDevicePairer(getActivity(), this);
        mPairer.disableAutoPairing();
        mPairingSuccess = false;
        statusChanged();
    }

    private void stopBluetoothPairer() {
        if (mPairer != null) {
            mPairer.setListener(null);
            mPairer.dispose();
            mPairer = null;
        }
    }

    private void refreshFocus() {
        bluetoothSwitch.requestFocus();
    }

    @Override
    public void statusChanged() {
        synchronized (mLock) {
            if (mPairer == null) return;

            int numDevices = mPairer.getAvailableDevices().size();
            int status = mPairer.getStatus();
            int oldStatus = mPreviousStatus;
            mPreviousStatus = status;

            String address = mPairer.getTargetDevice() == null ? ADDRESS_NONE :
                    mPairer.getTargetDevice().getAddress();

            String state = "?";
            switch (status) {
                case BluetoothDevicePairer.STATUS_NONE:
                    state = "BluetoothDevicePairer.STATUS_NONE";
                    break;
                case BluetoothDevicePairer.STATUS_SCANNING:
                    state = "BluetoothDevicePairer.STATUS_SCANNING";
                    break;
                case BluetoothDevicePairer.STATUS_WAITING_TO_PAIR:
                    state = "BluetoothDevicePairer.STATUS_WAITING_TO_PAIR";
                    break;
                case BluetoothDevicePairer.STATUS_PAIRING:
                    state = "BluetoothDevicePairer.STATUS_PAIRING";
                    break;
                case BluetoothDevicePairer.STATUS_CONNECTING:
                    state = "BluetoothDevicePairer.STATUS_CONNECTING";
                    break;
                case BluetoothDevicePairer.STATUS_ERROR:
                    state = "BluetoothDevicePairer.STATUS_ERROR";
                    break;
            }
            long time = mPairer.getNextStageTime() - SystemClock.elapsedRealtime();
            Log.d(TAG, "Update received, number of devices:" + numDevices + " state: " +
                    state + " target device: " + address + " time to next event: " + time);
            Log.d(TAG, "status : " + status);
            switch (status) {
                case BluetoothDevicePairer.STATUS_NONE:
                    // if we just connected to something or just tried to connect
                    // to something, restart scanning just in case the user wants
                    // to pair another device.
                    if (oldStatus == BluetoothDevicePairer.STATUS_CONNECTING) {
                        if (mPairingSuccess) {
                            // Pairing complete

                            // Done, return here and just wait for the message
                            // to close the activity
                            return;
                        }
                        Log.d(TAG, "Invalidating and restarting.");

                        mPairer.invalidateDevice(mPairer.getTargetDevice());
                        mPairer.start();
                        mPairer.cancelPairing();
                        setPairingBluetooth(false);

                        // if this looks like a successful connection run, reflect
                        // this in the UI, otherwise use the default message
                        if (!mPairingSuccess && BluetoothDevicePairer.hasValidInputDevice(getActivity())) {
                            mPairingSuccess = true;
                        }
                    }
                    break;
                case BluetoothDevicePairer.STATUS_SCANNING:
                    mPairingSuccess = false;
                    break;
                case BluetoothDevicePairer.STATUS_WAITING_TO_PAIR:
                    break;
                case BluetoothDevicePairer.STATUS_PAIRING:
                    // reset the pairing success value since this is now a new
                    // pairing run
                    mPairingSuccess = true;
                    break;
                case BluetoothDevicePairer.STATUS_CONNECTING:
                    Log.d(TAG, "STATUS_CONNECTING");
                    break;
                case BluetoothDevicePairer.STATUS_ERROR:
                    mPairingSuccess = false;
                    setPairingBluetooth(false);
                    break;
            }
        }
    }

    private void setPairingBluetooth(boolean pairing) {
        if (mPairingBluetooth != pairing) {
            mPairingBluetooth = pairing;
        }
    }

    static class BluetoothHandler extends Handler {

        WeakReference<BluetoothFragment> mFragmentWeakReference;
        BluetoothFragment mBluetoothFragment;

        BluetoothHandler(BluetoothFragment bluetoothFragment) {
            mFragmentWeakReference = new WeakReference<>(bluetoothFragment);
            mBluetoothFragment = mFragmentWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            Intent intent = (Intent) msg.obj;
            switch (msg.what) {
                case Constants.STATE_ON:
                    mBluetoothFragment.setVisible(true);
                    mBluetoothFragment.waitBar.setVisibility(View.GONE);
                    mBluetoothFragment.pairedList.clear();
                    mBluetoothFragment.pairedList.addAll(new ArrayList<>(mBluetoothFragment.mBluetoothAdapter.getBondedDevices()));
                    mBluetoothFragment.mPairedAdapter.notifyDataSetChanged();
                    mBluetoothFragment.startScan();
                    break;
                case Constants.STATE_OFF:
                    mBluetoothFragment.setVisible(false);
                    mBluetoothFragment.waitBar.setVisibility(View.GONE);
                    break;
                case Constants.ACTION_FOUND:
                    BluetoothDevice device = ((Intent) msg.obj).getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED && device.getName() != null) {
                        if (mBluetoothFragment.mAvailableDevices != null &&
                                !mBluetoothFragment.mAvailableDevices.containsKey(device.getAddress())) {
                            mBluetoothFragment.mAvailableDevices.put(device.getAddress(), device);
                        }
                    }
                    break;
                case Constants.ACTION_DISCOVERY_FINISHED:
                    if (mBluetoothFragment.mAvailableDevices != null
                            && mBluetoothFragment.mAvailableDevices.size() > 0) {
                        mBluetoothFragment.canPairList = new ArrayList<>(mBluetoothFragment.mAvailableDevices.values());
                        mBluetoothFragment.mCanPairAdapter = new BluetoothCanPairAdapter(mBluetoothFragment.getContext(),
                                mBluetoothFragment.canPairList);
                        mBluetoothFragment.mCanPairAdapter.setOnPairItemClickListener(mBluetoothFragment);
                        mBluetoothFragment.canPairListView.setAdapter(mBluetoothFragment.mCanPairAdapter);
                    }
                    mBluetoothFragment.stopScan();
                    break;
                case Constants.ACTION_DISCOVERY_STARTED:
                    mBluetoothFragment.mAvailableDevices = new HashMap<>();
                    break;
                case Constants.ACTION_BOND_STATE_CHANGED:
                    BluetoothDevice bondDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (bondDevice == null) return;
                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                            BluetoothDevice.ERROR);
                    Log.d(TAG, "state : " + bondState);
                    if (bondState == BluetoothDevice.BOND_BONDING) {
                        mBluetoothFragment.mPairing = true;
                        return;
                    }
                    if (bondState == BluetoothDevice.BOND_NONE) {
                        if (mBluetoothFragment.pairedList.contains(bondDevice)) {
                            mBluetoothFragment.pairedList.remove(bondDevice);
                        }
                        mBluetoothFragment.mPairedAdapter.notifyDataSetChanged();
                        mBluetoothFragment.refreshFocus();
                        if (mBluetoothFragment.pairedList.size() == 0) {
                            mBluetoothFragment.pairedListView.setVisibility(View.GONE);
                        }
                    } else if (bondState == BluetoothDevice.BOND_BONDED) {
                        mBluetoothFragment.pairedListView.setVisibility(View.VISIBLE);
                        mBluetoothFragment.pairedList.add(bondDevice);
                        mBluetoothFragment.mPairedAdapter.notifyDataSetChanged();
                        mBluetoothFragment.refreshFocus();
                        mBluetoothFragment.refreshCanpairList(bondDevice);
                    }
                    mBluetoothFragment.mPairing = false;
                    break;
                case Constants.ACTION_CONNECTION_STATE_CHANGED:
                    mBluetoothFragment.mPairedAdapter.notifyDataSetChanged();
                    mBluetoothFragment.refreshFocus();
                    break;
            }
        }
    }
}