package com.wat.student.adkoch.wattable.db.data.entities;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class Block {
    private int timeBlockNr;
    private String subjectName;
    private String subjectNameShort;
    private String director;
    private int blockNr;
    private String place;
    private String type;
    private Timestamp date;

    public int getTimeBlockNr() {
        return timeBlockNr;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectNameShort() {
        return subjectNameShort;
    }

    public String getDirector() {
        return director;
    }

    public int getBlockNr() {
        return blockNr;
    }

    public String getPlace() {
        return place;
    }

    public String getType() {
        return type;
    }

    public Timestamp getDate() {
        return date;
    }

    public Block(int timeBlockNr, String subjectName, String subjectNameShort, String director, int blockNr, String place, String type, Timestamp date) {
        this.timeBlockNr = timeBlockNr;
        this.subjectName = subjectName;
        this.subjectNameShort = subjectNameShort;
        this.director = director;
        this.blockNr = blockNr;
        this.place = place;
        this.type = type;
        this.date = date;
    }
    public Block(int timeBlockNr, Timestamp date){
        this.timeBlockNr = timeBlockNr;
        this.date = date;
        blockNr=-1;
        timeBlockNr=-1;
    }
    public String getReadableDate(){
        SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd");
        return sfd.format(date.toDate());
    }

    public Block(){

    }
}
