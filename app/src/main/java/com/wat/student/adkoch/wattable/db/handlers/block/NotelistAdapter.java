package com.wat.student.adkoch.wattable.db.handlers.block;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.SubscriptionMapper;
import com.wat.student.adkoch.wattable.db.data.entities.Note;

import java.util.List;

public class NotelistAdapter extends RecyclerView.Adapter<NotelistAdapter.NotelistViewHolder> {
    private List<Note> mDataset;

    static class NotelistViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView, authorTextView, descriptionTextView;
        NotelistViewHolder(View v){
            super(v);
            titleTextView = v.findViewById(R.id.title);
            authorTextView = v.findViewById(R.id.author);
            descriptionTextView = v.findViewById(R.id.description);
        }
    }

    public NotelistAdapter(List<Note> dataset){
        mDataset = dataset;
    }

    @NonNull
    @Override
    public NotelistAdapter.NotelistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);

        return new NotelistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotelistViewHolder holder, int position){
        Note note = mDataset.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.authorTextView.setText(SubscriptionMapper.getInstance().getSubTitle(note.getAuthor()));
        holder.descriptionTextView.setText(note.getDescription());
    }

    @Override
    public int getItemCount(){
        return mDataset.size();
    }
}
