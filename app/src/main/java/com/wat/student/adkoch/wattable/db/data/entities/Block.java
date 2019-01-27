package com.wat.student.adkoch.wattable.db.data.entities;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public final class Block {
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


    /*public Block(int timeBlockNr, String subjectName, String subjectNameShort, String director, int blockNr, String place, String type, Timestamp date) {
        this.timeBlockNr = timeBlockNr;
        this.subjectName = subjectName;
        this.subjectNameShort = subjectNameShort;
        this.director = director;
        this.blockNr = blockNr;
        this.place = place;
        this.type = type;
        this.date = date;
    }*/

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
   /* public Block(int timeBlockNr, Timestamp date){
        this.timeBlockNr = timeBlockNr;
        this.date = date;
        blockNr=-1;
        timeBlockNr=-1;
    }*/
    public Block(String subjectName, String type, int blockNr, String place, int month, int day,int timeBlockNr){

        String shortName="";
        String[] part = subjectName.split(" ");
        for (int i = 0; i < part.length; i++) {
            String s = part[i];
            shortName=shortName+s.charAt(0);
        }
        this.subjectNameShort=shortName;
        this.subjectName=subjectName;
        this.type=type;
        this.blockNr=blockNr;
        this.place=place;
        this.month=month;
        this.day=day;
        this.timeBlockNr=timeBlockNr;
        this.director="";
        if(month<9) this.part=2;
        else { this.part=1; }
    }


    public Block(){

    }
}
