package com.wat.student.adkoch.wattable.db.handlers.week;

import android.support.annotation.NonNull;
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

    static class WeekBlockViewHolder extends RecyclerView.ViewHolder{
        TextView descriptionTextView, detailsTextView, locationTextView;
        WeekBlockViewHolder(View v){
            super(v);
            descriptionTextView = v.findViewById(R.id.subject_description);
            detailsTextView = v.findViewById(R.id.subject_details);
            locationTextView = v.findViewById(R.id.subject_location);
        }
    }

    public WeekBlockAdapter(List<Block> dataset){
        mDataset = dataset;
    }

    @NonNull
    @Override
    public WeekBlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.week_block_item, parent, false);

        return new WeekBlockViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekBlockViewHolder holder, int position){
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
        holder.descriptionTextView.setText(displayDescription);
        holder.detailsTextView.setText(displayDetails);
        if(displayLocation.length()>5){
            displayLocation=displayLocation.substring(0,5)+"...";
        }
        holder.locationTextView.setText(displayLocation);
    }

    @Override
    public int getItemCount(){
        return mDataset.size();
    }
}
