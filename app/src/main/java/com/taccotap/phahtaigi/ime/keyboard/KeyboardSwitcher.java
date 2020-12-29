package com.taccotap.phahtaigi.ime.keyboard;

import android.content.res.Resources;
import android.inputmethodservice.Keyboard;
import android.view.inputmethod.InputMethodManager;

import com.taccotap.phahtaigi.R;
import com.taccotap.phahtaigi.ime.TaigiIme;
import com.taccotap.phahtaigi.ime.candidate.TaigiCandidateView;

public class KeyboardSwitcher {

    public static final int KEYBOARD_TYPE_TO_TAIBUN = -2;
    public static final int KEYBOARD_TYPE_TO_ENGBUN = -1;
    public static final int KEYBOARD_TYPE_LOMAJI_QWERTY = 0;
    public static final int KEYBOARD_TYPE_HANJI_QWERTY = 1;
    public static final int KEYBOARD_TYPE_LOMAJI_SYMBOL = 2;
    public static final int KEYBOARD_TYPE_LOMAJI_SYMBOL_SHIFTED = 3;
    public static final int KEYBOARD_TYPE_HANJI_SYMBOL = 4;
    public static final int KEYBOARD_TYPE_HANJI_SYMBOL_SHIFTED = 5;

    private final TaigiIme mTaigiIme;
    private final InputMethodManager mInputMethodManager;

    private TaigiKeyboardView mTaigiKeyboardView;
    private TaigiCandidateView mTaigiCandidateView;

    private TaigiKeyboard mTaibunLomajiQwertyKeyboard;
    private TaigiKeyboard mEngbunLomajiQwertyKeyboard;
    private TaigiKeyboard mTaibunHanjiQwertyKeyboard;
    private TaigiKeyboard mEngbunHanjiQwertyKeyboard;
    private TaigiKeyboard mLomajiSymbolsKeyboard;
    private TaigiKeyboard mLomajiSymbolsShiftedKeyboard;
    private TaigiKeyboard mHanjiSymbolsKeyboard;
    private TaigiKeyboard mHanjiSymbolsShiftedKeyboard;

    private TaigiKeyboard mCurrentKeyboard;
    private enum InputLanguage {
        TAIBUN, ENGBUN
    }
    private InputLanguage mCurrentInputLanguage = InputLanguage.TAIBUN;

    private int mImeOptions;

    public KeyboardSwitcher(TaigiIme taigiIme, InputMethodManager inputMethodManager) {
        mTaigiIme = taigiIme;
        mInputMethodManager = inputMethodManager;

        mTaibunLomajiQwertyKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_lomaji_qwerty_taibun);
        mEngbunLomajiQwertyKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_lomaji_qwerty_engbun);
        mTaibunHanjiQwertyKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_hanji_qwerty_taibun);
        mEngbunHanjiQwertyKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_hanji_qwerty_engbun);
        mLomajiSymbolsKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_lomaji_symbols);
        mLomajiSymbolsShiftedKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_lomaji_symbols_shift);
        mHanjiSymbolsKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_hanji_symbols);
        mHanjiSymbolsShiftedKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_hanji_symbols_shift);

        mCurrentKeyboard = mTaibunLomajiQwertyKeyboard;
    }

    public void setTaigiCandidateView(TaigiCandidateView taigiCandidateView) {
        mTaigiCandidateView = taigiCandidateView;
    }

