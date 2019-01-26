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
import android.widget.TextView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.DataAccess;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.ui.day.BlocklistAdapter;
import com.wat.student.adkoch.wattable.db.ui.day.DayBlocklistContainer;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

public class DayActivity extends AppCompatActivity {

    private RecyclerView dayRecyclerView;
    private RecyclerView.Adapter dayAdapter;
    private RecyclerView.LayoutManager dayLayoutManager;
    private DayBlocklistContainer myData;
    private Date currentDate;
    private TextView noBlocksTextView;

    private static final String[] months = {"Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"};
    private static final String[] daysOfTheWeek = { "Niedziela", "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_day);
        Toolbar toolbar = (Toolbar) findViewById(R.id.day_toolbar);
        setSupportActionBar(toolbar);

        noBlocksTextView = (TextView) findViewById(R.id.no_blocks_text_view);

        currentDate=new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int month=cal.get(Calendar.MONTH);
        int day=cal.get(Calendar.DAY_OF_MONTH);
        setTitle(daysOfTheWeek[cal.get(Calendar.DAY_OF_WEEK)-1]+" - "+cal.get(Calendar.DAY_OF_MONTH)+" "+months[month]);
        myData= new DayBlocklistContainer(month+1,day);
        Query myQuery = DataAccess.getDayQuery(currentDate);

        myQuery.addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDS, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("OChuj", "Listen failed.", e);
                    return;
                }
                for(QueryDocumentSnapshot doc: queryDS){
                    Log.d("dodawanie do Day block",doc.getId());
                    myData.put(doc.toObject(Block.class),doc.getId());
                    dayAdapter.notifyDataSetChanged();
                    if(!myData.isEmpty()) noBlocksTextView.setVisibility(View.GONE);
                    else noBlocksTextView.setVisibility(View.VISIBLE);
                }
            }
        });


        dayRecyclerView = (RecyclerView) findViewById(R.id.day_block_list_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        dayRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        dayLayoutManager = new LinearLayoutManager(this);
        dayRecyclerView.setLayoutManager(dayLayoutManager);
        dayRecyclerView.setItemAnimator(new DefaultItemAnimator());

        dayAdapter = new BlocklistAdapter(myData.getBlocklist());

        dayRecyclerView.setAdapter(dayAdapter);
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
}
