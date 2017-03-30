package com.taccotap.phahtaigi;

import android.app.Application;
import android.content.ContextWrapper;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.eggheadgames.realmassethelper.IRealmAssetHelperStorageListener;
import com.eggheadgames.realmassethelper.RealmAssetHelper;
import com.eggheadgames.realmassethelper.RealmAssetHelperStatus;
import com.pixplicity.easyprefs.library.Prefs;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class PhahTaigiApp extends Application {

    public static final String DATABASE_ASSETS_PATH = "preload_realm_db";
    public static final String DATABASE_BASE_NAME = "ime_dict";

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            // Set up Crashlytics, disabled for debug builds
            Crashlytics crashlyticsKit = new Crashlytics.Builder()
                    .core(new CrashlyticsCore.Builder().disabled(true).build())
                    .build();

            // Initialize Fabric with the debug-disabled crashlytics.
            Fabric.with(this, crashlyticsKit);
        } else {
            Fabric.with(this, new Crashlytics());
        }

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
