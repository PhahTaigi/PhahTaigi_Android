package com.taccotap.taigidictparser.tailo.parser;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PojInputConverter {
    private static final String TAG = PojInputConverter.class.getSimpleName();

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
        pojWithoutNumber = pojWithoutNumber.toLowerCase();
        int count = pojWithoutNumber.length();

        final int lastIndexOfVowel = lastIndexOfRegex(pojWithoutNumber, "a|i|u|e|o");
        if (lastIndexOfVowel == -1) {
            final int lastIndexOfHalfVowel = lastIndexOfRegex(pojWithoutNumber, "m|ng|n");
            if (lastIndexOfHalfVowel == -1) {
                return -1;
            } else {
                return lastIndexOfHalfVowel;
            }
        } else {
            if (count == 1) {
                return 0;
            } else {
                if (lastIndexOfVowel == 0) {
                    return 0;
                } else {
                    if (pojWithoutNumber.contains("oai") && pojWithoutNumber.endsWith("h")) {
                        return pojWithoutNumber.indexOf("o");
                    }

                    final String previousChar = pojWithoutNumber.substring(lastIndexOfVowel - 1, lastIndexOfVowel);
                    // if vowel count >= 2
                    if (previousChar.matches("a|i|u|e|o")) {
                        final String lastVowelChar = pojWithoutNumber.substring(lastIndexOfVowel, lastIndexOfVowel + 1);
                        if (lastVowelChar.equals("i") && lastIndexOfVowel == count - 2) {
                            return lastIndexOfVowel - 1;
                        } else {
                            // if vowel is the last char
                            if (lastIndexOfVowel == count - 1) {
                                if (previousChar.equals("i")) {
                                    return lastIndexOfVowel;
                                } else {
                                    return lastIndexOfVowel - 1;
                                }
                            } else {
                                return lastIndexOfVowel;
                            }
                        }
                    } else {
                        return lastIndexOfVowel;
                    }
                }
            }
        }
    }

    /**
     * Version of lastIndexOf that uses regular expressions for searching.
     * By Tomer Godinger.
     *
     * @param str    String in which to search for the pattern.
     * @param toFind Pattern to locate.
     * @return The index of the requested pattern, if found; NOT_FOUND (-1) otherwise.
     */
    public static int lastIndexOfRegex(String str, String toFind) {
        Pattern pattern = Pattern.compile(toFind);
        Matcher matcher = pattern.matcher(str);

        // Default to the NOT_FOUND constant
        int lastIndex = -1;

        // Search for the given pattern
        while (matcher.find()) {
            lastIndex = matcher.start();
        }

        return lastIndex;
    }

    private static String generatePojInput(String pojWithoutNumber, String number, int tonePosition) {
        StringBuilder stringBuilder = new StringBuilder();

        final int length = pojWithoutNumber.length();
        for (int i = 0; i < length; i++) {
            final String currentCharString = pojWithoutNumber.substring(i, i + 1);

            if (i == tonePosition) {
                String pojNumber = currentCharString + number;
                final String poj = Poj.sPojNumberToPojUnicodeHashMap.get(pojNumber);
                if (poj != null) {
                    stringBuilder.append(poj);
                } else {
                    stringBuilder.append(currentCharString);
                }
            } else {
                stringBuilder.append(currentCharString);
            }
        }

        return stringBuilder.toString();
    }
}
