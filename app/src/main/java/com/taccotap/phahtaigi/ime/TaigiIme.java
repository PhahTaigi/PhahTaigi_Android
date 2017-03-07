/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taccotap.phahtaigi.ime;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.annotation.IdRes;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.taccotap.phahtaigi.R;
import com.taccotap.phahtaigi.ime.candidate.TaigiCandidateController;
import com.taccotap.phahtaigi.ime.candidate.TaigiCandidateView;
import com.taccotap.phahtaigi.ime.keyboard.CustomKeycode;
import com.taccotap.phahtaigi.ime.keyboard.KeyboardSwitcher;
import com.taccotap.phahtaigi.ime.keyboard.TaigiKeyboardView;

import java.util.List;

/**
 * Example of writing an input method for a soft keyboard.  This code is
 * focused on simplicity over completeness, so it should in no way be considered
 * to be a complete soft keyboard implementation.  Its purpose is to provide
 * a basic example for how you would get started writing an input method, to
 * be fleshed out as appropriate.
 */
public class TaigiIme extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {
    private static final String TAG = TaigiIme.class.getSimpleName();

    public static final int INPUT_LOMAJI_MODE_TAILO = 0;
    public static final int INPUT_LOMAJI_MODE_POJ = 1;

//    /**
//     * This boolean indicates the optional example code for performing
//     * processing of hard keys in addition to regular text generation
//     * from on-screen interaction.  It would be used for input methods that
//     * perform language translations (such as converting text entered on
//     * a QWERTY keyboard to Chinese), but may not be used for input methods
//     * that are primarily intended to be used for on-screen text entry.
//     */
//    static final boolean PROCESS_HARD_KEYS = true;

    private InputMethodManager mInputMethodManager;

    private TaigiKeyboardView mTaigiKeyboardView;
    private TaigiCandidateView mTaigiCandidateView;

    private View mInputView;
    private LinearLayout mKeyboardSettingLayout;
    private RadioGroup mLomajiSelectionRadioGroup;

    private KeyboardSwitcher mKeyboardSwitcher;
    private TaigiCandidateController mTaigiCandidateController;

    private CompletionInfo[] mCompletions;

    private StringBuilder mComposing = new StringBuilder();
    private boolean mPredictionOn;
    private boolean mCompletionOn;
    private boolean mCapsLock;
    private long mLastShiftTime;
    private long mMetaState;

    private String mWordSeparators;

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mWordSeparators = getResources().getString(R.string.word_separators);
    }

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override
    public void onInitializeInterface() {
        initKeyboardViewAndKeyboardSwitcher();
    }

    @Override
    public View onCreateInputView() {
        initKeyboardViewAndKeyboardSwitcher();
        return mInputView;
    }

    private void initKeyboardViewAndKeyboardSwitcher() {
        if (mTaigiKeyboardView == null) {
            mInputView = getLayoutInflater().inflate(R.layout.input_view, null);
            mTaigiKeyboardView = (TaigiKeyboardView) mInputView.findViewById(R.id.taigi_keyboard);
            mTaigiKeyboardView.setOnKeyboardActionListener(this);

            mKeyboardSettingLayout = (LinearLayout) mInputView.findViewById(R.id.keyboardSettingLayout);

            mLomajiSelectionRadioGroup = (RadioGroup) mInputView.findViewById(R.id.lomajiSelectionRadioGroup);
            mLomajiSelectionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    if (checkedId == R.id.tailoRadioButton) {
                        Log.d(TAG, "onCheckedChanged(): tailoRadioButton");
                        mTaigiCandidateController.setCurrentInputLomajiMode(INPUT_LOMAJI_MODE_TAILO);
                    } else if (checkedId == R.id.pojRadioButton) {
                        Log.d(TAG, "onCheckedChanged(): pojRadioButton");
                        mTaigiCandidateController.setCurrentInputLomajiMode(INPUT_LOMAJI_MODE_POJ);
                    }
                }
            });
        }

        if (mKeyboardSwitcher == null) {
            mKeyboardSwitcher = new KeyboardSwitcher(this, mInputMethodManager, mTaigiKeyboardView);
            mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_LOMAJI_QWERTY);
        }
    }

    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     */
    @Override
    public View onCreateCandidatesView() {
        mTaigiCandidateView = new TaigiCandidateView(this);
        mTaigiCandidateView.setService(this);

        mKeyboardSwitcher.setTaigiCandidateView(mTaigiCandidateView);
        mTaigiCandidateController = new TaigiCandidateController(mTaigiCandidateView);

        return mTaigiCandidateView;
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

        initKeyboardViewAndKeyboardSwitcher();

        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.
        mComposing.setLength(0);
        updateInputForCandidate();

        if (!restarting) {
            // Clear shift states.
            mMetaState = 0;
        }

        mPredictionOn = false;
        mCompletionOn = false;
        mCompletions = null;

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

            case InputType.TYPE_CLASS_TEXT:
                // This is general text editing.  We will default to the
                // normal alphabetic keyboard, and assume that we should
                // be doing predictive text (showing candidates as the
                // user types).
                mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_LOMAJI_QWERTY);
                mPredictionOn = true;
                Log.d(TAG, "onStartInput[TYPE_CLASS_TEXT]: mPredictionOn = " + mPredictionOn);

