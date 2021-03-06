package com.roomies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roomies.Model.Apartment;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigationView;
    private List<SlideModel> slideModels;
    private String code;
    private String apartmentID = "apartmentID";
    private String imageUrl = "imageUrl";
    private String name = "name";
    private String addressInput = "address";
    private String apartmentTitle = "Apartment Code: ";
    private String notFound = "Place not found: ";
    private Intent menuIntent;
    private Apartment currentApartment;
    private TextView apartmentCodeTitle;
    private EditText apartmentName;
    private AutoCompleteTextView apartmentAddress;
    private EditText apartmentAddressNumber;
    private ImageSlider imageSlider;
    private Button updateButton;
    private Button shareButton;
    private String currentApartmentAddress;
    private String currentApartmentAddressNumber;
    private DatabaseReference mApartmentDatabase;
    private StringBuilder mResult;
    private List<String> options;
    private String apiKey;
    private List<String> imagesLinks;
    private String newUrl = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setBottomNavigator();
        createImagesLinks();
        apiKey = getString(R.string.app_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        menuIntent = getIntent();
        if (!apartmentID.equals(null)) {
            code = menuIntent.getExtras().getString(apartmentID);
        }
        findAll();
        mApartmentDatabase = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.Apartments)).child(code);
        currentApartmentDetails();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateApartment();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getResources().getString(R.string.joinOurRoom) + code;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.joinOurRoomTitle));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.shareVia)));
            }
        });

    }

    private void createImagesLinks() {
        imagesLinks = new ArrayList<>();
        imagesLinks.add("https://firebasestorage.googleapis.com/v0/b/roomies-85581.appspot.com/o/covers%2F1.jpg?alt=media&token=51b16512-ff5e-49d2-acd5-8286e0c45716");
        imagesLinks.add("https://firebasestorage.googleapis.com/v0/b/roomies-85581.appspot.com/o/covers%2F2.jpg?alt=media&token=06d5c9cd-a9ae-4023-bb3f-bce2f5c725b7");
        imagesLinks.add("https://firebasestorage.googleapis.com/v0/b/roomies-85581.appspot.com/o/covers%2F3.jpg?alt=media&token=de7fce9a-1a02-409b-b142-0e2255097e7c");
        imagesLinks.add("https://firebasestorage.googleapis.com/v0/b/roomies-85581.appspot.com/o/covers%2F4.jpg?alt=media&token=ddcbaf4d-7256-46d2-b347-61ce00707c75");
        imagesLinks.add("https://firebasestorage.googleapis.com/v0/b/roomies-85581.appspot.com/o/covers%2F5.jpg?alt=media&token=3420fc2e-ebef-4629-a122-a10408772688");

    }

    private void createSliderModel() {
        slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(currentApartment.getImageUrl()));
        for (String link : imagesLinks) {
            if (!link.equals(currentApartment.getImageUrl())) {
                slideModels.add(new SlideModel(link));
            }
        }
        imageSlider.setImageList(slideModels, true);
        imageSlider.stopSliding();
        imageSlider.setItemClickListener(i -> newUrl = slideModels.get(i).getImageUrl());
    }

    private void findAll() {
        apartmentCodeTitle = findViewById(R.id.apartmentCodeTitle);
        apartmentCodeTitle.setText(apartmentTitle + code);
        apartmentName = findViewById(R.id.apartmentName_update);
        apartmentAddress = findViewById(R.id.apartmentAddress_update);
        apartmentAddressNumber = findViewById(R.id.apartmentNumber_update);
        imageSlider = findViewById(R.id.image_slider_update);
        updateButton = findViewById(R.id.newApplyApartmentButton_update);
        shareButton = findViewById(R.id.share_room_button);
    }

    private void updateFields() {
        apartmentName.setHint(currentApartment.getName());
        String address = currentApartment.getAddress();

        int lastIndexOf = address.lastIndexOf(" ");
        currentApartmentAddress = address.substring(0, lastIndexOf);
        currentApartmentAddressNumber = address.substring(lastIndexOf + 1, address.length());
        apartmentAddressNumber.setHint(currentApartmentAddressNumber);
        apartmentAddress.setHint(currentApartmentAddress);
        createSliderModel();
        apartmentAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                autoComplete();
            }
        });
    }

    private void currentApartmentDetails() {
        mApartmentDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentApartment = dataSnapshot.getValue(Apartment.class);
                updateFields();
                newUrl = currentApartment.getImageUrl();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setBottomNavigator() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().findItem(R.id.settings).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.settings:
                    return true;
            }
            switch (menuItem.getItemId()) {
                case R.id.profile:
                    Intent newIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                    newIntent.putExtra(apartmentID, code);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
                    startActivity(newIntent);
                    finish();
                    return true;
            }
            switch (menuItem.getItemId()) {
                case R.id.home_btn:
                    Intent newIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    newIntent.putExtra(apartmentID, code);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
                    startActivity(newIntent);
                    finish();
                    return true;
            }
            return false;
        });
    }

    private void autoComplete() {
        PlacesClient placesClient = Places.createClient(getApplicationContext());
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363), //dummy lat/lng
                new LatLng(-33.858754, 151.229596));
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(bounds)
                .setCountry("IL")//
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(apartmentAddress.getText().toString())
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            mResult = new StringBuilder();
            options = new ArrayList();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                mResult.append(" ").append(prediction.getFullText(null) + "\n");
                options.add(prediction.getFullText(null).toString());
            }

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, options);

            apartmentAddress.setAdapter(adapter);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, notFound + apiException.getStatusCode());
            }
        });
    }

    private void updateApartment() {
        Apartment updateApartment = new Apartment();
        if (TextUtils.isEmpty(apartmentName.getText().toString().trim())) {
            updateApartment.setName(currentApartment.getName());
        } else {
            updateApartment.setName(apartmentName.getText().toString().trim());
        }
        if (TextUtils.isEmpty(apartmentAddress.getText().toString().trim()) && TextUtils.isEmpty(apartmentAddressNumber.getText().toString())) {
            updateApartment.setAddress(currentApartment.getAddress());
        }
        if(!TextUtils.isEmpty(apartmentAddressNumber.getText().toString())){
            updateApartment.setAddress(apartmentAddress.getText().toString().trim() + " " + apartmentAddressNumber.getText().toString().trim());
        }
        else {
            updateApartment.setAddress(apartmentAddress.getText().toString().trim() + " " + apartmentAddressNumber.getText().toString().trim());
        }
        if (newUrl.equals(null)) {
            updateApartment.setImageUrl(currentApartment.getImageUrl());
        } else {
            updateApartment.setImageUrl(newUrl);
        }
        mApartmentDatabase.child(imageUrl).setValue(updateApartment.getImageUrl());
        mApartmentDatabase.child(name).setValue(updateApartment.getName());
        mApartmentDatabase.child(addressInput).setValue(updateApartment.getAddress());
        overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
        finish();
    }

}
