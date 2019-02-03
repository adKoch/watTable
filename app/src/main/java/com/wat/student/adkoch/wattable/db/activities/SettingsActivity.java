package com.wat.student.adkoch.wattable.db.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.Settings;
import com.wat.student.adkoch.wattable.db.data.SubscriptionMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity{

    private String TAG;

    private Spinner groupSpinner, semesterSpinner;
    private TextView tokenTextView, yourTokenTextView, semesterTextView, groupTextView;
    private Button downloadSubsButton, setButton;
    private ProgressBar settingsProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TAG=getResources().getString(R.string.SettingsActivity_log_TAG);

        settingsProgressBar =findViewById(R.id.settings_spinner);
        groupSpinner = findViewById(R.id.group_spinner);
        semesterSpinner= findViewById(R.id.semester_spinner);
        tokenTextView = findViewById(R.id.your_token);
        downloadSubsButton = findViewById(R.id.download_sub_list_button);
        yourTokenTextView = findViewById(R.id.your_token_text);
        setButton= findViewById(R.id.set_button);
        semesterTextView = findViewById(R.id.semester_text);
        groupTextView =findViewById(R.id.group_text);

        loadSemesters();
        setToken();

        setSupportActionBar((Toolbar) findViewById(R.id.settings_toolbar));

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.setInstance(groupSpinner.getSelectedItem().toString(),semesterSpinner.getSelectedItem().toString(),getApplicationContext());
            }
        });

        downloadSubsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printPdf();
            }
        });
    }

    private void setGroups(Spinner groupSpinner, List<String> groups){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,groups);
        groupSpinner.setAdapter(adapter);
    }

    private void setSemesters(Spinner semesterSpinner, List<String> semesters){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,semesters);
        semesterSpinner.setAdapter(adapter);
    }

    private void switchVisibility(){

        settingsProgressBar.setVisibility(View.VISIBLE);
        groupSpinner.setVisibility(View.VISIBLE);
        semesterSpinner.setVisibility(View.VISIBLE);
        tokenTextView.setVisibility(View.VISIBLE);
        downloadSubsButton.setVisibility(View.VISIBLE);
        yourTokenTextView.setVisibility(View.VISIBLE);
        setButton.setVisibility(View.VISIBLE);
        semesterTextView.setVisibility(View.VISIBLE);
        groupTextView.setVisibility(View.VISIBLE);
        settingsProgressBar.setVisibility(View.INVISIBLE);
    }

    private void loadGroups(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<String> groupList = new ArrayList<>();
        try{
            db.collection(getString(R.string.collection_groups))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){

                                for(DocumentSnapshot doc : task.getResult()){
                                    groupList.add(doc.getId());
                                    Log.d(TAG,"Successful retrieval of: "+ doc.getId());
                                }
                                setGroups(groupSpinner, groupList);
                                switchVisibility();
                            } else {
                                Log.w(TAG,"Failed retrieval of document: "+ task.getException());
                            }
                        }
                    });
        } catch(Exception e){
            Log.w(TAG,getString(R.string.Settings_log_group_list_fail) + e);
        }
    }

    private void loadSemesters(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<String> semesterList = new ArrayList<>();
        try{
            db.collection(getString(R.string.collection_semester))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot document: task.getResult()){
                                    String id=document.getId();
                                    semesterList.add(id);
                                    Log.d(TAG,"Successful retrieval of"+ document.getId());
                                    setSemesters(semesterSpinner,semesterList);
                                    loadGroups();
                                    setToken();
                                }
                            } else {
                                Log.w(TAG,"Failed retrieval of document: "+ task.getException());
                            }
                        }
                    });
        } catch(Exception e){
            Log.w(TAG,"Fetching semester list fail:" + e);
        }
    }

    private void setToken() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = "";
        try {
            uid = user.getUid();
        } catch (Exception e) {
            Log.w(TAG, getString(R.string.Settings_log_failed_fetching_uid) + e);
        }
        tokenTextView.setText(uid.substring(0,12));
    }


    private boolean checkWritePermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE=124;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }

    private void printPdf(){

        File file = new File(getString(R.string.pdf_directory));

        if(!checkWritePermission()){
            return;
        }
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));

            document.open();

            document.setPageSize(PageSize.A5);
            document.addCreationDate();
            document.addCreator(getString(R.string.pdf_creator));

            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            Chunk mOrderIdChunk = new Chunk(getString(R.string.pdf_title), new Font());
            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderIdParagraph);
            for(Map.Entry<String,String> entry:SubscriptionMapper.getInstance().getSubs().entrySet()){
                document.add(new Chunk(lineSeparator));
                document.add(new Paragraph(entry.getKey()+"-"+entry.getValue()));
            }
            document.close();
            Toast.makeText(this,getString(R.string.toast_pdf_generation_success),Toast.LENGTH_SHORT).show();
        } catch (DocumentException e) {
            Toast.makeText(this,getString(R.string.toast_pdf_saving_document_fail),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Toast.makeText(this,getString(R.string.toast_pdf_opening_file_fail),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if(id == R.id.action_day_view){
            Intent intent = new Intent(this, DayActivity.class);
            startActivity(intent);
        } else if(id == R.id.action_week_view){
            Intent intent = new Intent(this, WeekActivity.class);
            startActivity(intent);
        } else if(id == R.id.action_subs){
            Intent intent = new Intent(this, SubListActivity.class);
            startActivity(intent);
        } else if(id == R.id.action_logout){
            Intent intent = new Intent(this, MainActivity.class);
            AuthUI.getInstance().signOut(this);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
