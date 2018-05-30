package com.example.matthew.pregnancybyweeksapp.week_calculator;

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
        int days = (int) (7 - (DAYS.between(currentDate, lastWeek) % 7));
        return days;

//        Calendar calendar = Calendar.getInstance();
//        calendar.set(getYear(), getMonth(), getDay());
//        WeekCalculatorDueDate nextWeekCalculator;
//        int i = 1;
//        for (; i <= 7; i++) {
//            calendar.add(Calendar.DAY_OF_MONTH, -1);
//            nextWeekCalculator = new WeekCalculatorDueDate(
//                    calendar.get(Calendar.YEAR),
//                    calendar.get(Calendar.MONTH),
//                    calendar.get(Calendar.DAY_OF_MONTH));
//            if (nextWeekCalculator.getCurrentWeek() > getCurrentWeek())
//                break;
//        }
//        return i;
    }
}
