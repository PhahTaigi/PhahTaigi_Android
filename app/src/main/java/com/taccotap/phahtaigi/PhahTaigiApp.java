package com.taccotap.phahtaigi;

import android.app.Application;
import android.content.ContextWrapper;

import androidx.multidex.MultiDexApplication;

import com.pixplicity.easyprefs.library.Prefs;
import com.taccotap.chhoetaigi.realm_db_installer.IRealmAssetHelperStorageListener;
import com.taccotap.chhoetaigi.realm_db_installer.RealmAssetHelper;
import com.taccotap.chhoetaigi.realm_db_installer.RealmAssetHelperStatus;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class PhahTaigiApp extends MultiDexApplication {

    public static final String DATABASE_ASSETS_PATH = "preload_realm_db";
    public static final String DATABASE_BASE_NAME = "PhahTaigiImeDict";

    @Override
    public void onCreate() {
        super.onCreate();

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        Realm.init(this);
        RealmAssetHelper.getInstance(this).loadDatabaseToStorage(DATABASE_ASSETS_PATH, DATABASE_BASE_NAME, new IRealmAssetHelperStorageListener() {
            @Override
            public void onLoadedToStorage(String realmDbName, RealmAssetHelperStatus realmAssetHelperStatus) {
                RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                        .name(realmDbName)
                        .build();
                Realm.setDefaultConfiguration(realmConfig);
            }
        });
    }
}
