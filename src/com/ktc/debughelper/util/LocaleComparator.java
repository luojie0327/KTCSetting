package com.ktc.debughelper.util;

import android.util.Log;

import com.android.internal.app.LocalePicker;

import java.util.Comparator;
import java.util.Locale;
import java.util.MissingResourceException;

public class LocaleComparator implements Comparator<LocalePicker.LocaleInfo> {
    private String TAG = "LocaleComparator";

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