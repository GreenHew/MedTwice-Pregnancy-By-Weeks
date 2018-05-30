package com.example.matthew.pregnancybyweeksapp.week_calculator;

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


//        Calendar calendar = Calendar.getInstance();
//        calendar.set(getYear(), getMonth(), getDay());
//        WeekCalculatorConception nextWeekCalculator;
//        int i = 1;
//        for (; i <= 7; i++) {
//            calendar.add(Calendar.DAY_OF_MONTH, 1);
//            nextWeekCalculator = new WeekCalculatorConception(
//                    calendar.get(Calendar.YEAR),
//                    calendar.get(Calendar.MONTH),
//                    calendar.get(Calendar.DAY_OF_MONTH));
//            if (nextWeekCalculator.getCurrentWeek() > getCurrentWeek())
//                break;
//        }
//        return i;
    }
}
