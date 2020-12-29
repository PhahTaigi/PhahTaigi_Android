package com.taccotap.phahtaigi.ime.converter

import android.text.TextUtils
import android.util.Log
import com.taccotap.phahtaigi.BuildConfig
import java.util.regex.Pattern

object KiplmjInputConverter {
    private val TAG = KiplmjInputConverter::class.java.simpleName

    private val sTailoWordExtractPattern = Pattern.compile("(?:(ph|p|m|b|th|tsh|ts|t|n|l|kh|k|ng|g|h|s|j)?([aiueo+]+(?:nn)?|ng|m)(?:(ng|m|n|re|r)|(p|t|h|k))?([12345789])?|(ph|p|m|b|th|tsh|ts|t|n|l|kh|k|ng|g|h|s|j)-?-?)", Pattern.CASE_INSENSITIVE)

    fun convertTailoNumberRawInputToTailoWords(input: String?): String? {
        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "convertTailoNumberRawInputToTailoWords(): input=" + input!!)
        }

        if (input == null) {
            return null
        }

        val matcher = sTailoWordExtractPattern.matcher(input)
        val groupCount = matcher.groupCount()
        if (groupCount == 0) {
            Log.w(TAG, "groupCount=0, return. input = $input")
            return input
        }

        val stringBuilder = StringBuilder()
        var isMatcherFound = false
        while (matcher.find()) {
            val foundTaigiWord = matcher.group()

            val tailo = convertTailoNumberToTailo(foundTaigiWord)
            if (BuildConfig.DEBUG_LOG) {
                Log.d(TAG, "foundTaigiWord=$foundTaigiWord, tailo=$tailo")
            }

            stringBuilder.append(tailo)
            stringBuilder.append("-")

            isMatcherFound = true
        }
        if (isMatcherFound) {
            stringBuilder.deleteCharAt(stringBuilder.length - 1)
        }

        return stringBuilder.toString()
    }

    private fun convertTailoNumberToTailo(tailoNumber: String): String {
        val fixedTailoNumber = ConverterUtils.fixLomajiNumber(tailoNumber)

        if (fixedTailoNumber.length <= 1) {
            return fixedTailoNumber
        }

        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "fixedTailoNumber=$fixedTailoNumber")
        }

        var number = ""

        val lastCharString = fixedTailoNumber.substring(fixedTailoNumber.length - 1)
        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "lastCharString=$lastCharString")
        }
        if (TextUtils.isDigitsOnly(lastCharString)) {
            number = lastCharString
        }

        if (TextUtils.isEmpty(number)) {
            return tailoNumber
        }

        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "number=$number")
        }

        val tailoWithoutNumber = fixedTailoNumber.substring(0, fixedTailoNumber.length - 1)

        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "tailoWithoutNumber=$tailoWithoutNumber")
        }

        if (tailoWithoutNumber.contains("a")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "a")
        } else if (tailoWithoutNumber.contains("A")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "A")
        } else if (tailoWithoutNumber.contains("oo")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "o")
        } else if (tailoWithoutNumber.contains("Oo") || tailoWithoutNumber.contains("OO")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "O")
        } else if (tailoWithoutNumber.contains("e")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "e")
        } else if (tailoWithoutNumber.contains("E")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "E")
        } else if (tailoWithoutNumber.contains("o")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "o")
        } else if (tailoWithoutNumber.contains("O")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "O")
        } else if (tailoWithoutNumber.contains("iu") || tailoWithoutNumber.contains("Iu")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "u")
        } else if (tailoWithoutNumber.contains("IU")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "U")
        } else if (tailoWithoutNumber.contains("ui") || tailoWithoutNumber.contains("Ui")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "i")
        } else if (tailoWithoutNumber.contains("UI")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "I")
        } else if (tailoWithoutNumber.contains("i")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "i")
        } else if (tailoWithoutNumber.contains("I")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "I")
        } else if (tailoWithoutNumber.contains("u")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "u")
        } else if (tailoWithoutNumber.contains("U")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "U")
        } else if (tailoWithoutNumber.contains("ng")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "ng")
        } else if (tailoWithoutNumber.contains("Ng")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "Ng")
        } else if (tailoWithoutNumber.contains("NG")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "NG")
        } else if (tailoWithoutNumber.contains("m")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "m")
        } else if (tailoWithoutNumber.contains("M")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "M")
        } else if (tailoWithoutNumber.contains("n")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "n")
        } else if (tailoWithoutNumber.contains("N")) {
            return replaceTailoNumberWithTailoUnicode(number, tailoWithoutNumber, "N")
        }

        return fixedTailoNumber
    }

    private fun replaceTailoNumberWithTailoUnicode(number: String, tailoWithoutNumber: String, contains: String): String {
        val tailoCharNumber = contains + number
        val tailoUnicode = KipLmj.sKiplmjNumberToKiplmjUnicodeHashMap[tailoCharNumber]
        return if (tailoUnicode != null) {
            tailoWithoutNumber.replaceFirst(contains.toRegex(), tailoUnicode)
        } else {
            tailoWithoutNumber
        }
    }
}
