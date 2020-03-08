package com.roomies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roomies.Model.ChoreData;

import java.text.DateFormat;
import java.util.Date;

public class ChoresActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar choresToolbar;
    private DatabaseReference mApartmentDatabase;
    private RecyclerView recyclerView;
    private FloatingActionButton fab_btn;
    private Intent menuIntent;
    private String code;
    private String apartmentID = "apartmentID";
    private TextView amountText;
    private String by;
    private int amount;
    private String note;
    private String postKey;
    private String choreKind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chores);
        choresToolbar = findViewById(R.id.chores_toolbar);
        setSupportActionBar(choresToolbar);
        getSupportActionBar().setTitle("Chores List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        menuIntent = getIntent();
        if(!apartmentID.equals(null)) {
            code = menuIntent.getExtras().getString(apartmentID);
        }
        mApartmentDatabase = FirebaseDatabase.getInstance().getReference().child("Apartments").child(code).child("choreslist");
        fab_btn = findViewById(R.id.add_chore_button);
        mApartmentDatabase.keepSynced(true);
        recyclerView = findViewById(R.id.recyceler_chores);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customeDialog();
            }
        });
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

    private void customeDialog(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(ChoresActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ChoresActivity.this);
        View myView = inflater.inflate(R.layout.input_chores_data, null);
        AlertDialog dialog = myDialog.create();
        dialog.setView(myView);

        TextView by = myView.findViewById(R.id.edt_by);
        by.setText(mAuth.getCurrentUser().getDisplayName());
        EditText amount = myView.findViewById(R.id.edt_amount);
        EditText note = myView.findViewById(R.id.edt_note);
        Spinner choreSpinner = myView.findViewById(R.id.edt_kind);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.chores_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choreSpinner.setAdapter(adapter);
        choreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                choreKind = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Button btnSave = myView.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mBy = by.getText().toString().trim();
                String mKind = choreKind;
                String mNote = note.getText().toString().trim();

                    if (TextUtils.isEmpty(mBy)) {
                        by.setError("Required failed..");
                        return;
                    }
                    if (TextUtils.isEmpty(mKind)) {
                        by.setError("Required failed..");
                        return;
                    }
                    if (TextUtils.isEmpty(mNote)) {
                        by.setError("Required failed..");
                        return;
                    }

                    String id = mApartmentDatabase.push().getKey();
                    String mDate = DateFormat.getDateInstance().format(new Date());
                    ChoreData data = new ChoreData(mBy, choreKind, mNote, mDate, id);
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
        FirebaseRecyclerAdapter<ChoreData, ChoresActivity.MyViewHolder> adapter = new FirebaseRecyclerAdapter<ChoreData, ChoresActivity.MyViewHolder>(
                ChoreData.class,
                R.layout.chore_data,
                ChoresActivity.MyViewHolder.class,
                mApartmentDatabase
        ) {


            @Override
            protected void populateViewHolder(ChoresActivity.MyViewHolder myViewHolder, ChoreData model, int i) {
                myViewHolder.setDate(model.getDate());
                myViewHolder.setTitle(choreKind);
                myViewHolder.setUserName(mAuth.getCurrentUser().getDisplayName());
                myViewHolder.setNote(model.getNote());
//                myViewHolder.setAccept(code);

                Button acceptButton = myViewHolder.my_view.findViewById(R.id.chore_accept_button);
                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        postKey = getRef(i).getKey();
                        deleteChore();
                    }
                });
//                myViewHolder.my_view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        postKey = getRef(i).getKey();
//                        by = model.getType();
//                        note = model.getNote();
////                        amount = model.getAmount();
//
//                        updateChore();
//                    }
//                });
            }

        };

        recyclerView.setAdapter(adapter);
    }

    private void deleteChore() {
        mApartmentDatabase.child(postKey).removeValue();
    }

    public void updateChore(){

        AlertDialog.Builder myDialog = new AlertDialog.Builder(ChoresActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ChoresActivity.this);
        View mView = inflater.inflate(R.layout.update_inputfield, null);
        AlertDialog dialog = myDialog.create();
        dialog.setView(mView);

        final EditText edtType = mView.findViewById(R.id.edt_type_update);
        final EditText edtAmount = mView.findViewById(R.id.edt_amount_update);
        final EditText edtNote = mView.findViewById(R.id.edt_note_update);

        edtType.setText(by);
        edtType.setSelection(by.length());
        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());
        edtNote.setText(note);
        edtNote.setSelection(note.length());

        Button btnUpdate = mView.findViewById(R.id.btn_update);
        Button btnDelete = mView.findViewById(R.id.btn_delete);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                by =edtType.getText().toString().trim();

//                String mdAmmount=String.valueOf(amount);

//                String mAmmount=edtAmount.getText().toString().trim();

                note=edtNote.getText().toString().trim();

//                int intammount=Integer.parseInt(mAmmount);

                String date=DateFormat.getDateInstance().format(new Date());

                ChoreData data=new ChoreData(by,choreKind,note,date,postKey);

                mApartmentDatabase.child(postKey).setValue(data);


                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApartmentDatabase.child(postKey).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View my_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            my_view = itemView;
        }

        public void setTitle(String type){
            TextView mType = my_view.findViewById(R.id.chore_name);
            mType.setText(type);
        }

        public void setNote(String note){
            TextView mNote = my_view.findViewById(R.id.chore_note);
            mNote.setText(note);
        }

        public void setUserName(String note){
            TextView mNote = my_view.findViewById(R.id.chore_owner);
            mNote.setText(note);
        }

        public void setDate(String date){
            TextView mDate = my_view.findViewById(R.id.chore_date);
            mDate.setText(date);
        }

//        public void setAccept(String code){
//            Button acceptButton = my_view.findViewById(R.id.chore_accept_button);
//
//        }

//        public void setAccept(int amount){
//            TextView mAmount = my_view.findViewById(R.id.amount);
//            String stam = String.valueOf(amount);
//            mAmount.setText(stam) ;
//        }
    }


}
