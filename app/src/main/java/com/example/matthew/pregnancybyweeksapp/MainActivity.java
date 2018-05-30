package com.example.matthew.pregnancybyweeksapp;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar.LayoutParams;

import com.example.matthew.pregnancybyweeksapp.notifications.AlarmReceiver;
import com.example.matthew.pregnancybyweeksapp.notifications.WeeklyNotification;
import com.example.matthew.pregnancybyweeksapp.week_calculator.WeekCalculator;
import com.example.matthew.pregnancybyweeksapp.week_calculator.WeekCalculatorConception;
import com.example.matthew.pregnancybyweeksapp.week_calculator.WeekCalculatorDueDate;
import com.example.matthew.pregnancybyweeksapp.week_calculator.WeekCalculatorLMP;
import com.example.matthew.pregnancybyweeksapp.week_calculator.WeekSelectionFragment;
import com.example.matthew.pregnancybyweeksapp.week_menu.WeekListDialogFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements WeekSelectionFragment.OnAcceptClickListener {

    WeekListDialogFragment weekListDialogFragment;
    Bundle instanceState;
    NotificationManager notificationManager;
    WeekCalculator weekCalculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instanceState = savedInstanceState;

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationManager.createNotificationChannel(
                    new NotificationChannel(
                            "NotificationEx", "NotificationEx",
                            NotificationManager.IMPORTANCE_DEFAULT));

        //Inflate and setup custom action bar
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View view = layoutInflater.inflate(R.layout.custom_action_bar, null);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        //Set action bar and fill parent
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        getSupportActionBar().setCustomView(view, layoutParams);
        android.support.v7.widget.Toolbar parent = (android.support.v7.widget.Toolbar) view.getParent();
        parent.getContext().setTheme(R.style.CustomPopupStyle);
        parent.setContentInsetsAbsolute(0,0);

        //Get saved week preferences and open corresponding week
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int year = preferences.getInt("year", 0);
        int month = preferences.getInt("month", 0);
        int day = preferences.getInt("day", 0);
        int selectedType = preferences.getInt("selection type", 0);
        if(year == 0)
            openWeekSelectionFragment();
        else {
            weekCalculator = new WeekCalculatorLMP(year, month, day);
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
            if (weekCalculator.getCurrentWeek() < 4 || weekCalculator.getCurrentWeek() > 41){
                openWeekSelectionFragment();
            } else
                openWeekVideoFragment(weekCalculator.getCurrentWeek());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean notifications_enabled = preferences.getBoolean("Notifications Enabled", true);
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(notifications_enabled);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        switch(item.getItemId()) {
            case R.id.action_settings_set_week:
                openWeekSelectionFragment();
                break;
            case R.id.action_settings_reset_week:
                WeeklyNotification.cancelAlarm(this, AlarmReceiver.class);

                //Set week date preferences to 0
                editor.putInt("year", 0);
                editor.putInt("month", 0);
                editor.putInt("day", 0);
                editor.putInt("selection type", 0);
                editor.apply();
                openWeekSelectionFragment();
            case R.id.action_settings_notifications:
                if (item.isChecked()) {
                    WeeklyNotification.cancelAlarm(this, AlarmReceiver.class);
                    editor.putBoolean("Notifications Enabled", false);
                    item.setChecked(false);
                } else {
                    WeeklyNotification.setAlarmManager(this, AlarmReceiver.class, weekCalculator);
                    editor.putBoolean("Notifications Enabled", true);
                    item.setChecked(true);
                }
                editor.apply();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openWeekSelectionFragment() {
        WeekSelectionFragment fragment = new WeekSelectionFragment();
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().addToBackStack(null)
                .replace(R.id.constraintLayout, fragment, "week_select").commit();
    }

    public void onMenuClick(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        weekListDialogFragment = WeekListDialogFragment.newInstance();
        weekListDialogFragment.show(fragmentManager, "weeks");
    }

    public void openWeekVideoFragment(int weekNum) {
        WeekVideoAndDescriptionFragment fragment = new WeekVideoAndDescriptionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("weekNum", weekNum);
        fragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.constraintLayout, fragment).addToBackStack(null).commit();
        if (weekListDialogFragment != null)
            weekListDialogFragment.dismiss();
    }

    @Override
    public void setNotification(WeekCalculator weekCalculator) {
        WeeklyNotification.setAlarmManager(this, AlarmReceiver.class, weekCalculator);
    }
}
