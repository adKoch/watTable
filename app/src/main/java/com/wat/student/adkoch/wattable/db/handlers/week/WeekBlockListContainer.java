package com.wat.student.adkoch.wattable.db.handlers.week;

import android.content.Context;

import com.wat.student.adkoch.wattable.db.data.entities.Block;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeekBlockListContainer {

    private final List<Block> blocks;
    private final List<WeekDateContainer> days;
    private final int count;
    private final Calendar startDate;

    public List<Block> getBlocks(){
        return blocks;
    }
    public List<WeekDateContainer> getDays(){
        return days;
    }


    public WeekBlockListContainer(Date startDate, Date endDate, Context context){

        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        this.startDate=startCal;

        count=getDaysCountBetween(startDate,endDate)+1;

        blocks=new ArrayList<>(count*7);
        days=new ArrayList<>(count);

        for(int i=0;i<count*7;i++){
            blocks.add(new Block());
        }

        while(!start.after(end)){
            int month=start.get(Calendar.MONTH)+1;
            int day=start.get(Calendar.DAY_OF_MONTH);
            days.add(new WeekDateContainer(month,day,start.get(Calendar.DAY_OF_WEEK)-1, context));
            start.add(Calendar.DATE,1);
        }

    }

    public void put(Block b){
        blocks.set(getBlockDayIndex(b)*7+b.getTimeBlockNr()-1,b);
    }

    private int getBlockDayIndex(Block b){

        Calendar bDate;
        bDate=Calendar.getInstance();
        if(b.getMonth()<startDate.get(Calendar.MONTH)+1){
            bDate.set(Calendar.YEAR,startDate.get(Calendar.YEAR)+1);
        } else {
            bDate.set(Calendar.YEAR,startDate.get(Calendar.YEAR));
        }
        bDate.set(Calendar.MONTH,b.getMonth()-1);
        bDate.set(Calendar.DAY_OF_MONTH,b.getDay());

        return getDaysCountBetween(startDate.getTime(),bDate.getTime());
    }

    private int getDaysCountBetween(Date d1, Date d2){
        return (int) ((d2.getTime()-d1.getTime())/(1000*60*60*24));
    }

    public int getDayIndex(Date date){
        int days=getDaysCountBetween(startDate.getTime(),date);
        if(days+7<count) days+=7;
        return days;
    }

}
