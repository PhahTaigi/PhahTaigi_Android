package com.taccotap.taigidictparser.custom;

import com.taccotap.taigidictmodel.tailo.TlTaigiWord;

import java.util.ArrayList;

public class CustomTaigiWords {

    public static ArrayList<TlTaigiWord> sTaigiWordArrayList = new ArrayList<>();

    public static final int CUSTOM_WORD_MAIN_CODE_START_INDEX = 1000000;

    static {
        TlTaigiWord taigiWord0 = new TlTaigiWord();
        taigiWord0.setMainCode(CUSTOM_WORD_MAIN_CODE_START_INDEX);
        taigiWord0.setWordPropertyCode(1);
        taigiWord0.setLomaji("Tâi-uân");
        taigiWord0.setHanji("臺灣");
        sTaigiWordArrayList.add(taigiWord0);

        TlTaigiWord taigiWord1 = new TlTaigiWord();
        taigiWord1.setMainCode(CUSTOM_WORD_MAIN_CODE_START_INDEX + 1);
        taigiWord1.setWordPropertyCode(1);
        taigiWord1.setLomaji("Tâi-gí");
        taigiWord1.setHanji("臺語");
        sTaigiWordArrayList.add(taigiWord1);

        TlTaigiWord taigiWord2 = new TlTaigiWord();
        taigiWord2.setMainCode(CUSTOM_WORD_MAIN_CODE_START_INDEX + 2);
        taigiWord2.setWordPropertyCode(1);
        taigiWord2.setLomaji("Pe̍h-uē-jī");
        taigiWord2.setHanji("白話字");
        sTaigiWordArrayList.add(taigiWord2);

        TlTaigiWord taigiWord3 = new TlTaigiWord();
        taigiWord3.setMainCode(CUSTOM_WORD_MAIN_CODE_START_INDEX + 3);
        taigiWord3.setWordPropertyCode(1);
        taigiWord3.setLomaji("POJ");
        taigiWord3.setHanji("白話字");
        sTaigiWordArrayList.add(taigiWord3);

        TlTaigiWord taigiWord4 = new TlTaigiWord();
        taigiWord4.setMainCode(CUSTOM_WORD_MAIN_CODE_START_INDEX + 4);
        taigiWord4.setWordPropertyCode(1);
        taigiWord4.setLomaji("LMJ");
        taigiWord4.setHanji("羅馬字");
        sTaigiWordArrayList.add(taigiWord4);

        TlTaigiWord taigiWord5 = new TlTaigiWord();
        taigiWord5.setMainCode(CUSTOM_WORD_MAIN_CODE_START_INDEX + 5);
        taigiWord5.setWordPropertyCode(1);
        taigiWord5.setLomaji("lô-má-jī");
        taigiWord5.setHanji("羅馬字");
        sTaigiWordArrayList.add(taigiWord5);

        TlTaigiWord taigiWord6 = new TlTaigiWord();
        taigiWord6.setMainCode(CUSTOM_WORD_MAIN_CODE_START_INDEX + 6);
        taigiWord6.setWordPropertyCode(1);
        taigiWord6.setLomaji("su-ji̍p-huat");
        taigiWord6.setHanji("輸入法");
        sTaigiWordArrayList.add(taigiWord6);

        TlTaigiWord taigiWord7 = new TlTaigiWord();
        taigiWord7.setMainCode(CUSTOM_WORD_MAIN_CODE_START_INDEX + 7);
        taigiWord7.setWordPropertyCode(1);
        taigiWord7.setLomaji("Tâi-lô");
        taigiWord7.setHanji("臺羅");
        sTaigiWordArrayList.add(taigiWord7);
    }
}
