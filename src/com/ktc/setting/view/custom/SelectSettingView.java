package com.ktc.setting.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.setting.R;

public class SelectSettingView extends RelativeLayout {

    private TextView selectTitle;
    private ImageView selectImage;
    private TextView selectContent;
    private OnItemSelectListener mOnItemSelectListener;
    private boolean isCheck = false;

    public SelectSettingView(Context context) {
        super(context);
        init(context, null);
    }

    public SelectSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setFocusable(true);
        setClickable(true);
        setFocusableInTouchMode(true);
        LayoutInflater.from(context).inflate(R.layout.view_setting_select, this, true);
        selectTitle = (TextView) findViewById(R.id.setting_select_title);
        selectContent = (TextView) findViewById(R.id.setting_select_content);
        selectImage = (ImageView) findViewById(R.id.setting_select_image);
        if (attrs != null) {
            initAttr(context, attrs);
        }
        addListener();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SelectSettingView);
        String titleString = ta.getString(R.styleable.SelectSettingView_selectTitle);
        String contentString = ta.getString(R.styleable.SelectSettingView_selectContent);
        boolean isSelected = ta.getBoolean(R.styleable.SelectSettingView_isSelect, false);
        ta.recycle();
        selectTitle.setText(titleString);
        selectContent.setText(contentString);
        setIsSelected(isSelected);
    }

    private void addListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                requestFocus();
                isCheck = !isCheck;
                setIsSelected(isCheck);
                if (mOnItemSelectListener != null) {
                    mOnItemSelectListener.onSelect(isCheck);
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
    }

    public void setSelectTitle(String text) {
        selectTitle.setText(text);
    }

    public void setSelectContent(String text) {
        selectContent.setText(text);
    }

    public void setIsSelected(boolean isSelected) {
        isCheck = isSelected;
        selectImage.setSelected(isSelected);
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        mOnItemSelectListener = onItemSelectListener;
    }

    public interface OnItemSelectListener {
        void onSelect(boolean isCheck);
    }
}
