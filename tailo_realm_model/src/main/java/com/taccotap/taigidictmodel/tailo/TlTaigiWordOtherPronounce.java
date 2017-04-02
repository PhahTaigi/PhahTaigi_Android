package com.taccotap.taigidictmodel.tailo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TlTaigiWordOtherPronounce extends RealmObject {

    @PrimaryKey
    private int index;

    private int mainCode;

    private String lomaji;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getMainCode() {
        return mainCode;
    }

    public void setMainCode(int mainCode) {
        this.mainCode = mainCode;
    }

    public String getLomaji() {
        return lomaji;
    }

    public void setLomaji(String lomaji) {
        this.lomaji = lomaji;
    }
}
