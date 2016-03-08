package com.pict.metaappui.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.pict.metaappui.modal.UserRequest;
import com.pict.metaappui.modal.UserResponses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tushar on 4/11/15.
 * Aync Task to send uuid_requestid to proxy and fetch the responses if available
 */

public class postAsync1 extends AsyncTask<String, Integer, Integer> {

    String response="";
    String TAG="postAsync1";
    String displayMessage;
    ProgressDialog progressDialog;
    Activity activity;
    DatabaseHelper db;

    public postAsync1(String displayMessage,Activity activity){
        this.displayMessage=displayMessage;
        this.activity=activity;
        progressDialog=new ProgressDialog(activity);
    }

    @Override
    protected Integer doInBackground(String... params) {
        // TODO Auto-generated method stub
        TAG = "postAsync1";
        int no_params=Integer.parseInt(params[0]);
        Map<String, String> parameters = new HashMap<String, String>();
        int i=1;
        while(i<=no_params){
            parameters.put(params[i],params[i+1]);
            i=i+2;
        }
        String endpoint = params[i];
        int responseCode=postData(endpoint, parameters);
        return responseCode;
    }

    public int postData(String endpoint,Map<String, String> params) {
        int status=404;
        try {
            URL url;
            try {
                url = new URL(endpoint);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("invalid url: " + endpoint);
            }
            StringBuilder bodyBuilder = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            // constructs the POST body using the parameters
            while (iterator.hasNext()) {
                Map.Entry<String, String> param = iterator.next();
                bodyBuilder.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    bodyBuilder.append('&');
                }
            }
            String body = bodyBuilder.toString();
            Log.v(TAG, "Posting '" + body + "' to " + url);
            byte[] bytes = body.getBytes();
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                //conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                conn.setRequestProperty("Accept-Encoding", "");
                if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                    conn.setRequestProperty("Connection", "close");
                }
                conn.connect();
                // post the request
                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();
                // handle the response
                status = conn.getResponseCode();
                if (status != 200) {
                    Log.i(TAG, "Post failed with error code " + status);
                } else {
                    Log.i(TAG, "Message sent to server with response code: " + status);
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    Log.i(TAG, "Message sent to server with response message: " + response);

                }
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage(displayMessage);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /*
    Add code to make pending field to false for the request ids in the database for whom we have recieved reponses.
     */
    @Override
    protected void onPostExecute(Integer responseCode) {
        super.onPostExecute(responseCode);
        db=new DatabaseHelper(activity);

        if(responseCode==200){
            try {
                JSONObject obj =new JSONObject(response);
                String uuid = obj.getString("Uuid");
                Preferences.putString(Preferences.PHONE_NUMBER, uuid);
                Log.i(TAG,"Uuid received: "+uuid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            Toast.makeText(activity,"Success",Toast.LENGTH_SHORT).show();
        }
        else{
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            Toast.makeText(activity,"Failure",Toast.LENGTH_SHORT).show();
        }
    }
}