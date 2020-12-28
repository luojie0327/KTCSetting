package com.ktc.debughelper.contacts;

import com.ktc.debughelper.bean.FunctionBean;
import com.ktc.debughelper.view.RowView;

import java.util.ArrayList;
import java.util.List;

public class OtherRowData extends RowView.RowDataSet<FunctionBean> {

    /**
     * you must define the Unique ID  by << 6
     * max size limit is 64
     */
    public static final int INDEX_MIN = 1 << 6;
    public static final int HARDWARE_UPDATE = 0x1 + (1 << 6);
    public static final int DEBUG_SERIAL = 0x2 + (1 << 6);
    public static final int EDIT_MAC = 0x4 + (1 << 6);
    public static final int LOG_RECORD = 0x5 + (1 << 6);
    public static final int OPEN_DEBUG_WINDOW = 0x6 + (1 << 6);
    public static final int INDEX_MAX = 1 << 7;

    @Override
    public List<FunctionBean> getRowDataSet() {
        List<FunctionBean> dataList = new ArrayList<>();
        dataList.add(new FunctionBean(HARDWARE_UPDATE, "固件升级"));
        dataList.add(new FunctionBean(DEBUG_SERIAL, "串口调试"));
        dataList.add(new FunctionBean(EDIT_MAC, "MAC编辑"));
        dataList.add(new FunctionBean(LOG_RECORD, "Log抓取同步录屏"));
        dataList.add(new FunctionBean(OPEN_DEBUG_WINDOW, "开启调试悬浮窗"));
        return dataList;
    }

    @Override
    public String getRowTitle() {
        return "其他";
    }
}