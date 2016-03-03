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
import com.pict.metaappui.adapter.RequestListAdapter;
import com.pict.metaappui.modal.RequestChild;
import com.pict.metaappui.modal.RequestParent;
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
    private List<RequestParent> mItems=new ArrayList<RequestParent>();
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
        RequestParent req;
        RequestChild res;
        List<UserResponses> responses;
        List<RequestChild> mResponses;
        int no_of_responses;
        for(UserRequest i : items){
            if(i.isPending()){
                req=new RequestParent(i.getRequestId(),i.getTopic(),i.getIntent_desc(),i.getDeadline(),0,null);
                mItems.add(req);
            }
            else{
                mResponses=new ArrayList<RequestChild>();
                responses=db.getUserResponses(i.getRequestId());
                for(UserResponses r : responses){
                    res=new RequestChild(r.getRequestId(),r.getTopic(),r.getService_desc(),r.getTime_to_complete(),r.getCost());
                    mResponses.add(res);
                }
                no_of_responses=mResponses.size();
                req=new RequestParent(i.getRequestId(),i.getTopic(),i.getIntent_desc(),i.getDeadline(),no_of_responses,mResponses);
                mItems.add(req);
            }
        }

        mRecyclerView=(RecyclerView)getView().findViewById(R.id.offer_received_rv);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.ItemDecoration itemDecoration=new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        mLayoutManager=new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter=new RequestListAdapter(getActivity(),mItems);
        mAdapter.onRestoreInstanceState(savedInstanceState);
        //mAdapter.setCus
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.onSaveInstanceState(outState);
    }

}
