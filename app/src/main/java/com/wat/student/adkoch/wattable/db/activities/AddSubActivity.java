package com.wat.student.adkoch.wattable.db.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.SubscriptionMapper;
import com.wat.student.adkoch.wattable.db.data.entities.Subscription;

import java.util.HashMap;
import java.util.Map;

public class AddSubActivity extends AppCompatActivity {
    private Subscription sub;
    private Button addSubButton, deleteButton;
    private EditText subName, subToken;
    private String TAG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button clearButton;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub);

        subName = findViewById(R.id.subName);
        subToken = findViewById(R.id.subToken);

        TAG =getString(R.string.SubAddActivity_log_TAG);

        addSubButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.delete_sub_button);
        clearButton = findViewById(R.id.clearButton);

        sub =(Subscription) getIntent().getSerializableExtra(getString(R.string.serializable_subscription_name));
        checkState();

        final Toast successfulAddToast= Toast.makeText(this,getString(R.string.toast_add_sub_add_success),Toast.LENGTH_SHORT);
        final Toast successfulDeleteToast= Toast.makeText(this,getString(R.string.toast_add_sub_delete_success),Toast.LENGTH_SHORT);
        final Toast successfulEditToast= Toast.makeText(this,getString(R.string.toast_add_sub_edit_success),Toast.LENGTH_SHORT);
        addSubButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(checkFields(subName.getText().toString(), subToken.getText().toString())){

                    if(null!=sub){
                        deleteSub(sub);
                        successfulEditToast.show();
                    } else {
                        successfulAddToast.show();
                    }

                    addSub(subName.getText().toString(), subToken.getText().toString());
                    goToSubs();
                    SubscriptionMapper.getInstance().setSubs();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                subName.setText("");
                subToken.setText("");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSub(sub);
                successfulDeleteToast.show();
                goToSubs();
                SubscriptionMapper.getInstance().setSubs();
            }
        });
    }

    private void checkState(){
        if(null==sub){
            deleteButton.setVisibility(View.INVISIBLE);
            subName.setText("");
            subToken.setText("");
        } else {
            deleteButton.setVisibility(View.VISIBLE);
            subName.setText(sub.getTitle());
            subToken.setText(sub.getToken());
            addSubButton.setText(getString(R.string.addSubButtonEditText));
        }
    }
    private void addSub(String subName, String subToken){
        putSub(new Subscription(subName, subToken));
    }
    private boolean checkFields(String name, String token){
        if(!token.matches("[0-9a-zA-Z]{12}") || token.contains(" ")){
            Toast.makeText(this,getString(R.string.toast_add_sub_wrong_token),Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!name.matches("[0-9a-zA-Z]+")){
            Toast.makeText(this,getString(R.string.toast_add_sub_wrong_name),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void goToSubs(){
        Intent intent = new Intent(this, SubListActivity.class);
        startActivity(intent);
    }

    private void putSub(Subscription sub){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> subscription = new HashMap<>();
        subscription.put(getResources().getString(R.string.entity_subscription_title),sub.getTitle());
        subscription.put(getString(R.string.entity_subscription_token),sub.getToken());

        try{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();

            db.collection(getString(R.string.collection_user))
                    .document(uid)
                    .collection(getString(R.string.collection_sublist))
                    .document(sub.getToken())
                    .set(subscription).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG,"Subscription added");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Adding Subscription failed :: ", e);
                }
            });
        } catch(Exception e){
            Log.w(TAG,"Fetching uid fail:" + e);
        }
    }

    private void deleteSub(Subscription sub){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();
            db.collection(getString(R.string.collection_user))
                    .document(uid)
                    .collection(getString(R.string.collection_sublist))
                    .document(sub.getToken())
                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG,"Subscription successfully deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Deleting Subscription failed : ", e);
                }
            });
        } catch(Exception e){
            Log.w(TAG,"Fetching uid fail:" + e);
        }
    }
}
