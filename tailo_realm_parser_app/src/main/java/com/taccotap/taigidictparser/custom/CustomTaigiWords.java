package com.taccotap.taigidictparser.custom;

import com.taccotap.taigidictmodel.tailo.TlTaigiWord;

import java.util.ArrayList;

public class CustomTaigiWords {

    public static ArrayList<TlTaigiWord> sTaigiWordArrayList = new ArrayList<>();

    static {
        TlTaigiWord taigiWord0 = new TlTaigiWord();
        taigiWord0.setMainCode(-1);
        taigiWord0.setWordPropertyCode(1);
        taigiWord0.setLomaji("Tâi-uân");
        taigiWord0.setHanji("臺灣");
        sTaigiWordArrayList.add(taigiWord0);

        TlTaigiWord taigiWord1 = new TlTaigiWord();
        taigiWord1.setMainCode(-1);
        taigiWord1.setWordPropertyCode(1);
        taigiWord1.setLomaji("Tâi-gí");
        taigiWord1.setHanji("臺語");
        sTaigiWordArrayList.add(taigiWord1);

        TlTaigiWord taigiWord2 = new TlTaigiWord();
        taigiWord2.setMainCode(-1);
        taigiWord2.setWordPropertyCode(1);
        taigiWord2.setLomaji("Pe̍h-uē-jī");
        taigiWord2.setHanji("白話字");
        sTaigiWordArrayList.add(taigiWord1);
    }
}
