package com.example.matthew.pregnancybyweeksapp.week_menu;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matthew.pregnancybyweeksapp.R;

import java.util.ArrayList;

public class PregnancyWeeksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ListItem> weeks;
    private OnWeekClicked onWeekClicked;

    public interface OnWeekClicked {
        void onItemClick(int weekNum);
    }

    PregnancyWeeksAdapter(ArrayList<ListItem> weeks) {
        this.weeks = weeks;
    }

    public static class ViewHolderWeek extends RecyclerView.ViewHolder {
        ImageView checkView;
        TextView weekText;
        ViewHolderWeek(View view) {
            super(view);
//            checkView = view.findViewById(R.id.checkImageView);
            weekText = view.findViewById(R.id.weekTextView);
        }
    }

    public static class ViewHolderTrimester extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolderTrimester(View view) {
            super(view);
            textView = view.findViewById(R.id.trimesterText);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 10 || position == 23)
            return 0;
        else
            return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View view0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_list_trimester, parent, false);
                return new ViewHolderTrimester(view0);
            default:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_list_item, parent, false);
                return new ViewHolderWeek(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ListItem trimester = weeks.get(position);
                ViewHolderTrimester viewHolderTrimester = (ViewHolderTrimester)holder;
                viewHolderTrimester.textView.setText(trimester.getText());
                break;
            case 1:
                final WeekListItem week = (WeekListItem)weeks.get(position);
                ViewHolderWeek viewHolderWeek = (ViewHolderWeek)holder;
                viewHolderWeek.weekText.setText(week.getText());
//                viewHolderWeek.checkView.setImageBitmap(week.getCheckImage());
                viewHolderWeek.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onWeekClicked.onItemClick(week.getWeekNumber());
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return weeks.size();
    }

    public void setOnWeekClicked(OnWeekClicked onWeekClicked) {
        this.onWeekClicked = onWeekClicked;
    }
}
