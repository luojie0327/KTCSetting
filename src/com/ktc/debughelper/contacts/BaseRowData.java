package com.ktc.debughelper.contacts;

import com.ktc.debughelper.bean.FunctionBean;
import com.ktc.debughelper.view.RowView;

import java.util.ArrayList;
import java.util.List;

public class BaseRowData extends RowView.RowDataSet<FunctionBean> {

    /**
     * you must define the Unique ID  by << 5
     * max size limit is 32
     */
    public static final int INDEX_MIN = 1 << 5;
    public static final int SOFT_INFO = 0x1 + (1 << 5);
    public static final int ANALYSE_BUILD = 0x2 + (1 << 5);
    public static final int OTA_UPDATE_INFO = 0x3 + (1 << 5);
    public static final int HARDWARE_INFO = 0x4 + (1 << 5);
    public static final int SCREEN_INFO = 0x5 + (1 << 5);
    public static final int LANGUAGE_INFO = 0x6 + (1 << 5);
    public static final int NETWORK_INFO = 0x7 + (1 << 5);
    public static final int INDEX_MAX = 1 << 6;


    @Override
    public List<FunctionBean> getRowDataSet() {
        List<FunctionBean> dataList = new ArrayList<>();
        dataList.add(new FunctionBean(SOFT_INFO, "软件信息"));
        dataList.add(new FunctionBean(ANALYSE_BUILD, "Build.prop解析"));
        dataList.add(new FunctionBean(OTA_UPDATE_INFO, "OTA升级信息"));
        dataList.add(new FunctionBean(HARDWARE_INFO, "硬件信息"));
        dataList.add(new FunctionBean(SCREEN_INFO, "屏幕信息"));
        dataList.add(new FunctionBean(LANGUAGE_INFO, "语言设置"));
        dataList.add(new FunctionBean(NETWORK_INFO, "网络设置"));
        return dataList;
    }

    @Override
    public String getRowTitle() {
        return "基础信息";
    }
}