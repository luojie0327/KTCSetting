package com.ktc.setting.view.universal.datetime;

import android.content.Intent;
import android.util.Log;
import android.view.View;


import com.ktc.setting.R;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.PickerView;
import com.ktc.setting.view.universal.UniversalActivity;
import com.ktc.setting.view.universal.datetime.observe.TimeObserver;

import java.util.Calendar;
import java.util.List;

public class TimeSettingFragment extends BaseFragment implements TimeObserver, PickerView.OnConfirmListener {

    private static final String TAG = TimeSettingFragment.class.getSimpleName();
    private PickerView yearPicker;
    private PickerView monthPicker;
    private PickerView dayPicker;
    private PickerView hourPicker;
    private PickerView minutePicker;
    private List<String> yearList;
    private List<String> monthList;
    private List<String> dayList;
    private List<String> hourList;
    private List<String> minuteList;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mRealMonth;
    private int mHour;
    private int mMinute;
    private boolean isMonthChange = false;

    @Override
    protected int getRes() {
        return R.layout.fragment_time_setting;
    }

    @Override
    protected int getTitle() {
        return R.string.str_universal_time_setting;
    }

    @Override
    protected void initView(View view) {
        yearPicker = (PickerView) view.findViewById(R.id.year_picker);
        monthPicker = (PickerView) view.findViewById(R.id.month_picker);
        dayPicker = (PickerView) view.findViewById(R.id.day_picker);
        hourPicker = (PickerView) view.findViewById(R.id.hour_picker);
        minutePicker = (PickerView) view.findViewById(R.id.minute_picker);
    }

    @Override
    protected void setFocus() {
        yearPicker.requestFocus();
    }

    @Override
    protected void initData() {
        ((UniversalActivity) getActivity()).mTimeObservation.addObserver(this);
        refreshSubtitle();
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mRealMonth = mMonth + 1;
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        yearList = DateTimeTool.getDataList(DateTimeTool.MIN_YEAR, DateTimeTool.MAX_YEAR);
        monthList = DateTimeTool.getDataList(1, 12);
        dayList = DateTimeTool.getDataList(1, DateTimeTool.getDaysOfMonth(mYear, mRealMonth));
        hourList = DateTimeTool.getDataList(0, 23);
        minuteList = DateTimeTool.getDataList(0, 59);

        yearPicker.setData(yearList);
        monthPicker.setData(monthList);
        dayPicker.setData(dayList);
        hourPicker.setData(hourList);
        minutePicker.setData(minuteList);

        yearPicker.setSelected(mYear - DateTimeTool.MIN_YEAR);
        monthPicker.setSelected(mRealMonth - 1);
        dayPicker.setSelected(mDay - 1);
        hourPicker.setSelected(mHour);
        minutePicker.setSelected(mMinute);
    }

    @Override
    protected void addListener() {
        yearPicker.setOnSelectListener(new PickerView.OnSelectListener() {
            @Override
            public void onSelect(String text, String original) {
                mYear = Integer.parseInt(text);
                refreshDayList();
            }
        });
        monthPicker.setOnSelectListener(new PickerView.OnSelectListener() {
            @Override
            public void onSelect(String text, String original) {
                mMonth = Integer.parseInt(text);
                isMonthChange = true;
                refreshDayList();
            }
        });
        dayPicker.setOnSelectListener(new PickerView.OnSelectListener() {
            @Override
            public void onSelect(String text, String original) {
                mDay = Integer.parseInt(text);
            }
        });
        hourPicker.setOnSelectListener(new PickerView.OnSelectListener() {
            @Override
            public void onSelect(String text, String original) {
                mHour = Integer.parseInt(text);
            }
        });
        minutePicker.setOnSelectListener(new PickerView.OnSelectListener() {
            @Override
            public void onSelect(String text, String original) {
                mMinute = Integer.parseInt(text);
            }
        });
        yearPicker.setOnConfirmListener(this);
        monthPicker.setOnConfirmListener(this);
        dayPicker.setOnConfirmListener(this);
        hourPicker.setOnConfirmListener(this);
        minutePicker.setOnConfirmListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((UniversalActivity) getActivity()).mTimeObservation.removeObserver(this);
    }

    private void refreshDayList() {
        int year = mYear;
        int month = mMonth;
        int day = mDay;
        int maxDay = DateTimeTool.getDaysOfMonth(year, month);
        dayPicker.setData(DateTimeTool.getDataList(1, maxDay));
        dayPicker.setSelected(day - 1);
        mDay = Integer.parseInt(dayPicker.getCurrentSelectedString());
    }

    private void refreshSubtitle() {
        mActivity.setSubTitle(DateTimeTool.getCurrentDateAndTime(getContext().getApplicationContext()));
    }

    @Override
    public void update(Intent intent) {
        if (Intent.ACTION_TIME_TICK.equals(intent.getAction())
                || Intent.ACTION_TIME_CHANGED.equals(intent.getAction())
                || Intent.ACTION_DATE_CHANGED.equals(intent.getAction())) {
            refreshSubtitle();
        }
    }

    @Override
    public void onConfirm() {
        Log.d(TAG, "year : " + mYear + " month : " + mMonth + " day : " + mDay
                + " hour : " + mHour + " minute : " + mMinute);
        if (isMonthChange) {
            DateTimeTool.setDate(getContext(), mYear, mMonth, mDay);
        } else {
            DateTimeTool.setDate(getContext(), mYear, mMonth + 1, mDay);
        }
        DateTimeTool.setTime(getContext(), mHour, mMinute);
        refreshSubtitle();
    }
}
