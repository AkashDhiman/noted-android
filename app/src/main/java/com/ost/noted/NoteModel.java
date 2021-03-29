package com.ost.noted;

public class NoteModel {
    private String text;
    private String image;
    private long noteType;
    private String ts; // created at timestamp
    public NoteModel() {

    }

    public NoteModel(String text, long noteType) {
        this.text = text;
        Long ts = System.currentTimeMillis() / 1000;
        this.ts = ts.toString();
        this.image = null;
        this.noteType = noteType;
    }

    public NoteModel(String text, long noteType, String image) {
        this.text = text;
        Long ts = System.currentTimeMillis() / 1000;
        this.ts = ts.toString();
        this.image = "https://picsum.photos/500/300";
        this.noteType = noteType;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getNoteType() {
        return noteType;
    }

    public void setNoteType(long noteType) {
        this.noteType = noteType;
    }
}
