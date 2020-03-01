package com.roomies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LogInActivity extends AppCompatActivity implements IPickResult {


    private static final String TAG = LogInActivity.class.getSimpleName();
    private int TAKE_IMAGE_CODE = 10001;
    private String apiKey;
    private ImageView bookIconImageView;
    private TextView bookITextView;
    private ProgressBar loadingProgressBar;
    private RelativeLayout rootView, afterAnimationView;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mNewEmailField;
    private EditText mNewNameField;
    private EditText mNewPasswordField;
    private Button mLoginBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView loginWindow;
    private TextView signUpWindow;
    private LinearLayout loginLayout;
    private LinearLayout signUpLayout;
    private StringBuilder mResult;
    private List<String> options;
    private String login;
    private String sign_up;
    private ProgressDialog mProgress;
    private TextView forgotButton;
    private ImageView userAvatar;
    private Bitmap bitmap;
    private String image;
    private String code;
    private String apartmentID = "apartmentID";
    private String imageUrl = "imageUrl";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        apiKey = getString(R.string.app_key);
        login = getString(R.string.login);
        sign_up = getString(R.string.sign_up);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_log_in);
        initViews();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mProgress = new ProgressDialog(this);
        timerHandler = new Handler();
        timerRunnable = () -> {
            bookITextView.setVisibility(GONE);
            loadingProgressBar.setVisibility(GONE);
            rootView.setBackgroundColor(ContextCompat.getColor(LogInActivity.this, R.color.colorLoginView));
            bookIconImageView.setImageResource(R.mipmap.logo);
            startAnimation();
            timerHandler.postDelayed(timerRunnable, 3000);
        };
        timerHandler.postDelayed(timerRunnable, 3000);
        mAuthListener = firebaseAuth -> {   /// HERE TO CHECK IF USER HAS APARTMENT ALREADY!!
            if(firebaseAuth.getCurrentUser() != null){
                mDatabase.child(mAuth.getCurrentUser().getUid()).child("ApartmentID").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String apartmentid = dataSnapshot.getValue(String.class);
                        if(apartmentid.equals("0")){
                            startActivity(new Intent(LogInActivity.this, ApartmentActivity.class));
                        }
                        else{
                            Intent newIntent = new Intent(getApplicationContext(),HomeActivity.class);
                            newIntent.putExtra(apartmentID, apartmentid);
                            startActivity(newIntent);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };

        mLoginBtn.setOnClickListener(view -> {
            if(mLoginBtn.getTag().equals(login)) {
                startSignIn();
            }
            else
                if(mLoginBtn.getTag().equals(sign_up)){
                startSignUp();
            }
        });

        loginWindow.setOnClickListener(view -> {
            signUpLayout.setVisibility(GONE);
            loginLayout.setVisibility(VISIBLE);
            signUpWindow.setVisibility(View.VISIBLE);
            mLoginBtn.setText(login);
            mLoginBtn.setTag(login);
        });

        signUpWindow.setOnClickListener(view -> {
            signUpLayout.setVisibility(VISIBLE);
            loginLayout.setVisibility(GONE);
            signUpWindow.setVisibility(View.INVISIBLE);
            mLoginBtn.setText("Join Us!");
            mLoginBtn.setTag(sign_up);
        });

        int[] images = {R.drawable.avatar1,R.drawable.avatar2};
        Random rand = new Random();
        int i = (int)(Math.random() * images.length);
        userAvatar.setImageResource(images[i]);
        bitmap = BitmapFactory.decodeResource(getResources(),
                images[i]);
        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                takePictureIntent();
                PickImageDialog.build(new PickSetup()).show(LogInActivity.this);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void initViews() {
        bookIconImageView = findViewById(R.id.bookIconImageView);
        bookITextView = findViewById(R.id.bookITextView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        rootView = findViewById(R.id.rootView);
        afterAnimationView = findViewById(R.id.afterAnimationView);
        mEmailField = findViewById(R.id.emailEditText);
        mPasswordField = findViewById(R.id.passwordEditText);
        mLoginBtn = findViewById(R.id.loginButton);
        loginWindow = findViewById(R.id.loginUpWindow);
        signUpWindow = findViewById(R.id.signUpWindow);
        loginLayout = findViewById(R.id.loginLayout);
        signUpLayout = findViewById(R.id.signUpLayout);
        mNewNameField = findViewById(R.id.newNameEditText);
        mNewEmailField = findViewById(R.id.newEmailEditText);
        mNewPasswordField = findViewById(R.id.newPasswordEditText);
        forgotButton = findViewById(R.id.forgot_password);
        userAvatar = findViewById(R.id.userAvatar);
    }

    private void startAnimation() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        ViewPropertyAnimator viewPropertyAnimator = bookIconImageView.animate();
        viewPropertyAnimator.x(width/2-bookIconImageView.getWidth()/2);
        viewPropertyAnimator.y(100f);
        viewPropertyAnimator.setDuration(1000);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                afterAnimationView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void startSignIn(){
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LogInActivity.this, "Fields are empty.", Toast.LENGTH_LONG).show();
        }
        else{
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(LogInActivity.this, "Sign in problem", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }



    private void startSignUp(){

        String name = mNewNameField.getText().toString();
        String email = mNewEmailField.getText().toString();
        String password = mNewPasswordField.getText().toString();

        mProgress.setMessage("Signing Up...");
        mProgress.show();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    handleUpload(bitmap);
                    String userId= mAuth.getCurrentUser().getUid();
                    FirebaseUser user = mAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/roomies-85581.appspot.com/o/2415.jpg?alt=media&token=04892b10-d93e-4fd5-89d1-8a656e533b2e"))
                            .build();
                    user.updateProfile(profileUpdates);
                    DatabaseReference current_userDB =  mDatabase.child(userId);
                    current_userDB.child("name").setValue(name);
                    current_userDB.child("email").setValue(email);
                    current_userDB.child("apartmentID").setValue("0");
                    mProgress.dismiss();
                    Intent mainIntent = new Intent(LogInActivity.this, ApartmentActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }

                if(!task.isSuccessful()) {
                    mProgress.dismiss();
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthWeakPasswordException e) {
                        mNewPasswordField.setError(getString(R.string.error_weak_password));
                        mNewPasswordField.requestFocus();
                    } catch(FirebaseAuthInvalidCredentialsException e) {
                        mNewEmailField.setError(getString(R.string.error_invalid_email));
                        mNewEmailField.requestFocus();
                    } catch(FirebaseAuthUserCollisionException e) {
                        mNewEmailField.setError(getString(R.string.error_user_exists));
                        mNewEmailField.requestFocus();
                    } catch(Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }
    }

    private void forgotPassword(View view){
        FirebaseAuth.getInstance().sendPasswordResetEmail(mEmailField.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Email sent.");
                    }
                });
    }

    private void takePictureIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == TAKE_IMAGE_CODE){
//            switch(resultCode){
//                case Activity.RESULT_OK:
//                    bitmap = (Bitmap)data.getExtras().get("data");
//                    userAvatar.setImageBitmap(bitmap);
////                    handleUpload(bitmap);
//            }
//        }
//    }

    private void handleUpload(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(mAuth.getCurrentUser().getUid()+".jpeg");
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(taskSnapshot ->
                getDownloadUrl(reference)).addOnFailureListener(e -> Log.e(TAG, "OnFailure: ", e.getCause()));
    }

    private void getDownloadUrl(StorageReference reference){
        reference.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d(TAG, "onSuccess: " + uri);
            setUserProfileUri(uri);
        });
    }

    private void setUserProfileUri(Uri uri){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
        user.updateProfile(request).addOnSuccessListener(aVoid -> Toast.makeText(LogInActivity.this, "Updated", Toast.LENGTH_SHORT))
                .addOnFailureListener(e -> Toast.makeText(LogInActivity.this, "Profile image failed..", Toast.LENGTH_SHORT));
    }


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
}
