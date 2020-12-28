package com.ktc.setting.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.setting.R;

public class ButtonSettingView extends RelativeLayout {

    private RelativeLayout mContainer;
    private ImageView mIcon;
    private MarqueeTextView mTitle;
    private MarqueeTextView mValue;
    private ImageView mRightArrow;

    private boolean mEnable = true;
    private OnButtonClickListener mClickListener = null;

    public ButtonSettingView(Context context) {
        this(context, null);
    }

    public ButtonSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ButtonSettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        bindView(context);
        setBackgroundResource(R.drawable.setting_view_bg_normal);
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        if (attrs != null) {
            getAttributes(context, attrs);
        }
        addListener();
    }

    private void bindView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_setting_button, this, true);
        mContainer = (RelativeLayout) findViewById(R.id.button_setting_container);
        mIcon = (ImageView) findViewById(R.id.button_setting_view_icon);
        mTitle = (MarqueeTextView) findViewById(R.id.button_setting_view_title);
        mValue = (MarqueeTextView) findViewById(R.id.button_setting_view_value);
        mRightArrow = (ImageView) findViewById(R.id.button_setting_view_right_arrow);
    }

    private void getAttributes(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ButtonSettingView);
        if (attributes != null) {

            String title = attributes.getString(R.styleable.ButtonSettingView_title);
            if (!TextUtils.isEmpty(title)) {
                mTitle.setText(title);
            }

            String value = attributes.getString(R.styleable.ButtonSettingView_value);
            if (!TextUtils.isEmpty(value)) {
                mValue.setText(value);
            } else {
                mValue.setText("");
            }

            int icon = attributes.getResourceId(R.styleable.ButtonSettingView_settingIcon, -1);
            if (icon != -1) {
                mIcon.setImageResource(icon);
                setIconVisible(true);
            } else {
                setIconVisible(false);
            }

            boolean arrowShow = attributes.getBoolean(R.styleable.ButtonSettingView_right_arrow_visible, false);
            setRightArrowVisible(arrowShow);

            mEnable = attributes.getBoolean(R.styleable.ButtonSettingView_enable, true);
            setEnabled(mEnable);

            attributes.recycle();
        }
    }

    private void addListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null && mEnable) {
                    mClickListener.onClick(v);
                }
            }
        });

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                        && mRightArrow.getVisibility() == VISIBLE
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mClickListener != null && mEnable) {
                        mClickListener.onClick(v);
                    }
                    return true;
                }

                return false;
            }
        });

        setOnHoverListener(new OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                requestFocus();
                requestFocusFromTouch();
                return true;
            }
        });

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    mTitle.setSelected(true);
                    mValue.setSelected(true);
                }else {
                    mTitle.setSelected(false);
                    mValue.setSelected(false);
                }
            }
        });
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setTile(int stringRes) {
        mTitle.setText(stringRes);
    }

    public void setValue(String value) {
        mValue.setText(value);
        mValue.setVisibility(VISIBLE);
    }

    public void setValue(int valueRes) {
        mValue.setText(valueRes);
    }

    public void setTextSize(float size) {
        mTitle.setTextSize(size);
        mValue.setTextSize(size);
    }

    public void setValueTextVisible(boolean visible) {
        mValue.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    public void setIconVisible(boolean visible) {
        if (visible) {
            mIcon.setVisibility(VISIBLE);
            RelativeLayout.LayoutParams lp = (LayoutParams) mTitle.getLayoutParams();
            lp.addRule(RelativeLayout.END_OF, mIcon.getId());
            lp.setMarginStart((int) getResources().getDimension(R.dimen.marginStartSettingTitleWithIcon));
            mTitle.setLayoutParams(lp);
        } else {
            mIcon.setVisibility(INVISIBLE);
            RelativeLayout.LayoutParams lp = (LayoutParams) mTitle.getLayoutParams();
            lp.removeRule(RelativeLayout.END_OF);
            lp.setMarginStart((int) getResources().getDimension(R.dimen.marginStartSettingTitleNoIcon));
            mTitle.setLayoutParams(lp);
        }
    }

    public void setRightArrowVisible(boolean visible) {
        if (visible) {
            mRightArrow.setVisibility(VISIBLE);
            RelativeLayout.LayoutParams lp = (LayoutParams) mValue.getLayoutParams();
            lp.removeRule(RelativeLayout.ALIGN_PARENT_END);
            lp.addRule(RelativeLayout.START_OF, mRightArrow.getId());
            lp.setMarginEnd((int) getResources().getDimension(R.dimen.marginStartSettingTitleWithIcon));
            mValue.setLayoutParams(lp);
        } else {
            mRightArrow.setVisibility(GONE);
            RelativeLayout.LayoutParams lp = (LayoutParams) mValue.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_END);
            lp.removeRule(RelativeLayout.START_OF);
            lp.setMarginEnd((int) getResources().getDimension(R.dimen.marginStartSettingTitleNoIcon));
            mValue.setLayoutParams(lp);
        }
    }

    @Override
    public void setEnabled(boolean enable) {
        mEnable = enable;
        setClickable(mEnable);
        setFocusable(mEnable);
        setFocusableInTouchMode(mEnable);
        mContainer.setAlpha(mEnable ? (float) 1.0 : (float) 0.5);
    }

    public String getTitle() {
        return mTitle.getText().toString();
    }

    public String getValueString() {
        return mValue.getText().toString();
    }

    @Override
    public boolean isEnabled() {
        return mEnable;
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        mClickListener = listener;
    }

    public interface OnButtonClickListener {
        void onClick(View view);
    }
}
