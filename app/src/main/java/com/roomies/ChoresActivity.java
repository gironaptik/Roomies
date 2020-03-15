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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private String choreslist = "choreslist";
    private String postKey;
    private String choreKind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chores);
        choresToolbar = findViewById(R.id.chores_toolbar);
        setSupportActionBar(choresToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.choresList));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        menuIntent = getIntent();
        if (!apartmentID.equals(null)) {
            code = menuIntent.getExtras().getString(apartmentID);
        }

        mApartmentDatabase = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.Apartments)).child(code).child(choreslist);
        fab_btn = findViewById(R.id.add_chore_button);
        mApartmentDatabase.keepSynced(true);
        recyclerView = findViewById(R.id.recyceler_chores);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        fab_btn.setOnClickListener(view -> customeDialog());
    }

    private void customeDialog() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(ChoresActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ChoresActivity.this);
        View myView = inflater.inflate(R.layout.input_chores_data, null);
        AlertDialog dialog = myDialog.create();
        dialog.setView(myView);

        TextView by = myView.findViewById(R.id.edt_by);
        by.setText(mAuth.getCurrentUser().getDisplayName());
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

        btnSave.setOnClickListener(view -> {
            String mBy = by.getText().toString().trim();
            String mNote = note.getText().toString().trim();

            if (TextUtils.isEmpty(mBy)) {
                by.setError(getResources().getString(R.string.requiredFailed));
                return;
            }

            String id = mApartmentDatabase.push().getKey();
            String mDate = DateFormat.getDateInstance().format(new Date());
            ChoreData data = new ChoreData(mBy, choreKind, mNote, mDate, id);
            mApartmentDatabase.child(id).setValue(data);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.dataAdd), Toast.LENGTH_SHORT);
            dialog.dismiss();
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

                Button acceptButton = myViewHolder.my_view.findViewById(R.id.chore_accept_button);
                acceptButton.setOnClickListener(view -> {
                    postKey = getRef(i).getKey();
                    deleteChore();
                });
            }

        };

        recyclerView.setAdapter(adapter);
    }

    private void deleteChore() {
        mApartmentDatabase.child(postKey).removeValue();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View my_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            my_view = itemView;
        }

        public void setTitle(String type) {
            TextView mType = my_view.findViewById(R.id.chore_name);
            mType.setText(type);
        }

        public void setNote(String note) {
            TextView mNote = my_view.findViewById(R.id.chore_note);
            mNote.setText(note);
        }

        public void setUserName(String note) {
            TextView mNote = my_view.findViewById(R.id.chore_owner);
            mNote.setText(note);
        }

        public void setDate(String date) {
            TextView mDate = my_view.findViewById(R.id.chore_date);
            mDate.setText(date);
        }
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
