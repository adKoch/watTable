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

public class BlockListAdapter extends RecyclerView.Adapter<BlockListAdapter.BlockListViewHolder>  {
    private final List<Block> mDataset;

    private final View.OnClickListener dayOnClickListener;

    private String[] blockTime;

    static class BlockListViewHolder extends RecyclerView.ViewHolder{
        final TextView timeTextView;
        final TextView descriptionTextView;
        final TextView detailsTextView;
        final TextView noteCountTextView;
        BlockListViewHolder(View v, View.OnClickListener onClickListener){
            super(v);
            timeTextView = v.findViewById(R.id.time);
            descriptionTextView = v.findViewById(R.id.subject_description);
            detailsTextView = v.findViewById(R.id.subject_details);
            noteCountTextView = v.findViewById(R.id.note_count);
            v.setOnClickListener(onClickListener);
        }
    }

    public BlockListAdapter(List<Block> dataset, View.OnClickListener onClickListener){
        dayOnClickListener=onClickListener;
        mDataset = dataset;
    }

    @NonNull
    @Override
    public BlockListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_block_item, parent, false);
        blockTime=parent.getContext().getResources().getStringArray(R.array.blockTimes);

        return new BlockListViewHolder(v,dayOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockListViewHolder holder, int position){
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
