package com.taccotap.phahtaigi.ime.converter;

import android.text.TextUtils;
import android.util.Log;

import com.taccotap.phahtaigi.BuildConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TailoInputConverter {
    private static final String TAG = TailoInputConverter.class.getSimpleName();

    private static Pattern sTailoWordExtractPattern = Pattern.compile("(?:(ph|p|m|b|th|tsh|ts|t|n|l|kh|k|ng|g|h|s|j)?([aiueo+]+(?:nn)?|ng|m)(?:(ng|m|n)|(p|t|h|k))?([1-9])?|(ph|p|m|b|th|tsh|ts|t|n|l|kh|k|ng|g|h|s|j)-?-?)", Pattern.CASE_INSENSITIVE);

    public static String convertTailoNumberRawInputToTailoWords(String input) {
        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "convertTailoNumberRawInputToTailoWords(): input=" + input);
        }

        if (input == null) {
            return null;
        }

        final Matcher matcher = sTailoWordExtractPattern.matcher(input);
        int groupCount = matcher.groupCount();
        if (groupCount == 0) {
            Log.w(TAG, "groupCount=0, return. input = " + input);
            return input;
        }

        StringBuilder stringBuilder = new StringBuilder();
        boolean isMatcherFound = false;
        while (matcher.find()) {
            final String foundTaigiWord = matcher.group();

            String tailo = convertTailoNumberToTailo(foundTaigiWord);
            if (BuildConfig.DEBUG_LOG) {
                Log.d(TAG, "foundTaigiWord=" + foundTaigiWord + ", tailo=" + tailo);
            }

            stringBuilder.append(tailo);
            stringBuilder.append("-");

            isMatcherFound = true;
        }
        if (isMatcherFound) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        return stringBuilder.toString();
    }

    private static String convertTailoNumberToTailo(String tailoNumber) {
        String fixedTailoNumber = ConverterUtils.fixLomajiNumber(tailoNumber);

        if (fixedTailoNumber.length() <= 1) {
            return fixedTailoNumber;
        }

        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "fixedTailoNumber=" + fixedTailoNumber);
        }

        String number = "";

        final String lastCharString = fixedTailoNumber.substring(fixedTailoNumber.length() - 1);
        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "lastCharString=" + lastCharString);
        }
        if (TextUtils.isDigitsOnly(lastCharString)) {
            number = lastCharString;
        }

        if (TextUtils.isEmpty(number)) {
            return tailoNumber;
        }

        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "number=" + number);
        }

        String tailoWithoutNumber = fixedTailoNumber.substring(0, fixedTailoNumber.length() - 1);

        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "tailoWithoutNumber=" + tailoWithoutNumber);
        }

        if (tailoWithoutNumber.contains("a")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "a");
        } else if (tailoWithoutNumber.contains("A")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "A");
        } else if (tailoWithoutNumber.contains("oo")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "o");
        } else if (tailoWithoutNumber.contains("Oo") || tailoWithoutNumber.contains("OO")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "O");
        } else if (tailoWithoutNumber.contains("e")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "e");
        } else if (tailoWithoutNumber.contains("E")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "E");
        } else if (tailoWithoutNumber.contains("o")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "o");
        } else if (tailoWithoutNumber.contains("O")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "O");
        } else if (tailoWithoutNumber.contains("iu") || tailoWithoutNumber.contains("Iu")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "u");
        } else if (tailoWithoutNumber.contains("IU")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "U");
        } else if (tailoWithoutNumber.contains("ui") || tailoWithoutNumber.contains("Ui")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "i");
        } else if (tailoWithoutNumber.contains("UI")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "I");
        } else if (tailoWithoutNumber.contains("i")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "i");
        } else if (tailoWithoutNumber.contains("I")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "I");
        } else if (tailoWithoutNumber.contains("u")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "u");
        } else if (tailoWithoutNumber.contains("U")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "U");
        } else if (tailoWithoutNumber.contains("ng")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "n");
        } else if (tailoWithoutNumber.contains("Ng") || tailoWithoutNumber.contains("NG")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "N");
        } else if (tailoWithoutNumber.contains("m")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "m");
        } else if (tailoWithoutNumber.contains("M")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "M");
        } else if (tailoWithoutNumber.contains("n")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "n");
        } else if (tailoWithoutNumber.contains("N")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "N");
        }

        return fixedTailoNumber;
    }

    private static String replaceTailoNumberWithTailoUnicode(String number, String tailoWithoutNumber, String contains) {
        String tailoCharNumber = contains + number;
        final String tailoUnicode = Tailo.sTailoNumberToTailoUnicodeHashMap.get(tailoCharNumber);
        if (tailoUnicode != null) {
            return tailoWithoutNumber.replaceFirst(contains, tailoUnicode);
        } else {
            return tailoWithoutNumber;
        }
    }
}
