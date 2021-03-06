package com.wat.student.adkoch.wattable.db.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wat.student.adkoch.wattable.R;

import java.util.HashMap;
import java.util.Map;

public class SubscriptionMapper {
    private Map<String,String> subMapper;
    private final String TAG="SubscriptionMapper";

    private static SubscriptionMapper instance;

    private SubscriptionMapper(){
        subMapper=new HashMap<>();
    }

    public static SubscriptionMapper getInstance(){
        if(null==instance){
            instance=new SubscriptionMapper();
        }
        return instance;
    }

    public String getSubTitle(String token){
        return subMapper.get(token);
    }

    public boolean contains(String s){
        return instance.subMapper.containsKey(s);
    }

    public Map<String,String> getSubs(){
        return instance.subMapper;
    }

    public void setSubs(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Context subContext=context;
        try{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String uid = user.getUid();
            final Map<String,String> mapper=new HashMap<>();
            db.collection(subContext.getString(R.string.collection_user))
                    .document(uid)
                    .collection(subContext.getString(R.string.collection_sublist))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot document: task.getResult()){
                                    mapper.put(uid.substring(0,12),subContext.getString(R.string.user_self));
                                    mapper.put(document.get(subContext.getString(R.string.entity_subscription_token)).toString(),
                                            document.get(subContext.getString(R.string.entity_subscription_title)).toString());
                                    Log.d(TAG,"Successful retrieval of"+ document.getId());
                                }
                                SubscriptionMapper.getInstance().subMapper=mapper;
                            } else {
                                Log.w(TAG,"Failed retrieval of document: "+ task.getException());
                            }
                        }
                    });
        } catch(Exception e){
            Log.w(TAG,"Fetching subscription list fail:" + e);
        }
    }
}
