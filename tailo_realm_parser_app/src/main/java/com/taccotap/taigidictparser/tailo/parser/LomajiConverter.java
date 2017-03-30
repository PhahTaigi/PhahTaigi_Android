package com.taccotap.taigidictparser.tailo.parser;

import android.text.TextUtils;

public class LomajiConverter {
    private static final String TAG = LomajiConverter.class.getSimpleName();

    public static final int LOMAJI_TYPE_POJ = 1;
    public static final int LOMAJI_TYPE_TAILO = 2;

    public static String convertLomajiWordToNumberTone(String lomajiWord, int lomajiType) {
        if (TextUtils.isEmpty(lomajiWord)) {
            return lomajiWord;
        }

        String fixedLomajiWord = convertTwoCharWordToPossibleOneCharWord(lomajiWord);

        StringBuilder stringBuilder = new StringBuilder();

        String number = null;
        int length = fixedLomajiWord.length();
        for (int i = 0; i < length; i++) {
            String currentLomajiChar = fixedLomajiWord.substring(i, i + 1);

            boolean isTwoCharWord = false;

            // check two-char word
            if (i > 0) {
                String previousLomajiChar = fixedLomajiWord.substring(i - 1, i);
                if (currentLomajiChar.equals("\u0300")
                        || currentLomajiChar.equals("\u0302")
                        || currentLomajiChar.equals("\u0304")
                        || currentLomajiChar.equals("\u030d")
                        || currentLomajiChar.equals("\u0358")) {
                    currentLomajiChar = previousLomajiChar + currentLomajiChar;
                    isTwoCharWord = true;
                }
            }

            String currentLomajiCharNumberTone = null;
            if (lomajiType == LOMAJI_TYPE_POJ) {
                currentLomajiCharNumberTone = Poj.sPojUnicodeToPojNumberHashMap.get(currentLomajiChar);
            } else if (lomajiType == LOMAJI_TYPE_TAILO) {
                currentLomajiCharNumberTone = Tailo.sTailoUnicodeToTailoNumberHashMap.get(currentLomajiChar);
            }

            if (currentLomajiCharNumberTone != null) {
//                Log.d(TAG, "currentLomajiCharNumberTone = " + currentLomajiCharNumberTone);

                number = currentLomajiCharNumberTone.substring(currentLomajiCharNumberTone.length() - 1);
                if (!isTwoCharWord) {
                    String currentLomajiWithoutNumber = currentLomajiCharNumberTone.substring(0, currentLomajiCharNumberTone.length() - 1);
                    stringBuilder.append(currentLomajiWithoutNumber);

//                    Log.d(TAG, "stringBuilder.append: currentLomajiWithoutNumber=" + currentLomajiWithoutNumber);
                }
            } else {
                if (number != null && TextUtils.isDigitsOnly(currentLomajiChar)) {
                    // remove useless ending numbers, skip
                } else {
                    stringBuilder.append(currentLomajiChar);

//                    Log.d(TAG, "stringBuilder.append: currentLomajiChar=" + currentLomajiChar);
                }
            }
        }

        if (number != null) {
            stringBuilder.append(number);
        }

        return stringBuilder.toString();
    }

    // fix two-char word to one-char word
    public static String convertTwoCharWordToPossibleOneCharWord(String unicodeLomaji) {
        String fixed = unicodeLomaji
                // x8 not change

                // a
                .replaceAll("\u0061\u0301", "\u00e1") // a2
                .replaceAll("\u0061\u0300", "\u00e0") // a3
                .replaceAll("\u0061\u0302", "\u00e2") // a5
                .replaceAll("\u0061\u0304", "\u0101") // a7

                .replaceAll("\u0041\u0301", "\u00c1") // A2
                .replaceAll("\u0041\u0300", "\u00c0") // A3
                .replaceAll("\u0041\u0302", "\u00c2") // A5
                .replaceAll("\u0041\u0304", "\u0100") // A7

                // i
                .replaceAll("\u0069\u0301", "\u00ed") // i2
                .replaceAll("\u0069\u0300", "\u00ec") // i3
                .replaceAll("\u0069\u0302", "\u00ee") // i5
                .replaceAll("\u0069\u0304", "\u012b") // i7

                .replaceAll("\u0049\u0301", "\u00cd") // I2
                .replaceAll("\u0049\u0300", "\u00cc") // I3
                .replaceAll("\u0049\u0302", "\u00ce") // I5
                .replaceAll("\u0049\u0304", "\u012a") // I7

                // u
                .replaceAll("\u0075\u0301", "\u00fa") // u2
                .replaceAll("\u0075\u0300", "\u00f9") // u3
                .replaceAll("\u0075\u0302", "\u00fb") // u5
                .replaceAll("\u0075\u0304", "\u016b") // u7

                .replaceAll("\u0055\u0301", "\u00da") // U2
                .replaceAll("\u0055\u0300", "\u00d9") // U3
                .replaceAll("\u0055\u0302", "\u00db") // U5
                .replaceAll("\u0055\u0304", "\u016a") // U7

                // e
                .replaceAll("\u0065\u0301", "\u00e9") // e2
                .replaceAll("\u0065\u0300", "\u00e8") // e3
                .replaceAll("\u0065\u0302", "\u00ea") // e5
                .replaceAll("\u0065\u0304", "\u0113") // e7

                .replaceAll("\u0045\u0301", "\u00c9") // E2
                .replaceAll("\u0045\u0300", "\u00c8") // E3
                .replaceAll("\u0045\u0302", "\u00ca") // E5
                .replaceAll("\u0045\u0304", "\u0102") // E7

                // o
                .replaceAll("\u006f\u0301", "\u00f3") // o2
                .replaceAll("\u006f\u0300", "\u00f2") // o3
                .replaceAll("\u006f\u0302", "\u00f4") // o5
                .replaceAll("\u006f\u0304", "\u014d") // o7

                .replaceAll("\u004f\u0301", "\u00d3") // O2
                .replaceAll("\u004f\u0300", "\u00d2") // O3
                .replaceAll("\u004f\u0302", "\u00d4") // O5
                .replaceAll("\u004f\u0304", "\u014c") // O7

                // n
                .replaceAll("\u006e\u0301", "\u0144") // n2
                .replaceAll("\u006e\u0300", "\u01f9") // n3; n5, n7 not change

                .replaceAll("\u004e\u0301", "\u0143") // N2
                .replaceAll("\u004e\u0300", "\u01f8") // N3; N5, N7 not change

                // m
                .replaceAll("\u006d\u0301", "\u1e3f") // m2; m3, m5, m7 not change

                .replaceAll("\u004d\u0301", "\u1e3e"); // M2; M3, M5, M7 not change

        return fixed;
    }

