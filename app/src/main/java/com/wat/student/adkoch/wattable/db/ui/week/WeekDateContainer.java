package com.wat.student.adkoch.wattable.db.ui.week;

public class WeekDateContainer {

    private static final String[] daysOfTheWeek = { "Niedziela", "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota"};
    private int month;
    private int day;
    private int dayOfTheWeek;

    public WeekDateContainer(int month, int day, int dayOfTheWeek) {
        this.month = month;
        this.day = day;
        this.dayOfTheWeek = dayOfTheWeek;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getDayOfTheWeekNumber() {
        return dayOfTheWeek;
    }

    public String getDayOfTheWeek(){
        return daysOfTheWeek[dayOfTheWeek];
    }
}
