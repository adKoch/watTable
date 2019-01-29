package com.wat.student.adkoch.wattable.db.handlers.week;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wat.student.adkoch.wattable.R;

import java.util.List;

public class WeekDateAdapter extends RecyclerView.Adapter<WeekDateAdapter.WeekDateViewHolder>  {
    private List<WeekDateContainer> mDataset;

    public static class WeekDateViewHolder extends RecyclerView.ViewHolder{
        public TextView date,dayoftw;
        public WeekDateViewHolder(View v){
            super(v);
            date = (TextView) v.findViewById(R.id.date_text_view);
            dayoftw = (TextView) v.findViewById(R.id.day_of_the_week_text_view);
        }
    }

    public WeekDateAdapter(List<WeekDateContainer> dataset){
        mDataset = dataset;
    }

    @Override
    public WeekDateAdapter.WeekDateViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.week_date_item, parent, false);

        return new WeekDateAdapter.WeekDateViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WeekDateAdapter.WeekDateViewHolder holder, int position){
        WeekDateContainer weekDate = mDataset.get(position);

        String displayDate=weekDate.getDay()+"."+weekDate.getMonth();
        String displayDayOfTheWeek=weekDate.getDayOfTheWeek();

        holder.date.setText(displayDate);
        holder.dayoftw.setText(displayDayOfTheWeek);
    }

    @Override
    public int getItemCount(){
        return mDataset.size();
    }
}
