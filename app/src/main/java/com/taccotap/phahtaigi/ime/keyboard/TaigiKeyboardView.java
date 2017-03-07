package com.taccotap.phahtaigi.ime.keyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

public class TaigiKeyboardView extends KeyboardView {

    public TaigiKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaigiKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean onLongPress(Key key) {
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            getOnKeyboardActionListener().onKey(CustomKeycode.KEYCODE_SETTINGS, null);
            return true;
        } else {
            return super.onLongPress(key);
        }
    }
}