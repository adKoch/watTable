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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.DataAccess;
import com.wat.student.adkoch.wattable.db.data.SubscriptionMapper;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.data.entities.Note;
import com.wat.student.adkoch.wattable.db.handlers.block.NotelistAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

public class BlockActivity extends AppCompatActivity {

    private TextView descriptionTextView, detailsTextView;
    private Block block;
    private static final String[] months = {"Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"};
    private static final String[] daysOfTheWeek = { "Niedziela", "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota"};
    private final String[] blockTime= {"8:00  ", "9:50  ", "11:40", "13:30", "15:45", "17:35", "19:25"};
    private final String TAG="BlockActivity";
    private List<Note> notes;
    private RecyclerView noteRecyclerView;
    private NotelistAdapter notelistAdapter;
    private ProgressBar blockProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        setSupportActionBar((Toolbar) findViewById(R.id.block_toolbar));

        RecyclerView.LayoutManager noteLayoutManager;
        notes=new ArrayList<>();
        block =(Block) getIntent().getSerializableExtra(getString(R.string.serializable_block_name));
        descriptionTextView= findViewById(R.id.description);
        detailsTextView= findViewById(R.id.details);
        blockProgressBar = findViewById(R.id.notes_spinner);

        setFields();
        notelistAdapter = new NotelistAdapter(notes);

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

        Query query = DataAccess.getNoteQuery(block);

        query.addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDS, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                notes.clear();
                for(QueryDocumentSnapshot doc: queryDS){
                    Log.d(TAG,"dodawanie do block: "+doc.getId());
                    if(SubscriptionMapper.getInstance().contains((String)doc.get("author"))){
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

    private void goToAddNote() {
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra("block",block);
        startActivity(intent);
    }

    private String getCurrentDayOfTheWeek(){
        Calendar bDate;
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(DataAccess.getSemesterStart().toDate());
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
        // Inflate the menu; this adds items to the action bar if it is present.
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
            AuthUI.getInstance().signOut(this);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
