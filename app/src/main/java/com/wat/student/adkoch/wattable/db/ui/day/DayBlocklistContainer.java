package com.wat.student.adkoch.wattable.db.ui.day;

import com.wat.student.adkoch.wattable.db.data.entities.Block;

import java.util.ArrayList;

public class DayBlocklistContainer {

    private ArrayList<Block> blocklist;

    public DayBlocklistContainer(){
        blocklist = new ArrayList<>(7);
        for(int i=0;i<7;i++){
            blocklist.add(new Block());
        }
    }

    public void put(Block b){
        if(b.getTimeBlockNr()>7 || b.getTimeBlockNr()<1) return;
        blocklist.set(b.getTimeBlockNr()-1,b);
    }

    public ArrayList<Block> getBlocklist(){
        return blocklist;
    }
}
