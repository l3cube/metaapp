package com.pict.metaappui.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.pict.metaappui.R;
import com.pict.metaappui.util.SampleSlide;

/**
 * Created by tushar on 6/3/16.
 */
public class IntroApp extends AppIntro {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(SampleSlide.newInstance(R.layout.intro1));
        addSlide(SampleSlide.newInstance(R.layout.intro2));
        addSlide(SampleSlide.newInstance(R.layout.intro3));
    }

    private void loadActivity(){
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    @Override
    public void onNextPressed() {
    }

    @Override
    public void onSkipPressed() {
        loadActivity();
        Toast.makeText(getApplicationContext(),"Skip", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDonePressed() {
        loadActivity();
    }

    @Override
    public void onSlideChanged() {
    }

    public void getStarted(View v){
        loadActivity();
    }
}
