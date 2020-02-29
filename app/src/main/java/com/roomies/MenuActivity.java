package com.roomies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MenuActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);

        collapsingToolbarLayout = findViewById(R.id.collapseToolbar);
        menuIntent = getIntent();
        if(apartmentID != null) {
            code = menuIntent.getExtras().getString(apartmentID);
            image = menuIntent.getExtras().getString(imageUrl);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Apartments").child(code);
        setName();
        setBackgroud();

//        collapsingToolbarLayout.setBackground();
//        menuIntent = getIntent();
//        if(apartmentID != null) {
//            String code = menuIntent.getExtras().getString(apartmentID);
//            String image = menuIntent.getExtras().getString(imageUrl);
//        }



//        mMessagesBtn = findViewById(R.id.messagesButton);
//        mShoppingBtn = findViewById(R.id.shoppingButton);
//        mBillsBtn = findViewById(R.id.billsButton);
//        mAssignmentsBtn = findViewById(R.id.assignmentsButton);
//        mFinancialBtn = findViewById(R.id.financialButton);



//        mMessagesBtn.setOnClickListener(view -> {
//            startActivity(new Intent(MenuActivity.this, MessagesActivity.class));
//        });
//
//        mShoppingBtn.setOnClickListener(view -> {
//            startActivity(new Intent(MenuActivity.this, ShoppingActivity.class));
//        });
//
//        mBillsBtn.setOnClickListener(view -> {
//            startActivity(new Intent(MenuActivity.this, BiilsActivity.class));
//        });
//
//        mAssignmentsBtn.setOnClickListener(view -> {
//            startActivity(new Intent(MenuActivity.this, AssignmentsActivity.class));
//        });
//
//        mFinancialBtn.setOnClickListener(view -> {
//            startActivity(new Intent(MenuActivity.this, FinancialActivity.class));
//        });
    }

    private void setBackgroud(){
        Glide.with(this)
                .load(image)
                .into(new CustomTarget<Drawable>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        collapsingToolbarLayout.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void setName(){
        mDatabase.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                collapsingToolbarLayout.setTitle(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}