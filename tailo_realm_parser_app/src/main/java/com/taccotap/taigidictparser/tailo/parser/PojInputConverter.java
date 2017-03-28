package com.taccotap.taigidictparser.tailo.parser;

import android.text.TextUtils;


public class PojInputConverter {
    private static final String TAG = PojInputConverter.class.getSimpleName();

    // [o, a ,e ,u, i, n, m]
    public static int[] sLomajiNumberToWordTempArray = {0, 0, 0, 0, 0, 0, 0};

    public static void resetTempArray() {
        int count = sLomajiNumberToWordTempArray.length;
        for (int i = 0; i < count; i++) {
            sLomajiNumberToWordTempArray[i] = 0;
        }
    }

    public static String convertPojNumberToPoj(String pojNumber) {
        String fixedPojNumber = ConverterUtils.fixLomajiNumber(pojNumber);

        if (fixedPojNumber.length() <= 1) {
            return fixedPojNumber;
        }

        String number = "";

        final String lastCharString = fixedPojNumber.substring(fixedPojNumber.length() - 1);
        if (TextUtils.isDigitsOnly(lastCharString)) {
            number = lastCharString;
        }

        if (TextUtils.isEmpty(number)) {
            return pojNumber;
        }

        String pojWithoutNumber = fixedPojNumber.substring(0, fixedPojNumber.length() - 1);

        int tonePosition = calculateTonePosition(pojWithoutNumber);
        String poj = generatePojInput(pojWithoutNumber, number, tonePosition);

        return poj;
    }

    private static int calculateTonePosition(String pojWithoutNumber) {
        resetTempArray();

        int count = pojWithoutNumber.length();
        for (int i = 0; i < count; i++) {
            final char c = pojWithoutNumber.charAt(i);

            switch (c) {
                case 'o':
                case 'O':
                    sLomajiNumberToWordTempArray[0] = i + 1;
                    break;
                case 'a':
                case 'A':
                    sLomajiNumberToWordTempArray[1] = i + 1;
                    break;
                case 'e':
                case 'E':
                    sLomajiNumberToWordTempArray[2] = i + 1;
                    break;
                case 'u':
                case 'U':
                    sLomajiNumberToWordTempArray[3] = i + 1;
                    break;
                case 'i':
                case 'I':
                    sLomajiNumberToWordTempArray[4] = i + 1;
                    break;
                case 'n':
                case 'N':
                    sLomajiNumberToWordTempArray[5] = i + 1;
                    break;
                case 'm':
                case 'M':
                    sLomajiNumberToWordTempArray[6] = i + 1;
                    break;
            }
        }
        int foundToneCharPosition = -1;
        for (final int pos : sLomajiNumberToWordTempArray) {
            if (pos > 0) {
                foundToneCharPosition = pos - 1;
                break;
            }
        }

        return foundToneCharPosition;
    }

    private static String generatePojInput(String pojWithoutNumber, String number, int tonePosition) {
        StringBuilder stringBuilder = new StringBuilder();

        final int length = pojWithoutNumber.length();
        for (int i = 0; i < length; i++) {
            final String currentCharString = pojWithoutNumber.substring(i, i + 1);

            if (i == tonePosition) {
                String pojNumber = currentCharString + number;
                final String poj = Poj.sPojNumberToPojUnicodeHashMap.get(pojNumber);
                stringBuilder.append(poj);
            } else {
                stringBuilder.append(currentCharString);
            }
        }

        return stringBuilder.toString();
    }
}
