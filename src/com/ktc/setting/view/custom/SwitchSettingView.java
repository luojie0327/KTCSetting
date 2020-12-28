package com.ktc.setting.view.custom;

import android.annotation.SuppressLint;
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

public class SwitchSettingView extends RelativeLayout {

    private SwitchSettingView mThis;
    private RelativeLayout mContainer;
    private ImageView mIcon;
    private TextView mTitle;
    private TextView mValue;
    private ImageView mLeftArrow;
    private ImageView mRightArrow;

    private CharSequence[] mValueArray;
    private boolean mEnable = true;
    private int mIndex = -1;
    private OnSwitchListener mSwitchListener;

    public SwitchSettingView(Context context) {
        super(context);
    }

    public SwitchSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SwitchSettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mThis = this;
        bindView(context);
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        getAttributes(context, attrs);
        addListener();
    }

    private void bindView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_setting_switch, this, true);
        mContainer = (RelativeLayout) findViewById(R.id.switch_setting_container);
        mIcon = (ImageView) findViewById(R.id.switch_setting_view_icon);
        mTitle = (TextView) findViewById(R.id.switch_setting_view_title);
        mValue = (TextView) findViewById(R.id.switch_setting_view_value);
        mLeftArrow = (ImageView) findViewById(R.id.switch_setting_view_left_arrow);
        mRightArrow = (ImageView) findViewById(R.id.switch_setting_view_right_arrow);
    }

    private void getAttributes(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SwitchSettingView);
        if (attributes != null) {

            String title = attributes.getString(R.styleable.SwitchSettingView_title);
            if (!TextUtils.isEmpty(title)) {
                mTitle.setText(title);
            }

            mValueArray = attributes.getTextArray(R.styleable.SwitchSettingView_value);
            if (mValueArray != null && mValueArray.length > 0) {
                mIndex = 0;
                mValue.setText(mValueArray[mIndex]);
            }

            int bg = attributes.getResourceId(R.styleable.SwitchSettingView_bg, -1);
            if (bg != -1) {
                setBackgroundResource(bg);
            } else {
                setBackgroundResource(R.drawable.setting_view_bg_normal);
            }

            int icon = attributes.getResourceId(R.styleable.SwitchSettingView_settingIcon, -1);
            if (icon != -1) {
                mIcon.setImageResource(icon);
                setIconVisible(true);
            } else {
                setIconVisible(false);
            }

            mEnable = attributes.getBoolean(R.styleable.SwitchSettingView_enable, true);
            setEnabled(mEnable);

            attributes.recycle();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addListener() {

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (mEnable) {
                            increaseIndex();
                            requestFocus();
                            if (mSwitchListener != null) {
                                mSwitchListener.onSwitch(v, mIndex);
                            }
                        }
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (mEnable) {
                            decreaseIndex();
                            requestFocus();
                            if (mSwitchListener != null && mEnable) {
                                mSwitchListener.onSwitch(v, mIndex);
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        mLeftArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEnable) {
                    decreaseIndex();
                    requestFocus();
                    if (mSwitchListener != null && mEnable) {
                        mSwitchListener.onSwitch(mThis, mIndex);
                    }
                }
            }
        });

        mRightArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEnable) {
                    increaseIndex();
                    requestFocus();
                    if (mSwitchListener != null) {
                        mSwitchListener.onSwitch(mThis, mIndex);
                    }
                }
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
            public void onFocusChange(View view, boolean b) {
                if (b) {
					mTitle.setSelected(true);
                    mValue.setSelected(true);
                } else {
					mTitle.setSelected(false);
                    mValue.setSelected(false);
                }
            }
        });
    }

    public void setTitle(String title) {
        mTitle.setText(title);
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

    @Override
    public void setEnabled(boolean enable) {
        mEnable = enable;
        setClickable(mEnable);
        setFocusable(mEnable);
        setFocusableInTouchMode(mEnable);
        mContainer.setAlpha(mEnable ? (float) 1.0 : (float) 0.5);
    }

    public void setIndex(int index) {
        if (mValueArray == null)
            return;

        if (index >= 0 && index < mValueArray.length) {
            mIndex = index;
            mValue.setText(mValueArray[mIndex]);
        }
    }

    public void setValueArray(CharSequence[] valueArray) {
        if (valueArray != null && valueArray.length > 0) {
            mValueArray = valueArray;
            setIndex(0);
        }
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

    public int getIndex() {
        return mIndex;
    }

    private void increaseIndex() {
        if (mValueArray != null && mValueArray.length > 0) {
            mIndex = ++mIndex % mValueArray.length;
        }
        setIndex(mIndex);
    }

    private void decreaseIndex() {
        if (mValueArray != null && mValueArray.length > 0) {
            mIndex--;
            if (mIndex < 0) {
                mIndex = mValueArray.length - 1;
            }
        }
        setIndex(mIndex);
    }

    public void setOnSwitchListener(OnSwitchListener listener) {
        mSwitchListener = listener;
    }

    public interface OnSwitchListener {
        void onSwitch(View view, int index);
    }
}
