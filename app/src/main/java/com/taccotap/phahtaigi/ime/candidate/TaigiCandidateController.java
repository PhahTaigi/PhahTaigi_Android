package com.taccotap.phahtaigi.ime.candidate;

import android.text.TextUtils;

import com.taccotap.phahtaigi.dictmodel.ImeDict;
import com.taccotap.phahtaigi.ime.TaigiIme;
import com.taccotap.phahtaigi.ime.converter.PojInputConverter;
import com.taccotap.phahtaigi.ime.converter.TailoInputConverter;

import java.util.ArrayList;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

public class TaigiCandidateController {

    private TaigiCandidateView mTaigiCandidateView;
    private int mCurrentInputLomajiMode = TaigiIme.INPUT_LOMAJI_MODE_TAILO;

    private String mRawInput;
    private String mRawInputSuggestion;

    private ArrayList<ImeDict> mCandidateImeDicts = new ArrayList<>();

    private Realm mRealm;
    private RealmResults<ImeDict> mImeDicts;
    private ImeDict mInputImeDict;

    public TaigiCandidateController() {
    }

    public void setRawInput(String rawInput) {
        mRawInput = rawInput;
        mRealm = Realm.getDefaultInstance();

        updateCandidateView();
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

    public void setTaigiCandidateView(TaigiCandidateView taigiCandidateView) {
        mTaigiCandidateView = taigiCandidateView;
    }

    private void updateCandidateView() {
        if (mTaigiCandidateView == null) {
            return;
        }

        mCandidateImeDicts.clear();

        mInputImeDict = new ImeDict();

        if (mCurrentInputLomajiMode == TaigiIme.INPUT_LOMAJI_MODE_TAILO) {
            mRawInputSuggestion = TailoInputConverter.convertTailoNumberRawInputToTailoWords(mRawInput);
            mInputImeDict.setTailo(mRawInputSuggestion);
            mInputImeDict.setPoj("");
        } else if (mCurrentInputLomajiMode == TaigiIme.INPUT_LOMAJI_MODE_POJ) {
            mRawInputSuggestion = PojInputConverter.convertPojNumberRawInputToPojWords(mRawInput);
            mInputImeDict.setPoj(mRawInputSuggestion);
            mInputImeDict.setTailo("");
        }

        mInputImeDict.setHanji("");
        mCandidateImeDicts.add(mInputImeDict);

//        // test input
//        for (int i = 0; i < 10; i++) {
//            final TlTaigiWord taigiWord2 = new TlTaigiWord();
//            taigiWord2.setTailo("Phah");
//            taigiWord2.setHanji("拍");
//            candidateImeDicts.add(taigiWord2);
//
//            final TlTaigiWord taigiWord1 = new TlTaigiWord();
//            taigiWord1.setTailo("Tâi-gí");
//            taigiWord1.setHanji("臺語");
//            candidateImeDicts.add(taigiWord1);
//        }

        mTaigiCandidateView.setSuggestions(mRawInput, mCandidateImeDicts, mCurrentInputLomajiMode);

        if (!TextUtils.isEmpty(mRawInput)) {
            getSuggestionsFromDict();
        }
    }

    private void getSuggestionsFromDict() {
        if (mImeDicts != null) {
            mImeDicts.removeAllChangeListeners();
        }

        String search = mRawInput.toLowerCase().replaceAll("1|4", "");

        if (!search.matches(".*\\d+.*")) {
            if (mCurrentInputLomajiMode == TaigiIme.INPUT_LOMAJI_MODE_TAILO) {
                mImeDicts = mRealm.where(ImeDict.class)
                        .equalTo("tailoInputWithoutTone", search)
                        .findAllAsync();
            } else if (mCurrentInputLomajiMode == TaigiIme.INPUT_LOMAJI_MODE_POJ) {
                mImeDicts = mRealm.where(ImeDict.class)
                        .equalTo("pojInputWithoutTone", search)
                        .findAllAsync();
            }
        } else {
            if (mCurrentInputLomajiMode == TaigiIme.INPUT_LOMAJI_MODE_TAILO) {
                mImeDicts = mRealm.where(ImeDict.class)
                        .equalTo("tailoInputWithNumberTone", search)
                        .findAllAsync();
            } else if (mCurrentInputLomajiMode == TaigiIme.INPUT_LOMAJI_MODE_POJ) {
                mImeDicts = mRealm.where(ImeDict.class)
                        .equalTo("pojInputWithNumberTone", search)
                        .findAllAsync();
            }
        }

        mImeDicts.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<ImeDict>>() {
            @Override
            public void onChange(RealmResults<ImeDict> imeDicts, OrderedCollectionChangeSet orderedCollectionChangeSet) {
                ArrayList<ImeDict> suggestions = new ArrayList<>();

                for (ImeDict imeDict : imeDicts) {
                    ImeDict newImeDict = new ImeDict();

                    if (mCurrentInputLomajiMode == TaigiIme.INPUT_LOMAJI_MODE_TAILO) {
                        if (mRawInput.toUpperCase().equals(mRawInput)) {
                            newImeDict.setTailo(imeDict.getTailo().toUpperCase());
                        } else if (mRawInput.substring(0, 1).toUpperCase().equals(mRawInput.substring(0, 1))) {
                            newImeDict.setTailo(imeDict.getTailo().substring(0, 1).toUpperCase() + imeDict.getTailo().substring(1));
                        } else {
                            newImeDict.setTailo(imeDict.getTailo());
                        }
                    } else if (mCurrentInputLomajiMode == TaigiIme.INPUT_LOMAJI_MODE_POJ) {
                        if (mRawInput.toUpperCase().equals(mRawInput)) {
                            newImeDict.setPoj(imeDict.getPoj().toUpperCase());
                        } else if (mRawInput.substring(0, 1).toUpperCase().equals(mRawInput.substring(0, 1))) {
                            newImeDict.setPoj(imeDict.getPoj().substring(0, 1).toUpperCase() + imeDict.getPoj().substring(1));
                        } else {
                            newImeDict.setPoj(imeDict.getPoj());
                        }
                    }

                    newImeDict.setHanji(imeDict.getHanji());

                    suggestions.add(newImeDict);
                }

                mCandidateImeDicts.clear();
                mCandidateImeDicts.add(mInputImeDict);
                mCandidateImeDicts.addAll(suggestions);
                mTaigiCandidateView.setSuggestions(mRawInput, mCandidateImeDicts, mCurrentInputLomajiMode);
            }
        });
    }
}
