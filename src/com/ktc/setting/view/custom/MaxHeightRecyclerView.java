package com.ktc.setting.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.ktc.setting.R;

public class MaxHeightRecyclerView extends RecyclerView {

    public static final int EATKEYEVENT = 5;// 是否屏蔽按键的事件，控制按键的频率
    private static final int keyEventTime = 200; //最短的按键事件应该是在100ms
    private static boolean eatKeyEvent = false;

    private int mMaxHeight;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EATKEYEVENT:
                    eatKeyEvent = false;
            }
        }
    };

    public MaxHeightRecyclerView(Context context) {
        super(context);
    }

    public MaxHeightRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public MaxHeightRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightRecyclerView);
        mMaxHeight = arr.getLayoutDimension(R.styleable.MaxHeightRecyclerView_maxHeight, mMaxHeight);
        arr.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMaxHeight > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            return super.dispatchKeyEvent(event);
        }

        if (eatKeyEvent) {
            return true;
        } else {
            if (event.getRepeatCount() >= 2) {
                eatKeyEvent = true;
                mHandler.removeMessages(EATKEYEVENT);
                Message msg = mHandler.obtainMessage(EATKEYEVENT);
                mHandler.sendMessageDelayed(msg, keyEventTime);
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
