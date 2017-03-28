package com.taccotap.taigidictparser.tailo.parser;

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

        // M
        sPojUnicodeToPojNumberHashMap.put("Ḿ", "M2");
        sPojUnicodeToPojNumberHashMap.put("M̀", "M3");
        sPojUnicodeToPojNumberHashMap.put("M̂", "M5");
        sPojUnicodeToPojNumberHashMap.put("M̄", "M7");
        sPojUnicodeToPojNumberHashMap.put("M̍", "M8");
        sPojUnicodeToPojNumberHashMap.put("M̋", "M9");

        // m
        sPojUnicodeToPojNumberHashMap.put("ḿ", "m2");
        sPojUnicodeToPojNumberHashMap.put("m̀", "m3");
        sPojUnicodeToPojNumberHashMap.put("m̂", "m5");
        sPojUnicodeToPojNumberHashMap.put("m̄", "m7");
        sPojUnicodeToPojNumberHashMap.put("m̍", "m8");
        sPojUnicodeToPojNumberHashMap.put("m̋", "m9");

        // N
        sPojUnicodeToPojNumberHashMap.put("Ń", "N2");
        sPojUnicodeToPojNumberHashMap.put("Ǹ", "N3");
        sPojUnicodeToPojNumberHashMap.put("N̂", "N5");
        sPojUnicodeToPojNumberHashMap.put("N̄", "N7");
        sPojUnicodeToPojNumberHashMap.put("N̍", "N8");
        sPojUnicodeToPojNumberHashMap.put("N̋", "N9");

        // n
        sPojUnicodeToPojNumberHashMap.put("ń", "n2");
        sPojUnicodeToPojNumberHashMap.put("ǹ", "n3");
        sPojUnicodeToPojNumberHashMap.put("n̂", "n5");
        sPojUnicodeToPojNumberHashMap.put("n̄", "n7");
        sPojUnicodeToPojNumberHashMap.put("n̍", "n8");
        sPojUnicodeToPojNumberHashMap.put("n̋", "n9");

        // Ng
        sPojUnicodeToPojNumberHashMap.put("Ńg", "Ng2");
        sPojUnicodeToPojNumberHashMap.put("Ǹg", "Ng3");
        sPojUnicodeToPojNumberHashMap.put("N̂g", "Ng5");
        sPojUnicodeToPojNumberHashMap.put("N̄g", "Ng7");
        sPojUnicodeToPojNumberHashMap.put("N̍g", "Ng8");
        sPojUnicodeToPojNumberHashMap.put("N̋g", "Ng9");

        // NG
        sPojUnicodeToPojNumberHashMap.put("ŃG", "NG2");
        sPojUnicodeToPojNumberHashMap.put("ǸG", "NG3");
        sPojUnicodeToPojNumberHashMap.put("N̂G", "NG5");
        sPojUnicodeToPojNumberHashMap.put("N̄G", "NG7");
        sPojUnicodeToPojNumberHashMap.put("N̍G", "NG8");
        sPojUnicodeToPojNumberHashMap.put("N̋G", "NG9");

        // ng
        sPojUnicodeToPojNumberHashMap.put("ńg", "ng2");
        sPojUnicodeToPojNumberHashMap.put("ǹg", "ng3");
        sPojUnicodeToPojNumberHashMap.put("n̂g", "ng5");
        sPojUnicodeToPojNumberHashMap.put("n̄g", "ng7");
        sPojUnicodeToPojNumberHashMap.put("n̍g", "ng8");
        sPojUnicodeToPojNumberHashMap.put("n̋g", "ng9");

        final Set<String> keys = sPojUnicodeToPojNumberHashMap.keySet();
        for (String key : keys) {
            String value = sPojUnicodeToPojNumberHashMap.get(key);
            sPojNumberToPojUnicodeHashMap.put(value, key);
        }
    }
}
