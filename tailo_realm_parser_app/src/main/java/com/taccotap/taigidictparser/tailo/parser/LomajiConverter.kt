package com.taccotap.taigidictparser.tailo.parser

object LomajiConverter {

    enum class ConvertLomajiInputStringCase {
        CASE_POJ_INPUT_FIX,
        CASE_POJ_INPUT_TO_KIPLMJ_INPUT,
        CASE_POJ_INPUT_TO_POJ_UNICODE,
        CASE_POJ_INPUT_TO_KIPLMJ_UNICODE,
        CASE_KIPLMJ_INPUT_TO_POJ_INPUT,
        CASE_KIPLMJ_INPUT_TO_KIPLMJ_UNICODE,
        CASE_KIPLMJ_INPUT_TO_POJ_UNICODE,
    }

    enum class ConvertLomajiUnicodeStringCase {
        CASE_KIPLMJ_UNICODE_TO_KIPLMJ_INPUT,
        CASE_POJ_UNICODE_TO_POJ_INPUT,
    }

    fun pojInputStringFix(pojInputString: String): String {
        return convertLomajiInputString(pojInputString, ConvertLomajiInputStringCase.CASE_POJ_INPUT_FIX)
    }

    private fun pojInputFix(pojInput: String): String {
        var str = pojInputFixTrailingN(pojInput)
        str = pojInputFixSianntiauSoojiPosition(str)

        str = str
                .replace("ts", "ch") // ts -> ch
                .replace("Ts", "Ch") // Ts -> Ch
                .replace("TS", "CH") // TS -> CH
                .replace("ou", "oo") // ou -> oo
                .replace("Ou", "Oo") // Ou -> Oo
                .replace("OU", "OO") // OU -> OO

        return str
    }

    private fun pojInputFixSianntiauSoojiPosition(pojInput: String): String {
        if (pojInput.length > 1) {
            if (pojInput.substring(pojInput.length - 1).isNumeric()) {
                return pojInput
            } else {
                val matchSequence = Regex("[0-9]").findAll(pojInput)
                val matchList = matchSequence.toList()
                val size = matchList.size

                if (size == 0) {
                    return pojInput
                } else {
                    val matchResult: MatchResult = matchList[0]

                    return pojInput.substring(0, matchResult.range.first) +
                            pojInput.substring(matchResult.range.last + 1) +
                            pojInput.substring(matchResult.range.first, matchResult.range.last + 1)
                }
            }
        }

        return pojInput
    }

    private fun pojInputToUnicodeFix(pojInput: String): String {
        var fixPojInput = pojInput
                .replace("oo", "o\u0358") // o͘
                .replace("Oo", "O\u0358") // O͘
                .replace("OO", "O\u0358") // O͘

        if (fixPojInput.length > 1) {
            var fixPojInputBoSianntiau = fixPojInput
            if (fixPojInputBoSianntiau.substring(fixPojInputBoSianntiau.length - 1).isNumeric()) {
                fixPojInputBoSianntiau = fixPojInputBoSianntiau.substring(0, fixPojInputBoSianntiau.length - 1)
            }
            if (fixPojInputBoSianntiau.endsWith("nn", true)) {
                fixPojInput = fixPojInput.replace("nn", "ⁿ")
                        .replace("NN", "ⁿ")
            } else if (fixPojInputBoSianntiau.endsWith("nnh", true)) {
                fixPojInput = fixPojInput.replace("nnh", "ⁿh")
                        .replace("NNH", "ⁿH")
            }
        }

        return fixPojInput
    }

    private fun pojInputFixTrailingN(pojInput: String): String {
        if (pojInput.isEmpty() || pojInput.length == 1) {
            return pojInput
        }

        val lastCharString: String = pojInput.substring(pojInput.length - 1)
        var pojInputBoSianntiau: String
        var sianntiauString = ""

        if (!lastCharString.isNumeric()) {
            pojInputBoSianntiau = pojInput
        } else {
            pojInputBoSianntiau = pojInput.substring(0, pojInput.length - 1)
            sianntiauString = pojInput.substring(pojInput.length - 1)
        }

        if (pojInputBoSianntiau.length > 1) {
            val lastCharString2 = pojInputBoSianntiau.substring(pojInputBoSianntiau.length - 1)
            if (lastCharString2 == "N") {
                pojInputBoSianntiau = pojInputBoSianntiau.substring(0, pojInputBoSianntiau.length - 1) + "nn"
            }
        }

        return pojInputBoSianntiau + sianntiauString
    }

