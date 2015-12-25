package com.pict.metaappui.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pict.metaappui.R;
import com.pict.metaappui.adapter.GridAdapter;
import com.pict.metaappui.adapter.RequestListAdapter;
import com.pict.metaappui.modal.FulfilledRequest;
import com.pict.metaappui.modal.PendingRequest;
import com.pict.metaappui.modal.UserRequest;
import com.pict.metaappui.modal.UserResponses;
import com.pict.metaappui.util.DatabaseHelper;
import com.pict.metaappui.util.DividerItemDecoration;
import com.pict.metaappui.util.Preferences;
import com.pict.metaappui.util.postAsync2;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Offer_received extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RequestListAdapter mAdapter;
    private static final String TAG="Offer_received";
    private List<Object> mItems=new ArrayList<Object>();
    private List<UserRequest> items;
    DatabaseHelper db;

    public Offer_received() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offer_received, container, false);
    }

    /*
    If request is not pending fetch the no of responses for the field along with a list of responses for the request
    and store in the object to be used for updating the Ui
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new postAsync2("Retrieving Offers...",getActivity()).execute("1", "Uuid", Preferences.getString(Preferences.PHONE_NUMBER), Preferences.proxyurl + "android/receive");
        Log.i(TAG, "Offers retrieved");

        db=new DatabaseHelper(getActivity());
        items=db.getAllUserRequests();
        mItems.clear();
        PendingRequest pr;
        FulfilledRequest fr;
        List<UserResponses> responses;
        int no_of_responses;
        for(UserRequest i : items){
            if(i.isPending()){
                pr=new PendingRequest(i.getRequestId(),i.getTopic(),i.getIntent_desc(),i.getDeadline());
                mItems.add(pr);
            }
            else{
                responses=db.getUserResponses(i.getRequestId());
                no_of_responses=responses.size();
                fr=new FulfilledRequest(i.getRequestId(),i.getTopic(),i.getIntent_desc(),i.getDeadline(),no_of_responses,responses);
                mItems.add(fr);
            }
        }

        mRecyclerView=(RecyclerView)getView().findViewById(R.id.offer_received_rv);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.ItemDecoration itemDecoration=new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        mLayoutManager=new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter=new RequestListAdapter(mItems);
        mRecyclerView.setAdapter(mAdapter);
    }
}
