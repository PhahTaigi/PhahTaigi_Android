package com.taccotap.chhoetaigi.realm_db_installer;

@SuppressWarnings("WeakerAccess")
public interface IRealmAssetHelperStorageListener {

    @SuppressWarnings("UnusedParameters")
    void onLoadedToStorage(String filePath, RealmAssetHelperStatus status);
}
