package com.taccotap.phahtaigi.ime.converter;

import android.text.TextUtils;
import android.util.Log;

import com.taccotap.phahtaigi.BuildConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PojInputConverter {
    private static final String TAG = PojInputConverter.class.getSimpleName();

    private static Pattern sPojWordExtractPattern = Pattern.compile("(?:(ph|p|m|b|th|t|n|l|kh|k|ng|g|h|chh|ch|s|j)?([aiueo+]+(?:nn)?|ng|m)(?:(ng|m|n|re|r)|(p|t|h|k))?([1-9])?|(ph|p|m|b|th|t|n|l|kh|k|ng|g|h|chh|ch|s|j)-?-?)", Pattern.CASE_INSENSITIVE);

//    // [o, a ,e ,u, i, n, m]
//    private static int[] sLomajiNumberToWordTempArray = {0, 0, 0, 0, 0, 0, 0};
//
//    private static void resetTempArray() {
//        int count = sLomajiNumberToWordTempArray.length;
//        for (int i = 0; i < count; i++) {
//            sLomajiNumberToWordTempArray[i] = 0;
//        }
//    }

    public static String convertPojNumberRawInputToPojWords(String input) {
        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "convertPojNumberRawInputToPojWords(): input=" + input);
        }

        if (input == null) {
            return null;
        }

        final Matcher matcher = sPojWordExtractPattern.matcher(input);
        int groupCount = matcher.groupCount();
        if (groupCount == 0) {
            Log.w(TAG, "groupCount=0, return. input = " + input);
            return input;
        }

        StringBuilder stringBuilder = new StringBuilder();
        boolean isMatcherFound = false;
        while (matcher.find()) {
            final String foundTaigiWord = matcher.group();

            String pojNumber = convertPojRawInputToPojNumber(foundTaigiWord);
            String poj = convertPojNumberToPoj(pojNumber);
            if (BuildConfig.DEBUG_LOG) {
                Log.d(TAG, "foundTaigiWord=" + foundTaigiWord + ", poj=" + poj);
            }

            stringBuilder.append(poj);
            stringBuilder.append("-");

            isMatcherFound = true;
        }
        if (isMatcherFound) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        return stringBuilder.toString();
    }

    private static String convertPojRawInputToPojNumber(String foundTaigiWord) {
        String pojNumber = foundTaigiWord.replace("OO", "O͘")
                .replace("Oo", "O͘")
                .replace("oo", "o͘");

        if (pojNumber.indexOf("nn") > 0) {
            pojNumber = pojNumber.replace("nn", "ⁿ");
        } else if (pojNumber.indexOf("NN") > 0) {
            pojNumber = pojNumber.replace("NN", "ⁿ");
        }

        return pojNumber;
    }

    private static String convertPojNumberToPoj(String pojNumber) {
        String fixedPojNumber = ConverterUtils.fixLomajiNumber(pojNumber);

        if (fixedPojNumber.length() <= 1) {
            return fixedPojNumber;
        }

        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "fixedPojNumber=" + fixedPojNumber);
        }

        String number = "";

        final String lastCharString = fixedPojNumber.substring(fixedPojNumber.length() - 1);
        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "lastCharString=" + lastCharString);
        }
        if (TextUtils.isDigitsOnly(lastCharString)) {
            number = lastCharString;
        }

        if (TextUtils.isEmpty(number)) {
            return pojNumber;
        }

        String pojWithoutNumber = fixedPojNumber.substring(0, fixedPojNumber.length() - 1);

        int tonePosition = calculateTonePosition(pojWithoutNumber);
        String poj = generatePojInput(pojWithoutNumber, number, tonePosition);

        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "pojWithoutNumber=" + pojWithoutNumber + ", number=" + number + ", tonePosition=" + tonePosition + ", poj=" + poj);
        }

        return poj;
    }

    private static int calculateTonePosition(String pojWithoutNumber) {
        pojWithoutNumber = pojWithoutNumber.toLowerCase();
        int count = pojWithoutNumber.length();

        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "calculateTonePosition: pojWithoutNumber = " + pojWithoutNumber + ", count = " + count);
        }

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

                    final String previousChar = pojWithoutNumber.substring(count - 2, count - 1);
                    // if vowel count >= 2
                    if (previousChar.matches("a|i|u|e|o")) {
                        // if vowel is the last char
                        if (lastIndexOfVowel + 1 == count) {
                            if (previousChar.equals("i")) {
                                return lastIndexOfVowel;
                            } else {
                                return lastIndexOfVowel - 1;
                            }
                        } else {
                            return lastIndexOfVowel;
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

//    private static int calculateTonePositionWithPojToneStatistics(String pojWithoutNumber) {
//        resetTempArray();
//
//        int count = pojWithoutNumber.length();
//        for (int i = 0; i < count; i++) {
//            final char c = pojWithoutNumber.charAt(i);
//
//            switch (c) {
//                case 'o':
//                case 'O':
//                    sLomajiNumberToWordTempArray[0] = i + 1;
//                    break;
//                case 'a':
//                case 'A':
//                    sLomajiNumberToWordTempArray[1] = i + 1;
//                    break;
//                case 'e':
//                case 'E':
//                    sLomajiNumberToWordTempArray[2] = i + 1;
//                    break;
//                case 'u':
//                case 'U':
//                    sLomajiNumberToWordTempArray[3] = i + 1;
//                    break;
//                case 'i':
//                case 'I':
//                    sLomajiNumberToWordTempArray[4] = i + 1;
//                    break;
//                case 'n':
//                case 'N':
//                    sLomajiNumberToWordTempArray[5] = i + 1;
//                    break;
//                case 'm':
//                case 'M':
//                    sLomajiNumberToWordTempArray[6] = i + 1;
//                    break;
//            }
//        }
//        int foundToneCharPosition = -1;
//        for (final int pos : sLomajiNumberToWordTempArray) {
//            if (pos > 0) {
//                foundToneCharPosition = pos - 1;
//                break;
//            }
//        }
//
//        return foundToneCharPosition;
//    }

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
