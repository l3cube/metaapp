package com.pict.metaappui.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.pict.metaappui.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Intent_publish extends Fragment {

    String category;
    String description;
    String deadline;

    Button publishButton;
    EditText categoryText;
    EditText descriptionText;
    DatePicker expiryDate;

    public Intent_publish() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        category=getArguments().getString("Category");
        View view=inflater.inflate(R.layout.fragment_intent_publish, container, false);

        categoryText=(EditText)view.findViewById(R.id.categoryText);
        categoryText.setText(category);

        publishButton=(Button)view.findViewById(R.id.publishButton);
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPublishButtonClicked();
            }
        });
        return view;
    }

    public void onPublishButtonClicked(){
        Toast.makeText(getActivity(), "Send resp intent", Toast.LENGTH_SHORT).show();
    }


}
