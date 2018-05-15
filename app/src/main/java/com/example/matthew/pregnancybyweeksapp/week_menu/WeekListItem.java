package com.example.matthew.pregnancybyweeksapp.week_menu;

import android.graphics.Bitmap;

public class WeekListItem extends ListItem {

    private int weekNumber;
    private Boolean watched;
    private Bitmap checkImage;

    WeekListItem(int weekNumber, Boolean watched, Bitmap checkImage) {
        super("week " + weekNumber);
        this.weekNumber = weekNumber;
        this.watched = watched;
        this.checkImage = checkImage;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public Boolean getWatched() {
        return watched;
    }

    public Bitmap getCheckImage() {
        return checkImage;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public void setWatched(Boolean watched, Bitmap checkImage) {
        this.watched = watched;
        this.checkImage = checkImage;
    }
}
