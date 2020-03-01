package com.roomies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.hashids.Hashids;

public class MainActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private Button b;
    private Button logout;
    private DrawerLayout drawer;
    private String image;
    private String code;
    private String apartmentID = "apartmentID";
    private String imageUrl = "imageUrl";
    private TextView drawerUsername;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.drawerUsername);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        logout = findViewById(R.id.logout);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null)
            navUsername.setText("Roomie");
        else
            navUsername.setText(mAuth.getCurrentUser().getEmail());

//        navUsername.setText(hash);


        drawer = findViewById(R.id.drawer_layouts);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mAuthListener = firebaseAuth -> {
            if(firebaseAuth.getCurrentUser() == null){
                Intent loginIntent = new Intent(MainActivity.this, LogInActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
            }
            else{
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            }
        };
        b = findViewById(R.id.next);
        logout.setOnClickListener(view -> mAuth.signOut());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void clickToPlay(View view) {
        Intent activityChangeIntent = new Intent(MainActivity.this, LogInActivity.class);
        this.startActivity(activityChangeIntent);
    }

    public void clickToaApartment(View view) {
        mDatabase.child("apartmentID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if(value.equals("0")){
                    Intent activityChangeIntent = new Intent(MainActivity.this, ApartmentActivity.class);
                    startActivity(activityChangeIntent);
                }
                else{
                    Intent activityChangeIntent = new Intent(MainActivity.this, HomeActivity.class);
                    activityChangeIntent.putExtra(apartmentID, value);
                    startActivity(activityChangeIntent);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
