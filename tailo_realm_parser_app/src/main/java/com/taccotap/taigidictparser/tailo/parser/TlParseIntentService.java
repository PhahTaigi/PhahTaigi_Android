package com.taccotap.taigidictparser.tailo.parser;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.taccotap.phahtaigi.dictmodel.ImeDict;
import com.taccotap.taigidictmodel.tailo.TlTaigiWord;
import com.taccotap.taigidictparser.custom.CustomTaigiWords;
import com.taccotap.taigidictparser.utils.ExcelUtils;
import com.taccotap.taigidictparser.utils.LomajiPhraseSplitter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.ArrayList;
import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmResults;

public class TlParseIntentService extends IntentService {
    private static final String TAG = TlParseIntentService.class.getSimpleName();

    private static final String ASSETS_PATH_TAILO_DICT_TAIGI_WORDS = "tailo/詞目總檔(含俗諺).xls";

    private Realm mRealm;
    private ArrayList<ImeDict> mImeDictArrayList = new ArrayList<>();

    public TlParseIntentService() {
        super("TlParseIntentService");
    }

    public static void startParsing(Context context) {
        Intent intent = new Intent(context, TlParseIntentService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // worker thread
        parseDict();
    }

    private void parseDict() {
        mRealm = Realm.getDefaultInstance();

        parseDictTaigiWord();
        injectCustomTaigiWords();
        handleTaigiWords();

        mRealm.close();

        Log.d(TAG, "finish ALL");
    }

    private void parseDictTaigiWord() {
        Log.d(TAG, "start: parseDictTaigiWord()");

        final HSSFWorkbook workbook = ExcelUtils.readExcelWorkbookFromAssetsFile(this, ASSETS_PATH_TAILO_DICT_TAIGI_WORDS);
        if (workbook != null) {
            final HSSFSheet firstSheet = workbook.getSheetAt(0);

            Iterator rowIterator = firstSheet.rowIterator();
            final ArrayList<TlTaigiWord> taigiWords = new ArrayList<>();

            int rowNum = 1;
            while (rowIterator.hasNext()) {
                HSSFRow currentRow = (HSSFRow) rowIterator.next();
                if (rowNum == 1) {
                    rowNum++;
                    continue;
                }

                Iterator cellIterator = currentRow.cellIterator();

                final TlTaigiWord currentTaigiWord = new TlTaigiWord();

                int colNum = 1;
                while (cellIterator.hasNext()) {
                    HSSFCell currentCell = (HSSFCell) cellIterator.next();

                    if (colNum == 1) {
                        final String stringCellValue = currentCell.getStringCellValue();
                        currentTaigiWord.setMainCode(Integer.valueOf(stringCellValue));
                    } else if (colNum == 2) {
                        final String stringCellValue = currentCell.getStringCellValue();
                        currentTaigiWord.setWordPropertyCode(Integer.valueOf(stringCellValue));
                    } else if (colNum == 3) {
                        currentTaigiWord.setHanji(currentCell.getStringCellValue());
                    } else if (colNum == 4) {
                        currentTaigiWord.setLomaji(currentCell.getStringCellValue());
                    } else {
                        break;
                    }

                    colNum++;
                }

                if (!TextUtils.isEmpty(currentTaigiWord.getLomaji()) && !TextUtils.isEmpty(currentTaigiWord.getHanji())) {
                    taigiWords.add(currentTaigiWord);
                }

                rowNum++;
            }

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (TlTaigiWord taigiWord : taigiWords) {
                        realm.copyToRealmOrUpdate(taigiWord);
                    }
                }
            });
        }

        Log.d(TAG, "finish: parseDictTaigiWord()");
    }

    private void injectCustomTaigiWords() {
        Log.d(TAG, "start: injectCustomTaigiWords()");

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (TlTaigiWord taigiWord : CustomTaigiWords.sTaigiWordArrayList) {
                    realm.copyToRealmOrUpdate(taigiWord);
                }
            }
        });

        Log.d(TAG, "finish: injectCustomTaigiWords()");
    }

    private void handleTaigiWords() {
        Log.d(TAG, "start: handleTaigiWords()");

        mImeDictArrayList.clear();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.delete(ImeDict.class);
            }
        });

        final RealmResults<TlTaigiWord> taigiWords = mRealm.where(TlTaigiWord.class).findAll();

        int count = taigiWords.size();
        for (int i = 0; i < count; i++) {
            TlTaigiWord taigiWord = taigiWords.get(i);

            // skip 俗諺
            if (taigiWord.getWordPropertyCode() == 25) {
                continue;
            }

            String tailo = taigiWord.getLomaji();
            String hanji = taigiWord.getHanji();

            boolean isStorePerWord = false;
            if (tailo.contains("/")) {
                final String[] lomajiPhrases = tailo.split("/");
                for (String lomajiPhrase : lomajiPhrases) {
                    // not split words for 專有名詞
                    if (taigiWord.getWordPropertyCode() >= 11 && taigiWord.getWordPropertyCode() <= 22) {
                        isStorePerWord = false;
                    } else {
                        isStorePerWord = true;
                    }

                    handleTailoPhrase(taigiWord, lomajiPhrase.trim(), hanji, isStorePerWord);
                }
            } else {
                // not split words for 專有名詞
                if (taigiWord.getWordPropertyCode() >= 11 && taigiWord.getWordPropertyCode() <= 22) {
                    isStorePerWord = false;
                } else {
                    isStorePerWord = true;
                }

                handleTailoPhrase(taigiWord, tailo.trim(), hanji, isStorePerWord);
            }
        }

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(TAG, "start: store mImeDictArrayList");

                int count = mImeDictArrayList.size();
                for (int i = 0; i < count; i++) {
                    ImeDict imeDict = mImeDictArrayList.get(i);

                    Log.d(TAG, "tailoWord=" + imeDict.getTailo() + ", tailoNumberTone=" + imeDict.getTailoInputWithNumberTone() + ", tailoWithoutTone=" + imeDict.getTailoInputWithoutTone() + ", hanjiWord=" + imeDict.getHanji() + ", poj=" + imeDict.getPoj() + ", getPojInputWithNumberTone=" + imeDict.getPojInputWithNumberTone() + ", getPojInputWithoutTone=" + imeDict.getPojInputWithoutTone());

                    imeDict.setWordId(i);

                    try {
                        realm.copyToRealmOrUpdate(imeDict);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }

                Log.d(TAG, "finish: store mImeDictArrayList");
            }
        });

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.delete(TlTaigiWord.class);
            }
        });

        Log.d(TAG, "finish: handleTaigiWords()");
    }

    private void handleTailoPhrase(TlTaigiWord taigiWord, String tailoPhrase, String hanji, boolean isStorePerWord) {
        StringBuilder tailoNumberWordsStringBuilder = new StringBuilder();
        StringBuilder pojNumberWordsStringBuilder = new StringBuilder();
        StringBuilder pojPhraseStringBuilder = new StringBuilder();

        // ignore "--" prefix
        if (tailoPhrase.startsWith("--")) {
            tailoPhrase = tailoPhrase.substring(2);
        }

        // check 專有名詞
        String firstChar = tailoPhrase.substring(0, 1);
        if (firstChar.equals(firstChar.toUpperCase())) {
//                Log.w(TAG, "專有名詞, tailoPhrase: " + tailoPhrase + ", hanji: " + hanji);
            isStorePerWord = false;
        }

        LomajiPhraseSplitter lomajiPhraseSplitter = new LomajiPhraseSplitter();
        final LomajiPhraseSplitter.LomajiPhraseSplitterResult lomajiPhraseSplitterResult = lomajiPhraseSplitter.split(tailoPhrase);

        int lomajiWordToneNumber = 10;
        if (lomajiPhraseSplitterResult.getSplitSperators().size() > 0) {
            final ArrayList<String> tailoWords = lomajiPhraseSplitterResult.getSplitStrings();

            if (!isLomajiLengthMatchHanjiLength(tailoWords.size(), hanji)) {
                Log.e(TAG, "Lomaji not match Hanji: lomaji = " + tailoPhrase + ", hanji = " + hanji);
                return;
            } else {
                int hanjiCharPos = 0;
                int hanjiCharCount = 0;
                int length = tailoWords.size();

                for (int i = 0; i < length; i++) {
                    String tailoWord = tailoWords.get(i);

//                    try {
//                        Log.d(TAG, "i=" + i + ", tailoPhrase = " + tailoPhrase + ", tailoWord = " + tailoWord + ", hanji=" + hanji + ", hanji unicode=" + StrToUnicode(hanji));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    hanjiCharCount = Character.charCount(hanji.codePointAt(hanjiCharPos));
                    String hanjiWord = hanji.substring(hanjiCharPos, hanjiCharPos + hanjiCharCount);
                    hanjiCharPos += hanjiCharCount;

//                Log.d(TAG, "i=" + i + ", tailoWord = " + tailoWord + ", hanjiWord = " + hanjiWord);

                    final String tailoInputWithNumberTone = LomajiConverter.convertLomajiWordToNumberTone(tailoWord, LomajiConverter.LOMAJI_TYPE_TAILO);

                    final String pojInputWithNumberTone = LomajiConverter.convertTailoInputWordToPojInputWord(tailoInputWithNumberTone);
                    final String pojNumberWord = LomajiConverter.convertTailoNumberWordToPojNumberWord(tailoInputWithNumberTone);
                    final String pojWord = PojInputConverter.convertPojNumberToPoj(pojNumberWord);

                    if (i == 0) {
                        lomajiWordToneNumber = LomajiConverter.getLomajiWordToneNumber(tailoInputWithNumberTone);
                    }

                    if (isStorePerWord) {
                        final String tailoInputWithoutTone = LomajiConverter.removeToneNumberAndHyphens(tailoInputWithNumberTone.toLowerCase());
                        final String pojInputWithoutTone = LomajiConverter.removeToneNumberAndHyphens(pojInputWithNumberTone.toLowerCase());

                        int toneNumber = LomajiConverter.getLomajiWordToneNumber(tailoInputWithNumberTone);

                        prepareImeDict(taigiWord, tailoWord, tailoInputWithNumberTone.toLowerCase(), tailoInputWithoutTone, pojWord, pojInputWithNumberTone.toLowerCase(), pojInputWithoutTone, hanjiWord, toneNumber);
                    }

                    tailoNumberWordsStringBuilder.append(tailoInputWithNumberTone);
                    pojNumberWordsStringBuilder.append(pojInputWithNumberTone);

                    pojPhraseStringBuilder.append(pojWord);
                    if (i < length - 1) {
                        pojPhraseStringBuilder.append(lomajiPhraseSplitterResult.getSplitSperators().get(i));
                    }
                }
            }
        } else {
            // single word case
            final String tailoNumberWord = LomajiConverter.convertLomajiWordToNumberTone(tailoPhrase, LomajiConverter.LOMAJI_TYPE_TAILO);

            final String pojInputWord = LomajiConverter.convertTailoInputWordToPojInputWord(tailoNumberWord);
            final String pojNumberWord = LomajiConverter.convertTailoNumberWordToPojNumberWord(tailoNumberWord);
            final String pojWord = PojInputConverter.convertPojNumberToPoj(pojNumberWord);

            lomajiWordToneNumber = LomajiConverter.getLomajiWordToneNumber(tailoNumberWord);

            tailoNumberWordsStringBuilder.append(tailoNumberWord);
            pojNumberWordsStringBuilder.append(pojInputWord);
            pojPhraseStringBuilder.append(pojWord);
        }

        final String tailoInputWithNumberTone = tailoNumberWordsStringBuilder.toString().toLowerCase();
        final String tailoInputWithoutTone = LomajiConverter.removeToneNumberAndHyphens(tailoInputWithNumberTone);
//        Log.d(TAG, "tailoPhrase = " + tailoPhrase + ", tailoInputWithNumberTone = " + tailoInputWithNumberTone + ", tailoInputWithoutTone = " + tailoInputWithoutTone);

        final String pojPhrase = pojPhraseStringBuilder.toString();
        final String pojInputWithNumberTone = pojNumberWordsStringBuilder.toString().toLowerCase();
        final String pojInputWithoutTone = LomajiConverter.removeToneNumberAndHyphens(pojInputWithNumberTone);

        prepareImeDict(taigiWord, tailoPhrase, tailoInputWithNumberTone, tailoInputWithoutTone, pojPhrase, pojInputWithNumberTone, pojInputWithoutTone, taigiWord.getHanji(), lomajiWordToneNumber);
    }

    private void prepareImeDict(TlTaigiWord taigiWord, String lomajiPhrase, String lomajiInputWithNumberTone, String lomajiInputWithoutTone, String pojPhrase, String pojInputWithNumberTone, String pojInputWithoutTone, String hanji, int firstWordToneNumber) {
        ImeDict newImeDict = new ImeDict();

        newImeDict.setMainCode(taigiWord.getMainCode());
        newImeDict.setWordPropertyCode(taigiWord.getWordPropertyCode());
        newImeDict.setTailo(lomajiPhrase);
        newImeDict.setTailoInputWithNumberTone(lomajiInputWithNumberTone);
        newImeDict.setTailoInputWithoutTone(lomajiInputWithoutTone);
        newImeDict.setPoj(pojPhrase);
        newImeDict.setPojInputWithNumberTone(pojInputWithNumberTone);
        newImeDict.setPojInputWithoutTone(pojInputWithoutTone);
        newImeDict.setHanji(hanji);
        newImeDict.setFirstWordToneNumber(firstWordToneNumber);

        for (ImeDict imeDict : mImeDictArrayList) {
            if (newImeDict.getTailo().equals(imeDict.getTailo()) && newImeDict.getHanji().equals(imeDict.getHanji())) {
                return;
            }
        }

        mImeDictArrayList.add(newImeDict);
    }

    private boolean isLomajiLengthMatchHanjiLength(int lomajiLength, String hanji) {
        int hanjiLength = 0;

        int hanjiCharCount = hanji.length();
        for (int i = 0; i < hanjiCharCount; ) {
            int thisCharCount = Character.charCount(hanji.codePointAt(i));
            i += thisCharCount;
            hanjiLength++;
        }

        return hanjiLength == lomajiLength;
    }

//    public static String parseStringToUnicode(String str) throws Exception {
//        StringBuffer outHexStrBuf = new StringBuffer();
//        for (char c : str.toCharArray()) {
//            outHexStrBuf.append("\\u");
//            String hexStr = Integer.toHexString(c);
//            for (int i = 0; i < (4 - hexStr.length()); i++) outHexStrBuf.append("0");
//            outHexStrBuf.append(hexStr);
//        }
//        return outHexStrBuf.toString();
//    }
}