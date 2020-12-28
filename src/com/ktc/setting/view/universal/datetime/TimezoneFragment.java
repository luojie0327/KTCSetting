package com.ktc.setting.view.universal.datetime;

import android.view.View;

import com.ktc.setting.R;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.PickerView;
import com.ktc.setting.view.universal.datetime.timezone.ZoneUtil;

import java.util.ArrayList;
import java.util.List;

public class TimezoneFragment extends BaseFragment implements PickerView.OnSelectListener {

    private static final String TAG = TimezoneFragment.class.getSimpleName();

    private PickerView timezonePicker;

    @Override
    protected int getRes() {
        return R.layout.fragment_timezone;
    }

    @Override
    protected int getTitle() {
        return R.string.str_universal_timezone;
    }

    @Override
    protected void initView(View view) {
        timezonePicker = (PickerView) view.findViewById(R.id.timezone_picker);
    }

    @Override
    protected void setFocus() {

    }

    @Override
    protected void initData() {
        ArrayList<String> zones = ZoneUtil.getZoneInfoList(getContext());
        List<String> zoneIdLists = ZoneUtil.getZoneIdList(getContext());
        int currentZoneIdx = ZoneUtil.getCurrentZoneIndex(getContext());
        timezonePicker.setData(zones, zoneIdLists);
        timezonePicker.setSelected(currentZoneIdx);
        mActivity.setSubTitle(ZoneUtil.getCurrentZoneInfoForSubtitle(getContext()));
    }

    @Override
    protected void addListener() {
        timezonePicker.setOnSelectListener(this);
    }

    @Override
    public void onSelect(String text, String original) {
        mActivity.setSubTitle(text);
        DateTimeTool.setTimeZone(getContext(), original);
    }
}
