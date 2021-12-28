package com.taccotap.phahtaigi.ime;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.pixplicity.easyprefs.library.Prefs;
import com.taccotap.phahtaigi.AppPrefs;
import com.taccotap.phahtaigi.BuildConfig;
import com.taccotap.phahtaigi.R;
import com.taccotap.phahtaigi.about.AboutActivity;
import com.taccotap.phahtaigi.ime.candidate.TaigiCandidateController;
import com.taccotap.phahtaigi.ime.candidate.TaigiCandidateView;
import com.taccotap.phahtaigi.ime.keyboard.CustomKeycode;
import com.taccotap.phahtaigi.ime.keyboard.KeyboardSwitcher;
import com.taccotap.phahtaigi.ime.keyboard.TaigiKeyboardView;
import com.taccotap.phahtaigi.rxbus.RxBus;
import com.taccotap.phahtaigi.rxbus.events.UpdateHanjiFontEvent;

import java.util.List;

import io.reactivex.functions.Consumer;


/**
 * Example of writing an input method for a soft keyboard.  This code is
 * focused on simplicity over completeness, so it should in no way be considered
 * to be a complete soft keyboard implementation.  Its purpose is to provide
 * a basic example for how you would get started writing an input method, to
 * be fleshed out as appropriate.
 */
public class TaigiIme extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {
    public static final int KEY_VIBRATION_MILLISECONDS = 5;
    private static final String TAG = TaigiIme.class.getSimpleName();

    private String mWordSeparators;
    private String mWordEndingSentence;

    private InputMethodManager mInputMethodManager;
    private Vibrator mVibrator;

    private TaigiKeyboardView mTaigiKeyboardView;
    private TaigiCandidateView mTaigiCandidateView;

    private RelativeLayout mInputViewBase;
    private ConstraintLayout mInputView;
    private ConstraintLayout mSettingPopupLayout;
    private LinearLayout mTopMenuLayout;

    private KeyboardSwitcher mKeyboardSwitcher;
    private TaigiCandidateController mTaigiCandidateController;

//    private int mCurrentInputLomajiMode;
    private int mCurrentInputMode;

    private StringBuilder mComposing = new StringBuilder();
    private boolean mIsCapsLock;
    private long mLastShiftTime;

    private int mLastPrimaryKey = -9999;
    private long mLastPrimaryKeyTime = 0;

    private volatile boolean mIsNeedToUpdateHanjiFont = false;
    private boolean mIsVibration;

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mWordSeparators = getResources().getString(R.string.word_separators);
        mWordEndingSentence = getResources().getString(R.string.word_ending_sentence);

//        RxBus.get().asFlowable().subscribe(new Consumer<Object>() {
//            @Override
//            public void accept(Object event) throws Exception {
//                if (event instanceof UpdateHanjiFontEvent) {
//                    mIsNeedToUpdateHanjiFont = true;
//                }
//            }
//        });
    }

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override
    public void onInitializeInterface() {
        if (BuildConfig.DEBUG_LOG) {
            Log.i(TAG, "onInitializeInterface");
        }

        if (mTaigiCandidateController == null) {
            mTaigiCandidateController = new TaigiCandidateController();
        }

        if (mKeyboardSwitcher == null) {
            mKeyboardSwitcher = new KeyboardSwitcher(this, mInputMethodManager);
        }
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);

        if (BuildConfig.DEBUG_LOG) {
            Log.i(TAG, "onStartInput(): restarting = " + restarting);
        }

        updateCurrentVibrationModeFromPrefs();

        // We are now going to initialize our state based on the type of
        // text being edited.
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_DATETIME:
                // Numbers and dates default to the symbols keyboard, with
                // no extra features.
                mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_LOMAJI_SYMBOL);
                break;

            case InputType.TYPE_CLASS_PHONE:
                // Phones will also default to the symbols keyboard, though
                // often you will want to have a dedicated phone keyboard.
                mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_LOMAJI_SYMBOL);
                break;

