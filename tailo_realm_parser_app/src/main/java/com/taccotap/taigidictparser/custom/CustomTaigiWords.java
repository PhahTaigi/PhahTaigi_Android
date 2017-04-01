package com.taccotap.taigidictparser.custom;

import com.taccotap.taigidictmodel.tailo.TlTaigiWord;

import java.util.ArrayList;

public class CustomTaigiWords {

    public static ArrayList<TlTaigiWord> sTaigiWordArrayList = new ArrayList<>();

    static {
        TlTaigiWord taigiWord0 = new TlTaigiWord();
        taigiWord0.setMainCode(100000);
        taigiWord0.setWordPropertyCode(1);
        taigiWord0.setLomaji("Tâi-uân");
        taigiWord0.setHanji("臺灣");
        sTaigiWordArrayList.add(taigiWord0);

        TlTaigiWord taigiWord1 = new TlTaigiWord();
        taigiWord1.setMainCode(100001);
        taigiWord1.setWordPropertyCode(1);
        taigiWord1.setLomaji("Tâi-gí");
        taigiWord1.setHanji("臺語");
        sTaigiWordArrayList.add(taigiWord1);

        TlTaigiWord taigiWord2 = new TlTaigiWord();
        taigiWord2.setMainCode(100002);
        taigiWord2.setWordPropertyCode(1);
        taigiWord2.setLomaji("Pe̍h-uē-jī");
        taigiWord2.setHanji("白話字");
        sTaigiWordArrayList.add(taigiWord2);

        TlTaigiWord taigiWord3 = new TlTaigiWord();
        taigiWord3.setMainCode(100003);
        taigiWord3.setWordPropertyCode(1);
        taigiWord3.setLomaji("POJ");
        taigiWord3.setHanji("白話字");
        sTaigiWordArrayList.add(taigiWord3);

        TlTaigiWord taigiWord4 = new TlTaigiWord();
        taigiWord4.setMainCode(100004);
        taigiWord4.setWordPropertyCode(1);
        taigiWord4.setLomaji("LMJ");
        taigiWord4.setHanji("羅馬字");
        sTaigiWordArrayList.add(taigiWord4);
    }
}
