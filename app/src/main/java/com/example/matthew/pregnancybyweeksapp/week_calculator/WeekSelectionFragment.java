package com.example.matthew.pregnancybyweeksapp.week_calculator;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.matthew.pregnancybyweeksapp.R;
import com.example.matthew.pregnancybyweeksapp.WeekVideoAndDescriptionFragment;

import java.time.LocalDate;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class WeekSelectionFragment extends android.support.v4.app.Fragment implements View.OnClickListener{
    private static final String[] selectionModes = {"Last Menstrual Period (LMP)", "Conception Date", "Projected Due Date"};
    DatePicker datePicker;
    WeekCalculator weekCalculator;
    TextView invalidText;
    Spinner spinner;
    OnAcceptClickListener onAcceptClickListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.current_week_selector_fragment, container, false);

        final TextView currentWeekView = view.findViewById(R.id.currentWeekView);
        invalidText = view.findViewById(R.id.invalidWeekTextView);
        datePicker = view.findViewById(R.id.datePicker);

        //Get and set saved date preferences
        LocalDate date = LocalDate.now();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int year = preferences.getInt("year", 0);
        int month = preferences.getInt("month", 0);
        int day = preferences.getInt("day", 0);
        int selectedType = preferences.getInt("selection type", 0);

        //if no saved date preference
        if (year == 0) {
            year = date.getYear();
            month = date.getMonthValue() - 1;
            day = date.getDayOfMonth();
        }

        //Initialize selection type spinner and set on item selected listener
        spinner = (Spinner) Objects.requireNonNull(view.findViewById(R.id.estimationMode));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_spinner_dropdown_item, selectionModes);
        spinner.setAdapter(adapter);
        spinner.setSelection(selectedType);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectionType = spinner.getSelectedItemPosition();
                WeekCalculator weekCalculator = getWeekCalculator(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), selectionType);

                String text = weekCalculator.getCurrentWeek() + "";
                currentWeekView.setText(text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button acceptButton = view.findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(this);


        int selectionType = spinner.getSelectedItemPosition();
        WeekCalculator weekCalculator = getWeekCalculator(year, month, day, selectionType);

        //Initialize date picker widget and set on date changed listener
        String text = weekCalculator.getCurrentWeek() + "";
        currentWeekView.setText(text);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                WeekCalculator weekCalculator1 = getWeekCalculator(year, monthOfYear, dayOfMonth, spinner.getSelectedItemPosition());
                String text = weekCalculator1.getCurrentWeek() + "";
                currentWeekView.setText(text);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onAcceptClickListener = (OnAcceptClickListener) context;
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: Activity doesn't implement OnAcceptClickListener", e);
        }
    }

    private WeekCalculator getWeekCalculator(int year, int month, int day, int selectionType) {
        switch (selectionType) {
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
        return weekCalculator;
    }

    public interface OnAcceptClickListener {
        void setNotification(WeekCalculator weekCalculator);
    }

    //Accept button onClick
    //if date is between 4 and 41 then saves the selected date and type then opens corresponding week fragment
    @Override
    public void onClick(View v) {
        if (weekCalculator == null || weekCalculator.getCurrentWeek() < 4 || weekCalculator.getCurrentWeek() > 41){
            invalidText.setText(R.string.week_out_of_bounds);
            return;
        }
        invalidText.setText("");

        //Save user current week preferences
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).edit();
        editor.putInt("year", datePicker.getYear());
        editor.putInt("month", datePicker.getMonth());
        editor.putInt("day", datePicker.getDayOfMonth());
        editor.putInt("selection type", spinner.getSelectedItemPosition());
        editor.apply();

        onAcceptClickListener.setNotification(weekCalculator);

        //Open current week fragment
        WeekVideoAndDescriptionFragment fragment = new WeekVideoAndDescriptionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("weekNum", weekCalculator.getCurrentWeek());
        fragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.constraintLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

//        Intent intent = new Intent(getActivity(), WeekVideoAndDescriptionActivity.class);
//        startActivity(intent);

//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
//        Log.i("year", "year " + preferences.getInt("year", 0));
//        Log.i("month", "month " + preferences.getInt("month", 0));
//        Log.i("day", "day " + preferences.getInt("day", 0));
    }
}
