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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.data.entities.Note;
import com.wat.student.adkoch.wattable.db.data.entities.Subscription;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DataAccess {

    private static String userGroup="I6B2S1";


    private static String sublistRetrievalTAG = "Sublist retrieval";
    private static String weekBlocksRetrievalTAG = "Week block list retrieval";
    private static String dayBlocksRetrievalTAG = "Day block list retrieval";
    private static String grouplistRetrievalTAG = "Group list retrieval";
    private static String notelistRetrievalTAG = "Note list retrieval";
    private static String subAddTAG = "Sub addition";
    private static String noteAddTAG = "Note addition";

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

    public static List<Block> getWeek(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<Block> blocklist = new ArrayList<>();
        try{
            db.collection("group/"+userGroup+"day/").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot document: task.getResult()){
                                    try{
                                        db.collection("group/"+userGroup+"day/"+document.getId()+"/table").get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            for(QueryDocumentSnapshot document: task.getResult()){
                                                                blocklist.add(document.toObject(Block.class));
                                                                Log.d(weekBlocksRetrievalTAG,"Successful retrieval of block document: "+ document.getId());
                                                            }
                                                        } else {
                                                            Log.w(weekBlocksRetrievalTAG,"Failed retrieval of block document: "+ task.getException());
                                                        }
                                                    }
                                                });
                                    } catch(Exception e){
                                        Log.w(weekBlocksRetrievalTAG,"Failed retrieval of block: " + e);
                                    }

                                    Log.d(weekBlocksRetrievalTAG,"Successful retrieval of day document"+ document.getId());
                                }
                            } else {
                                Log.w(weekBlocksRetrievalTAG,"Failed retrieval of day document: "+ task.getException());
                            }
                        }
                    });
        } catch(Exception e){
            Log.w(sublistRetrievalTAG,"Fetching day fail:" + e);
        }
        return blocklist;
    }

    public static List<Block> getDay(Timestamp day){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<Block> blocklist = new ArrayList<>();
        try{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd");
            String readDay = sfd.format(day.toDate());
            db.collection("group/"+userGroup+"day/"+readDay+"/table").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot document: task.getResult()){
                                    blocklist.add(document.toObject(Block.class));
                                    Log.d(dayBlocksRetrievalTAG,"Successful retrieval of"+ document.getId());
                                }
                            } else {
                                Log.w(dayBlocksRetrievalTAG,"Failed retrieval of document: "+ task.getException());
                            }
                        }
                    });
        } catch(Exception e){
            Log.w(sublistRetrievalTAG,"Fetching subscription list fail:" + e);
        }
        return blocklist;
    }

    public static List<Note> getNotes(Block block){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final List<Note> notelist = new ArrayList<>();
        try{
            db.collection("group/"+userGroup+"day/"+block.getReadableDate()+"/table/"+block.getSubjectName()+"-"+block.getType()+block.getBlockNr()+"note/").get()
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

    public static void putNote(Block block, Note note){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

            //public Note(String title, String description, String author, boolean isPublic) {
        Map<String, Object> newNote = new HashMap<>();
        newNote.put("title",note.getTitle());
        newNote.put("author",note.getAuthor());
        newNote.put("description",note.getDescription());

            db.document("group/"+userGroup+"day/"+block.getReadableDate()+"/table/"+block.getSubjectName()+"-"+block.getType()+block.getBlockNr()+"note/"+note.getAuthor()+note.getTitle())
                    .set(newNote).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(noteAddTAG,"Note added");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(noteAddTAG,"Adding Note failed :: ", e);
                }
            });
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
            Log.w(sublistRetrievalTAG,"Fetching subscription list fail:" + e);
        }
        return grouplist;
    }
}
