package com.ktc.setting.view.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ktc.setting.R;

public class EditDialog extends Dialog {

    private String mTitle;
    private String mContent;
    private String mValue;
    private String mLeftStr;
    private String mRightStr;
    private View.OnClickListener mLeftListener;
    private View.OnClickListener mRightListener;

    private TextView mTitleTv;
    private TextView mContentTv;
    private EditText mValueEt;
    private Button mLeftButton;
    private Button mRightButton;

    public EditDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    public EditDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit);

        findView();
        initView();
    }

    private void findView() {
        mTitleTv = (TextView) findViewById(R.id.dialog_title);
        mContentTv = (TextView) findViewById(R.id.dialog_content);
        mValueEt = (EditText) findViewById(R.id.et_value);
        mLeftButton = (Button) findViewById(R.id.dialog_button_left);
        mRightButton = (Button) findViewById(R.id.dialog_button_right);
    }

    private void initView() {
        if (mTitle != null) {
            mTitleTv.setText(mTitle);
        }

        if (mContent != null) {
            mContentTv.setText(mContent);
            mContentTv.setVisibility(View.VISIBLE);
        }

        if (mValue != null) {
            mValueEt.setText(mValue);
        }

        if (mLeftStr != null) {
            mLeftButton.setText(mLeftStr);
            mLeftButton.setVisibility(View.VISIBLE);
        }

        if (mLeftListener != null) {
            mLeftButton.setOnClickListener(mLeftListener);
        }

        if (mRightStr != null) {
            mRightButton.setText(mRightStr);
            mRightButton.setVisibility(View.VISIBLE);
        }

        if (mRightListener != null) {
            mRightButton.setOnClickListener(mRightListener);
        }
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getValue() {
        return mValueEt.getText().toString();
    }

    public void setValue(String value) {
        mValue = value;
    }

    public void setLeftButtonText(String str) {
        mLeftStr = str;
    }

    public void setLeftButtonClickListener(View.OnClickListener listener) {
        mLeftListener = listener;
    }

    public void setRightButtonText(String str) {
        mRightStr = str;
    }

    public void setRightButtonClickListener(View.OnClickListener listener) {
        mRightListener = listener;
    }
}