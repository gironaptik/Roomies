package com.roomies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

public class ChoresActivity extends AppCompatActivity {

    private Toolbar choresToolbar;
    private Intent menuIntent;
    private String code;
    private String apartmentID = "apartmentID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chores);
        choresToolbar = findViewById(R.id.chores_toolbar);
        setSupportActionBar(choresToolbar);
        getSupportActionBar().setTitle("Chores List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        menuIntent = getIntent();
        if(!apartmentID.equals(null)) {
            code = menuIntent.getExtras().getString(apartmentID);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
            Intent newIntent = new Intent(getApplicationContext(),HomeActivity.class);
            newIntent.putExtra(apartmentID, code);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(newIntent);        }

        return super.onOptionsItemSelected(item);
    }


}
