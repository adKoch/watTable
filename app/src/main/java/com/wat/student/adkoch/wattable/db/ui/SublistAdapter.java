package com.wat.student.adkoch.wattable.db.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.entities.Subscription;

import java.util.List;

public class SublistAdapter extends RecyclerView.Adapter<SublistAdapter.SublistViewHolder> {
    private List<Subscription> mDataset;

    public static class SublistViewHolder extends RecyclerView.ViewHolder{
        public TextView title, token;
        public SublistViewHolder(View v){
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            token = (TextView) v.findViewById(R.id.token);
        }
    }

    public SublistAdapter(List<Subscription> dataset){
        mDataset = dataset;
    }

    @Override
    public SublistAdapter.SublistViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sub_list_item, parent, false);

        return new SublistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SublistViewHolder holder, int position){
        Subscription sub = mDataset.get(position);
        holder.title.setText(sub.getTitle());
        holder.token.setText(sub.getToken());
    }

    @Override
    public int getItemCount(){
        return mDataset.size();
    }
}
