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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.DataAccess;
import com.wat.student.adkoch.wattable.db.data.entities.Subscription;
import com.wat.student.adkoch.wattable.db.handlers.BarCompatActivity;
import com.wat.student.adkoch.wattable.db.handlers.ListRecyclerTouchListener;


public class SubListActivity extends BarCompatActivity {

    private FirestoreRecyclerAdapter<Subscription, SublistViewHolder> recyclerAdapter;
    private final AppCompatActivity thisActivity=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerView mRecyclerView;
        RecyclerView.LayoutManager mLayoutManager;
        setContentView(R.layout.activity_sub_list);

        setToolbar((Toolbar) findViewById(R.id.sub_list_toolbar));
        FloatingActionButton fab =findViewById(R.id.sub_page_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddSub();
            }
        });

        mRecyclerView = findViewById(R.id.sub_list_recycler_view);

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
                Subscription sub = recyclerAdapter.getItem(position);
                Intent intent = new Intent(thisActivity, AddSubActivity.class);
                intent.putExtra("sub",sub);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

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

}
