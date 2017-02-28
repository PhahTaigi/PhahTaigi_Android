package com.taccotap.taigidictmodel.tailo;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class TlTaigiWord extends RealmObject {
    @PrimaryKey
    private int mainCode;

    private int wordPropertyCode;

    @Index
    @Required
    private String hanji;

    @Index
    @Required
    private String lomaji;

    public int getMainCode() {
        return mainCode;
    }

    public void setMainCode(int mainCode) {
        this.mainCode = mainCode;
    }

    public int getWordPropertyCode() {
        return wordPropertyCode;
    }

    public void setWordPropertyCode(int wordPropertyCode) {
        this.wordPropertyCode = wordPropertyCode;
    }

    public String getHanji() {
        return hanji;
    }

    public void setHanji(String hanji) {
        this.hanji = hanji;
    }

    public String getLomaji() {
        return lomaji;
    }

    public void setLomaji(String lomaji) {
        this.lomaji = lomaji;
    }
}
