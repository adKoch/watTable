package com.wat.student.adkoch.wattable.db.handlers.sublist;

import android.support.annotation.NonNull;
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

    static class SublistViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView, tokenTextView;
        SublistViewHolder(View v){
            super(v);
            titleTextView = v.findViewById(R.id.title);
            tokenTextView = v.findViewById(R.id.token);
        }
    }

    public SublistAdapter(List<Subscription> dataset){
        mDataset = dataset;
    }

    @NonNull
    @Override
    public SublistAdapter.SublistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sub_list_item, parent, false);

        return new SublistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SublistViewHolder holder, int position){
        Subscription sub = mDataset.get(position);
        holder.titleTextView.setText(sub.getTitle());
        holder.tokenTextView.setText(sub.getToken());
    }

    @Override
    public int getItemCount(){
        return mDataset.size();
    }
}
