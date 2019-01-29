package com.wat.student.adkoch.wattable.db.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.data.entities.Note;
import com.wat.student.adkoch.wattable.db.data.entities.Subscription;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class DataAccess {

    private static String userGroup="I6B2S1";

    private static String semester="2018Zima";

    private static String sublistRetrievalTAG = "SublistRetrieval";
    private static String subAddTAG = "SubAddition";

    private static Timestamp semesterStart;
    private static Timestamp semesterEnd;

    public static Timestamp getSemesterStart(){
        return semesterStart;
    }
    public static Timestamp getSemesterEnd(){
        return semesterEnd;
    }


    public static void putSub(Subscription sub){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> subscription = new HashMap<>();
        subscription.put("title",sub.getTitle());
        subscription.put("token",sub.getToken());

        try{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();
            db.document("user/"+uid+"/sublist/"+sub.getToken()).set(subscription).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        } catch(Exception e){
            Log.w(subAddTAG,"Fetching uid fail:" + e);
        }
    }

    public static void deleteSub(Subscription sub){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();
            db.document("user/"+uid+"/sublist/"+sub.getToken()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(subAddTAG,"Subscription successfully deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(subAddTAG, "Deleting Subscription failed : ", e);
                }
            });
        } catch(Exception e){
            Log.w(subAddTAG,"Fetching uid fail:" + e);
        }
    }

    public static Query getSublistQuery(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = null;
        try{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();
            query = db.collection("user").document(uid).collection("sublist");
            Log.d(sublistRetrievalTAG,"Fetching subscription list success:");
        } catch(Exception e){
            Log.w(sublistRetrievalTAG,"Fetching subscription list fail:" + e);
        }
        return query;
    }

    public static Query getTimetableQuery(){
        return FirebaseFirestore.getInstance().collection("semester").document(semester).collection(userGroup);
    }

    public static Query getDayQuery(Date day){
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("semester/"+semester+"/"+userGroup)
                .whereEqualTo("month",cal.get(Calendar.MONTH)+1)
                .whereEqualTo("day",cal.get(Calendar.DAY_OF_MONTH));
    }

    public static String getSemester(){
        return semester;
    }

    public static void setSemesterRanges(){
        DocumentReference doc = FirebaseFirestore.getInstance().document("semester/"+DataAccess.getSemester());
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot ds = task.getResult();
                    semesterStart=(Timestamp) ds.get("semesterStart");
                    semesterEnd=(Timestamp) ds.get("semesterEnd");

                    Log.d("WeekFetch","Fetch week ranges successful start: "+new SimpleDateFormat("dd/MM/yyyy").format(semesterStart.toDate()) +", end: "+new SimpleDateFormat("dd/MM/yyyy").format(semesterEnd.toDate()));
                }else {
                    Log.w("WeekFetch","Fetchind week ranges unsuccessful");
                }
            }
        });
    }

    public static Query getNoteQuery(Block b){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("semester")
                .document(semester)
                .collection(userGroup)
                .document(b.getPart()+"-"+b.getMonth()+"-"+b.getDay()+"-"+b.getTimeBlockNr())
                .collection("notes");
    }

    public static void putNote(Block b,Note n){
        final Block block = b;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final HashMap<String,Object> note = new HashMap<>();
        note.put("title",n.getTitle());
        note.put("author",n.getAuthor());
        note.put("description",n.getDescription());
                db.collection("semester")
                .document(semester)
                .collection(userGroup)
                .document(block.getPart()+"-"+block.getMonth()+"-"+block.getDay()+"-"+block.getTimeBlockNr())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            long index=0;
                            try{
                                index=(long) (task.getResult().get("noteIndex"));

                            }catch (Exception e){
                                Log.w("NotePut","Fetching note index fail: "+e);
                            }
                            index++;
                            FirebaseFirestore.getInstance().collection("semester")
                                    .document(semester)
                                    .collection(userGroup)
                                    .document(block.getPart()+"-"+block.getMonth()+"-"+block.getDay()+"-"+block.getTimeBlockNr())
                                    .collection("notes")
                                    .document("note-"+index).set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("NotePut","Note added successfuly");
                                    }
                                }
                            });
                            FirebaseFirestore.getInstance().collection("semester")
                                    .document(semester)
                                    .collection(userGroup)
                                    .document(block.getPart()+"-"+block.getMonth()+"-"+block.getDay()+"-"+block.getTimeBlockNr())
                                    .update("noteIndex",index);
                            FirebaseFirestore.getInstance().collection("semester")
                                    .document(semester)
                                    .collection(userGroup)
                                    .document(block.getPart()+"-"+block.getMonth()+"-"+block.getDay()+"-"+block.getTimeBlockNr())
                                    .update("noteCount",index);

                        }else {
                            Log.w("NotePut","Fetching block notes");
                        }
                    }
                });
    }

    public static String getUserToken(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid="";
        try{
            uid = user.getUid();
        }catch (Exception e){
            Log.w("getUid","failed fetching uid: "+e);
        }
        return uid.substring(0,12);
    }


}
