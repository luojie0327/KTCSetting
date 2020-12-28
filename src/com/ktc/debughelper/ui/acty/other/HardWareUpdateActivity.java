package com.ktc.debughelper.ui.acty.other;


import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ktc.debughelper.bean.SingleChoiceBean;
import com.ktc.debughelper.ui.acty.BaseDialogActivity;
import com.ktc.debughelper.ui.adapter.RvBaseAdapter;
import com.ktc.debughelper.ui.adapter.SingleChoiceAdapter;
import com.ktc.debughelper.util.BaseInfoUtil;
import com.ktc.debughelper.util.KtcFileUtil;
import com.ktc.debughelper.view.KItemDecoration;
import com.ktc.setting.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HardWareUpdateActivity extends BaseDialogActivity<Void, List<SingleChoiceBean>> {

    //most probably we should define handler to static,in case of leak memory
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };
    private List<SingleChoiceBean> dataList;
    private SingleChoiceAdapter adapter;
    private String fileName = "";
    private int lastCheckIndex = -1;

    @Override
    public List<SingleChoiceBean> loadData() {
        List<SingleChoiceBean> dataList = new ArrayList<>();
        List<String> rootDirs = KtcFileUtil.getAllExternalSdcardPath(HardWareUpdateActivity.this);
        File rootFile;
        for (int i = 0; i < rootDirs.size(); i++) {
            String rootDirName = rootDirs.get(i);
            rootFile = new File(rootDirName);
            String[] childFileNames = rootFile.list();
            for (int j = 0; j < childFileNames.length; j++) {
                if (childFileNames[j].endsWith(".bin")) {
                    dataList.add(new SingleChoiceBean(childFileNames[j], false));
                }
            }
        }
        return dataList;
    }

    @Override
    public void performDataToUi(List<SingleChoiceBean> singleChoiceBeans) {
        dataList = singleChoiceBeans;
        if (dataList.isEmpty()) {
            TextView textContent = createCenterTextView();
            textContent.setText(getString(R.string.str_upgrade_tip_no_usb));
            mContainerFL.addView(textContent);
        } else {
            RecyclerView recyclerView = createRecycleView();
            recyclerView.setPadding(16, 16, 16, 16);
            recyclerView.addItemDecoration(new KItemDecoration(8, 8, 8, 8));
            adapter = new SingleChoiceAdapter(context, dataList, R.layout.item_sigle_choice);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new RvBaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, int itemId) {
                    if (lastCheckIndex == position)
                        return;
                    fileName = dataList.get(position).getItemName();
                    dataList.get(position).setCurItemSelected(true);
                    if (lastCheckIndex != -1) {
                        dataList.get(lastCheckIndex).setCurItemSelected(false);
                        adapter.notifyItemChanged(lastCheckIndex);
                    }
                    adapter.notifyItemChanged(position);
                    lastCheckIndex = position;
                    mActionTv.setEnabled(!fileName.isEmpty());
                }
            });
            mContainerFL.addView(recyclerView);
        }
    }

    @Override
    public Boolean beforeLoadUi() {
        mDialogTitleTv.setText(getString(R.string.str_other_upgrade_title));
        mActionTv.setText(getString(R.string.str_upgrade));
        mActionTv.setEnabled(false);
        mActionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUpgrade();
            }
        });
        return true;
    }

    private Boolean checkUsbIsExist() {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        return !usbManager.getDeviceList().isEmpty();
    }

    private void gotoUpgrade() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (checkUsbIsExist() && BaseInfoUtil.isUsbMounted(context)) {
                    int ret = BaseInfoUtil.UpgradeMainFun(context, fileName);
                    Message message = mHandler.obtainMessage();
                    message.what = ret;
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    public enum EnumUpgradeStatus {
        // status fail
        E_UPGRADE_FAIL,
        // status success
        E_UPGRADE_SUCCESS,
        // file not found
        E_UPGRADE_FILE_NOT_FOUND
    }
}
