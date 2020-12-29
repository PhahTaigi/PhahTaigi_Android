package com.taccotap.phahtaigi.ime.converter

import android.text.TextUtils
import android.util.Log
import com.taccotap.phahtaigi.BuildConfig
import java.util.regex.Pattern


object PojInputConverter {
    private val TAG = PojInputConverter::class.java.simpleName

    private val sPojWordExtractPattern = Pattern.compile("(?:(ph|p|m|b|th|t|n|l|kh|k|ng|g|h|chh|ch|s|j)?([aiueo+]+(?:nn)?|ng|m)(?:(ng|m|n|re|r)|(p|t|h|k))?([12345789])?|(ph|p|m|b|th|t|n|l|kh|k|ng|g|h|chh|ch|s|j)-?-?)", Pattern.CASE_INSENSITIVE)

    //    // [o, a ,e ,u, i, n, m]
    //    private static int[] sLomajiNumberToWordTempArray = {0, 0, 0, 0, 0, 0, 0};
    //
    //    private static void resetTempArray() {
    //        int count = sLomajiNumberToWordTempArray.length;
    //        for (int i = 0; i < count; i++) {
    //            sLomajiNumberToWordTempArray[i] = 0;
    //        }
    //    }

    fun convertPojNumberRawInputToPojWords(input: String?): String? {
        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "convertPojNumberRawInputToPojWords(): input=" + input!!)
        }

        if (input == null) {
            return null
        }

        val matcher = sPojWordExtractPattern.matcher(input)
        val groupCount = matcher.groupCount()
        if (groupCount == 0) {
            Log.w(TAG, "groupCount=0, return. input = $input")
            return input
        }

        val stringBuilder = StringBuilder()
        var isMatcherFound = false
        while (matcher.find()) {
            val foundTaigiWord = matcher.group()

            val pojNumber = convertPojRawInputToPojNumber(foundTaigiWord)
            val poj = convertPojNumberToPoj(pojNumber)
            if (BuildConfig.DEBUG_LOG) {
                Log.d(TAG, "foundTaigiWord=$foundTaigiWord, poj=$poj")
            }

            stringBuilder.append(poj)
            stringBuilder.append("-")

            isMatcherFound = true
        }
        if (isMatcherFound) {
            stringBuilder.deleteCharAt(stringBuilder.length - 1)
        }

        return stringBuilder.toString()
    }

    private fun convertPojRawInputToPojNumber(foundTaigiWord: String): String {
        var pojNumber = foundTaigiWord.replace("OO", "O͘")
                .replace("Oo", "O͘")
                .replace("oo", "o͘")

        if (pojNumber.indexOf("nn") > 0) {
            pojNumber = pojNumber.replace("nn", "ⁿ")
        } else if (pojNumber.indexOf("NN") > 0) {
            pojNumber = pojNumber.replace("NN", "ⁿ")
        }

        return pojNumber
    }

    private fun convertPojNumberToPoj(pojNumber: String): String {
        val fixedPojNumber = ConverterUtils.fixLomajiNumber(pojNumber)

        if (fixedPojNumber.length <= 1) {
            return fixedPojNumber
        }

        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "fixedPojNumber=$fixedPojNumber")
        }

        var number = ""

        val lastCharString = fixedPojNumber.substring(fixedPojNumber.length - 1)
        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "lastCharString=$lastCharString")
        }
        if (TextUtils.isDigitsOnly(lastCharString)) {
            number = lastCharString
        }

        if (TextUtils.isEmpty(number)) {
            return pojNumber
        }

        val pojWithoutNumber = fixedPojNumber.substring(0, fixedPojNumber.length - 1)

        val pojSianntiauPosition: PojSianntiauPosition? = getPojSianntiauPosition(pojWithoutNumber)
        if (pojSianntiauPosition == null) {
            return pojWithoutNumber
        } else {
            val str1 = pojWithoutNumber.substring(0, pojSianntiauPosition.pos)

            val str2PojBosianntiau = pojWithoutNumber.substring(pojSianntiauPosition.pos, pojSianntiauPosition.pos + pojSianntiauPosition.length)
            val str2PojNumber = str2PojBosianntiau + number
            val str2 = Poj.sPojNumberToPojUnicodeHashMap[str2PojNumber]
            if (str2.isNullOrEmpty()) {
                println("Poj.sPojNumberToPojUnicodeHashMap not found: $pojNumber, $str2PojNumber")
                throw PojUnicodeNotFoundException("Poj.sPojNumberToPojUnicodeHashMap[$str2PojNumber] not found")
            }

            val str3 = pojWithoutNumber.substring(pojSianntiauPosition.pos + pojSianntiauPosition.length, pojWithoutNumber.length)

//            println("$pojBoSianntiau$soojiSianntiauString -> $str1$str2$str3")

            val poj = str1 + str2 + str3

            if (BuildConfig.DEBUG_LOG) {
                Log.d(TAG, "pojWithoutNumber=$pojWithoutNumber, number=$number, tonePosition=${pojSianntiauPosition.pos}, poj=$poj")
            }

            return poj
        }

