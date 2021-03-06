package com.wat.student.adkoch.wattable.db.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.Settings;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.handlers.ListRecyclerTouchListener;
import com.wat.student.adkoch.wattable.db.handlers.week.WeekBlockAdapter;
import com.wat.student.adkoch.wattable.db.handlers.week.WeekBlockListContainer;
import com.wat.student.adkoch.wattable.db.handlers.week.WeekDateAdapter;

import java.util.Date;

public class WeekActivity extends AppCompatActivity {

    private ProgressBar spinner;
    private RecyclerView weekBlockRecyclerView;
    private RecyclerView.Adapter weekBlockAdapter;
    private RecyclerView.LayoutManager weekBlockLayoutManager;

    private RecyclerView weekDateRecyclerView;
    private RecyclerView.Adapter weekDateAdapter;
    private RecyclerView.LayoutManager weekDateLayoutManager;

    private final String TAG="WeekAct";

    private WeekBlockListContainer weekBlocklistContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);

        spinner=findViewById(R.id.week_spinner);
        setSupportActionBar((Toolbar) findViewById(R.id.week_toolbar));

        weekBlocklistContainer=new WeekBlockListContainer(Settings.getInstance().getSemesterStart().toDate(),Settings.getInstance().getSemesterEnd().toDate(),this);
        loadData(getTimetableQuery());
        weekBlockRecyclerView = findViewById(R.id.week_block_recycler_view);
        weekBlockRecyclerView.setHasFixedSize(true);
        weekBlockLayoutManager = new GridLayoutManager(this, 7);
        weekBlockRecyclerView.setLayoutManager(weekBlockLayoutManager);
        weekBlockRecyclerView.setItemAnimator(new DefaultItemAnimator());


        weekBlockAdapter = new WeekBlockAdapter(weekBlocklistContainer.getBlocks(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = weekBlockRecyclerView.getChildAdapterPosition(v);
                Block b = weekBlocklistContainer.getBlocks().get(position);
                if(null!=b.getSubjectName()){
                    goToBlockView(b);
                }
            }
        });

        weekDateRecyclerView = findViewById(R.id.week_date_recycler_view);
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
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
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
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
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

    private void goToBlockView(Block block){
        Intent intent = new Intent(this, BlockActivity.class);
        intent.putExtra(getString(R.string.serializable_block_name),block);
        startActivity(intent);
    }

    private void loadData(Query timetableQuery){

        timetableQuery.get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot doc:task.getResult()){
                            weekBlocklistContainer.put(doc.toObject(Block.class));
                            Log.d(TAG,getString(R.string.week_log_success_retrieved_document) +doc.getId());
                        }
                        weekBlockAdapter.notifyDataSetChanged();
                        weekDateAdapter.notifyDataSetChanged();
                        weekBlockLayoutManager.scrollToPosition(weekBlocklistContainer.getDayIndex(new Date())*7);
                        weekDateLayoutManager.scrollToPosition(weekBlocklistContainer.getDayIndex(new Date()));
                        spinner.setVisibility(View.GONE);
                        weekBlockRecyclerView.setVisibility(View.VISIBLE);
                        weekDateRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        Log.w(TAG,getString(R.string.week_log_fail_fetching_days));
                    }
                }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private Query getTimetableQuery(){
        return FirebaseFirestore.getInstance().collection(getResources().getString(R.string.collection_semester))
                .document(Settings.getInstance().getSemester())
                .collection(Settings.getInstance().getGroup());
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