//            case InputType.TYPE_CLASS_TEXT:
//                // This is general text editing.  We will default to the
//                // normal alphabetic keyboard, and assume that we should
//                // be doing predictive text (showing candidates as the
//                // user types).
//                if (mCurrentInputMode == INPUT_MODE_LOMAJI) {
//                    mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_LOMAJI_QWERTY);
//                } else if (mCurrentInputMode == INPUT_MODE_HANJI) {
//                    mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_HANJI_QWERTY);
//                } else {
//                    mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_LOMAJI_QWERTY);
//                }
//                updateShiftKeyState(attribute);
//                break;

            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                if (mCurrentInputMode == AppPrefs.INPUT_MODE_LOMAJI) {
                    mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_LOMAJI_QWERTY);
                } else if (mCurrentInputMode == AppPrefs.INPUT_MODE_HANJI) {
                    mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_HANJI_QWERTY);
                } else {
                    mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_LOMAJI_QWERTY);
                }
        }

        // Update the label on the enter key, depending on what the application
        // says it will do.
        mKeyboardSwitcher.setImeOptions(getResources(), attribute.imeOptions);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateInputView() {
        if (BuildConfig.DEBUG_LOG) {
            Log.i(TAG, "onCreateInputView");
        }

        // init InputViewBase
        if (mInputViewBase == null) {
            mInputViewBase = (RelativeLayout) getLayoutInflater().inflate(R.layout.input_view_base, null);
        } else {
            mInputViewBase.removeAllViews();

            final ViewGroup viewGroup1 = (ViewGroup) mInputViewBase.getParent();
            if (viewGroup1 != null) {
                viewGroup1.removeView(mInputViewBase);
            }
        }

        // init real InputView
        mInputView = (ConstraintLayout) getLayoutInflater().inflate(R.layout.input_view, null);

        // init layouts
        initTopMenuLayout();
        initTaigiKeyboardView();
        initSettingPopupView();

        // return IbputView for InputMethodService
        mInputViewBase.addView(mInputView);
        return mInputViewBase;
    }

    private void initTopMenuLayout() {
        mTopMenuLayout = mInputView.findViewById(R.id.topMenuLayout);

        Button openSettingButton = mTopMenuLayout.findViewById(R.id.openSettingButton);
        openSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsVibration) {
                    mVibrator.vibrate(KEY_VIBRATION_MILLISECONDS);
                }

                if (mSettingPopupLayout == null) {
                    mSettingPopupLayout = mInputView.findViewById(R.id.settingPopupLayout);
                }
                mSettingPopupLayout.setVisibility(View.VISIBLE);
            }
        });
        Button openAboutButton = mTopMenuLayout.findViewById(R.id.openAboutButton);
        openAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsVibration) {
                    mVibrator.vibrate(KEY_VIBRATION_MILLISECONDS);
                }

                final Intent intent = new Intent(TaigiIme.this, AboutActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void initTaigiKeyboardView() {
        mTaigiKeyboardView = mInputView.findViewById(R.id.taigiKeyboardView);
        mTaigiKeyboardView.setOnKeyboardActionListener(this);
    }

    private void initSettingPopupView() {
        if (mSettingPopupLayout == null) {
            mSettingPopupLayout = mInputView.findViewById(R.id.settingPopupLayout);
        }

        com.bitvale.switcher.SwitcherX settingPopupViewPojSwitch = mSettingPopupLayout.findViewById(R.id.settingPopupViewPojSwitch);
        com.bitvale.switcher.SwitcherX settingPopupViewVibrationSwitch = mSettingPopupLayout.findViewById(R.id.settingPopupViewVibrationSwitch);
        Button settingPopupViewDoneButton = mSettingPopupLayout.findViewById(R.id.settingPopupViewDoneButton);

        // settingPopupViewPojSwitch
        final int savedInputLomajiMode = Prefs.getInt(AppPrefs.PREFS_KEY_CURRENT_INPUT_LOMAJI_MODE_V3, AppPrefs.INPUT_LOMAJI_MODE_APP_DEFAULT);
        if (savedInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
            settingPopupViewPojSwitch.setChecked(true, false);
        } else {
            settingPopupViewPojSwitch.setChecked(false, false);
        }
        mTaigiCandidateController.setCurrentInputLomajiMode(savedInputLomajiMode);
        settingPopupViewPojSwitch.setOnCheckedChangeListener(checked -> {
            if (mIsVibration) {
                mVibrator.vibrate(KEY_VIBRATION_MILLISECONDS);
            }

            if (checked) {
                Prefs.putInt(AppPrefs.PREFS_KEY_CURRENT_INPUT_LOMAJI_MODE_V3, AppPrefs.INPUT_LOMAJI_MODE_POJ);
            } else {
                Prefs.putInt(AppPrefs.PREFS_KEY_CURRENT_INPUT_LOMAJI_MODE_V3, AppPrefs.INPUT_LOMAJI_MODE_KIP);
            }
            return null;
        });

        // settingPopupViewVibrationSwitch
        final boolean isVibration = Prefs.getBoolean(AppPrefs.PREFS_KEY_IS_VIBRATION, AppPrefs.PREFS_KEY_IS_VIBRATION_YES);
        if (isVibration == AppPrefs.PREFS_KEY_IS_VIBRATION_YES) {
            settingPopupViewVibrationSwitch.setChecked(true, false);
        } else {
            settingPopupViewVibrationSwitch.setChecked(false, false);
        }
        settingPopupViewVibrationSwitch.setOnCheckedChangeListener(checked -> {
            if (mIsVibration) {
                mVibrator.vibrate(KEY_VIBRATION_MILLISECONDS);
            }

            Prefs.putBoolean(AppPrefs.PREFS_KEY_IS_VIBRATION, checked);
            return null;
        });

        // settingPopupViewVibrationSwitch
        settingPopupViewDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsVibration) {
                    mVibrator.vibrate(KEY_VIBRATION_MILLISECONDS);
                }

                mSettingPopupLayout.setVisibility(View.GONE);

                updateCurrentInputLomajiModeFromPrefs();
                updateCurrentVibrationModeFromPrefs();
            }
        });
    }

    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     */
    @Override
    public View onCreateCandidatesView() {
        if (BuildConfig.DEBUG_LOG) {
            Log.i(TAG, "onCreateCandidatesView");
        }

        if (mTaigiCandidateView == null) {
            mTaigiCandidateView = new TaigiCandidateView(this, mVibrator, new Handler(Looper.getMainLooper()));
            mTaigiCandidateView.setService(this);

            mKeyboardSwitcher.setTaigiCandidateView(mTaigiCandidateView);
            mTaigiCandidateController.setTaigiCandidateView(mTaigiCandidateView);
        }

        mTaigiCandidateView.setIsVibration(mIsVibration);

        final ViewGroup viewGroup = (ViewGroup) mTaigiCandidateView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(mTaigiCandidateView);
        }

        return mTaigiCandidateView;
    }

    @Override
    public void setInputView(View view) {
        if (BuildConfig.DEBUG_LOG) {
            Log.i(TAG, "setInputView");
        }

        final ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(view);
        }

        super.setInputView(view);
    }

    @Override
    public void setCandidatesView(View view) {
        if (BuildConfig.DEBUG_LOG) {
            Log.i(TAG, "setCandidatesView");
        }

        final ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(view);
        }

        super.setCandidatesView(view);
    }

    @Override

    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);

        if (BuildConfig.DEBUG_LOG) {
            Log.i(TAG, "onStartInputView(): restarting = " + restarting);
        }

        mKeyboardSwitcher.resetKeyboard(mTaigiKeyboardView);

        if (mIsNeedToUpdateHanjiFont) {
            mTaigiCandidateView.resetTextSettings();
            mIsNeedToUpdateHanjiFont = false;
        }

        updateCurrentInputModeFromPrefs();
        updateCurrentInputLomajiModeFromPrefs();
        updateCurrentVibrationModeFromPrefs();

        handleKeyboardViewAutoCaps();

