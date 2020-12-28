package com.ktc.setting.view.base;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ktc.setting.R;
import com.ktc.setting.view.universal.language.LanguageActivity;

public abstract class BaseActivity extends Activity {

    private TextView mTvTitle;
    private TextView mTvSubTitle;
    private FrameLayout mFrameLayout;
    public int preFocusViewIdFirst = 0;
    public int preFocusViewIdSecond = 0;
    public int preFocusViewIdThird = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_base);
        mTvTitle = (TextView) findViewById(R.id.title_bar);
        mTvSubTitle = (TextView) findViewById(R.id.sub_title);
        mFrameLayout = (FrameLayout) findViewById(R.id.content_fragment);
        BaseFragment fragment = getFragment();
        if (fragment != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_fragment, fragment);
            transaction.commit();
        }
    }

    protected abstract BaseFragment getFragment();

    public void newFragment(BaseFragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void newFragmentWithoutStack(BaseFragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_fragment, fragment);
        transaction.commit();
    }

    @Override
    public void setTitle(int titleRes) {
        mTvTitle.setText(titleRes);
    }

    public void setSubTitle(int subTitleRes) {
        mTvSubTitle.setText(subTitleRes);
    }

    public void setSubTitle(String subTitle) {
        mTvSubTitle.setText(subTitle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /*if (!(this instanceof LanguageActivity)) {
            recreate();
        }*/
    }
}