    /*
        For single word only, not for string.
     */
    fun pojInputToKiplmjInput(pojInput: String): String {
        return pojInput
                .replace("ch", "ts") // ch -> ts
                .replace("Ch", "Ts") // Ch -> Ts
                .replace("CH", "TS") // CH -> TS
                .replace("o([aAeE])".toRegex()) { "u" + it.value.substring(1) } // oa -> ua, oe -> ue.
                .replace("O([aAeE])".toRegex()) { "U" + it.value.substring(1) } // Oa -> Ua, Oe -> Ue.
                .replace("ek", "ik") // ek -> ik
                .replace("Ek", "Ik") // Ek -> Ik
                .replace("EK", "IK") // Ek -> IK
                .replace("eng", "ing") // eng -> ing
                .replace("Eng", "Ing") // Eng -> Ing
                .replace("ENG", "ING")
    }

    /*
         For single word only, not for string.
    */
    fun kiplmjInputToPojInput(tailoInput: String): String {
        return tailoInput
                .replace("ts", "ch") // ts -> ch
                .replace("Ts", "Ch") // Ts -> Ch
                .replace("TS", "CH") // TS -> CH
                .replace("u([aAeE])".toRegex()) { "o" + it.value.substring(1) } // ua -> oa, ue -> oe.
                .replace("U([aAeE])".toRegex()) { "O" + it.value.substring(1) } // Ua -> Oa, Ue -> Oe.
                .replace("ik", "ek") // ik -> ek
                .replace("Ik", "Ek") // Ik -> Ek
                .replace("IK", "EK") // IK -> EK
                .replace("ing", "eng") // ing -> eng
                .replace("Ing", "Eng") // Ing -> Eng
                .replace("ING", "ENG")
    }

    /*
        For single word only, not for string.
    */
    private fun kiplmjUnicodeToKiplmjInput(kiplmjUnicode: String): String {
        for (possibleKiplmjUnicode in KipLmj.sKiplmjUnicodeToKiplmjNumberHashMap.keys) {
            if (kiplmjUnicode.contains(possibleKiplmjUnicode)) {
                val kiplmjSoojiSianntiau: String? = KipLmj.sKiplmjUnicodeToKiplmjNumberHashMap[possibleKiplmjUnicode]
                if (kiplmjSoojiSianntiau != null) {
                    val tailoBoSianntiau = kiplmjSoojiSianntiau.substring(0, kiplmjSoojiSianntiau.length - 1)
                    val sianntiauSooji = kiplmjSoojiSianntiau.substring(kiplmjSoojiSianntiau.length - 1)
                    return kiplmjUnicode.replace(possibleKiplmjUnicode, tailoBoSianntiau) + sianntiauSooji
                }
            }
        }

        return kiplmjUnicode
    }

    /*
    For single word only, not for string.
*/
    private fun pojUnicodeToPojInput(pojUnicode: String): String {
        for (possiblePojUnicode in Poj.sPojUnicodeToPojNumberHashMap.keys) {
            if (pojUnicode.contains(possiblePojUnicode)) {
                val pojSoojiSianntiau: String? = Poj.sPojUnicodeToPojNumberHashMap[possiblePojUnicode]
                if (pojSoojiSianntiau != null) {
                    val pojBoSianntiau = pojSoojiSianntiau.substring(0, pojSoojiSianntiau.length - 1)
                    val sianntiauSooji = pojSoojiSianntiau.substring(pojSoojiSianntiau.length - 1)
                    return pojUnicode
                            .replace(possiblePojUnicode, pojBoSianntiau)
                            .replace("ⁿ", "nn") +
                            sianntiauSooji
                }
            }
        }

        return pojUnicode.replace("ⁿ", "nn")
    }

