package com.wat.student.adkoch.wattable.db.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Settings {

    private static String TAG="Settings";

    private String group;
    private String semester;
    private String token;

    private Timestamp semesterStart;
    private Timestamp semesterEnd;

    public String getGroup(){
        return group;
    }
    public String getSemester(){
        return semester;
    }
    public String getToken(){
        return token;
    }
    public Timestamp getSemesterStart(){
        return semesterStart;
    }
    public Timestamp getSemesterEnd(){
        return semesterEnd;
    }

    private static Settings instance;

    public Query getTimetableQuery(){
        return FirebaseFirestore.getInstance().collection("semester").document(semester).collection(group);
    }

    private Settings(String group, String semester){
        token=getUserToken();
        this.group=group;
        this.semester=semester;
    }

    public Query getDayQuery(Date day){
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("semester/"+semester+"/"+group)
                .whereEqualTo("month",cal.get(Calendar.MONTH)+1)
                .whereEqualTo("day",cal.get(Calendar.DAY_OF_MONTH));
    }

    public static void setInstance(String group, String semester, Timestamp semesterStart, Timestamp semesterEnd){
        instance=new Settings(group,semester);
        instance.putUserInfo(semester,group);
        instance.semesterStart=semesterStart;
        instance.semesterEnd=semesterEnd;
    }

    public static void setInstance(String group, String semester){
        instance=new Settings(group,semester);
        instance.putUserInfo(semester,group);
        instance.setSemesterRanges(semester);
    }
    private void setSemesterRanges(String semester){
        DocumentReference doc = FirebaseFirestore.getInstance().document("semester/"+semester);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot ds = task.getResult();
                    if (ds != null) {
                        semesterStart=(Timestamp) ds.get("semesterStart");
                        semesterEnd=(Timestamp) ds.get("semesterEnd");
                    }

                    Log.d("WeekFetch","Fetch week ranges successful start: "+new SimpleDateFormat("dd/MM/yyyy").format(semesterStart.toDate()) +", end: "+new SimpleDateFormat("dd/MM/yyyy").format(semesterEnd.toDate()));
                }else {
                    Log.w("WeekFetch","Fetchind week ranges unsuccessful");
                }
            }
        });
    }

    public static Settings getInstance(){
        return instance;
    }

    private String getUserToken(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid="";
        try{
            uid = user.getUid();
        }catch (Exception e){
            Log.w("getUid","failed fetching uid: "+e);
        }
        return uid.substring(0,12);
    }

    private void putUserInfo(String semester, String group){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid;
        Map<String, Object> info = new HashMap<>();
        info.put("semester",semester);
        info.put("group", group);
        try{
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection("user")
                    .document(uid)
                    .set(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Log.d(TAG,"Successfuly set new group and semester");
                    } else {
                        Log.w(TAG,"Failed setting new group and semester");
                    }
                }
            });
        }catch (Exception e){
            Log.w("getUid","failed fetching uid: "+e);
        }
    }
}
