package com.wat.student.adkoch.wattable.db.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.DataAccess;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.handlers.ListRecyclerTouchListener;
import com.wat.student.adkoch.wattable.db.handlers.day.BlocklistAdapter;
import com.wat.student.adkoch.wattable.db.handlers.day.DayBlocklistContainer;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

public class DayActivity extends AppCompatActivity {

    private RecyclerView dayRecyclerView;
    private RecyclerView.Adapter dayAdapter;
    private RecyclerView.LayoutManager dayLayoutManager;
    private DayBlocklistContainer dayBlocklistContainer;
    private Date currentDate;
    private TextView noBlocksTextView;
    private final AppCompatActivity thisActivity=this;

    private String TAG="DayActivity";
    private ProgressBar spinner;

    private static final String[] months = {"Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"};
    private static final String[] daysOfTheWeek = { "Niedziela", "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        spinner= findViewById(R.id.day_spinner);
        setSupportActionBar((Toolbar) findViewById(R.id.day_toolbar));

        noBlocksTextView = findViewById(R.id.no_blocks_text_view);

        setView();

        Query myQuery = DataAccess.getDayQuery(currentDate);

        myQuery.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDS, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                for(QueryDocumentSnapshot doc: queryDS){
                    Log.d(TAG,"dodawanie do Day block: "+doc.getId());
                    dayBlocklistContainer.put(doc.toObject(Block.class),doc.getId());
                }
                spinner.setVisibility(View.INVISIBLE);
                dayAdapter.notifyDataSetChanged();
                if(!dayBlocklistContainer.isEmpty()) noBlocksTextView.setVisibility(View.GONE);
                else noBlocksTextView.setVisibility(View.VISIBLE);
            }
        });


        dayRecyclerView = findViewById(R.id.day_block_list_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        dayRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        dayLayoutManager = new LinearLayoutManager(this);
        dayRecyclerView.setLayoutManager(dayLayoutManager);
        dayRecyclerView.setItemAnimator(new DefaultItemAnimator());

        dayAdapter = new BlocklistAdapter(dayBlocklistContainer.getBlocklist());

        dayRecyclerView.setAdapter(dayAdapter);

        dayRecyclerView.addOnItemTouchListener( new ListRecyclerTouchListener(this, dayRecyclerView, new ListRecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Block b = dayBlocklistContainer.getBlock(position);
                if(null!=b.getSubjectName()){
                    Intent intent = new Intent(thisActivity, BlockActivity.class);
                    intent.putExtra(getString(R.string.serializable_block_name),b);
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void setView(){
        currentDate=new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int month=cal.get(Calendar.MONTH);
        int day=cal.get(Calendar.DAY_OF_MONTH);
        setTitle(daysOfTheWeek[cal.get(Calendar.DAY_OF_WEEK)-1]+" - "+cal.get(Calendar.DAY_OF_MONTH)+" "+months[month]);
        dayBlocklistContainer = new DayBlocklistContainer(month+1,day);
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
