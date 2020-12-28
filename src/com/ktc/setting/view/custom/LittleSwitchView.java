package com.ktc.setting.view.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.setting.R;

public class LittleSwitchView extends RelativeLayout {

    private LittleSwitchView mThis;
    private RelativeLayout mContainer;
    private TextView mTitle;
    private TextView mValue;
    private ImageView mLeftArrow;
    private ImageView mRightArrow;

    private CharSequence[] mValueArray;
    private boolean mEnable = true;
    private int mIndex = -1;
    private LittleSwitchView.OnSwitchListener mSwitchListener;

    public LittleSwitchView(Context context) {
        super(context);
    }

    public LittleSwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LittleSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        LayoutInflater.from(context).inflate(R.layout.view_switch_little, this, true);
        mContainer = (RelativeLayout) findViewById(R.id.switch_setting_container);
        mTitle = (TextView) findViewById(R.id.switch_setting_view_title);
        mValue = (TextView) findViewById(R.id.switch_setting_view_value);
        mLeftArrow = (ImageView) findViewById(R.id.switch_setting_view_left_arrow);
        mRightArrow = (ImageView) findViewById(R.id.switch_setting_view_right_arrow);
    }

    private void getAttributes(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.LittleSwitchView);
        if (attributes != null) {

            String title = attributes.getString(R.styleable.LittleSwitchView_title);
            if (!TextUtils.isEmpty(title)) {
                mTitle.setText(title);
            }

            mValueArray = attributes.getTextArray(R.styleable.LittleSwitchView_value);
            if (mValueArray != null && mValueArray.length > 0) {
                mIndex = 0;
                mValue.setText(mValueArray[mIndex]);
            }

            mEnable = attributes.getBoolean(R.styleable.LittleSwitchView_enable, true);
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

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mTitle.setSelected(b);
                mValue.setSelected(b);
                if (b) {
                    setBackgroundResource(R.color.colorSecond);
                } else {
                    setBackground(null);
                }
            }
        });
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

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public String getValueString() {
        return mValue.getText().toString();
    }

    @Override
    public boolean isEnabled() {
        return mEnable;
    }

    @Override
    public void setEnabled(boolean enable) {
        mEnable = enable;
        setClickable(mEnable);
        setFocusable(mEnable);
        setFocusableInTouchMode(mEnable);
        mContainer.setAlpha(mEnable ? (float) 1.0 : (float) 0.5);
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        if (mValueArray == null)
            return;

        if (index >= 0 && index < mValueArray.length) {
            mIndex = index;
            mValue.setText(mValueArray[mIndex]);
        }
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