    fun convertLomajiInputString(inputString: String, convertLomajiInputStringCase: ConvertLomajiInputStringCase): String {
        val matchSequence: Sequence<MatchResult> = LomajiSplitter.splitLomajiSoojiTiauho(inputString)
        val matchList = matchSequence.toList()
        val size = matchList.size

        if (size == 0) {
            return inputString
        }

        val stringBuilder = StringBuilder()

        var lastPos = 0
        for (i in 0 until size) {
            val matchResult: MatchResult = matchList.get(i)

            val nonPojString = inputString.substring(lastPos, matchResult.range.first)
            stringBuilder.append(nonPojString)

            val lomajiSoojiTiauhu = inputString.substring(matchResult.range.first, matchResult.range.last + 1)

            when (convertLomajiInputStringCase) {
                ConvertLomajiInputStringCase.CASE_POJ_INPUT_FIX -> {
                    val pojInputFix = pojInputFix(lomajiSoojiTiauhu)
                    stringBuilder.append(pojInputFix)
                }

                ConvertLomajiInputStringCase.CASE_POJ_INPUT_TO_POJ_UNICODE -> {
                    val lomajiUnicode: String = LomajiConverter.pojInputToPojUnicode(lomajiSoojiTiauhu)
                    stringBuilder.append(lomajiUnicode)
                }

                ConvertLomajiInputStringCase.CASE_KIPLMJ_INPUT_TO_KIPLMJ_UNICODE -> {
                    val lomajiUnicode: String = LomajiConverter.kiplmjInputToKiplmjUnicode(lomajiSoojiTiauhu)
                    stringBuilder.append(lomajiUnicode)
                }

                ConvertLomajiInputStringCase.CASE_POJ_INPUT_TO_KIPLMJ_UNICODE -> {
                    val tailoInput = pojInputToKiplmjInput(lomajiSoojiTiauhu)
                    val tailoUnicode: String = LomajiConverter.kiplmjInputToKiplmjUnicode(tailoInput)
                    stringBuilder.append(tailoUnicode)
                }

                ConvertLomajiInputStringCase.CASE_KIPLMJ_INPUT_TO_POJ_UNICODE -> {
                    val pojInput = kiplmjInputToPojInput(lomajiSoojiTiauhu)
                    val pojUnicode: String = LomajiConverter.pojInputToPojUnicode(pojInput)
                    stringBuilder.append(pojUnicode)
                }

                ConvertLomajiInputStringCase.CASE_POJ_INPUT_TO_KIPLMJ_INPUT -> {
                    val tailoInput = pojInputToKiplmjInput(lomajiSoojiTiauhu)
                    stringBuilder.append(tailoInput)
                }

                ConvertLomajiInputStringCase.CASE_KIPLMJ_INPUT_TO_POJ_INPUT -> {
                    val pojInput = kiplmjInputToPojInput(lomajiSoojiTiauhu)
                    stringBuilder.append(pojInput)
                }
            }

            lastPos = matchResult.range.last + 1

            if (i == size - 1) {
                val trailingNonPojString = inputString.substring(matchResult.range.last + 1)
                stringBuilder.append(trailingNonPojString)
            }
        }

        return stringBuilder.toString()
    }

    fun convertLomajiUnicodeString(unicodeString: String, convertLomajiUnicodeStringCase: ConvertLomajiUnicodeStringCase): String {
        val matchSequence: Sequence<MatchResult>

        when (convertLomajiUnicodeStringCase) {
            ConvertLomajiUnicodeStringCase.CASE_KIPLMJ_UNICODE_TO_KIPLMJ_INPUT -> {
                matchSequence = LomajiSplitter.splitTailoUnicode(unicodeString)
            }

            ConvertLomajiUnicodeStringCase.CASE_POJ_UNICODE_TO_POJ_INPUT -> {
                matchSequence = LomajiSplitter.splitPojUnicode(unicodeString)
            }
        }

        val matchList = matchSequence.toList()
        val size = matchList.size

        if (size == 0) {
            return unicodeString
        }

        val stringBuilder = StringBuilder()

        var lastPos = 0
        for (i in 0 until size) {
            val matchResult: MatchResult = matchList[i]

            val nonPojString = unicodeString.substring(lastPos, matchResult.range.first)
            stringBuilder.append(nonPojString)

            val lomajiUnicode = unicodeString.substring(matchResult.range.first, matchResult.range.last + 1)

            when (convertLomajiUnicodeStringCase) {
                ConvertLomajiUnicodeStringCase.CASE_KIPLMJ_UNICODE_TO_KIPLMJ_INPUT -> {
                    val tailoInput = kiplmjUnicodeToKiplmjInput(lomajiUnicode)
                    stringBuilder.append(tailoInput)
                }

                ConvertLomajiUnicodeStringCase.CASE_POJ_UNICODE_TO_POJ_INPUT -> {
                    val pojInput = pojUnicodeToPojInput(lomajiUnicode)
                    stringBuilder.append(pojInput)
                }
            }

            lastPos = matchResult.range.last + 1

            if (i == size - 1) {
                val trailingNonPojString = unicodeString.substring(matchResult.range.last + 1)
                stringBuilder.append(trailingNonPojString)
            }
        }

        return stringBuilder.toString()
    }

