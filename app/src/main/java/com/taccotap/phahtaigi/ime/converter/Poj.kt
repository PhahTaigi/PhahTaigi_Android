package com.taccotap.phahtaigi.ime.converter

object Poj {

    // <poj_unicode, poj_number>
    val sPojUnicodeToPojNumberHashMap: HashMap<String, String> = HashMap()

    // <poj_number, poj_unicode>
    val sPojNumberToPojUnicodeHashMap: HashMap<String, String> = HashMap()

    init {
        // A
        sPojUnicodeToPojNumberHashMap["Á"] = "A2"
        sPojUnicodeToPojNumberHashMap["À"] = "A3"
        sPojUnicodeToPojNumberHashMap["Â"] = "A5"
        sPojUnicodeToPojNumberHashMap["Ā"] = "A7"
        sPojUnicodeToPojNumberHashMap["A̍"] = "A8"
        sPojUnicodeToPojNumberHashMap["Ă"] = "A9"

        // a
        sPojUnicodeToPojNumberHashMap["á"] = "a2"
        sPojUnicodeToPojNumberHashMap["à"] = "a3"
        sPojUnicodeToPojNumberHashMap["â"] = "a5"
        sPojUnicodeToPojNumberHashMap["ā"] = "a7"
        sPojUnicodeToPojNumberHashMap["a̍"] = "a8"
        sPojUnicodeToPojNumberHashMap["ă"] = "a9"

        // I
        sPojUnicodeToPojNumberHashMap["Í"] = "I2"
        sPojUnicodeToPojNumberHashMap["Ì"] = "I3"
        sPojUnicodeToPojNumberHashMap["Î"] = "I5"
        sPojUnicodeToPojNumberHashMap["Ī"] = "I7"
        sPojUnicodeToPojNumberHashMap["I̍"] = "I8"
        sPojUnicodeToPojNumberHashMap["Ĭ"] = "I9"

        // i
        sPojUnicodeToPojNumberHashMap["í"] = "i2"
        sPojUnicodeToPojNumberHashMap["ì"] = "i3"
        sPojUnicodeToPojNumberHashMap["î"] = "i5"
        sPojUnicodeToPojNumberHashMap["ī"] = "i7"
        sPojUnicodeToPojNumberHashMap["i̍"] = "i8"
        sPojUnicodeToPojNumberHashMap["ĭ"] = "i9"

        // U
        sPojUnicodeToPojNumberHashMap["Ú"] = "U2"
        sPojUnicodeToPojNumberHashMap["Ù"] = "U3"
        sPojUnicodeToPojNumberHashMap["Û"] = "U5"
        sPojUnicodeToPojNumberHashMap["Ū"] = "U7"
        sPojUnicodeToPojNumberHashMap["U̍"] = "U8"
        sPojUnicodeToPojNumberHashMap["Ŭ"] = "U9"

        // u
        sPojUnicodeToPojNumberHashMap["ú"] = "u2"
        sPojUnicodeToPojNumberHashMap["ù"] = "u3"
        sPojUnicodeToPojNumberHashMap["û"] = "u5"
        sPojUnicodeToPojNumberHashMap["ū"] = "u7"
        sPojUnicodeToPojNumberHashMap["u̍"] = "u8"
        sPojUnicodeToPojNumberHashMap["ŭ"] = "u9"

        // E
        sPojUnicodeToPojNumberHashMap["É"] = "E2"
        sPojUnicodeToPojNumberHashMap["È"] = "E3"
        sPojUnicodeToPojNumberHashMap["Ê"] = "E5"
        sPojUnicodeToPojNumberHashMap["Ē"] = "E7"
        sPojUnicodeToPojNumberHashMap["E̍"] = "E8"
        sPojUnicodeToPojNumberHashMap["Ĕ"] = "E9"

        // e
        sPojUnicodeToPojNumberHashMap["é"] = "e2"
        sPojUnicodeToPojNumberHashMap["è"] = "e3"
        sPojUnicodeToPojNumberHashMap["ê"] = "e5"
        sPojUnicodeToPojNumberHashMap["ē"] = "e7"
        sPojUnicodeToPojNumberHashMap["e̍"] = "e8"
        sPojUnicodeToPojNumberHashMap["ĕ"] = "e9"

        // O
        sPojUnicodeToPojNumberHashMap["Ó"] = "O2"
        sPojUnicodeToPojNumberHashMap["Ò"] = "O3"
        sPojUnicodeToPojNumberHashMap["Ô"] = "O5"
        sPojUnicodeToPojNumberHashMap["Ō"] = "O7"
        sPojUnicodeToPojNumberHashMap["O̍"] = "O8"
        sPojUnicodeToPojNumberHashMap["Ŏ"] = "O9"

        // o
        sPojUnicodeToPojNumberHashMap["ó"] = "o2"
        sPojUnicodeToPojNumberHashMap["ò"] = "o3"
        sPojUnicodeToPojNumberHashMap["ô"] = "o5"
        sPojUnicodeToPojNumberHashMap["ō"] = "o7"
        sPojUnicodeToPojNumberHashMap["o̍"] = "o8"
        sPojUnicodeToPojNumberHashMap["ŏ"] = "o9"

        // O͘
        sPojUnicodeToPojNumberHashMap["Ó\u0358"] = "O\u03582"
        sPojUnicodeToPojNumberHashMap["Ò\u0358"] = "O\u03583"
        sPojUnicodeToPojNumberHashMap["Ô\u0358"] = "O\u03585"
        sPojUnicodeToPojNumberHashMap["Ō\u0358"] = "O\u03587"
        sPojUnicodeToPojNumberHashMap["O̍\u0358"] = "O\u03588"
        sPojUnicodeToPojNumberHashMap["Ŏ\u0358"] = "O\u03589"

        // o͘
        sPojUnicodeToPojNumberHashMap["ó\u0358"] = "o\u03582"
        sPojUnicodeToPojNumberHashMap["ò\u0358"] = "o\u03583"
        sPojUnicodeToPojNumberHashMap["ô\u0358"] = "o\u03585"
        sPojUnicodeToPojNumberHashMap["ō\u0358"] = "o\u03587"
        sPojUnicodeToPojNumberHashMap["o̍\u0358"] = "o\u03588"
        sPojUnicodeToPojNumberHashMap["ŏ\u0358"] = "o\u03589"

        // M
        sPojUnicodeToPojNumberHashMap["Ḿ"] = "M2"
        sPojUnicodeToPojNumberHashMap["M̀"] = "M3"
        sPojUnicodeToPojNumberHashMap["M̂"] = "M5"
        sPojUnicodeToPojNumberHashMap["M̄"] = "M7"
        sPojUnicodeToPojNumberHashMap["M̍"] = "M8"
        sPojUnicodeToPojNumberHashMap["M̆"] = "M9"

        // m
        sPojUnicodeToPojNumberHashMap["ḿ"] = "m2"
        sPojUnicodeToPojNumberHashMap["m̀"] = "m3"
        sPojUnicodeToPojNumberHashMap["m̂"] = "m5"
        sPojUnicodeToPojNumberHashMap["m̄"] = "m7"
        sPojUnicodeToPojNumberHashMap["m̍"] = "m8"
        sPojUnicodeToPojNumberHashMap["m̆"] = "m9"

        // N
        sPojUnicodeToPojNumberHashMap["Ń"] = "N2"
        sPojUnicodeToPojNumberHashMap["Ǹ"] = "N3"
        sPojUnicodeToPojNumberHashMap["N̂"] = "N5"
        sPojUnicodeToPojNumberHashMap["N̄"] = "N7"
        sPojUnicodeToPojNumberHashMap["N̍"] = "N8"
        sPojUnicodeToPojNumberHashMap["N̋"] = "N9"

        // n
        sPojUnicodeToPojNumberHashMap["ń"] = "n2"
        sPojUnicodeToPojNumberHashMap["ǹ"] = "n3"
        sPojUnicodeToPojNumberHashMap["n̂"] = "n5"
        sPojUnicodeToPojNumberHashMap["n̄"] = "n7"
        sPojUnicodeToPojNumberHashMap["n̍"] = "n8"
        sPojUnicodeToPojNumberHashMap["n̋"] = "n9"

        // Ng
        sPojUnicodeToPojNumberHashMap["Ńg"] = "Ng2"
        sPojUnicodeToPojNumberHashMap["Ǹg"] = "Ng3"
        sPojUnicodeToPojNumberHashMap["N̂g"] = "Ng5"
        sPojUnicodeToPojNumberHashMap["N̄g"] = "Ng7"
        sPojUnicodeToPojNumberHashMap["N̍g"] = "Ng8"
        sPojUnicodeToPojNumberHashMap["N̆g"] = "Ng9"

        // NG
        sPojUnicodeToPojNumberHashMap["ŃG"] = "NG2"
        sPojUnicodeToPojNumberHashMap["ǸG"] = "NG3"
        sPojUnicodeToPojNumberHashMap["N̂G"] = "NG5"
        sPojUnicodeToPojNumberHashMap["N̄G"] = "NG7"
        sPojUnicodeToPojNumberHashMap["N̍G"] = "NG8"
        sPojUnicodeToPojNumberHashMap["N̆G"] = "NG9"

        // ng
        sPojUnicodeToPojNumberHashMap["ńg"] = "ng2"
        sPojUnicodeToPojNumberHashMap["ǹg"] = "ng3"
        sPojUnicodeToPojNumberHashMap["n̂g"] = "ng5"
        sPojUnicodeToPojNumberHashMap["n̄g"] = "ng7"
        sPojUnicodeToPojNumberHashMap["n̍g"] = "ng8"
        sPojUnicodeToPojNumberHashMap["n̆g"] = "ng9"

        for ((key, value) in sPojUnicodeToPojNumberHashMap) {
            sPojNumberToPojUnicodeHashMap[value] = key
        }
    }
}
