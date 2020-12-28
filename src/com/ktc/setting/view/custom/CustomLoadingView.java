package com.ktc.setting.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import java.lang.ref.WeakReference;

public class CustomLoadingView extends View {

    private int radius = 5;
    private Paint mPaint;
    private DrawHandler mDrawHandler;
    private int mCount = -1;
    private static final int INVALIDATE = 0;
    private static final int REFRESH = 1;

    public CustomLoadingView(Context context) {
        super(context);
        init();
    }

    public CustomLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mDrawHandler = new DrawHandler(this);
        mDrawHandler.sendEmptyMessage(INVALIDATE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas, mCount % 4);
        mCount++;
    }

    private void drawCircle(Canvas canvas, int count) {
        if (count == 3 || count == -1) return;
        canvas.drawCircle(radius, radius, radius, mPaint);
        if (count == 0) {
            mPaint.setColor(Color.TRANSPARENT);
        }
        canvas.drawCircle(radius * 4, radius, radius, mPaint);
        if (count == 1) {
            mPaint.setColor(Color.TRANSPARENT);
        }
        canvas.drawCircle(radius * 7, radius, radius, mPaint);
        mPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != GONE) {
            if (mDrawHandler != null) {
                Message message = mDrawHandler.obtainMessage();
                message.what = REFRESH;
                mDrawHandler.sendMessageAtFrontOfQueue(message);
            }
        }
    }

    static class DrawHandler extends Handler {

        CustomLoadingView mLoadingView;
        WeakReference<CustomLoadingView> mWeakReference;

        DrawHandler(CustomLoadingView view) {
            mWeakReference = new WeakReference<>(view);
            mLoadingView = mWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH:
                    mLoadingView.mCount = 0;
                    break;
                case INVALIDATE:
                    sendEmptyMessageDelayed(INVALIDATE, 500);
                    break;
            }
            mLoadingView.invalidate();
        }
    }
}
