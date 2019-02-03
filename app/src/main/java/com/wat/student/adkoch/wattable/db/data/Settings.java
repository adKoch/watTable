package com.wat.student.adkoch.wattable.db.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wat.student.adkoch.wattable.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Settings {

    private static String TAG="Settings";

    private String group;
    private String semester;

    private Timestamp semesterStart;
    private Timestamp semesterEnd;

    public String getGroup(){
        return group;
    }
    public String getSemester(){
        return semester;
    }

    public Timestamp getSemesterStart(){
        return semesterStart;
    }
    public Timestamp getSemesterEnd(){
        return semesterEnd;
    }

    private static Settings instance;

    private Settings(String group, String semester){
        this.group=group;
        this.semester=semester;
    }

    public static void setInstance(String group, String semester, Timestamp semesterStart, Timestamp semesterEnd,Context context){
        instance=new Settings(group,semester);
        instance.putUserInfo(semester,group,context);
        instance.semesterStart=semesterStart;
        instance.semesterEnd=semesterEnd;
    }

    public static void setInstance(String group, String semester, Context context){
        instance=new Settings(group,semester);
        instance.putUserInfo(semester,group, context);
        instance.setSemesterRanges(semester, context);
    }
    private void setSemesterRanges(String semester, Context context){
        final Context semContext=context;
        DocumentReference doc = FirebaseFirestore.getInstance()
                .collection(context.getString(R.string.collection_semester))
                .document(semester);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot ds = task.getResult();
                    if (ds != null) {
                        semesterStart=(Timestamp) ds.get(semContext.getString(R.string.semester_ranges_start));
                        semesterEnd=(Timestamp) ds.get(semContext.getString(R.string.semester_ranges_end));
                    }

                    Log.d("WeekFetch","Fetch week ranges successful start: "+new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(semesterStart.toDate()) +", end: "+new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(semesterEnd.toDate()));
                }else {
                    Log.w("WeekFetch","Fetchind week ranges unsuccessful");
                }
            }
        });
    }

    public static Settings getInstance(){
        return instance;
    }

    private void putUserInfo(String semester, String group, Context context){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid;
        Map<String, Object> info = new HashMap<>();
        info.put(context.getString(R.string.user_info_semester),semester);
        info.put(context.getString(R.string.user_info_group), group);
        try{
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection(context.getString(R.string.collection_user))
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
