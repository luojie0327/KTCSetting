package com.ktc.debughelper.contacts;

import com.ktc.debughelper.bean.FunctionBean;
import com.ktc.debughelper.view.RowView.RowDataSet;

import java.util.ArrayList;
import java.util.List;

public class DebugRowData extends RowDataSet<FunctionBean> {
    /**
     * you must define the Unique ID
     * max size limit is 32
     */
    public static final int INDEX_MIN = 0;
    public static final int SHOW_UI_BORDER = 0x1;
    public static final int SHOW_GPU_DRAW = 0x2;
    public static final int SHOW_STL_LAYOUT = 0x3;
    public static final int USB_DEBUG = 0x4;
    public static final int FORCE_GPU_SHADER = 0x5;
    public static final int SHOW_GPU_UPDATE = 0x6;
    public static final int SHOW_HARD_WARE = 0x7;
    public static final int INDEX_MAX = 1 << 5;

    @Override
    public List<FunctionBean> getRowDataSet() {
        List<FunctionBean> dataList = new ArrayList<>();
        dataList.add(new FunctionBean(SHOW_UI_BORDER, "显示UI布局边界"));
        dataList.add(new FunctionBean(SHOW_GPU_DRAW, "显示GPU过度绘制"));
        dataList.add(new FunctionBean(SHOW_STL_LAYOUT, "强制布局显示从右到左"));
        dataList.add(new FunctionBean(USB_DEBUG, "USB调试"));
        dataList.add(new FunctionBean(FORCE_GPU_SHADER, "强制GPU渲染"));
        dataList.add(new FunctionBean(SHOW_GPU_UPDATE, "显示GPU视图更新"));
        dataList.add(new FunctionBean(SHOW_HARD_WARE, "显示硬件层"));
        return dataList;
    }

    @Override
    public String getRowTitle() {
        return "调试相关(仅RD可见)";
    }
}