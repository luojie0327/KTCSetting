package com.ktc.setting.view.universal.datetime;

import android.content.Intent;
import android.view.View;

import com.ktc.setting.R;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.ButtonSettingView;
import com.ktc.setting.view.custom.SettingViewContainer;
import com.ktc.setting.view.custom.SwitchSettingView;
import com.ktc.setting.view.universal.UniversalActivity;
import com.ktc.setting.view.universal.datetime.observe.TimeObserver;
import com.ktc.setting.view.universal.datetime.timezone.ZoneUtil;

public class DateTimeFragment extends BaseFragment implements ButtonSettingView.OnButtonClickListener
        , SwitchSettingView.OnSwitchListener, TimeObserver {

    private static final String TAG = DateTimeFragment.class.getSimpleName();
    private SettingViewContainer dateTimeContainer;
    private SwitchSettingView dateTimeAutoSwitch;
    private SwitchSettingView dateFormatSwitch;
    private SwitchSettingView use24hSwitch;
    private ButtonSettingView timezoneBtn;
    private ButtonSettingView timeSettingBtn;

    @Override
    protected int getRes() {
        return R.layout.fragment_time;
    }

    @Override
    protected int getTitle() {
        return R.string.str_universal_time;
    }

    @Override
    protected void initView(View view) {
        dateTimeContainer = (SettingViewContainer) view.findViewById(R.id.date_time_container);
        dateTimeAutoSwitch = (SwitchSettingView) view.findViewById(R.id.date_time_auto_get_switch);
        dateFormatSwitch = (SwitchSettingView) view.findViewById(R.id.date_time_date_format_switch);
        use24hSwitch = (SwitchSettingView) view.findViewById(R.id.date_time_24h_switch);
        timezoneBtn = (ButtonSettingView) view.findViewById(R.id.date_time_timezone_btn);
        timeSettingBtn = (ButtonSettingView) view.findViewById(R.id.date_time_time_setting_btn);
    }

    @Override
    protected void setFocus() {
        View newFocus = null;
        switch (mActivity.preFocusViewIdSecond) {
            case R.id.date_time_timezone_btn:
                newFocus = timezoneBtn;
                break;
            case R.id.date_time_time_setting_btn:
                newFocus = timeSettingBtn;
                break;
        }
        if (newFocus != null) {
            newFocus.requestFocus();
            dateTimeContainer.setNewFocus(newFocus);
        } else {
            dateTimeContainer.setNewFocus(dateTimeAutoSwitch);
        }
    }

    @Override
    protected void initData() {
        ((UniversalActivity) getActivity()).mTimeObservation.addObserver(this);
        dateTimeAutoSwitch.setValueArray(getResources().getStringArray(R.array.str_array_data_time_auto));
        dateFormatSwitch.setValueArray(getResources().getStringArray(R.array.str_array_time_format));
        use24hSwitch.setValueArray(getResources().getStringArray(R.array.str_array_common_switch));
        refreshUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUI();
        mActivity.preFocusViewIdSecond = 0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((UniversalActivity) getActivity()).mTimeObservation.removeObserver(this);
    }

    private void refreshUI() {
        dateTimeAutoSwitch.setIndex(DateTimeTool.getTimeAutoState(getContext().getApplicationContext()) ? 0 : 1);
        timezoneBtn.setValue(ZoneUtil.getCurrentZoneInfo(getContext()));
        timeSettingBtn.setValue(DateTimeTool.getCurrentDateAndTime(getContext().getApplicationContext()));
        dateFormatSwitch.setIndex(DateTimeTool.getDateFormatIndex(getContext().getApplicationContext()));
        use24hSwitch.setIndex(DateTimeTool.isCurrent24Hour(getContext().getApplicationContext()) ? 0 : 1);
        if (DateTimeTool.getTimeAutoState(getContext().getApplicationContext())) {
            timezoneBtn.setEnabled(false);
            timeSettingBtn.setEnabled(false);
            timezoneBtn.setFocusable(false);
            timezoneBtn.setFocusableInTouchMode(false);
            timeSettingBtn.setFocusable(false);
            timeSettingBtn.setFocusableInTouchMode(false);
        } else {
            timezoneBtn.setEnabled(true);
            timeSettingBtn.setEnabled(true);
            timezoneBtn.setFocusable(true);
            timezoneBtn.setFocusableInTouchMode(true);
            timeSettingBtn.setFocusable(true);
            timeSettingBtn.setFocusableInTouchMode(true);
        }
    }

    @Override
    protected void addListener() {
        timezoneBtn.setOnButtonClickListener(this);
        timeSettingBtn.setOnButtonClickListener(this);
        dateTimeAutoSwitch.setOnSwitchListener(this);
        dateFormatSwitch.setOnSwitchListener(this);
        use24hSwitch.setOnSwitchListener(this);
    }

    @Override
    public void onClick(View view) {
        mActivity.preFocusViewIdSecond = view.getId();
        switch (view.getId()) {
            case R.id.date_time_timezone_btn:
                mActivity.newFragment(new TimezoneFragment());
                break;
            case R.id.date_time_time_setting_btn:
                mActivity.newFragment(new TimeSettingFragment());
                break;
        }
    }

    @Override
    public void onSwitch(View view, int index) {
        switch (view.getId()) {
            case R.id.date_time_auto_get_switch:
                DateTimeTool.setTimeAutoState(getContext().getApplicationContext(), index);
                refreshUI();
                break;
            case R.id.date_time_date_format_switch:
                DateTimeTool.setDateFormatByIndex(getContext().getApplicationContext(), index);
                break;
            case R.id.date_time_24h_switch:
                DateTimeTool.setTime24Hour(getContext().getApplicationContext(), index == 0);
                refreshUI();
                break;
        }
    }

    @Override
    public void update(Intent intent) {
        if (Intent.ACTION_TIME_TICK.equals(intent.getAction())
                || Intent.ACTION_TIME_CHANGED.equals(intent.getAction())
                || Intent.ACTION_DATE_CHANGED.equals(intent.getAction())
                || Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
            refreshUI();
        }
    }
}
