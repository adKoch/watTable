package com.wat.student.adkoch.wattable.db.handlers.week;

import android.content.Context;

import com.wat.student.adkoch.wattable.R;

class WeekDateContainer {

    private String[] daysOfTheWeek;
    private int month;
    private int day;
    private int dayOfTheWeek;

    WeekDateContainer(int month, int day, int dayOfTheWeek, Context context) {
        this.month = month;
        this.day = day;
        this.dayOfTheWeek = dayOfTheWeek;
        daysOfTheWeek = context.getResources().getStringArray(R.array.daysOfTheWeekShort);
    }

    int getMonth() {
        return month;
    }

    int getDay() {
        return day;
    }

    String getDayOfTheWeek(){
        return daysOfTheWeek[dayOfTheWeek];
    }
}
