package com.ktc.setting.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.ktc.setting.R;

public class SettingViewContainer extends RelativeLayout
        implements ViewTreeObserver.OnGlobalFocusChangeListener,
        ViewTreeObserver.OnGlobalLayoutListener {

    private LayoutParams mFocusLayoutParams;
    private FlyBorderView mFocusView;
    private View mNewFocus;

    private boolean mIsFirstInit = true;
    private int mDuration = 200;

    public SettingViewContainer(Context context) {
        super(context);
    }

    public SettingViewContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SettingViewContainer(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setClipChildren(false);
        this.mFocusLayoutParams = new RelativeLayout.LayoutParams(0, 0);
        this.mFocusView = new FlyBorderView(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingViewContainer);
        int focusBg = a.getResourceId(R.styleable.SettingViewContainer_focusBg, R.drawable.default_focus);
        mDuration = a.getInteger(R.styleable.SettingViewContainer_duration, 200);
        a.recycle();
        this.mFocusView.setBackgroundResource(focusBg);
        this.addView(this.mFocusView, this.mFocusLayoutParams);
        getViewTreeObserver().addOnGlobalFocusChangeListener(this);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        float scaleRatio = 1.04f;
        if (newFocus != null) {
            newFocus.requestFocus();

            if (newFocus instanceof Button
                    || newFocus instanceof RecyclerView
                    || newFocus instanceof ScrollView) {
                scaleRatio = 1;
            } else {
                scaleRatio = 1.04f;
            }

            if (newFocus instanceof IpEditText) {
                do {
                    newFocus = (View) newFocus.getParent();
                } while (!(newFocus instanceof IpInputView));
            }
            if (oldFocus instanceof IpEditText) {
                do {
                    oldFocus = (View) oldFocus.getParent();
                } while (!(oldFocus instanceof IpInputView));
            }

            if (newFocus instanceof EditText) {
                newFocus = (View) newFocus.getParent();
            }
            if (oldFocus instanceof EditText) {
                oldFocus = (View) oldFocus.getParent();
            }

            if (oldFocus == newFocus) {
                return;
            }
            if (oldFocus != null && !(oldFocus instanceof RecyclerView) && !(oldFocus instanceof ScrollView)) {
                newFocus.animate().scaleX(scaleRatio).translationZ(1).setDuration(mDuration).start();
                mFocusView.setDuration(mDuration);
            } else {
                newFocus.animate().scaleX(scaleRatio).translationZ(1).setDuration(0).start();
                mFocusView.setDuration(0);
            }
        }
        if (oldFocus != null) {
            oldFocus.animate().scaleX(1.0f).translationZ(0).setDuration(mDuration).start();
        }
        if (newFocus != null) {
            mFocusView.attachToView(newFocus, scaleRatio, 1.0f);
        } else {
            mFocusView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (mIsFirstInit) {
            mIsFirstInit = false;
            onGlobalFocusChanged(null, mNewFocus);
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }

    public void reAttachView() {
        View focus = getFocusedChild();
        if (focus != null) {
            if (focus instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) focus;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    if (viewGroup.getChildAt(i).hasFocus()) {
                        mFocusView.attachToView(viewGroup.getChildAt(i), 1.04f, 1.04f);
                        return;
                    }
                }
            } else {
                mFocusView.attachToView(focus, 1.04f, 1.04f);
            }
        }
    }

    public void setNewFocus(View v) {
        mNewFocus = v;
    }

}
