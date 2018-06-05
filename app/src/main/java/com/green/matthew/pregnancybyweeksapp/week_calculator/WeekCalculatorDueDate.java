package com.green.matthew.pregnancybyweeksapp.week_calculator;

import java.time.LocalDate;
import java.util.Calendar;

import static java.time.temporal.ChronoUnit.DAYS;

public class WeekCalculatorDueDate extends WeekCalculator {

    public WeekCalculatorDueDate(int year, int month, int day) {
        super(year, month, day);
    }

    @Override
    public int getCurrentWeek() {
        LocalDate dateLMP = LocalDate.of(getYear(), getMonth(), getDay());
        LocalDate currentDate = LocalDate.now();
        int week = (int) (40 - (DAYS.between(currentDate, dateLMP) / 7));
        setCurrentWeek(week);
        return week;
    }

    @Override
    public int getDaysUntilNextWeek() {
        LocalDate lastWeek = LocalDate.of(getYear(), getMonth(), getDay());
        lastWeek.plusDays(7); //Move date from week 40 to week 41
        LocalDate currentDate = LocalDate.now();
        return (int) (7 - (DAYS.between(currentDate, lastWeek) % 7));

    }
}
