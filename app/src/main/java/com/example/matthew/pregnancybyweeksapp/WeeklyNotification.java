package com.example.matthew.pregnancybyweeksapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.matthew.pregnancybyweeksapp.week_calculator.WeekCalculator;

import java.util.Calendar;

public class WeeklyNotification {

    private static final int ALARM_REQUEST_CODE = 100;

    public static void setAlarmManager(Context context, Class<?> cls, WeekCalculator weekCalculator) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DAY_OF_MONTH, weekCalculator.daysUntilNextWeek());
        calendar.set(Calendar.HOUR_OF_DAY, 6);

        //cancel previous alarms
        cancelAlarm(context, cls);

        //Enable receiver
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        }
    }

    public static void cancelAlarm(Context context,  Class<?> cls) {
        //Disable receiver
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        pendingIntent.cancel();
    }

    public static void showNotification(Context context, Class<?> cls, WeekCalculator weekCalculator) {
        int week = weekCalculator.getCurrentWeek();
        if (week > 3 && week < 42) {
            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.putExtra("weekNum", week);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(notificationIntent);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(ALARM_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder noteBuilder = new NotificationCompat.Builder(context, "Week Notification")
                    .setContentTitle("MedTwice Pregnancy by Weeks")
                    .setContentText("You have reached week  " + week + ". New video available.")
                    .setSmallIcon(R.mipmap.calendar_icon_round)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    notificationManager.createNotificationChannel(
                            new NotificationChannel(
                                    "Week Notification", "Week Notification",
                                    NotificationManager.IMPORTANCE_DEFAULT));
                notificationManager.notify(ALARM_REQUEST_CODE, noteBuilder.build());
            }
        }

        //Cancel notifications if outside of week range
        if (week < 4 || week >= 41) {
            cancelAlarm(context, cls);
        }
    }
}
