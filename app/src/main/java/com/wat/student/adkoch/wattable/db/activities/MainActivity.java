package com.wat.student.adkoch.wattable.db.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.DataAccess;
import com.wat.student.adkoch.wattable.db.data.Settings;
import com.wat.student.adkoch.wattable.db.data.SubscriptionMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button loginButton;

    private static String TAG="Main/loginActivity";
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton=findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSignInIntent();
            }
        });

        DataAccess.setSemesterRanges();
        createSignInIntent();
    }

    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        AuthUI.getInstance().signOut(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                setSettings();
                Intent intent = new Intent(this, DayActivity.class);
                Toast.makeText(this,"Pomyślnie zalogowano!",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            } else {

                if(null==response){
                    Toast.makeText(this,"Logowanie przerwano",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,"Błąd logowania",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setSettings(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SubscriptionMapper.getInstance().setSubs();
        String uid="";
        try{
            uid = user.getUid();
        }catch (Exception e){
            Log.w(TAG,"failed fetching uid: "+e);
        }
            try{
                db.collection("user")
                        .document(uid)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    Map<String,Object> fields = ((DocumentSnapshot) task.getResult()).getData();
                                    if(null==fields){
                                        Settings.setInstance("I6B2S1","2018Zima");
                                    } else {
                                        if(null==fields.get("semester")||null==fields.get("semester")){
                                            Settings.setInstance("I6B2S1","2018Zima");
                                        } else {
                                            String semester= (String) fields.get("semester");
                                            String group= (String) fields.get("semester");
                                            Settings.setInstance(group, semester);
                                        }
                                    }
                                    Log.d(TAG,"Successful retrieval of"+ task.getResult().getId());
                                } else {
                                    Settings.setInstance("I6B2S1","2018Zima");
                                    Log.w(TAG,"Failed retrieval of document: "+ task.getException());
                                }
                            }
                        });
            } catch(Exception e){
                Log.w(TAG,"Fetching group list fail:" + e);
            }
        }
}
