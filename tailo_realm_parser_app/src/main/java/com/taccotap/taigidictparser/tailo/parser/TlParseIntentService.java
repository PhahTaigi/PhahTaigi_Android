package com.taccotap.taigidictparser.tailo.parser;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.taccotap.phahtaigi.dictmodel.ImeDict;
import com.taccotap.taigidictmodel.tailo.TlTaigiMadeWordCode;
import com.taccotap.taigidictmodel.tailo.TlTaigiWord;
import com.taccotap.taigidictmodel.tailo.TlTaigiWordOtherPronounce;
import com.taccotap.taigidictparser.custom.CustomTaigiWords;
import com.taccotap.taigidictparser.utils.ExcelUtils;
import com.taccotap.taigidictparser.utils.LomajiPhraseSplitter;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVReader;
import io.realm.Realm;
import io.realm.RealmResults;

public class TlParseIntentService extends IntentService {
    private static final String TAG = TlParseIntentService.class.getSimpleName();

    private static final String ASSETS_PATH_TAILO_DICT_TAIGI_WORDS = "tailo/詞目總檔(含俗諺).xls";
    private static final String ASSETS_PATH_TAILO_DICT_TAIGI_WORDS_ANOTHER_PRONOUNCE = "tailo/又音(又唸作).xls";
    private static final String ASSETS_PATH_TAILO_DICT_TAIGI_MADE_WORDS_CODES = "tailo/x-造字.csv";

    private static Pattern sTailoShortInputExtractPattern = Pattern.compile("^(ph|p|m|b|th|tsh|ts|t|n|l|kh|k|ng|g|h|s|j|a|i|u|e|oo|o])", Pattern.CASE_INSENSITIVE);
    private static Pattern sPojShotInputExtractPattern = Pattern.compile("^(ph|p|m|b|th|t|n|l|kh|k|ng|g|h|chh|ch|s|j|a|i|u|e|oo|o)", Pattern.CASE_INSENSITIVE);

    private ArrayList<TlTaigiMadeWordCode> mTaigiMadeWordCodes = new ArrayList<>();

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
        parseDictTaigiWordAnotherPronounce();

        prepareMadeWordCode();

