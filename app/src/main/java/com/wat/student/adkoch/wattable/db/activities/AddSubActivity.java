package com.wat.student.adkoch.wattable.db.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.DataAccess;
import com.wat.student.adkoch.wattable.db.data.SubscriptionMapper;
import com.wat.student.adkoch.wattable.db.data.entities.Subscription;
import com.wat.student.adkoch.wattable.db.handlers.BarCompatActivity;

public class AddSubActivity extends BarCompatActivity {
    Subscription sub;
    Button addSubButton, clearButton, deleteButton;
    EditText subName, subToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub);
        setToolbar((Toolbar) findViewById(R.id.add_sub_toolbar));

        subName = findViewById(R.id.subName);
        subToken = findViewById(R.id.subToken);

        addSubButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.delete_sub_button);
        clearButton = findViewById(R.id.clearButton);

        sub =(Subscription) getIntent().getSerializableExtra("sub");
        checkState();

        final Toast successfullAddToast= Toast.makeText(this,"Pomyślnie dodano subskrypcję!",Toast.LENGTH_SHORT);
        final Toast successfullDeleteToast= Toast.makeText(this,"Pomyślnie usunięto subskrypcję!",Toast.LENGTH_SHORT);
        final Toast successfullEditToast= Toast.makeText(this,"Pomyślnie zedytowano subskrypcję!",Toast.LENGTH_SHORT);
        addSubButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(checkFields(subName.getText().toString(), subToken.getText().toString())){

                    if(null!=sub){
                        DataAccess.deleteSub(sub);
                        successfullEditToast.show();
                    } else {
                        successfullAddToast.show();
                    }

                    addSub(subName.getText().toString(), subToken.getText().toString());
                    goToSubs();
                    SubscriptionMapper.getInstance().setSubs();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                subName.setText("");
                subToken.setText("");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataAccess.deleteSub(sub);
                successfullDeleteToast.show();
                goToSubs();
                SubscriptionMapper.getInstance().setSubs();
            }
        });
    }

    private void checkState(){
        if(null==sub){
            deleteButton.setVisibility(View.INVISIBLE);
            subName.setText("");
            subToken.setText("");
        } else {
            deleteButton.setVisibility(View.VISIBLE);
            subName.setText(sub.getTitle());
            subToken.setText(sub.getToken());
            addSubButton.setText("Edytuj");
        }
    }
    private void addSub(String subName, String subToken){
        DataAccess.putSub(new Subscription(subName, subToken));
    }
    private boolean checkFields(String name, String token){
        if(!token.matches("[0-9a-zA-Z]{12}") || token.contains(" ")){
            Toast.makeText(this,"Źle wprowadzono token!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!name.matches("[0-9a-zA-Z]")){
            Toast.makeText(this,"Źle wprowadzono nazwę!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void goToSubs(){
        Intent intent = new Intent(this, SubListActivity.class);
        startActivity(intent);
    }
}
