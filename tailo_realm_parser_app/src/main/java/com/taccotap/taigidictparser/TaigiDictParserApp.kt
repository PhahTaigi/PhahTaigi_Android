package com.taccotap.taigidictparser

import android.app.Application

import io.realm.Realm
import io.realm.RealmConfiguration

class TaigiDictParserApp : Application() {

    override fun onCreate() {
        super.onCreate()

        initRealm()
    }

    private fun initRealm() {
        Realm.init(this)
        val realmConfig = RealmConfiguration.Builder().build()
        //        Realm.deleteRealm(realmConfig); // Delete Realm between app restarts.
        Realm.setDefaultConfiguration(realmConfig)
    }
}
