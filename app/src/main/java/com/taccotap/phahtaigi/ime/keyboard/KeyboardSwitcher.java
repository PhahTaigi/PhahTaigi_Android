package com.taccotap.phahtaigi.ime.keyboard;

import android.inputmethodservice.Keyboard;
import android.view.inputmethod.InputMethodManager;

import com.taccotap.phahtaigi.R;
import com.taccotap.phahtaigi.ime.TaigiIme;
import com.taccotap.phahtaigi.ime.ui.TaigiKeyboardView;

public class KeyboardSwitcher {

    public static final int KEYBOARD_TYPE_QWERTY_LOMAJI = 0;
    public static final int KEYBOARD_TYPE_QWERTY_HANJI = 1;
    public static final int KEYBOARD_TYPE_SYMBOL = 2;
    public static final int KEYBOARD_TYPE_SYMBOL_SHIFTED = 3;

    private final TaigiIme mTaigiIme;
    private final InputMethodManager mInputMethodManager;
    private final TaigiKeyboardView mTaigiKeyboardView;

    private TaigiKeyboard mQwertyLomajiKeyboard;
    private TaigiKeyboard mQwertyHanjiKeyboard;
    private TaigiKeyboard mSymbolsKeyboard;
    private TaigiKeyboard mSymbolsShiftedKeyboard;
    private TaigiKeyboard mSymbolsHanjiKeyboard;
    private TaigiKeyboard mSymbolsShiftedHanjiKeyboard;

    private int mPreviousSwitchKeyboardTypeCode = -1;

    public KeyboardSwitcher(TaigiIme taigiIme, InputMethodManager inputMethodManager, TaigiKeyboardView taigiKeyboardView) {
        mTaigiIme = taigiIme;
        mInputMethodManager = inputMethodManager;
        mTaigiKeyboardView = taigiKeyboardView;

        mQwertyLomajiKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_taigi_qwerty_lomaji);
        mQwertyHanjiKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_taigi_qwerty_hanji);
        mSymbolsKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_taigi_symbols_for_lomaji);
        mSymbolsShiftedKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_taigi_symbols_shift_for_lomaji);
        mSymbolsHanjiKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_taigi_symbols_for_hanji);
        mSymbolsShiftedHanjiKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_taigi_symbols_shift_for_hanji);
    }

    public TaigiKeyboard getCurrentKeyboard() {
        return (TaigiKeyboard) mTaigiKeyboardView.getKeyboard();
    }

    public void setKeyboard(int keyboardType) {
        TaigiKeyboard newKeyboard;

        if (keyboardType == KEYBOARD_TYPE_QWERTY_LOMAJI) {
            newKeyboard = mQwertyLomajiKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_QWERTY_HANJI) {
            newKeyboard = mQwertyHanjiKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_SYMBOL) {
            newKeyboard = mSymbolsKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_SYMBOL_SHIFTED) {
            newKeyboard = mSymbolsShiftedKeyboard;
        } else {
            newKeyboard = mQwertyLomajiKeyboard;
        }

        mTaigiKeyboardView.setKeyboard(newKeyboard);

        resetKeyboard();
    }

    public void resetKeyboard() {
        TaigiKeyboard currentKeyboard = (TaigiKeyboard) mTaigiKeyboardView.getKeyboard();
        mTaigiKeyboardView.setKeyboard(currentKeyboard);
    }

    public boolean isCurrentKeyboardViewUseQwertyKeyboard() {
        return mTaigiKeyboardView.getKeyboard() == mQwertyLomajiKeyboard
                || mTaigiKeyboardView.getKeyboard() == mQwertyHanjiKeyboard;
    }

    public void switchKeyboard() {
        TaigiKeyboard currentKeyboard = (TaigiKeyboard) mTaigiKeyboardView.getKeyboard();

        if (currentKeyboard == mQwertyLomajiKeyboard) {
            mPreviousSwitchKeyboardTypeCode = KEYBOARD_TYPE_QWERTY_LOMAJI;
            mTaigiKeyboardView.setKeyboard(mSymbolsKeyboard);
        } else if (currentKeyboard == mQwertyHanjiKeyboard) {
            mPreviousSwitchKeyboardTypeCode = KEYBOARD_TYPE_QWERTY_HANJI;
            mTaigiKeyboardView.setKeyboard(mSymbolsKeyboard);
        } else {
            if (mPreviousSwitchKeyboardTypeCode == KEYBOARD_TYPE_QWERTY_LOMAJI) {
                mTaigiKeyboardView.setKeyboard(mQwertyLomajiKeyboard);
            } else if (mPreviousSwitchKeyboardTypeCode == KEYBOARD_TYPE_QWERTY_HANJI) {
                mTaigiKeyboardView.setKeyboard(mQwertyHanjiKeyboard);
            }
            mTaigiKeyboardView.getKeyboard().setShifted(false);
        }
    }

    public void handleShift() {
        Keyboard currentKeyboard = mTaigiKeyboardView.getKeyboard();

        if (currentKeyboard == mSymbolsKeyboard) {
            mSymbolsKeyboard.setShifted(true);
            setKeyboard(KEYBOARD_TYPE_SYMBOL_SHIFTED);
            mSymbolsShiftedKeyboard.setShifted(true);
        } else if (currentKeyboard == mSymbolsShiftedKeyboard) {
            mSymbolsShiftedKeyboard.setShifted(false);
            setKeyboard(KEYBOARD_TYPE_SYMBOL);
            mSymbolsKeyboard.setShifted(false);
        }
    }
}
