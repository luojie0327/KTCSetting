package com.ktc.setting.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class IpEditText extends android.support.v7.widget.AppCompatEditText {

    public IpEditText(Context context) {
        super(context);
    }

    public IpEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public IpEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);

        String input = text.toString().trim();
        if (0 != input.length()) {

            int cursorLoc = getSelectionStart();
            int index = cursorLoc > 0 ? (cursorLoc - 1) : 0;
            char lastChar = input.charAt(index);

            if (input.length() <= 3) {
                int ip = Integer.parseInt(input);
                if (ip >= 256) {
                    setText(String.valueOf(lastChar));
                    setSelection(length());
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (this.hasSelection()) {
                this.setCursorVisible(true);
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (this.hasSelection()) {
                this.setCursorVisible(true);
            }
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (this.hasSelection()) {
                this.setCursorVisible(true);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
