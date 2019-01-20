package com.wat.student.adkoch.wattable.db.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.data.entities.Note;
import com.wat.student.adkoch.wattable.db.data.entities.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DataAccess {

    private static String sublistRetrievalTAG = "Sublist retrieval";
    private static String subAddTAG = "Sub addition";

    public static void putSub(Subscription sub){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> subscription = new HashMap<>();
        subscription.put("title",sub.getTitle());
        subscription.put("token",sub.getToken());

        db.document("test/subscription").set(subscription).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(subAddTAG,"Subscription added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(subAddTAG, "Adding Subscription failed :: ", e);
            }
        });
    }

    public static List<Subscription> getSubs() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<Subscription> sublist = new ArrayList<>();
        try{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();
             db.collection("user").document(uid).collection("sublist").get()
             .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                 @Override
                 public void onComplete(@NonNull Task<QuerySnapshot> task) {
                     if(task.isSuccessful()){
                         for(QueryDocumentSnapshot document: task.getResult()){
                             sublist.add(new Subscription(document.get("title").toString(),document.get("token").toString()));
                             Log.d(sublistRetrievalTAG,"Successful retrieval of"+ document.getId());
                         }
                     } else {
                         Log.w(sublistRetrievalTAG,"Failed retrieval of document: "+ task.getException());
                     }
                 }
             });


        } catch(Exception e){
            Log.w(sublistRetrievalTAG,"Fetching subscription list fail:" + e);
        }
        return sublist;
    }

    public static List<Block> getWeek(){
        return null;
    }

    public static List<Block> getDay(){
        return null;
    }

    public static List<Note> getNotes(Block block){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return null;
    }

    public static void putBlock(Block block){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> bl = new HashMap<>();
        bl.put("subjectName",block.getSubjectName());
        bl.put("subjectNameShort",block.getSubjectNameShort());
        bl.put("date",block.getDate());
        bl.put("blockNr",block.getBlockNr());
        bl.put("director",block.getDirector());
        bl.put("place",block.getPlace());
        bl.put("TimeBlockNr",block.getTimeBlockNr());
        bl.put("type",block.getType());

        db.document("group/" + "I6B2S1/" + "day/" + block.getReadableDate() +"/table/" +block.getSubjectName()+"-"+block.getType()+block.getBlockNr()).set(bl).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("BlockAdd","Block added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("BlockAdd", "Adding Block failed :: ", e);
            }
        });
    }
}
