package com.roomies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;

public class ChatActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Intent menuIntent;
    private Button mMessagesBtn;
    private Button mShoppingBtn;
    private Button mBillsBtn;
    private Button mAssignmentsBtn;
    private Button mFinancialBtn;
    private String image;
    private String code;
    private String apartmentID = "apartmentID";
    private String imageUrl = "imageUrl";
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chat);
        setBottomNavigator();
        menuIntent = getIntent();
        if(!apartmentID.equals(null)) {
            code = menuIntent.getExtras().getString(apartmentID);
        }
    }

    private void setBottomNavigator(){
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.chat);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch(menuItem.getItemId()){
                case R.id.chat:
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

