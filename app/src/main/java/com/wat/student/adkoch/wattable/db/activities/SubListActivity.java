package com.wat.student.adkoch.wattable.db.activities;

import android.content.Intent;
import android.os.Bundle;
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

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.entities.Subscription;
import com.wat.student.adkoch.wattable.db.handlers.ListRecyclerTouchListener;


public class SubListActivity extends AppCompatActivity {

    private FirestoreRecyclerAdapter<Subscription, SublistViewHolder> recyclerAdapter;
    private final AppCompatActivity thisActivity=this;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerView mRecyclerView;
        RecyclerView.LayoutManager mLayoutManager;
        setContentView(R.layout.activity_sub_list);

        TAG=getString(R.string.SubListActivity_log_TAG);

        setSupportActionBar((Toolbar) findViewById(R.id.sub_list_toolbar));
        FloatingActionButton fab =findViewById(R.id.sub_page_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddSub();
            }
        });

        mRecyclerView = findViewById(R.id.sub_list_recycler_view);

        Query query = getSublistQuery();

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
            public void onClick(int position) {
                Subscription sub = recyclerAdapter.getItem(position);
                Intent intent = new Intent(thisActivity, AddSubActivity.class);
                intent.putExtra(getString(R.string.serializable_subscription_name),sub);
                startActivity(intent);
            }
        }));
        mRecyclerView.setAdapter(recyclerAdapter);
    }



    private static class SublistViewHolder extends RecyclerView.ViewHolder{
        private TextView titleTextView, tokenTextView;

        SublistViewHolder(View v){
            super(v);
            titleTextView = v.findViewById(R.id.title);
            tokenTextView = v.findViewById(R.id.token);
        }

        void setFields(Subscription sub){
            titleTextView.setText(sub.getTitle());
            tokenTextView.setText(sub.getToken());
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
    private void goToAddSub(){
        Intent intent = new Intent(this, AddSubActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            AuthUI.getInstance().signOut(this);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private Query getSublistQuery(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = null;
        try{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();
            query = db.collection(getString(R.string.collection_user)).document(uid).collection(getString(R.string.collection_sublist));
            Log.d(TAG,"Fetching subscription list success:");
        } catch(Exception e){
            Log.w(TAG,"Fetching subscription list fail:" + e);
        }
        return query;
    }
}
