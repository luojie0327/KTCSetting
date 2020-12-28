package com.ktc.setting.view.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.ktc.setting.R;
import com.ktc.setting.helper.DestinyUtil;

public class LoadDialog extends Dialog {

    private ImageView mImageView;
    private TextView mTextView;

    public LoadDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public LoadDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_load_layout, null);
        setContentView(view);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        mImageView = (ImageView) view.findViewById(R.id.dialog_load_image);
        mTextView = (TextView) view.findViewById(R.id.dialog_load_text);
        startAnimation();
    }

    private void startAnimation() {
        RotateAnimation rotate = new RotateAnimation(0f, 360f
                , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(1000);
        rotate.setRepeatCount(-1);
        mImageView.setAnimation(rotate);
        rotate.start();
    }

    public void setMessageText(String text) {
        mTextView.setText(text);
    }

    /**
     * 设置dialog大小 单位:dp，设为0即为WRAP_CONTENT
     */
    public void setDialogSize(int width, int height) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (height > 0) {
            lp.height = DestinyUtil.dp2px(getContext(), height);
        } else {
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        if (width > 0) {
            lp.width = DestinyUtil.dp2px(getContext(), width);
        } else {
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        getWindow().setAttributes(lp);
    }

}
