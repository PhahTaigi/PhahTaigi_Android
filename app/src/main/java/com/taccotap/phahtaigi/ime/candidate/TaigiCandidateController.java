package com.taccotap.phahtaigi.ime.candidate;

import android.text.TextUtils;
import android.util.Log;

import com.taccotap.phahtaigi.AppPrefs;
import com.taccotap.phahtaigi.BuildConfig;
import com.taccotap.phahtaigi.ime.converter.KipInputConverter;
import com.taccotap.phahtaigi.ime.converter.PojInputConverter;
import com.taccotap.phahtaigi.imedict.ImeDictModel;

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
    private int mCurrentInputLomajiMode = AppPrefs.INPUT_LOMAJI_MODE_POJ;

    private String mRawInput = "";
    private String mRawInputSuggestion = "";

    private ArrayList<ImeDictModel> mCandidateImeDictModels = new ArrayList<>();

    private Realm mRealm;
    private RealmResults<ImeDictModel> mImeDictModels;
    private ImeDictModel mInputImeDictModel;

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

        mCandidateImeDictModels.clear();

        mInputImeDictModel = new ImeDictModel();

        if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIP) {
            mRawInputSuggestion = KipInputConverter.INSTANCE.convertKipNumberRawInputToTailoWords(mRawInput);
            mInputImeDictModel.setKip(mRawInputSuggestion);
            mInputImeDictModel.setPoj("");
        } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
            mRawInputSuggestion = PojInputConverter.INSTANCE.convertPojNumberRawInputToPojWords(mRawInput);
            mInputImeDictModel.setPoj(mRawInputSuggestion);
            mInputImeDictModel.setKip("");
        }

        mInputImeDictModel.setHanji("");
        mCandidateImeDictModels.add(mInputImeDictModel);

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

        mTaigiCandidateView.setSuggestions(mRawInput, mCandidateImeDictModels, mCurrentInputLomajiMode);

        getSuggestionsFromDict();
    }

    private void getSuggestionsFromDict() {
        if (TextUtils.isEmpty(mRawInput)) {
            return;
        }

        if (mImeDictModels != null) {
            mImeDictModels.removeAllChangeListeners();
        }

        String search = mRawInput.toLowerCase()
                .replaceAll("1|4", "");
//                .replaceAll("6", "2");

        if (!search.matches(".*\\d+.*")) {
            if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIP) {
                mImeDictModels = mRealm.where(ImeDictModel.class)
                        .beginsWith("kipSujipBoSooji", search)
                        .or()
                        .beginsWith("kipSujipThauJibo", search)
                        .sort("kipPriority", Sort.ASCENDING)
                        .limit(100)
                        .findAllAsync();
            } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                mImeDictModels = mRealm.where(ImeDictModel.class)
                        .beginsWith("pojSujipBoSooji", search)
                        .or()
                        .beginsWith("pojSujipThauJibo", search)
                        .sort("pojPriority", Sort.ASCENDING)
                        .limit(100)
                        .findAllAsync();
            }
        } else {
            if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIP) {
                mImeDictModels = mRealm.where(ImeDictModel.class)
                        .beginsWith("kipSujip", search)
                        .sort("kipPriority", Sort.ASCENDING)
                        .limit(100)
                        .findAllAsync();
            } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                mImeDictModels = mRealm.where(ImeDictModel.class)
                        .beginsWith("pojSujip", search)
                        .sort("pojPriority", Sort.ASCENDING)
                        .limit(100)
                        .findAllAsync();
            }
        }

        mImeDictModels.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<ImeDictModel>>() {
            @Override
            public void onChange(RealmResults<ImeDictModel> imeDictModels, OrderedCollectionChangeSet orderedCollectionChangeSet) {
                if (TextUtils.isEmpty(mRawInput)) {
                    return;
                }

                if (BuildConfig.DEBUG_LOG) {
                    Log.w(TAG, "handleQueryResults: count = " + imeDictModels.size());
                }

//                for (ImeDict imeDict : mImeDicts) {
//                    Log.d(TAG, "poj = " + imeDict.poj + ", hanji = " + imeDict.getHanji());
//                }

                ArrayList<ImeDictModel> mutableArrayList = new ArrayList<>(mRealm.copyFromRealm(imeDictModels));

                handleQueryResultsAsync(mutableArrayList);
            }
        });
    }

    private void handleQueryResultsAsync(ArrayList<ImeDictModel> mutableArrayList) {
        handleQueryResults(mutableArrayList, mCurrentInputLomajiMode, mRawInput)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<ArrayList<ImeDictModel>>() {
                    @Override
                    public void accept(ArrayList<ImeDictModel> imeDictModels) throws Exception {
                        mCandidateImeDictModels.clear();
                        mCandidateImeDictModels.addAll(imeDictModels);
                        mCandidateImeDictModels.add(0, mInputImeDictModel);

                        mTaigiCandidateView.setSuggestions(mRawInput, mCandidateImeDictModels, mCurrentInputLomajiMode);
                    }
                })
                .subscribe();
    }

    private Flowable<ArrayList<ImeDictModel>> handleQueryResults(final ArrayList<ImeDictModel> imeDictModels, final int currentInputLomajiMode, final String rawInput) {
        return Flowable.create(new FlowableOnSubscribe<ArrayList<ImeDictModel>>() {
            @Override
            public void subscribe(FlowableEmitter<ArrayList<ImeDictModel>> flowableEmitter) throws Exception {
                ArrayList<ImeDictModel> suggestions = new ArrayList<>();

                int count = imeDictModels.size();
                for (int i = 0; i < count; i++) {
                    final ImeDictModel imeDictModel = imeDictModels.get(i);
                    ImeDictModel newImeDictModel = new ImeDictModel(imeDictModel);

                    boolean isRawInputEqual = rawInput.substring(0, 1).toUpperCase().equals(rawInput.substring(0, 1));
                    if (currentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIP) {
                        if (rawInput.toUpperCase().equals(rawInput)) {
                            newImeDictModel.setKip(imeDictModel.getKip().toUpperCase());
                        } else if (isRawInputEqual) {
                            newImeDictModel.setKip(imeDictModel.getKip().substring(0, 1).toUpperCase() + imeDictModel.getKip().substring(1));
                        }
                    } else if (currentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                        if (rawInput.toUpperCase().equals(rawInput)) {
                            newImeDictModel.setPoj(imeDictModel.getPoj().toUpperCase());
                        } else if (isRawInputEqual) {
                            newImeDictModel.setPoj(imeDictModel.getPoj().substring(0, 1).toUpperCase() + imeDictModel.getPoj().substring(1));
                        }
                    }

                    suggestions.add(newImeDictModel);
                }

                // do sorting again
                Collections.sort(suggestions, new Comparator<ImeDictModel>() {
                    @Override
                    public int compare(ImeDictModel o1, ImeDictModel o2) {
                        if (currentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIP) {
                            if (o1.getKip().length() > o2.getKip().length()) {
                                return 1;
                            } else if (o1.getKip().length() < o2.getKip().length()) {
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

    private int getPrioritySorting(int currentInputLomajiMode, ImeDictModel o1, ImeDictModel o2) {
        if (currentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIP) {
            if (o1.getKipPriority() > o2.getKipPriority()) {
                return 1;
            } else if (o1.getKipPriority() < o2.getKipPriority()) {
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