//        val poj = generatePojInput(pojWithoutNumber, number, pojSianntiauPosition)
    }

    fun getPojSianntiauPosition(pojBoSianntiau: String): PojSianntiauPosition? {
        val str = pojBoSianntiau.toLowerCase()
        val vowelList = listOf("a", "i", "u", "o͘", "e", "o")
        val semivowelList = listOf("m", "ng", "n")
        val choankhiunnVowelList = listOf("ir", "er")

        val lastIndexOfAnyVowel = str.lastIndexOfAny(vowelList)

        if (lastIndexOfAnyVowel == -1) {
            // Found no vowel

            val lastIndexOfAnySemiVowel = str.lastIndexOfAny(semivowelList)
            if (lastIndexOfAnySemiVowel == -1) {
                // Found no vowel, nor semivowel. Abort tone marking.
                return null
            } else {
                // Found no vowel, but found semivowel. Tone marks at semivowel.
                if (str.contains("ng")) {
                    return PojSianntiauPosition(lastIndexOfAnySemiVowel, 2)
                } else {
                    return PojSianntiauPosition(lastIndexOfAnySemiVowel, 1)
                }
            }
        } else {
            // Found a vowel

            // handle ir/er
            if (str.contains("(ir|er)".toRegex())) {
//                println("ir/er pojBoSianntiau: $pojBoSianntiau")

                val lastIndexOfAnyChoankhiunnVowel = str.lastIndexOfAny(choankhiunnVowelList)
                return PojSianntiauPosition(lastIndexOfAnyChoankhiunnVowel, 1)
            }

            val vowelCount: Int = getVowelCount(str, lastIndexOfAnyVowel)

            if (vowelCount == 1) {
                return PojSianntiauPosition(lastIndexOfAnyVowel, 1)
            } else {
                // Found HokBoim

                val last2ndJiboPosition = findJiboPositionFromLastCharExludingPhinnim(str, 2)
                val last2ndJiboString = str.substring(last2ndJiboPosition, last2ndJiboPosition + 1)

                val isPojJipsiann = isPojJipsiann(str)
                if (isPojJipsiann) {
                    // Found HokBoim Jipsiann

                    // Handle special cases:
                    if (str.toLowerCase().contains("iuh")) {
                        // "iuh" found
                        val findJiboPositionFromLastCharExludingPhinnim = findJiboPositionFromLastCharExludingPhinnim(str, 2)
                        return PojSianntiauPosition(findJiboPositionFromLastCharExludingPhinnim, 1)
                    } else {
                        if (last2ndJiboString.toLowerCase().matches("[iu]".toRegex())) {
                            val findJiboPositionFromLastCharExludingPhinnim = findJiboPositionFromLastCharExludingPhinnim(str, 3)
                            return PojSianntiauPosition(findJiboPositionFromLastCharExludingPhinnim, 1)
                        } else {
                            return PojSianntiauPosition(last2ndJiboPosition, 1)
                        }
                    }
                } else {
                    // Found HokBoim Not Jipsiann

                    // Handle special cases:
                    if (last2ndJiboString.toLowerCase() == "i") {
                        // Tone marks at the last jibo. (excluding phinnim)
                        val findJiboPositionFromLastCharExludingPhinnim = findJiboPositionFromLastCharExludingPhinnim(str, 1)
                        return PojSianntiauPosition(findJiboPositionFromLastCharExludingPhinnim, 1)
                    }

                    // Tone marks at the last 2nd jibo. (excluding phinnim)
                    val findJiboPositionFromLastCharExludingPhinnim = findJiboPositionFromLastCharExludingPhinnim(str, 2)
                    return PojSianntiauPosition(findJiboPositionFromLastCharExludingPhinnim, 1)
                }
            }
        }
    }

    private fun isPojJipsiann(pojBoSianntiau: String): Boolean {
        val lastCharExcludingPhinnim = pojBoSianntiau.replace("ⁿ", "").substring(pojBoSianntiau.length - 1)
        return lastCharExcludingPhinnim.toLowerCase().matches("[ptkh]".toRegex())
    }

    private fun findJiboPositionFromLastCharExludingPhinnim(pojBoSianntiau: String, findWhichCharFromRight: Int): Int {
        var pos: Int = pojBoSianntiau.length - 1
        var foundJiboCount = 0

        while (pos >= 0) {
            val currentCharString = pojBoSianntiau.substring(pos, pos + 1)
            var isFoundPojOoPoint = false
            var isFoundPojNg = false

            if (currentCharString == "ⁿ") {
                // skip
            } else if (currentCharString == "\u0358") {
                // found "o͘  "'s "͘  "
                isFoundPojOoPoint = true
            } else if (currentCharString.toLowerCase() == "g" && pos - 1 >= 0) {
                val nextCharString = pojBoSianntiau.substring(pos - 1, pos)
                if (nextCharString.toLowerCase() == "n") {
                    // found "ng"
                    isFoundPojNg = true
                } else {
                    // found "g"
                }
                foundJiboCount++
            } else {
                foundJiboCount++
            }

            if (foundJiboCount == findWhichCharFromRight) {
                break
            }

            if (isFoundPojNg) {
                pos -= 2
            } else if (isFoundPojOoPoint) {
                pos--
            } else {
                pos--
            }

            if (pos < 0) {
                break
            }
        }

        return pos
    }

    private fun getVowelCount(pojBoSianntiau: String, lastIndexOfAnyVowel: Int): Int {
        if (lastIndexOfAnyVowel == 0) {
            return 1
        }

        val isLeftCharAlsoVowel = pojBoSianntiau.substring(lastIndexOfAnyVowel - 1, lastIndexOfAnyVowel).contains("[aiueo]".toRegex())
        if (!isLeftCharAlsoVowel) {
            return 1
        }

        return 2

//        if (lastIndexOfAnyVowel - 2 <= 0) {
//            return 2
//        }
//
//        val isLeft2CharAlsoVowel = pojBoSianntiau.substring(lastIndexOfAnyVowel - 2, lastIndexOfAnyVowel - 1).contains("a|i|u|e|o".toRegex())
//        if (!isLeft2CharAlsoVowel) {
//            return 2
//        }
//
//        return 3
    }

