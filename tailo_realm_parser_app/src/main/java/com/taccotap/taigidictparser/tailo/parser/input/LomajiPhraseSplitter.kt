package com.taccotap.taigidictparser.tailo.parser.input

import java.util.*

class LomajiPhraseSplitter {

    inner class LomajiPhraseSplitterResult {
        var splitStrings = ArrayList<String>()
            set(splitStrings) {
                this.splitStrings.clear()
                this.splitStrings.addAll(splitStrings)
            }
        var splitSperators = ArrayList<String>()
            set(splitSperators) {
                this.splitSperators.clear()
                this.splitSperators.addAll(splitSperators)
            }
    }

    fun split(lomajiPhrase: String): LomajiPhraseSplitterResult {
        val splitStrings = ArrayList<String>()
        val splitSperators = ArrayList<String>()

        val lomajiPhraseSplitterResult = LomajiPhraseSplitterResult()

        val count = lomajiPhrase.length
        var startIndex = 0
        var i = 0
        while (i < count) {
            val charString = lomajiPhrase.substring(i, i + 1)
            //            Log.d(TAG, "charString = " + charString);

            var splitSperator: String? = null

            if (charString == " ") {
                splitSperator = " "
            } else if (charString == "-") {
                if (i + 2 < count && lomajiPhrase.substring(i + 1, i + 2) == "-") {
                    splitSperator = "--"
                } else {
                    splitSperator = "-"
                }
            }

            if (splitSperator != null) {
                val splitString = lomajiPhrase.substring(startIndex, i)

                splitStrings.add(splitString)
                splitSperators.add(splitSperator)

                startIndex += splitString.length + splitSperator.length
                //                Log.d(TAG, "startIndex = " + startIndex + ", splitString=" + splitString + ", splitSperator=\"" + splitSperator + "\"");

                if (splitSperator.length > 1) {
                    i += splitSperator.length - 1
                }
            } else {
                if (i == count - 1) {
                    val splitString = lomajiPhrase.substring(startIndex)
                    splitStrings.add(splitString)
                }
            }
            i++
        }

        lomajiPhraseSplitterResult.splitStrings = splitStrings
        lomajiPhraseSplitterResult.splitSperators = splitSperators

        return lomajiPhraseSplitterResult
    }

    companion object {
        private val TAG = LomajiPhraseSplitter::class.java.simpleName
    }
}
