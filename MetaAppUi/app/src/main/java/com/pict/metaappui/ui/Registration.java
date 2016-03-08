package com.pict.metaappui.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONObject;

import java.security.KeyPair;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.pict.metaappui.R;
import com.pict.metaappui.crypto.RSA;
import com.pict.metaappui.util.Preferences;
import com.pict.metaappui.util.RandomString;
import com.pict.metaappui.util.postAsync;
import com.pict.metaappui.util.postAsync1;

public class Registration extends AppCompatActivity {
    private boolean isregistered;
    private static final String TAG="Registration";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String aes_seed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Preferences.init(this);
        //Check for one-time registration and then inflate the activity..
        isregistered = Preferences.getBoolean(Preferences.IS_REGISTERED);
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Log.i(TAG, "Value of isregistered " + isregistered);
        if (checkPlayServices()) {
            if(isregistered)
            {
                String session_expiry_str = Preferences.getString((Preferences.SESSION_EXPIRY));
                Date session_expiry,today;
                try {
                    session_expiry = formatter.parse(session_expiry_str);
                    today = Calendar.getInstance().getTime();
                    Log.i(TAG,"Todays date : "+formatter.format(today));
                    Log.i(TAG,"Session expiry date : "+session_expiry_str);
                    if(today.before(session_expiry)){
                        Log.i(TAG,"Session not expired yet!!!");
                    }
                    else {
                        //Generate AES random seed and post it to server
                        Log.i(TAG, "Session expired!!!");
                        Calendar c = Calendar.getInstance();
                        c.setTime(today);
                        c.add(Calendar.DATE,10);
                        Date new_expiry = c.getTime();
                        Preferences.putString(Preferences.SESSION_EXPIRY, formatter.format(new_expiry));
                        genkey();
                        sendSessionInfo();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Intent mainintent=new Intent(this,Homepage.class);
                startActivity(mainintent);
                Toast.makeText(getApplicationContext(), "User already registered!!!", Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {

                Calendar c = Calendar.getInstance();
                c.setTime(c.getTime());
                c.add(Calendar.DATE, 10);
                Date new_expiry = c.getTime();
                Preferences.putString(Preferences.SESSION_EXPIRY, formatter.format(new_expiry));
                Preferences.putInteger(Preferences.REQUEST_ID, 1);
                Preferences.putBoolean(Preferences.IS_REGISTERED, true);
                Log.i(TAG,"Session expiry set to: "+Preferences.getString(Preferences.SESSION_EXPIRY));
                genkey();
                sendCredInfo();
                Intent mainintent = new Intent(getApplicationContext(), Homepage.class);
                startActivity(mainintent);
                Toast.makeText(getApplicationContext(), "User successfully registered!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void genkey(){
        /*
        To generate RSA Keypais but not required now
        final KeyPair kp= RSA.generate();
        Crypto.writePublicKeyToPreferences(kp);
        Crypto.writePrivateKeyToPreferences(kp);
        Log.i(TAG, "Keypair generated");
        Log.i(TAG, "Private Key:" + Preferences.getString(Preferences.RSA_PUBLIC_KEY));
        Log.i(TAG, "Private Key:" + Preferences.getString(Preferences.RSA_PRIVATE_KEY));
        */
        RandomString rs=new RandomString(10);
        aes_seed = rs.nextString();
        Preferences.putString(Preferences.AES_SEED, aes_seed);
        Log.i(TAG,"AES Seed:"+Preferences.getString(Preferences.AES_SEED));

    }

    private void sendCredInfo()
    {
        try {
            JSONObject jobj = new JSONObject();
            jobj.accumulate("AesSeed", aes_seed);
            String jstr;
            jstr=jobj.toString();
            Log.i(TAG, jstr);
            String rsaEncryptSeed=RSA.encryptWithKey(Preferences.SERVER_PUB_KEY, jstr);
            //Log.i(TAG, "Encrpted Seed:" + rsaEncryptSeed);
            Log.i(TAG, "Message :" + rsaEncryptSeed);
            new postAsync1("Registration...",this).execute("2","Message",rsaEncryptSeed, Preferences.url);
            Log.i(TAG,"Sent cred");
            /*
            Decode Code

            JSONObject tryDecode=new JSONObject(sendStr);
            String encrptedSeed=tryDecode.getString("Seed");
            String encrptedmessage=tryDecode.getString("Message");
            String dseed=RSA.decryptWithStoredKey(encrptedSeed);
            Log.i(TAG,"Decrypted Seed:"+dseed);

            String djstr=AESCrypt.decrypt(seed,encrptedmessage);
            Log.i(TAG,"Decrypted Message:"+djstr);
            */
        }
        catch (Exception e)
        {
            Log.i(TAG,e.toString());
            e.printStackTrace();
        }
    }

    private void sendSessionInfo()
    {
        try {
            JSONObject jobj = new JSONObject();
            jobj.accumulate("AesSeed", aes_seed);
            jobj.accumulate("Uuid", Preferences.getString(Preferences.PHONE_NUMBER));
            String jstr;
            jstr=jobj.toString();
            Log.i(TAG, jstr);
            String rsaEncryptMsg=RSA.encryptWithKey(Preferences.SERVER_PUB_KEY, jstr);
            Log.i(TAG, "Message :" + rsaEncryptMsg);
            new postAsync("Loading...",this).execute("2","Message",rsaEncryptMsg, Preferences.url+"session/update");
            Log.i(TAG,"Sent cred");
        }
        catch (Exception e)
        {
            Log.i(TAG,e.toString());
            e.printStackTrace();
        }
    }

}


