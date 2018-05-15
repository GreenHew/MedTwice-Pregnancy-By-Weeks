package com.example.matthew.pregnancybyweeksapp.week_menu;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.matthew.pregnancybyweeksapp.MainActivity;
import com.example.matthew.pregnancybyweeksapp.R;
import com.example.matthew.pregnancybyweeksapp.WeekVideoAndDescriptionFragment;

import java.util.ArrayList;
import java.util.Objects;

public class WeekListDialogFragment extends DialogFragment implements PregnancyWeeksAdapter.OnWeekClicked {

    private ArrayList<ListItem> weeks = new ArrayList<>();


    public WeekListDialogFragment() {
    }

    public static WeekListDialogFragment newInstance() {
        WeekListDialogFragment fragment = new WeekListDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setWeeks();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater  inflater, ViewGroup container, Bundle savedInstanceState) {

        Objects.requireNonNull(getDialog().getWindow()).setGravity(Gravity.START|Gravity.TOP);
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        getDialog().getWindow().setAttributes(layoutParams);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View view = inflater.inflate(R.layout.week_list_fragment, container);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerFragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        PregnancyWeeksAdapter adapter = new PregnancyWeeksAdapter(weeks);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        this.getDialog().setTitle("Weeks");
        adapter.setOnWeekClicked(this);
        return view;
    }

    private void setWeeks() {
        int offset = 0;
        for (int i = 4; i < 45; i++) {
            ListItem item;
            if (i == 4) {
                item = new ListItem("First Trimester");
                offset++;
            }
            else if(i == 14) {
                item = new ListItem("Second Trimester");
                offset++;
            }
            else if(i == 27) {
                item = new ListItem("Third Trimester");
                offset++;
            }
            else
                item = new WeekListItem(i - offset, false, BitmapFactory.decodeResource(this.getResources(), android.R.drawable.checkbox_on_background));
            weeks.add(item);
        }

    }

    @Override
    public void onItemClick(int weekNum) {
        ((MainActivity)getActivity()).openWeekVideoFragment(weekNum);
    }
}
