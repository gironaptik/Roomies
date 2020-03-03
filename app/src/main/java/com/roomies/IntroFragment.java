
package com.roomies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class IntroFragment extends Fragment {

    private DatabaseReference mApartmentDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private Button newRoomB;
    private Button joinRoom;
    private TextView logout;
    private EditText joinRoomCode;
    private TextView joinRoomB;
    private CommunicationInterface callback;
    private RelativeLayout introFrame;
    private View rootView;
    private String apartmentID = "apartmentID";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_intro, container, false);
        mAuth = FirebaseAuth.getInstance();
        mApartmentDatabase = FirebaseDatabase.getInstance().getReference().child("Apartments");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        logout = rootView.findViewById(R.id.logoutButton);
        joinRoomB = rootView.findViewById(R.id.joinRoomButton);
        newRoomB = rootView.findViewById(R.id.newRoomButton);
        logout.setOnClickListener(view -> signOut());
        newRoomB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback= (CommunicationInterface) getActivity();
                callback.onSuccess();//according to your purpose use where ever you like
            }
        });
        introFrame = rootView.findViewById(R.id.introFragLayout);

        joinRoomB.setOnClickListener(view -> onButtonShowPopupWindowClick(view));
        return rootView;
    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback= (CommunicationInterface) activity;
    }

    public void onButtonShowPopupWindowClick(View view) {

        LayoutInflater inflater = (LayoutInflater)getActivity().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.join_room, introFrame, false);
        popupView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.popupanim));
        int width = (int) (ViewGroup.LayoutParams.WRAP_CONTENT);
        int height = (int) (ViewGroup.LayoutParams.WRAP_CONTENT);
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        joinRoom = popupView.findViewById(R.id.joinroomB);
        joinRoomCode = popupView.findViewById(R.id.joinroomCode);
        joinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApartmentDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String apartmentCode = joinRoomCode.getText().toString();
                        if(apartmentCode.equals(""))
                            Toast.makeText(getContext(), "You should type something", Toast.LENGTH_LONG).show();
                        else{
                            if(dataSnapshot.hasChild(apartmentCode))
                            {
                                DatabaseReference mnApartmentDatabase = FirebaseDatabase.getInstance().getReference().child("Apartments");
                                mUserDatabase.child(mAuth.getCurrentUser().getUid()).child("apartmentID").setValue(apartmentCode);
                                mnApartmentDatabase.child(apartmentCode).child("users").child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getPhotoUrl().toString());
                                Intent apartmentIntent = new Intent(getContext(), HomeActivity.class);
                                apartmentIntent.putExtra(apartmentID, joinRoomCode.getText().toString());
                                apartmentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(apartmentIntent);
                            }
                            else
                                Toast.makeText(getContext(), "Apartment code not Exist!", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
//        sensorSwitch = popupView.findViewById(R.id.sensorSwitch);
////        sensorSwitchChanged();

    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(getContext(),LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

}
