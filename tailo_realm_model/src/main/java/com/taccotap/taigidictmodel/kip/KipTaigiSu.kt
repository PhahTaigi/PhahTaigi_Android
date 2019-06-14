package com.taccotap.taigidictmodel.kip

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class KipTaigiSu : RealmObject() {
    @PrimaryKey
    var mainCode: Int = 0

    var wordPropertyCode: Int = 0

    @Index
    @Required
    lateinit var hanji: String

    @Index
    @Required
    lateinit var lomaji: String
}
