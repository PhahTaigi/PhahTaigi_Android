package com.taccotap.phahtaigi.ime.converter;

public class ConverterUtils {

    public static String fixLomajiNumber(String lomajiNumber) {
        final int foundNumberIndex = findCorrectNumberIndex(lomajiNumber);
        if (foundNumberIndex == -1) {
            return lomajiNumber;
        }

        return lomajiNumber.substring(0, foundNumberIndex + 1);
    }

    // ex: handle tai5566 -> tai5
    private static int findCorrectNumberIndex(String lomajiNumber) {
        int foundNumberIndex = -1;

        final int count = lomajiNumber.length();
        for (int i = count - 1; i >= 0; i--) {
            char c = lomajiNumber.charAt(i);
            if (Character.isDigit(c)) {
                foundNumberIndex = i;
            } else {
                break;
            }
        }

        return foundNumberIndex;
    }
}
