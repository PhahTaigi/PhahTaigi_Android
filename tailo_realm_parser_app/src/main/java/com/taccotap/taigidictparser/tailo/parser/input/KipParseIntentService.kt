package com.taccotap.taigidictparser.tailo.parser.input

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.taccotap.phahtaigi.dictmodel.ImeDict
import com.taccotap.taigidictmodel.kip.KipTaigiSu
import com.taccotap.taigidictmodel.kip.KipTaigiSuKithannKonghoat
import com.taccotap.taigidictparser.custom.CustomKipTaigiSu
import com.taccotap.taigidictparser.tailo.parser.LomajiConverter
import com.taccotap.taigidictparser.utils.ExcelUtils
import io.realm.Realm
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import java.util.*

class KipParseIntentService : IntentService("KipParseIntentService") {
    private val TAG = KipParseIntentService::class.java.simpleName

    private val ASSETS_PATH_KIP_DICT_TAIGI_WORDS = "kip/wordsMain.xls"
    private val ASSETS_PATH_KIP_DICT_TAIGI_WORDS_ANOTHER_PRONOUNCE = "kip/wordsAnother.xls"

    private var mImeDictArrayList = ArrayList<ImeDict>()

    companion object {
        fun startParsing(context: Context) {
            val intent = Intent(context, KipParseIntentService::class.java)
            context.startService(intent)
        }
    }

    override fun onHandleIntent(p0: Intent?) {
        // worker thread
        parseDict()
    }

    private fun parseDict() {
        var realm = Realm.getDefaultInstance()
        mImeDictArrayList.clear()

        readDictTaigiSu(realm)
        readDictTaigiSuKithannKonghoat(realm)
        readCustomTaigiSu(realm)

        parseMainTaigiSu(realm)
        parseKipTaigiSuKithannKonghoat(realm)

        sortAndGeneratePriorities()

        realm.executeTransaction {
            realm.delete(ImeDict::class.java)
        }

        storeImeDicts(realm)

        realm.executeTransaction {
            realm.delete(KipTaigiSu::class.java)
            realm.delete(KipTaigiSuKithannKonghoat::class.java)
        }

        realm.close()

        Log.d(TAG, "finish ALL")
    }

    private fun readDictTaigiSu(realm: Realm) {
        Log.d(TAG, "start: readDictTaigiSu()")

        val workbook = ExcelUtils.readExcelWorkbookFromAssetsFile(this, ASSETS_PATH_KIP_DICT_TAIGI_WORDS)
        if (workbook != null) {
            val firstSheet = workbook.getSheetAt(0)

            val rowIterator = firstSheet.rowIterator()
            val kipTaigiSuArrayList = ArrayList<KipTaigiSu>()

            var rowNum = 1
            while (rowIterator.hasNext()) {
                val currentRow = rowIterator.next() as HSSFRow
                if (rowNum == 1) {
                    rowNum++
                    continue
                }

                val cellIterator = currentRow.cellIterator()

                val currentKipTaigiSu = KipTaigiSu()

                var colNum = 1
                while (cellIterator.hasNext()) {
                    val currentCell = cellIterator.next() as HSSFCell

                    if (colNum == 1) {
                        val stringCellValue = currentCell.stringCellValue
                        currentKipTaigiSu.mainCode = Integer.valueOf(stringCellValue)
                    } else if (colNum == 2) {
                        val stringCellValue = currentCell.stringCellValue
                        currentKipTaigiSu.wordPropertyCode = Integer.valueOf(stringCellValue)
                    } else if (colNum == 3) {
                        currentKipTaigiSu.hanji = currentCell.stringCellValue
                    } else if (colNum == 4) {
                        currentKipTaigiSu.lomaji = currentCell.stringCellValue
                    } else {
                        break
                    }

                    colNum++
                }

                if (!TextUtils.isEmpty(currentKipTaigiSu.lomaji) && !TextUtils.isEmpty(currentKipTaigiSu.hanji)) {
                    kipTaigiSuArrayList.add(currentKipTaigiSu)
                }

                rowNum++
            }

            realm.executeTransaction {
                for (kipTaigiSu in kipTaigiSuArrayList) {
                    it.copyToRealmOrUpdate(kipTaigiSu)
                }
            }
        }

        Log.d(TAG, "finish: readDictTaigiSu()")
    }

