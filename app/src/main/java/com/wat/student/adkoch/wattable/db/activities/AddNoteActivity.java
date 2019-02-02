package com.wat.student.adkoch.wattable.db.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.Settings;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.data.entities.Note;

import java.util.HashMap;

public class AddNoteActivity extends AppCompatActivity {

    private Block block;
    private String token;
    private EditText titleInput, descriptionInput;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Button addButton, clearButton;
        TAG=getString(R.string.addNoteActivity_log_TAG);

        addButton=findViewById(R.id.addNoteButton);
        clearButton=findViewById(R.id.clearNoteButton);
        titleInput=findViewById(R.id.noteTitle);
        descriptionInput=findViewById(R.id.note_description);

        token= getUserToken();
        block =(Block) getIntent().getSerializableExtra(getString(R.string.serializable_block_name));

        final Toast addToast = Toast.makeText(this,getString(R.string.adding_note_toast_text),Toast.LENGTH_SHORT);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!titleInput.getText().toString().equals("")&&!descriptionInput.getText().toString().equals("")){
                    putNote(block,new Note(titleInput.getText().toString(),descriptionInput.getText().toString(),token));
                    addToast.show();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleInput.setText("");
                descriptionInput.setText("");
            }
        });
    }
    private void putNote(Block b, Note n){
        final String semester = Settings.getInstance().getSemester();
        final String group = Settings.getInstance().getGroup();
        final Block block = b;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final HashMap<String,Object> note = new HashMap<>();
        note.put(getString(R.string.entity_note_title),n.getTitle());
        note.put(getString(R.string.entity_note_author),n.getAuthor());
        note.put(getString(R.string.entity_note_description),n.getDescription());
        db.collection(getString(R.string.collection_semester))
                .document(semester)
                .collection(group)
                .document(block.getPart()+"-"+block.getMonth()+"-"+block.getDay()+"-"+block.getTimeBlockNr())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    long index=0;
                    try{
                        index=(long) (task.getResult().get(getString(R.string.document_attribute_noteIndex)));

                    }catch (Exception e){
                        Log.w(TAG,"Fetching note index fail: "+e);
                    }
                    index++;
                    FirebaseFirestore.getInstance().collection(getString(R.string.collection_semester))
                            .document(semester)
                            .collection(group)
                            .document(block.getPart()+"-"+block.getMonth()+"-"+block.getDay()+"-"+block.getTimeBlockNr())
                            .collection(getString(R.string.collection_notes))
                            .document("note-"+index).set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG,"Note added successfuly");
                            }
                        }
                    });
                    FirebaseFirestore.getInstance().collection(getString(R.string.collection_semester))
                            .document(semester)
                            .collection(group)
                            .document(block.getPart()+"-"+block.getMonth()+"-"+block.getDay()+"-"+block.getTimeBlockNr())
                            .update(getString(R.string.document_attribute_noteIndex),index);
                    FirebaseFirestore.getInstance().collection(getString(R.string.collection_semester))
                            .document(semester)
                            .collection(group)
                            .document(block.getPart()+"-"+block.getMonth()+"-"+block.getDay()+"-"+block.getTimeBlockNr())
                            .update(getString(R.string.document_attribute_noteCount),index);

                }else {
                    Log.w(TAG,"Fetching block notes");
                }
            }
        });
    }
    private String getUserToken(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid="";
        try{
            uid = user.getUid();
        }catch (Exception e){
            Log.w(TAG,"failed fetching uid: "+e);
        }
        return uid.substring(0,12);
    }
}