    fun pojInputToPojUnicode(pojInput: String): String {
        val pojInputFix = pojInputToUnicodeFix(pojInput)

        if (pojInputFix.length > 1) {
            val lastCharString: String = pojInputFix.substring(pojInputFix.length - 1)
            if (!lastCharString.isNumeric()) {
                return pojInputFix
            } else if (lastCharString == "1" || lastCharString == "4") {
                return pojInputFix.substring(0, pojInputFix.length - 1)
            } else {
                val pojBoSianntiau: String = pojInputFix.substring(0, pojInputFix.length - 1)
                val soojiSianntiauString: String = lastCharString

                return convertPojInputBoSianntiauWithSoojiSianntiauToPojUnicode(pojInput, pojBoSianntiau, soojiSianntiauString)
            }
        }

        return pojInputFix
    }

    private fun convertPojInputBoSianntiauWithSoojiSianntiauToPojUnicode(pojInput: String, pojBoSianntiau: String, soojiSianntiauString: String): String {
        val pojSianntiauPosition = getPojSianntiauPosition(pojBoSianntiau)

        if (pojSianntiauPosition == null) {
            return pojBoSianntiau
        } else {
            val str1 = pojBoSianntiau.substring(0, pojSianntiauPosition.pos)

            val str2PojBosianntiau = pojBoSianntiau.substring(pojSianntiauPosition.pos, pojSianntiauPosition.pos + pojSianntiauPosition.length)
            val str2PojNumber = str2PojBosianntiau + soojiSianntiauString
            val str2 = Poj.sPojNumberToPojUnicodeHashMap[str2PojNumber]
            if (str2.isNullOrEmpty()) {
                println("Poj.sPojNumberToPojUnicodeHashMap not found: $pojInput, $str2PojNumber")
                throw PojUnicodeNotFoundException("Poj.sPojNumberToPojUnicodeHashMap[$str2PojNumber] not found")
            }

            val str3 = pojBoSianntiau.substring(pojSianntiauPosition.pos + pojSianntiauPosition.length, pojBoSianntiau.length)

//            println("$pojBoSianntiau$soojiSianntiauString -> $str1$str2$str3")

            return str1 + str2 + str3
        }
    }

//    private fun convertPojInputBoSianntiauWithSoojiSianntiauToPojUnicodeDeprecated(pojBoSianntiau: String, soojiSianntiauString: String): String {
//        val pojSianntiauPosition: Int = getPojSianntiauPosition(pojBoSianntiau)
//
//        if (pojSianntiauPosition == -1) {
//            return pojBoSianntiau
//        } else {
//            val str1 = pojBoSianntiau.substring(0, pojSianntiauPosition)
//
//            val pojNumber = pojBoSianntiau.substring(pojSianntiauPosition, pojSianntiauPosition + 1) + soojiSianntiauString
//            val str2 = Poj.sPojNumberToPojUnicodeHashMap[pojNumber]
//            if (str2.isNullOrEmpty()) {
//                throw PojUnicodeNotFoundException("Poj.sPojNumberToPojUnicodeHashMap[$pojNumber] not found")
//            }
//
//            val str3 = pojBoSianntiau.substring(pojSianntiauPosition + 1)
//
//            return str1 + str2 + str3
//        }
//    }

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

