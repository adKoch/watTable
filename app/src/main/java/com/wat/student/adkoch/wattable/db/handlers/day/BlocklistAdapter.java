package com.wat.student.adkoch.wattable.db.handlers.day;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.entities.Block;

import java.util.List;

public class BlocklistAdapter extends RecyclerView.Adapter<BlocklistAdapter.BlocklistViewHolder>  {
    private List<Block> mDataset;

    private final String[] blockTime= {"8:00  ", "9:50  ", "11:40", "13:30", "15:45", "17:35", "19:25"};

    static class BlocklistViewHolder extends RecyclerView.ViewHolder{
        TextView timeTextView, descriptionTextView, detailsTextView, noteCountTextView;
        BlocklistViewHolder(View v){
            super(v);
            timeTextView = v.findViewById(R.id.time);
            descriptionTextView = v.findViewById(R.id.subject_description);
            detailsTextView = v.findViewById(R.id.subject_details);
            noteCountTextView = v.findViewById(R.id.note_count);
        }
    }

    public BlocklistAdapter(List<Block> dataset){
        mDataset = dataset;
    }

    @NonNull
    @Override
    public BlocklistAdapter.BlocklistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_block_item, parent, false);

        return new BlocklistAdapter.BlocklistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BlocklistAdapter.BlocklistViewHolder holder, int position){
        Block block = mDataset.get(position);
        String displayDescription;
        String displayDetails;
        String displayTime="";
        String displayNoteCount="";
        if(block.getPlace() == null || block.getBlockNr() == -1 || block.getType() == null || block.getSubjectName() == null || block.getSubjectNameShort() == null || block.getTimeBlockNr() == -1){
            displayDescription = " ";
            displayDetails = " ";
        } else {
            if(null==block.getDirector()){
                displayDetails = block.getDirector() + " " + block.getPlace();
            } else {
                displayDetails =block.getPlace();
            }

            displayDescription = block.getSubjectName() + " (" + block.getType() + ") [" + block.getBlockNr() + "]";

        }
        if(block.getNoteCount()>0){
            displayNoteCount= ""+block.getNoteCount();
            holder.noteCountTextView.setVisibility(View.VISIBLE);
        } else {
            holder.noteCountTextView.setVisibility(View.INVISIBLE);
        }
        if(block.getTimeBlockNr()>=1 && block.getTimeBlockNr()<=7) displayTime = blockTime[block.getTimeBlockNr()-1] + "   ";
        holder.timeTextView.setText(displayTime);
        holder.descriptionTextView.setText(displayDescription);
        holder.detailsTextView.setText(displayDetails);
        holder.noteCountTextView.setText(displayNoteCount);
    }

    @Override
    public int getItemCount(){
        return mDataset.size();
    }
}
