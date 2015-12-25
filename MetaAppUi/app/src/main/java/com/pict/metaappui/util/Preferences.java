package com.pict.metaappui.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Preferences {
    public static final String url="http://10.42.0.1:5000/";
    public static final String proxyurl="http://10.23.18.144:5000/";
    public static final String SHARED_PREFERENCES = "SHARED_PREFERENCES";
    public static final String RSA_GENERATED = "RSA_GENERATED";
    public static final String RSA_PUBLIC_KEY = "RSA_PUBLIC_KEY";
    public static final String RSA_PRIVATE_KEY = "RSA_PRIVATE_KEY";
    public static final String ENCRYPTED_MESSAGE = "ENCRYPTED_MESSAGE";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String GET_TOKEN = "token";
    public static final String GET_IMSI = "imsi";
    public static final String IS_REGISTERED="registered";
    public static final String PHONE_NUMBER="number";
    public static final String REQUEST_ID="reqid";
    public static String SERVER_PUB_KEY="";

    //Login user deatils
    public static final String USER_NAME="userName";
    public static final String USER_EMAIL="userEmail";
    public static final String USER_PHOTO="userPhoto";

    public static SharedPreferences mPreferences;

    public static void init(Context context) {
        mPreferences = context.getSharedPreferences(SHARED_PREFERENCES, 0);
        BufferedReader reader=null;
        StringBuilder sb=new StringBuilder();
        try{
            reader=new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("serverPub.key")));
            String line;
            while ((line=reader.readLine())!=null){
                sb.append(line);
                sb.append("\n");
            }
            SERVER_PUB_KEY=sb.toString();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    public static boolean getBoolean(String key) {
        return mPreferences.getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return mPreferences.getBoolean(key, defaultValue);
    }

    public static void putBoolean(String key, boolean bool) {
        mPreferences.edit().putBoolean(key, bool).commit();
    }

    public static void putString(String key, String s) {
        mPreferences.edit().putString(key, s).commit();
    }

    public static void putInteger(String key, Integer integer) {
        mPreferences.edit().putInt(key, integer).commit();
    }


    public static void clear() {
        mPreferences.edit().clear().commit();
    }

    public static void remove(String key) {
        mPreferences.edit().remove(key).commit();
    }

    public static String getString(String key) {
        return mPreferences.getString(key, null);
    }

    public static int getInteger(String key) {
        return mPreferences.getInt(key, 0);
    }


}
