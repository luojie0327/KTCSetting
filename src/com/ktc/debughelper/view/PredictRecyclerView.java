package com.ktc.debughelper.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * @author longzj
 */
public class PredictRecyclerView extends RecyclerView {

    private static final int DIR_LEFT = 0;
    private static final int DIR_RIGHT = 1;

    public PredictRecyclerView(Context context) {
        super(context);
    }

    public PredictRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PredictRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //    @Override
    //    public boolean dispatchKeyEvent(KeyEvent event) {
    //        int keyCode = event.getKeyCode();
    //        int action = event.getAction();
    //        if (action == KeyEvent.ACTION_DOWN) {
    //            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
    //                    && decideFocus(DIR_LEFT)) {
    //                return true;
    //            }
    //            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
    //                    && decideFocus(DIR_RIGHT)) {
    //                return true;
    //            }
    //        }
    //        return super.dispatchKeyEvent(event);
    //    }
    //
    //    boolean decideFocus(int direction) {
    //        View focusedView = getFocusedChild();
    //        if (focusedView == null) {
    //            return false;
    //        }
    //        View searchView = null;
    //        if (direction == DIR_LEFT) {
    //            searchView = focusedView.focusSearch(FOCUS_LEFT);
    //        } else if (direction == DIR_RIGHT) {
    //            searchView = focusedView.focusSearch(FOCUS_RIGHT);
    //        }
    //        if (searchView == null) {
    //            return false;
    //        }
    //        int pos = indexOfChild(searchView);
    //        return pos == NO_POSITION;
    //
    //    }
}
