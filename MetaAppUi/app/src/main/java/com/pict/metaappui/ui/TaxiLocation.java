package com.pict.metaappui.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.pict.metaappui.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaxiLocation extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private String TAG = "TaxiLocation";
    private TextView pickUpText;
    private TextView dropOfText;
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
        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(),this)
                .build();

        pickUpText = (TextView)view.findViewById(R.id.pickUpText);
        dropOfText = (TextView)view.findViewById(R.id.dropText);

        pickUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()),PickUp_Location_Request);
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
        return view;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG,"Connection failed");
        Toast.makeText(getContext(),"Connection failure. Please try again!",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PickUp_Location_Request && resultCode == -1){
            Place place = PlacePicker.getPlace(getContext(),data);
            Toast.makeText(getContext(),"Pickup Location: "+place.getName(),Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == DropOf_Location_Request && resultCode == -1){
            Place place = PlacePicker.getPlace(getContext(),data);
            Toast.makeText(getContext(),"Dropof Location: "+place.getName(),Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(),"No Location selected. Please try again!",Toast.LENGTH_SHORT).show();
        }

    }
}
