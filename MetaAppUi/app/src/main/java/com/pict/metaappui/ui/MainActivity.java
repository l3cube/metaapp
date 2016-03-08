package com.pict.metaappui.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.pict.metaappui.util.Preferences;

public class MainActivity extends Activity {
    private boolean showIntro;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.init(this);
        showIntro = Preferences.getBoolean(Preferences.SHOW_INTRO,true);
        Log.i(TAG, "Value of showIntro: " + showIntro);
        if(showIntro) {
            Preferences.putBoolean(Preferences.SHOW_INTRO, false);
            Intent i = new Intent(getApplicationContext(), IntroApp.class);
            startActivity(i);
        }
        else{
            Intent i = new Intent(getApplicationContext(), Registration.class);
            startActivity(i);
        }
    }
}
