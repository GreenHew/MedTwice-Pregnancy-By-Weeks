package com.example.matthew.pregnancybyweeksapp.week_calculator;

import java.time.LocalDate;

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
}
