package com.wat.student.adkoch.wattable.db.ui.day;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.data.entities.Subscription;
import com.wat.student.adkoch.wattable.db.ui.sublist.SublistAdapter;

import org.w3c.dom.Text;

import java.util.List;

public class BlocklistAdapter extends RecyclerView.Adapter<BlocklistAdapter.BlocklistViewHolder>  {
    private List<Block> mDataset;

    private final String[] blockTime= {"8:00  ", "9:50  ", "11:40", "13:30", "15:45", "17:35", "19:25"};

    public static class BlocklistViewHolder extends RecyclerView.ViewHolder{
        public TextView time, description, details;
        public BlocklistViewHolder(View v){
            super(v);
            time = (TextView) v.findViewById(R.id.time);
            description = (TextView) v.findViewById(R.id.subject_description);
            details = (TextView) v.findViewById(R.id.subject_details);
        }
    }

    public BlocklistAdapter(List<Block> dataset){
        mDataset = dataset;
    }

    @Override
    public BlocklistAdapter.BlocklistViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_item, parent, false);

        return new BlocklistAdapter.BlocklistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BlocklistAdapter.BlocklistViewHolder holder, int position){
        Block block = mDataset.get(position);
        String displayDescription;
        String displayDetails;
        if(block.getPlace() == null || block.getBlockNr() == -1 || block.getType() == null || block.getDate() == null || block.getDirector() == null || block.getSubjectName() == null || block.getSubjectNameShort() == null || block.getTimeBlockNr() == -1){
            displayDescription = " ";
            displayDetails = " ";
        } else {
            displayDescription = block.getSubjectName() + " (" + block.getType() + " " + block.getBlockNr() + ")";
            displayDetails = block.getDirector() + " " + block.getPlace();
        }
        holder.time.setText(blockTime[block.getTimeBlockNr()-1] + "   ");
        holder.description.setText(displayDescription);
        holder.details.setText(displayDetails);
    }

    @Override
    public int getItemCount(){
        return mDataset.size();
    }
}
