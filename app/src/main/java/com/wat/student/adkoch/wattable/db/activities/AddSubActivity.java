package com.wat.student.adkoch.wattable.db.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.data.DataAccess;
import com.wat.student.adkoch.wattable.db.data.entities.Block;
import com.wat.student.adkoch.wattable.db.data.entities.Subscription;

public class AddSubActivity extends AppCompatActivity {
    Subscription sub;
    Button addSubButton, clearButton, deleteButton;
    EditText subName, subToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub);
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_sub_toolbar);

        subName = findViewById(R.id.subName);
        subToken = findViewById(R.id.subToken);

        addSubButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.delete_sub_button);
        clearButton = findViewById(R.id.clearButton);

        setSupportActionBar(toolbar);

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
        if(name==null || token==null || name==" " || token==" ") return false;
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.action_day_view){
            goToDay();
        } else if(id == R.id.action_week_view){
            goToWeek();
        } else if(id == R.id.action_subs){
            goToSubs();
        }

        return super.onOptionsItemSelected(item);
    }
    private void goToDay(){
        Intent intent = new Intent(this, DayActivity.class);
        startActivity(intent);
    }
    private void goToWeek(){
        Intent intent = new Intent(this, WeekActivity.class);
        startActivity(intent);
    }
    private void goToSubs(){
        Intent intent = new Intent(this, SubListActivity.class);
        startActivity(intent);
    }
}
