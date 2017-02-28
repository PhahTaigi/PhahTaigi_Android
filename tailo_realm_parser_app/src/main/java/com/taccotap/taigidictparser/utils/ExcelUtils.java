package com.taccotap.taigidictparser.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ExcelUtils {
    private static final String TAG = ExcelUtils.class.getSimpleName();

    public static HSSFWorkbook readExcelWorkbookFromAssetsFile(Context context, String assetsExcelFilePath) {
        // Creating Input Stream
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(assetsExcelFilePath);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(inputStream);

            // Create a workbook using the File System
            return new HSSFWorkbook(myFileSystem);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void readExcelFromAssetsFile(Context context, String assetsExcelFilePath) {
        try {
            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = readExcelWorkbookFromAssetsFile(context, assetsExcelFilePath);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator rowIter = mySheet.rowIterator();

            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();
                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.d(TAG, "Cell Value: " + myCell.toString());
                    Toast.makeText(context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
