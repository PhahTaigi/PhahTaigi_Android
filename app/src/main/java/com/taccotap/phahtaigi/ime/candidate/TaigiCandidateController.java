package com.taccotap.phahtaigi.ime.candidate;

import android.text.TextUtils;
import android.util.Log;

import com.taccotap.phahtaigi.AppPrefs;
import com.taccotap.phahtaigi.BuildConfig;
import com.taccotap.phahtaigi.dictmodel.ImeDict;
import com.taccotap.phahtaigi.ime.converter.KiplmjInputConverter;
import com.taccotap.phahtaigi.ime.converter.PojInputConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class TaigiCandidateController {
    private static final String TAG = TaigiCandidateController.class.getSimpleName();

    private static final int QUERY_LIMIT_100 = 150;

    private TaigiCandidateView mTaigiCandidateView;
    private int mCurrentInputLomajiMode = AppPrefs.INPUT_LOMAJI_MODE_KIPLMJ;

    private String mRawInput = "";
    private String mRawInputSuggestion = "";

    private ArrayList<ImeDict> mCandidateImeDicts = new ArrayList<>();

    private Realm mRealm;
    private RealmResults<ImeDict> mImeDicts;
    private ImeDict mInputImeDict;

    private boolean mIsSetQueryLimit = false;

    public TaigiCandidateController() {
    }

    public void setRawInput(String rawInput) {
        if (BuildConfig.DEBUG_LOG) {
            Log.i(TAG, "setRawInput(): " + rawInput);
        }

        if (rawInput == null) {
            mRawInput = "";
        } else {
            mRawInput = rawInput;
        }
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

        if (mCurrentInputLomajiMode != AppPrefs.INPUT_LOMAJI_MODE_ENGLISH) {
            updateCandidateView();
        }
    }

//    public int getCurrentInputLomajiMode() {
//        return mCurrentInputLomajiMode;
//    }

    public void setTaigiCandidateView(TaigiCandidateView taigiCandidateView) {
        mTaigiCandidateView = taigiCandidateView;
    }

    private void updateCandidateView() {
        if (mTaigiCandidateView == null) {
            return;
        }

        mCandidateImeDicts.clear();

        mInputImeDict = new ImeDict();

        if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIPLMJ) {
            mRawInputSuggestion = KiplmjInputConverter.INSTANCE.convertTailoNumberRawInputToTailoWords(mRawInput);
            mInputImeDict.setKiplmj(mRawInputSuggestion);
            mInputImeDict.setPoj("");
        } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
            mRawInputSuggestion = PojInputConverter.INSTANCE.convertPojNumberRawInputToPojWords(mRawInput);
            mInputImeDict.setPoj(mRawInputSuggestion);
            mInputImeDict.setKiplmj("");
        }

        mInputImeDict.setHanji("");
        mCandidateImeDicts.add(mInputImeDict);

