package com.roomies;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
    private FirebaseAuth mAuth;
    private Intent menuIntent;
    private AppBarLayout appBarLayout;
    private Button mMessagesBtn;
    private Button mShoppingBtn;
    private Button mBillsBtn;
    private Button mAssignmentsBtn;
    private Button mFinancialBtn;
    private String image;
    private String code;
    private String apartmentID = "apartmentID";
    private String imageUrl = "imageUrl";
    private DatabaseReference mApartmentDatabase;
    private DatabaseReference mUserDatabase;
    private BottomNavigationView bottomNavigationView;
    private List<String> usersKeyList;
    private List<User> userList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        setBottomNavigator();
        usersKeyList = new ArrayList<>();
        userList = new ArrayList<>();

//        CircularImageView circularImageView = findViewById(R.id.my_avatar);
//        Glide.with(getApplicationContext())
//                .load(mAuth.getCurrentUser().getPhotoUrl())
//                .into(circularImageView);
        avatarsLayout = findViewById(R.id.avatars_layout);

//        setUsersAvatar(mAuth.getCurrentUser().getPhotoUrl().toString());

        collapsingToolbarLayout = findViewById(R.id.collapseToolbar);
        menuIntent = getIntent();
        if(!apartmentID.equals(null)) {
            code = menuIntent.getExtras().getString(apartmentID);
            image = menuIntent.getExtras().getString(imageUrl);
        }
        mApartmentDatabase = FirebaseDatabase.getInstance().getReference().child("Apartments").child(code);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        createUserList();
        appBarLayout = findViewById(R.id.appbar);
        setName();
        setBackgroud();
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
        for(String key : usersKeyList) {
            mUserDatabase.child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
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
                case R.id.chat:
                    Intent newIntent = new Intent(getApplicationContext(),ChatActivity.class);
                    newIntent.putExtra(apartmentID, code);
                    newIntent.putExtra(imageUrl, image);
                    startActivity(newIntent);
                    overridePendingTransition(0,0);
                    return true;
            }
            switch(menuItem.getItemId()){
                case R.id.profile:
                    Intent newIntent = new Intent(getApplicationContext(),ProfileActivity.class);
                    newIntent.putExtra(apartmentID, code);
                    newIntent.putExtra(imageUrl, image);
                    startActivity(newIntent);
                    overridePendingTransition(0,0);
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
        circle.setBorderColor(Color.parseColor("#3f51b5"));
        circle.setCircleColor(R.color.colorAccent);
        circle.setBorderWidth(borderWidth);
        circle.setShadowColor(Color.parseColor("#3f51b5"));
        circle.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        Glide.with(getApplicationContext())
                .load(image)
                .into(circle);
        avatarsLayout.addView(circle);
    }
}
