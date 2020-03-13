package com.roomies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roomies.Model.ApartmentBalance;
import com.roomies.Model.ChoreData;
import com.roomies.Model.FinanceData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FinancialActivity extends AppCompatActivity implements  DatePickerDialog.OnDateSetListener{

    private Toolbar financialToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mApartmentDatabase;
    private DatabaseReference mApartmentUserDatabase;
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
    private List<ApartmentBalance> balanceList;
    private FloatingActionButton fab_btn;
    private RecyclerView recyclerView;
    private String billTypeName;
    private String postKey;
    private LinearLayout usersBalanceLayout;
    private final Calendar myCalendar = Calendar.getInstance();
    private EditText from;
    private EditText to;
    private Calendar startDate;
    private Calendar endDate;
    static final int DATE_DIALOG_ID = 0;
    private int start = 0;
    private int end = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial);
        financialToolbar = findViewById(R.id.financial_toolbar);
        setSupportActionBar(financialToolbar);
        getSupportActionBar().setTitle("Financial Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        usersNameList = new ArrayList<>();
        balanceList = new ArrayList<>();
        value = new ArrayList<>();
        incomingIntent = getIntent();
        if(!apartmentID.equals(null)) {
            code = incomingIntent.getExtras().getString(apartmentID);
            usersKeyList = getIntent().getStringArrayListExtra(apartmentUsrList);
            usersNameList = getIntent().getStringArrayListExtra(apartmentUsrNameList);
        }
        mApartmentDatabase = FirebaseDatabase.getInstance().getReference().child("Apartments").child(code).child("financial");
        mApartmentUserDatabase = FirebaseDatabase.getInstance().getReference().child("Apartments").child(code).child("financialBalance");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        fab_btn = findViewById(R.id.add_finance_button);
        mApartmentDatabase.keepSynced(true);
        setChart();
        recyclerView = findViewById(R.id.recyceler_financial);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        fab_btn.setOnClickListener(view -> customeDialog());
        usersBalanceLayout = findViewById(R.id.balanceLayout);
        balanceList();



//        userList();


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

    private void setChart(){
        pieChart = findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(true);
        Description desc = new Description();
        desc.setText("");
        desc.setTextSize(20f);
//        for(String name : usersNameList){
//            value.add(new PieEntry(20f, name));
//            pieChart.notifyDataSetChanged();
//        }
        pieChart.setDescription(desc);
        pieDataSet = new PieDataSet(value, "Balance");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
    }

    private void updateChart(){
//        pieDataSet = new PieDataSet(value, "Balance");
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
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

    private void customeDialog(){

        AlertDialog.Builder myDialog = new AlertDialog.Builder(FinancialActivity.this);
        LayoutInflater inflater = LayoutInflater.from(FinancialActivity.this);
        View myView = inflater.inflate(R.layout.input_finance_data, null);
        AlertDialog dialog = myDialog.create();
        dialog.setView(myView);

        Spinner billType = myView.findViewById(R.id.edt_bill_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.finance_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        billType.setAdapter(adapter);
        billType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                billTypeName = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        from = myView.findViewById(R.id.edt_from);
        to = myView.findViewById(R.id.edt_to);
//        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                  int dayOfMonth) {
//                // TODO Auto-generated method stub
//                myCalendar.set(Calendar.YEAR, year);
//                myCalendar.set(Calendar.MONTH, monthOfYear);
//                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//            }
//
//        };

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                new DatePickerDialog(FinancialActivity.this, date, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//                        String myFormat = "MM/dd/yy"; //In which you need put here
//                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//                        to.setText(sdf.format(myCalendar.getTime()));
////                        updateLabel(to);
                showDatePickerDialog(end);
            }
        });

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                new DatePickerDialog(FinancialActivity.this, date, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//                        String myFormat = "MM/dd/yy"; //In which you need put here
//                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//                        from.setText(sdf.format(myCalendar.getTime()));
                showDatePickerDialog(start);
            }
        });

        EditText sum = myView.findViewById(R.id.edt_sum);
        Button btnSave = myView.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mBillTypeName = billTypeName;
                String mSum = sum.getText().toString().trim();
                String mFrom = from.getText().toString().trim();
                String mTo = to.getText().toString().trim();

                if (TextUtils.isEmpty(mSum)) {
                    sum.setError("Required failed..");
                    return;
                }
                if (TextUtils.isEmpty(mFrom)) {
                    from.setError("Required failed..");
                    return;
                }
                if (TextUtils.isEmpty(mTo)) {
                    to.setError("Required failed..");
                    return;
                }

                String id = mApartmentDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                FinanceData data = new FinanceData(mBillTypeName, mSum, mFrom, mTo, id);
                mApartmentDatabase.child(id).setValue(data);    ////
                Toast.makeText(getApplicationContext(), "Data Add", Toast.LENGTH_SHORT);
                dialog.dismiss();

            }
        });

        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<FinanceData, FinancialActivity.MyViewHolder> adapter = new FirebaseRecyclerAdapter<FinanceData, FinancialActivity.MyViewHolder>(
                FinanceData.class,
                R.layout.finance_data,
                FinancialActivity.MyViewHolder.class,
                mApartmentDatabase
        ) {


            @Override
            protected void populateViewHolder(FinancialActivity.MyViewHolder myViewHolder, FinanceData model, int i) {
                myViewHolder.setType(model.getBillTypeName());
                myViewHolder.setSum(model.getSum());
                myViewHolder.setFrom(model.getFrom());
                myViewHolder.setTo(model.getTo());
//                myViewHolder.setAccept(code);

                Button acceptButton = myViewHolder.my_view.findViewById(R.id.finance_accept_button);
                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        postKey = getRef(i).getKey();
                        mApartmentUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long change = Long.parseLong(model.getSum()) / usersKeyList.size();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if(mAuth.getCurrentUser().getUid().equals(snapshot.getKey())){
                                        long userBalance = (long) dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("balance").getValue();
                                        userBalance += Long.parseLong(model.getSum());
                                        mApartmentUserDatabase.child(mAuth.getCurrentUser().getUid()).child("balance").setValue(userBalance);
                                    }
                                    else{
                                        long userBalance = (long) dataSnapshot.child(snapshot.getKey()).child("balance").getValue();
                                        userBalance -= change;
                                        mApartmentUserDatabase.child(snapshot.getKey()).child("balance").setValue(userBalance);
                                    }
                                }
                                balanceList();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
//                        balanceList();
                        deleteChore();
                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);
    }

    private void deleteChore() {
        mApartmentDatabase.child(postKey).removeValue();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        String myFormat = month + "/" + dayOfMonth + "/" + year;
        if(datePicker.getTag().equals(start)) {
            from.setText(myFormat);
        }
        else {
            to.setText(myFormat);
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View my_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            my_view = itemView;
        }

        public void setType(String type){
            TextView mType = my_view.findViewById(R.id.finance_type);
            mType.setText(type);
        }

        public void setSum(String sum){
            TextView mSum = my_view.findViewById(R.id.finance_sum);
            mSum.setText(sum);
        }

        public void setFrom(String from){
            TextView mFrom = my_view.findViewById(R.id.finance_date_from);
            mFrom.setText(from);
        }

        public void setTo(String to){
            TextView mNote = my_view.findViewById(R.id.finance_date_to);
            mNote.setText(to);
        }

    }

    private void balanceList(){
        balanceList.clear();
        usersNameList.clear();
        usersBalanceLayout.removeAllViews();
        value.clear();
        for(String key : usersKeyList) {
            mApartmentUserDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ApartmentBalance userBalance = dataSnapshot.getValue(ApartmentBalance.class);
                    balanceList.add(userBalance);
                    try {
                        TextView currentBalance = new TextView(getApplicationContext());
                        currentBalance.setText("\u25CF " + userBalance.getName() + ": " + userBalance.getBalance() + " ");
                        currentBalance.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.proximabold);
                        currentBalance.setTypeface(typeface);
                        if(userBalance.getBalance() <= 0)
                        {
                            value.add(new PieEntry(1, userBalance.getName()));
                            currentBalance.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.minus));
                        }
                        else {
                            value.add(new PieEntry(userBalance.getBalance(), userBalance.getName()));
                            currentBalance.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.plus));

                        }
                        updateChart();
                        usersBalanceLayout.addView(currentBalance);
                    }
                    catch (NullPointerException o){
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

    }

    private void showDatePickerDialog(int i){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setTag(i);
        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