//        // test input
//        for (int i = 0; i < 10; i++) {
//            final KipTaigiWord taigiWord2 = new KipTaigiWord();
//            taigiWord2.setTailo("Phah");
//            taigiWord2.setHanji("拍");
//            candidateImeDicts.add(taigiWord2);
//
//            final KipTaigiWord taigiWord1 = new KipTaigiWord();
//            taigiWord1.setTailo("Tâi-gí");
//            taigiWord1.setHanji("臺語");
//            candidateImeDicts.add(taigiWord1);
//        }

        mTaigiCandidateView.setSuggestions(mRawInput, mCandidateImeDicts, mCurrentInputLomajiMode);

        getSuggestionsFromDict();
    }

    private void getSuggestionsFromDict() {
        if (TextUtils.isEmpty(mRawInput)) {
            return;
        }

        if (mImeDicts != null) {
            mImeDicts.removeAllChangeListeners();
        }

        String search = mRawInput.toLowerCase()
                .replaceAll("1|4", "")
                .replaceAll("6", "2");

        if (!search.matches(".*\\d+.*")) {
            mIsSetQueryLimit = true;

            if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIPLMJ) {
                mImeDicts = mRealm.where(ImeDict.class)
                        .beginsWith("kiplmjInputWithoutTone", search)
                        .or()
                        .beginsWith("kiplmjShortInput", search)
                        .sort("kiplmjPriority", Sort.ASCENDING)
                        .findAllAsync();
            } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                mImeDicts = mRealm.where(ImeDict.class)
                        .beginsWith("pojInputWithoutTone", search)
                        .or()
                        .beginsWith("pojShortInput", search)
                        .sort("pojPriority", Sort.ASCENDING)
                        .findAllAsync();
            }
        } else {
            mIsSetQueryLimit = false;

            if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIPLMJ) {
                mImeDicts = mRealm.where(ImeDict.class)
                        .beginsWith("kiplmjInputWithNumberTone", search)
                        .sort("kiplmjPriority", Sort.ASCENDING)
                        .findAllAsync();
            } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                mImeDicts = mRealm.where(ImeDict.class)
                        .beginsWith("pojInputWithNumberTone", search)
                        .sort("pojPriority", Sort.ASCENDING)
                        .findAllAsync();
            }
        }

        mImeDicts.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<ImeDict>>() {
            @Override
            public void onChange(RealmResults<ImeDict> imeDicts, OrderedCollectionChangeSet orderedCollectionChangeSet) {
                if (TextUtils.isEmpty(mRawInput)) {
                    return;
                }

                int count = imeDicts.size();

                if (BuildConfig.DEBUG_LOG) {
                    Log.w(TAG, "handleQueryResults: count = " + count);
                }

                if (mIsSetQueryLimit && count > QUERY_LIMIT_100) {
                    count = QUERY_LIMIT_100;
                }

//                for (ImeDict imeDict : mImeDicts) {
//                    Log.d(TAG, "tailo = " + imeDict.getTailo() + ", hanji = " + imeDict.getHanji());
//                }

                ArrayList<ImeDict> mutableArrayList = new ArrayList<>(mRealm.copyFromRealm(imeDicts.subList(0, count)));

                handleQueryResultsAsync(mutableArrayList);
            }
        });
    }

    private void handleQueryResultsAsync(ArrayList<ImeDict> mutableArrayList) {
        handleQueryResults(mutableArrayList, mCurrentInputLomajiMode, mRawInput)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<ArrayList<ImeDict>>() {
                    @Override
                    public void accept(ArrayList<ImeDict> imeDicts) throws Exception {
                        mCandidateImeDicts.clear();
                        mCandidateImeDicts.addAll(imeDicts);
                        mCandidateImeDicts.add(0, mInputImeDict);

                        mTaigiCandidateView.setSuggestions(mRawInput, mCandidateImeDicts, mCurrentInputLomajiMode);
                    }
                })
                .subscribe();
    }

    private Flowable<ArrayList<ImeDict>> handleQueryResults(final ArrayList<ImeDict> imeDicts, final int currentInputLomajiMode, final String rawInput) {
        return Flowable.create(new FlowableOnSubscribe<ArrayList<ImeDict>>() {
            @Override
            public void subscribe(FlowableEmitter<ArrayList<ImeDict>> flowableEmitter) throws Exception {
                ArrayList<ImeDict> suggestions = new ArrayList<>();

                int count = imeDicts.size();
                for (int i = 0; i < count; i++) {
                    final ImeDict imeDict = imeDicts.get(i);
                    ImeDict newImeDict = new ImeDict(imeDict);

                    if (currentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIPLMJ) {
                        if (rawInput.toUpperCase().equals(rawInput)) {
                            newImeDict.setKiplmj(imeDict.getKiplmj().toUpperCase());
                        } else if (rawInput.substring(0, 1).toUpperCase().equals(rawInput.substring(0, 1))) {
                            newImeDict.setKiplmj(imeDict.getKiplmj().substring(0, 1).toUpperCase() + imeDict.getKiplmj().substring(1));
                        }
                    } else if (currentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                        if (rawInput.toUpperCase().equals(rawInput)) {
                            newImeDict.setPoj(imeDict.getPoj().toUpperCase());
                        } else if (rawInput.substring(0, 1).toUpperCase().equals(rawInput.substring(0, 1))) {
                            newImeDict.setPoj(imeDict.getPoj().substring(0, 1).toUpperCase() + imeDict.getPoj().substring(1));
                        }
                    }

                    suggestions.add(newImeDict);
                }

                // do sorting again
                Collections.sort(suggestions, new Comparator<ImeDict>() {
                    @Override
                    public int compare(ImeDict o1, ImeDict o2) {
                        if (currentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIPLMJ) {
                            if (o1.getKiplmj().length() > o2.getKiplmj().length()) {
                                return 1;
                            } else if (o1.getKiplmj().length() < o2.getKiplmj().length()) {
                                return -1;
                            } else {
                                return getPrioritySorting(currentInputLomajiMode, o1, o2);
                            }
                        } else if (currentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                            if (o1.getPoj().length() > o2.getPoj().length()) {
                                return 1;
                            } else if (o1.getPoj().length() < o2.getPoj().length()) {
                                return -1;
                            } else {
                                return getPrioritySorting(currentInputLomajiMode, o1, o2);
                            }
                        } else {
                            return 0;
                        }
                    }
                });

                flowableEmitter.onNext(suggestions);
                flowableEmitter.onComplete();
            }
        }, BackpressureStrategy.BUFFER);
    }

    private int getPrioritySorting(int currentInputLomajiMode, ImeDict o1, ImeDict o2) {
        if (currentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIPLMJ) {
            if (o1.getKiplmjPriority() > o2.getKiplmjPriority()) {
                return 1;
            } else if (o1.getKiplmjPriority() < o2.getKiplmjPriority()) {
                return -1;
            } else {
                return 0;
            }
        } else if (currentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
            if (o1.getPojPriority() > o2.getPojPriority()) {
                return 1;
            } else if (o1.getPojPriority() < o2.getPojPriority()) {
                return -1;
            } else {
                return 0;
            }
        }

        return 0;
    }
}
