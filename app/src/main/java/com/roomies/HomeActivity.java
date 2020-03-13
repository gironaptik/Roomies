package com.roomies;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private LinearLayout avatarsLayout;
    private Intent menuIntent;
    private Button mShoppingBtn;
    private Button mAssignmentsBtn;
    private Button mFinancialBtn;
    private Button mChatBtn;
    private String code;
    private String apartmentID = "apartmentID";
    private String apartmentUsrList = "apartmentUsrList";
    private String apartmentUsrNameList = "apartmentUsrNameList";
    private DatabaseReference mApartmentDatabase;
    private DatabaseReference mUserDatabase;
    private BottomNavigationView bottomNavigationView;
    private List<String> usersKeyList;
    private List<String> usersNameList;
    private List<User> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setBottomNavigator();
        usersKeyList = new ArrayList<>();
        userList = new ArrayList<>();
        usersNameList = new ArrayList<>();
        avatarsLayout = findViewById(R.id.avatars_layout);
        collapsingToolbarLayout = findViewById(R.id.collapseToolbar);
        menuIntent = getIntent();
        if(!apartmentID.equals(null)) {
            code = menuIntent.getExtras().getString(apartmentID);
        }
        mApartmentDatabase = FirebaseDatabase.getInstance().getReference().child("Apartments").child(code);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        createUserList();
        setName();
        setBackgroud();
        mFinancialBtn = findViewById(R.id.btn_finance);
        mChatBtn = findViewById(R.id.btn_chat);
        mShoppingBtn = findViewById(R.id.shopping_screen);
        mAssignmentsBtn = findViewById(R.id.btn_chores);
        mChatBtn.setOnClickListener(view -> {
            Intent newIntent = new Intent(getApplicationContext(),ChatActivity.class);
            newIntent.putExtra(apartmentID, code);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            startActivity(newIntent);
        });
        mAssignmentsBtn.setOnClickListener(view -> {
            Intent newIntent = new Intent(getApplicationContext(),ChoresActivity.class);
            newIntent.putExtra(apartmentID, code);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            startActivity(newIntent);
        });
        mShoppingBtn.setOnClickListener(view -> {
            Intent newIntent = new Intent(getApplicationContext(),ShoppinglistActivity.class);
            newIntent.putExtra(apartmentID, code);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            startActivity(newIntent);
        });
        mFinancialBtn.setOnClickListener(view -> {
            Intent newIntent = new Intent(getApplicationContext(),FinancialActivity.class);
            newIntent.putExtra(apartmentID, code);
            newIntent.putExtra(apartmentUsrList, (ArrayList<String>)usersKeyList);
            newIntent.putExtra(apartmentUsrNameList, (ArrayList<String>)usersNameList);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            startActivity(newIntent);
        });
    }

    private void createUserList() {
        mApartmentDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersKeyList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                            usersKeyList.add(key);
                            usersKeyList.size();
                    }
                    userList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void userList() {
        userList.clear();
        usersNameList.clear();
        for(String key : usersKeyList) {
            mUserDatabase.child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                    usersNameList.add(user.getName());
                    setUsersAvatar(user.getImage());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    private void setBottomNavigator(){
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch(menuItem.getItemId()){
                case R.id.settings:
                    Intent newIntent = new Intent(getApplicationContext(),SettingsActivity.class);
                    newIntent.putExtra(apartmentID, code);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
                    startActivity(newIntent);
                    return true;
            }
            switch(menuItem.getItemId()){
                case R.id.profile:
                    Intent newIntent = new Intent(getApplicationContext(),ProfileActivity.class);
                    newIntent.putExtra(apartmentID, code);
//                    overridePendingTransition(R.anim.fade_out, R.anim.fade_out);
                    startActivity(newIntent);
//                    finish();
                    return true;
            }
            switch(menuItem.getItemId()){
                case R.id.home:
                    return true;
            }
            return false;
        });
    }

    private void setBackgroud(){
        mApartmentDatabase.child("imageUrl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                Glide.with(getApplicationContext())
                        .load(url)
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setName(){
        mApartmentDatabase.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                collapsingToolbarLayout.setTitle(dataSnapshot.getValue().toString());
                collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), R.color.colorButtonBackground));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUsersAvatar(String image){
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, r.getDisplayMetrics());
        float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        float borderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)px, (int)px);
        lp.setMargins((int)margin, 0,0,0);
        CircularImageView circle = new CircularImageView(getApplicationContext());
        circle.setLayoutParams(lp);
        circle.setBorderColor(Color.parseColor("#00E676"/*"#3f51b5"*/));
        circle.setCircleColor(R.color.colorAccent);
        circle.setBorderWidth(borderWidth);
        circle.setShadowColor(Color.parseColor("#3f51b5"));
        circle.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        Glide.with(getApplicationContext())
                .load(image)
                .into(circle);
        avatarsLayout.addView(circle);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
