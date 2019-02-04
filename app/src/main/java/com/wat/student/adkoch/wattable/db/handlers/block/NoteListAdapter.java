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

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteListViewHolder> {
    private final List<Note> mDataset;

    static class NoteListViewHolder extends RecyclerView.ViewHolder{
        final TextView titleTextView;
        final TextView authorTextView;
        final TextView descriptionTextView;
        NoteListViewHolder(View v){
            super(v);
            titleTextView = v.findViewById(R.id.title);
            authorTextView = v.findViewById(R.id.author);
            descriptionTextView = v.findViewById(R.id.description);
        }
    }

    public NoteListAdapter(List<Note> dataset){
        mDataset = dataset;
    }

    @NonNull
    @Override
    public NoteListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);

        return new NoteListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteListViewHolder holder, int position){
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
