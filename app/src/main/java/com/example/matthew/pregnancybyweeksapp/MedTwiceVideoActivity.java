package com.example.matthew.pregnancybyweeksapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MedTwiceVideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medtwice_activity);

        Intent intent = getIntent();
        String link = intent.getStringExtra("link");
        String title = intent.getStringExtra("title");
        if (link != null) {
            WeekVideoAndDescriptionFragment fragment = new WeekVideoAndDescriptionFragment();
            Bundle bundle = new Bundle();
            bundle.putString("link", link);
            bundle.putString("title", title);
            fragment.setArguments(bundle);
            android.support.v4.app.FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.medTwiceConstraint, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
