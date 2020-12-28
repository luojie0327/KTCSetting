package com.ktc.setting.view.custom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
                View focusedView = getFocusedChild();
                if (focusedView instanceof SettingViewContainer) {
                    for (int i = 0; i < ((SettingViewContainer) focusedView).getChildCount(); i++) {
                        View child = ((SettingViewContainer) focusedView).getChildAt(i);
                        if (child.hasFocus() && child instanceof RecyclerView) {
                            return false;
                        }
                    }
                }
        }
        return super.dispatchKeyEvent(event);
    }
}
