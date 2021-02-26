package com.ost.Noted;

import java.util.Date;

public class NoteModel {
    private String text;
    private String ts; // created at timestamp
    public NoteModel() {

    }

    public NoteModel(String text) {
        this.text = text;
        Long ts = System.currentTimeMillis() / 1000;
        this.ts = ts.toString();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }
}
