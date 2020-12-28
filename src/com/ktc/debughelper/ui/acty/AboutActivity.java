package com.ktc.debughelper.ui.acty;


import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;

import com.ktc.setting.R;

public class AboutActivity extends BaseDialogActivity<Void, String> {

    @Override
    public String loadData() {
        return context.getString(R.string.str_about_desc);
    }

    @Override
    public void performDataToUi(String result) {
        TextView textContent = createCenterTextView();
        try {
            textContent.setText(result + "\n\n当前版本：" + getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mContainerFL.addView(textContent);
    }

    @Override
    public Boolean beforeLoadUi() {
        mDialogTitleTv.setText(getString(R.string.str_home_about));
        mActionTv.setText(getString(R.string.str_base_dialog_exit));
        mActionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return true;
    }
}