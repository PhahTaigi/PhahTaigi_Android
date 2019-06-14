package com.taccotap.phahtaigi.dictmodel

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class ImeDict : RealmObject {
    @PrimaryKey
    var wordId: Int = 0

    @Index
    @Required
    lateinit var kiplmj: String

    @Index
    @Required
    lateinit var kiplmjInputWithNumberTone: String

    @Index
    @Required
    lateinit var kiplmjInputWithoutTone: String

    @Index
    @Required
    lateinit var kiplmjShortInput: String

    @Index
    @Required
    lateinit var poj: String

    @Index
    @Required
    lateinit var pojInputWithNumberTone: String

    @Index
    @Required
    lateinit var pojInputWithoutTone: String

    @Index
    @Required
    lateinit var pojShortInput: String

    var priority: Int = 0

    @Index
    @Required
    lateinit var hanji: String

    constructor() {}

    constructor(imeDict: ImeDict) {
        this.wordId = imeDict.wordId

        this.kiplmj = imeDict.kiplmj
        this.kiplmjInputWithNumberTone = imeDict.kiplmjInputWithNumberTone
        this.kiplmjInputWithoutTone = imeDict.kiplmjInputWithoutTone
        this.kiplmjShortInput = imeDict.kiplmjShortInput

        this.poj = imeDict.poj
        this.pojInputWithNumberTone = imeDict.pojInputWithNumberTone
        this.pojInputWithoutTone = imeDict.pojInputWithoutTone
        this.pojShortInput = imeDict.pojShortInput

        this.hanji = imeDict.hanji

        this.priority = imeDict.priority
    }
}