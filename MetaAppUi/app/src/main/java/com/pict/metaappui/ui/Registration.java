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

import com.pict.metaappui.R;
import com.pict.metaappui.crypto.Crypto;
import com.pict.metaappui.crypto.RSA;
import com.pict.metaappui.util.Preferences;
import com.pict.metaappui.util.postAsync;
import com.pict.metaappui.crypto.AESnew;

public class Registration extends AppCompatActivity {
    private EditText phonenumberedittext;
    private Button registerbutton;
    private boolean isregistered;
    private String number;
    private static final String TAG="Registration";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.init(this);
        //Check for one-time registration and then inflate the activity..
        isregistered = Preferences.getBoolean(Preferences.IS_REGISTERED);
        Log.i(TAG, "Value of isregistered " + isregistered);
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            if(isregistered)
            {
                Intent mainintent=new Intent(this,Homepage.class);
                startActivity(mainintent);
                Toast.makeText(getApplicationContext(), "User already registered!!!", Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                setContentView(R.layout.activity_registration);
                phonenumberedittext=(EditText)findViewById(R.id.phonenumberedittext);
                registerbutton=(Button)findViewById(R.id.registerbutton);
                registerbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        number = phonenumberedittext.getText().toString();
                        Preferences.putString(Preferences.PHONE_NUMBER, number);
                        Preferences.putInteger(Preferences.REQUEST_ID,1);
                        Preferences.putBoolean(Preferences.IS_REGISTERED, true);
                        genkey();
                        sendcred();
                        Intent mainintent = new Intent(getApplicationContext(), Homepage.class);
                        startActivity(mainintent);
                        Toast.makeText(getApplicationContext(), "User successfully registered!!!", Toast.LENGTH_SHORT).show();
                    }
                });
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
        final KeyPair kp= RSA.generate();
        Crypto.writePublicKeyToPreferences(kp);
        Crypto.writePrivateKeyToPreferences(kp);
        Log.i(TAG, "Keypair generated");
        Log.i(TAG,"Private Key:"+Preferences.getString(Preferences.RSA_PUBLIC_KEY));
        Log.i(TAG,"Private Key:"+Preferences.getString(Preferences.RSA_PRIVATE_KEY));

    }

    private void sendcred()
    {
        try {
            String pubKey=Preferences.getString(Preferences.RSA_PUBLIC_KEY);
            JSONObject jobj = new JSONObject();
            jobj.accumulate("Pubkey", pubKey);
            jobj.accumulate("UUID", number);
            String jstr;
            jstr=jobj.toString();
            Log.i(TAG, jstr);
            String seed="Aes seed value";
            String aesEncrptJstr= AESnew.getInstance().encrypt_string(jstr);
            Log.i(TAG, "Encrpyted Message:" + aesEncrptJstr);
            Log.i(TAG,"Length:"+aesEncrptJstr.length());
            //Log.i(TAG,"SP:"+Preferences.getString(Preferences.RSA_PUBLIC_KEY));
            //Log.i(TAG,"TF:"+Preferences.SERVER_PUB_KEY);
            String rsaEncryptSeed=RSA.encryptWithKey(Preferences.SERVER_PUB_KEY, seed);
            //Log.i(TAG, "Encrpted Seed:" + rsaEncryptSeed);
            JSONObject sendJson=new JSONObject();
            sendJson.accumulate("Message",aesEncrptJstr);
            sendJson.accumulate("Seed", rsaEncryptSeed);
            String sendStr=sendJson.toString();
            Log.i(TAG, "Final Message:" + sendStr);
            new postAsync("Registration...",this).execute("4","Message",aesEncrptJstr,"Seed", rsaEncryptSeed, Preferences.url);
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

}


