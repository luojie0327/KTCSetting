package com.ktc.setting.view.universal.language;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.ktc.setting.R;
import com.ktc.setting.view.base.BaseActivity;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.PickerView;
import com.ktc.setting.view.custom.ToastFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LanguageFragment extends BaseFragment implements PickerView.OnSelectListener
        , PickerView.OnConfirmListener {

    private PickerView languagePickerView;
    private ArrayList<String> languageList;
    private Locale currentLocale;
    private int currentLocaleIndex;
    private ToastHandler mHandler;

    @Override
    protected int getRes() {
        return R.layout.fragment_language;
    }

    @Override
    protected int getTitle() {
        return R.string.str_universal_language_title;
    }

    @Override
    protected void initView(View view) {
        languagePickerView = (PickerView) view.findViewById(R.id.language_picker);
    }

    @Override
    protected void setFocus() {
        mActivity.preFocusViewIdFirst = R.id.universal_language_btn; //解决切换语言退出当前fragment后焦点问题
    }

    @Override
    protected void initData() {
        String[] names = LanguageTool.getLocaleDisplayName(getContext().getApplicationContext());
        List<String> localeStringList = LanguageTool.getAvailableLocaleStringList(getContext().getApplicationContext());
        currentLocaleIndex = LanguageTool.getCurrentLocaleIndex(getContext().getApplicationContext());
        languageList = new ArrayList<>();
        languageList.addAll(Arrays.asList(names));
        languagePickerView.setData(languageList, localeStringList);
        languagePickerView.setSelected(currentLocaleIndex);
        mHandler = new ToastHandler(mActivity);
    }

    @Override
    protected void addListener() {
        languagePickerView.setOnSelectListener(this);
        languagePickerView.setOnConfirmListener(this);
    }

    @Override
    public void onSelect(String text, String original) {
        if (original.contains("_")) {
            currentLocale = new Locale(original.split("_")[0], original.split("_")[1]);
        }
    }

    @Override
    public void onConfirm() {
        if (currentLocale != null
                && currentLocaleIndex != LanguageTool.getLocaleIndex(getActivity(), currentLocale)) {
            if (mHandler.hasMessages(100)) {
                mHandler.removeMessages(100);
            }
            LanguageTool.setLanguage(currentLocale);
            mActivity.getFragmentManager().popBackStack();
            LanguageFragment fragment = new LanguageFragment();
            mActivity.newFragmentWithoutStack(fragment);
            mHandler.sendEmptyMessageDelayed(100, 500);
        }
    }

    static class ToastHandler extends Handler {

        BaseActivity mActivity;
        WeakReference<BaseActivity> mWeakReference;

        ToastHandler(BaseActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            mActivity = mWeakReference.get();
            if (mActivity != null) {
                switch (msg.what) {
                    case 100:
                        ToastFactory.showToast(mActivity, mActivity.getString(R.string.str_universal_language_ok), Toast.LENGTH_SHORT);
                        break;
                }
            }
        }
    }
}
