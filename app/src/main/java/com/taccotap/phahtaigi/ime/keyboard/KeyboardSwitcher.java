package com.taccotap.phahtaigi.ime.keyboard;

import android.content.res.Resources;
import android.inputmethodservice.Keyboard;
import android.view.inputmethod.InputMethodManager;

import com.taccotap.phahtaigi.R;
import com.taccotap.phahtaigi.ime.TaigiIme;
import com.taccotap.phahtaigi.ime.candidate.TaigiCandidateView;

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
    private TaigiCandidateView mTaigiCandidateView;

    private TaigiKeyboard mLomajiQwertyKeyboard;
    private TaigiKeyboard mHanjiQwertyKeyboard;
    private TaigiKeyboard mLomajiSymbolsKeyboard;
    private TaigiKeyboard mLomajiSymbolsShiftedKeyboard;
    private TaigiKeyboard mHanjiSymbolsKeyboard;
    private TaigiKeyboard mHanjiSymbolsShiftedKeyboard;

    private int mImeOptions;

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

    public void setTaigiCandidateView(TaigiCandidateView taigiCandidateView) {
        mTaigiCandidateView = taigiCandidateView;
    }

    public TaigiKeyboard getCurrentKeyboard() {
        return (TaigiKeyboard) mTaigiKeyboardView.getKeyboard();
    }

    public void setKeyboardByType(int keyboardType) {
        TaigiKeyboard nextKeyboard;

        if (keyboardType == KEYBOARD_TYPE_LOMAJI_QWERTY) {
            nextKeyboard = mLomajiQwertyKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_HANJI_QWERTY) {
            nextKeyboard = mHanjiQwertyKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_LOMAJI_SYMBOL) {
            nextKeyboard = mLomajiSymbolsKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_LOMAJI_SYMBOL_SHIFTED) {
            nextKeyboard = mLomajiSymbolsShiftedKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_HANJI_SYMBOL) {
            nextKeyboard = mHanjiSymbolsKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_HANJI_SYMBOL_SHIFTED) {
            nextKeyboard = mHanjiSymbolsShiftedKeyboard;
        } else {
            nextKeyboard = mLomajiQwertyKeyboard;
        }

        setKeyboard(nextKeyboard);
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
        } else if (currentKeyboard == mHanjiSymbolsKeyboard || currentKeyboard == mHanjiSymbolsShiftedKeyboard) {
            nextKeyboard = mHanjiQwertyKeyboard;
        }

        setKeyboard(nextKeyboard);
    }

    public void handleShift() {
        Keyboard currentKeyboard = mTaigiKeyboardView.getKeyboard();
        TaigiKeyboard nextKeyboard = mLomajiQwertyKeyboard;

        if (currentKeyboard == mLomajiSymbolsKeyboard) {
            nextKeyboard = mLomajiSymbolsShiftedKeyboard;
        } else if (currentKeyboard == mLomajiSymbolsShiftedKeyboard) {
            nextKeyboard = mLomajiSymbolsKeyboard;
        } else if (currentKeyboard == mHanjiSymbolsKeyboard) {
            nextKeyboard = mHanjiSymbolsShiftedKeyboard;
        } else if (currentKeyboard == mHanjiSymbolsShiftedKeyboard) {
            nextKeyboard = mHanjiSymbolsKeyboard;
        }

        setKeyboard(nextKeyboard);
    }

    private void setKeyboard(TaigiKeyboard nextKeyboard) {
        nextKeyboard.setImeOptions(mTaigiIme.getResources(), mImeOptions);
        mTaigiKeyboardView.setKeyboard(nextKeyboard);

        if (mTaigiCandidateView != null) {
            if (nextKeyboard == mLomajiQwertyKeyboard
                    || nextKeyboard == mLomajiSymbolsKeyboard
                    || nextKeyboard == mLomajiSymbolsShiftedKeyboard) {
                mTaigiCandidateView.setIsMainCandidateLomaji(true);
            } else {
                mTaigiCandidateView.setIsMainCandidateLomaji(false);
            }
        }
    }

    public void setImeOptions(Resources resources, int imeOptions) {
        mImeOptions = imeOptions;
        getCurrentKeyboard().setImeOptions(resources, imeOptions);
    }
}
