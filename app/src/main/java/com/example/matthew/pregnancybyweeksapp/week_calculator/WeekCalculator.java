package com.example.matthew.pregnancybyweeksapp.week_calculator;

public class WeekCalculator {
    private int year;
    private int month;
    private int day;
    private int currentWeek;

    WeekCalculator(int year, int month, int day) {
        this.year = year;
        this.month = month + 1;
        this.day = day;
        currentWeek = 0;
    }

    int getYear() {
        return year;
    }

    int getMonth() {
        return month;
    }

    int getDay() {
        return day;
    }

    public int getCurrentWeek() {
        return currentWeek;
    }

    public int daysUntilNextWeek() {return 0;}

    void setCurrentWeek(int currentWeek) {
        this.currentWeek = currentWeek;
    }
}
