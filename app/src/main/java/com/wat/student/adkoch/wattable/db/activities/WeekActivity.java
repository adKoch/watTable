package com.wat.student.adkoch.wattable.db.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.ui.ListRecyclerTouchListener;
import com.wat.student.adkoch.wattable.db.ui.week.WeekAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeekActivity extends AppCompatActivity {

    private RecyclerView weekRecyclerView;
    private RecyclerView.Adapter WeekAdapter;
    private RecyclerView.LayoutManager weekLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);
        Toolbar toolbar = (Toolbar) findViewById(R.id.week_toolbar);
        setSupportActionBar(toolbar);

        weekRecyclerView = (RecyclerView) findViewById(R.id.week_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        weekRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        weekLayoutManager = new GridLayoutManager(this, 7);
        weekRecyclerView.setLayoutManager(weekLayoutManager);
        weekRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // TODO exchange with getting block from db
        final List<Block> myData = new ArrayList<>();
        myData.add(new Block(1, "Programowanie zdarzeniowe", "PZ", "Adam XYZ", 3, "095(S)","p",new Date()));
        myData.add(new Block(2, "Programowanie zdarzeniowe", "PZ", "Adam XYZ", 4, "095(S)","p",new Date()));
        myData.add(new Block(3, "Metodyki numeryczne", "Mn", "abc NieAdam", 1, "313(S)","Ćw",new Date()));
        myData.add(new Block(4, new Date()));
        myData.add(new Block(5, new Date()));
        myData.add(new Block(6, "Analiza i wizualizacja danych", "Awd", "Adam Kochalniczak", 8, "224(065)","l",new Date()));
        myData.add(new Block(7, new Date()));
        myData.add(new Block(1, "Analiza", "PZ", "Adam XYZ", 3, "095(S)","p",new Date()));
        myData.add(new Block(2, "coś innego", "PZ", "Adam XYZ", 4, "095(S)","p",new Date()));
        myData.add(new Block(3, new Date()));
        myData.add(new Block(4, "Metodyki numeryczne", "Mn", "abc NieAdam", 1, "313(S)","Ćw",new Date()));
        myData.add(new Block(5, new Date()));
        myData.add(new Block(6, "Analiza i wizualizacja danych", "Awd", "Adam Kochalniczak", 8, "224(065)","l",new Date()));
        myData.add(new Block(7, new Date()));
        myData.add(new Block(1, "Programowanie zdarzeniowe", "PZ", "Adam XYZ", 3, "095(S)","p",new Date()));
        myData.add(new Block(2, "Programowanie zdarzeniowe", "PZ", "Adam XYZ", 4, "095(S)","p",new Date()));
        myData.add(new Block(3, "Metodyki numeryczne", "Mn", "abc NieAdam", 1, "313(S)","Ćw",new Date()));
        myData.add(new Block(4, new Date()));
        myData.add(new Block(5, new Date()));
        myData.add(new Block(6, "Analiza i wizualizacja danych", "Awd", "Adam Kochalniczak", 8, "224(065)","l",new Date()));
        myData.add(new Block(7, new Date()));

        WeekAdapter = new WeekAdapter(myData);
        weekRecyclerView.addOnItemTouchListener(new ListRecyclerTouchListener(getApplicationContext(), weekRecyclerView, new ListRecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Block block = myData.get(position);
                Toast.makeText(getApplicationContext(), block.getSubjectName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        weekRecyclerView.setAdapter(WeekAdapter);
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