//                // We now look for a few special variations of text that will
//                // modify our behavior.
//                int variation = attribute.inputType & InputType.TYPE_MASK_VARIATION;
//                if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
//                        variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
//                    // Do not display predictions / what the user is typing
//                    // when they are entering a password.
//                    mPredictionOn = false;
//                    Log.d(TAG, "onStartInput[TYPE_TEXT_VARIATION_PASSWORD]: mPredictionOn = " + mPredictionOn);
//                }
//
//                if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
//                        || variation == InputType.TYPE_TEXT_VARIATION_URI
//                        || variation == InputType.TYPE_TEXT_VARIATION_FILTER) {
//                    // Our predictions are not useful for e-mail addresses
//                    // or URIs.
//                    mPredictionOn = false;
//                    Log.d(TAG, "onStartInput[TYPE_TEXT_VARIATION_EMAIL_ADDRESS]: mPredictionOn = " + mPredictionOn);
//                }
//
//                if ((attribute.inputType & InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
//                    // If this is an auto-complete text view, then our predictions
//                    // will not be shown and instead we will allow the editor
//                    // to supply their own.  We only show the editor's
//                    // candidates when in fullscreen mode, otherwise relying
//                    // own it displaying its own UI.
//                    mPredictionOn = false;
//                    mCompletionOn = isFullscreenMode();
//                    Log.d(TAG, "onStartInput[TYPE_TEXT_FLAG_AUTO_COMPLETE]: mPredictionOn = " + mPredictionOn);
//                }

                // We also want to look at the current state of the editor
                // to decide whether our alphabetic keyboard should start out
                // shifted.
                updateShiftKeyState(attribute);
                break;

            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_LOMAJI_QWERTY);
                updateShiftKeyState(attribute);
        }

        Log.d(TAG, "onStartInput: mPredictionOn = " + mPredictionOn);

        // Update the label on the enter key, depending on what the application
        // says it will do.
        mKeyboardSwitcher.setImeOptions(getResources(), attribute.imeOptions);
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override
    public void onFinishInput() {
        super.onFinishInput();

        // Clear current composing text and candidates.
        mComposing.setLength(0);
        updateInputForCandidate();

        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false);

        mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_LOMAJI_QWERTY);
        if (mTaigiKeyboardView != null) {
            mTaigiKeyboardView.closing();
        }
    }

    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        // Apply the selected keyboard to the input view.
        mKeyboardSwitcher.resetKeyboard();
        mTaigiKeyboardView.closing();
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
//        // If the current selection in the text view changes, we should
//        // clear whatever candidate text we have.
//        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
//                || newSelEnd != candidatesEnd)) {
//            mComposing.setLength(0);
//            updateInputForCandidate();
//            InputConnection ic = getCurrentInputConnection();
//            if (ic != null) {
//                ic.finishComposingText();
//            }
//        }
//    }

