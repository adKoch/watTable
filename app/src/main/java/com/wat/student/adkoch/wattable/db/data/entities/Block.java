package com.wat.student.adkoch.wattable.db.data.entities;

import java.io.Serializable;

public final class Block implements Serializable {
    private int timeBlockNr;
    private String subjectName;
    private String subjectNameShort;
    private String director;
    private int blockNr;
    private String place;
    private String type;
    private int part;
    private int month;
    private int day;
    private int noteCount;

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

    public int getPart(){ return part;}

    public int getMonth() { return month;}

    public int getDay() { return day;}

    public int getNoteCount(){
        return noteCount;
    }

    public Block(int timeBlockNr, int blockNr, int part, int month, int day, String director, String place, String subjectName, String subjectNameShort, String type, int noteCount) {
        this.timeBlockNr = timeBlockNr;
        this.subjectName = subjectName;
        this.subjectNameShort = subjectNameShort;
        this.director = director;
        this.blockNr = blockNr;
        this.place = place;
        this.type = type;
        this.part=part;
        this.month=month;
        this.day=day;
        this.noteCount=noteCount;
    }
    public Block(){

    }
}
