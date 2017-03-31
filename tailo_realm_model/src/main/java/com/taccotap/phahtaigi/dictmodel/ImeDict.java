package com.taccotap.phahtaigi.dictmodel;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class ImeDict extends RealmObject {
    @PrimaryKey
    private int wordId;

    private int mainCode;

    private int wordPropertyCode;

    @Index
    @Required
    private String tailo;

    @Index
    @Required
    private String tailoInputWithNumberTone;

    @Index
    @Required
    private String tailoInputWithoutTone;

    @Index
    @Required
    private String poj;

    @Index
    @Required
    private String pojInputWithNumberTone;

    @Index
    @Required
    private String pojInputWithoutTone;

    private long priority;

    @Index
    @Required
    private String hanji;

    public ImeDict() {
    }

    public ImeDict(ImeDict imeDict) {
        this.wordId = imeDict.getWordId();
        this.mainCode = imeDict.getMainCode();
        this.wordPropertyCode = imeDict.getWordPropertyCode();
        this.tailo = imeDict.getTailo();
        this.tailoInputWithNumberTone = imeDict.getTailoInputWithNumberTone();
        this.tailoInputWithoutTone = imeDict.getTailoInputWithoutTone();
        this.poj = imeDict.getPoj();
        this.pojInputWithNumberTone = imeDict.getPojInputWithNumberTone();
        this.pojInputWithoutTone = imeDict.getPojInputWithoutTone();
        this.hanji = imeDict.getHanji();
        this.priority = imeDict.getPriority();
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

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

    public String getTailo() {
        return tailo;
    }

    public void setTailo(String tailo) {
        this.tailo = tailo;
    }

    public String getTailoInputWithNumberTone() {
        return tailoInputWithNumberTone;
    }

    public void setTailoInputWithNumberTone(String tailoInputWithNumberTone) {
        this.tailoInputWithNumberTone = tailoInputWithNumberTone;
    }

    public String getTailoInputWithoutTone() {
        return tailoInputWithoutTone;
    }

    public void setTailoInputWithoutTone(String tailoInputWithoutTone) {
        this.tailoInputWithoutTone = tailoInputWithoutTone;
    }

    public String getPoj() {
        return poj;
    }

    public void setPoj(String poj) {
        this.poj = poj;
    }

    public String getPojInputWithNumberTone() {
        return pojInputWithNumberTone;
    }

    public void setPojInputWithNumberTone(String pojInputWithNumberTone) {
        this.pojInputWithNumberTone = pojInputWithNumberTone;
    }

    public String getPojInputWithoutTone() {
        return pojInputWithoutTone;
    }

    public void setPojInputWithoutTone(String pojInputWithoutTone) {
        this.pojInputWithoutTone = pojInputWithoutTone;
    }

    public String getHanji() {
        return hanji;
    }

    public void setHanji(String hanji) {
        this.hanji = hanji;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }
}