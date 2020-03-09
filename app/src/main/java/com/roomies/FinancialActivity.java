package com.roomies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FinancialActivity extends AppCompatActivity {

    private Toolbar financialToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mApartmentDatabase;
    private Intent incomingIntent;
    private String code;
    private String apartmentID = "apartmentID";

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
        FirebaseUser user = mAuth.getCurrentUser();
        incomingIntent = getIntent();
        if(!apartmentID.equals(null)) {
            code = incomingIntent.getExtras().getString(apartmentID);
        }
        mApartmentDatabase = FirebaseDatabase.getInstance().getReference().child("Apartments").child(code).child("financial");
        PieChart pieChart = findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(true);
        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(40f, "Jan"));
        value.add(new PieEntry(60f, "Fab"));
        PieDataSet pieDataSet = new PieDataSet(value, "months");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);




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
