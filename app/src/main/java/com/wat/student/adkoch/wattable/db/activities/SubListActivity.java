package com.wat.student.adkoch.wattable.db.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.DataAccess;
import com.wat.student.adkoch.wattable.db.data.entities.Subscription;
import com.wat.student.adkoch.wattable.db.ui.sublist.SublistAdapter;
import com.wat.student.adkoch.wattable.db.ui.ListRecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;


public class SubListActivity extends AppCompatActivity {

    private FirestoreRecyclerAdapter<Subscription, SublistViewHolder> recyclerAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sub_list_toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.sub_page_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddSub();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.sub_list_recycler_view);

        Query query = DataAccess.getSublistQuery();

        FirestoreRecyclerOptions<Subscription> options = new FirestoreRecyclerOptions.Builder<Subscription>()
                .setQuery(query, Subscription.class)
                .build();
        recyclerAdapter = new FirestoreRecyclerAdapter<Subscription, SublistViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SublistViewHolder holder, int position, @NonNull Subscription model) {
                holder.setFields(model);
            }

            @NonNull
            @Override
            public SublistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sub_list_item,viewGroup,false);
                return new SublistViewHolder(view);
            }
        };

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.addOnItemTouchListener(new ListRecyclerTouchListener(getApplicationContext(), mRecyclerView, new ListRecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Subscription sub = myData.get(position);
                //Toast.makeText(getApplicationContext(), sub.getTitle() + "is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        mRecyclerView.setAdapter(recyclerAdapter);
    }



    private static class SublistViewHolder extends RecyclerView.ViewHolder{
        private TextView title, token;
        private View view;

        SublistViewHolder(View v){
            super(v);
            view=v;
            title = (TextView) v.findViewById(R.id.title);
            token = (TextView) v.findViewById(R.id.token);
        }

        void setFields(Subscription sub){
            title.setText(sub.getTitle());
            token.setText(sub.getToken());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (recyclerAdapter != null) {
            recyclerAdapter.stopListening();
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