//    /**
//     * This tells us about completions that the editor has determined based
//     * on the current text in it.  We want to use this in fullscreen mode
//     * to show the completions ourself, since the editor can not be seen
//     * in that situation.
//     */
//    @Override
//    public void onDisplayCompletions(CompletionInfo[] completions) {
//        if (mCompletionOn) {
//            mCompletions = completions;
//            if (completions == null) {
//                setRawInputForCandidate(null, false, false);
//                return;
//            }
//
//            List<String> stringList = new ArrayList<String>();
//            for (int i = 0; i < completions.length; i++) {
//                CompletionInfo ci = completions[i];
//                if (ci != null) stringList.add(ci.getText().toString());
//            }
//            setRawInputForCandidate(stringList, true, true);
//        }
//    }

//    /**
//     * This translates incoming hard key events in to edit operations on an
//     * InputConnection.  It is only needed when using the
//     * PROCESS_HARD_KEYS option.
//     */
//    private boolean translateKeyDown(int keyCode, KeyEvent event) {
//        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState,
//                keyCode, event);
//        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
//        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
//        InputConnection ic = getCurrentInputConnection();
//        if (c == 0 || ic == null) {
//            return false;
//        }
//
//        boolean dead = false;
//        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
//            dead = true;
//            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
//        }
//
//        if (mComposing.length() > 0) {
//            char accent = mComposing.charAt(mComposing.length() - 1);
//            int composed = KeyEvent.getDeadChar(accent, c);
//            if (composed != 0) {
//                c = composed;
//                mComposing.setLength(mComposing.length() - 1);
//            }
//        }
//
//        onKey(c, null);
//
//        return true;
//    }

//    /**
//     * Use this to monitor key events being delivered to the application.
//     * We get first crack at them, and can either resume them or let them
//     * continue to the app.
//     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK:
//                // The InputMethodService already takes care of the back
//                // key for us, to dismiss the input method if it is shown.
//                // However, our keyboard could be showing a pop-up window
//                // that back should dismiss, so we first allow it to do that.
//                if (event.getRepeatCount() == 0 && mTaigiKeyboardView != null) {
//                    if (mTaigiKeyboardView.handleBack()) {
//                        return true;
//                    }
//                }
//                break;
//
//            case KeyEvent.KEYCODE_DEL:
//                // Special handling of the delete key: if we currently are
//                // composing text for the user, we want to modify that instead
//                // of let the application to the delete itself.
//                if (mComposing.length() > 0) {
//                    onKey(Keyboard.KEYCODE_DELETE, null);
//                    return true;
//                }
//                break;
//
//            case KeyEvent.KEYCODE_ENTER:
//                // Let the underlying text editor always handle these.
//                return false;
//
//            default:
//                // For all other keys, if we want to do transformations on
//                // text being entered with a hard keyboard, we need to process
//                // it and do the appropriate action.
//                if (PROCESS_HARD_KEYS) {
//                    if (keyCode == KeyEvent.KEYCODE_SPACE
//                            && (event.getMetaState() & KeyEvent.META_ALT_ON) != 0) {
//                        // A silly example: in our input method, Alt+Space
//                        // is a shortcut for 'android' in lower case.
//                        InputConnection ic = getCurrentInputConnection();
//                        if (ic != null) {
//                            // First, tell the editor that it is no longer in the
//                            // shift state, since we are consuming this.
//                            ic.clearMetaKeyStates(KeyEvent.META_ALT_ON);
//                            keyDownUp(KeyEvent.KEYCODE_A);
//                            keyDownUp(KeyEvent.KEYCODE_N);
//                            keyDownUp(KeyEvent.KEYCODE_D);
//                            keyDownUp(KeyEvent.KEYCODE_R);
//                            keyDownUp(KeyEvent.KEYCODE_O);
//                            keyDownUp(KeyEvent.KEYCODE_I);
//                            keyDownUp(KeyEvent.KEYCODE_D);
//                            // And we consume this event.
//                            return true;
//                        }
//                    }
//                    if (mPredictionOn && translateKeyDown(keyCode, event)) {
//                        return true;
//                    }
//                }
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }

