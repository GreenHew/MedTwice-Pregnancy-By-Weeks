package com.example.matthew.pregnancybyweeksapp.week_calculator;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class WeekCalculatorLMP extends WeekCalculator {

    public WeekCalculatorLMP(int year, int month, int day) {
        super(year, month, day);
    }

    @Override
    public int getCurrentWeek() {
        LocalDate dateLMP = LocalDate.of(getYear(), getMonth(), getDay());
        LocalDate currentDate = LocalDate.now();
        int week = (int) (DAYS.between(dateLMP, currentDate) / 7);
        setCurrentWeek(week);
        return week;
    }

    @Override
    public void setWeek(int year, int month, int day) {
        setYear(year);
        setMonth(month);
        setDay(day);
    }
}
