package com.taccotap.phahtaigi.ime.keyboard;

import android.inputmethodservice.Keyboard;
import android.view.inputmethod.InputMethodManager;

import com.taccotap.phahtaigi.R;
import com.taccotap.phahtaigi.ime.TaigiIme;
import com.taccotap.phahtaigi.ime.ui.TaigiKeyboardView;

public class KeyboardSwitcher {

    public static final int KEYBOARD_TYPE_LOMAJI_QWERTY = 0;
    public static final int KEYBOARD_TYPE_HANJI_QWERTY = 1;
    public static final int KEYBOARD_TYPE_LOMAJI_SYMBOL = 2;
    public static final int KEYBOARD_TYPE_LOMAJI_SYMBOL_SHIFTED = 3;
    public static final int KEYBOARD_TYPE_HANJI_SYMBOL = 4;
    public static final int KEYBOARD_TYPE_HANJI_SYMBOL_SHIFTED = 5;

    private final TaigiIme mTaigiIme;
    private final InputMethodManager mInputMethodManager;
    private final TaigiKeyboardView mTaigiKeyboardView;

    private TaigiKeyboard mLomajiQwertyKeyboard;
    private TaigiKeyboard mHanjiQwertyKeyboard;
    private TaigiKeyboard mLomajiSymbolsKeyboard;
    private TaigiKeyboard mLomajiSymbolsShiftedKeyboard;
    private TaigiKeyboard mHanjiSymbolsKeyboard;
    private TaigiKeyboard mHanjiSymbolsShiftedKeyboard;

    public KeyboardSwitcher(TaigiIme taigiIme, InputMethodManager inputMethodManager, TaigiKeyboardView taigiKeyboardView) {
        mTaigiIme = taigiIme;
        mInputMethodManager = inputMethodManager;
        mTaigiKeyboardView = taigiKeyboardView;

        mLomajiQwertyKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_lomaji_qwerty);
        mHanjiQwertyKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_hanji_qwerty);
        mLomajiSymbolsKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_lomaji_symbols);
        mLomajiSymbolsShiftedKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_lomaji_symbols_shift);
        mHanjiSymbolsKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_hanji_symbols);
        mHanjiSymbolsShiftedKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_hanji_symbols_shift);
    }

    public TaigiKeyboard getCurrentKeyboard() {
        return (TaigiKeyboard) mTaigiKeyboardView.getKeyboard();
    }

    public void setKeyboardByType(int keyboardType) {
        TaigiKeyboard newKeyboard;

        if (keyboardType == KEYBOARD_TYPE_LOMAJI_QWERTY) {
            newKeyboard = mLomajiQwertyKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_HANJI_QWERTY) {
            newKeyboard = mHanjiQwertyKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_LOMAJI_SYMBOL) {
            newKeyboard = mLomajiSymbolsKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_LOMAJI_SYMBOL_SHIFTED) {
            newKeyboard = mLomajiSymbolsShiftedKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_HANJI_SYMBOL) {
            newKeyboard = mHanjiSymbolsKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_HANJI_SYMBOL_SHIFTED) {
            newKeyboard = mHanjiSymbolsShiftedKeyboard;
        } else {
            newKeyboard = mLomajiQwertyKeyboard;
        }

        mTaigiKeyboardView.setKeyboard(newKeyboard);

        resetKeyboard();
    }

    public void resetKeyboard() {
        TaigiKeyboard currentKeyboard = (TaigiKeyboard) mTaigiKeyboardView.getKeyboard();
        mTaigiKeyboardView.setKeyboard(currentKeyboard);
    }

    public boolean isCurrentKeyboardViewUseQwertyKeyboard() {
        return mTaigiKeyboardView.getKeyboard() == mLomajiQwertyKeyboard
                || mTaigiKeyboardView.getKeyboard() == mHanjiQwertyKeyboard;
    }

    public void switchKeyboard() {
        TaigiKeyboard currentKeyboard = (TaigiKeyboard) mTaigiKeyboardView.getKeyboard();
        TaigiKeyboard nextKeyboard = mLomajiQwertyKeyboard;

        if (currentKeyboard == mLomajiQwertyKeyboard) {
            nextKeyboard = mLomajiSymbolsKeyboard;
        } else if (currentKeyboard == mHanjiQwertyKeyboard) {
            nextKeyboard = mHanjiSymbolsKeyboard;
        } else if (currentKeyboard == mLomajiSymbolsKeyboard || currentKeyboard == mLomajiSymbolsShiftedKeyboard) {
            nextKeyboard = mLomajiQwertyKeyboard;
            nextKeyboard.setShifted(false);
        } else if (currentKeyboard == mHanjiSymbolsKeyboard || currentKeyboard == mHanjiSymbolsShiftedKeyboard) {
            nextKeyboard = mHanjiQwertyKeyboard;
            nextKeyboard.setShifted(false);
        }

        mTaigiKeyboardView.setKeyboard(nextKeyboard);
    }

    public void handleShift() {
        Keyboard currentKeyboard = mTaigiKeyboardView.getKeyboard();

//        if (currentKeyboard == mLomajiSymbolsKeyboard) {
//            mLomajiSymbolsKeyboard.setShifted(true);
//            setKeyboardByType(KEYBOARD_TYPE_LOMAJI_SYMBOL_SHIFTED);
//            mLomajiSymbolsShiftedKeyboard.setShifted(true);
//        } else if (currentKeyboard == mLomajiSymbolsShiftedKeyboard) {
//            mLomajiSymbolsShiftedKeyboard.setShifted(false);
//            setKeyboardByType(KEYBOARD_TYPE_LOMAJI_SYMBOL);
//            mLomajiSymbolsKeyboard.setShifted(false);
//        }

        if (currentKeyboard == mLomajiSymbolsKeyboard) {
            mTaigiKeyboardView.setKeyboard(mLomajiSymbolsShiftedKeyboard);
        } else if (currentKeyboard == mLomajiSymbolsShiftedKeyboard) {
            mTaigiKeyboardView.setKeyboard(mLomajiSymbolsKeyboard);
        } else if (currentKeyboard == mHanjiSymbolsKeyboard) {
            mTaigiKeyboardView.setKeyboard(mHanjiSymbolsShiftedKeyboard);
        } else if (currentKeyboard == mHanjiSymbolsShiftedKeyboard) {
            mTaigiKeyboardView.setKeyboard(mHanjiSymbolsKeyboard);
        }
    }
}
