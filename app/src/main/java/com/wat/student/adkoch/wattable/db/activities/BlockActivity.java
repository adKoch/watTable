package com.wat.student.adkoch.wattable.db.activities;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.DataAccess;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.data.entities.Note;
import com.wat.student.adkoch.wattable.db.ui.block.NotelistAdapter;
import com.wat.student.adkoch.wattable.db.ui.day.BlocklistAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class BlockActivity extends AppCompatActivity {

    private TextView description, details;
    private Block block;
    private static final String[] months = {"Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"};
    private static final String[] daysOfTheWeek = { "Niedziela", "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota"};
    private final String[] blockTime= {"8:00  ", "9:50  ", "11:40", "13:30", "15:45", "17:35", "19:25"};
    private String TAG="BlockActivity";
    private List<Note> notes;
    private RecyclerView noteRecyclerView;
    private RecyclerView.Adapter noteAdapter;
    private RecyclerView.LayoutManager noteLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        Toolbar toolbar = (Toolbar) findViewById(R.id.block_toolbar);
        setSupportActionBar(toolbar);

        block =(Block) getIntent().getSerializableExtra("block");
        description=findViewById(R.id.description);
        details=findViewById(R.id.details);

        String displayDescription="";
        String displayDetails="";
        if(null!=block.getDirector()){
            displayDetails = block.getDirector() + " " + block.getPlace();
        } else {
            displayDetails =block.getPlace();
        }

        displayDescription = block.getSubjectName() + " (" + block.getType() + ") [" + block.getBlockNr() + "]";

        setTitle(getCurrentDayOfTheWeek()+" - "+block.getDay()+" "+months[block.getMonth()-1]+" - "+blockTime[block.getTimeBlockNr()-1]);
        description.setText(displayDescription);
        details.setText(displayDetails);

        Query myQuery = DataAccess.getNoteQuery(block);

        myQuery.addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDS, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                for(QueryDocumentSnapshot doc: queryDS){
                    Log.d(TAG,"dodawanie do Block activity: "+doc.getId());
                    notes.add(doc.toObject(Note.class));
                }
                noteAdapter.notifyDataSetChanged();
            }
        });


        noteRecyclerView = (RecyclerView) findViewById(R.id.note_list_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        noteRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        noteLayoutManager = new LinearLayoutManager(this);
        noteRecyclerView.setLayoutManager(noteLayoutManager);
        noteRecyclerView.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        noteRecyclerView.addItemDecoration(dividerItemDecoration);

        noteAdapter = new NotelistAdapter(notes);

        noteRecyclerView.setAdapter(noteAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.action_day_view){
            goToDay();
        } else if(id == R.id.action_week_view){
            goToWeek();
        } else if(id == R.id.action_subs){
            goToSubs();
        }

        return super.onOptionsItemSelected(item);
    }
    private void goToDay(){
        Intent intent = new Intent(this, DayActivity.class);
        startActivity(intent);
    }
    private void goToWeek(){
        Intent intent = new Intent(this, WeekActivity.class);
        startActivity(intent);
    }
    private void goToSubs(){
        Intent intent = new Intent(this, SubListActivity.class);
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
}
