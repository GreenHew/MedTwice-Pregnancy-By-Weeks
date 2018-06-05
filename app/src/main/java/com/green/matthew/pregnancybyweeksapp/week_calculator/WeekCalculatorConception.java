package com.green.matthew.pregnancybyweeksapp.week_calculator;

import java.time.LocalDate;
import java.util.Calendar;

import static java.time.temporal.ChronoUnit.DAYS;

public class WeekCalculatorConception extends WeekCalculator{

    public WeekCalculatorConception(int year, int month, int day) {
        super(year, month, day);
    }

    @Override
    public int getCurrentWeek() {
        LocalDate dateConception = LocalDate.of(getYear(), getMonth(), getDay());
        LocalDate currentDate = LocalDate.now();
        int week = (int) (DAYS.between(dateConception, currentDate) / 7) - 2;
        setCurrentWeek(week);
        return week;
    }

    @Override
    public int getDaysUntilNextWeek() {
        LocalDate lastWeek = LocalDate.of(getYear(), getMonth(), getDay());
        lastWeek.plusDays(39 * 7); //Move date from week 2 to week 41
        LocalDate currentDate = LocalDate.now();
        return (int) (7 - (DAYS.between(lastWeek, currentDate) % 7));

    }
}
