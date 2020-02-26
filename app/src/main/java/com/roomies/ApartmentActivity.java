package com.roomies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class ApartmentActivity extends AppIntro2 implements CommunicationInterface{

    private Button newRoom;
    private int counter = 0;
    private Fragment introFragment;
    private Fragment newRoomFragment;
    private int slidesNum;
    private Bundle state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        state = savedInstanceState;
        showPagerIndicator(false);
        setAppIntro();
    }

    private void setAppIntro(){
        if(counter == 0) {
            introFragment = new IntroFragment();
            newRoomFragment = new NewRoomFragment();
            addSlide(introFragment);
//            addSlide(AppIntroFragment.newInstance("First App intro", "First App intro details", R.drawable.one,
//                    ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
            addSlide(newRoomFragment);
            setNextPageSwipeLock(true);
            showPagerIndicator(false);
            counter += 1;
        }

        if(counter == 2){
            setNextPageSwipeLock(false);
            showPagerIndicator(true);

            getPager().goToNextSlide();
        }

        if(counter > 2){
            getPager().goToNextSlide();
        }
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent menuIntent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(menuIntent);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent menuIntent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(menuIntent);
    }

    @Override
    public void onSuccess() {
        counter += 1;
        setAppIntro();
    }

    @Override
    public void onFailed() {

    }
}