        mImeDictArrayList.clear();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.delete(ImeDict.class);
            }
        });

        handleDefaultTaigiWords();
        handleAnotherPronounceTaigiWords();

        storeImeDicts();

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.delete(TlTaigiWord.class);
                mRealm.delete(TlTaigiWordOtherPronounce.class);
            }
        });
        mRealm.close();

        Log.d(TAG, "finish ALL");
    }

    private void prepareMadeWordCode() {
        Log.d(TAG, "start: prepareMadeWordCode()");

        AssetManager assetManager = getAssets();

        try {
            InputStream csvStream = assetManager.open(ASSETS_PATH_TAILO_DICT_TAIGI_MADE_WORDS_CODES);
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
            CSVReader csvReader = new CSVReader(csvStreamReader);
            String[] line;

            // throw away the header
            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {
                String madeWordCode = StringEscapeUtils.unescapeJava("\\u" + line[0]);
                TlTaigiMadeWordCode tlTaigiMadeWordCode = new TlTaigiMadeWordCode(madeWordCode, line[1]);
                mTaigiMadeWordCodes.add(tlTaigiMadeWordCode);

                Log.d(TAG, "line[0] = " + line[0] + ", line[1] = " + line[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "finish: prepareMadeWordCode()");
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

    private void parseDictTaigiWordAnotherPronounce() {
        Log.d(TAG, "start: parseDictTaigiWordAnotherPronounce()");

        final HSSFWorkbook workbook = ExcelUtils.readExcelWorkbookFromAssetsFile(this, ASSETS_PATH_TAILO_DICT_TAIGI_WORDS_ANOTHER_PRONOUNCE);
        if (workbook != null) {
            final HSSFSheet firstSheet = workbook.getSheetAt(0);

            Iterator rowIterator = firstSheet.rowIterator();
            final ArrayList<TlTaigiWordOtherPronounce> taigiWordOtherPronounces = new ArrayList<>();

            int rowNum = 1;
            while (rowIterator.hasNext()) {
                HSSFRow currentRow = (HSSFRow) rowIterator.next();
                if (rowNum == 1) {
                    rowNum++;
                    continue;
                }

                Iterator cellIterator = currentRow.cellIterator();

                final TlTaigiWordOtherPronounce currentTaigiWordOtherPronounce = new TlTaigiWordOtherPronounce();

                int colNum = 1;
                while (cellIterator.hasNext()) {
                    HSSFCell currentCell = (HSSFCell) cellIterator.next();

                    if (colNum == 1) {
                        final String stringCellValue = currentCell.getStringCellValue();
                        currentTaigiWordOtherPronounce.setIndex(Integer.valueOf(stringCellValue));
                    } else if (colNum == 2) {
                        final String stringCellValue = currentCell.getStringCellValue();
                        currentTaigiWordOtherPronounce.setMainCode(Integer.valueOf(stringCellValue));
                    } else if (colNum == 3) {
                        currentTaigiWordOtherPronounce.setLomaji(currentCell.getStringCellValue());
                    } else {
                        break;
                    }

                    colNum++;
                }

                taigiWordOtherPronounces.add(currentTaigiWordOtherPronounce);

                rowNum++;
            }

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (TlTaigiWordOtherPronounce taigiWordOtherPronounce : taigiWordOtherPronounces) {
                        realm.copyToRealmOrUpdate(taigiWordOtherPronounce);
                    }
                }
            });

            Log.d(TAG, "finish: parseDictTaigiWordAnotherPronounce()");
        }
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

    private void handleDefaultTaigiWords() {
        Log.d(TAG, "start: handleDefaultTaigiWords()");

        final RealmResults<TlTaigiWord> taigiWords = mRealm.where(TlTaigiWord.class).findAll();

        final ArrayList<TlTaigiWord> taigiWordArrayList = new ArrayList<>(mRealm.copyFromRealm(taigiWords));
        handleEachTaigiWord(taigiWordArrayList);

        Log.d(TAG, "finish: handleDefaultTaigiWords()");
    }

    private void handleAnotherPronounceTaigiWords() {
        Log.d(TAG, "start: handleDefaultTaigiWords()");

        final RealmResults<TlTaigiWord> taigiWordRealmResults = mRealm.where(TlTaigiWord.class).findAll();
        List<TlTaigiWord> taigiWords = mRealm.copyFromRealm(taigiWordRealmResults);

        HashMap<Integer, TlTaigiWord> taigiWordsMainCodeMap = new HashMap<>();
        for (TlTaigiWord taigiWord : taigiWords) {
            taigiWordsMainCodeMap.put(taigiWord.getMainCode(), taigiWord);
        }

        final RealmResults<TlTaigiWordOtherPronounce> taigiWordOtherPronounces = mRealm.where(TlTaigiWordOtherPronounce.class).findAll();

        final ArrayList<TlTaigiWord> taigiWordArrayList = new ArrayList<>();

        for (TlTaigiWordOtherPronounce taigiWordOtherPronounce : taigiWordOtherPronounces) {
            final TlTaigiWord taigiWord = taigiWordsMainCodeMap.get(taigiWordOtherPronounce.getMainCode());
            if (taigiWord != null) {
                taigiWord.setLomaji(taigiWordOtherPronounce.getLomaji());

                taigiWordArrayList.add(taigiWord);
            }
        }

        handleEachTaigiWord(taigiWordArrayList);

        Log.d(TAG, "finish: handleDefaultTaigiWords()");
    }

    private void handleEachTaigiWord(ArrayList<TlTaigiWord> taigiWords) {
        int count = taigiWords.size();
        for (int i = 0; i < count; i++) {
            TlTaigiWord taigiWord = taigiWords.get(i);

            // skip 俗諺
            if (taigiWord.getWordPropertyCode() == 25) {
                continue;
            }

            fixMadeWordHanjiCode(taigiWord);

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
    }

    private void fixMadeWordHanjiCode(TlTaigiWord taigiWord) {
        String fixedHanji = taigiWord.getHanji();

        for (TlTaigiMadeWordCode madeWordCode : mTaigiMadeWordCodes) {
            fixedHanji = fixedHanji.replaceAll(madeWordCode.getMadeWordCode(), madeWordCode.getMadeWordReplaceCode());

            if (taigiWord.getHanji().contains(madeWordCode.getMadeWordCode())) {
                Log.w(TAG, "MadeWord found: lomaji = " + taigiWord.getLomaji() + ", hanji = " + taigiWord.getHanji() + ", fixedHanji = " + fixedHanji);
            }
        }

        taigiWord.setHanji(fixedHanji);
    }

    private void handleTailoPhrase(TlTaigiWord taigiWord, String tailoPhrase, String hanji, boolean isStorePerWord) {
//        Log.d(TAG, "handleTailoPhrase: tailoPhrase = " + tailoPhrase);

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

        long priority = 0;
        int wordLength = 0;
        StringBuilder tailoShortInputStringBuilder = new StringBuilder();
        StringBuilder pojShortInputStringBuilder = new StringBuilder();

        if (lomajiPhraseSplitterResult.getSplitSperators().size() > 0) {
            final ArrayList<String> tailoWords = lomajiPhraseSplitterResult.getSplitStrings();
            wordLength = tailoWords.size();

            if (!isLomajiLengthMatchHanjiLength(tailoWords.size(), hanji)) {
                Log.w(TAG, "Lomaji not match Hanji: lomaji = " + tailoPhrase + ", hanji = " + hanji);

                if (tailoPhrase.contains("(") || tailoPhrase.contains(")")
                        || hanji.contains("(") || hanji.contains(")") || hanji.contains("、")) {
                    // special case: 羅馬字或漢字同時有不同字表示之特例，沒有規則，暫不處理
                    Log.e(TAG, "Skip parsing: lomaji = " + tailoPhrase + ", hanji = " + hanji);
                    return;
                } else {
                    // special case: 羅馬字長度與漢字長度不對應，不處理單獨漢字與羅馬字對應，僅處理整組詞
                    isStorePerWord = false;
                }
            }

            int hanjiCharPos = 0;
            int length = tailoWords.size();

            for (int i = 0; i < length; i++) {
                String tailoWord = tailoWords.get(i);

//                    try {
//                        Log.d(TAG, "i=" + i + ", tailoPhrase = " + tailoPhrase + ", tailoWord = " + tailoWord + ", hanji=" + hanji + ", hanji unicode=" + StrToUnicode(hanji));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                String hanjiWord;
                if (isStorePerWord) {
                    int hanjiCharCount = Character.charCount(hanji.codePointAt(hanjiCharPos));
                    hanjiWord = hanji.substring(hanjiCharPos, hanjiCharPos + hanjiCharCount);
                    hanjiCharPos += hanjiCharCount;

                    // Log.d(TAG, "i=" + i + ", tailoWord = " + tailoWord + ", hanjiWord = " + hanjiWord);
                } else {
                    hanjiWord = hanji;
                }

                final String tailoInputWithNumberTone = LomajiConverter.convertLomajiWordToNumberTone(tailoWord, LomajiConverter.LOMAJI_TYPE_TAILO);

                final String pojInputWithNumberTone = LomajiConverter.convertTailoInputWordToPojInputWord(tailoInputWithNumberTone);
                final String pojNumberWord = LomajiConverter.convertTailoNumberWordToPojNumberWord(tailoInputWithNumberTone);
                final String pojWord = PojInputConverter.convertPojNumberToPoj(pojNumberWord);

                int thisLomajiWordToneNumber = LomajiConverter.getLomajiWordToneNumber(tailoInputWithNumberTone);

                priority += thisLomajiWordToneNumber * (int) (Math.pow(10, i));
                if (priority < 0) {
                    priority = Long.MAX_VALUE;
                }

                if (isStorePerWord) {
                    final String tailoInputWithoutTone = LomajiConverter.removeToneNumberAndHyphens(tailoInputWithNumberTone.toLowerCase());
                    final String pojInputWithoutTone = LomajiConverter.removeToneNumberAndHyphens(pojInputWithNumberTone.toLowerCase());

                    int thisPriority = LomajiConverter.getLomajiWordToneNumber(tailoInputWithNumberTone);

                    prepareImeDict(taigiWord, tailoWord, tailoInputWithNumberTone.toLowerCase(), tailoInputWithoutTone, "", pojWord, pojInputWithNumberTone.toLowerCase(), pojInputWithoutTone, "", hanjiWord, thisPriority, 1);
                }

                tailoNumberWordsStringBuilder.append(tailoInputWithNumberTone);
                pojNumberWordsStringBuilder.append(pojInputWithNumberTone);

                pojPhraseStringBuilder.append(pojWord);
                if (i < length - 1) {
                    pojPhraseStringBuilder.append(lomajiPhraseSplitterResult.getSplitSperators().get(i));
                }

                final String tailoShortInput = getTailoShortInput(tailoInputWithNumberTone);
                tailoShortInputStringBuilder.append(tailoShortInput);

                final String pojShortInput = getPojShortInput(pojInputWithNumberTone);
                pojShortInputStringBuilder.append(pojShortInput);

//                    Log.d(TAG, "tailoInputWithNumberTone=" + tailoInputWithNumberTone + ", tailoShortInput=" + tailoShortInput + ", pojInputWithNumberTone=" + pojInputWithNumberTone + ", pojShortInput=" + pojShortInput);
            }
        } else {
            // single word case
            wordLength = 1;

            final String tailoNumberWord = LomajiConverter.convertLomajiWordToNumberTone(tailoPhrase, LomajiConverter.LOMAJI_TYPE_TAILO);

            final String pojInputWord = LomajiConverter.convertTailoInputWordToPojInputWord(tailoNumberWord);
            final String pojNumberWord = LomajiConverter.convertTailoNumberWordToPojNumberWord(tailoNumberWord);
            final String pojWord = PojInputConverter.convertPojNumberToPoj(pojNumberWord);

            priority = LomajiConverter.getLomajiWordToneNumber(tailoNumberWord);

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

        prepareImeDict(taigiWord, tailoPhrase, tailoInputWithNumberTone, tailoInputWithoutTone, tailoShortInputStringBuilder.toString().toLowerCase(), pojPhrase, pojInputWithNumberTone, pojInputWithoutTone, pojShortInputStringBuilder.toString().toLowerCase(), taigiWord.getHanji(), priority, wordLength);
    }

    private String getTailoShortInput(String tailoWord) {
        final Matcher matcher = sTailoShortInputExtractPattern.matcher(tailoWord);
        if (matcher.find()) {
            return matcher.group(0);
        }

        return "";
    }

    private String getPojShortInput(String pojWord) {
        final Matcher matcher = sPojShotInputExtractPattern.matcher(pojWord);
        if (matcher.find()) {
            return matcher.group(0);
        }

        return "";
    }

    private void prepareImeDict(TlTaigiWord taigiWord, String tailoPhrase, String tailoInputWithNumberTone, String tailoInputWithoutTone, String tailoShortInput, String pojPhrase, String pojInputWithNumberTone, String pojInputWithoutTone, String pojShortInput, String hanji, long priority, int wordLength) {
        ImeDict newImeDict = new ImeDict();

        newImeDict.setMainCode(taigiWord.getMainCode());
        newImeDict.setWordPropertyCode(taigiWord.getWordPropertyCode());
        newImeDict.setTailo(tailoPhrase);
        newImeDict.setTailoInputWithNumberTone(tailoInputWithNumberTone);
        newImeDict.setTailoInputWithoutTone(tailoInputWithoutTone);
        newImeDict.setTailoShortInput(tailoShortInput);
        newImeDict.setPoj(pojPhrase);
        newImeDict.setPojInputWithNumberTone(pojInputWithNumberTone);
        newImeDict.setPojInputWithoutTone(pojInputWithoutTone);
        newImeDict.setPojShortInput(pojShortInput);
        newImeDict.setHanji(hanji);
        newImeDict.setPriority(priority);
        newImeDict.setWordLength(wordLength);

        for (ImeDict imeDict : mImeDictArrayList) {
            if (newImeDict.getTailo().equals(imeDict.getTailo()) && newImeDict.getHanji().equals(imeDict.getHanji())) {
                return;
            }
        }

        for (TlTaigiMadeWordCode madeWordCode : mTaigiMadeWordCodes) {
            if (newImeDict.getHanji().equals(madeWordCode.getMadeWordCode())) {
                Log.e(TAG, "MadeWord found???: lomaji = " + taigiWord.getLomaji() + ", hanji = " + taigiWord.getHanji());
                return;
            }
        }

        mImeDictArrayList.add(newImeDict);
    }

    private void storeImeDicts() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(TAG, "start: store mImeDictArrayList");

                int count = mImeDictArrayList.size();
                for (int i = 0; i < count; i++) {
                    ImeDict imeDict = mImeDictArrayList.get(i);

//                    Log.d(TAG, "tailoWord=" + imeDict.getTailo() + ", tailoNumberTone=" + imeDict.getTailoInputWithNumberTone() + ", tailoWithoutTone=" + imeDict.getTailoInputWithoutTone() + ", hanjiWord=" + imeDict.getHanji() + ", poj=" + imeDict.getPoj() + ", getPojInputWithNumberTone=" + imeDict.getPojInputWithNumberTone() + ", getPojInputWithoutTone=" + imeDict.getPojInputWithoutTone());
//                    Log.d(TAG, "tailoWord=" + imeDict.getTailo() + ", tailoShortInput=" + imeDict.getTailoShortInput() + ", pojWord=" + imeDict.getPoj() + ", pojShortInput=" + imeDict.getPojShortInput());

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