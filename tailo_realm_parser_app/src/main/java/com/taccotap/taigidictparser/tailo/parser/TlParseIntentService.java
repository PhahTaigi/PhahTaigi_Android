package com.taccotap.taigidictparser.tailo.parser;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.taccotap.taigidictmodel.tailo.TlTaigiWord;
import com.taccotap.taigidictparser.utils.ExcelUtils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.ArrayList;
import java.util.Iterator;

import io.realm.Realm;

public class TlParseIntentService extends IntentService {
    private static final String TAG = TlParseIntentService.class.getSimpleName();

    private static final String ASSETS_PATH_TAILO_DICT_TAIGI_WORDS = "tailo/詞目總檔(含俗諺).xls";

    private Realm mRealm;

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
}
