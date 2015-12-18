package com.pict.metaappui.ui;


import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
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
import android.widget.Toast;

import com.pict.metaappui.R;
import com.pict.metaappui.crypto.AESnew;
import com.pict.metaappui.crypto.RSA;
import com.pict.metaappui.util.Preferences;
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
public class Intent_publish extends Fragment {
    String uuid;
    String requestId;
    String category;
    String description;
    String deadline;
    String tlc;
    private static final String TAG="Intent_publish";

    Button publishButton;
    EditText categoryText;
    EditText descriptionText;
    EditText expiryDateText;
    Button expiryButton;

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

        expiryDateText=(EditText)view.findViewById(R.id.expiryDateText);
        expiryButton=(Button)view.findViewById(R.id.expiryDateButton);
        expiryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExpiryButtonClicked();
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

    public void onExpiryButtonClicked(){
        DatePickerFragment dateFragment=new DatePickerFragment();
        dateFragment.setOnDateSetListener(new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                deadline=new StringBuilder().append(dayOfMonth).append("-").append(monthOfYear+1).append("-").append(year).toString();
                expiryDateText.setText(deadline);
            }
        });
        dateFragment.show(getFragmentManager(),"Expiry Date");
    }

    public void onPublishButtonClicked() throws JSONException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException {
        uuid = Preferences.getString(Preferences.PHONE_NUMBER);
        requestId = Integer.toString(Preferences.getInteger(Preferences.REQUEST_ID));
        description=descriptionText.getText().toString();

        JSONObject message=new JSONObject();
        message.accumulate("Intent_Description",description);
        message.accumulate("Deadline", deadline);

        JSONObject finalmessage=new JSONObject();
        finalmessage.accumulate("Uuid",uuid);
        finalmessage.accumulate("RequestId",requestId);
        finalmessage.accumulate("Topic", tlc);
        finalmessage.accumulate("Message", message.toString());

        String plainText=finalmessage.toString();
        String seed="Aes seed value";
        String aesEncrptJstr= AESnew.getInstance().encrypt_string(plainText);
        Log.i(TAG, "Encrpyted Message:" + aesEncrptJstr);
        Log.i(TAG, "Length:" + aesEncrptJstr.length());
        String rsaEncryptSeed= RSA.encryptWithKey(Preferences.SERVER_PUB_KEY, seed);
        JSONObject sendJson=new JSONObject();
        sendJson.accumulate("Message",aesEncrptJstr);
        sendJson.accumulate("Seed", rsaEncryptSeed);
        String sendStr=sendJson.toString();
        Log.i(TAG, "Final Message:" + sendStr);
        new postAsync("Publishing Intent...",getActivity()).execute("4", "Message", aesEncrptJstr, "Seed", rsaEncryptSeed, Preferences.url + "receive/");
        Log.i(TAG, "Publish intent");

        Preferences.putInteger(Preferences.REQUEST_ID,Integer.parseInt(requestId)+1);
    }

    public static class DatePickerFragment extends DialogFragment{

        private OnDateSetListener onDateSetListener;

        public DatePickerFragment() {}

        public void setOnDateSetListener(OnDateSetListener onDateSetListener) {
            this.onDateSetListener = onDateSetListener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
        }

    }
}