//    public TaigiKeyboard getCurrentKeyboard() {
//        return (TaigiKeyboard) mTaigiKeyboardView.getKeyboard();
//    }

    public void setKeyboardByType(int keyboardType) {
        TaigiKeyboard nextKeyboard = null;

        if (keyboardType == KEYBOARD_TYPE_LOMAJI_QWERTY) {
            nextKeyboard = mTaibunLomajiQwertyKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_HANJI_QWERTY) {
            nextKeyboard = mTaibunHanjiQwertyKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_TO_ENGBUN) {
            mCurrentInputLanguage = InputLanguage.ENGBUN;
            if (mCurrentKeyboard == mTaibunHanjiQwertyKeyboard) {
                nextKeyboard = mEngbunHanjiQwertyKeyboard;
            } else {
                nextKeyboard = mEngbunLomajiQwertyKeyboard;
            }
        } else if (keyboardType == KEYBOARD_TYPE_TO_TAIBUN) {
            mCurrentInputLanguage = InputLanguage.TAIBUN;
            if (mCurrentKeyboard == mEngbunHanjiQwertyKeyboard) {
                nextKeyboard = mTaibunHanjiQwertyKeyboard;
            } else {
                nextKeyboard = mTaibunLomajiQwertyKeyboard;
            }
        } else if (keyboardType == KEYBOARD_TYPE_LOMAJI_SYMBOL) {
            nextKeyboard = mLomajiSymbolsKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_LOMAJI_SYMBOL_SHIFTED) {
            nextKeyboard = mLomajiSymbolsShiftedKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_HANJI_SYMBOL) {
            nextKeyboard = mHanjiSymbolsKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_HANJI_SYMBOL_SHIFTED) {
            nextKeyboard = mHanjiSymbolsShiftedKeyboard;
        } else {
            nextKeyboard = mTaibunLomajiQwertyKeyboard;
        }

        mCurrentKeyboard = nextKeyboard;

        setKeyboard(nextKeyboard);
    }

    public void resetKeyboard(TaigiKeyboardView taigiKeyboardView) {
        mTaigiKeyboardView = taigiKeyboardView;
        setKeyboard(mCurrentKeyboard);
    }

    public boolean isCurrentKeyboardViewUseQwertyKeyboard() {
        return mCurrentKeyboard == mTaibunLomajiQwertyKeyboard ||
                mCurrentKeyboard == mEngbunLomajiQwertyKeyboard ||
                mCurrentKeyboard == mTaibunHanjiQwertyKeyboard ||
                mCurrentKeyboard == mEngbunHanjiQwertyKeyboard;
    }

    public boolean isCurrentKeyboardViewUseTaibunQwertyKeyboard() {
        return mCurrentKeyboard == mTaibunLomajiQwertyKeyboard ||
                mCurrentKeyboard == mTaibunHanjiQwertyKeyboard;
    }

    public void switchKeyboard() {
        TaigiKeyboard currentKeyboard = (TaigiKeyboard) mTaigiKeyboardView.getKeyboard();
        TaigiKeyboard nextKeyboard = mTaibunLomajiQwertyKeyboard;

        if (currentKeyboard == mTaibunLomajiQwertyKeyboard || currentKeyboard == mEngbunLomajiQwertyKeyboard) {
            nextKeyboard = mLomajiSymbolsKeyboard;
        } else if (currentKeyboard == mTaibunHanjiQwertyKeyboard || currentKeyboard == mEngbunHanjiQwertyKeyboard) {
            nextKeyboard = mHanjiSymbolsKeyboard;
        } else if (currentKeyboard == mLomajiSymbolsKeyboard || currentKeyboard == mLomajiSymbolsShiftedKeyboard) {
            if (mCurrentInputLanguage == InputLanguage.ENGBUN) {
                nextKeyboard = mEngbunLomajiQwertyKeyboard;
            } else {
                nextKeyboard = mTaibunLomajiQwertyKeyboard;
            }
        } else if (currentKeyboard == mHanjiSymbolsKeyboard || currentKeyboard == mHanjiSymbolsShiftedKeyboard) {
            if (mCurrentInputLanguage == InputLanguage.ENGBUN) {
                nextKeyboard = mEngbunHanjiQwertyKeyboard;
            } else {
                nextKeyboard = mTaibunHanjiQwertyKeyboard;
            }
        }

        mCurrentKeyboard = nextKeyboard;

        setKeyboard(nextKeyboard);
    }

    public void handleShift() {
        Keyboard currentKeyboard = mTaigiKeyboardView.getKeyboard();
        TaigiKeyboard nextKeyboard = mTaibunLomajiQwertyKeyboard;

        if (currentKeyboard == mLomajiSymbolsKeyboard) {
            nextKeyboard = mLomajiSymbolsShiftedKeyboard;
        } else if (currentKeyboard == mLomajiSymbolsShiftedKeyboard) {
            nextKeyboard = mLomajiSymbolsKeyboard;
        } else if (currentKeyboard == mHanjiSymbolsKeyboard) {
            nextKeyboard = mHanjiSymbolsShiftedKeyboard;
        } else if (currentKeyboard == mHanjiSymbolsShiftedKeyboard) {
            nextKeyboard = mHanjiSymbolsKeyboard;
        }

        mCurrentKeyboard = nextKeyboard;

        setKeyboard(nextKeyboard);
    }

    private void setKeyboard(TaigiKeyboard nextKeyboard) {
        nextKeyboard.setImeOptions(mTaigiIme.getResources(), mImeOptions);

        if (mTaigiKeyboardView != null) {
            mTaigiKeyboardView.setKeyboard(nextKeyboard);
        }

        if (mTaigiCandidateView != null) {
            if (nextKeyboard == mTaibunLomajiQwertyKeyboard
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
        mCurrentKeyboard.setImeOptions(resources, imeOptions);
    }
}
