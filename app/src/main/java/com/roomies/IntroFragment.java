
package com.roomies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import org.w3c.dom.Text;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class IntroFragment extends Fragment {


    private Button newRoomB;
    private Button joinRoom;
    private EditText joinRoomCode;
    private TextView joinRoomB;
    private CommunicationInterface callback;
    private RelativeLayout introFrame;
    private View rootView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_intro, container, false);

        joinRoomB = rootView.findViewById(R.id.joinRoomButton);

        newRoomB = rootView.findViewById(R.id.newRoomButton);
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

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)getActivity().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.join_room, introFrame, false);
        int width = (int) (ViewGroup.LayoutParams.WRAP_CONTENT);
        int height = (int) (ViewGroup.LayoutParams.WRAP_CONTENT);
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        joinRoom = popupView.findViewById(R.id.joinroomB);
        joinRoomCode = popupView.findViewById(R.id.joinroomCode);
        joinApartment();
//        sensorSwitch = popupView.findViewById(R.id.sensorSwitch);
////        sensorSwitchChanged();

    }

    private void joinApartment(){
        joinRoom.setOnClickListener(view -> {
            String code = joinRoomCode.getText().toString();
        });
    }

}
