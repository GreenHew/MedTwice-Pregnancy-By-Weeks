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

    public void setWeek(int year, int  month, int day) {}

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

    void setYear(int year) {
        this.year = year;
    }

    void setMonth(int month) {
        this.month = month;
    }

    void setDay(int day) {
        this.day = day;
    }

    void setCurrentWeek(int currentWeek) {
        this.currentWeek = currentWeek;
    }
}
