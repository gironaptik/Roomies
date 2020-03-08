package com.roomies.Model;

public class ChoreData {

    String by;
    String choreKind;
    String note;
    String date;
    String id;

    public ChoreData(){

    }

    public ChoreData(String by, String choreKind, String note, String date, String id) {
        this.by = by;
        this.choreKind = choreKind;
        this.note = note;
        this.date = date;
        this.id = id;
    }

    public String getBy() {
        return by;
    }

    public String getChoreKind() {
        return choreKind;
    }

    public String getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public void setChoreKind(String choreKind) {
        this.choreKind = choreKind;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setId(String id) {
        this.id = id;
    }
}
