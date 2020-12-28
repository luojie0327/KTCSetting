package com.ktc.setting.view.network.wifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ktc.setting.R;
import com.ktc.setting.helper.NetworkHelper;
import com.ktc.setting.model.WifiItem;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.ButtonSettingView;
import com.ktc.setting.view.custom.CustomDialog;
import com.ktc.setting.view.custom.MaxHeightRecyclerView;
import com.ktc.setting.view.custom.SettingViewContainer;
import com.ktc.setting.view.custom.SwitchSettingView;
import com.ktc.setting.view.custom.ToastFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.net.wifi.SupplicantState.DORMANT;


public class WifiFragment extends BaseFragment implements ViewTreeObserver.OnGlobalLayoutListener {
    public static final String EXTRA_CONNECT = "connect_extra";
    public static final String EXTRA_ITEM = "item_extra";

    private static final int SCAN_INTERVAL = 10 * 1000;
    private static final int TIMEOUT_TIME = 15 * 1000;

    public static final int RESULT_SUCCESS = 0;
    private static final int SET_LIST_FOCUS = 1;
    private static final int SET_ADD_FOCUS = 2;
    private static final int SCROLL_TOP = 3;
    private static final int MSG_TIMEOUT = 4;
    private static final int SCAN_WIFI = 0;
    public static final int RESULT_UNKNOWN_ERROR = 1;
    public static final int RESULT_TIMEOUT = 2;
    public static final int RESULT_BAD_AUTHENTICATION = 3;
    public static final int RESULT_REJECTED_BY_AP = 4;
    public static final int RESULT_ERROR_PASSWORD = 5;

    private NetworkHelper mNetworkHelper;
    private List<WifiItem> mResults = new ArrayList<WifiItem>();
    private LinearLayoutManager mManager;
    private WifiAdapter mAdapter;
    private MyHandler mHandler;
    private int mLastPosition = -1;
    private WifiItem mItem;
    private boolean mWasAssociating;
    private boolean mWasAssociated;
    private boolean mWasHandshaking;
    private boolean mFromConnectWifi = false;
    private boolean mFromAddWifi = false;
    private boolean mClickConnect = false;
    private BroadcastReceiver mReceiver;

    private SettingViewContainer mContainer;
    private SwitchSettingView mWifiSwitch;
    private MaxHeightRecyclerView mWifiListView;
    private ButtonSettingView mAddWifiButton;
    private ProgressBar mProgressBar;

    @Override
    protected int getRes() {
        return R.layout.fragment_wifi;
    }

    @Override
    protected int getTitle() {
        return R.string.title_wifi;
    }

    @Override
    protected void initView(View view) {
        mContainer = (SettingViewContainer) view.findViewById(R.id.wifi_container);
        mContainer.setClipChildren(true);
        mContainer.setClipToPadding(false);
        mWifiSwitch = (SwitchSettingView) view.findViewById(R.id.switch_wifi);
        mAddWifiButton = (ButtonSettingView) view.findViewById(R.id.button_add_wifi);
        mProgressBar = (ProgressBar) view.findViewById(R.id.wifi_progress_bar);

        mWifiListView = (MaxHeightRecyclerView) view.findViewById(R.id.list_wifi);
        mManager = new LinearLayoutManager(mActivity);
        mWifiListView.setLayoutManager(mManager);
        mWifiListView.addItemDecoration(new SpaceItemDecoration(
                (int) (getResources().getDimension(R.dimen.space_recycler_item))));
        mWifiListView.setAnimation(null);
        mWifiListView.setClipToPadding(false);
    }

