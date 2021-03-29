package com.ost.noted;

public class NoteModel {
    private String text;
    private String fileUrl;
    private long noteType;
    private String ts; // created at timestamp
    public NoteModel() {

    }

    public NoteModel(String text, long noteType) {
        this.text = text;
        Long ts = System.currentTimeMillis() / 1000;
        this.ts = ts.toString();
        this.fileUrl = null;
        this.noteType = noteType;
    }

    public NoteModel(String text, long noteType, String fileUrl) {
        this.text = text;
        Long ts = System.currentTimeMillis() / 1000;
        this.ts = ts.toString();
        this.noteType = noteType;
        this.fileUrl = fileUrl;
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getNoteType() {
        return noteType;
    }

    public void setNoteType(long noteType) {
        this.noteType = noteType;
    }
}
