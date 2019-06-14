package com.taccotap.taigidictparser.custom

import com.taccotap.taigidictmodel.kip.KipTaigiSu
import java.util.*

object CustomKipTaigiSu {

    var sTaigiSuArrayList = ArrayList<KipTaigiSu>()

    val CUSTOM_WORD_MAIN_CODE_START_INDEX = 1000000

    init {
        val taigiWord0 = KipTaigiSu()
        taigiWord0.mainCode = CUSTOM_WORD_MAIN_CODE_START_INDEX
        taigiWord0.wordPropertyCode = 1
        taigiWord0.lomaji = "Tâi-uân"
        taigiWord0.hanji = "台灣"
        sTaigiSuArrayList.add(taigiWord0)

        val taigiWord1 = KipTaigiSu()
        taigiWord1.mainCode = CUSTOM_WORD_MAIN_CODE_START_INDEX + 1
        taigiWord1.wordPropertyCode = 1
        taigiWord1.lomaji = "Tâi-uân-lâng"
        taigiWord1.hanji = "台灣人"
        sTaigiSuArrayList.add(taigiWord1)

        val taigiWord2 = KipTaigiSu()
        taigiWord2.mainCode = CUSTOM_WORD_MAIN_CODE_START_INDEX + 2
        taigiWord2.wordPropertyCode = 1
        taigiWord2.lomaji = "Tâi-gí"
        taigiWord2.hanji = "台語"
        sTaigiSuArrayList.add(taigiWord2)

        val taigiWord3 = KipTaigiSu()
        taigiWord3.mainCode = CUSTOM_WORD_MAIN_CODE_START_INDEX + 3
        taigiWord3.wordPropertyCode = 1
        taigiWord3.lomaji = "Tâi-bûn"
        taigiWord3.hanji = "台文"
        sTaigiSuArrayList.add(taigiWord3)

        val taigiWord4 = KipTaigiSu()
        taigiWord4.mainCode = CUSTOM_WORD_MAIN_CODE_START_INDEX + 4
        taigiWord4.wordPropertyCode = 1
        taigiWord4.lomaji = "Tâi-gí-bûn"
        taigiWord4.hanji = "台語文"
        sTaigiSuArrayList.add(taigiWord4)

        val taigiWord5 = KipTaigiSu()
        taigiWord5.mainCode = CUSTOM_WORD_MAIN_CODE_START_INDEX + 5
        taigiWord5.wordPropertyCode = 1
        taigiWord5.lomaji = "POJ"
        taigiWord5.hanji = "白話字"
        sTaigiSuArrayList.add(taigiWord5)

        val taigiWord6 = KipTaigiSu()
        taigiWord6.mainCode = CUSTOM_WORD_MAIN_CODE_START_INDEX + 6
        taigiWord6.wordPropertyCode = 1
        taigiWord6.lomaji = "LMJ"
        taigiWord6.hanji = "羅馬字"
        sTaigiSuArrayList.add(taigiWord6)

        val taigiWord7 = KipTaigiSu()
        taigiWord7.mainCode = CUSTOM_WORD_MAIN_CODE_START_INDEX + 7
        taigiWord7.wordPropertyCode = 1
        taigiWord7.lomaji = "su-ji̍p-huat"
        taigiWord7.hanji = "輸入法"
        sTaigiSuArrayList.add(taigiWord7)

        val taigiWord8 = KipTaigiSu()
        taigiWord8.mainCode = CUSTOM_WORD_MAIN_CODE_START_INDEX + 8
        taigiWord8.wordPropertyCode = 1
        taigiWord8.lomaji = "Hàn-Lô"
        taigiWord8.hanji = "漢羅"
        sTaigiSuArrayList.add(taigiWord8)
    }
}
