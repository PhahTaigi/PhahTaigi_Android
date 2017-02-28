package com.taccotap.phahtaigi.ime.keyboard;

import android.app.Dialog;
import android.inputmethodservice.Keyboard;
import android.os.IBinder;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.taccotap.phahtaigi.R;
import com.taccotap.phahtaigi.ime.TaigiIme;
import com.taccotap.phahtaigi.ime.ui.TaigiKeyboardView;

public class KeyboardSwitcher {

    public static final int KEYBOARD_TYPE_QWERTY = 0;
    public static final int KEYBOARD_TYPE_SYMBOL = 1;
    public static final int KEYBOARD_TYPE_SYMBOL_SHIFTED = 2;

    private final TaigiIme mTaigiIme;
    private final InputMethodManager mInputMethodManager;
    private final TaigiKeyboardView mTaigiKeyboardView;

    private TaigiKeyboard mQwertyKeyboard;
    private TaigiKeyboard mSymbolsKeyboard;
    private TaigiKeyboard mSymbolsShiftedKeyboard;

    public KeyboardSwitcher(TaigiIme taigiIme, InputMethodManager inputMethodManager, TaigiKeyboardView taigiKeyboardView) {
        mTaigiIme = taigiIme;
        mInputMethodManager = inputMethodManager;
        mTaigiKeyboardView = taigiKeyboardView;

        mQwertyKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_taigi_qwerty);
        mSymbolsKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_taigi_symbols);
        mSymbolsShiftedKeyboard = new TaigiKeyboard(taigiIme, R.xml.keyboard_layout_taigi_symbols_shift);
    }

    public TaigiKeyboard getCurrentKeyboard() {
        return (TaigiKeyboard) mTaigiKeyboardView.getKeyboard();
    }

    public void setKeyboard(int keyboardType) {
        TaigiKeyboard newKeyboard;

        if (keyboardType == KEYBOARD_TYPE_QWERTY) {
            newKeyboard = mQwertyKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_SYMBOL) {
            newKeyboard = mSymbolsKeyboard;
        } else if (keyboardType == KEYBOARD_TYPE_SYMBOL_SHIFTED) {
            newKeyboard = mSymbolsShiftedKeyboard;
        } else {
            newKeyboard = mQwertyKeyboard;
        }

        mTaigiKeyboardView.setKeyboard(newKeyboard);

        resetKeyboard();
    }

    public void resetKeyboard() {
        final boolean shouldSupportLanguageSwitchKey =
                mInputMethodManager.shouldOfferSwitchingToNextInputMethod(getToken(mTaigiIme));

        TaigiKeyboard currentKeyboard = (TaigiKeyboard) mTaigiKeyboardView.getKeyboard();

        currentKeyboard.setLanguageSwitchKeyVisibility(shouldSupportLanguageSwitchKey);
        mTaigiKeyboardView.setKeyboard(currentKeyboard);
    }

    public boolean isCurrentKeyboardViewUseQwertyKeyboard() {
        return mTaigiKeyboardView.getKeyboard() == mQwertyKeyboard;
    }

    public void switchKeyboard() {
        TaigiKeyboard currentKeyboard = (TaigiKeyboard) mTaigiKeyboardView.getKeyboard();

        if (currentKeyboard == mQwertyKeyboard) {
            mTaigiKeyboardView.setKeyboard(mSymbolsKeyboard);
        } else {
            mTaigiKeyboardView.setKeyboard(mQwertyKeyboard);
            mTaigiKeyboardView.getKeyboard().setShifted(false);
        }
    }

    public static IBinder getToken(TaigiIme taigiIme) {
        final Dialog dialog = taigiIme.getWindow();
        if (dialog == null) {
            return null;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return null;
        }
        return window.getAttributes().token;
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
