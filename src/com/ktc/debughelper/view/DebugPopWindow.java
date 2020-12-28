package com.ktc.debughelper.view;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ThreadedRenderer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.android.internal.app.LocalePicker;
import com.ktc.debughelper.bean.CheckBoxBean;
import com.ktc.debughelper.ui.adapter.CheckBoxViewAdapter;
import com.ktc.debughelper.util.BaseInfoUtil;
import com.ktc.setting.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DebugPopWindow extends PopupWindow {
    private Context context;
    private int childIndex = -1;
    private Resources resources;
    private PredictRecyclerView mContainerRv;
    private View baseView;
    private List<CheckBoxBean> dataList = new ArrayList<>();
    private CheckBoxViewAdapter adapter;

    /**
     * init the PopWindow attrs
     */
    public DebugPopWindow(Context context, int index) {
        this.context = context;
        this.resources = context.getResources();
        this.childIndex = index;
        init();
    }

    public void init() {
        baseView = LayoutInflater.from(context).inflate(R.layout.popwindow_debug, null);
        this.setContentView(baseView);
        inflaterView();
        this.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setOutsideTouchable(false);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(Color.rgb(96, 96, 96));
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.popwindow_anim_style);
    }

    /**
     * init the UIs
     * you need to set the title .default status .and an action for ON/OFF
     */
    private void inflaterView() {
        mContainerRv = (PredictRecyclerView) baseView.findViewById(R.id.mContainerRv);
        //
        dataList.add(
                new CheckBoxBean(
                        resources.getString(R.string.str_debug_show_ui_border),
                        SystemProperties.getBoolean("debug.layout", false)
                ));
        dataList.add(
                new CheckBoxBean(
                        resources.getString(R.string.str_debug_show_gpu_draw),
                        !SystemProperties.get("debug.hwui.overdraw", "false").equals("false")
                ));
        dataList.add(
                new CheckBoxBean(
                        resources.getString(R.string.str_debug_force_rtl),
                        Settings.Global.getInt(context.getContentResolver(),
                                "debug.force_rtl", 0) != 0
                ));
        dataList.add(
                new CheckBoxBean(
                        resources.getString(R.string.str_debug_usb_debug),
                        Settings.Global.getInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0) != 0
                ));
        dataList.add(
                new CheckBoxBean(
                        resources.getString(R.string.str_debug_force_gpu_shader),
                        !SystemProperties.get(ThreadedRenderer.PROFILE_PROPERTY, "false").equals("false")
                ));
        dataList.add(
                new CheckBoxBean(
                        resources.getString(R.string.str_debug_show_gpu_update),
                        SystemProperties.getBoolean(
                                ThreadedRenderer.DEBUG_DIRTY_REGIONS_PROPERTY, false
                        )
                ));
        dataList.add(
                new CheckBoxBean(
                        resources.getString(R.string.str_debug_show_hardware_layer),
                        SystemProperties.getBoolean(
                                ThreadedRenderer.DEBUG_SHOW_LAYERS_UPDATES_PROPERTY, false
                        )
                ));
        //
        adapter = new CheckBoxViewAdapter(context, dataList, R.layout.view_checkbox);
        mContainerRv.setLayoutManager(new FocusLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        mContainerRv.setAdapter(adapter);
        mContainerRv.setItemAnimator(null);
        mContainerRv.addItemDecoration(new KItemDecoration(4, 4, 4, 4)); //to resolve the notifyItemChanged flicker
        adapter.setOnItemClickListener(new CheckBoxViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position, boolean isChecked) {
                dataList.get(position).setChecked(!isChecked);
                //                dataList[position].action.invoke(dataList[position].isChecked)
                dealClickEvent(position, dataList.get(position).getChecked());
                adapter.notifyItemChanged(position);
            }
        });
        //指定item获取焦点
        mContainerRv.post(new Runnable() {
            @Override
            public void run() {
                mContainerRv.getChildAt(childIndex).requestFocus();
            }
        });
    }

    private void dealClickEvent(int position, Boolean checked) {
        switch (position) {
            case 0:
                if (checked) {
                    SystemProperties.set("debug.layout", "true");
                } else {
                    SystemProperties.set("debug.layout", "false");
                }
                systemPropPoker();
                break;
            case 1:
                if (checked) {
                    SystemProperties.set("debug.hwui.overdraw", "show");
                } else {
                    SystemProperties.set("debug.hwui.overdraw", "false");
                }
                systemPropPoker();
                break;
            case 2:
                Locale locale = BaseInfoUtil.getLocale(context);
                if (checked) {
                    Settings.Global.putInt(
                            context.getContentResolver(),
                            "debug.force_rtl", 1
                    );
                    SystemProperties.set("debug.force_rtl", "1");
                } else {
                    Settings.Global.putInt(
                            context.getContentResolver(),
                            "debug.force_rtl", 0
                    );
                    SystemProperties.set("debug.force_rtl", "0");
                }
                LocalePicker.updateLocale(locale);
                systemPropPoker();
                break;
            case 3:
                if (checked) {
                    Settings.Global.putInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 1);
                } else {
                    Settings.Global.putInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0);
                }
                break;
            case 4:
                if (checked) {
                    SystemProperties.set(
                            ThreadedRenderer.PROFILE_PROPERTY, "visual_bars"
                    );
                } else {
                    SystemProperties.set(
                            ThreadedRenderer.PROFILE_PROPERTY, "false"
                    );
                }
                systemPropPoker();
                break;
            case 5:
                if (checked) {
                    SystemProperties.set(
                            ThreadedRenderer.DEBUG_DIRTY_REGIONS_PROPERTY, "true"
                    );
                } else {
                    SystemProperties.set(
                            ThreadedRenderer.DEBUG_DIRTY_REGIONS_PROPERTY, "false"
                    );
                }
                systemPropPoker();
                break;
            case 6:
                if (checked) {
                    SystemProperties.set(
                            ThreadedRenderer.DEBUG_SHOW_LAYERS_UPDATES_PROPERTY, "true"
                    );
                } else {
                    SystemProperties.set(
                            ThreadedRenderer.DEBUG_SHOW_LAYERS_UPDATES_PROPERTY, "false"
                    );
                }
                systemPropPoker();
                break;
        }
    }


    /**
     * after  modify the  SystemProperties
     */
    private void systemPropPoker() {
        final int SYSPROPS_TRANSACTION = ('_' << 24) | ('S' << 16) | ('P' << 8) | 'R';
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] services = ServiceManager.listServices();
                    for (int i = 0; i < services.length; i++) {
                        IBinder result = ServiceManager.checkService(services[i]);
                        Parcel data = Parcel.obtain();
                        result.transact(SYSPROPS_TRANSACTION, data, null, 0);
                        data.recycle();
                    }
                } catch (Exception e) {

                }
            }
        }).start();
    }
}