package com.ktc.setting.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.setting.R;

public class MainViewItem extends LinearLayout {

    private ImageView mIcon;
    private TextView mTitle;
    private RelativeLayout mIconContainer;

    public MainViewItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        bindView(context);
        getAttributes(context, attrs);
        addListener();

        setOrientation(VERTICAL);
        setFocusable(true);
        setClickable(true);
        setFocusableInTouchMode(true);
    }

    private void addListener() {
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, final boolean hasFocus) {
                ScaleAnimation scaleAnimation;
                if (hasFocus) {
                    scaleAnimation = new ScaleAnimation(
                            1.0f, 1.2f, 1.0f, 1.2f,
                            Animation.RELATIVE_TO_SELF, 0.5F,
                            Animation.RELATIVE_TO_SELF, 0.5F);
                    scaleAnimation.setDuration(50);
                    v.setElevation(1);

                    mTitle.setTextColor(getResources().getColor(R.color.colorMainViewTextSelect));
                    mTitle.setBackgroundColor(getResources().getColor(R.color.colorMainViewTextBackgroundSelect));
                    mIconContainer.setBackgroundColor(getResources().getColor(R.color.colorMainViewBackgroundSelect));
                    setBackgroundResource(R.drawable.main_view_bg);
                } else {
                    scaleAnimation = new ScaleAnimation(
                            1.2f, 1.0f, 1.2f, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.5F,
                            Animation.RELATIVE_TO_SELF, 0.5F);
                    scaleAnimation.setDuration(0);
                    v.setElevation(0);

                    mTitle.setTextColor(getResources().getColor(R.color.colorMainViewTextNormal));
                    mTitle.setBackgroundColor(getResources().getColor(R.color.colorMainViewTextBackgroundNormal));
                    mIconContainer.setBackgroundColor(getResources().getColor(R.color.colorMainViewBackgroundNormal));
                    setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                    setPadding(0, 0, 0, 0);
                }
                scaleAnimation.setFillAfter(true);
                v.startAnimation(scaleAnimation);
            }
        });

        setOnHoverListener(new OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                requestFocus();
                return true;
            }
        });
    }

    private void getAttributes(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.MainViewItem);
        if (attributes != null) {

            String title = attributes.getString(R.styleable.MainViewItem_title);
            if (!TextUtils.isEmpty(title)) {
                mTitle.setText(title);
            }

            int icon = attributes.getResourceId(R.styleable.MainViewItem_settingIcon, -1);
            if (icon != -1) {
                mIcon.setImageResource(icon);
            }

            float textHeight = attributes.getDimension(R.styleable.MainViewItem_text_height, 0);
            mTitle.getLayoutParams().height = ((int) textHeight);
            attributes.recycle();
        }
    }

    private void bindView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_item_main, this, true);
        mIcon = (ImageView) findViewById(R.id.main_view_icon);
        mTitle = (TextView) findViewById(R.id.main_view_title);
        mIconContainer = (RelativeLayout) findViewById(R.id.main_view_icon_container);
    }
}
