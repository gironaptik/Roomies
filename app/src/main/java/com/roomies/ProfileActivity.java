package com.roomies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity implements IPickResult {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    private int TAKE_IMAGE_CODE = 10001;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private DatabaseReference userDatabase;
    private DatabaseReference apartmentDatabase;
    private Intent menuIntent;
    private BottomNavigationView bottomNavigationView;
    private String image;
    private String code;
    private String apartmentID = "apartmentID";
    private String imageUrl = "imageUrl";
    private TextView userEmail;
    private EditText editName;
    private CircularImageView circularImageView;
    private EditText editCurrentPassword;
    private EditText editNewPassword;
    private Button saveChanges;
    private Button leaveApartment;
    private Button logout;
    private Bitmap bitmap;
    private AuthCredential credential;
    private Uri newImageUri;
    private ProgressDialog mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setBottomNavigator();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userDatabase = mDatabase.child("Users").child(mAuth.getCurrentUser().getUid());
        mProgress = new ProgressDialog(this);
        findAllById();
        menuIntent = getIntent();
        if(!apartmentID.equals(null)) {
            code = menuIntent.getExtras().getString(apartmentID);
        }
        apartmentDatabase = mDatabase.child("Apartments").child(code);
        Glide.with(this)
                .asBitmap()
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        bitmap = resource;
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });

        circularImageView.setOnClickListener(view -> {
            PickImageDialog.build(new PickSetup()).show(ProfileActivity.this);
        });


        saveChanges.setOnClickListener(view -> handleUpload(bitmap));
        leaveApartment.setOnClickListener(view -> leaveApartment());
        logout.setOnClickListener(view ->signOut());
    }

    private void findAllById(){
        userEmail = findViewById(R.id.emailText);
        userEmail.setText(mAuth.getCurrentUser().getEmail());
        editName = findViewById(R.id.editNameEditText);
        editName.setText(mAuth.getCurrentUser().getDisplayName());
        circularImageView = findViewById(R.id.my_avatar);
        Glide.with(this).load(mAuth.getCurrentUser().getPhotoUrl()).into(circularImageView);
        editCurrentPassword = findViewById(R.id.edit_CurrentPasswordEditText);
        editCurrentPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editName.getText().equals(null)){
                    credential = EmailAuthProvider
                            .getCredential(mAuth.getCurrentUser().getEmail(), editCurrentPassword.getText().toString());
                }
            }
        });
        editNewPassword = findViewById(R.id.edit_NewPasswordEditText);
        saveChanges = findViewById(R.id.edit_saveChanges);
        leaveApartment = findViewById(R.id.edit_leaveApartment);
        logout = findViewById(R.id.edit_logout);
        bitmap = BitmapFactory.decodeResource(getResources(),
                circularImageView.getImageAlpha());
    }
    private void setBottomNavigator(){
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch(menuItem.getItemId()){
                case R.id.settings:
                    Intent newIntent = new Intent(getApplicationContext(),SettingsActivity.class);
                    newIntent.putExtra(apartmentID, code);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
                    startActivity(newIntent);
                    finish();
                    return true;
            }
            switch(menuItem.getItemId()){
                case R.id.profile:
                    return true;
            }
            switch(menuItem.getItemId()){
                case R.id.home:
                    Intent newIntent = new Intent(getApplicationContext(),HomeActivity.class);
                    newIntent.putExtra(apartmentID, code);
                    overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
                    startActivity(newIntent);
                    finish();
                    return true;
            }
            return false;
        });
    }

    private void handleUpload(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(mAuth.getCurrentUser().getUid()+".jpeg");
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(taskSnapshot -> {
            getDownloadUrl(reference);
            mProgress.dismiss();
        }).addOnFailureListener(e -> Log.e(TAG, "OnFailure: ", e.getCause()))
        .addOnProgressListener(taskSnapshot -> {
            mProgress.setMessage("Updating...");
            mProgress.show();
        });
    }

    private void getDownloadUrl(StorageReference reference){
        reference.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d(TAG, "onSuccess: " + uri);
            newImageUri = uri;
            updateUser();
        });
    }



    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            bitmap = r.getBitmap();
            circularImageView.setImageBitmap(bitmap);

        } else {
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userDatabase.child("image").setValue(newImageUri.toString());
        userDatabase.child("name").setValue(editName.getText().toString());
        apartmentDatabase.child("financialBalance").child(user.getUid()).child("name").setValue(editName.getText().toString());
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(editName.getText().toString())
                .setPhotoUri(newImageUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                    }
                });
        if(!editCurrentPassword.getText().toString().equals(null) && !editNewPassword.getText().toString().equals(null) && credential != null){
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(editNewPassword.getText().toString()).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d(TAG, "Password updated");
                                } else {
                                    Log.d(TAG, "Error password not updated");
                            }
                        });
                        }
                        else {
                            Log.d(TAG, "Error auth failed");
                        }
                    });
        }
    }

    private void signOut(){
            FirebaseAuth.getInstance().signOut();
            Intent i=new Intent(getApplicationContext(),LogInActivity.class);
            startActivity(i);

    }
    private void leaveApartment(){
        userDatabase.child("apartmentID").setValue("0");
        Intent intent = new Intent(getApplicationContext(), ApartmentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        apartmentDatabase.child("users").child(mAuth.getCurrentUser().getUid()).removeValue();
        apartmentDatabase.child("financialBalance").child(mAuth.getCurrentUser().getUid()).removeValue();

    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
