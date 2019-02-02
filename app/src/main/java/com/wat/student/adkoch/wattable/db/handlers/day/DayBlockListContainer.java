package com.wat.student.adkoch.wattable.db.handlers.day;

import com.wat.student.adkoch.wattable.db.data.entities.Block;

import java.util.ArrayList;
import java.util.Objects;

public class DayBlockListContainer {

    private ArrayList<Block> blockList;
    private String[] titles;
    private int month;
    private int day;

    public DayBlockListContainer(int month, int day){
        titles = new String[7];
        blockList = new ArrayList<>(7);
        this.month=month;
        this.day=day;
        for(int i=0;i<7;i++){
            blockList.add(new Block());
            titles[i] = "";
        }
    }

    public boolean isEmpty(){
        for(String title:titles){
            if(Objects.equals("",title)){
                return false;
            }
        }
        return true;
    }

    public void put(Block b, String title){
        int index=checkTitles(title);
        if(index==0) add(b,title);
        else {
            if(b.getDay()!=day || b.getMonth()!=month){
                titles[index]="";
                blockList.set(index,new Block());
            }
        }
    }

    private void add(Block b, String title){
        if(b.getTimeBlockNr()>7 || b.getTimeBlockNr()<1) return;
        titles[b.getTimeBlockNr()-1] = title;
        blockList.set(b.getTimeBlockNr()-1,b);
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
        return blockList.get(index);
    }
    public ArrayList<Block> getBlockList(){
        return blockList;
    }
}
