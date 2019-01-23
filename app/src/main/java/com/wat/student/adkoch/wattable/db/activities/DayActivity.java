package com.wat.student.adkoch.wattable.db.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.DataAccess;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.data.entities.Subscription;
import com.wat.student.adkoch.wattable.db.ui.ListRecyclerTouchListener;
import com.wat.student.adkoch.wattable.db.ui.day.BlocklistAdapter;

import java.util.Date;
import java.util.List;

public class DayActivity extends AppCompatActivity {

    private RecyclerView dayRecyclerView;
    private RecyclerView.Adapter dayAdapter;
    private RecyclerView.LayoutManager dayLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        Toolbar toolbar = (Toolbar) findViewById(R.id.day_toolbar);
        setSupportActionBar(toolbar);

        dayRecyclerView = (RecyclerView) findViewById(R.id.day_block_list_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        dayRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        dayLayoutManager = new LinearLayoutManager(this);
        dayRecyclerView.setLayoutManager(dayLayoutManager);
        dayRecyclerView.setItemAnimator(new DefaultItemAnimator());

        final List<Block> myData = DataAccess.getDay(Timestamp.now());

        Query query = DataAccess.getDayQuery(new Timestamp(new Date(119,1,23)));

        FirestoreRecyclerOptions<Block> options = new FirestoreRecyclerOptions.Builder<Block>()
                .setQuery(query, Block.class)
                .build();
        dayAdapter = new FirestoreRecyclerAdapter<Block, DayActivity.BlocklistViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DayActivity.BlocklistViewHolder holder, int position, @NonNull Block model) {
                holder.setFields(model);
            }

            @NonNull
            @Override
            public DayActivity.BlocklistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.day_item,viewGroup,false);
                return new DayActivity.BlocklistViewHolder(view);
            }
        };
        dayRecyclerView.addOnItemTouchListener(new ListRecyclerTouchListener(getApplicationContext(), dayRecyclerView, new ListRecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Block block = myData.get(position);
                Toast.makeText(getApplicationContext(), block.getSubjectName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        dayRecyclerView.setAdapter(dayAdapter);
    }

    private static class BlocklistViewHolder extends RecyclerView.ViewHolder{
        private TextView time, description, details;
        private View view;
        private final String[] blockTime= {"8:00  ", "9:50  ", "11:40", "13:30", "15:45", "17:35", "19:25"};

        public BlocklistViewHolder(View v){
            super(v);
            time = (TextView) v.findViewById(R.id.time);
            description = (TextView) v.findViewById(R.id.subject_description);
            details = (TextView) v.findViewById(R.id.subject_details);
        }

        void setFields(Block block){
            String displayDescription;
            String displayDetails;
            if(block.getPlace() == null || block.getBlockNr() == -1 || block.getType() == null || block.getDate() == null || block.getSubjectName() == null || block.getSubjectNameShort() == null || block.getTimeBlockNr() == -1){
                displayDescription = " ";
                displayDetails = " ";
            } else {
                if(null!=block.getDirector()){
                    displayDetails = block.getDirector() + " " + block.getPlace();
                } else {
                    displayDetails =block.getPlace();
                }

                displayDescription = block.getSubjectName() + " (" + block.getType() + ") [" + block.getBlockNr() + "]";

            }
            time.setText(blockTime[block.getTimeBlockNr()-1] + "   ");
            description.setText(displayDescription);
            details.setText(displayDetails);
        }
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
