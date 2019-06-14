package com.taccotap.taigidictparser.tailo.parser

fun String.isNumeric(): Boolean = this.matches("-?\\d+(\\.\\d+)?".toRegex())