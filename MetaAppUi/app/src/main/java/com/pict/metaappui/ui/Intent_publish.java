package com.pict.metaappui.ui;


import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
public class Intent_publish extends Fragment implements postAsync.PostExecuteInterface {
    String uuid;
    String requestId;
    String category;
    String description;
    String tlc;

    private String expiryDate;
    private String expiryTime;

    private static final String TAG="Intent_publish";

    Button publishButton;
    EditText categoryText;
    EditText descriptionText;
    TextView expiryDateText;
    TextView expiryTimeText;
    ImageButton expiryDateButton;
    ImageButton expiryTimeButton;
    DatabaseHelper db;

    public Intent_publish() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        category=getArguments().getString("Category");
        tlc=getArguments().getString("TLC");

        View view=inflater.inflate(R.layout.fragment_intent_publish, container, false);

        categoryText=(EditText)view.findViewById(R.id.categoryText);
        categoryText.setText(tlc);
        categoryText.setEnabled(false);

        descriptionText=(EditText)view.findViewById(R.id.descriptionText);

        expiryDateText = (TextView)view.findViewById(R.id.expiryDateText);
        expiryTimeText = (TextView)view.findViewById(R.id.expiryTimeText);
        expiryDateButton = (ImageButton)view.findViewById(R.id.expiryDateButton);
        expiryTimeButton = (ImageButton)view.findViewById(R.id.expiryTimeButton);

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

        publishButton=(Button)view.findViewById(R.id.publishButton);
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onPublishButtonClicked();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
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
                expiryDate = new StringBuilder().append(year).append("-").append(monthOfYear + 1).append("-").append(dayOfMonth).toString();
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
