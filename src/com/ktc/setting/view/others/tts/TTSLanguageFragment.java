package com.ktc.setting.view.others.tts;

import android.view.View;

import com.ktc.setting.R;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.PickerView;
import com.ktc.setting.view.others.OthersActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TTSLanguageFragment extends BaseFragment implements PickerView.OnSelectListener
        , PickerView.OnConfirmListener {

    private static final String TAG = TTSLanguageFragment.class.getSimpleName();
    private PickerView ttsLanguagePicker;
    private TTSManager mTTSManager;
    private String currentText;
    private Locale currentLocale;

    @Override
    protected int getRes() {
        return R.layout.fragment_tts_language;
    }

    @Override
    protected int getTitle() {
        return R.string.str_others_tts_title;
    }

    @Override
    protected void initView(View view) {
        ttsLanguagePicker = (PickerView) view.findViewById(R.id.tts_language_picker);
    }

    @Override
    protected void setFocus() {

    }

    @Override
    protected void initData() {
        mTTSManager = ((OthersActivity) getActivity()).mTTSManager;
        List<String> ttsLanguageNames = new ArrayList<>();
        List<Locale> ttsLanguages = mTTSManager.getTTSLanguageList();
        List<String> ttsLanguageString = new ArrayList<>();
        Locale currentLocale = mTTSManager.getCurrentTTSLanguage();
        for (Locale locale : ttsLanguages) {
            ttsLanguageNames.add(locale.getDisplayName());
            ttsLanguageString.add(locale.toString());
        }
        ttsLanguagePicker.setData(ttsLanguageNames, ttsLanguageString);
        if (currentLocale != null) {
            mActivity.setSubTitle(currentLocale.getDisplayName());
            for (int i = 0; i < ttsLanguages.size(); i++) {
                Locale locale = ttsLanguages.get(i);
                if (locale.getLanguage().equals(currentLocale.getLanguage()) && locale.getCountry()
                        .equals(currentLocale.getCountry())) {
                    ttsLanguagePicker.setSelected(i);
                }
            }
        }
    }

    @Override
    protected void addListener() {
        ttsLanguagePicker.setOnSelectListener(this);
        ttsLanguagePicker.setOnConfirmListener(this);
    }

    @Override
    public void onSelect(String text, String original) {
        currentText = text;
        currentLocale = new Locale(original.split("_")[0], original.split("_")[1]);
    }

    @Override
    public void onConfirm() {
        mActivity.setSubTitle(currentText);
        if (currentLocale != null) {
            mTTSManager.updateLanguageTo(currentLocale);
        }
    }
}
