package com.roomies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LogInActivity extends AppCompatActivity{


    private static final String TAG = LogInActivity.class.getSimpleName();
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
    private AutoCompleteTextView mAddressField;
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
        mAuthListener = firebaseAuth -> {
            if(firebaseAuth.getCurrentUser() != null){
                startActivity(new Intent(LogInActivity.this, MenuActivity.class));
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

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        mAddressField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                autoComplete();
            }

            @Override
            public void afterTextChanged(Editable editable) {
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
        mAddressField = findViewById(R.id.addressAuthEditText);
        forgotButton = findViewById(R.id.forgot_password);
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

    private void autoComplete(){
        PlacesClient placesClient = Places.createClient(this);
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        // Create a RectangularBounds object.
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363), //dummy lat/lng
                new LatLng(-33.858754, 151.229596));
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                .setCountry("IL")//
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(mAddressField.getText().toString())
                .build();


        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            mResult = new StringBuilder();
            options = new ArrayList();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                mResult.append(" ").append(prediction.getFullText(null) + "\n");
                options.add(prediction.getFullText(null).toString());
            }

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(LogInActivity.this, android.R.layout.simple_list_item_1, options);

            mAddressField.setAdapter(adapter);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });
    }

    private void startSignUp(){

        String name = mNewNameField.getText().toString();
        String email = mNewEmailField.getText().toString();
        String password = mNewPasswordField.getText().toString();
        String address = mAddressField.getText().toString();

        mProgress.setMessage("Signing Up...");
        mProgress.show();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(address)){
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    String userId= mAuth.getCurrentUser().getUid();
                    DatabaseReference current_userDB =  mDatabase.child(userId);
                    current_userDB.child("name").setValue(name);
                    current_userDB.child("address").setValue(address);
                    current_userDB.child("apartmentId").setValue("0");
                    mProgress.dismiss();
                    Intent mainIntent = new Intent(LogInActivity.this, MainActivity.class);
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

}
