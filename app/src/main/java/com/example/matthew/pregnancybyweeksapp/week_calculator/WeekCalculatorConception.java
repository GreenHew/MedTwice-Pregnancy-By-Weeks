package com.example.matthew.pregnancybyweeksapp.week_calculator;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class WeekCalculatorConception extends WeekCalculator{

    public WeekCalculatorConception(int year, int month, int day) {
        super(year, month, day);
    }

    @Override
    public int getCurrentWeek() {
        LocalDate dateLMP = LocalDate.of(getYear(), getMonth(), getDay());
        LocalDate currentDate = LocalDate.now();
        int week = (int) (DAYS.between(dateLMP, currentDate) / 7) - 2;
        setCurrentWeek(week);
        return week;
    }
}
