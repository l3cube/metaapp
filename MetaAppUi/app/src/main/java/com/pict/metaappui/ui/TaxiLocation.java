package com.pict.metaappui.ui;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.pict.metaappui.R;
import com.pict.metaappui.crypto.AESnew;
import com.pict.metaappui.crypto.RSA;
import com.pict.metaappui.modal.UserRequest;
import com.pict.metaappui.util.DatabaseHelper;
import com.pict.metaappui.util.DatePickerFragment;
import com.pict.metaappui.util.Preferences;
import com.pict.metaappui.util.TimePickerFragment;
import com.pict.metaappui.util.postAsync;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaxiLocation extends Fragment implements GoogleApiClient.OnConnectionFailedListener, postAsync.PostExecuteInterface {

    private String uuid;
    private String requestId;
    private String category;
    private String description;
    private String tlc;

    private String pickUpLocation;
    private String dropOffLocation;
    private String expiryDate;
    private String expiryTime;

    private String TAG = "TaxiLocation";
    private TextView pickUpText;
    private TextView dropOfText;
    private Button publishButton;
    private EditText categoryText;
    private EditText descriptionText;
    private TextView expiryDateText;
    private TextView expiryTimeText;
    private ImageButton expiryDateButton;
    private ImageButton expiryTimeButton;

    private DatabaseHelper db;
    private GoogleApiClient mGoogleApiClient;
    private int PickUp_Location_Request = 1;
    private int DropOf_Location_Request = 2;

    public TaxiLocation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_taxi_location, container, false);

        //category=getArguments().getString("Category");
        tlc=getArguments().getString("TLC");

        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        pickUpText = (TextView)view.findViewById(R.id.pickUpText);
        dropOfText = (TextView)view.findViewById(R.id.dropOffText);
        publishButton = (Button)view.findViewById(R.id.publishButton);
        categoryText = (EditText)view.findViewById(R.id.categoryText);
        descriptionText = (EditText)view.findViewById(R.id.descriptionText);
        expiryDateText = (TextView)view.findViewById(R.id.expiryDateText);
        expiryTimeText = (TextView)view.findViewById(R.id.expiryTimeText);
        expiryDateButton = (ImageButton)view.findViewById(R.id.expiryDateButton);
        expiryTimeButton = (ImageButton)view.findViewById(R.id.expiryTimeButton);

        categoryText.setText(tlc);
        categoryText.setEnabled(false);

        pickUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PickUp_Location_Request);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

        dropOfText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()),DropOf_Location_Request);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

        expiryDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });

        expiryTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime();
            }
        });

        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onPublishButtonClicked();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    public void pickDate(){
        DatePickerFragment dateFragment=new DatePickerFragment();
        dateFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                expiryDate=new StringBuilder().append(year).append("-").append(monthOfYear+1).append("-").append(dayOfMonth).toString();
                expiryDateText.setText(expiryDate);
            }
        });
        dateFragment.show(getFragmentManager(),"Expiry Date");
    }

    public void pickTime(){
        TimePickerFragment timeFragment=new TimePickerFragment();
        timeFragment.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                expiryTime = new StringBuilder().append(hourOfDay).append(":").append(minute).append(":").append(0).toString();
                expiryTimeText.setText(expiryTime);
            }
        });
        timeFragment.show(getFragmentManager(),"Expiry Time");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed");
        Toast.makeText(getContext(),"Connection failure. Please try again!",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PickUp_Location_Request && resultCode == -1){
            Place place = PlacePicker.getPlace(getContext(),data);
            pickUpLocation = place.getAddress().toString();
            pickUpText.setText(pickUpLocation);;
            Toast.makeText(getContext(),"Pickup Location: "+place.getName(),Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == DropOf_Location_Request && resultCode == -1){
            Place place = PlacePicker.getPlace(getContext(),data);
            dropOffLocation = place.getAddress().toString();
            dropOfText.setText(dropOffLocation);;
            Toast.makeText(getContext(),"Dropof Location: "+place.getName(),Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(),"No Location selected. Please try again!",Toast.LENGTH_SHORT).show();
        }

    }

    public void onPublishButtonClicked() throws JSONException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException {
        uuid = Preferences.getString(Preferences.PHONE_NUMBER);
        requestId = Integer.toString(Preferences.getInteger(Preferences.REQUEST_ID));
        description=descriptionText.getText().toString();

        JSONObject deadline=new JSONObject();
        deadline.accumulate("Date",expiryDate);
        deadline.accumulate("Time",expiryTime);

        JSONObject message=new JSONObject();
        message.accumulate("Intent_Description",description);
        message.accumulate("Deadline",deadline.toString());
        message.accumulate("PickUp_Location",pickUpLocation);
        message.accumulate("DropOff_Location",dropOffLocation);

        JSONObject finalmessage=new JSONObject();
        finalmessage.accumulate("Uuid",uuid);
        finalmessage.accumulate("RequestId",requestId);
        finalmessage.accumulate("Topic", tlc);
        finalmessage.accumulate("Message", message.toString());

        String plainText=finalmessage.toString();
        String seed=Preferences.getString(Preferences.AES_SEED);
        String aesEncrptJstr= AESnew.getInstance(seed).encrypt_string(plainText);
        Log.i(TAG, "Encrpyted Message:" + aesEncrptJstr);
        Log.i(TAG, "Length:" + aesEncrptJstr.length());
        String rsaEncryptSeed= RSA.encryptWithKey(Preferences.SERVER_PUB_KEY, seed);
        JSONObject sendJson=new JSONObject();
        sendJson.accumulate("Message",aesEncrptJstr);
        sendJson.accumulate("Seed", rsaEncryptSeed);
        String sendStr=sendJson.toString();
        Log.i(TAG, "Final Message:" + sendStr);
        new postAsync("Publishing Intent...",getActivity(),this).execute("4", "Message", aesEncrptJstr, "Seed", rsaEncryptSeed, Preferences.url + "receive/");
        Log.i(TAG, "Publish intent");


    }

    @Override
    public void postExecute(int responseCode) {
        if(responseCode==200){
            //add to database
            db=new DatabaseHelper(getActivity());
            UserRequest obj=new UserRequest();
            obj.setRequestId(Integer.parseInt(requestId));
            obj.setTopic(tlc);
            obj.setIntent_desc(description);
            obj.setDeadline_date(expiryDate);
            obj.setDeadline_time(expiryTime);
            obj.setPending(true);
            long id=db.createUserRequest(obj);
            db.closeDB();
            Log.i(TAG,"Entry made with id "+id);
            Preferences.putInteger(Preferences.REQUEST_ID,Integer.parseInt(requestId)+1);
        }
    }
}
