package com.wat.student.adkoch.wattable.db.ui.block;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.entities.Note;

import java.util.List;

public class NotelistAdapter extends RecyclerView.Adapter<NotelistAdapter.NotelistViewHolder> {
    private List<Note> mDataset;

    public static class NotelistViewHolder extends RecyclerView.ViewHolder{
        public TextView title, author, description;
        public NotelistViewHolder(View v){
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            author = (TextView) v.findViewById(R.id.author);
            description = (TextView) v.findViewById(R.id.description);
        }
    }

    public NotelistAdapter(List<Note> dataset){
        mDataset = dataset;
    }

    @Override
    public NotelistAdapter.NotelistViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);

        return new NotelistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NotelistViewHolder holder, int position){
        Note note = mDataset.get(position);
        holder.title.setText(note.getTitle());
        holder.author.setText(note.getAuthor());
        holder.description.setText(note.getDescription());
    }

    @Override
    public int getItemCount(){
        return mDataset.size();
    }
}
