package com.green.matthew.pregnancybyweeksapp.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.green.matthew.pregnancybyweeksapp.MainActivity;
import com.green.matthew.pregnancybyweeksapp.week_calculator.WeekCalculator;
import com.green.matthew.pregnancybyweeksapp.week_calculator.WeekCalculatorConception;
import com.green.matthew.pregnancybyweeksapp.week_calculator.WeekCalculatorDueDate;
import com.green.matthew.pregnancybyweeksapp.week_calculator.WeekCalculatorLMP;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean notificationEnabled = preferences.getBoolean("Notifications Enabled", true);
        if (!notificationEnabled)
            return;
        int year = preferences.getInt("year", 0);
        int month = preferences.getInt("month", 0);
        int day = preferences.getInt("day", 0);
        int selectedType = preferences.getInt("selection type", 0);
        WeekCalculator weekCalculator = new WeekCalculatorLMP(year, month, day);
        switch (selectedType) {
            case 0:
                weekCalculator = new WeekCalculatorLMP(year, month, day);
                break;
            case 1:
                weekCalculator = new WeekCalculatorConception(year, month, day);
                break;
            case 2:
                weekCalculator = new WeekCalculatorDueDate(year, month, day);
                break;
        }

        if (Intent.ACTION_BOOT_COMPLETED.equalsIgnoreCase(intent.getAction())) {
            WeeklyNotification.setAlarmManager(context, AlarmReceiver.class, weekCalculator);
            return;
        }
        WeeklyNotification.showNotification(context, MainActivity.class, weekCalculator);
    }
}
