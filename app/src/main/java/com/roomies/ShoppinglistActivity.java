package com.roomies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roomies.Model.ShoppingData;

import java.text.DateFormat;
import java.util.Date;

public class ShoppinglistActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar shoppingToolbar;
    private DatabaseReference mApartmentDatabase;
    private Intent menuIntent;
    private String code;
    private String apartmentID = "apartmentID";
    private FloatingActionButton fab_btn;
    private RecyclerView recyclerView;
    private TextView amountText;
    private String type;
    private int amount;
    private String note;
    private String postKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist);
        shoppingToolbar = findViewById(R.id.shopping_toolbar);
        setSupportActionBar(shoppingToolbar);
        getSupportActionBar().setTitle("Shopping List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        menuIntent = getIntent();
        if(!apartmentID.equals(null)) {
            code = menuIntent.getExtras().getString(apartmentID);
        }
        mApartmentDatabase = FirebaseDatabase.getInstance().getReference().child("Apartments").child(code).child("shoppinglist");
        fab_btn = findViewById(R.id.add_shopping_button);
        amountText = findViewById(R.id.totalAmount);
        mApartmentDatabase.keepSynced(true);
        recyclerView = findViewById(R.id.recyceler_shopping);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);



        mApartmentDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalAmount = 0;
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    ShoppingData shoppingData = snap.getValue(ShoppingData.class);
                    totalAmount += shoppingData.getAmount();
                    String tAmount = totalAmount + ".00";
                    amountText.setText(tAmount);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customeDialog();
            }
        });
    }

    private void customeDialog(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(ShoppinglistActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ShoppinglistActivity.this);
        View myView = inflater.inflate(R.layout.input_shopping_data, null);
        AlertDialog dialog = myDialog.create();
        dialog.setView(myView);

        EditText type = myView.findViewById(R.id.edt_type);
        EditText amount = myView.findViewById(R.id.edt_amount);
        EditText note = myView.findViewById(R.id.edt_note);
        Button btnSave = myView.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mType = type.getText().toString().trim();
                String mAmount = amount.getText().toString().trim();
                String mNote = note.getText().toString().trim();

                try {
                    int ammint = Integer.parseInt(mAmount);
                    if (TextUtils.isEmpty(mType)) {
                        type.setError("Required failed..");
                        return;
                    }
                    if (TextUtils.isEmpty(mAmount)) {
                        type.setError("Required failed..");
                        return;
                    }
                    if (TextUtils.isEmpty(mNote)) {
                        type.setError("Required failed..");
                        return;
                    }

                    String id = mApartmentDatabase.push().getKey();
                    String mDate = DateFormat.getDateInstance().format(new Date());
                    ShoppingData shoppingData = new ShoppingData(mType, ammint, mNote, mDate, id);
                    mApartmentDatabase.child(id).setValue(shoppingData);    ////
                    Toast.makeText(getApplicationContext(), "Data Add", Toast.LENGTH_SHORT);
                    dialog.dismiss();
                }
                catch (NumberFormatException e){
                    amount.setError("Illegal Amount");
                }
            }
        });

        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<ShoppingData, MyViewHolder> adapter = new FirebaseRecyclerAdapter<ShoppingData, MyViewHolder>(
                ShoppingData.class,
                R.layout.item_data,
                MyViewHolder.class,
                mApartmentDatabase
        ) {


            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, ShoppingData model, int i) {
                myViewHolder.setDate(model.getDate());
                myViewHolder.setAmount(model.getAmount());
                myViewHolder.setNote(model.getNote());
                myViewHolder.setType(model.getType());

                myViewHolder.my_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        postKey = getRef(i).getKey();
                        type = model.getType();
                        note = model.getNote();
                        amount = model.getAmount();

                        updateDate();
                    }
                });
            }

        };

        recyclerView.setAdapter(adapter);
    }

    public void updateDate(){

        AlertDialog.Builder myDialog = new AlertDialog.Builder(ShoppinglistActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ShoppinglistActivity.this);
        View mView = inflater.inflate(R.layout.update_inputfield, null);
        AlertDialog dialog = myDialog.create();
        dialog.setView(mView);

        final EditText edtType = mView.findViewById(R.id.edt_type_update);
        final EditText edtAmount = mView.findViewById(R.id.edt_amount_update);
        final EditText edtNote = mView.findViewById(R.id.edt_note_update);

        edtType.setText(type);
        edtType.setSelection(type.length());
        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());
        edtNote.setText(note);
        edtNote.setSelection(note.length());

        Button btnUpdate = mView.findViewById(R.id.btn_update);
        Button btnDelete = mView.findViewById(R.id.btn_delete);

        btnUpdate.setOnClickListener(view -> {
            type=edtType.getText().toString().trim();
            String mAmmount=edtAmount.getText().toString().trim();
            note=edtNote.getText().toString().trim();
            int intammount=Integer.parseInt(mAmmount);
            String date=DateFormat.getDateInstance().format(new Date());
            ShoppingData shoppingData =new ShoppingData(type,intammount,note,date,postKey);
            mApartmentDatabase.child(postKey).setValue(shoppingData);
            dialog.dismiss();
        });
        btnDelete.setOnClickListener(view -> {
            mApartmentDatabase.child(postKey).removeValue();
            dialog.dismiss();
        });


        dialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
//            Intent newIntent = new Intent(getApplicationContext(),HomeActivity.class);
//            newIntent.putExtra(apartmentID, code);
//            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(newIntent);
        }

        return super.onOptionsItemSelected(item);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View my_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            my_view = itemView;
        }

        public void setType(String type){
            TextView mType = my_view.findViewById(R.id.type);
            mType.setText(type);
        }

        public void setNote(String note){
            TextView mNote = my_view.findViewById(R.id.note);
            mNote.setText(note);
        }

        public void setDate(String date){
            TextView mDate = my_view.findViewById(R.id.date);
            mDate.setText(date);
        }

        public void setAmount(int amount){
            TextView mAmount = my_view.findViewById(R.id.amount);
            String stam = String.valueOf(amount);
            mAmount.setText(stam) ;
        }
    }
}
