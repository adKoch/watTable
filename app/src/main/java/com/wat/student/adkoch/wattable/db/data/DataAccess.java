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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.data.entities.Note;
import com.wat.student.adkoch.wattable.db.data.entities.Subscription;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DataAccess {

    private static String userGroup="I6B2S1";

    private static String semester="2018Zima";

    private static String sublistRetrievalTAG = "SublistRetrieval";
    private static String weekBlocksRetrievalTAG = "WeekBlocklistRetrieval";
    private static String dayBlocksRetrievalTAG = "DayBlocklistRetrieval";
    private static String grouplistRetrievalTAG = "GrouplistRetrieval";
    private static String notelistRetrievalTAG = "NotelistRetrieval";
    private static String subAddTAG = "SubAddition";
    private static String noteAddTAG = "NoteAddition";

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

    public static List<Block> getWeek(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<Block> blocklist = new ArrayList<>();
        try{
                                        db.collection("group/"+userGroup+"/block/").get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            for(QueryDocumentSnapshot document: task.getResult()){
                                                                Block b =document.toObject(Block.class);
                                                                blocklist.add(b);
                                                            }
                                                        } else {
                                                            Log.w(weekBlocksRetrievalTAG,"Failed retrieval of block document: "+ task.getException());
                                                        }
                                                    }
                                                });


        } catch(Exception e){
            Log.w(weekBlocksRetrievalTAG,"Fetching week fail:" + e);
        }
        return blocklist;
    }

    public static ArrayList<Block> getDay(Date day){
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<Block> blocklist = new ArrayList<>();
        try{
         db.collection("group/"+userGroup+"/"+semester)
                 .whereEqualTo("month",cal.get(Calendar.MONTH))
                 .whereEqualTo("day",cal.get(Calendar.DAY_OF_MONTH))
                 .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot document: task.getResult()){
                                    Block b = document.toObject(Block.class);
                                    blocklist.add(b);
                                    Log.d(dayBlocksRetrievalTAG,"Successful retrieval of"+ b.getSubjectName()+"-"+b.getType()+b.getBlockNr());
                                }
                            } else {
                                Log.w(dayBlocksRetrievalTAG,"Failed retrieval of document: "+ task.getException());
                            }
                        }
                    });
        } catch(Exception e){
            Log.w(dayBlocksRetrievalTAG,"Fetching subscription list fail:" + e);
        }
        return blocklist;
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



    public static List<Note> getNotes(Block block){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final List<Note> notelist = new ArrayList<>();
        try{
            db.collection("group/"+userGroup+"day/"+"/table/"+block.getSubjectName()+"-"+block.getType()+block.getBlockNr()+"note/").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot document: task.getResult()){
                                    notelist.add(document.toObject(Note.class));
                                    Log.d(notelistRetrievalTAG,"Successful retrieval of"+ document.getId());
                                }
                            } else {
                                Log.w(notelistRetrievalTAG,"Failed retrieval of document: "+ task.getException());
                            }
                        }
                    });
        } catch(Exception e){
            Log.w(notelistRetrievalTAG,"Fetching note list fail:" + e);
        }
        return notelist;
    }

    public static void putBlock(Block block){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> bl = new HashMap<>();
        bl.put("subjectName",block.getSubjectName());
        bl.put("subjectNameShort",block.getSubjectNameShort());
        bl.put("part",block.getPart());
        bl.put("blockNr",block.getBlockNr());
        bl.put("director",block.getDirector());
        bl.put("place",block.getPlace());
        bl.put("timeBlockNr",block.getTimeBlockNr());
        bl.put("type",block.getType());
        bl.put("month",block.getMonth());
        bl.put("day",block.getDay());

        db.document("semester/2018Zima/I6B2S1/"+ block.getPart()+"-"+block.getMonth() +"-"+block.getDay()+"-" +block.getTimeBlockNr()).set(bl).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public static void setUserGroup(String group){
        userGroup=group;
    }

    public static List<String> getAvailableGroups(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<String> grouplist = new ArrayList<>();
        try{
            db.collection("group").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot document: task.getResult()){
                                    grouplist.add(document.getId());
                                    Log.d(grouplistRetrievalTAG,"Successful retrieval of"+ document.getId());
                                }
                            } else {
                                Log.w(grouplistRetrievalTAG,"Failed retrieval of document: "+ task.getException());
                            }
                        }
                    });
        } catch(Exception e){
            Log.w(grouplistRetrievalTAG,"Fetching subscription list fail:" + e);
        }
        return grouplist;
    }

    public static String getUserGroup(){
        return userGroup;
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
        note.put("subjectName",n.getTitle());
        note.put("subjectNameShort",n.getAuthor());
        note.put("part",n.getDescription());


                db.collection("semester")
                .document(semester)
                .collection(userGroup)
                .document(b.getPart()+"-"+b.getMonth()+"-"+b.getDay()+"-"+b.getTimeBlockNr()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            int index=(int) task.getResult().get("noteIndex");
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
                        }else {
                            Log.w("NotePut","Fetching note index fail");
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
