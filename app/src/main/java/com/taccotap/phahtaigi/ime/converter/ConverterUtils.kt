package com.taccotap.phahtaigi.ime.converter

object ConverterUtils {

    fun fixLomajiNumber(lomajiNumber: String): String {
        val foundNumberIndex = findCorrectNumberIndex(lomajiNumber)
        return if (foundNumberIndex == -1) {
            lomajiNumber
        } else lomajiNumber.substring(0, foundNumberIndex + 1)

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
