package com.wat.student.adkoch.wattable.db.ui.day;

import com.wat.student.adkoch.wattable.db.data.entities.Block;

import java.util.ArrayList;
import java.util.Objects;

public class DayBlocklistContainer {

    private ArrayList<Block> blocklist;
    private String[] titles;
    private int month;
    private int day;

    public DayBlocklistContainer(int month, int day){
        titles = new String[7];
        blocklist = new ArrayList<>(7);
        this.month=month;
        this.day=day;
        for(int i=0;i<7;i++){
            blocklist.add(new Block());
            titles[i] = "";
        }
    }

    public boolean isEmpty(){
        for(int i=0;i<titles.length;i++){
            if(!Objects.equals("",titles[i])) return false;
        }
        return true;
    }

    public void put(Block b, String title){
        int index=checkTitles(title);
        if(index==0) add(b,title);
        else {
            if(b.getDay()!=day || b.getMonth()!=month){
                titles[index]="";
                blocklist.set(index,new Block());
            }
        }
    }

    private void add(Block b, String title){
        if(b.getTimeBlockNr()>7 || b.getTimeBlockNr()<1) return;
        titles[b.getTimeBlockNr()-1] = title;
        blocklist.set(b.getTimeBlockNr()-1,b);
    }

    private int checkTitles(String title){
        for(int i=0;i<titles.length;i++){
            if(Objects.equals(title,titles[i])){
                return i;
            }
        }
        return 0;
    }

    public Block getBlock(int index){
        return blocklist.get(index);
    }
    public ArrayList<Block> getBlocklist(){
        return blocklist;
    }
}
