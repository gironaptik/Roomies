package com.roomies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigationView;
    private String image;
    private String code;
    private String apartmentID = "apartmentID";
    private String imageUrl = "imageUrl";
    private Intent menuIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setBottomNavigator();
        menuIntent = getIntent();
        if(!apartmentID.equals(null)) {
            code = menuIntent.getExtras().getString(apartmentID);
        }
    }


    private void setBottomNavigator(){
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.settings);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch(menuItem.getItemId()){
                case R.id.settings:
                    return true;
            }
            switch(menuItem.getItemId()){
                case R.id.profile:
                    Intent newIntent = new Intent(getApplicationContext(),ProfileActivity.class);
                    newIntent.putExtra(apartmentID, code);
                    startActivity(newIntent);
                    overridePendingTransition(0,0);
                    return true;
            }
            switch(menuItem.getItemId()){
                case R.id.home:
                    Intent newIntent = new Intent(getApplicationContext(),HomeActivity.class);
                    newIntent.putExtra(apartmentID, code);
                    startActivity(newIntent);
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });
    }

}
