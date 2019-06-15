package com.taccotap.phahtaigi.ime.converter

object ConverterUtils {

    fun fixLomajiNumber(lomajiNumber: String): String {
        val foundNumberIndex = findCorrectNumberIndex(lomajiNumber)

        if (foundNumberIndex == -1) {
            return lomajiNumber
        }

        var fixLomajiNumber = lomajiNumber.substring(0, foundNumberIndex + 1)

        val number = lomajiNumber.substring(foundNumberIndex, foundNumberIndex + 1)
        if (number == "1" || number == "4") {
            return fixLomajiNumber.substring(0, fixLomajiNumber.length - 1)
        } else if (number == "6") {
            return fixLomajiNumber.substring(0, fixLomajiNumber.length - 1) + "2"
        }

        return fixLomajiNumber
    }

    // ex: handle tai5566 -> tai5
    private fun findCorrectNumberIndex(lomajiNumber: String): Int {
        var foundNumberIndex = -1

        val count = lomajiNumber.length
        for (i in count - 1 downTo 0) {
            val c = lomajiNumber[i]
            if (Character.isDigit(c)) {
                foundNumberIndex = i
            } else {
                break
            }
        }

        return foundNumberIndex
    }
}
