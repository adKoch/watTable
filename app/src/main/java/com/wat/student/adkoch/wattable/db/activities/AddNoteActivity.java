package com.wat.student.adkoch.wattable.db.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.DataAccess;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.data.entities.Note;
import com.wat.student.adkoch.wattable.db.handlers.BarCompatActivity;

public class AddNoteActivity extends BarCompatActivity {

    private Block block;
    private String token;
    private EditText titleInput, descriptionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Button addButton, clearButton;
        setToolbar((Toolbar) findViewById(R.id.add_note_toolbar));

        addButton=findViewById(R.id.addNoteButton);
        clearButton=findViewById(R.id.clearNoteButton);
        titleInput=findViewById(R.id.noteTitle);
        descriptionInput=findViewById(R.id.note_description);

        token= DataAccess.getUserToken();
        block =(Block) getIntent().getSerializableExtra("block");

        final Toast addToast = Toast.makeText(this,"Dodaję notatkę...",Toast.LENGTH_SHORT);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!titleInput.getText().toString().equals("")&&!descriptionInput.getText().toString().equals("")){
                    DataAccess.putNote(block,new Note(titleInput.getText().toString(),descriptionInput.getText().toString(),token));
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
}
