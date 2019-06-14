package com.taccotap.taigidictparser.tailo.parser.input

import android.os.Build
import androidx.annotation.RequiresApi

class SplitStringByCodePoint {
    companion object {
        @RequiresApi(Build.VERSION_CODES.N)
        fun split(str: String): ArrayList<String> {
            return splitImplement(str).toCollection(ArrayList())
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun splitImplement(str: String): Array<String> {
            val codepoints = str.codePoints().toArray()
            return Array(codepoints.size) { index ->
                String(codepoints, index, 1)
            }
        }
    }
}