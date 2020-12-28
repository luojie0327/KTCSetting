package com.ktc.setting.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.ktc.setting.R;
import com.ktc.setting.helper.DestinyUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 滚动选择器
 */
public class PickerView extends View {

    public static final String TAG = PickerView.class.getSimpleName();
    /**
     * 自动回滚到中间的速度
     */
    public static final float SPEED = 10;

    private List<String> mDataList;
    private List<String> originalDataList;
    /**
     * 选中的位置，这个位置是mDataList的中心位置，一直不变
     */
    private int mCurrentSelected;
    private Paint mPaint;

    private float mTextSize1 = 70;
    private float mTextSize2 = 55;
    private float mTextSize3 = 50;
    private float mTextSize4 = 38;
    private float mTextSize5;

    private float mTextAlpha1 = 255f;
    private float mTextAlpha2 = 178.5f;
    private float mTextAlpha3 = 100f;
    private float mTextAlpha4 = 70f;
    private float mTextAlpha5;

    private int mViewHeight;
    private int mViewWidth;

    private float mLastDownY;
    /**
     * 滑动的距离
     */
    private float mMoveLen = 0;
    private boolean isInit = false;
    private OnSelectListener mSelectListener;
    private OnConfirmListener mOnConfirmListener;
    private Timer timer;
    private MyTimerTask mTask;

    private Drawable centerDrawable;
    private Drawable LinesDrawable;
    private int dividerDrawableRes;
    private int lineHeight1;
    private int lineHeight2;
    private int lineHeight3;//如果只需要绘制5行则不需要这个值，置为0即可
    private int dividerHeight;
    private int dividerWidth;
    private boolean isLineTwo;
    private int focusDrawableRes;


    //避免绘制期间创建过多对象
    private float d;
    private float scale;
    private float size;
    private int alpha;
    private float y;
    private float len;
    private float baseline;
    private float baseline2;
    private FontMetricsInt fmi;

    private UpdateHandler updateHandler = new UpdateHandler(this);

    static class UpdateHandler extends Handler {
        WeakReference<PickerView> mPickerNumberViewWeakReference;
        PickerView mPickerView;

        UpdateHandler(PickerView pickerView) {
            mPickerNumberViewWeakReference = new WeakReference<>(pickerView);
            mPickerView = mPickerNumberViewWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (Math.abs(mPickerView.mMoveLen) < SPEED) {
                mPickerView.mMoveLen = 0;
                if (mPickerView.mTask != null) {
                    mPickerView.mTask.cancel();
                    mPickerView.mTask = null;
                    mPickerView.performSelect();
                }
            } else
                // 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
                mPickerView.mMoveLen = mPickerView.mMoveLen - mPickerView.mMoveLen
                        / Math.abs(mPickerView.mMoveLen) * SPEED;
            mPickerView.invalidate();
        }
    }

    public PickerView(Context context) {
        super(context);
        init(context, null);
    }

    public PickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setOnSelectListener(OnSelectListener listener) {
        mSelectListener = listener;
    }

    private void performSelect() {
        if (mSelectListener != null)
            if (originalDataList != null) {
                mSelectListener.onSelect(mDataList.get(mCurrentSelected), originalDataList.get(mCurrentSelected));
            } else {
                mSelectListener.onSelect(mDataList.get(mCurrentSelected), null);
            }

    }

    public void setData(List<String> datas) {
        mDataList = datas;
        mCurrentSelected = datas.size() / 2;
        invalidate();
    }

    public void setData(List<String> datas, List<String> originalDataList) {
        mDataList = datas;
        mCurrentSelected = datas.size() / 2;
        this.originalDataList = originalDataList;
        invalidate();
    }

