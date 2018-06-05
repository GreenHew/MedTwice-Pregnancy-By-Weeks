package com.green.matthew.pregnancybyweeksapp.week_calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.FontRequest;
import android.provider.FontsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.green.matthew.pregnancybyweeksapp.R;
import com.green.matthew.pregnancybyweeksapp.WeekVideoAndDescriptionFragment;

import java.time.LocalDate;
import java.util.Objects;


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

        final TextView currentWeekView = view.findViewById(R.id.weekNumText);
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
        ArrayAdapter adapter = new ArrayAdapter<String>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_spinner_dropdown_item, selectionModes) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                //Change spinner font
                View view = super.getView(position, convertView, parent);
                final TextView textView = view.findViewById(android.R.id.text1);
                FontRequest fontRequest = new FontRequest("com.google.android.gms.fonts", "com.google.android.gms", "Open Sans");
                FontsContract.FontRequestCallback callback = new FontsContract.FontRequestCallback() {
                    @Override
                    public void onTypefaceRetrieved(Typeface typeface) {
                        textView.setTypeface(typeface);
                    }
                };
                Handler handler = new Handler();
                FontsContract.requestFonts(getContext(), fontRequest, handler, null, callback);
                return view;
            }
        };
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
            e.printStackTrace();
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
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