//        mTaigiKeyboardView.closing();
    }

    private void updateCurrentInputModeFromPrefs() {
        mCurrentInputMode = Prefs.getInt(AppPrefs.PREFS_KEY_CURRENT_INPUT_MODE, AppPrefs.INPUT_MODE_LOMAJI);
    }

    private void updateCurrentInputLomajiModeFromPrefs() {
        int currentInputLomajiMode = Prefs.getInt(AppPrefs.PREFS_KEY_CURRENT_INPUT_LOMAJI_MODE_V3, AppPrefs.INPUT_LOMAJI_MODE_APP_DEFAULT);
        mTaigiCandidateController.setCurrentInputLomajiMode(currentInputLomajiMode);
    }

    private void updateCurrentVibrationModeFromPrefs() {
        mIsVibration = Prefs.getBoolean(AppPrefs.PREFS_KEY_IS_VIBRATION, AppPrefs.PREFS_KEY_IS_VIBRATION_YES);
        if (mTaigiCandidateView != null) {
            mTaigiCandidateView.setIsVibration(mIsVibration);
        }
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override
    public void onFinishInput() {
        super.onFinishInput();

        if (BuildConfig.DEBUG_LOG) {
            Log.i(TAG, "onFinishInput");
        }
    }

//    /**
//     * Deal with the editor reporting movement of its cursor.
//     */
//    @Override
//    public void onUpdateSelection(int oldSelStart, int oldSelEnd,
//                                  int newSelStart, int newSelEnd,
//                                  int candidatesStart, int candidatesEnd) {
//        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
//                candidatesStart, candidatesEnd);
//
////        // If the current selection in the text view changes, we should
////        // clear whatever candidate text we have.
////        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
////                || newSelEnd != candidatesEnd)) {
////            mComposing.setLength(0);
////            updateInputForCandidate();
////            InputConnection ic = getCurrentInputConnection();
////            if (ic != null) {
////                ic.finishComposingText();
////            }
////        }
//
//        if (BuildConfig.DEBUG_LOG) {
//            Log.d(TAG, "onUpdateSelection: candidatesStart=" + candidatesStart + ", candidatesEnd=" + candidatesEnd);
//        }
//
//        handleKeyboardViewAutoCaps();
//    }

//    /**
//     * Helper function to commit any text being composed in to the editor.
//     */
//    private void commitTyped() {
//        if (mComposing.length() > 0) {
//            getCurrentInputConnection().commitText(mComposing, 1);
//            mComposing.setLength(0);
//            updateInputForCandidate();
//        }
//    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    // Implementation of KeyboardViewListener
    public void onKey(int primaryCode, int[] keyCodes) {
        boolean isShiftKey = false;

        if (isWordSeparator(primaryCode)) {
            handleWordSeparator(primaryCode);
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            isShiftKey = true;
            handleShiftForSwitchKeyboard();
        } else if (primaryCode == CustomKeycode.KEYCODE_SWITCH_TO_HANJI) {
            Prefs.putInt(AppPrefs.PREFS_KEY_CURRENT_INPUT_MODE, AppPrefs.INPUT_MODE_HANJI);
            mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_HANJI_QWERTY);
        } else if (primaryCode == CustomKeycode.KEYCODE_SWITCH_TO_LOMAJI) {
            Prefs.putInt(AppPrefs.PREFS_KEY_CURRENT_INPUT_MODE, AppPrefs.INPUT_MODE_LOMAJI);
            mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_LOMAJI_QWERTY);
        } else if (primaryCode == CustomKeycode.KEYCODE_SWITCH_TO_TAIBUN) {
            mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_TO_TAIBUN);
        } else if (primaryCode == CustomKeycode.KEYCODE_SWITCH_TO_ENGBUN) {
            commitRawInputSuggestion();
            mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_TO_ENGBUN);
        } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE && mTaigiKeyboardView != null) {
            commitRawInputSuggestion();
            mKeyboardSwitcher.switchKeyboard();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            handleClose();
            return;
        } else if (primaryCode == CustomKeycode.KEYCODE_SHOW_IME_PICKER) {
            showImePicker();
        } else {
            handleCharacter(primaryCode, keyCodes);
        }

        if (!isShiftKey) {
            handleKeyboardViewAutoCaps();
        }

        if (isNeedVibration(primaryCode)) {
            if (mIsVibration) {
                mVibrator.vibrate(KEY_VIBRATION_MILLISECONDS);
            }
        }
    }

    private boolean isNeedVibration(int primaryCode) {
        boolean isNeedVibration = true;

        final long now = System.currentTimeMillis();
        final long time = now - mLastPrimaryKeyTime;
        if (primaryCode == mLastPrimaryKey && time < 100) {
            isNeedVibration = false;
        }

        mLastPrimaryKey = primaryCode;
        mLastPrimaryKeyTime = now;

        return isNeedVibration;
    }

    private void handleWordSeparator(int primaryCode) {
        if (mKeyboardSwitcher.isCurrentKeyboardViewUseQwertyKeyboard() && mTaigiCandidateController.hasRawInputSuggestion()) {
            commitRawInputSuggestion();
        }
        sendKey(primaryCode);

        updateKeyboardViewShiftIcon();
    }

    /**
     * Helper to send a character to the editor as raw key events.
     */
    private void sendKey(int keyCode) {
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                }
                break;
        }
    }

    public void onText(CharSequence text) {
    }

    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
    private void updateInputForCandidate() {
        if (mComposing.length() > 0) {
            setRawInputForCandidate(mComposing.toString());
        } else {
            setRawInputForCandidate(null);
        }
    }

    public void setRawInputForCandidate(String rawInput) {
        if (!TextUtils.isEmpty(rawInput)) {
            mTopMenuLayout.setVisibility(View.GONE);
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            mTopMenuLayout.setVisibility(View.GONE);
            setCandidatesViewShown(true);
        } else {
            setCandidatesViewShown(false);
            mTopMenuLayout.setVisibility(View.VISIBLE);
        }

        if (mTaigiCandidateController != null) {
            mTaigiCandidateController.setRawInput(rawInput);

            if (BuildConfig.DEBUG_LOG) {
                Log.d(TAG, "setRawInputForCandidate: " + rawInput);
            }
        }
    }

    private void handleBackspace() {
        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);