    private fun readDictTaigiSuKithannKonghoat(realm: Realm) {
        Log.d(TAG, "start: readDictTaigiSuKithannKonghoat()")

        val workbook = ExcelUtils.readExcelWorkbookFromAssetsFile(this, ASSETS_PATH_KIP_DICT_TAIGI_WORDS_ANOTHER_PRONOUNCE)
        if (workbook != null) {
            val firstSheet = workbook.getSheetAt(0)

            val rowIterator = firstSheet.rowIterator()
            val kipTaigiSuKithannKonghoatArrayList = ArrayList<KipTaigiSuKithannKonghoat>()

            var rowNum = 1
            while (rowIterator.hasNext()) {
                val currentRow = rowIterator.next() as HSSFRow
                if (rowNum == 1) {
                    rowNum++
                    continue
                }

                val cellIterator = currentRow.cellIterator()

                val currentKipTaigiSuKithannKonghoat = KipTaigiSuKithannKonghoat()

                var isSiokliamim = false
                var colNum = 1
                while (cellIterator.hasNext()) {
                    val currentCell = cellIterator.next() as HSSFCell

                    if (colNum == 1) {
                        val stringCellValue = currentCell.stringCellValue
                        currentKipTaigiSuKithannKonghoat.index = Integer.valueOf(stringCellValue)
                    } else if (colNum == 2) {
                        val stringCellValue = currentCell.stringCellValue
                        currentKipTaigiSuKithannKonghoat.mainCode = Integer.valueOf(stringCellValue)
                    } else if (colNum == 3) {
                        currentKipTaigiSuKithannKonghoat.lomaji = currentCell.stringCellValue
                    } else if (colNum == 4) {
                        val stringCellValue = currentCell.stringCellValue
                        val type = Integer.valueOf(stringCellValue)
                        if (type == 2) {
                            isSiokliamim = true
                            break
                        }
                    } else {
                        break
                    }

                    colNum++
                }

                rowNum++

                if (isSiokliamim) {
                    continue
                }

                kipTaigiSuKithannKonghoatArrayList.add(currentKipTaigiSuKithannKonghoat)
            }

            realm.executeTransaction {
                for (kipTaigiSuKithannKonghoat in kipTaigiSuKithannKonghoatArrayList) {
                    it.copyToRealmOrUpdate(kipTaigiSuKithannKonghoat)
                }
            }

            Log.d(TAG, "finish: readDictTaigiSuKithannKonghoat()")
        }
    }

    private fun readCustomTaigiSu(realm: Realm) {
        Log.d(TAG, "start: readCustomTaigiSu()")

        realm.executeTransaction {
            for (taigiSu in CustomKipTaigiSu.sTaigiSuArrayList) {
                it.copyToRealmOrUpdate(taigiSu)
            }
        }

        Log.d(TAG, "finish: readCustomTaigiSu()")
    }

    private fun parseMainTaigiSu(realm: Realm) {
        Log.d(TAG, "start: parseMainTaigiSu()")

        val kipTaigiSuResults = realm.where(KipTaigiSu::class.java).findAll()

        val kipTaigiSuArrayList = ArrayList<KipTaigiSu>(realm.copyFromRealm(kipTaigiSuResults))
        parseEachKipTaigiSu(kipTaigiSuArrayList)

        Log.d(TAG, "finish: parseMainTaigiSu()")
    }

