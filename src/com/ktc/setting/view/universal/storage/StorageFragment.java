package com.ktc.setting.view.universal.storage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ktc.setting.R;
import com.ktc.setting.helper.DestinyUtil;
import com.ktc.setting.helper.SpaceItemDecoration;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.LoadDialog;
import com.ktc.setting.view.custom.SelectSettingView;
import com.ktc.setting.view.custom.SettingViewContainer;
import com.ktc.setting.view.custom.ToastFactory;

import java.util.ArrayList;
import java.util.List;

public class StorageFragment extends BaseFragment implements View.OnClickListener,
        SelectSettingView.OnItemSelectListener {

    private static final String TAG = StorageFragment.class.getSimpleName();

    private RecyclerView storageListView;
    private SettingViewContainer storageContainer;
    private StorageAdapter mAdapter;
    private Button formatBtn;
    private List<DiskInfo> mDiskInfoList;
    private List<String> formatDiskPath;
    private boolean isInit = false;

    @Override
    protected int getRes() {
        return R.layout.fragment_storage;
    }

    @Override
    protected int getTitle() {
        return R.string.str_universal_storage_title;
    }

    @Override
    protected void initView(View view) {
        storageListView = (RecyclerView) view.findViewById(R.id.storage_list);
        storageContainer = (SettingViewContainer) view.findViewById(R.id.storage_container);
        formatBtn = (Button) view.findViewById(R.id.storage_format_btn);
    }

    @Override
    protected void setFocus() {
        storageListView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (!isInit) {
                    storageContainer.setNewFocus(view);
                    isInit = true;
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mUsbReceiver);
    }

    @Override
    protected void initData() {
        mDiskInfoList = StorageUtil.getMountedDisksList(getContext());
        formatDiskPath = new ArrayList<>();
        storageListView.setLayoutManager(new LinearLayoutManager(getContext()));
        storageListView.addItemDecoration(new SpaceItemDecoration(DestinyUtil.dp2px(getContext(), 6.7f)));
        mAdapter = new StorageAdapter(getContext(), mDiskInfoList);
        mAdapter.setOnItemSelectListener(this);
        storageListView.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        registerUsbReceiver();
        formatBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.storage_format_btn:
                FormatAsyncTask formatAsyncTask = new FormatAsyncTask();
                formatAsyncTask.execute(formatDiskPath.toArray(new String[formatDiskPath.size()]));
                break;
        }
    }

    @Override
    public void onSelect(boolean isCheck) {
        int currentPosition = 0;
        for (int i = 0; i < storageListView.getChildCount(); i++) {
            if (storageListView.getChildAt(i).hasFocus()) {
                currentPosition = i;
            }
        }
        DiskInfo currentDiskInfo = mDiskInfoList.get(currentPosition);
        if (isCheck) {
            formatDiskPath.add(currentDiskInfo.getPath());
        } else {
            if (formatDiskPath.contains(currentDiskInfo.getPath())) {
                formatDiskPath.remove(currentDiskInfo.getPath());
            }
        }
    }

    private void refreshUI() {
        mDiskInfoList.clear();
        mDiskInfoList.addAll(StorageUtil.getMountedDisksList(getContext()));
        mAdapter.notifyDataSetChanged();
    }

    private void registerUsbReceiver() {
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        usbFilter.addDataScheme("file");
        getActivity().registerReceiver(mUsbReceiver, usbFilter);
    }

    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                String path = intent.getData().getPath();
                if (!TextUtils.isEmpty(path)) {
                    mDiskInfoList.add(StorageUtil.getDiskInfoByPath(context, path));
                }
                mAdapter.notifyDataSetChanged();
                refreshList();
            } else if (Intent.ACTION_MEDIA_EJECT.equals(action)) {
                String path = intent.getData().getPath();
                if (!TextUtils.isEmpty(path)) {
                    DiskInfo needRemoveDisk = null;
                    for (DiskInfo diskInfo : mDiskInfoList) {
                        if (diskInfo.getPath().equals(path)) {
                            needRemoveDisk = diskInfo;
                        }
                    }
                    if (needRemoveDisk != null) {
                        mDiskInfoList.remove(needRemoveDisk);
                        mAdapter.notifyDataSetChanged();
                        if (formatDiskPath.contains(needRemoveDisk.getPath())) {
                            formatDiskPath.remove(needRemoveDisk.getPath());
                        }
                    }
                }
                refreshList();
            }
        }
    };

    private void refreshList() {
        if (storageListView.getChildAt(0) != null) {
            storageListView.getChildAt(0).requestFocus();
        }
    }

    private void clearSelect() {
        for (int i = 0; i < storageListView.getChildCount(); i++) {
            if (storageListView.getChildAt(i) instanceof SelectSettingView) {
                ((SelectSettingView) storageListView.getChildAt(i)).setIsSelected(false);
            }
        }
        formatDiskPath.clear();
    }

    class FormatAsyncTask extends AsyncTask<String, Void, Integer> {

        static final int SUCCESS = 0;
        static final int FAIL = 1;
        static final int NO_SELECT = 3;
        LoadDialog mLoadDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadDialog = new LoadDialog(getContext(), R.style.LoadDialog);
            mLoadDialog.setDialogSize(427, 150);
            mLoadDialog.setMessageText(getResources().getString(R.string.str_universal_fomatting));
            mLoadDialog.show();
            mLoadDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    onCancelled(FAIL);
                }
            });
        }

        @Override
        protected Integer doInBackground(String... strings) {
            if (strings == null || strings.length == 0) {
                return NO_SELECT;
            }
            try {
                for (String s : strings) {
                    StorageUtil.formatStorage(s);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return FAIL;
            }
            return SUCCESS;
        }

        @Override
        protected void onPostExecute(Integer i) {
            super.onPostExecute(i);
            switch (i) {
                case SUCCESS:
                    ToastFactory.showToast(getContext(), getResources().getString(R.string.str_universal_storage_format_success)
                            , Toast.LENGTH_SHORT);
                    clearSelect();
                    refreshUI();
                    break;
                case FAIL:
                    ToastFactory.showToast(getContext(), getResources().getString(R.string.str_universal_storage_format_fail)
                            , Toast.LENGTH_SHORT);
                    break;
                case NO_SELECT:
                    ToastFactory.showToast(getContext(), getResources().getString(R.string.str_universal_storage_format_select_tips)
                            , Toast.LENGTH_SHORT);
                    break;
            }
            if (mLoadDialog != null && mLoadDialog.isShowing()) {
                mLoadDialog.dismiss();
            }
        }
    }
}