    /**
     * 选择选中的item的index
     */
    public void setSelected(int selected) {
        mCurrentSelected = selected;
        int distance = mDataList.size() / 2 - mCurrentSelected;
        if (distance < 0)
            for (int i = 0; i < -distance; i++) {
                moveHeadToTail();
                mCurrentSelected--;
            }
        else if (distance > 0)
            for (int i = 0; i < distance; i++) {
                moveTailToHead();
                mCurrentSelected++;
            }
        invalidate();
    }

    /**
     * 选择选中的内容
     */
    public void setSelected(String mSelectItem) {
        for (int i = 0; i < mDataList.size(); i++)
            if (mDataList.get(i).equals(mSelectItem)) {
                setSelected(i);
                break;
            }
    }

    public String getCurrentSelectedString() {
        return mDataList.get(mCurrentSelected);
    }

    //循环的原理是：往上滑，把头部加到底部；往下滑，把尾部加到头部
    private void moveHeadToTail() {
        String head = mDataList.get(0);
        mDataList.remove(0);
        mDataList.add(head);
        if (originalDataList != null) {
            String originalHead = originalDataList.get(0);
            originalDataList.remove(0);
            originalDataList.add(originalHead);
        }
    }

    private void moveTailToHead() {
        String tail = mDataList.get(mDataList.size() - 1);
        mDataList.remove(mDataList.size() - 1);
        mDataList.add(0, tail);
        if (originalDataList != null) {
            String originalTail = originalDataList.get(originalDataList.size() - 1);
            originalDataList.remove(originalDataList.size() - 1);
            originalDataList.add(0, originalTail);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
        isInit = true;
        invalidate();
    }

    private void init(Context context, AttributeSet attrs) {
        timer = new Timer();
        mDataList = new ArrayList<>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.FILL);
        mPaint.setTextAlign(Align.CENTER);
        mPaint.setColor(Color.WHITE);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PickerView);
            dividerDrawableRes = ta.getResourceId(R.styleable.PickerView_dividerNumberDrawable, 0);
            lineHeight1 = ta.getInteger(R.styleable.PickerView_lineHeight1, Integer.MAX_VALUE);
            lineHeight2 = ta.getInteger(R.styleable.PickerView_lineHeight2, Integer.MAX_VALUE);
            lineHeight3 = ta.getInteger(R.styleable.PickerView_lineHeight3, Integer.MAX_VALUE);
            dividerHeight = ta.getInteger(R.styleable.PickerView_dividerHeight, 0);
            dividerWidth = ta.getInteger(R.styleable.PickerView_dividerWidth, Integer.MAX_VALUE);
            isLineTwo = ta.getBoolean(R.styleable.PickerView_isLineTwo, false);
            focusDrawableRes = ta.getResourceId(R.styleable.PickerView_focusDrawable, 0);
            mTextSize1 = ta.getInteger(R.styleable.PickerView_textSize1, 0);
            mTextSize2 = ta.getInteger(R.styleable.PickerView_textSize2, 0);
            mTextSize3 = ta.getInteger(R.styleable.PickerView_textSize3, 0);
            mTextSize4 = ta.getInteger(R.styleable.PickerView_textSize4, 0);
            mTextSize5 = ta.getInteger(R.styleable.PickerView_textSize5, 0);
            mTextAlpha1 = ta.getFloat(R.styleable.PickerView_textAlpha1, 0);
            mTextAlpha2 = ta.getFloat(R.styleable.PickerView_textAlpha2, 0);
            mTextAlpha3 = ta.getFloat(R.styleable.PickerView_textAlpha3, 0);
            mTextAlpha4 = ta.getFloat(R.styleable.PickerView_textAlpha4, 0);
            mTextAlpha5 = ta.getFloat(R.styleable.PickerView_textAlpha5, 0);
            lineHeight1 = DestinyUtil.dp2px(getContext(), lineHeight1 / 1.5f);
            lineHeight2 = DestinyUtil.dp2px(getContext(), lineHeight2 / 1.5f);
            lineHeight3 = DestinyUtil.dp2px(getContext(), lineHeight3 / 1.5f);
            dividerHeight = DestinyUtil.dp2px(getContext(), dividerHeight / 1.5f);
            dividerWidth = DestinyUtil.dp2px(getContext(), dividerWidth / 1.5f);
            mTextSize1 = DestinyUtil.dp2px(getContext(), mTextSize1 / 1.5f);
            mTextSize2 = DestinyUtil.dp2px(getContext(), mTextSize2 / 1.5f);
            mTextSize3 = DestinyUtil.dp2px(getContext(), mTextSize3 / 1.5f);
            mTextSize4 = DestinyUtil.dp2px(getContext(), mTextSize4 / 1.5f);
            mTextSize5 = DestinyUtil.dp2px(getContext(), mTextSize5 / 1.5f);
            ta.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 根据index绘制view
        if (isInit) {
            initResourcesIfNecessary();
            drawCenterRect(canvas);
            drawData(canvas);
        }
    }

    private void drawData(Canvas canvas) {
        scale = linearScale(0, 0, 0, 0);
        size = (mTextSize1 - mTextSize2) * scale + mTextSize2;
        mPaint.setTextSize(size);
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha((int) ((mTextAlpha1 - mTextAlpha2) * scale + mTextAlpha2));
        y = (float) (mViewHeight / 2.0 + mMoveLen);
        fmi = mPaint.getFontMetricsInt();
        if (!isLineTwo) {
            baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
            canvas.drawText(mDataList.get(mCurrentSelected), (float) (mViewWidth / 2.0), baseline, mPaint);
        } else {
            baseline = y - fmi.descent;
            baseline2 = y - fmi.ascent;
            canvas.drawText(getFirstLineString(mDataList.get(mCurrentSelected)), (float) (mViewWidth / 2.0)
                    , baseline, mPaint);
            canvas.drawText(getSecondLineString(mDataList.get(mCurrentSelected)), (float) (mViewWidth / 2.0)
                    , baseline2, mPaint);
        }
        if (mDataList.size() >= 3) {
            drawOtherText(canvas, 1, -1);
            drawOtherText(canvas, 1, 1);
            if (mDataList.size() >= 5) {
                drawOther2Text(canvas, 2, -1);
                drawOther2Text(canvas, 2, 1);
            }
            if (lineHeight3 != Integer.MAX_VALUE && mDataList.size() >= 7) {
                drawOther3Text(canvas, 3, -1);
                drawOther3Text(canvas, 3, 1);
            }
        }
    }

    /**
     * @param canvas   画笔
     * @param position 距离mCurrentSelected的差值
     * @param type     1表示向下绘制，-1表示向上绘制
     */
    private void drawOtherText(Canvas canvas, int position, int type) {
        if (type * mMoveLen > 0) {
            d = lineHeight1 + type * mMoveLen * lineHeight2 / lineHeight1;
            scale = linearScale(type, mTextSize1, mTextSize2, mTextSize3);
            size = (mTextSize2 - mTextSize3) * scale + mTextSize3;
            alpha = (int) ((mTextAlpha2 - mTextAlpha3) * scale + mTextAlpha3);
        } else {
            d = lineHeight1 + type * mMoveLen;
            scale = linearScale(type, mTextSize1, mTextSize2, mTextSize3);
            size = (mTextSize2 - mTextSize3) * scale + mTextSize3;
            alpha = (int) ((mTextAlpha2 - mTextAlpha3) * scale + mTextAlpha3);
        }
        if (mTextAlpha2 == 255f) {
            alpha = (int) mTextAlpha2;
        }
        if (alpha > 255f) {
            alpha = (int) 255f;
        }
        mPaint.setTextSize(size);
        mPaint.setAlpha(alpha);
        y = (float) (mViewHeight / 2.0 + type * d);
        fmi = mPaint.getFontMetricsInt();
        if (!isLineTwo) {
            baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
            canvas.drawText(mDataList.get(mCurrentSelected + type * position),
                    (float) (mViewWidth / 2.0), baseline, mPaint);
        } else {
            baseline = y - fmi.descent;
            baseline2 = y - fmi.ascent;
            canvas.drawText(getFirstLineString(mDataList.get(mCurrentSelected + type)),
                    (float) (mViewWidth / 2.0), baseline, mPaint);
            canvas.drawText(getSecondLineString(mDataList.get(mCurrentSelected + type)),
                    (float) (mViewWidth / 2.0), baseline2, mPaint);
        }
    }

    private void drawOther2Text(Canvas canvas, int position, int type) {
        len = mMoveLen * lineHeight3 / lineHeight1;
        d = lineHeight2 * (position - 1) + type * len;
        scale = linearScale(type, mTextSize2, mTextSize3, mTextSize4);
        size = (mTextSize3 - mTextSize4) * scale + mTextSize4;
        alpha = (int) ((mTextAlpha3 - mTextAlpha4) * scale + mTextAlpha4);
        if (alpha > 255f) {
            alpha = (int) 255f;
        }
        mPaint.setTextSize(size);
        mPaint.setAlpha(alpha);
        y = (float) (mViewHeight / 2.0 + type * (d + lineHeight1));
        fmi = mPaint.getFontMetricsInt();
        if (!isLineTwo) {
            baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
            canvas.drawText(mDataList.get(mCurrentSelected + type * position),
                    (float) (mViewWidth / 2.0), baseline, mPaint);
        } else {
            baseline = y - fmi.descent;
            baseline2 = y - fmi.ascent;
            canvas.drawText(getFirstLineString(mDataList.get(mCurrentSelected + type * position)),
                    (float) (mViewWidth / 2.0), baseline, mPaint);
            canvas.drawText(getSecondLineString(mDataList.get(mCurrentSelected + type * position)),
                    (float) (mViewWidth / 2.0), baseline2, mPaint);
        }

    }

    private void drawOther3Text(Canvas canvas, int position, int type) {
        len = mMoveLen * lineHeight3 / lineHeight1;
        d = lineHeight3 * (position - 2) + type * len + lineHeight2;
        scale = linearScale(type, mTextSize3, mTextSize4, mTextSize5);
        size = (mTextSize4 - mTextSize5) * scale + mTextSize5;
        alpha = (int) ((mTextAlpha4 - mTextAlpha5) * scale + mTextAlpha5);
        if (alpha > mTextAlpha3) {
            alpha = (int) mTextAlpha3;
        }
        mPaint.setTextSize(size);
        mPaint.setAlpha(alpha);
        y = (float) (mViewHeight / 2.0 + type * (d + lineHeight1));
        fmi = mPaint.getFontMetricsInt();
        if (!isLineTwo) {
            baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
            canvas.drawText(mDataList.get(mCurrentSelected + type * position),
                    (float) (mViewWidth / 2.0), baseline, mPaint);
        } else {
            baseline = y - fmi.descent;
            baseline2 = y - fmi.ascent;
            canvas.drawText(getFirstLineString(mDataList.get(mCurrentSelected + type * position)),
                    (float) (mViewWidth / 2.0), baseline, mPaint);
            canvas.drawText(getSecondLineString(mDataList.get(mCurrentSelected + type * position)),
                    (float) (mViewWidth / 2.0), baseline2, mPaint);
        }
    }

    /**
     * @return scale
     */
    private float linearScale(int type, float size1, float size2, float size3) {
        if (type * mMoveLen >= 0) {
            return (lineHeight1 - Math.abs(mMoveLen)) / lineHeight1;
        } else {
            return (lineHeight1 + Math.abs(mMoveLen) * ((size1 - size3) * 1.0f / (size2
                    - size3) * 1.0f - 1)) / lineHeight1;
        }
    }

    /**
     * Initializes resources
     */
    private void initResourcesIfNecessary() {
        if (centerDrawable == null && focusDrawableRes != 0) {
            centerDrawable = getContext().getResources().getDrawable(focusDrawableRes);
        }
        if (LinesDrawable == null) {
            if (dividerDrawableRes != 0) {
                LinesDrawable = getContext().getResources().getDrawable(dividerDrawableRes);
            } else {
                LinesDrawable = new ColorDrawable(Color.WHITE);
            }
        }
    }

    /**
     * Draws rect for current value
     *
     * @param canvas the canvas for drawing
     */
    private void drawCenterRect(Canvas canvas) {
        int heightOffset = dividerHeight / 2;
        int widthOffset = (mViewWidth - dividerWidth) / 2;
        int lineWidth = DestinyUtil.dp2px(getContext(), 2);
        if (dividerWidth > getWidth()) {
            widthOffset = 0;
        }
        LinesDrawable.setBounds(widthOffset, mViewHeight / 2 + heightOffset, mViewWidth - widthOffset, mViewHeight / 2 + heightOffset + lineWidth);
        LinesDrawable.draw(canvas);
        LinesDrawable.setBounds(widthOffset, mViewHeight / 2 - heightOffset - lineWidth, mViewWidth - widthOffset, mViewHeight / 2 - heightOffset);
        LinesDrawable.draw(canvas);
        if (isFocused())
            setFocusedRect(canvas, mViewHeight / 2, heightOffset);
    }

    private void setFocusedRect(Canvas canvas, int center, int offset) {
        if (centerDrawable != null) {
            int hor = DestinyUtil.dp2px(getContext(), 11);
            int ver = DestinyUtil.dp2px(getContext(), 12);
            centerDrawable.setBounds(0 - hor, center - offset - ver, getWidth() + hor, center + offset + ver);
            centerDrawable.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                doDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                doMove(event);
                break;
            case MotionEvent.ACTION_UP:
                doUp();
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (mTask != null) {
                return true;
            }
            moveTailToHead();
            mMoveLen = mMoveLen - lineHeight1;
            mTask = new MyTimerTask(updateHandler);
            timer.schedule(mTask, 0, 20);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (mTask != null) {
                return true;
            }
            moveHeadToTail();
            mMoveLen = mMoveLen + lineHeight1;
            mTask = new MyTimerTask(updateHandler);
            timer.schedule(mTask, 0, 20);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (mOnConfirmListener != null) {
                mOnConfirmListener.onConfirm();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void doDown(MotionEvent event) {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mLastDownY = event.getY();
    }

    private void doMove(MotionEvent event) {

        mMoveLen += (event.getY() - mLastDownY);

        if (mMoveLen > lineHeight1 / 2) {
            // 往下滑超过离开距离
            moveTailToHead();
            mMoveLen = mMoveLen - lineHeight1;
        } else if (mMoveLen < -lineHeight1 / 2) {
            // 往上滑超过离开距离
            moveHeadToTail();
            mMoveLen = mMoveLen + lineHeight1;
        }

        mLastDownY = event.getY();
        invalidate();
    }

    private void doUp() {
        // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
        if (Math.abs(mMoveLen) < 0.0001) {
            mMoveLen = 0;
            return;
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mTask = new MyTimerTask(updateHandler);
        timer.schedule(mTask, 0, 10);
    }

    /**
     * 分别获取两行数据
     */
    private String getFirstLineString(String s) {
        if (s.contains("\n")) {
            return s.split("\n")[0];
        }
        return s;
    }

    private String getSecondLineString(String s) {
        if (s.contains("\n")) {
            return s.split("\n")[1];
        }
        return s;
    }

    class MyTimerTask extends TimerTask {
        Handler handler;

        MyTimerTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }

    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        mOnConfirmListener = onConfirmListener;
    }

    public interface OnSelectListener {
        void onSelect(String text, String original);
    }

    public interface OnConfirmListener {
        void onConfirm();
    }
}