    private fun parseKipTaigiSuKithannKonghoat(realm: Realm) {
        Log.d(TAG, "start: parseKipTaigiSuKithannKonghoat()")

        val kipTaigiSuResults = realm.where(KipTaigiSu::class.java).findAll()
        val kipTaigiSuList = realm.copyFromRealm(kipTaigiSuResults)

        val kipTaigiSuMainCodeMap = HashMap<Int, KipTaigiSu>()
        for (kipTaigiSu in kipTaigiSuList) {
            kipTaigiSuMainCodeMap[kipTaigiSu.mainCode] = kipTaigiSu
        }

        val kipTaigiSuKithannKonghoatResults = realm.where(KipTaigiSuKithannKonghoat::class.java).findAll()

        val kipTaigiSuArrayList = ArrayList<KipTaigiSu>()

        for (kipTaigiSuKithannKonghoat in kipTaigiSuKithannKonghoatResults) {
            val kipTaigiSu = kipTaigiSuMainCodeMap[kipTaigiSuKithannKonghoat.mainCode]
            if (kipTaigiSu != null) {
                kipTaigiSu.lomaji = kipTaigiSuKithannKonghoat.lomaji

                kipTaigiSuArrayList.add(kipTaigiSu)
            }
        }

        parseEachKipTaigiSu(kipTaigiSuArrayList)

        Log.d(TAG, "finish: parseKipTaigiSuKithannKonghoat()")
    }

