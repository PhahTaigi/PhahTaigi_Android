package com.taccotap.phahtaigi.ime.converter;

import java.util.HashMap;
import java.util.Set;

public class Poj {

    // <poj_unicode, poj_number>
    public static HashMap<String, String> sPojUnicodeToPojNumberHashMap;

    // <poj_number, poj_unicode>
    public static HashMap<String, String> sPojNumberToPojUnicodeHashMap;

    static {
        sPojUnicodeToPojNumberHashMap = new HashMap<>();
        sPojNumberToPojUnicodeHashMap = new HashMap<>();

        // A
        sPojUnicodeToPojNumberHashMap.put("Á", "A2");
        sPojUnicodeToPojNumberHashMap.put("À", "A3");
        sPojUnicodeToPojNumberHashMap.put("Â", "A5");
        sPojUnicodeToPojNumberHashMap.put("Ā", "A7");
        sPojUnicodeToPojNumberHashMap.put("A̍", "A8");
        sPojUnicodeToPojNumberHashMap.put("Ă", "A9");

        // a
        sPojUnicodeToPojNumberHashMap.put("á", "a2");
        sPojUnicodeToPojNumberHashMap.put("à", "a3");
        sPojUnicodeToPojNumberHashMap.put("â", "a5");
        sPojUnicodeToPojNumberHashMap.put("ā", "a7");
        sPojUnicodeToPojNumberHashMap.put("a̍", "a8");
        sPojUnicodeToPojNumberHashMap.put("ă", "a9");

        // I
        sPojUnicodeToPojNumberHashMap.put("Í", "I2");
        sPojUnicodeToPojNumberHashMap.put("Ì", "I3");
        sPojUnicodeToPojNumberHashMap.put("Î", "I5");
        sPojUnicodeToPojNumberHashMap.put("Ī", "I7");
        sPojUnicodeToPojNumberHashMap.put("I̍", "I8");
        sPojUnicodeToPojNumberHashMap.put("Ĭ", "I9");

        // i
        sPojUnicodeToPojNumberHashMap.put("í", "i2");
        sPojUnicodeToPojNumberHashMap.put("ì", "i3");
        sPojUnicodeToPojNumberHashMap.put("î", "i5");
        sPojUnicodeToPojNumberHashMap.put("ī", "i7");
        sPojUnicodeToPojNumberHashMap.put("i̍", "i8");
        sPojUnicodeToPojNumberHashMap.put("ĭ", "i9");

        // U
        sPojUnicodeToPojNumberHashMap.put("Ú", "U2");
        sPojUnicodeToPojNumberHashMap.put("Ù", "U3");
        sPojUnicodeToPojNumberHashMap.put("Û", "U5");
        sPojUnicodeToPojNumberHashMap.put("Ū", "U7");
        sPojUnicodeToPojNumberHashMap.put("U̍", "U8");
        sPojUnicodeToPojNumberHashMap.put("Ŭ", "U9");

        // u
        sPojUnicodeToPojNumberHashMap.put("ú", "u2");
        sPojUnicodeToPojNumberHashMap.put("ù", "u3");
        sPojUnicodeToPojNumberHashMap.put("û", "u5");
        sPojUnicodeToPojNumberHashMap.put("ū", "u7");
        sPojUnicodeToPojNumberHashMap.put("u̍", "u8");
        sPojUnicodeToPojNumberHashMap.put("ŭ", "u9");

        // E
        sPojUnicodeToPojNumberHashMap.put("É", "E2");
        sPojUnicodeToPojNumberHashMap.put("È", "E3");
        sPojUnicodeToPojNumberHashMap.put("Ê", "E5");
        sPojUnicodeToPojNumberHashMap.put("Ē", "E7");
        sPojUnicodeToPojNumberHashMap.put("E̍", "E8");
        sPojUnicodeToPojNumberHashMap.put("Ĕ", "E9");

        // e
        sPojUnicodeToPojNumberHashMap.put("é", "e2");
        sPojUnicodeToPojNumberHashMap.put("è", "e3");
        sPojUnicodeToPojNumberHashMap.put("ê", "e5");
        sPojUnicodeToPojNumberHashMap.put("ē", "e7");
        sPojUnicodeToPojNumberHashMap.put("e̍", "e8");
        sPojUnicodeToPojNumberHashMap.put("ĕ", "e9");

        // O
        sPojUnicodeToPojNumberHashMap.put("Ó", "O2");
        sPojUnicodeToPojNumberHashMap.put("Ò", "O3");
        sPojUnicodeToPojNumberHashMap.put("Ô", "O5");
        sPojUnicodeToPojNumberHashMap.put("Ō", "O7");
        sPojUnicodeToPojNumberHashMap.put("O̍", "O8");
        sPojUnicodeToPojNumberHashMap.put("Ŏ", "O9");

        // o
        sPojUnicodeToPojNumberHashMap.put("ó", "o2");
        sPojUnicodeToPojNumberHashMap.put("ò", "o3");
        sPojUnicodeToPojNumberHashMap.put("ô", "o5");
        sPojUnicodeToPojNumberHashMap.put("ō", "o7");
        sPojUnicodeToPojNumberHashMap.put("o̍", "o8");
        sPojUnicodeToPojNumberHashMap.put("ŏ", "o9");

        // O͘
        sPojUnicodeToPojNumberHashMap.put("Ó\u0358", "O\u03582");
        sPojUnicodeToPojNumberHashMap.put("Ò\u0358", "O\u03583");
        sPojUnicodeToPojNumberHashMap.put("Ô\u0358", "O\u03585");
        sPojUnicodeToPojNumberHashMap.put("Ō\u0358", "O\u03587");
        sPojUnicodeToPojNumberHashMap.put("O̍\u0358", "O\u03588");
        sPojUnicodeToPojNumberHashMap.put("Ŏ\u0358", "O\u03589");

        // o͘
        sPojUnicodeToPojNumberHashMap.put("ó\u0358", "o\u03582");
        sPojUnicodeToPojNumberHashMap.put("ò\u0358", "o\u03583");
        sPojUnicodeToPojNumberHashMap.put("ô\u0358", "o\u03585");
        sPojUnicodeToPojNumberHashMap.put("ō\u0358", "o\u03587");
        sPojUnicodeToPojNumberHashMap.put("o̍\u0358", "o\u03588");
        sPojUnicodeToPojNumberHashMap.put("ŏ\u0358", "o\u03589");

        final Set<String> keys = sPojUnicodeToPojNumberHashMap.keySet();
        for (String key : keys) {
            String value = sPojUnicodeToPojNumberHashMap.get(key);
            sPojNumberToPojUnicodeHashMap.put(value, key);
        }
    }
}
