package com.wat.student.adkoch.wattable.db.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.DataAccess;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.ui.day.DayBlocklistContainer;
import com.wat.student.adkoch.wattable.db.ui.week.WeekBlockAdapter;
import com.wat.student.adkoch.wattable.db.ui.week.WeekBlocklistContainer;
import com.wat.student.adkoch.wattable.db.ui.week.WeekDateAdapter;
import com.wat.student.adkoch.wattable.db.ui.week.WeekDateContainer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeekActivity extends AppCompatActivity{

    private RecyclerView weekBlockRecyclerView;
    private RecyclerView.Adapter weekBlockAdapter;
    private RecyclerView.LayoutManager weekBlockLayoutManager;
    private List<Block> blocks;

    private RecyclerView weekDateRecyclerView;
    private RecyclerView.Adapter weekDateAdapter;
    private RecyclerView.LayoutManager weekDateLayoutManager;
    private List<WeekDateContainer> dates;

    private String TAG = "WeekActivity";

    WeekBlocklistContainer weekBlocklistContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);


        Toolbar toolbar = (Toolbar) findViewById(R.id.week_toolbar);
        setSupportActionBar(toolbar);

        weekBlocklistContainer=new WeekBlocklistContainer(DataAccess.getSemesterStart().toDate(),DataAccess.getSemesterEnd().toDate());
        loadData(DataAccess.getTimetableQuery());
        //new LoadDataTask().execute();
        weekBlockRecyclerView = (RecyclerView) findViewById(R.id.week_block_recycler_view);
        weekBlockRecyclerView.setHasFixedSize(true);
        weekBlockLayoutManager = new GridLayoutManager(this, 7);
        weekBlockRecyclerView.setLayoutManager(weekBlockLayoutManager);
        weekBlockRecyclerView.setItemAnimator(new DefaultItemAnimator());


        weekBlockAdapter = new WeekBlockAdapter(weekBlocklistContainer.getBlocks());

        weekDateRecyclerView = (RecyclerView) findViewById(R.id.week_date_recycler_view);
        weekDateRecyclerView.setHasFixedSize(true);
        weekDateLayoutManager = new LinearLayoutManager(this);
        weekDateRecyclerView.setLayoutManager(weekDateLayoutManager);
        weekDateRecyclerView.setItemAnimator(new DefaultItemAnimator());

        weekDateAdapter = new WeekDateAdapter(weekBlocklistContainer.getDays());

        weekDateRecyclerView.setAdapter(weekDateAdapter);
        weekBlockRecyclerView.setAdapter(weekBlockAdapter);

        final RecyclerView.OnScrollListener[] scrollListeners = new RecyclerView.OnScrollListener[2];
        scrollListeners[0] = new RecyclerView.OnScrollListener( )
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                weekBlockRecyclerView.removeOnScrollListener(scrollListeners[1]);
                weekBlockRecyclerView.scrollBy(dx, dy);
                weekBlockRecyclerView.addOnScrollListener(scrollListeners[1]);
            }
        };
        scrollListeners[1] = new RecyclerView.OnScrollListener( )
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                weekDateRecyclerView.removeOnScrollListener(scrollListeners[0]);
                weekDateRecyclerView.scrollBy(dx, dy);
                weekDateRecyclerView.addOnScrollListener(scrollListeners[0]);
            }
        };
        weekDateRecyclerView.addOnScrollListener(scrollListeners[0]);
        weekBlockRecyclerView.addOnScrollListener(scrollListeners[1]);
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

    private void loadData(Query timetableQuery){

        timetableQuery.get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot doc:task.getResult()){
                            weekBlocklistContainer.put(doc.toObject(Block.class));
                            Log.d(TAG,"Successfuly retrieved document: " +doc.getId());
                        }

                        weekBlockAdapter.notifyDataSetChanged();
                        weekDateAdapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG,"Failure fetching days for the given day");
                    }
                }
        });
    }
}


