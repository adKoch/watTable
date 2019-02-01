package com.wat.student.adkoch.wattable.db.handlers.week;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wat.student.adkoch.wattable.R;

import java.util.List;

public class WeekDateAdapter extends RecyclerView.Adapter<WeekDateAdapter.WeekDateViewHolder>  {
    private List<WeekDateContainer> mDataset;

    static class WeekDateViewHolder extends RecyclerView.ViewHolder{
        TextView dateTextView,dayoftwTextView;
        WeekDateViewHolder(View v){
            super(v);
            dateTextView = v.findViewById(R.id.date_text_view);
            dayoftwTextView = v.findViewById(R.id.day_of_the_week_text_view);
        }
    }

    public WeekDateAdapter(List<WeekDateContainer> dataset){
        mDataset = dataset;
    }

    @NonNull
    @Override
    public WeekDateAdapter.WeekDateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.week_date_item, parent, false);

        return new WeekDateAdapter.WeekDateViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekDateAdapter.WeekDateViewHolder holder, int position){
        WeekDateContainer weekDate = mDataset.get(position);

        String displayDate=weekDate.getDay()+"."+weekDate.getMonth();
        String displayDayOfTheWeek=weekDate.getDayOfTheWeek();

        holder.dateTextView.setText(displayDate);
        holder.dayoftwTextView.setText(displayDayOfTheWeek);
    }

    @Override
    public int getItemCount(){
        return mDataset.size();
    }
}
