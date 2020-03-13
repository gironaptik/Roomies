package com.roomies;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.hashids.Hashids;
import java.util.ArrayList;
import java.util.List;


public class NewRoomFragment extends Fragment {

    private static final String TAG = NewRoomFragment.class.getSimpleName();
    private AutoCompleteTextView mAddressField;
    private EditText mApartmentNameView;
    private EditText mApartmentNumberView;
    private Button applyButton;
    private String mApartmentImage = "https://firebasestorage.googleapis.com/v0/b/roomies-85581.appspot.com/o/covers%2F1.jpg?alt=media&token=51b16512-ff5e-49d2-acd5-8286e0c45716";
    private String mApartmentName;
    private String mAddress;
    private String mApartmentNumber;
    private String apiKey;
    private StringBuilder mResult;
    private List<String> options;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mAuthUser;
    private String counter = "Counter";
    private String apartments = "Apartments";
    private String apartmentID = "apartmentID";
    private String imageUrl = "imageUrl";
    private String saltValue;
    private String hash;
    private ImageSlider imageSlider;
    private List<SlideModel> slideModels;
    private long idCounter;
    private int coverIndex;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_room, container, false);
        slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/roomies-85581.appspot.com/o/covers%2F1.jpg?alt=media&token=51b16512-ff5e-49d2-acd5-8286e0c45716"));
        slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/roomies-85581.appspot.com/o/covers%2F2.jpg?alt=media&token=06d5c9cd-a9ae-4023-bb3f-bce2f5c725b7"));
        slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/roomies-85581.appspot.com/o/covers%2F3.jpg?alt=media&token=de7fce9a-1a02-409b-b142-0e2255097e7c"));
        slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/roomies-85581.appspot.com/o/covers%2F4.jpg?alt=media&token=ddcbaf4d-7256-46d2-b347-61ce00707c75"));
        slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/roomies-85581.appspot.com/o/covers%2F5.jpg?alt=media&token=3420fc2e-ebef-4629-a122-a10408772688"));
        imageSlider = rootView.findViewById(R.id.image_slider);
        imageSlider.setImageList(slideModels, true);
        imageSlider.stopSliding();
        imageSlider.setItemClickListener(i -> mApartmentImage = slideModels.get(i).getImageUrl());
        mAuth = FirebaseAuth.getInstance();
        mAuthUser = mAuth.getCurrentUser();
        mAddressField = rootView.findViewById(R.id.apartmentAddress);
        mApartmentNameView = rootView.findViewById(R.id.apartmentName);
        mApartmentNumberView = rootView.findViewById(R.id.apartmentNumber);
        applyButton = rootView.findViewById(R.id.newApplyApartmentButton);
        mDatabase = FirebaseDatabase.getInstance().getReference(); //.child("Apartments")
        apiKey = getString(R.string.app_key);
        saltValue = getString(R.string.saltValue);
        if (!Places.isInitialized()) {
            Places.initialize(getContext(), apiKey);
        }

        mAddressField.addTextChangedListener(new TextWatcher() {
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
        applyButton.setOnClickListener(view -> createRoom());
        return rootView;
    }

    private void autoComplete(){
        PlacesClient placesClient = Places.createClient(getContext());
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363), //dummy lat/lng
                new LatLng(-33.858754, 151.229596));
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(bounds)
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
                    new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, options);

            mAddressField.setAdapter(adapter);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });
    }

    private void createRoom(){
        mApartmentName = mApartmentNameView.getText().toString();
        mAddress = mAddressField.getText().toString();
        mApartmentNumber = mApartmentNumberView.getText().toString();
        DatabaseReference apartmentsDB =  mDatabase.child(apartments);
        mDatabase.child(counter).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long number = (Long)dataSnapshot.getValue();
                mDatabase.child(counter).setValue(number+1);
                idCounter = number+1;
                Hashids hashids = new Hashids(saltValue);
                hash = hashids.encode(idCounter);
                DatabaseReference newApartmentDB =  apartmentsDB.child(hash);
                newApartmentDB.child("id").setValue(hash);
                newApartmentDB.child("imageUrl").setValue(mApartmentImage);
                newApartmentDB.child("name").setValue(mApartmentName);
                newApartmentDB.child("address").setValue(mAddress+ " " + mApartmentNumber);
                newApartmentDB.child("users").child(mAuthUser.getUid()).setValue(mAuthUser.getPhotoUrl().toString());
                newApartmentDB.child("financialBalance").child(mAuth.getCurrentUser().getUid()).child("name").setValue(mAuth.getCurrentUser().getDisplayName());
                newApartmentDB.child("financialBalance").child(mAuth.getCurrentUser().getUid()).child("balance").setValue(0);
                mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("apartmentID").setValue(hash);
                Intent menuIntent = new Intent(getContext(), HomeActivity.class);
                menuIntent.putExtra(apartmentID, hash);
                startActivity(menuIntent);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }




}
