package com.wat.student.adkoch.wattable.db.handlers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.wat.student.adkoch.wattable.R;
import com.wat.student.adkoch.wattable.db.activities.DayActivity;
import com.wat.student.adkoch.wattable.db.activities.MainActivity;
import com.wat.student.adkoch.wattable.db.activities.SettingsActivity;
import com.wat.student.adkoch.wattable.db.activities.SubListActivity;
import com.wat.student.adkoch.wattable.db.activities.WeekActivity;

public abstract class BarCompatActivity extends AppCompatActivity {

    protected void setToolbar(Toolbar toolbar){
        setSupportActionBar(toolbar);
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
            goToSettings();
        } else if(id == R.id.action_day_view){
            goToDay();
        } else if(id == R.id.action_week_view){
            goToWeek();
        } else if(id == R.id.action_subs){
            goToSubs();
        } else if(id == R.id.action_logout){
            goToLoginScreen();
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
    private void goToLoginScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        AuthUI.getInstance().signOut(this);
        startActivity(intent);
    }
    private void goToSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
