package com.ktc.debughelper.ui.acty;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ktc.debughelper.presenter.view.BaseView;

public class BaseActivity extends Activity implements BaseView {

    public ProgressBar loadProBar;
    public Context context;
    public KToast mKToast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        mKToast = new KToast();
    }

    /**
     * change the window alpha value
     */
    public void setBackgroundAlpha(Float alpha) {
        WindowManager.LayoutParams attrs = this.getWindow().getAttributes();
        attrs.alpha = alpha;
        getWindow().setAttributes(attrs);
    }

    @Override
    public void showLoadingBar() {

    }

    @Override
    public void hideLoadingBar() {

    }

    public class KToast {
        private Toast mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);

        public void showToast(String str) {
            mToast.setText(str);
            mToast.show();
        }
    }
}