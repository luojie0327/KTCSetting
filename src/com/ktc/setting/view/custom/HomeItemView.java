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

public class HomeItemView extends RelativeLayout {

    private ImageView mImageView;
    private TextView mTextView;
    private RelativeLayout imageContainer;
    private OnItemClickListener mOnItemClickListener;

    public HomeItemView(Context context) {
        super(context);
        init(context, null);
    }

    public HomeItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_home_item, this, true);
        mImageView = (ImageView) findViewById(R.id.home_item_image);
        mTextView = (TextView) findViewById(R.id.home_item_text);
        imageContainer = (RelativeLayout) findViewById(R.id.home_item_image_container);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HomeItemView);
            int resId = ta.getResourceId(R.styleable.HomeItemView_image, 0);
            String text = ta.getString(R.styleable.HomeItemView_itemTitle);
            mImageView.setImageResource(resId);
            mTextView.setText(text);
            ta.recycle();
        }
        addListener();
    }

    private void addListener() {
        imageContainer.setBackgroundResource(R.drawable.home_item_background_normal);
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    imageContainer.setBackgroundResource(R.drawable.home_item_background_focus);
                } else {
                    imageContainer.setBackgroundResource(R.drawable.home_item_background_normal);
                }
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v);
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

    public void setImageView(int resId) {
        mImageView.setImageResource(resId);
    }

    public void setTextView(String text) {
        mTextView.setText(text);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view);
    }
}
