package com.taccotap.taigidictmodel.kip

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class KipTaigiSuKithannKonghoat : RealmObject() {

    @PrimaryKey
    var index: Int = 0

    var mainCode: Int = 0

    lateinit var lomaji: String
}