    fun isNotChoanKiplmjString(possibleKiplmjString: String): Boolean {
        val tailoInput = convertLomajiUnicodeString(possibleKiplmjString, ConvertLomajiUnicodeStringCase.CASE_KIPLMJ_UNICODE_TO_KIPLMJ_INPUT)
        return tailoInput.matches("[a-zA-Z0-9 -/.!?]+".toRegex()).not()
    }

    private fun isPojJipsiann(pojBoSianntiau: String): Boolean {
        val lastCharExcludingPhinnim = pojBoSianntiau.replace("ⁿ", "")
        return lastCharExcludingPhinnim.substring(lastCharExcludingPhinnim.length - 1).toLowerCase().matches("[ptkh]".toRegex())
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

    fun kiplmjInputToKiplmjUnicode(kiplmjInput: String): String {
        if (kiplmjInput.length > 1) {
            val lastCharString: String = kiplmjInput.substring(kiplmjInput.length - 1)
            if (!lastCharString.isNumeric()) {
                return kiplmjInput
            } else {
                val kiplmjBoSianntiau: String = kiplmjInput.substring(0, kiplmjInput.length - 1)
                val soojiSianntiauString: String = lastCharString

                return convertTailoInputBoSianntiauWithSoojiSianntiauToTailoUnicode(kiplmjBoSianntiau, soojiSianntiauString)
            }
        }

        return kiplmjInput
    }

    private fun convertTailoInputBoSianntiauWithSoojiSianntiauToTailoUnicode(kiplmjBoSianntiau: String, soojiSianntiauString: String): String {
        // Tone marks with this orders
        if (kiplmjBoSianntiau.contains("a")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "a")
        } else if (kiplmjBoSianntiau.contains("A")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "A")
        } else if (kiplmjBoSianntiau.contains("oo")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "o")
        } else if (kiplmjBoSianntiau.contains("Oo") || kiplmjBoSianntiau.contains("OO")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "O")
        } else if (kiplmjBoSianntiau.contains("e")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "e")
        } else if (kiplmjBoSianntiau.contains("E")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "E")
        } else if (kiplmjBoSianntiau.contains("o")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "o")
        } else if (kiplmjBoSianntiau.contains("O")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "O")
        } else if (kiplmjBoSianntiau.contains("iu") || kiplmjBoSianntiau.contains("Iu")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "u")
        } else if (kiplmjBoSianntiau.contains("IU")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "U")
        } else if (kiplmjBoSianntiau.contains("ui") || kiplmjBoSianntiau.contains("Ui")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "i")
        } else if (kiplmjBoSianntiau.contains("UI")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "I")
        } else if (kiplmjBoSianntiau.contains("i")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "i")
        } else if (kiplmjBoSianntiau.contains("I")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "I")
        } else if (kiplmjBoSianntiau.contains("u")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "u")
        } else if (kiplmjBoSianntiau.contains("U")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "U")
        } else if (kiplmjBoSianntiau.contains("ng")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "ng")
        } else if (kiplmjBoSianntiau.contains("Ng")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "Ng")
        } else if (kiplmjBoSianntiau.contains("NG")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "NG")
        } else if (kiplmjBoSianntiau.contains("m")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "m")
        } else if (kiplmjBoSianntiau.contains("M")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "M")
        } else if (kiplmjBoSianntiau.contains("n")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "n")
        } else if (kiplmjBoSianntiau.contains("N")) {
            return replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau, soojiSianntiauString, "N")
        } else {
            return kiplmjBoSianntiau
        }
    }

    private fun replaceKiplmjToKiplmjSianntiauUnicodeWithSoojiSianntiau(kiplmjBoSianntiau: String, soojiSiautiauStringString: String, replaceCharString: String): String {
        val kiplmjCharNumber = replaceCharString + soojiSiautiauStringString
        val kiplmjUnicode = KipLmj.sKiplmjNumberToKiplmjUnicodeHashMap[kiplmjCharNumber]
        return if (kiplmjUnicode != null) {
            kiplmjBoSianntiau.replaceFirst(replaceCharString, kiplmjUnicode, false)
        } else {
            kiplmjBoSianntiau
        }
    }
}