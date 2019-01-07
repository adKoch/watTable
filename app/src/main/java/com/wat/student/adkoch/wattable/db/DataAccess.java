package com.wat.student.adkoch.wattable.db;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wat.student.adkoch.wattable.db.entities.Subscription;

import java.util.HashMap;
import java.util.Map;

public final class DataAccess {

    public static void putSub(Subscription sub){
        FirebaseFirestore db = FirebaseFirestore.getInstance();



        Map<String, Object> subscription = new HashMap<>();
        subscription.put("name",sub.getName());
        subscription.put("token",sub.getToken());

        db.document("test/subscription").set(subscription).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("SubAdd","Subscription added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("SubAdd", "Adding Subscription failed :: ", e);
            }
        });
    }
}
