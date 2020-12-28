package com.ktc.setting.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.setting.R;

public class EditButton extends RelativeLayout {

    private TextView mTitle;
    private EditText mValue;

    public EditButton(Context context) {
        super(context);
    }

    public EditButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EditButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        bindView(context);
        setBackgroundResource(R.drawable.setting_view_bg_normal);
        getAttributes(context, attrs);
        addListener();
    }

    private void bindView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_button_edit, this, true);
        mTitle = (TextView) findViewById(R.id.edit_button_title);
        mValue = (EditText) findViewById(R.id.edit_button_value);
    }

    private void getAttributes(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.EditButton);
        if (attributes != null) {
            String title = attributes.getString(R.styleable.EditButton_title);
            if (!TextUtils.isEmpty(title)) {
                mTitle.setText(title);
            }
            attributes.recycle();
        }
    }

    private void addListener() {
        setOnHoverListener(new OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                mValue.requestFocus();
                return true;
            }
        });
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

    public void setHint(String hint) {
        mValue.setHint(hint);
    }

    public void setValueTextVisible(boolean visible) {
        mValue.setVisibility(visible ? VISIBLE : INVISIBLE);
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

    public void setValueInputType(int type) {
        mValue.setInputType(type);
    }
}