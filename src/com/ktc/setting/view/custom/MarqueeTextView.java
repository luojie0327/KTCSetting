package com.ktc.setting.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView {

    private boolean canFocused = false;

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return isCanFocused();
    }

    public boolean isCanFocused() {
        return canFocused;
    }

    public void setCanFocused(boolean canFocused) {
        this.canFocused = canFocused;
    }
}
