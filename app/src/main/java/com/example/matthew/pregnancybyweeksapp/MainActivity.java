package com.example.matthew.pregnancybyweeksapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.Window;
import android.view.WindowManager;

import com.example.matthew.pregnancybyweeksapp.notifications.AlarmReceiver;
import com.example.matthew.pregnancybyweeksapp.notifications.WeeklyNotification;
import com.example.matthew.pregnancybyweeksapp.week_calculator.WeekCalculator;
import com.example.matthew.pregnancybyweeksapp.week_calculator.WeekCalculatorConception;
import com.example.matthew.pregnancybyweeksapp.week_calculator.WeekCalculatorDueDate;
import com.example.matthew.pregnancybyweeksapp.week_calculator.WeekCalculatorLMP;
import com.example.matthew.pregnancybyweeksapp.week_calculator.WeekSelectionFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements WeekSelectionFragment.OnAcceptClickListener {

    Bundle instanceState;
    NotificationManager notificationManager;
    WeekCalculator weekCalculator;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

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

        //Set toolbar and add drawer button
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setPopupTheme(R.style.CustomPopupStyle);
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black);
        actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));

        //Set status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.colorPrimaryDark));

        //Initialize navigation drawer and set submenus and items
        navigationView = findViewById(R.id.nav_view);
        final Menu menu = navigationView.getMenu();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int weekNum = Integer.parseInt(StringUtils.substringAfter(item.getTitle().toString(), " "));
                        openWeekVideoFragment(weekNum);
                        drawerLayout.closeDrawer(Gravity.START);
                        return true;
                    }
                });
        SubMenu subMenu = menu.addSubMenu("\tFirst Trimester");
        for (int i = 4; i <= 41; i++) {
            if (i == 13)
                subMenu = menu.addSubMenu("\tSecond Trimester");
            else if (i == 25)
                subMenu = menu.addSubMenu("\tThird Trimester");
            subMenu.add("\t\t\t\tWeek " + i);
        }



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
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    public void openWeekSelectionFragment() {
        WeekSelectionFragment fragment = new WeekSelectionFragment();
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().addToBackStack(null)
                .replace(R.id.content_frame, fragment, "week_select").commit();
    }


    public void openWeekVideoFragment(int weekNum) {
        WeekVideoAndDescriptionFragment fragment = new WeekVideoAndDescriptionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("weekNum", weekNum);
        fragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment).addToBackStack(null).commit();
    }

    @Override
    public void setNotification(WeekCalculator weekCalculator) {
        WeeklyNotification.setAlarmManager(this, AlarmReceiver.class, weekCalculator);
    }
}
