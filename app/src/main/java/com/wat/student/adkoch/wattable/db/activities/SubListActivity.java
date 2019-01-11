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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.entities.Subscription;
import com.wat.student.adkoch.wattable.db.ui.SublistAdapter;
import com.wat.student.adkoch.wattable.db.ui.sublist.SublistRecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

public class SubListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.sub_page_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddSub();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.sub_list_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // TODO exchange with getting sub list from db
        final List<Subscription> myData = new ArrayList<>();
        myData.add(new Subscription("Adam", "75GSF3$"));
        myData.add(new Subscription("Ada", "354FG^D!"));
        myData.add(new Subscription("Stefan", "DFS@7#F"));
        myData.add(new Subscription("Janek", "432TSDr234SDF@"));
        myData.add(new Subscription("Misiek", "gD%2s54"));
        myData.add(new Subscription("Małek", "sGe%@#sdSD"));
        myData.add(new Subscription("Kacper", "strf45sdf"));
        myData.add(new Subscription("Melchior", "saf%dfASDF35S"));

        mAdapter = new SublistAdapter(myData);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.addOnItemTouchListener(new SublistRecyclerTouchListener(getApplicationContext(), mRecyclerView, new SublistRecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Subscription sub = myData.get(position);
                Toast.makeText(getApplicationContext(), sub.getTitle() + "is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        mRecyclerView.setAdapter(mAdapter);
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
    private void goToAddSub(){
        Intent intent = new Intent(this, AddSubActivity.class);
        startActivity(intent);
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
