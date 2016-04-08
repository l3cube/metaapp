package com.pict.metaappui.ui;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pict.metaappui.R;
import com.pict.metaappui.adapter.RequestListAdapter;
import com.pict.metaappui.modal.RequestChild;
import com.pict.metaappui.modal.RequestParent;
import com.pict.metaappui.modal.SellerRank;
import com.pict.metaappui.modal.UserRequest;
import com.pict.metaappui.modal.UserResponses;
import com.pict.metaappui.util.CostComparator;
import com.pict.metaappui.util.DatabaseHelper;
import com.pict.metaappui.util.DividerItemDecoration;
import com.pict.metaappui.util.Preferences;
import com.pict.metaappui.util.RankComparator;
import com.pict.metaappui.util.RatingComparator;
import com.pict.metaappui.util.TimeComparator;
import com.pict.metaappui.util.postAsync2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Offer_received extends Fragment implements UserReviewDialog.UserReviewDialogListener, RequestListAdapter.OnChildItemClickedListener, postAsync2.PostExecuteInterface, SortOptionDialog.sortOptionListener {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RequestListAdapter mAdapter;
    private static final String TAG="Offer_received";
    private List<RequestParent> mItems=new ArrayList<RequestParent>();
    private List<UserRequest> items;
    DatabaseHelper db;
    int sortId = 1;
    int refreshId = 2;
    public Offer_received() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        new postAsync2("Retrieving Offers...",getActivity(),this,1).execute("2", "Uuid", Preferences.getString(Preferences.PHONE_NUMBER), Preferences.proxyurl + "android/receive");
        Log.i(TAG, "Offers retrieved");

        fetchUserResponses();
        mRecyclerView=(RecyclerView)getView().findViewById(R.id.offer_received_rv);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.ItemDecoration itemDecoration=new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        mLayoutManager=new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter=new RequestListAdapter(getActivity(),mItems);
        mAdapter.setOnChildItemClickedListener(this);
        mAdapter.onRestoreInstanceState(savedInstanceState);
        //mAdapter.setCus
        mRecyclerView.setAdapter(mAdapter);
    }

    public void fetchUserResponses(){
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
                req=new RequestParent(i.getRequestId(),i.getTopic(),i.getIntent_desc(),i.getDeadline_date()+" "+i.getDeadline_time(),0,null);
                mItems.add(req);
            }
            else{
                mResponses=new ArrayList<RequestChild>();
                responses=db.getUserResponses(i.getRequestId());
                for(UserResponses r : responses){
                    res=new RequestChild(r.getRequestId(),r.getTopic(),r.getService_desc(),r.getTime_to_complete(),r.getCost(),r.getSeller(),db.getSellerRank(r.getSeller()),db.getSellerRating(r.getSeller()));
                    mResponses.add(res);
                }
                Collections.sort(mResponses, new RankComparator());
                no_of_responses=mResponses.size();
                req=new RequestParent(i.getRequestId(),i.getTopic(),i.getIntent_desc(),i.getDeadline_date()+" "+i.getDeadline_time(),no_of_responses,mResponses);
                mItems.add(req);
            }
        }
        db.closeDB();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(Menu.NONE, sortId, Menu.NONE, "Sort by");
        menu.add(Menu.NONE,refreshId,Menu.NONE,"Refresh Sellers" );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return false;
        }

        if(id == sortId){
            String[] sortOptions = {"Rank","Rating","Cost","Time"};
            SortOptionDialog dialog=new SortOptionDialog();
            dialog.setData(sortOptions,this);
            dialog.show(getFragmentManager(),"SortOptions");
        }

        if(id == refreshId){
            new postAsync2("Refreshing sellers...",getActivity(),this,3).execute("0", Preferences.proxyurl + "android/refresh/");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.onSaveInstanceState(outState);
    }

    @Override
    public void onChildItemClicked(RequestChild obj) {
        UserReviewDialog dialog = new UserReviewDialog();
        dialog.setData(obj.getSeller());
        dialog.setUserReviewDialogListener(this);
        dialog.show(getFragmentManager(), "UserReview");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String seller, String review, int rating) {
        new postAsync2("Sending...",getActivity(),this,2).execute("6", "Seller", seller, "Review", review, "Stars", String.valueOf(rating), Preferences.proxyurl + "receive/review/");
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    @Override
    public void postExecute(int responseCode, String response, int tag) {
        Log.e(TAG,"Response code "+responseCode+" tag "+tag);
        if(tag==1) {
            if (responseCode == 200) {
                try {
                    db = new DatabaseHelper(getActivity());
                    JSONObject jsonobj;
                    UserResponses obj;
                    JSONObject offer;
                    SellerRank seller_obj;
                    JSONArray jarr = new JSONArray(response);
                    for (int i = 0; i < jarr.length(); i++) {
                        jsonobj = jarr.getJSONObject(i);
                        obj = new UserResponses();
                        obj.setRequestId(Integer.parseInt(jsonobj.getString("RequestId")));
                        obj.setTopic(jsonobj.getString("Topic"));
                        offer = new JSONObject(jsonobj.getString("Offer"));
                        obj.setService_desc(offer.getString("Service_Description"));
                        obj.setCost(Float.parseFloat(offer.getString("Cost")));
                        obj.setTime_to_complete(offer.getString("Time_to_Complete"));
                        obj.setSeller(jsonobj.getString("Seller"));
                        long id = db.createUserResponses(obj);
                        int noofrowsaffected = db.updatePendingRow(Integer.parseInt(jsonobj.getString("RequestId")));
                        Log.i(TAG, "Entry made with id " + id + " and no of rows affected " + noofrowsaffected);
                        seller_obj =  new SellerRank();
                        seller_obj.setName(jsonobj.getString("Seller"));
                        seller_obj.setRank(jsonobj.getDouble("Rank"));
                        seller_obj.setRating(jsonobj.getInt("Rating"));
                        Log.e(TAG, jsonobj.getString("Seller"));
                        Log.e(TAG, String.valueOf(jsonobj.getDouble("Rank")));
                        Log.e(TAG, String.valueOf(jsonobj.getInt("Rating")));
                        db.createSeller(seller_obj);
                    }
                    db.closeDB();
                    fetchUserResponses();
                    for(int i=0; i<mItems.size(); i++){
                        mAdapter.notifyParentItemChanged(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if(tag==2){
            if(responseCode == 200){
                try {
                    db =new DatabaseHelper(getActivity());
                    JSONObject jsonobj;
                    SellerRank obj;
                    JSONArray jsonArray = new JSONArray(response);
                    Log.e(TAG, response);
                    Log.e(TAG, String.valueOf(jsonArray.length()));
                    for(int i=0; i<jsonArray.length(); i++){
                        jsonobj=jsonArray.getJSONObject(i);
                        obj=new SellerRank();
                        obj.setName(jsonobj.getString("Seller"));
                        obj.setRank(jsonobj.getDouble("Rank"));
                        obj.setRating(jsonobj.getInt("Rating"));
                        Log.e(TAG, jsonobj.getString("Seller"));
                        Log.e(TAG, String.valueOf(jsonobj.getDouble("Rank")));
                        Log.e(TAG, String.valueOf(jsonobj.getInt("Rating")));
                        db.createSeller(obj);
                    }
                    db.closeDB();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if(tag==3){
            if(responseCode == 200){
                try {
                    db =new DatabaseHelper(getActivity());
                    JSONObject jsonobj;
                    SellerRank obj;
                    JSONArray jsonArray = new JSONArray(response);
                    Log.e(TAG, response);
                    Log.e(TAG, String.valueOf(jsonArray.length()));
                    for(int i=0; i<jsonArray.length(); i++){
                        jsonobj=jsonArray.getJSONObject(i);
                        obj=new SellerRank();
                        obj.setName(jsonobj.getString("Seller"));
                        obj.setRank(jsonobj.getDouble("Rank"));
                        obj.setRating(jsonobj.getInt("Rating"));
                        Log.e(TAG, jsonobj.getString("Seller"));
                        Log.e(TAG, String.valueOf(jsonobj.getDouble("Rank")));
                        Log.e(TAG, String.valueOf(jsonobj.getInt("Rating")));
                        db.createSeller(obj);
                    }
                    db.closeDB();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void sortFunction(String sortby) {
        fetchUserResponses();
        List<RequestChild> mResponses;
        int mSize;
        int i=0;
        for(RequestParent item : mItems){
            mSize = item.getNo_of_responses();
            mResponses = item.getResponses();
            if(mSize>1){
                for(RequestChild xs : mResponses){
                    Log.e(TAG,xs.getSeller());
                    Log.e(TAG, String.valueOf(xs.getRank()));
                    Log.e(TAG, String.valueOf(xs.getRating()));
                    Log.e(TAG, String.valueOf(xs.getCost()));
                }
                Toast.makeText(getContext(),"Sorting by "+sortby,Toast.LENGTH_SHORT).show();
                if(sortby.equals("Rank"))
                    Collections.sort(mResponses, new RankComparator());
                else if(sortby.equals("Rating"))
                    Collections.sort(mResponses, new RatingComparator());
                else if(sortby.equals("Cost"))
                    Collections.sort(mResponses, new CostComparator());
                else if(sortby.equals("Time"))
                    Collections.sort(mResponses, new TimeComparator());
                mAdapter.notifyParentItemChanged(i);
            }
            i++;
        }
    }
}
