package com.wat.student.adkoch.wattable.db.handlers.week;

class WeekDateContainer {

    private static final String[] daysOfTheWeek = { "Nd", "Pon", "Wt", "Śr", "Czw", "Pt", "Sb"};
    private int month;
    private int day;
    private int dayOfTheWeek;

    WeekDateContainer(int month, int day, int dayOfTheWeek) {
        this.month = month;
        this.day = day;
        this.dayOfTheWeek = dayOfTheWeek;
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