//    /**
//     * Use this to monitor key events being delivered to the application.
//     * We get first crack at them, and can either resume them or let them
//     * continue to the app.
//     */
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        // If we want to do transformations on text being entered with a hard
//        // keyboard, we need to process the up events to update the meta key
//        // state we are tracking.
//        if (PROCESS_HARD_KEYS) {
//            if (mPredictionOn) {
//                mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState,
//                        keyCode, event);
//            }
//        }
//
//        return super.onKeyUp(keyCode, event);
//    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped() {
        if (mComposing.length() > 0) {
            getCurrentInputConnection().commitText(mComposing, mComposing.length());
            mComposing.setLength(0);
            updateInputForCandidate();
        }
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private void updateShiftKeyState(EditorInfo attr) {
        if (attr != null
                && mTaigiKeyboardView != null && mKeyboardSwitcher.isCurrentKeyboardViewUseQwertyKeyboard()) {
            int caps = 0;
            EditorInfo ei = getCurrentInputEditorInfo();
            if (ei != null && ei.inputType != InputType.TYPE_NULL) {
                caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
            }
            mTaigiKeyboardView.setShifted(mCapsLock || caps != 0);
        }
    }

    /**
     * Helper to determine if a given character code is alphabetic.
     */
    private boolean isAlphabet(int code) {
        if (Character.isLetter(code)) {
            return true;
        } else {
            return false;
        }
    }

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
        if (isWordSeparator(primaryCode)) {
            handleWordSeparator(primaryCode);
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            handleShift();
        } else if (primaryCode == CustomKeycode.KEYCODE_SWITCH_TO_HANJI) {
            mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_HANJI_QWERTY);
        } else if (primaryCode == CustomKeycode.KEYCODE_SWITCH_TO_LOMAJI) {
            mKeyboardSwitcher.setKeyboardByType(KeyboardSwitcher.KEYBOARD_TYPE_LOMAJI_QWERTY);
        } else if (primaryCode == CustomKeycode.KEYCODE_SETTINGS) {
            handleOpenSettings();
        } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE && mTaigiKeyboardView != null) {
            mKeyboardSwitcher.switchKeyboard();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            handleClose();
            return;
        } else {
            handleCharacter(primaryCode, keyCodes);
        }
    }

    private void handleOpenSettings() {
        if (mKeyboardSettingLayout.getVisibility() == View.VISIBLE) {
            mKeyboardSettingLayout.setVisibility(View.GONE);
        } else {
            mKeyboardSettingLayout.setVisibility(View.VISIBLE);
        }
    }

    private void handleWordSeparator(int primaryCode) {
        if (mKeyboardSwitcher.isCurrentKeyboardViewUseQwertyKeyboard() && mTaigiCandidateController.hasRawInputSuggestion()) {
            commitRawInputSuggestion();
        }
        sendKey(primaryCode);
        updateShiftKeyState(getCurrentInputEditorInfo());
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
        Log.d(TAG, "onText: text = " + text);

//        InputConnection ic = getCurrentInputConnection();
//        if (ic == null) return;
//        ic.beginBatchEdit();
//        if (mComposing.length() > 0) {
//            commitTyped(ic);
//        }
//        ic.commitText(text, 0);
//        ic.endBatchEdit();
//        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
    private void updateInputForCandidate() {
        if (!mCompletionOn) {
            if (mComposing.length() > 0) {
                setRawInputForCandidate(mComposing.toString());
            } else {
                setRawInputForCandidate(null);
            }
        }
    }

    public void setRawInputForCandidate(String rawInput) {
        if (!TextUtils.isEmpty(rawInput)) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        } else {
            setCandidatesViewShown(false);
        }

        if (mTaigiCandidateController != null) {
            mTaigiCandidateController.setRawInput(rawInput);
            Log.d(TAG, "setRawInputForCandidate: " + rawInput);
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
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleShift() {
        if (mTaigiKeyboardView == null) {
            return;
        }

        if (mKeyboardSwitcher.isCurrentKeyboardViewUseQwertyKeyboard()) {
            // Alphabet keyboard
            checkToggleCapsLock();

            mTaigiKeyboardView.setShifted(mCapsLock || !mTaigiKeyboardView.isShifted());
        } else {
            mKeyboardSwitcher.handleShift();
        }

        // update icon
        updateShiftIcon();
    }

    private void updateShiftIcon() {
        final List<Keyboard.Key> keys = mTaigiKeyboardView.getKeyboard().getKeys();
        final int shiftKeyIndex = mTaigiKeyboardView.getKeyboard().getShiftKeyIndex();
        final Keyboard.Key shiftkey = keys.get(shiftKeyIndex);
        int[] state;
        if (mCapsLock) {
            state = new int[]{android.R.attr.state_checked};/**/
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
        Log.d(TAG, "handleCharacter: " + primaryCode + ", keyCodes: " + keyCodes);

        if (isInputViewShown()) {
            if (mTaigiKeyboardView.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);

                if (!mCapsLock) {
                    mTaigiKeyboardView.setShifted(false);
                    updateShiftIcon();
                }
            }
        }

        if (mKeyboardSwitcher.isCurrentKeyboardViewUseQwertyKeyboard()) {
            mComposing.append((char) primaryCode);
            updateShiftKeyState(getCurrentInputEditorInfo());
            updateInputForCandidate();
        } else {
            sendKey(primaryCode);
        }
    }

    private void handleClose() {
        commitTyped();
        requestHideSelf(0);
        mTaigiKeyboardView.closing();
    }

    private void checkToggleCapsLock() {
        if (mCapsLock) {
            mCapsLock = false;
            mLastShiftTime = 0;
            return;
        }

        long now = System.currentTimeMillis();
        if (mLastShiftTime + 800 > now) {
            mCapsLock = !mCapsLock;
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

//    public void pickDefaultCandidate() {
//        pickSuggestionManually(0);
//    }

//    public void pickSuggestionManually(String pickedSuggestion) {
//        if (mCompletionOn && mCompletions != null && index >= 0
//                && index < mCompletions.length) {
//            CompletionInfo ci = mCompletions[index];
//            getCurrentInputConnection().commitCompletion(ci);
//            if (mTaigiWordCandidateView != null) {
//                mTaigiWordCandidateView.clear();
//            }
//            updateShiftKeyState(getCurrentInputEditorInfo());
//        } else if (mComposing.length() > 0) {
//            // If we were generating candidate suggestions for the current
//            // text, we would commit one of them here.  But for this sample,
//            // we will just commit the current text.
//            commitTyped(getCurrentInputConnection());
//        }
//    }

    public void commitRawInputSuggestion() {
        final String rawInputSuggestion = mTaigiCandidateController.getRawInputSuggestion();
        getCurrentInputConnection().commitText(rawInputSuggestion, rawInputSuggestion.length());
        mComposing.setLength(0);
        updateInputForCandidate();
    }

    public void commitPickedSuggestion(String pickedSuggestion) {
        getCurrentInputConnection().commitText(pickedSuggestion, pickedSuggestion.length());
        mComposing.setLength(0);
        updateInputForCandidate();
    }

    public void swipeRight() {
    }

    public void swipeLeft() {
    }

    public void swipeDown() {
        handleClose();
    }

    public void swipeUp() {
        handleOpenSettings();
    }

    public void onPress(int primaryCode) {
    }

    public void onRelease(int primaryCode) {
    }
}