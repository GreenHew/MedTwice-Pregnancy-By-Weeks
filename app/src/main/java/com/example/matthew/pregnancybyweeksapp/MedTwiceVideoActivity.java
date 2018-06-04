package com.example.matthew.pregnancybyweeksapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import java.util.Objects;

public class MedTwiceVideoActivity extends AppCompatActivity {
    WeekVideoAndDescriptionFragment weekVideoAndDescriptionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medtwice_activity);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(null);
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));

        //Set status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.colorPrimaryDark));

        Intent intent = getIntent();
        String link = intent.getStringExtra("link");
        String title = intent.getStringExtra("title");
        if (link != null) {
            weekVideoAndDescriptionFragment = new WeekVideoAndDescriptionFragment();
            Bundle bundle = new Bundle();
            bundle.putString("link", link);
            bundle.putString("title", title);
            weekVideoAndDescriptionFragment.setArguments(bundle);
            android.support.v4.app.FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.medTwiceFrame, weekVideoAndDescriptionFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (weekVideoAndDescriptionFragment != null && weekVideoAndDescriptionFragment.fullScreen)
            weekVideoAndDescriptionFragment.videoPlayer.setFullscreen(false);
        else
            this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
