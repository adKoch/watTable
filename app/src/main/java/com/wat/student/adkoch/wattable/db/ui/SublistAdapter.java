package com.wat.student.adkoch.wattable.db.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wat.student.adkoch.wattable.R;

public class SublistAdapter extends RecyclerView.Adapter<SublistAdapter.SublistViewHolder> {
    private String[] mDataset;

    public static class SublistViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public SublistViewHolder(TextView v){
            super(v);
            mTextView=v;
        }
    }

    public SublistAdapter(String[] dataset){
        mDataset = dataset;
    }

    @Override
    public SublistAdapter.SublistViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sub_list_text_view, parent, false);

        SublistViewHolder vh = new SublistViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SublistViewHolder holder, int position){
        holder.mTextView.setText(mDataset[position]);
    }

    @Override
    public int getItemCount(){
        return mDataset.length;
    }
}
