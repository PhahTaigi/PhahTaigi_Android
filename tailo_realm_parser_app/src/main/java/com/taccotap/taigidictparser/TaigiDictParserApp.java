package com.taccotap.taigidictparser;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class TaigiDictParserApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initRealm();
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().build();
//        Realm.deleteRealm(realmConfig); // Delete Realm between app restarts.
        Realm.setDefaultConfiguration(realmConfig);
    }
}
