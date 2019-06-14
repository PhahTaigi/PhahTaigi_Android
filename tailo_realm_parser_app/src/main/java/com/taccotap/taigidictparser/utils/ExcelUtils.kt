package com.taccotap.taigidictparser.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import java.io.IOException
import java.io.InputStream

object ExcelUtils {
    private val TAG = ExcelUtils::class.java.simpleName

    fun readExcelWorkbookFromAssetsFile(context: Context, assetsExcelFilePath: String): HSSFWorkbook? {
        // Creating Input Stream
        var inputStream: InputStream?
        try {
            inputStream = context.assets.open(assetsExcelFilePath)

            // Create a POIFSFileSystem object
            val myFileSystem = POIFSFileSystem(inputStream)

            // Create a workbook using the File System
            return HSSFWorkbook(myFileSystem)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    fun readExcelFromAssetsFile(context: Context, assetsExcelFilePath: String) {
        try {
            // Create a workbook using the File System
            val myWorkBook = readExcelWorkbookFromAssetsFile(context, assetsExcelFilePath)

            // Get the first sheet from workbook
            val mySheet = myWorkBook!!.getSheetAt(0)

            /** We now need something to iterate through the cells. */
            val rowIter = mySheet.rowIterator()

            while (rowIter.hasNext()) {
                val myRow = rowIter.next() as HSSFRow
                val cellIter = myRow.cellIterator()
                while (cellIter.hasNext()) {
                    val myCell = cellIter.next() as HSSFCell
                    Log.d(TAG, "Cell Value: $myCell")
                    Toast.makeText(context, "cell Value: $myCell", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
