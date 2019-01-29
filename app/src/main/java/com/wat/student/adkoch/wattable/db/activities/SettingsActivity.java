package com.wat.student.adkoch.wattable.db.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.Settings;
import com.wat.student.adkoch.wattable.db.data.SubscriptionMapper;
import com.wat.student.adkoch.wattable.db.handlers.BarCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends BarCompatActivity {

    private static String TAG="SettingsActivity";

    private Spinner groupSpinner, semesterSpinner;
    private TextView token, tokenText, semesterText, groupText;
    private Button downloadSubs, setButton;
    private ProgressBar settingsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settingsSpinner=(ProgressBar) findViewById(R.id.settings_spinner);
        groupSpinner = (Spinner) findViewById(R.id.group_spinner);
        semesterSpinner=(Spinner) findViewById(R.id.semester_spinner);
        token=(TextView) findViewById(R.id.your_token);
        downloadSubs=(Button) findViewById(R.id.download_sub_list_button);
        tokenText=(TextView) findViewById(R.id.your_token_text);
        setButton=(Button) findViewById(R.id.set_button);
        semesterText=(TextView) findViewById(R.id.semester_text);
        groupText=(TextView) findViewById(R.id.group_text);

        loadSemesters();
        setToken();

        setToolbar((Toolbar) findViewById(R.id.settings_toolbar));

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.setInstance(groupSpinner.getSelectedItem().toString(),semesterSpinner.getSelectedItem().toString());
            }
        });

        downloadSubs.setOnClickListener(new View.OnClickListener() {
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

        settingsSpinner.setVisibility(View.VISIBLE);
        groupSpinner.setVisibility(View.VISIBLE);
        semesterSpinner.setVisibility(View.VISIBLE);
        token.setVisibility(View.VISIBLE);
        downloadSubs.setVisibility(View.VISIBLE);
        tokenText.setVisibility(View.VISIBLE);
        setButton.setVisibility(View.VISIBLE);
        semesterText.setVisibility(View.VISIBLE);
        groupText.setVisibility(View.VISIBLE);
        settingsSpinner.setVisibility(View.INVISIBLE);
    }

    private void loadGroups(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<String> grouplist = new ArrayList<>();
        try{
            db.collection("groups")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){

                                for(DocumentSnapshot doc : task.getResult()){
                                    grouplist.add(doc.getId());
                                    Log.d(TAG,"Successful retrieval of: "+ doc.getId());
                                }
                                setGroups(groupSpinner, grouplist);
                                switchVisibility();
                            } else {
                                Log.w(TAG,"Failed retrieval of document: "+ task.getException());
                            }
                        }
                    });
        } catch(Exception e){
            Log.w(TAG,"Fetching group list fail:" + e);
        }
    }

    private void loadSemesters(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<String> semesterlist = new ArrayList<>();
        try{
            db.collection("semester")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot document: task.getResult()){
                                    String id=document.getId();
                                    semesterlist.add(id);
                                    Log.d(TAG,"Successful retrieval of"+ document.getId());
                                    setSemesters(semesterSpinner,semesterlist);
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
            Log.w(TAG, "failed fetching uid: " + e);
        }
        token.setText(uid.substring(0,12));
    }

    private void printPdf(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE=124;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
        Map<String,String> subs = SubscriptionMapper.getInstance().getSubs();
        BaseFont myFont;
        Document document = new Document();
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Subskrypcje.pdf");
            if(!file.mkdirs()){
                Toast.makeText(this,"Błąd otwierania pliku",Toast.LENGTH_SHORT).show();
                return;
            }
            PdfWriter.getInstance(document, new FileOutputStream(file));

            myFont = BaseFont.createFont();

            document.open();
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addCreator("WAT Rozkład");

            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            Font mSublistDetailsTitleFont = new Font(myFont, 36.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mSublistDetailsTitleChunk = new Chunk("Lista subskrypcji", mSublistDetailsTitleFont);
            Paragraph mSublistDetailsTitleParagraph = new Paragraph(mSublistDetailsTitleChunk);
            mSublistDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);

            document.add(mSublistDetailsTitleParagraph);


            Map<String,String> sublist =  SubscriptionMapper.getInstance().getSubs();
            Iterator it = sublist.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry pair = (Map.Entry) it.next();
                document.add(new Chunk(lineSeparator));
                document.add(new Paragraph(pair.getValue() +" - "+pair.getKey()));
            }

            document.close();
            Toast.makeText(this,"Pomyślnie wygenerowano dokument!",Toast.LENGTH_SHORT).show();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(java.io.IOException e){
            e.printStackTrace();
        }

    }
}
