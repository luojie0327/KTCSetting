package com.ktc.setting.view.universal.language;

import android.app.ActivityManagerNative;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.internal.app.LocalePicker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.MissingResourceException;

public class LanguageTool {

    private static final String TAG = LanguageTool.class.getSimpleName();

    private static class LocaleComparator implements Comparator<LocalePicker.LocaleInfo> {
        @Override
        public int compare(LocalePicker.LocaleInfo l, LocalePicker.LocaleInfo r) {
            Locale lhs = l.getLocale();
            Locale rhs = r.getLocale();

            String lhsCountry = "";
            String rhsCountry = "";
            try {
                lhsCountry = lhs.getISO3Country();
            } catch (MissingResourceException e) {
                Log.e(TAG, "LocaleComparator cuaught exception, country set to empty.");
            }
            try {
                rhsCountry = rhs.getISO3Country();
            } catch (MissingResourceException e) {
                Log.e(TAG, "LocaleComparator cuaught exception, country set to empty.");
            }
            String lhsLang = "";
            String rhsLang = "";
            try {
                lhsLang = lhs.getISO3Language();
            } catch (MissingResourceException e) {
                Log.e(TAG, "LocaleComparator cuaught exception, language set to empty.");
            }
            try {
                rhsLang = rhs.getISO3Language();
            } catch (MissingResourceException e) {
                Log.e(TAG, "LocaleComparator cuaught exception, language set to empty.");
            }
            // if they're the same locale, return 0
            if (lhsCountry.equals(rhsCountry) && lhsLang.equals(rhsLang)) {
                return 0;
            }

            // prioritize US over other countries
            if ("USA".equals(lhsCountry)) {
                // if right hand side is not also USA, left hand side is first
                if (!"USA".equals(rhsCountry)) {
                    return -1;
                } else {
                    // if one of the languages is english we want to prioritize
                    // it, otherwise we don't care, just alphabetize
                    if ("ENG".equals(lhsLang) && "ENG".equals(rhsLang)) {
                        return 0;
                    } else {
                        return "ENG".equals(lhsLang) ? -1 : 1;
                    }
                }
            } else if ("USA".equals(rhsCountry)) {
                // right-hand side is the US and the other isn't, return greater than 1
                return 1;
            } else {
                // neither side is the US, sort based on display language name
                // then country name
                int langEquiv = lhs.getDisplayLanguage(lhs)
                        .compareToIgnoreCase(rhs.getDisplayLanguage(rhs));
                if (langEquiv == 0) {
                    return lhs.getDisplayCountry(lhs)
                            .compareToIgnoreCase(rhs.getDisplayCountry(rhs));
                } else {
                    return langEquiv;
                }
            }
        }
    }

    public static ArrayAdapter<LocalePicker.LocaleInfo> getAvailableLocale(Context context) {
        ArrayAdapter<LocalePicker.LocaleInfo> mLocales;
        mLocales = LocalePicker.constructAdapter(context);
        mLocales.sort(new LocaleComparator());
        return mLocales;
    }

    public static ArrayList<String> getAvailableLocaleStringList(Context context) {
        ArrayAdapter<LocalePicker.LocaleInfo> mLocales = getAvailableLocale(context);
        ArrayList<String> localeStringList = new ArrayList<>();
        for (int i = 0; i < mLocales.getCount(); i++) {
            localeStringList.add(mLocales.getItem(i).getLocale().getLanguage() + "_"
                    + mLocales.getItem(i).getLocale().getCountry());
        }
        return localeStringList;
    }

    public static String[] getLocaleDisplayName(Context context) {
        ArrayAdapter<LocalePicker.LocaleInfo> mLocales = getAvailableLocale(context);
        final String[] localeNames = new String[mLocales.getCount()];
        Locale currentLocale = getCurrentLocale();
        for (int i = 0; i < localeNames.length; i++) {
            Locale target = mLocales.getItem(i).getLocale();
            localeNames[i] = getDisplayName(context, target);
            if (localeNames[i].length() > 0) {
                localeNames[i] = localeNames[i].substring(0, 1).toUpperCase(currentLocale) +
                        localeNames[i].substring(1);
            }
        }
        return localeNames;
    }

    public static Locale getCurrentLocale() {
        Locale currentLocale;
        try {
            currentLocale = ActivityManagerNative.getDefault().getConfiguration().locale;
        } catch (RemoteException e) {
            currentLocale = null;
        }
        return currentLocale;
    }

    public static int getCurrentLocaleIndex(Context context) {
        Locale currentLocale = getCurrentLocale();
        ArrayAdapter<LocalePicker.LocaleInfo> availableLocale = getAvailableLocale(context);
        for (int i = 0; i < availableLocale.getCount(); i++) {
            if (availableLocale.getItem(i).getLocale().equals(currentLocale)) {
                return i;
            }
        }
        return 0;
    }

    public static int getLocaleIndex(Context context, Locale locale) {
        ArrayAdapter<LocalePicker.LocaleInfo> availableLocale = getAvailableLocale(context);
        for (int i = 0; i < availableLocale.getCount(); i++) {
            if (availableLocale.getItem(i).getLocale().equals(locale)) {
                return i;
            }
        }
        return 0;
    }

    public static String getDisplayName(Context context, Locale l) {
        final String[] specialLocaleCodes = context.getResources().getStringArray(
                com.android.internal.R.array.special_locale_codes);
        final String[] specialLocaleNames = context.getResources().getStringArray(
                com.android.internal.R.array.special_locale_names);
        String code = l.toString();

        for (int i = 0; i < specialLocaleCodes.length; i++) {
            if (specialLocaleCodes[i] == null
                    || code == null) {
                continue;
            }
            if (specialLocaleCodes[i].equals(code)) {
                return specialLocaleNames[i];
            }
        }
        return l.getDisplayName(l);
    }

    public static void setLanguage(Locale locale) {
        LocalePicker.updateLocale(locale);
    }
}
