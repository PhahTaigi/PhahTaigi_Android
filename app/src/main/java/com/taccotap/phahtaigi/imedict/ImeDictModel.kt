package com.taccotap.phahtaigi.imedict

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class ImeDictModel : RealmObject {
    @PrimaryKey
    var wordId: Int = 0

    @Required
    lateinit var kip: String

    @Index
    @Required
    lateinit var kipSujip: String

    @Index
    @Required
    lateinit var kipSujipBoSooji: String

    @Index
    @Required
    lateinit var kipSujipThauJibo: String

    @Required
    lateinit var poj: String

    @Index
    @Required
    lateinit var pojSujip: String

    @Index
    @Required
    lateinit var pojSujipBoSooji: String

    @Index
    @Required
    lateinit var pojSujipThauJibo: String

    @Index
    var pojPriority: Int = 0

    @Index
    var kipPriority: Int = 0

    @Required
    lateinit var hanji: String

    var srcDict: Int = 0

    constructor() {}

    constructor(imeDictModel: ImeDictModel) {
        this.wordId = imeDictModel.wordId

        this.poj = imeDictModel.poj
        this.pojSujip = imeDictModel.pojSujip
        this.pojSujipBoSooji = imeDictModel.pojSujipBoSooji
        this.pojSujipThauJibo = imeDictModel.pojSujipThauJibo

        this.kip = imeDictModel.kip
        this.kipSujip = imeDictModel.kipSujip
        this.kipSujipBoSooji = imeDictModel.kipSujipBoSooji
        this.kipSujipThauJibo = imeDictModel.kipSujipThauJibo

        this.hanji = imeDictModel.hanji

        this.pojPriority = imeDictModel.pojPriority
        this.kipPriority = imeDictModel.kipPriority

        this.srcDict = imeDictModel.srcDict
    }
}