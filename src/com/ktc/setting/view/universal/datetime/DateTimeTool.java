package com.ktc.setting.view.universal.datetime;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.text.format.DateFormat;

import com.ktc.setting.R;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DateTimeTool {

    private static final String HOURS_12 = "12";
    private static final String HOURS_24 = "24";
    public static final int MAX_YEAR = 2033;
    public static final int MIN_YEAR = 1970;
    private static final int AUTO_TIME_ON = 0;
    private static final int AUTO_TIME_OFF = 1;

    public static boolean getTimeAutoState(Context context) {
        try {
            return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME) > 0;
        } catch (Settings.SettingNotFoundException e) {
            return false;
        }
    }

    public static void setTimeAutoState(Context context, int index) {
        switch (index) {
            case AUTO_TIME_ON:
                Settings.Global.putInt(context.getContentResolver(), Settings.Global.AUTO_TIME, 1);
                break;
            case AUTO_TIME_OFF:
                Settings.Global.putInt(context.getContentResolver(), Settings.Global.AUTO_TIME, 0);
                break;
        }
    }

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sf.format(calendar.getTime());
    }

    public static String getCurrentDateAndTime(Context context) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sf;
        if (isCurrent24Hour(context.getApplicationContext())) {
            sf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        } else {
            sf = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());
        }
        return sf.format(calendar.getTime());
    }

    public static boolean isCurrent24Hour(Context context) {
        return DateFormat.is24HourFormat(context);
    }

    public static void setTime24Hour(Context context, boolean is24Hour) {
        Settings.System.putString(context.getContentResolver(),
                Settings.System.TIME_12_24,
                is24Hour ? HOURS_24 : HOURS_12);
        timeUpdated(context, is24Hour);
    }

    private static void timeUpdated(Context context, boolean use24Hour) {
        Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
        int timeFormatPreference =
                use24Hour ? Intent.EXTRA_TIME_PREF_VALUE_USE_24_HOUR
                        : Intent.EXTRA_TIME_PREF_VALUE_USE_12_HOUR;
        timeChanged.putExtra(Intent.EXTRA_TIME_PREF_24_HOUR_FORMAT, timeFormatPreference);
        context.sendBroadcast(timeChanged);
    }

    public static int getDateFormatIndex(Context context) {
        String[] dateFormat = context.getResources().getStringArray(R.array.str_array_date_format);
        for (int i = 0; i < dateFormat.length; i++) {
            if (getDateFormat(context) == null) {
                return 0;
            }
            if (dateFormat[i].toLowerCase().equals(getDateFormat(context).toLowerCase())) {
                return i;
            }
        }
        return 0;
    }

    public static void setDateFormatByIndex(Context context, int index) {
        String[] dateFormat = context.getResources().getStringArray(R.array.str_array_date_format);
        if (index > dateFormat.length) {
            return;
        }
        setDateFormat(context, dateFormat[index]);
    }

    //TODO for date format
    public static String getDateFormat(Context context) {
        return Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT);
    }

    public static void setDateFormat(Context context, String format) {
        Settings.System.putString(context.getContentResolver(), Settings.System.DATE_FORMAT
                , format);
    }

    public static int getDaysOfMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        return a.get(Calendar.DATE);
    }

    public static List<String> getDataList(int minVal, int maxVal) {
        List<String> mDatas = new ArrayList<>();
        for (int i = minVal; i <= maxVal; i++) {
            mDatas.add("" + i);
        }
        return mDatas;
    }

    public static void setDate(Context context, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        if (year == 0)
            year = c.get(Calendar.YEAR);
        if (month == 0)
            month = c.get(Calendar.MONTH);
        else
            month = month - 1;
        if (day == 0)
            day = c.get(Calendar.DAY_OF_MONTH);

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        if (day > getDaysOfMonth(year, month + 1))
            c.set(Calendar.DAY_OF_MONTH, getDaysOfMonth(year, month + 1));
        else
            c.set(Calendar.DAY_OF_MONTH, day);
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }

    public static void setTime(Context context, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        if (hourOfDay == -1)
            hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (minute == -1)
            minute = c.get(Calendar.MINUTE);

        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }

    public static void setTimeZone(Context context, String tzId) {
        Log.d("dingyl","tz : " + tzId);
        MtkTvConfig.getInstance().setConfigValue(MtkTvConfigType.CFG_TIME_TZ_SYNC_WITH_TS, 0);
        final AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setTimeZone(tzId);
    }
}
