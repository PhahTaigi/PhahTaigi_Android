package com.taccotap.phahtaigi.ime.converter;

public class PojToTailoConverter {
    private static final String TAG = PojToTailoConverter.class.getSimpleName();

    public static final String pojNumberToTailoNumber(String pojNumber) {
        String tailoNumber = pojNumber
                .replaceAll("ch", "ts") // ch -> ts
                .replaceAll("Ch", "Ts") // Ch -> Ts
                .replaceAll("\u207f", "nn") // ⁿ -> nn
                .replaceAll("o\u0358", "oo") // o͘ -> oo
                .replaceAll("O\u0358", "Oo") // O͘ -> Oo
                .replaceAll("o([aAeE])", "u$1") // oa -> ua, oe -> ue.
                .replaceAll("O([aAeE])", "U$1") // Oa -> Ua, Oe -> Ue.
                .replaceAll("ek", "ik") // ek -> ik
                .replaceAll("Ek", "Ik") // Ek -> Ik
                .replaceAll("eng", "ing") // eng -> ing
                .replaceAll("Eng", "Ing"); // Eng -> Ing

        return tailoNumber;
    }

}