    private fun parseEachKipTaigiSu(kipTaigiSuArrayList: ArrayList<KipTaigiSu>) {
        for (kipTaigiSu in kipTaigiSuArrayList) {
            if (kipTaigiSu.wordPropertyCode == 12 || kipTaigiSu.wordPropertyCode == 25) {
                continue
            }
            if (kipTaigiSu.lomaji.isEmpty()) {
//            println("word no lomaji: hanji=${srcEntry.hanjiTaibun}")
                continue
            }

            var koosuHanjiString = kipTaigiSu.hanji.trim()
            val lomajiString = kipTaigiSu.lomaji.trim()

            val koojiHanjiArrayList: ArrayList<String> = SplitStringByCodePoint.split(koosuHanjiString)

            val koosuLomajiArrayList = ArrayList(lomajiString.split("(/|、)".toRegex()))
            var hanjiimMap = HashMap<String, MutableSet<String>>()   // HashMap<Hanji,HanjiimArray<Lomaji>>
            for (koosuLomaji in koosuLomajiArrayList) {
                // fix lomaji
                var fixKoosuLomaji = if (koosuLomaji.trim().startsWith("--")) {
                    koosuLomaji.trim().substring(2)
                } else {
                    koosuLomaji.trim()
                }
                val koojiLomajiArrayList = ArrayList(fixKoosuLomaji.split("(--|-|‑| )".toRegex()))

                // fix hanji
                var fixKoosuHanji = if (koojiHanjiArrayList.size > 1) {
                    koosuHanjiString.replace("臺", "台")
                } else {
                    koosuHanjiString
                }

                // check word count
                var wordCountMatch = true
                if (koojiHanjiArrayList.size != koojiLomajiArrayList.size) {
                    Log.d(TAG, "word count not match: hanji=${kipTaigiSu.hanji} (${koojiHanjiArrayList.size}), lmj=${kipTaigiSu.lomaji} (${koojiLomajiArrayList.size})")
//                    for (koojiLomaji in koojiLomajiArrayList) {
//                        Log.d(TAG, "word count not match (list): $koojiLomaji")
//                    }
                    wordCountMatch = false

                    // fix special case (delete directly)
                    if (fixKoosuHanji.contains("\\(.*\\)".toRegex())) {
                        fixKoosuHanji = fixKoosuHanji.replace("\\(.*\\)".toRegex(), "")
                        Log.d(TAG, "fixed hanjiString = $fixKoosuHanji")
                    } else if (fixKoosuHanji.contains("、")) {
                        val indexOf = fixKoosuHanji.indexOf("、")
                        fixKoosuHanji = fixKoosuHanji.substring(0, indexOf)
                        Log.d(TAG, "fixed hanjiString = $fixKoosuHanji")
                    }
                }

                // parse ko͘-jī
                val stringBuilderKiplmjInput = StringBuilder()
                val stringBuilderPojInput = StringBuilder()
                val stringBuilderKiplmjShortInput = StringBuilder()
                val stringBuilderPojShortInput = StringBuilder()


                val size = koojiLomajiArrayList.size
                for (i in 0 until size) {
                    val koojiLomaji: String = koojiLomajiArrayList[i].replace(".", "")

                    if (koojiLomaji.isEmpty()) {
                        continue
                    }

                    val kiplmjKoojiFullInput = LomajiConverter.convertLomajiUnicodeString(koojiLomaji, LomajiConverter.ConvertLomajiUnicodeStringCase.CASE_KIPLMJ_UNICODE_TO_KIPLMJ_INPUT).toLowerCase()
                    val pojKoojiFullInput = LomajiConverter.convertLomajiInputString(kiplmjKoojiFullInput, LomajiConverter.ConvertLomajiInputStringCase.CASE_KIPLMJ_INPUT_TO_POJ_INPUT).toLowerCase()

                    // generate row input
                    stringBuilderKiplmjInput.append(kiplmjKoojiFullInput)
                    stringBuilderPojInput.append(pojKoojiFullInput)

                    // short input
                    if (size >= 2) {
                        // kiplmj
                        val matchResult1 = "(ph|p|m|b|th|tsh|ts|t|n|l|kh|k|ng|g|h|s|j)".toRegex().find(kiplmjKoojiFullInput, 0)
                        if (matchResult1 != null && matchResult1.range.first == 0) {
                            stringBuilderKiplmjShortInput.append(matchResult1.value)
                        } else {
                            val matchResult2 = "(a|i|u|oo|o|e|ng|g|m)".toRegex().find(kiplmjKoojiFullInput, 0)
                            if (matchResult2 != null && matchResult2.range.first == 0) {
                                stringBuilderKiplmjShortInput.append(matchResult2.value)
                            }
                        }

                        // poj
                        val matchResult3 = "(ph|p|m|b|th|chh|ch|t|n|l|kh|k|ng|g|h|s|j)".toRegex().find(pojKoojiFullInput, 0)
                        if (matchResult3 != null && matchResult3.range.first == 0) {
                            stringBuilderPojShortInput.append(matchResult3.value)
                        } else {
                            val matchResult4 = "(a|i|u|o|e|ng|g|m)".toRegex().find(pojKoojiFullInput, 0)
                            if (matchResult4 != null && matchResult4.range.first == 0) {
                                stringBuilderPojShortInput.append(matchResult4.value)
                            }
                        }
                    }

                    if (wordCountMatch) {
                        // hanjiim
                        val koojiHanji: String = koojiHanjiArrayList[i]

                        var hanjiimSet: MutableSet<String>? = hanjiimMap[koojiHanji]
                        if (hanjiimSet == null) {
                            var newHanjiimSet = mutableSetOf<String>()
                            newHanjiimSet.add(koojiLomaji)
                            hanjiimMap.put(koojiHanji, newHanjiimSet)
                        } else {
                            if (!hanjiimSet.contains(koojiLomaji)) {
                                hanjiimSet.add(koojiLomaji)
                            }
                        }
                    }
                }

                // parse ko͘-sû
                addKipTaigiImeDict(fixKoosuLomaji, stringBuilderKiplmjInput.toString(), stringBuilderPojInput.toString(), stringBuilderKiplmjShortInput.toString(), stringBuilderPojShortInput.toString(), fixKoosuHanji)
            }

            // process kooji hanji/lomaji pairs
            for (hanji in hanjiimMap.keys) {
                var hanjiimSet = hanjiimMap[hanji]
                var lomajiArrayList = hanjiimSet!!.toCollection(ArrayList())

                for (lomaji in lomajiArrayList) {
                    // check exist
                    var alreadyHasTaigiji = false
                    for (imeDict in mImeDictArrayList) {
                        if (imeDict.kiplmj == lomaji && imeDict.hanji == hanji) {
                            Log.d(TAG, "lomaji=$lomaji, hanji=$hanji")
                            alreadyHasTaigiji = true
                            break
                        }
                    }

                    if (alreadyHasTaigiji) {
                        continue
                    } else {
                        val kiplmj = lomaji
                        val kiplmjInput = LomajiConverter.convertLomajiUnicodeString(kiplmj, LomajiConverter.ConvertLomajiUnicodeStringCase.CASE_KIPLMJ_UNICODE_TO_KIPLMJ_INPUT).toLowerCase()
                        val pojInput = LomajiConverter.convertLomajiInputString(kiplmj, LomajiConverter.ConvertLomajiInputStringCase.CASE_KIPLMJ_INPUT_TO_POJ_INPUT).toLowerCase()
                        addKipTaigiImeDict(lomaji, kiplmjInput, pojInput, hanji)
                    }
                }
            }
        }
    }

