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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
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
    private DatabaseReference apartment;
    private Intent menuIntent;
    private BottomNavigationView bottomNavigationView;
    private String image;
    private String code;
    private String apartmentID = "apartmentID";
    private String imageUrl = "imageUrl";
    private ImageView userAvatar;
    private EditText editName;
//    private EditText editEmail;
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
        apartment = mDatabase.child("Apartments").child(mAuth.getCurrentUser().getUid());
        mProgress = new ProgressDialog(this);

        findAllById();
        menuIntent = getIntent();
        if(!apartmentID.equals(null)) {
            code = menuIntent.getExtras().getString(apartmentID);
        }

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

        userAvatar.setOnClickListener(view -> {
            PickImageDialog.build(new PickSetup()).show(ProfileActivity.this);
        });


        saveChanges.setOnClickListener(view -> handleUpload(bitmap));
        leaveApartment.setOnClickListener(view -> leaveApartment());
        logout.setOnClickListener(view -> logout());
    }
    private void findAllById(){
//        Log.v("URL", mAuth.getCurrentUser().getPhotoUrl());
        userAvatar = findViewById(R.id.edituserAvatar);
//        Glide.with(getApplicationContext())
//                .load(mAuth.getCurrentUser().getPhotoUrl())
//                .into(userAvatar);

        Glide.with(this).load(mAuth.getCurrentUser().getPhotoUrl()).into(userAvatar);

//        userAvatar.setImageURI(mAuth.getCurrentUser().getPhotoUrl());
        editName = findViewById(R.id.editNameEditText);
        editName.setText(mAuth.getCurrentUser().getDisplayName());
//        editEmail = findViewById(R.id.editEmailEditText);
//        editEmail.setText(mAuth.getCurrentUser().getEmail());
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
                userAvatar.getImageAlpha());
    }
    private void setBottomNavigator(){
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch(menuItem.getItemId()){
                case R.id.chat:
                    Intent newIntent = new Intent(getApplicationContext(),ChatActivity.class);
                    newIntent.putExtra(apartmentID, code);
                    startActivity(newIntent);
                    overridePendingTransition(0,0);
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
                    startActivity(newIntent);
                    overridePendingTransition(0,0);
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
//            setUserProfileUri(uri);
        });
    }

//    private void setUserProfileUri(Uri uri){
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        newImageUri = uri;
//        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
//        user.updateProfile(request).addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Updated", Toast.LENGTH_SHORT))
//                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Profile image failed..", Toast.LENGTH_SHORT));
//    }


    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            bitmap = r.getBitmap();
            userAvatar.setImageBitmap(bitmap);
//            handleUpload(bitmap);
//            getImageView().setImageBitmap(r.getBitmap());

            //r.getPath();
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateUser(){
//        handleUpload(bitmap);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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


    private void logout(){
        logout.setOnClickListener(view -> mAuth.signOut());
//        Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
    }

    private void leaveApartment(){
        userDatabase.child("apartmentID").setValue("0");
        Intent intent = new Intent(getApplicationContext(), ApartmentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        /// Need to add also removing from apartment db
    }

}
