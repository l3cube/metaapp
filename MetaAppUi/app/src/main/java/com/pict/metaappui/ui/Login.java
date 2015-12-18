package com.pict.metaappui.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pict.metaappui.R;
import com.pict.metaappui.util.Preferences;

import java.util.Arrays;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    LoginButton fbLoginButton;
    CallbackManager callbackManager;
    boolean isGoogleLogin;
    GoogleSignInOptions gso;
    GoogleApiClient mGoogleApiClient;
    SignInButton googleLoginButton;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        isGoogleLogin=false;

        //Facebook Login Code
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager=CallbackManager.Factory.create();
        fbLoginButton=(LoginButton)findViewById(R.id.fb_login_button);
        fbLoginButton.setReadPermissions(Arrays.asList("email"));
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //bug in fb login
                /*
                AccessToken accessToken=loginResult.getAccessToken();
                Profile profile=Profile.getCurrentProfile();
                String userName=profile.getName();
                String userEmail=profile.getId();
                Uri userPhoto=profile.getProfilePictureUri(64,64);
                Preferences.putString(Preferences.USER_NAME,userName);
                Preferences.putString(Preferences.USER_EMAIL,userEmail);
                Preferences.putString(Preferences.USER_PHOTO,userPhoto.toString());
                */
                Toast.makeText(getApplicationContext(), "Successfull Login", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Login Error", Toast.LENGTH_SHORT).show();
            }
        });

        //Google Login Code
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestProfile().build();
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleLoginButton=(SignInButton)findViewById(R.id.google_login_button);
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
    }


    public void googleSignIn(){
        isGoogleLogin=true;
        Intent signInIntent=Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(isGoogleLogin){
            if(requestCode==RC_SIGN_IN){
                GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account=result.getSignInAccount();
            String userName=account.getDisplayName();
            String userEmail=account.getEmail();
            Uri userPhoto=account.getPhotoUrl();
            Preferences.putString(Preferences.USER_NAME,userName);
            Preferences.putString(Preferences.USER_EMAIL,userEmail);
            Preferences.putString(Preferences.USER_PHOTO,userPhoto.toString());
            Toast.makeText(getApplicationContext(),"Sign in account:"+account.getEmail(),Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
    }
}