    @Override
    protected void setFocus() {
        if (mFromConnectWifi && !mClickConnect) {
            //mHandler.sendEmptyMessageDelayed(SET_LIST_FOCUS, 50);
            mWifiListView.addOnChildAttachStateChangeListener(
                    new RecyclerView.OnChildAttachStateChangeListener() {
                        @Override
                        public void onChildViewAttachedToWindow(View view) {
                            int index = mWifiListView.indexOfChild(view);
                            if (mLastPosition == index) {
                                mContainer.setNewFocus(view);
                                mContainer.onGlobalFocusChanged(null, view);
                                mWifiListView.removeOnChildAttachStateChangeListener(this);
                                mContainer.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mContainer.reAttachView();
                                    }
                                }, 50);
                            }

                            if (index == mManager.findLastVisibleItemPosition()) {
                                mWifiListView.removeOnChildAttachStateChangeListener(this);
                            }
                        }

                        @Override
                        public void onChildViewDetachedFromWindow(View view) {

                        }
                    });


        } else if (mFromAddWifi && !mClickConnect) {
            mAddWifiButton.setVisibility(View.VISIBLE);
            mAddWifiButton.requestFocus();
            mContainer.setNewFocus(mAddWifiButton);
            mContainer.onGlobalFocusChanged(null, mAddWifiButton);
            mContainer.reAttachView();
        } else {
            mContainer.setNewFocus(mWifiSwitch);
            mWifiSwitch.requestFocus();
            mWifiSwitch.requestFocusFromTouch();

            if (mClickConnect) {
                mHandler.sendEmptyMessageDelayed(SCROLL_TOP, 50);
            }
        }
    }

    @Override
    protected void initData() {
        mNetworkHelper = NetworkHelper.getInstance(getActivity());
        mHandler = new MyHandler(this);

        refreshUI(mNetworkHelper.isWifiOpen() ?
                WifiManager.WIFI_STATE_ENABLED : WifiManager.WIFI_STATE_DISABLED);

        if ((!mFromConnectWifi && !mFromAddWifi) || mAdapter == null) {
            if (mNetworkHelper.isWifiOpen()) {
                mNetworkHelper.startScan();
                mProgressBar.setVisibility(View.VISIBLE);
            }
        } else {
            mWifiListView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mAddWifiButton.setVisibility(View.VISIBLE);
        }
        mAdapter = new WifiAdapter(mResults);
        mWifiListView.setAdapter(mAdapter);
        refreshWifiList();
    }

    private void refreshUI(int state) {
        switch (state) {
            case WifiManager.WIFI_STATE_DISABLED:
                mWifiSwitch.setEnabled(true);
                mWifiSwitch.requestFocus();
                mWifiSwitch.requestFocusFromTouch();
                mWifiSwitch.setIndex(0);
                mAddWifiButton.setVisibility(View.GONE);
                mWifiListView.setVisibility(View.GONE);
                break;

            case WifiManager.WIFI_STATE_ENABLED:
                mWifiSwitch.setEnabled(true);
                if (!mFromAddWifi && !mFromConnectWifi) {
                    mWifiSwitch.requestFocus();
                    mWifiSwitch.requestFocusFromTouch();
                }
                mWifiSwitch.setIndex(1);
                mNetworkHelper.startScan();
                mProgressBar.setVisibility(View.VISIBLE);
                break;

            default:
                mAddWifiButton.setVisibility(View.GONE);
                mWifiListView.setVisibility(View.GONE);
                mWifiSwitch.setEnabled(false);
                break;
        }
    }

    private void refreshWifiList() {
        mResults.clear();
        mResults.addAll(mNetworkHelper.getAvailableNetworks());
        if (mResults.size() == 0) {
            mNetworkHelper.startScan();
        }
        mAddWifiButton.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
        mAddWifiButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void addListener() {
        mWifiSwitch.setOnSwitchListener(new SwitchSettingView.OnSwitchListener() {
            @Override
            public void onSwitch(View view, int index) {
                if (index == 1) {
                    mNetworkHelper.setWifiOpen(true);
                } else {
                    mNetworkHelper.setWifiOpen(false);
                }
                mAddWifiButton.setVisibility(View.GONE);
                mWifiListView.setVisibility(View.GONE);
                mWifiSwitch.setEnabled(false);
            }
        });

        mAddWifiButton.setOnButtonClickListener(new ButtonSettingView.OnButtonClickListener() {
            @Override
            public void onClick(View view) {
                AddWifiFragment fragment = new AddWifiFragment();
                fragment.setTargetFragment(WifiFragment.this, 101);
                mActivity.newFragment(fragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAddWifiButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mReceiver = new WifiBroadcastReceiver();
        mActivity.registerReceiver(mReceiver, filter);

        if (!mHandler.hasMessages(SCAN_WIFI)) {
            mHandler.sendEmptyMessageDelayed(SCAN_WIFI, SCAN_INTERVAL);
        }

        mWasAssociating = false;
        mWasAssociated = false;
        mWasHandshaking = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mActivity.unregisterReceiver(mReceiver);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        } else {
            if (requestCode == 100) {
                mFromConnectWifi = true;
                mFromAddWifi = false;
            } else if (requestCode == 101) {
                mFromAddWifi = true;
                mFromConnectWifi = false;
            }
            mClickConnect = data.getBooleanExtra(EXTRA_CONNECT, false);
            mItem = data.getParcelableExtra(EXTRA_ITEM);
            setFocus();
        }
    }

    @Override
    public void onGlobalLayout() {
        View focus = mActivity.getCurrentFocus();
        if (focus == mAddWifiButton) {
            mContainer.onGlobalFocusChanged(null, focus);
        }

        for (int i = 0; i < mManager.findLastVisibleItemPosition() - mManager.findFirstVisibleItemPosition(); i++) {
            View item = mWifiListView.getChildAt(i);
            if (item != null && item != focus) {
                if (item.getScaleX() > 1.0f) {
                    item.animate().scaleX(1.0f).translationZ(0).setDuration(0).start();
                }
            }
        }
        mAddWifiButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private void updateList(String bssid, SupplicantState state) {
        for (int position = 0; position < mResults.size() - 1; position++) {
            if (TextUtils.equals(bssid, mResults.get(position).getBSSID())) {
                WifiItem item = mResults.get(position);
                if (position != 0) {
                    mResults.remove(position);
                    mResults.add(0, item);
                }
                if (state != SupplicantState.COMPLETED) {
                    item.setConnectedState(WifiItem.STATE_CONNECTING);
                }
                mItem = item;

                mAddWifiButton.getViewTreeObserver().addOnGlobalLayoutListener(this);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showToast(int reason) {
        int resId = 0;
        switch (reason) {
            case RESULT_SUCCESS:
                break;
            case RESULT_UNKNOWN_ERROR:
                resId = R.string.network_wifi_result_error;
                break;
            case RESULT_TIMEOUT:
                resId = R.string.network_wifi_result_timeout;
                break;
            case RESULT_BAD_AUTHENTICATION:
                resId = R.string.network_wifi_result_authentic_fail;
                break;
            case RESULT_REJECTED_BY_AP:
                resId = R.string.network_wifi_result_reject;
                break;
            case RESULT_ERROR_PASSWORD:
                resId = R.string.network_wifi_pw_error;
        }
        ToastFactory.showToast(mActivity, mActivity.getString(resId)
                , Toast.LENGTH_SHORT);
    }

    private void goToConnectFragment(WifiItem item) {
        ConnectWifiFragment fragment = ConnectWifiFragment.newInstance(item);
        fragment.setTargetFragment(WifiFragment.this, 100);
        mActivity.newFragment(fragment);
    }

    static class MyHandler extends Handler {
        WeakReference<WifiFragment> mWeakReference;

        public MyHandler(WifiFragment fragment) {
            mWeakReference = new WeakReference<WifiFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final WifiFragment fragment = mWeakReference.get();
            if (fragment != null) {
                switch (msg.what) {
                    case SCAN_WIFI:
                        fragment.mNetworkHelper.startScan();
                        break;

                    case SET_LIST_FOCUS:
                        int position = fragment.mLastPosition -
                                ((LinearLayoutManager) fragment.mWifiListView.getLayoutManager())
                                        .findFirstVisibleItemPosition();
                        View v = fragment.mWifiListView.getChildAt(position);
                        if (v != null) {
                            fragment.mContainer.onGlobalFocusChanged(null, v);
                        } else {
                            fragment.mContainer.onGlobalFocusChanged(null, fragment.mWifiSwitch);
                        }
                        break;

                    case SET_ADD_FOCUS:
                        fragment.mContainer.onGlobalFocusChanged(null, fragment.mAddWifiButton);
                        break;

                    case SCROLL_TOP:
                        fragment.mWifiListView.smoothScrollToPosition(0);
                        break;

                    case MSG_TIMEOUT:
                        if (!fragment.mNetworkHelper.isWifiConnected()) {
                            fragment.mNetworkHelper.disableWifi();
                            fragment.showToast(RESULT_TIMEOUT);
                        }
                        break;
                }
            }
        }
    }

    private class WifiBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                int preState = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                refreshUI(state);
                mHandler.removeMessages(MSG_TIMEOUT);
                mFromAddWifi = false;
                mFromConnectWifi = false;

            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (mItem != null
                        && mItem.getSSID().equals(WifiInfo.removeDoubleQuotes(networkInfo.getExtraInfo()))) {
                    mHandler.removeMessages(MSG_TIMEOUT);
                    mWasAssociating = false;
                    mWasAssociated = false;
                    mWasHandshaking = false;
                }
                if (mNetworkHelper.isWifiOpen()) {
                    refreshWifiList();
                }

            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    refreshWifiList();
                    mWifiListView.setVisibility(View.VISIBLE);
                    if (!mHandler.hasMessages(SCAN_WIFI)) {
                        mHandler.sendEmptyMessageDelayed(SCAN_WIFI, SCAN_INTERVAL);
                    }
                } else {
                    mHandler.sendEmptyMessage(SCAN_WIFI);
                }

            } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
                WifiInfo wifiInfo = mNetworkHelper.getConnectionInfo();
                updateList(wifiInfo.getBSSID(), state);
                if (mItem == null) {
                    return;
                }

                if (error == WifiManager.ERROR_AUTHENTICATING) {
                    mNetworkHelper.forgetWifi(mItem.getSSID(), mItem.getSecurity());
                    mWasAssociating = false;
                    mWasAssociated = false;
                    mWasHandshaking = false;
                    refreshWifiList();
                    showToast(RESULT_ERROR_PASSWORD);
                    mHandler.removeMessages(MSG_TIMEOUT);
                    goToConnectFragment(mItem);
                    return;
                }

                switch (state) {
                    case ASSOCIATING:
                        mWasAssociating = true;
                        break;
                    case ASSOCIATED:
                        mWasAssociated = true;
                        break;
                    case FOUR_WAY_HANDSHAKE:
                    case GROUP_HANDSHAKE:
                        mWasHandshaking = true;
                        break;
                    case COMPLETED:
                        // this just means the supplicant has connected, now
                        // we wait for the rest of the framework to catch up
                        break;
                    case DISCONNECTED:
                    case DORMANT:
                        // MStar Android Patch Begin
                        // Becase the Shared Key Authentication for wep  has not
                        // 4-ways handshaking certification process. It will return
                        // an ASSOC-REJECT, and make supplicant return a disconnected status,
                        // when it is in Associating stage. So when mWasAssociating is true and
                        // mWasAssociated is false, users input a wrong password.
                        if (mNetworkHelper.getSecurity(wifiInfo) == NetworkHelper.SECURITY_NONE
                                && mWasAssociating && !mWasAssociated) {
                            mNetworkHelper.disableWifi();
                            mWasAssociating = false;
                            mWasAssociated = false;
                            mWasHandshaking = false;
                            if (state == DORMANT){
                                showToast(RESULT_BAD_AUTHENTICATION);
                            }
                        } else if (mWasAssociated || mWasHandshaking) {
                            // MStar Android Patch End
                            mNetworkHelper.disableWifi();
                            if (state == DORMANT){
                                showToast(mWasHandshaking ? RESULT_BAD_AUTHENTICATION : RESULT_UNKNOWN_ERROR);
                            }
                            mWasAssociating = false;
                            mWasAssociated = false;
                            mWasHandshaking = false;
                        }
                        if (wifiInfo.getNetworkId() == WifiConfiguration.INVALID_NETWORK_ID)
                            return;
                        break;
                    case INTERFACE_DISABLED:
                    case UNINITIALIZED:
                        //showToast(RESULT_UNKNOWN_ERROR);
                        break;
                    case INACTIVE:
                        if (mWasAssociating && !mWasAssociated) {
                            // If we go inactive after 'associating' without ever having
                            // been 'associated', the AP(s) must have rejected us.
                            mNetworkHelper.disableWifi();
                            mWasAssociating = false;
                            mWasAssociated = false;
                            mWasHandshaking = false;
                            showToast(RESULT_REJECTED_BY_AP);
                            break;
                        }
                    case INVALID:
                    case SCANNING:
                    default:
                        return;
                }
                mHandler.removeMessages(MSG_TIMEOUT);
                mHandler.sendEmptyMessageDelayed(MSG_TIMEOUT, TIMEOUT_TIME);

            } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
                if (mNetworkHelper.isWifiOpen()) {
                    refreshWifiList();
                }
            }
        }
    }

    private class WifiListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private int[] SIGNAL_QUALITY_WITH_LOCK = {
                R.drawable.wifi_lock_0, R.drawable.wifi_lock_1, R.drawable.wifi_lock_2,
                R.drawable.wifi_lock_3, R.drawable.wifi_lock_4
        };

        private int[] SIGNAL_QUALITY_WITHOUT_LOCK = {
                R.drawable.wifi_unlock_0, R.drawable.wifi_unlock_1, R.drawable.wifi_unlock_2,
                R.drawable.wifi_unlock_3, R.drawable.wifi_unlock_4
        };

        private WifiItem mWifiItem;

        private ImageView mIcon;
        private TextView mName;
        private TextView mState;

        public WifiListHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.view_item_wifi, parent, false));

            mIcon = (ImageView) itemView.findViewById(R.id.lock_icon);
            mName = (TextView) itemView.findViewById(R.id.wifi_name);
            mState = (TextView) itemView.findViewById(R.id.wifi_state);

            itemView.setOnClickListener(this);
            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mName.setSelected(hasFocus);
                }
            });
            //            itemView.setOnKeyListener(new View.OnKeyListener() {
            //                @Override
            //                public boolean onKey(View v, int keyCode, KeyEvent event) {
            //                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP
            //                            || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            //                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            //                            mHandler.removeMessages(SCAN_WIFI);
            //                        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            //                            mHandler.sendEmptyMessageDelayed(SCAN_WIFI, SCAN_INTERVAL);
            //                        }
            //                    }
            //
            //                    return false;
            //                }
            //            });

            //            itemView.setOnHoverListener(new View.OnHoverListener() {
            //                @Override
            //                public boolean onHover(View v, MotionEvent event) {
            //                    itemView.requestFocus();
            //                    itemView.requestFocusFromTouch();
            //                    return true;
            //                }
            //            });
        }

        public void bind(WifiItem item) {
            mWifiItem = item;
            int security = mWifiItem.getSecurity();

            mName.setText(mWifiItem.getSSID());
            if (security == NetworkHelper.SECURITY_NONE) {
                mIcon.setImageResource(SIGNAL_QUALITY_WITHOUT_LOCK[mWifiItem.getLevel()]);
            } else {
                mIcon.setImageResource(SIGNAL_QUALITY_WITH_LOCK[mWifiItem.getLevel()]);
            }

            int connectedState = mWifiItem.getConnectedState();
            if (connectedState == WifiItem.STATE_CONNECTED) {
                mState.setText(R.string.network_connected);
            } else if (connectedState == WifiItem.STATE_CONNECTING) {
                mState.setText(R.string.network_connecting);
            } else if (mWifiItem.isSaved()) {
                mState.setText(R.string.network_saved);
            } else if (security != NetworkHelper.SECURITY_NONE) {
                mState.setText(getResources().getStringArray(R.array.security_type)[security]);
            }
        }

        @Override
        public void onClick(View view) {
            mLastPosition = getLayoutPosition() - mManager.findFirstVisibleItemPosition();

            if (mWifiItem.getConnectedState() == WifiItem.STATE_CONNECTED) {
                EditWifiFragment fragment = EditWifiFragment.newInstance(mWifiItem);
                fragment.setTargetFragment(WifiFragment.this, 100);
                mActivity.newFragment(fragment);

            } else if (mWifiItem.isSaved()) {
                final CustomDialog dialog = new CustomDialog(mActivity);
                dialog.setTitle(mWifiItem.getSSID());
                dialog.setLeftButtonText(getString(R.string.network_connect));
                dialog.setRightButtonText(getString(R.string.network_forget));
                dialog.setLeftButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mNetworkHelper.connectSavedWifi(mWifiItem.getSSID(), mWifiItem.getSecurity());
                        mItem = mWifiItem;
                        dialog.dismiss();
                    }
                });
                dialog.setRightButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mNetworkHelper.forgetWifi(mWifiItem.getSSID(), mWifiItem.getSecurity());
                        dialog.dismiss();
                        refreshWifiList();
                    }
                });
                dialog.show();

            } else if (mWifiItem.getConnectedState() == WifiItem.STATE_DISCONNECTED) {
                goToConnectFragment(mWifiItem);
            }
        }
    }

    private class WifiAdapter extends RecyclerView.Adapter<WifiListHolder> {

        List<WifiItem> mItems;

        public WifiAdapter(List<WifiItem> items) {
            mItems = items;
            setHasStableIds(true);
        }

        @Override
        public WifiListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new WifiListHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(WifiListHolder holder, int position) {
            WifiItem item = mItems.get(position);
            holder.bind(item);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        int mSpace;

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = mSpace;
        }
    }
}
