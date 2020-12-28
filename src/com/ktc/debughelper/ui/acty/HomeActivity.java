package com.ktc.debughelper.ui.acty;


import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ktc.debughelper.contacts.BaseRowData;
import com.ktc.debughelper.contacts.DebugRowData;
import com.ktc.debughelper.contacts.OtherRowData;
import com.ktc.debughelper.ui.acty.baseInfo.BaseSoftInfoActivity;
import com.ktc.debughelper.ui.acty.baseInfo.BuildPropActivity;
import com.ktc.debughelper.ui.acty.baseInfo.HardWareInfoActivity;
import com.ktc.debughelper.ui.acty.baseInfo.LanguageActivity;
import com.ktc.debughelper.ui.acty.baseInfo.NetWorkActivity;
import com.ktc.debughelper.ui.acty.baseInfo.OtaUpgradeActivity;
import com.ktc.debughelper.ui.acty.baseInfo.ScreenInfoActivity;
import com.ktc.debughelper.ui.acty.other.DebugWindowActivity;
import com.ktc.debughelper.ui.acty.other.HardWareUpdateActivity;
import com.ktc.debughelper.ui.acty.other.KtcDebugHomeActivity;
import com.ktc.debughelper.ui.acty.other.MacEditActivity;
import com.ktc.debughelper.ui.acty.other.SerialPortActivity;
import com.ktc.debughelper.ui.adapter.RowDataAdapter;
import com.ktc.debughelper.ui.adapter.RvBaseAdapter;
import com.ktc.debughelper.view.DebugPopWindow;
import com.ktc.debughelper.view.RowView;
import com.ktc.debughelper.view.TvScrollView;
import com.ktc.setting.R;

public class HomeActivity extends BaseActivity implements RvBaseAdapter.OnItemClickListener {

    private final static boolean KTC_DEVELOPER = true;
    private TvScrollView mHomeContentTsv;
    private TextView mHomeAbout;
    private LinearLayout mRootView;

    /**
     * deal the item click event
     */
    @Override
    public void onItemClick(int position, int itemId) {
        if (itemId > DebugRowData.INDEX_MIN && itemId < DebugRowData.INDEX_MAX) {
            //if it is the debug item,just goto the popWindow
            DebugPopWindow debugWindow = new DebugPopWindow(this, itemId - 1);
            debugWindow.showAtLocation(mRootView, Gravity.LEFT, 0, 0);
            setBackgroundAlpha(0.8f); //界面背景半透明
            debugWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    setBackgroundAlpha(1.0f);//恢复界面
                }
            });
            return;
        }
        //others action will use the dialog activity to apply
        Intent gotoIntent = null;
        switch (itemId) {
            case BaseRowData.SOFT_INFO:
                gotoIntent = new Intent(context, BaseSoftInfoActivity.class);
                break;
            case BaseRowData.ANALYSE_BUILD:
                gotoIntent = new Intent(context, BuildPropActivity.class);
                break;
            case BaseRowData.OTA_UPDATE_INFO:
                gotoIntent = new Intent(context, OtaUpgradeActivity.class);
                break;
            case BaseRowData.HARDWARE_INFO:
                gotoIntent = new Intent(context, HardWareInfoActivity.class);
                break;
            case BaseRowData.SCREEN_INFO:
                gotoIntent = new Intent(context, ScreenInfoActivity.class);
                break;
            case BaseRowData.LANGUAGE_INFO:
                gotoIntent = new Intent(context, LanguageActivity.class);
                break;
            case BaseRowData.NETWORK_INFO:
                gotoIntent = new Intent(context, NetWorkActivity.class);
                break;
            case OtherRowData.HARDWARE_UPDATE:
                gotoIntent = new Intent(context, HardWareUpdateActivity.class);
                break;
            case OtherRowData.DEBUG_SERIAL:
                gotoIntent = new Intent(context, SerialPortActivity.class);
                break;
            case OtherRowData.EDIT_MAC:
                gotoIntent = new Intent(context, MacEditActivity.class);
                break;
            case OtherRowData.LOG_RECORD:
                gotoIntent = new Intent(context, KtcDebugHomeActivity.class);
                break;
            case OtherRowData.OPEN_DEBUG_WINDOW:
                gotoIntent = new Intent(context, DebugWindowActivity.class);
                break;
        }
        if (gotoIntent != null)
            startActivity(gotoIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        mHomeAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AboutActivity.class));
            }
        });
    }

    /**
     * init row view
     */
    private void init() {
        mHomeContentTsv = (TvScrollView) findViewById(R.id.mHomeContentTsv);
        mHomeAbout = (TextView) findViewById(R.id.mHomeAbout);
        mRootView = (LinearLayout) findViewById(R.id.mRootView);

        if (KTC_DEVELOPER) {
            mHomeContentTsv.addRow(
                    new RowView(this).init(
                            new DebugRowData().getRowTitle(),
                            new RowDataAdapter(this, new DebugRowData().getRowDataSet(), R.layout.item_row)
                    )
            );
        }
        mHomeContentTsv.addRow(
                new RowView(this).init(
                        new BaseRowData().getRowTitle(),
                        new RowDataAdapter(this, new BaseRowData().getRowDataSet(), R.layout.item_row)
                )
        );
        mHomeContentTsv.addRow(
                new RowView(this).init(
                        new OtherRowData().getRowTitle(),
                        new RowDataAdapter(this, new OtherRowData().getRowDataSet(), R.layout.item_row)
                )
        );
        mHomeContentTsv.setOnItemClickListener(this);
    }
}