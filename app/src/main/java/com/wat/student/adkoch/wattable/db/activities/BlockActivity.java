package com.wat.student.adkoch.wattable.db.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.Settings;
import com.wat.student.adkoch.wattable.db.data.SubscriptionMapper;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.data.entities.Note;
import com.wat.student.adkoch.wattable.db.handlers.block.NoteListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

public class BlockActivity extends AppCompatActivity {

    private TextView descriptionTextView, detailsTextView;
    private Block block;
    private String[] months;
    private String[] daysOfTheWeek;
    private String[] blockTime;
    private String TAG="BlockAct";
    private List<Note> notes;
    private RecyclerView noteRecyclerView;
    private NoteListAdapter notelistAdapter;
    private ProgressBar blockProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        setSupportActionBar((Toolbar) findViewById(R.id.block_toolbar));
        months = getResources().getStringArray(R.array.months);
        daysOfTheWeek = getResources().getStringArray(R.array.daysOfTheWeek);
        blockTime = getResources().getStringArray(R.array.blockTimes);

        RecyclerView.LayoutManager noteLayoutManager;
        notes=new ArrayList<>();
        block =(Block) getIntent().getSerializableExtra(getString(R.string.serializable_block_name));
        descriptionTextView= findViewById(R.id.description);
        detailsTextView= findViewById(R.id.details);
        blockProgressBar = findViewById(R.id.notes_spinner);

        setFields();
        notelistAdapter = new NoteListAdapter(notes);

        FloatingActionButton fab = findViewById(R.id.block_page_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddNote();
            }
        });

        noteRecyclerView = findViewById(R.id.note_list_recycler_view);

        noteRecyclerView.setHasFixedSize(true);

        noteLayoutManager = new LinearLayoutManager(this);
        noteRecyclerView.setLayoutManager(noteLayoutManager);
        noteRecyclerView.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        noteRecyclerView.addItemDecoration(dividerItemDecoration);

        Query query = getNoteQuery(block);

        query.addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDS, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, getString(R.string.listen_failed_text), e);
                    return;
                }
                notes.clear();
                for(QueryDocumentSnapshot doc: queryDS){
                    Log.d(TAG,getString(R.string.Block_log_adding_block_doc)+doc.getId());
                    if(SubscriptionMapper.getInstance().contains((String)doc.get(getString(R.string.subscription_mapper_contains_author)))){
                        notes.add(doc.toObject(Note.class));
                    }
                }
                blockProgressBar.setVisibility(View.INVISIBLE);
                notelistAdapter.notifyDataSetChanged();
                noteRecyclerView.setVisibility(View.VISIBLE);
            }
        });
        noteRecyclerView.setAdapter(notelistAdapter);
    }

    private Query getNoteQuery(Block b){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(getString(R.string.collection_semester))
                .document(Settings.getInstance().getSemester())
                .collection(Settings.getInstance().getGroup())
                .document(b.getPart()+"-"+b.getMonth()+"-"+b.getDay()+"-"+b.getTimeBlockNr())
                .collection(getString(R.string.collection_notes));
    }

    private void goToAddNote() {
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra(getString(R.string.serializable_block_name),block);
        startActivity(intent);
    }

    private String getCurrentDayOfTheWeek(){
        Calendar bDate;
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(Settings.getInstance().getSemesterStart().toDate());
        bDate=Calendar.getInstance();
        if(block.getMonth()<startDate.get(Calendar.MONTH)+1){
            bDate.set(Calendar.YEAR,startDate.get(Calendar.YEAR)+1);
        } else {
            bDate.set(Calendar.YEAR,startDate.get(Calendar.YEAR));
        }
        bDate.set(Calendar.MONTH,block.getMonth()-1);
        bDate.set(Calendar.DAY_OF_MONTH,block.getDay());

        return daysOfTheWeek[bDate.get(Calendar.DAY_OF_WEEK)-1];
    }

    private void setFields(){
        String displayDetails;
        String displayDescription;
        if(null!=block.getDirector()){
            displayDetails = block.getDirector() + " " + block.getPlace();
        } else {
            displayDetails =block.getPlace();
        }

        displayDescription = block.getSubjectName() + " (" + block.getType() + ") [" + block.getBlockNr() + "]";

        setTitle(getCurrentDayOfTheWeek()+" - "+block.getDay()+" "+months[block.getMonth()-1]+" - "+blockTime[block.getTimeBlockNr()-1]+"("+ block.getTimeBlockNr()+")");
        descriptionTextView.setText(displayDescription);
        detailsTextView.setText(displayDetails);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if(id == R.id.action_day_view){
            Intent intent = new Intent(this, DayActivity.class);
            startActivity(intent);
        } else if(id == R.id.action_week_view){
            Intent intent = new Intent(this, WeekActivity.class);
            startActivity(intent);
        } else if(id == R.id.action_subs){
            Intent intent = new Intent(this, SubListActivity.class);
            startActivity(intent);
        } else if(id == R.id.action_logout){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            AuthUI.getInstance().signOut(this);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
