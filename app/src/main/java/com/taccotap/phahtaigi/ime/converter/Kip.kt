package com.taccotap.phahtaigi.ime.converter

import java.util.*

object Kip {

    // <kiplmj_unicode, kiplmj_number>
    val sKiplmjUnicodeToKiplmjNumberHashMap: HashMap<String, String> = HashMap()

    // <kiplmj_number, kiplmj_unicode>
    val sKiplmjNumberToKiplmjUnicodeHashMap: HashMap<String, String> = HashMap()

    init {
        // A
        sKiplmjUnicodeToKiplmjNumberHashMap["Á"] = "A2"
        sKiplmjUnicodeToKiplmjNumberHashMap["À"] = "A3"
        sKiplmjUnicodeToKiplmjNumberHashMap["Â"] = "A5"
        sKiplmjUnicodeToKiplmjNumberHashMap["Ā"] = "A7"
        sKiplmjUnicodeToKiplmjNumberHashMap["A̍"] = "A8"
        sKiplmjUnicodeToKiplmjNumberHashMap["A̋"] = "A9"

        // a
        sKiplmjUnicodeToKiplmjNumberHashMap["á"] = "a2"
        sKiplmjUnicodeToKiplmjNumberHashMap["à"] = "a3"
        sKiplmjUnicodeToKiplmjNumberHashMap["â"] = "a5"
        sKiplmjUnicodeToKiplmjNumberHashMap["ā"] = "a7"
        sKiplmjUnicodeToKiplmjNumberHashMap["a̍"] = "a8"
        sKiplmjUnicodeToKiplmjNumberHashMap["a̋"] = "a9"

        // I
        sKiplmjUnicodeToKiplmjNumberHashMap["Í"] = "I2"
        sKiplmjUnicodeToKiplmjNumberHashMap["Ì"] = "I3"
        sKiplmjUnicodeToKiplmjNumberHashMap["Î"] = "I5"
        sKiplmjUnicodeToKiplmjNumberHashMap["Ī"] = "I7"
        sKiplmjUnicodeToKiplmjNumberHashMap["I̍"] = "I8"
        sKiplmjUnicodeToKiplmjNumberHashMap["I̋"] = "I9"

        // i
        sKiplmjUnicodeToKiplmjNumberHashMap["í"] = "i2"
        sKiplmjUnicodeToKiplmjNumberHashMap["ì"] = "i3"
        sKiplmjUnicodeToKiplmjNumberHashMap["î"] = "i5"
        sKiplmjUnicodeToKiplmjNumberHashMap["ī"] = "i7"
        sKiplmjUnicodeToKiplmjNumberHashMap["i̍"] = "i8"
        sKiplmjUnicodeToKiplmjNumberHashMap["i̋"] = "i9"

        // U
        sKiplmjUnicodeToKiplmjNumberHashMap["Ú"] = "U2"
        sKiplmjUnicodeToKiplmjNumberHashMap["Ù"] = "U3"
        sKiplmjUnicodeToKiplmjNumberHashMap["Û"] = "U5"
        sKiplmjUnicodeToKiplmjNumberHashMap["Ū"] = "U7"
        sKiplmjUnicodeToKiplmjNumberHashMap["U̍"] = "U8"
        sKiplmjUnicodeToKiplmjNumberHashMap["Ű"] = "U9"

        // u
        sKiplmjUnicodeToKiplmjNumberHashMap["ú"] = "u2"
        sKiplmjUnicodeToKiplmjNumberHashMap["ù"] = "u3"
        sKiplmjUnicodeToKiplmjNumberHashMap["û"] = "u5"
        sKiplmjUnicodeToKiplmjNumberHashMap["ū"] = "u7"
        sKiplmjUnicodeToKiplmjNumberHashMap["u̍"] = "u8"
        sKiplmjUnicodeToKiplmjNumberHashMap["ű"] = "u9"

        // E
        sKiplmjUnicodeToKiplmjNumberHashMap["É"] = "E2"
        sKiplmjUnicodeToKiplmjNumberHashMap["È"] = "E3"
        sKiplmjUnicodeToKiplmjNumberHashMap["Ê"] = "E5"
        sKiplmjUnicodeToKiplmjNumberHashMap["Ē"] = "E7"
        sKiplmjUnicodeToKiplmjNumberHashMap["E̍"] = "E8"
        sKiplmjUnicodeToKiplmjNumberHashMap["E̋"] = "E9"

        // e
        sKiplmjUnicodeToKiplmjNumberHashMap["é"] = "e2"
        sKiplmjUnicodeToKiplmjNumberHashMap["è"] = "e3"
        sKiplmjUnicodeToKiplmjNumberHashMap["ê"] = "e5"
        sKiplmjUnicodeToKiplmjNumberHashMap["ē"] = "e7"
        sKiplmjUnicodeToKiplmjNumberHashMap["e̍"] = "e8"
        sKiplmjUnicodeToKiplmjNumberHashMap["e̋"] = "e9"

        // O
        sKiplmjUnicodeToKiplmjNumberHashMap["Ó"] = "O2"
        sKiplmjUnicodeToKiplmjNumberHashMap["Ò"] = "O3"
        sKiplmjUnicodeToKiplmjNumberHashMap["Ô"] = "O5"
        sKiplmjUnicodeToKiplmjNumberHashMap["Ō"] = "O7"
        sKiplmjUnicodeToKiplmjNumberHashMap["O̍"] = "O8"
        sKiplmjUnicodeToKiplmjNumberHashMap["Ő"] = "O9"

        // o
        sKiplmjUnicodeToKiplmjNumberHashMap["ó"] = "o2"
        sKiplmjUnicodeToKiplmjNumberHashMap["ò"] = "o3"
        sKiplmjUnicodeToKiplmjNumberHashMap["ô"] = "o5"
        sKiplmjUnicodeToKiplmjNumberHashMap["ō"] = "o7"
        sKiplmjUnicodeToKiplmjNumberHashMap["o̍"] = "o8"
        sKiplmjUnicodeToKiplmjNumberHashMap["ő"] = "o9"

        // Oo
        sKiplmjUnicodeToKiplmjNumberHashMap["Óo"] = "Oo2"
        sKiplmjUnicodeToKiplmjNumberHashMap["Òo"] = "Oo3"
        sKiplmjUnicodeToKiplmjNumberHashMap["Ôo"] = "Oo5"
        sKiplmjUnicodeToKiplmjNumberHashMap["Ōo"] = "Oo7"
        sKiplmjUnicodeToKiplmjNumberHashMap["O̍o"] = "Oo8"
        sKiplmjUnicodeToKiplmjNumberHashMap["Őo"] = "Oo9"

        // OO
        sKiplmjUnicodeToKiplmjNumberHashMap["ÓO"] = "OO2"
        sKiplmjUnicodeToKiplmjNumberHashMap["ÒO"] = "OO3"
        sKiplmjUnicodeToKiplmjNumberHashMap["ÔO"] = "OO5"
        sKiplmjUnicodeToKiplmjNumberHashMap["ŌO"] = "OO7"
        sKiplmjUnicodeToKiplmjNumberHashMap["O̍O"] = "OO8"
        sKiplmjUnicodeToKiplmjNumberHashMap["ŐO"] = "OO9"

        // o
        sKiplmjUnicodeToKiplmjNumberHashMap["óo"] = "oo2"
        sKiplmjUnicodeToKiplmjNumberHashMap["òo"] = "oo3"
        sKiplmjUnicodeToKiplmjNumberHashMap["ôo"] = "oo5"
        sKiplmjUnicodeToKiplmjNumberHashMap["ōo"] = "oo7"
        sKiplmjUnicodeToKiplmjNumberHashMap["o̍o"] = "oo8"
        sKiplmjUnicodeToKiplmjNumberHashMap["őo"] = "oo9"

        // M
        sKiplmjUnicodeToKiplmjNumberHashMap["Ḿ"] = "M2"
        sKiplmjUnicodeToKiplmjNumberHashMap["M̀"] = "M3"
        sKiplmjUnicodeToKiplmjNumberHashMap["M̂"] = "M5"
        sKiplmjUnicodeToKiplmjNumberHashMap["M̄"] = "M7"
        sKiplmjUnicodeToKiplmjNumberHashMap["M̍"] = "M8"
        sKiplmjUnicodeToKiplmjNumberHashMap["M̋"] = "M9"

        // m
        sKiplmjUnicodeToKiplmjNumberHashMap["ḿ"] = "m2"
        sKiplmjUnicodeToKiplmjNumberHashMap["m̀"] = "m3"
        sKiplmjUnicodeToKiplmjNumberHashMap["m̂"] = "m5"
        sKiplmjUnicodeToKiplmjNumberHashMap["m̄"] = "m7"
        sKiplmjUnicodeToKiplmjNumberHashMap["m̍"] = "m8"
        sKiplmjUnicodeToKiplmjNumberHashMap["m̋"] = "m9"

        // N
        sKiplmjUnicodeToKiplmjNumberHashMap["Ń"] = "N2"
        sKiplmjUnicodeToKiplmjNumberHashMap["Ǹ"] = "N3"
        sKiplmjUnicodeToKiplmjNumberHashMap["N̂"] = "N5"
        sKiplmjUnicodeToKiplmjNumberHashMap["N̄"] = "N7"
        sKiplmjUnicodeToKiplmjNumberHashMap["N̍"] = "N8"
        sKiplmjUnicodeToKiplmjNumberHashMap["N̋"] = "N9"

        // n
        sKiplmjUnicodeToKiplmjNumberHashMap["ń"] = "n2"
        sKiplmjUnicodeToKiplmjNumberHashMap["ǹ"] = "n3"
        sKiplmjUnicodeToKiplmjNumberHashMap["n̂"] = "n5"
        sKiplmjUnicodeToKiplmjNumberHashMap["n̄"] = "n7"
        sKiplmjUnicodeToKiplmjNumberHashMap["n̍"] = "n8"
        sKiplmjUnicodeToKiplmjNumberHashMap["n̋"] = "n9"

        // Ng
        sKiplmjUnicodeToKiplmjNumberHashMap["Ńg"] = "Ng2"
        sKiplmjUnicodeToKiplmjNumberHashMap["Ǹg"] = "Ng3"
        sKiplmjUnicodeToKiplmjNumberHashMap["N̂g"] = "Ng5"
        sKiplmjUnicodeToKiplmjNumberHashMap["N̄g"] = "Ng7"
        sKiplmjUnicodeToKiplmjNumberHashMap["N̍g"] = "Ng8"
        sKiplmjUnicodeToKiplmjNumberHashMap["N̋g"] = "Ng9"

        // NG
        sKiplmjUnicodeToKiplmjNumberHashMap["ŃG"] = "NG2"
        sKiplmjUnicodeToKiplmjNumberHashMap["ǸG"] = "NG3"
        sKiplmjUnicodeToKiplmjNumberHashMap["N̂G"] = "NG5"
        sKiplmjUnicodeToKiplmjNumberHashMap["N̄G"] = "NG7"
        sKiplmjUnicodeToKiplmjNumberHashMap["N̍G"] = "NG8"
        sKiplmjUnicodeToKiplmjNumberHashMap["N̋G"] = "NG9"

        // ng
        sKiplmjUnicodeToKiplmjNumberHashMap["ńg"] = "ng2"
        sKiplmjUnicodeToKiplmjNumberHashMap["ǹg"] = "ng3"
        sKiplmjUnicodeToKiplmjNumberHashMap["n̂g"] = "ng5"
        sKiplmjUnicodeToKiplmjNumberHashMap["n̄g"] = "ng7"
        sKiplmjUnicodeToKiplmjNumberHashMap["n̍g"] = "ng8"
        sKiplmjUnicodeToKiplmjNumberHashMap["n̋g"] = "ng9"

        for ((key, value) in sKiplmjUnicodeToKiplmjNumberHashMap) {
            sKiplmjNumberToKiplmjUnicodeHashMap[value] = key
        }
    }
}
