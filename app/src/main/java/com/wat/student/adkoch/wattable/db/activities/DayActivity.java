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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.Settings;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.handlers.ListRecyclerTouchListener;
import com.wat.student.adkoch.wattable.db.handlers.day.BlockListAdapter;
import com.wat.student.adkoch.wattable.db.handlers.day.DayBlockListContainer;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

public class DayActivity extends AppCompatActivity {


    private RecyclerView.Adapter dayAdapter;
    private DayBlockListContainer dayBlocklistContainer;
    private Date currentDate;
    private TextView noBlocksTextView;
    private final AppCompatActivity thisActivity=this;

    private final String TAG="DayAct";
    private ProgressBar dayProgressBar;

    private String[] months;
    private String[] daysOfTheWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        months = getResources().getStringArray(R.array.months);
        daysOfTheWeek = getResources().getStringArray(R.array.daysOfTheWeek);
        dayProgressBar = findViewById(R.id.day_spinner);
        setSupportActionBar((Toolbar) findViewById(R.id.day_toolbar));
        final RecyclerView dayRecyclerView;

        noBlocksTextView = findViewById(R.id.no_blocks_text_view);

        setView();

        Query myQuery = getDayQuery(currentDate);

        myQuery.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDS, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, getString(R.string.listen_failed_text), e);
                    return;
                }
                for(QueryDocumentSnapshot doc: queryDS){
                    Log.d(TAG,getString(R.string.day_log_adding_to_day_block_text)+doc.getId());
                    dayBlocklistContainer.put(doc.toObject(Block.class),doc.getId());
                }
                dayProgressBar.setVisibility(View.INVISIBLE);
                dayAdapter.notifyDataSetChanged();
                if(dayBlocklistContainer.isFull()) noBlocksTextView.setVisibility(View.GONE);
                else noBlocksTextView.setVisibility(View.VISIBLE);
            }
        });


        dayRecyclerView = findViewById(R.id.day_block_list_recycler_view);

        dayRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager dayLayoutManager = new LinearLayoutManager(this);
        dayRecyclerView.setLayoutManager(dayLayoutManager);
        dayRecyclerView.setItemAnimator(new DefaultItemAnimator());

        dayAdapter = new BlockListAdapter(dayBlocklistContainer.getBlockList(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = dayRecyclerView.getChildAdapterPosition(v);
                Block b = dayBlocklistContainer.getBlock(position);
                if(null!=b.getSubjectName()){
                    Intent intent = new Intent(thisActivity, BlockActivity.class);
                    intent.putExtra(getString(R.string.serializable_block_name),b);
                    startActivity(intent);
                }
            }
        });

        dayRecyclerView.setAdapter(dayAdapter);
    }

    private void setView(){
        currentDate=new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int month=cal.get(Calendar.MONTH);
        int day=cal.get(Calendar.DAY_OF_MONTH);
        setTitle(daysOfTheWeek[cal.get(Calendar.DAY_OF_WEEK)-1]+" - "+cal.get(Calendar.DAY_OF_MONTH)+" "+months[month]);
        dayBlocklistContainer = new DayBlockListContainer(month+1,day);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private Query getDayQuery(Date day){
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(getResources().getString(R.string.collection_semester))
                .document(Settings.getInstance().getSemester())
                .collection(Settings.getInstance().getGroup())
                .whereEqualTo(getString(R.string.entity_block_month),cal.get(Calendar.MONTH)+1)
                .whereEqualTo(getString(R.string.entity_block_day),cal.get(Calendar.DAY_OF_MONTH));
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
