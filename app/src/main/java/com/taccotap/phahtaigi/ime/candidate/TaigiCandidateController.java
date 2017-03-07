package com.taccotap.phahtaigi.ime.candidate;

import android.text.TextUtils;

import com.taccotap.phahtaigi.ime.TaigiIme;
import com.taccotap.phahtaigi.ime.parser.RawInputParserUtils;
import com.taccotap.taigidictmodel.tailo.TlTaigiWord;

import java.util.ArrayList;

public class TaigiCandidateController {

    private final TaigiCandidateView mTaigiCandidateView;
    private int mCurrentInputLomajiMode = TaigiIme.INPUT_LOMAJI_MODE_TAILO;

    private String mRawInput;
    private String mRawInputSuggestion;

    public TaigiCandidateController(TaigiCandidateView taigiCandidateView) {
        mTaigiCandidateView = taigiCandidateView;
    }

    public void setRawInput(String rawInput) {
        mRawInput = rawInput;
        updateCandidateView();
    }

    private void updateCandidateView() {
        ArrayList<TlTaigiWord> candidateTaigiWords = new ArrayList<>();

        final TlTaigiWord taigiWord = new TlTaigiWord();
        mRawInputSuggestion = RawInputParserUtils.parseRawInputToLomaji(mRawInput, mCurrentInputLomajiMode);
        taigiWord.setLomaji(mRawInputSuggestion);
        taigiWord.setHanji("");
        candidateTaigiWords.add(taigiWord);

        // TODO test input
        for (int i = 0; i < 10; i++) {
            final TlTaigiWord taigiWord2 = new TlTaigiWord();
            taigiWord2.setLomaji("Phah");
            taigiWord2.setHanji("拍");
            candidateTaigiWords.add(taigiWord2);

            final TlTaigiWord taigiWord1 = new TlTaigiWord();
            taigiWord1.setLomaji("Tâi-gí");
            taigiWord1.setHanji("臺語");
            candidateTaigiWords.add(taigiWord1);
        }

        mTaigiCandidateView.setSuggestions(mRawInput, candidateTaigiWords);
    }

    public String getRawInputSuggestion() {
        return mRawInputSuggestion;
    }

    public boolean hasRawInputSuggestion() {
        return !TextUtils.isEmpty(mRawInputSuggestion);
    }

    public void setCurrentInputLomajiMode(int currentInputLomajiMode) {
        mCurrentInputLomajiMode = currentInputLomajiMode;

        // TODO parse current raw input first?

        updateCandidateView();
    }

    public int getCurrentInputLomajiMode() {
        return mCurrentInputLomajiMode;
    }
}