//            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateInputForCandidate();
        } else if (length > 0) {
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
            updateInputForCandidate();
        } else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
        updateKeyboardViewShiftIcon();
    }

    private void handleShiftForSwitchKeyboard() {
        if (mKeyboardSwitcher.isCurrentKeyboardViewUseQwertyKeyboard()) {
            // Alphabet keyboard
            checkToggleCapsLock();

            mTaigiKeyboardView.setShifted(mIsCapsLock || !mTaigiKeyboardView.isShifted());
        } else {
            mKeyboardSwitcher.handleShift();
        }

        // update icon
        updateKeyboardViewShiftIcon();
    }

    private void updateKeyboardViewShiftIcon() {
        final List<Keyboard.Key> keys = mTaigiKeyboardView.getKeyboard().getKeys();
        final int shiftKeyIndex = mTaigiKeyboardView.getKeyboard().getShiftKeyIndex();
        final Keyboard.Key shiftkey = keys.get(shiftKeyIndex);
        int[] state;
        if (mIsCapsLock) {
            state = new int[]{android.R.attr.state_checked};
            shiftkey.icon.setState(state);
        } else {
            if (mTaigiKeyboardView.isShifted()) {
                state = new int[]{android.R.attr.state_pressed};
                shiftkey.icon.setState(state);
            } else {
                state = new int[]{android.R.attr.state_empty};
                shiftkey.icon.setState(state);
            }
        }
        mTaigiKeyboardView.invalidateKey(shiftKeyIndex);
    }

    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "handleCharacter: " + primaryCode);
        }

        if (isInputViewShown()) {
            if (mTaigiKeyboardView.isShifted()) {
                if (BuildConfig.DEBUG_LOG) {
                    Log.d(TAG, "mTaigiKeyboardView.isShifted() = " + mTaigiKeyboardView.isShifted());
                    Log.d(TAG, "mIsCapsLock = " + mIsCapsLock);
                }

                primaryCode = Character.toUpperCase(primaryCode);

                if (!mIsCapsLock) {
                    mTaigiKeyboardView.setShifted(false);
                    updateKeyboardViewShiftIcon();
                }
            }
        }

        if (!mKeyboardSwitcher.isCurrentKeyboardViewUseTaibunQwertyKeyboard()) {
            sendKey(primaryCode);
        } else {
            if (isDirectlySendKeyWhenOnlyInputNumbers(primaryCode)) {
                sendKey(primaryCode);
            } else {
                if (!isInputNonTaibunCharacters(primaryCode)) {
                    mComposing.append((char) primaryCode);
                    updateInputForCandidate();
                }
            }
        }
    }

    private boolean isInputNonTaibunCharacters(int primaryCode) {
        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "isInputNonTaibunCharacters: primaryCode = " + primaryCode);
        }

        if (mComposing.length() == 0) {
            if (primaryCode == 81 || primaryCode == 113   // Qq
                    || primaryCode == 87 || primaryCode == 119   // Ww
                    || primaryCode == 89 || primaryCode == 121   // Yy
                    || primaryCode == 68 || primaryCode == 100   // Dd
                    || primaryCode == 70 || primaryCode == 102   // Ff
                    || primaryCode == 90 || primaryCode == 122   // Zz
                    || primaryCode == 88 || primaryCode == 120   // Xx
                    || primaryCode == 86 || primaryCode == 118   // Vv
            ) {
                return true;
            }
        }

        return false;
    }

    private boolean isDirectlySendKeyWhenOnlyInputNumbers(int primaryCode) {
        if (mComposing.length() == 0) {
            // 0~9
            if (primaryCode >= 48 && primaryCode <= 57) {
                return true;
            }
        }

        return false;
    }

    private void handleKeyboardViewAutoCaps() {
        if (mKeyboardSwitcher.isCurrentKeyboardViewUseQwertyKeyboard() && !mIsCapsLock) {
            String inputText;
            if (!TextUtils.isEmpty(mComposing.toString())) {
                inputText = mComposing.toString().trim();
            } else {
                final InputConnection inputConnection = getCurrentInputConnection();
                if (inputConnection == null) {
                    inputText = null;
                } else {
                    final CharSequence textBeforeCursor = inputConnection.getTextBeforeCursor(100, 0);
                    if (textBeforeCursor == null) {
                        inputText = null;
                    } else {
                        inputText = textBeforeCursor.toString().trim();
                    }
                }
            }

//            if (BuildConfig.DEBUG_LOG) {
//                Log.d(TAG, "handleKeyboardViewAutoCaps(): inputText = " + inputText);
//            }

            if (TextUtils.isEmpty(inputText)) {
                mTaigiKeyboardView.setShifted(true);
            } else {
                final String lastChar = inputText.substring(inputText.length() - 1);

//                if (BuildConfig.DEBUG_LOG) {
//                    Log.d(TAG, "handleKeyboardViewAutoCaps(): lastChar = " + lastChar);
//                }

                if (mWordEndingSentence.contains(lastChar)) {
                    mTaigiKeyboardView.setShifted(true);
                } else {
                    mTaigiKeyboardView.setShifted(false);
                }
            }

            updateKeyboardViewShiftIcon();
        }
    }

    private void handleClose() {
        commitRawInputSuggestion();
        requestHideSelf(0);

//        // We only hide the candidates window when finishing input on
//        // a particular editor, to avoid popping the underlying application
//        // up and down if the user is entering text into the bottom of
//        // its window.
//        setCandidatesViewShown(false);
//
//        mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_LOMAJI_QWERTY);

        if (mTaigiKeyboardView != null) {
            mTaigiKeyboardView.closing();
        }
    }

    private void checkToggleCapsLock() {
        if (mIsCapsLock) {
            mIsCapsLock = false;
            mLastShiftTime = 0;
            return;
        }

        long now = System.currentTimeMillis();
        if (mLastShiftTime + 800 > now) {
            mIsCapsLock = !mIsCapsLock;
            mLastShiftTime = 0;
        } else {
            mLastShiftTime = now;
        }
    }

    private String getWordSeparators() {
        return mWordSeparators;
    }

    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char) code));
    }

    public void commitRawInputSuggestion() {
        final String rawInputSuggestion = mTaigiCandidateController.getRawInputSuggestion();
        if (!TextUtils.isEmpty(rawInputSuggestion)) {
            getCurrentInputConnection().commitText(rawInputSuggestion, 1);
            mComposing.setLength(0);
            updateInputForCandidate();
        }
    }

    public void commitPickedSuggestion(String pickedSuggestion) {
        getCurrentInputConnection().commitText(pickedSuggestion, 1);
        mComposing.setLength(0);
        updateInputForCandidate();
    }

