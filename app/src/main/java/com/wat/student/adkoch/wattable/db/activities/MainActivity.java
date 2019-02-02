package com.wat.student.adkoch.wattable.db.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.Settings;
import com.wat.student.adkoch.wattable.db.data.SubscriptionMapper;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static String TAG="Main/loginActivity";
    private static final int RC_SIGN_IN = 123;
    private ProgressBar mainProgressBar;
    private Button loginButton;
    private ImageView googleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        googleImage = findViewById(R.id.googleImage);
        mainProgressBar=findViewById(R.id.main_spinner);
        loginButton=findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSignInIntent();
            }
        });

        createSignInIntent();
    }

    private void switchVisibility(){
        loginButton.setVisibility(View.INVISIBLE);
        googleImage.setVisibility(View.INVISIBLE);
        mainProgressBar.setVisibility(View.VISIBLE);
    }

    private void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

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
                setSettings();
            } else {

                if(null==response){
                    Toast.makeText(this,"Logowanie przerwano",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,"Błąd logowania",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setSettings() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SubscriptionMapper.getInstance().setSubs();
        String uid = "";
        switchVisibility();
        try {
            uid = user.getUid();
        } catch (Exception e) {
            Log.w(TAG, "failed fetching uid: " + e);
        }
        try {
            db.collection("user")
                    .document(uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Map<String, Object> fields = (task.getResult()).getData();
                                if (null == fields) {
                                    setDefaultSettings();
                                } else {
                                    if (null == fields.get("semester") || null == fields.get("semester")) {
                                        setDefaultSettings();
                                    } else {
                                        final String semester = (String) fields.get("semester");
                                        final String group = (String) fields.get("group");
                                        DocumentReference doc = FirebaseFirestore.getInstance().document("semester/"+semester);
                                        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    DocumentSnapshot ds = task.getResult();
                                                    if (ds != null) {
                                                        Timestamp semesterStart=(Timestamp) ds.get("semesterStart");
                                                        Timestamp semesterEnd=(Timestamp) ds.get("semesterEnd");
                                                        Settings.setInstance(group, semester,semesterStart, semesterEnd);
                                                        Log.d("WeekFetch","Fetch week ranges successful start: "
                                                                +new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(semesterStart.toDate()) +", end: "+new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(semesterEnd.toDate()));
                                                    }
                                                }else {
                                                    Log.w("WeekFetch","Fetching week ranges unsuccessful");
                                                }
                                                finishSignIn();
                                            }
                                        });
                                    }
                                }
                                Log.d(TAG, "Successful retrieval of" + task.getResult().getId());
                            } else {
                                setDefaultSettings();
                                Log.w(TAG, "Failed retrieval of document: " + task.getException());
                            }
                        }
                    });
        } catch (Exception e) {
            Log.w(TAG, "Fetching group list fail:" + e);
        }
    }

    private void setDefaultSettings(){
        final String defaultSemester = "2018Zima";
        final String defaultGroup = "I6B2S1";
        final Timestamp defaultSemesterStart = new Timestamp(new GregorianCalendar(2018,9,1).getTime());
        final Timestamp defaultSemesterEnd= new Timestamp(new GregorianCalendar(2019,2,28).getTime());
        Settings.setInstance(defaultGroup,defaultSemester,defaultSemesterStart,defaultSemesterEnd);
    }

    private void finishSignIn(){
        Intent intent = new Intent(this, DayActivity.class);
        Toast.makeText(this,"Pomyślnie zalogowano!",Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    @Override
    protected void onStop(){
        super.onStop();
        finish();
    }
}
