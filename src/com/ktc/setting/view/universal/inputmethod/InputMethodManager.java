package com.ktc.setting.view.universal.inputmethod;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;

import java.util.ArrayList;
import java.util.List;

public class InputMethodManager {

    private static final String TAG = InputMethodManager.class.getSimpleName();
    private Context context;
    private PackageManager mPackageManager;

    public InputMethodManager(Context context) {
        this.context = context.getApplicationContext();
        mPackageManager = context.getPackageManager();
    }

    public List<InputMethodInfo> getImeList() {
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> methodList = imm.getInputMethodList();
        return methodList;
    }

    public List<String> getImeNameList() {
        List<String> mNameList = new ArrayList<>();
        List<InputMethodInfo> mMethodList = getImeList();
        for (InputMethodInfo info : mMethodList) {
            mNameList.add(info.loadLabel(mPackageManager).toString());
        }
        return mNameList;
    }

    public int getCurrentImeIndex() {
        List<InputMethodInfo> mMethodList = getImeList();
        for (int i = 0; i < mMethodList.size(); i++) {
            if (mMethodList.get(i).getId().equals(getDefaultImePackage())) {
                return i;
            }
        }
        return 0;
    }

    private String getDefaultImePackage() {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
    }

    public void setCurrentIme(String imeName) {
        List<InputMethodInfo> imeList = getImeList();
        for (InputMethodInfo imf : imeList) {
            if (imf.loadLabel(mPackageManager).toString().equals(imeName)) {
                Settings.Secure.putString(context.getContentResolver(),
                        Settings.Secure.DEFAULT_INPUT_METHOD,
                        imf.getId());
            }
        }
    }
}