    public static String removeToneNumberAndHyphens(String lomajiNumber) {
        return lomajiNumber.replaceAll("[0-9]|-", "");
    }

//    public static final String convertPojNumberPhraseToTailoNumberPhrase(String pojNumberPhrase) {
//        String tailoNumberPhrase = pojNumberPhrase
//                .replaceAll("ch", "ts") // ch -> ts
//                .replaceAll("Ch", "Ts") // Ch -> Ts
//                .replaceAll("\u207f", "nn") // ⁿ -> nn
//                .replaceAll("o\u0358", "oo") // o͘ -> oo
//                .replaceAll("O\u0358", "Oo") // O͘ -> Oo
//                .replaceAll("o([aAeE])", "u$1") // oa -> ua, oe -> ue.
//                .replaceAll("O([aAeE])", "U$1") // Oa -> Ua, Oe -> Ue.
//                .replaceAll("ek", "ik") // ek -> ik
//                .replaceAll("Ek", "Ik") // Ek -> Ik
//                .replaceAll("eng", "ing") // eng -> ing
//                .replaceAll("Eng", "Ing"); // Eng -> Ing
//
//        return tailoNumberPhrase;
//    }

    public static final String convertTailoNumberWordToPojNumberWord(String tailoNumberWord) {
        String pojNumberWord = tailoNumberWord
                .replace("ts", "ch") // ts -> ch
                .replace("Ts", "Ch") // Ts -> Ch
                .replace("oo", "o\u0358") // oo -> o͘
                .replace("Oo", "O\u0358") // Oo -> O͘
                .replace("OO", "O\u0358") // OO -> O͘
                .replaceAll("u([aAeE])", "o$1") // ua -> oa, ue -> oe.
                .replaceAll("U([aAeE])", "O$1") // Ua -> Oa, Ue -> Oe.
                .replace("ik", "ek") // ik -> ek
                .replace("Ik", "Ek") // Ik -> Ek
                .replace("ing", "eng") // ing -> eng
                .replace("Ing", "Eng"); // Ing -> Eng

        if (pojNumberWord.indexOf("nn") > 0) {
            pojNumberWord = pojNumberWord.replace("nn", "\u207f"); // ⁿ -> nn
        }

        return pojNumberWord;
    }

    public static final String convertTailoInputWordToPojInputWord(String tailoInputWord) {
        String pojInputWord = tailoInputWord
                .replace("ts", "ch") // ts -> ch
                .replace("Ts", "Ch") // Ts -> Ch
                .replaceAll("u([aAeE])", "o$1") // ua -> oa, ue -> oe.
                .replaceAll("U([aAeE])", "O$1") // Ua -> Oa, Ue -> Oe.
                .replace("ik", "ek") // ik -> ek
                .replace("Ik", "Ek") // Ik -> Ek
                .replace("ing", "eng") // ing -> eng
                .replace("Ing", "Eng"); // Ing -> Eng

        return pojInputWord;
    }

    public static final int getLomajiWordToneNumber(String lomajiNumberWord) {
        String lastCharString = lomajiNumberWord.substring(lomajiNumberWord.length() - 1);
        if (TextUtils.isDigitsOnly(lastCharString)) {
            return Integer.valueOf(lastCharString);
        } else {
            if (lastCharString.matches("p|t|k|h")) {
                return 4;
            } else {
                return 1;
            }
        }
    }
}