//    private fun calculateTonePosition(pojWithoutNumber: String): Int {
//        var pojWithoutNumber = pojWithoutNumber
//        pojWithoutNumber = pojWithoutNumber.toLowerCase()
//        val count = pojWithoutNumber.length
//
//        if (BuildConfig.DEBUG_LOG) {
//            Log.d(TAG, "calculateTonePosition: pojWithoutNumber = $pojWithoutNumber, count = $count")
//        }
//
//        val lastIndexOfVowel = lastIndexOfRegex(pojWithoutNumber, "a|i|u|e|o")
//        if (lastIndexOfVowel == -1) {
//            val lastIndexOfHalfVowel = lastIndexOfRegex(pojWithoutNumber, "m|ng|n")
//            return if (lastIndexOfHalfVowel == -1) {
//                -1
//            } else {
//                lastIndexOfHalfVowel
//            }
//        } else {
//            if (count == 1) {
//                return 0
//            } else {
//                if (lastIndexOfVowel == 0) {
//                    return 0
//                } else {
//                    if (pojWithoutNumber.contains("oai") && pojWithoutNumber.endsWith("h")) {
//                        return pojWithoutNumber.indexOf("o")
//                    }
//
//                    val previousChar = pojWithoutNumber.substring(lastIndexOfVowel - 1, lastIndexOfVowel)
//                    // if vowel count >= 2
//                    if (previousChar.matches("a|i|u|e|o".toRegex())) {
//                        val lastVowelChar = pojWithoutNumber.substring(lastIndexOfVowel, lastIndexOfVowel + 1)
//                        return if (lastVowelChar == "i" && lastIndexOfVowel == count - 2) {
//                            lastIndexOfVowel - 1
//                        } else {
//                            // if vowel is the last char
//                            if (lastIndexOfVowel == count - 1) {
//                                if (previousChar == "i") {
//                                    lastIndexOfVowel
//                                } else {
//                                    lastIndexOfVowel - 1
//                                }
//                            } else {
//                                lastIndexOfVowel
//                            }
//                        }
//                    } else {
//                        return lastIndexOfVowel
//                    }
//                }
//            }
//        }
//    }

    /**
     * Version of lastIndexOf that uses regular expressions for searching.
     * By Tomer Godinger.
     *
     * @param str    String in which to search for the pattern.
     * @param toFind Pattern to locate.
     * @return The index of the requested pattern, if found; NOT_FOUND (-1) otherwise.
     */
    fun lastIndexOfRegex(str: String, toFind: String): Int {
        val pattern = Pattern.compile(toFind)
        val matcher = pattern.matcher(str)

        // Default to the NOT_FOUND constant
        var lastIndex = -1

        // Search for the given pattern
        while (matcher.find()) {
            lastIndex = matcher.start()
        }

        return lastIndex
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

//    private fun generatePojInput(pojWithoutNumber: String, number: String, pojSianntiauPosition: PojSianntiauPosition): String {
//        val stringBuilder = StringBuilder()
//
//        val length = pojWithoutNumber.length
//        for (i in 0 until length) {
//            val currentCharString = pojWithoutNumber.substring(i, i + 1)
//
//            if (i == tonePosition) {
//                val pojNumber = currentCharString + number
//                val poj = Poj.sPojNumberToPojUnicodeHashMap[pojNumber]
//                if (poj != null) {
//                    stringBuilder.append(poj)
//                } else {
//                    stringBuilder.append(currentCharString)
//                }
//            } else {
//                stringBuilder.append(currentCharString)
//            }
//        }
//
//        return stringBuilder.toString()
//    }
}