//    private int getCurrentCursorPosition() {
//        return getCurrentInputConnection().getExtractedText(new ExtractedTextRequest(), 0).selectionEnd;
//    }

    public void swipeRight() {
        if (mIsVibration) {
            mVibrator.vibrate(KEY_VIBRATION_MILLISECONDS);
        }
        switchToNextIme();
    }

    public void swipeLeft() {
        if (mIsVibration) {
            mVibrator.vibrate(KEY_VIBRATION_MILLISECONDS);
        }
        showImePicker();
    }

    public void swipeDown() {
        if (mIsVibration) {
            mVibrator.vibrate(KEY_VIBRATION_MILLISECONDS);
        }
        handleClose();
    }

    public void swipeUp() {
        // Go ChhoeTaigi website
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://chhoe.taigi.info/"));
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(myIntent);
    }

    public void onPress(int primaryCode) {
    }

    public void onRelease(int primaryCode) {
    }

    private void switchToNextIme() {
        mInputMethodManager.switchToNextInputMethod(getToken(), false);
    }

    private void showImePicker() {
        mInputMethodManager.showInputMethodPicker();
    }

    private IBinder getToken() {
        final Dialog dialog = getWindow();
        if (dialog == null) {
            return null;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return null;
        }
        return window.getAttributes().token;
    }
}