    private fun addKipTaigiImeDict(kiplmj: String, kiplmjInputWithNumberTone: String, pojInputWithNumberTone: String, hanji: String) {
        addKipTaigiImeDict(kiplmj, kiplmjInputWithNumberTone, pojInputWithNumberTone, "", "", hanji)
    }

    private fun addKipTaigiImeDict(kiplmj: String, kiplmjInputWithNumberTone: String, pojInputWithNumberTone: String, kiplmjShortInput: String, pojShortInput: String, hanji: String) {
//        Log.d(TAG, "addKipTaigiImeDict(): kiplmj=$kiplmj, hanji=$hanji")

        val imeDict = ImeDict()
        imeDict.hanji = hanji

        imeDict.kiplmj = kiplmj
        imeDict.kiplmjInputWithNumberTone = kiplmjInputWithNumberTone
        imeDict.kiplmjInputWithoutTone = imeDict.kiplmjInputWithNumberTone.replace("[0-9]".toRegex(), "")
        imeDict.kiplmjShortInput = kiplmjShortInput

        imeDict.pojInputWithNumberTone = pojInputWithNumberTone
        imeDict.pojInputWithoutTone = imeDict.pojInputWithNumberTone.replace("[0-9]".toRegex(), "")
        imeDict.pojShortInput = pojShortInput

        val kiplmjFullInput = LomajiConverter.convertLomajiUnicodeString(kiplmj, LomajiConverter.ConvertLomajiUnicodeStringCase.CASE_KIPLMJ_UNICODE_TO_KIPLMJ_INPUT)
        imeDict.poj = LomajiConverter.convertLomajiInputString(kiplmjFullInput, LomajiConverter.ConvertLomajiInputStringCase.CASE_KIPLMJ_INPUT_TO_POJ_UNICODE)

        mImeDictArrayList.add(imeDict)
    }

    private fun sortAndGeneratePriorities() {
        // kiplmj
        mImeDictArrayList.sortBy { it.kiplmjInputWithNumberTone.toLowerCase() }

        var kiplmjPriority = 0
        for (imeDict in mImeDictArrayList) {
            imeDict.kiplmjPriority = kiplmjPriority
            kiplmjPriority++
        }

        // poj
        mImeDictArrayList.sortBy { it.pojInputWithNumberTone.toLowerCase() }

        var pojPriority = 0
        for (imeDict in mImeDictArrayList) {
            imeDict.pojPriority = pojPriority
            pojPriority++
        }
    }


    private fun storeImeDicts(realm: Realm) {
        realm.executeTransaction {
            Log.d(TAG, "start: store mImeDictArrayList")

            val count = mImeDictArrayList.size
            Log.d(TAG, "count: ${mImeDictArrayList.size}")

            for (i in 0 until count) {
                val imeDict = mImeDictArrayList[i]

//                Log.d(TAG, "kiplmj=" + imeDict.kiplmj + ", kiplmjInputWithNumberTone=" + imeDict.kiplmjInputWithNumberTone + ", kiplmjInputWithoutTone=" + imeDict.kiplmjInputWithoutTone + ", hanji=" + imeDict.hanji + ", poj=" + imeDict.poj + ", pojInputWithNumberTone=" + imeDict.pojInputWithNumberTone + ", pojInputWithoutTone=" + imeDict.pojInputWithoutTone);
//                Log.d(TAG, "kiplmj=" + imeDict.kiplmj + ", kiplmjShortInput=" + imeDict.kiplmjShortInput + ", poj=" + imeDict.poj + ", pojShortInput=" + imeDict.pojShortInput)

                imeDict.wordId = i

                try {
                    it.copyToRealmOrUpdate(imeDict)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }

            }

            Log.d(TAG, "finish: store mImeDictArrayList")
        }
    }
}