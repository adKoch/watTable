package com.wat.student.adkoch.wattable.db.ui.week;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.entities.Block;

import java.util.List;

public class WeekBlockAdapter extends RecyclerView.Adapter<WeekBlockAdapter.WeekBlockViewHolder>  {
    private List<Block> mDataset;

    private final String[] blockTime= {"8:00  ", "9:50  ", "11:40", "13:30", "15:45", "17:35", "19:25"};

    public static class WeekBlockViewHolder extends RecyclerView.ViewHolder{
        public TextView description, details, location;
        public WeekBlockViewHolder(View v){
            super(v);
            description = (TextView) v.findViewById(R.id.subject_description);
            details = (TextView) v.findViewById(R.id.subject_details);
            location = (TextView) v.findViewById(R.id.subject_location);
        }
    }

    public WeekBlockAdapter(List<Block> dataset){
        mDataset = dataset;
    }

    @Override
    public WeekBlockViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.week_block_item, parent, false);

        return new WeekBlockViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WeekBlockViewHolder holder, int position){
        Block block = mDataset.get(position);
        String displayDescription;
        String displayDetails;
        String displayLocation;
        if(block.getPlace() == null || block.getBlockNr() == -1 || block.getType() == null || block.getDirector() == null || block.getSubjectName() == null || block.getSubjectNameShort() == null || block.getTimeBlockNr() == -1){
            displayDescription = " ";
            displayDetails = " ";
            displayLocation = " ";
        } else {
            displayDescription = block.getSubjectNameShort();
            displayDetails = "(" + block.getType() + ") [" + block.getBlockNr() + "]";
            displayLocation = block.getPlace();
        }
        holder.description.setText(displayDescription);
        holder.details.setText(displayDetails);
        if(displayLocation.length()>5){
            displayLocation=displayLocation.substring(0,5)+"...";
        }
        holder.location.setText(displayLocation);
    }

    @Override
    public int getItemCount(){
        return mDataset.size();
    }
}
