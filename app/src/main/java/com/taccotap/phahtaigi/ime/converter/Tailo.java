package com.taccotap.phahtaigi.ime.converter;

import java.util.HashMap;
import java.util.Set;

public class Tailo {

    // <tailo_unicode, tailo_number>
    public static HashMap<String, String> sTailoUnicodeToTailoNumberHashMap;

    // <tailo_number, tailo_unicode>
    public static HashMap<String, String> sTailoNumberToTailoUnicodeHashMap;

    static {
        sTailoUnicodeToTailoNumberHashMap = new HashMap<>();
        sTailoNumberToTailoUnicodeHashMap = new HashMap<>();

        // A
        sTailoUnicodeToTailoNumberHashMap.put("Á", "A2");
        sTailoUnicodeToTailoNumberHashMap.put("À", "A3");
        sTailoUnicodeToTailoNumberHashMap.put("Â", "A5");
        sTailoUnicodeToTailoNumberHashMap.put("Ā", "A7");
        sTailoUnicodeToTailoNumberHashMap.put("A̍", "A8");
        sTailoUnicodeToTailoNumberHashMap.put("A̋", "A9");

        // a
        sTailoUnicodeToTailoNumberHashMap.put("á", "a2");
        sTailoUnicodeToTailoNumberHashMap.put("à", "a3");
        sTailoUnicodeToTailoNumberHashMap.put("â", "a5");
        sTailoUnicodeToTailoNumberHashMap.put("ā", "a7");
        sTailoUnicodeToTailoNumberHashMap.put("a̍", "a8");
        sTailoUnicodeToTailoNumberHashMap.put("a̋", "a9");

        // I
        sTailoUnicodeToTailoNumberHashMap.put("Í", "I2");
        sTailoUnicodeToTailoNumberHashMap.put("Ì", "I3");
        sTailoUnicodeToTailoNumberHashMap.put("Î", "I5");
        sTailoUnicodeToTailoNumberHashMap.put("Ī", "I7");
        sTailoUnicodeToTailoNumberHashMap.put("I̍", "I8");
        sTailoUnicodeToTailoNumberHashMap.put("I̋", "I9");

        // i
        sTailoUnicodeToTailoNumberHashMap.put("í", "i2");
        sTailoUnicodeToTailoNumberHashMap.put("ì", "i3");
        sTailoUnicodeToTailoNumberHashMap.put("î", "i5");
        sTailoUnicodeToTailoNumberHashMap.put("ī", "i7");
        sTailoUnicodeToTailoNumberHashMap.put("i̍", "i8");
        sTailoUnicodeToTailoNumberHashMap.put("i̋", "i9");

        // U
        sTailoUnicodeToTailoNumberHashMap.put("Ú", "U2");
        sTailoUnicodeToTailoNumberHashMap.put("Ù", "U3");
        sTailoUnicodeToTailoNumberHashMap.put("Û", "U5");
        sTailoUnicodeToTailoNumberHashMap.put("Ū", "U7");
        sTailoUnicodeToTailoNumberHashMap.put("U̍", "U8");
        sTailoUnicodeToTailoNumberHashMap.put("Ű", "U9");

        // u
        sTailoUnicodeToTailoNumberHashMap.put("ú", "u2");
        sTailoUnicodeToTailoNumberHashMap.put("ù", "u3");
        sTailoUnicodeToTailoNumberHashMap.put("û", "u5");
        sTailoUnicodeToTailoNumberHashMap.put("ū", "u7");
        sTailoUnicodeToTailoNumberHashMap.put("u̍", "u8");
        sTailoUnicodeToTailoNumberHashMap.put("ű", "u9");

        // E
        sTailoUnicodeToTailoNumberHashMap.put("É", "E2");
        sTailoUnicodeToTailoNumberHashMap.put("È", "E3");
        sTailoUnicodeToTailoNumberHashMap.put("Ê", "E5");
        sTailoUnicodeToTailoNumberHashMap.put("Ē", "E7");
        sTailoUnicodeToTailoNumberHashMap.put("E̍", "E8");
        sTailoUnicodeToTailoNumberHashMap.put("E̋", "E9");

        // e
        sTailoUnicodeToTailoNumberHashMap.put("é", "e2");
        sTailoUnicodeToTailoNumberHashMap.put("è", "e3");
        sTailoUnicodeToTailoNumberHashMap.put("ê", "e5");
        sTailoUnicodeToTailoNumberHashMap.put("ē", "e7");
        sTailoUnicodeToTailoNumberHashMap.put("e̍", "e8");
        sTailoUnicodeToTailoNumberHashMap.put("e̋", "e9");

        // O
        sTailoUnicodeToTailoNumberHashMap.put("Ó", "O2");
        sTailoUnicodeToTailoNumberHashMap.put("Ò", "O3");
        sTailoUnicodeToTailoNumberHashMap.put("Ô", "O5");
        sTailoUnicodeToTailoNumberHashMap.put("Ō", "O7");
        sTailoUnicodeToTailoNumberHashMap.put("O̍", "O8");
        sTailoUnicodeToTailoNumberHashMap.put("Ő", "O9");

        // o
        sTailoUnicodeToTailoNumberHashMap.put("ó", "o2");
        sTailoUnicodeToTailoNumberHashMap.put("ò", "o3");
        sTailoUnicodeToTailoNumberHashMap.put("ô", "o5");
        sTailoUnicodeToTailoNumberHashMap.put("ō", "o7");
        sTailoUnicodeToTailoNumberHashMap.put("o̍", "o8");
        sTailoUnicodeToTailoNumberHashMap.put("ő", "o9");

        // Oo
        sTailoUnicodeToTailoNumberHashMap.put("Óo", "Oo2");
        sTailoUnicodeToTailoNumberHashMap.put("Òo", "Oo3");
        sTailoUnicodeToTailoNumberHashMap.put("Ôo", "Oo5");
        sTailoUnicodeToTailoNumberHashMap.put("Ōo", "Oo7");
        sTailoUnicodeToTailoNumberHashMap.put("O̍o", "Oo8");
        sTailoUnicodeToTailoNumberHashMap.put("Őo", "Oo9");

        // OO
        sTailoUnicodeToTailoNumberHashMap.put("ÓO", "OO2");
        sTailoUnicodeToTailoNumberHashMap.put("ÒO", "OO3");
        sTailoUnicodeToTailoNumberHashMap.put("ÔO", "OO5");
        sTailoUnicodeToTailoNumberHashMap.put("ŌO", "OO7");
        sTailoUnicodeToTailoNumberHashMap.put("O̍O", "OO8");
        sTailoUnicodeToTailoNumberHashMap.put("ŐO", "OO9");

        // o
        sTailoUnicodeToTailoNumberHashMap.put("óo", "oo2");
        sTailoUnicodeToTailoNumberHashMap.put("òo", "oo3");
        sTailoUnicodeToTailoNumberHashMap.put("ôo", "oo5");
        sTailoUnicodeToTailoNumberHashMap.put("ōo", "oo7");
        sTailoUnicodeToTailoNumberHashMap.put("o̍o", "oo8");
        sTailoUnicodeToTailoNumberHashMap.put("őo", "oo9");

        // M
        sTailoUnicodeToTailoNumberHashMap.put("Ḿ", "M2");
        sTailoUnicodeToTailoNumberHashMap.put("M̀", "M3");
        sTailoUnicodeToTailoNumberHashMap.put("M̂", "M5");
        sTailoUnicodeToTailoNumberHashMap.put("M̄", "M7");
        sTailoUnicodeToTailoNumberHashMap.put("M̍", "M8");
        sTailoUnicodeToTailoNumberHashMap.put("M̋", "M9");

        // m
        sTailoUnicodeToTailoNumberHashMap.put("ḿ", "m2");
        sTailoUnicodeToTailoNumberHashMap.put("m̀", "m3");
        sTailoUnicodeToTailoNumberHashMap.put("m̂", "m5");
        sTailoUnicodeToTailoNumberHashMap.put("m̄", "m7");
        sTailoUnicodeToTailoNumberHashMap.put("m̍", "m8");
        sTailoUnicodeToTailoNumberHashMap.put("m̋", "m9");

        // N
        sTailoUnicodeToTailoNumberHashMap.put("Ń", "N2");
        sTailoUnicodeToTailoNumberHashMap.put("Ǹ", "N3");
        sTailoUnicodeToTailoNumberHashMap.put("N̂", "N5");
        sTailoUnicodeToTailoNumberHashMap.put("N̄", "N7");
        sTailoUnicodeToTailoNumberHashMap.put("N̍", "N8");
        sTailoUnicodeToTailoNumberHashMap.put("N̋", "N9");

        // n
        sTailoUnicodeToTailoNumberHashMap.put("ń", "n2");
        sTailoUnicodeToTailoNumberHashMap.put("ǹ", "n3");
        sTailoUnicodeToTailoNumberHashMap.put("n̂", "n5");
        sTailoUnicodeToTailoNumberHashMap.put("n̄", "n7");
        sTailoUnicodeToTailoNumberHashMap.put("n̍", "n8");
        sTailoUnicodeToTailoNumberHashMap.put("n̋", "n9");

        // Ng
        sTailoUnicodeToTailoNumberHashMap.put("Ńg", "Ng2");
        sTailoUnicodeToTailoNumberHashMap.put("Ǹg", "Ng3");
        sTailoUnicodeToTailoNumberHashMap.put("N̂g", "Ng5");
        sTailoUnicodeToTailoNumberHashMap.put("N̄g", "Ng7");
        sTailoUnicodeToTailoNumberHashMap.put("N̍g", "Ng8");
        sTailoUnicodeToTailoNumberHashMap.put("N̋g", "Ng9");

        // NG
        sTailoUnicodeToTailoNumberHashMap.put("ŃG", "NG2");
        sTailoUnicodeToTailoNumberHashMap.put("ǸG", "NG3");
        sTailoUnicodeToTailoNumberHashMap.put("N̂G", "NG5");
        sTailoUnicodeToTailoNumberHashMap.put("N̄G", "NG7");
        sTailoUnicodeToTailoNumberHashMap.put("N̍G", "NG8");
        sTailoUnicodeToTailoNumberHashMap.put("N̋G", "NG9");

        // ng
        sTailoUnicodeToTailoNumberHashMap.put("ńg", "ng2");
        sTailoUnicodeToTailoNumberHashMap.put("ǹg", "ng3");
        sTailoUnicodeToTailoNumberHashMap.put("n̂g", "ng5");
        sTailoUnicodeToTailoNumberHashMap.put("n̄g", "ng7");
        sTailoUnicodeToTailoNumberHashMap.put("n̍g", "ng8");
        sTailoUnicodeToTailoNumberHashMap.put("n̋g", "ng9");

        final Set<String> keys = sTailoUnicodeToTailoNumberHashMap.keySet();
        for (String key : keys) {
            String value = sTailoUnicodeToTailoNumberHashMap.get(key);
            sTailoNumberToTailoUnicodeHashMap.put(value, key);
        }
    }
}
