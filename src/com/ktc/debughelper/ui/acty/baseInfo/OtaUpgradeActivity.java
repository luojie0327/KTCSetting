package com.ktc.debughelper.ui.acty.baseInfo;


import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ktc.debughelper.bean.OtaInfoBean;
import com.ktc.debughelper.ui.acty.BaseDialogActivity;
import com.ktc.debughelper.ui.adapter.OtaInfoAdapter;
import com.ktc.debughelper.util.BaseInfoUtil;
import com.ktc.setting.R;

import java.util.ArrayList;
import java.util.List;

public class OtaUpgradeActivity extends BaseDialogActivity<Void, List<OtaInfoBean>> {

    @Override
    public List<OtaInfoBean> loadData() {
        List<OtaInfoBean> dataList = new ArrayList<>();
        //device
        dataList.add(new OtaInfoBean(getString(R.string.str_ota_device), "", "", true));
        dataList.add(
                new OtaInfoBean(
                        getString(R.string.str_ota_proper),
                        getString(R.string.str_ota_proper_notice),
                        getString(R.string.str_ota_proper_values),
                        false
                )
        );
        dataList.add(
                new OtaInfoBean(
                        getString(R.string.str_ota_model) + ("\n") + (getString(R.string.str_ota_model_pre)),
                        getString(R.string.str_ota_model_note),
                        BaseInfoUtil.getOtaProductName(),
                        false
                )
        );
        dataList.add(
                new OtaInfoBean(
                        getString(R.string.str_ota_customer) + ("\n") + (getString(R.string.str_ota_customer_pre)),
                        getString(R.string.str_ota_customer_note),
                        BaseInfoUtil.getCustomer(),
                        false
                )
        );
        dataList.add(
                new OtaInfoBean(
                        getString(R.string.str_ota_sp_board_type) + ("\n") + (getString(R.string.str_ota_sp_board_type_pre)),
                        getString(R.string.str_ota_sp_board_type_note),
                        BaseInfoUtil.getBoardType(),
                        false
                )
        );
        dataList.add(
                new OtaInfoBean(
                        getString(R.string.str_ota_sys_language) + ("\n") + (getString(R.string.str_ota_sys_language_pre)),
                        getString(R.string.str_ota_sys_language_note),
                        BaseInfoUtil.getBoardType_Language(),
                        false
                )
        );
        dataList.add(
                new OtaInfoBean(
                        getString(R.string.str_ota_sys_timezone) + ("\n") + (getString(R.string.str_ota_sys_timezone_pre)),
                        getString(R.string.str_ota_sys_timezone_note),
                        BaseInfoUtil.getBoardType_Timezone(),
                        false
                )
        );
        dataList.add(
                new OtaInfoBean(
                        getString(R.string.str_ota_board_reserved) + ("\n") + (getString(R.string.str_ota_board_reserved_pre)),
                        getString(R.string.str_ota_board_reserved_note),
                        BaseInfoUtil.getBoardType_Reserved(),
                        false
                )
        );
        dataList.add(
                new OtaInfoBean(
                        getString(R.string.str_ota_board_memory) + ("\n") + (getString(R.string.str_ota_board_memory_pre)),
                        getString(R.string.str_ota_board_memory_note),
                        BaseInfoUtil.getBoardMemory(),
                        false
                )
        );
        dataList.add(
                new OtaInfoBean(
                        getString(R.string.str_ota_lcd_density) + ("\n") + (getString(R.string.str_ota_lcd_density_pre)),
                        getString(R.string.str_ota_lcd_density_note),
                        BaseInfoUtil.getLcd_Density(),
                        false
                )
        );
        dataList.add(
                new OtaInfoBean(
                        getString(R.string.str_ota_version) + ("\n") + (getString(R.string.str_ota_version_pre)),
                        getString(R.string.str_ota_version_note),
                        BaseInfoUtil.getSystemVersion(),
                        false
                )
        );
        //supernova
        dataList.add(new OtaInfoBean(getString(R.string.str_ota_supernova), "", "", true));
        dataList.add(
                new OtaInfoBean(
                        getString(R.string.str_ota_proper),
                        getString(R.string.str_ota_proper_notice),
                        getString(R.string.str_ota_proper_values),
                        false
                )
        );
        dataList.add(
                new OtaInfoBean(
                        getString(R.string.str_ota_board_type) + ("\n") + (getString(R.string.str_ota_board_type_pre)),
                        getString(R.string.str_ota_board_type_note),
                        BaseInfoUtil.getBoardType_ota(),
                        false
                )
        );
        dataList.add(
                new OtaInfoBean(
                        getString(R.string.str_ota_num) + ("\n") + (getString(R.string.str_ota_num_pre)),
                        getString(R.string.str_ota_num_note),
                        BaseInfoUtil.getSDANum_Ini(),
                        false
                )
        );
        dataList.add(
                new OtaInfoBean(
                        getString(R.string.str_ota_mac) + ("\n") + (getString(R.string.str_ota_mac_pre)),
                        getString(R.string.str_ota_mac_note),
                        BaseInfoUtil.getMacAddress(),
                        false
                )
        );
        return dataList;
    }

    @Override
    public void performDataToUi(List<OtaInfoBean> result) {
        RecyclerView recyclerView = createRecycleView();
        recyclerView.setAdapter(new OtaInfoAdapter(context, result, R.layout.item_ota_info, false));
        mContainerFL.addView(recyclerView);
    }

    @Override
    public Boolean beforeLoadUi() {
        mDialogTitleTv.setText(getString(R.string.str_base_ota));
        mActionTv.setText(getString(R.string.str_base_dialog_exit));
        mActionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return true;
    }
}