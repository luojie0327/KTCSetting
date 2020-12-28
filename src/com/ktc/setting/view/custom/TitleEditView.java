package com.ktc.setting.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.setting.R;

public class TitleEditView extends RelativeLayout {

    private TextView mTitle;
    private EditText mValue;
    private CheckBox mShow;

    public TitleEditView(Context context) {
        this(context, null);
    }

    public TitleEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_edit_title, this);
        bindViews(context, attrs, defStyleAttr);
    }

    private void bindViews(Context context, AttributeSet attrs, int defStyle) {
        mTitle = (TextView) findViewById(R.id.title_edit_view_title);
        mValue = (EditText) findViewById(R.id.title_edit_view_value);
        mShow = (CheckBox) findViewById(R.id.show_password);

        initViews(context, attrs, defStyle);
        addListener(context);
        setFocusable(false);
        setFocusableInTouchMode(false);
    }

    private void initViews(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleEditView, defStyle, 0);
        String title = a.getString(R.styleable.TitleEditView_title);
        boolean passwordType = a.getBoolean(R.styleable.TitleEditView_passwordType, false);
        boolean showPassword = a.getBoolean(R.styleable.TitleEditView_showPassword, false);
        boolean enable = a.getBoolean(R.styleable.TitleEditView_enable, true);
        a.recycle();
        mTitle.setText(title);
        if (passwordType) {
            mValue.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mShow.setVisibility(VISIBLE);
            if (showPassword) {
                mValue.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                mValue.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        } else {
            mValue.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
            mShow.setVisibility(GONE);
        }
        setEnabled(enable);
    }

    private void addListener(final Context context) {
        mShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mValue.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mValue.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    public String getTitle() {
        return mTitle.getText().toString();
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public String getValue() {
        return mValue.getText().toString();
    }

    public void setValue(String value) {
        mValue.setText(value);
    }

    @Override
    public void setEnabled(boolean b) {
        setAlpha(b ? (float) 1.0 : (float) 0.5);
        mValue.setFocusable(b);
        mValue.setFocusableInTouchMode(b);
        mShow.setFocusable(b);
        mShow.setFocusableInTouchMode(b);
    }
}