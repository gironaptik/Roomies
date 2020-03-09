package com.roomies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FinancialActivity extends AppCompatActivity {

    private Toolbar financialToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mApartmentDatabase;
    private DatabaseReference mUserDatabase;
    private Intent incomingIntent;
    private String code;
    private String apartmentID = "apartmentID";
    private String apartmentUsrList = "apartmentUsrList";
    private String apartmentUsrNameList = "apartmentUsrNameList";
    private List<String> usersKeyList;
    private List<String> usersNameList;
    private List<PieEntry> value;
    private PieChart pieChart;
    private PieDataSet pieDataSet;
    private List<User> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial);
        financialToolbar = findViewById(R.id.financial_toolbar);
        setSupportActionBar(financialToolbar);
        getSupportActionBar().setTitle("Financial Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        usersNameList = new ArrayList<>();
//        FirebaseUser user = mAuth.getCurrentUser();
        incomingIntent = getIntent();
        if(!apartmentID.equals(null)) {
            code = incomingIntent.getExtras().getString(apartmentID);
            usersKeyList = getIntent().getStringArrayListExtra(apartmentUsrList);
            usersNameList = getIntent().getStringArrayListExtra(apartmentUsrNameList);
        }
        mApartmentDatabase = FirebaseDatabase.getInstance().getReference().child("Apartments").child(code).child("financial");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        value = new ArrayList<>();
        userList = new ArrayList<>();

//        userList();

        setChart();

//        value.add(new PieEntry(20f, "Jan"));
//        value.add(new PieEntry(60f, "Fab"));


//        pieDataSet = new PieDataSet(value, "Balance");

//        for(int i=0; i <= usersKeyList.size() ; i++) {
//            if (i < usersKeyList.size()){
//                mUserDatabase.child(usersKeyList.get(i)).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        User user = dataSnapshot.getValue(User.class);
//                        value.add(new PieEntry(5f, user.getName()));
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//            else {
//
//            }
//        }







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

    private void setChart(){
        pieChart = findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(true);
        Description desc = new Description();
        desc.setText("");
        desc.setTextSize(20f);
        for(String name : usersNameList){
            value.add(new PieEntry(20f, name));
            pieChart.notifyDataSetChanged();
        }
        pieChart.setDescription(desc);
        pieDataSet = new PieDataSet(value, "Balance");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
    }

//    private void userList() {
//        userList.clear();
//        value.clear();
//        for(String key : usersKeyList) {
//            mUserDatabase.child(key).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    User user = dataSnapshot.getValue(User.class);
//                    userList.add(user);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                }
//            });
//        }
//        for(User user : userList){
//            value.add(new PieEntry(20f, user.getName()));
//        }
//        setChart();
//    }
}
