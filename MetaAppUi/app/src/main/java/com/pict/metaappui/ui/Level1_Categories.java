package com.pict.metaappui.ui;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.pict.metaappui.R;
import com.pict.metaappui.adapter.GridAdapter;
import com.pict.metaappui.app.AppController;
import com.pict.metaappui.modal.CategoryItem;
import com.pict.metaappui.util.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Level1_Categories extends Fragment {

    String level;
    String category;
    String endpoint;
    String tlc;//Top level category
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    GridAdapter mAdapter;
    private static final String TAG="Level1_Categories";
    private static final String url= Preferences.url;
    private List<CategoryItem> mItems=new ArrayList<CategoryItem>();
    private ProgressDialog progressDialog;

    public Level1_Categories() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        level=Integer.toString(getArguments().getInt("Level"));
        category=getArguments().getString("Category");

        if(getArguments().getInt("Level")==2){
            tlc=category;
        }
        else{
            tlc=getArguments().getString("TLC");
        }

        endpoint="level"+level+"_"+category;
        View fragmentView=inflater.inflate(R.layout.fragment_level1_categories, container, false);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Calling the RecyclerView
        mItems.clear();
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.level1_category);
        mRecyclerView.setHasFixedSize(true);

        // The number of Columns
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new GridAdapter(mItems);
        mAdapter.setOnItemClickedListener(new GridAdapter.OnItemClickedListener() {
            @Override
            /*
                Add logic to stop at the leaves and call another fragment...
             */
            public void onItemClicked(String itemSelected,boolean more) {
                if(more) {
                    int templevel = Integer.parseInt(level);
                    templevel += 1;
                    Bundle bundle = new Bundle();
                    bundle.putInt("Level", templevel);
                    bundle.putString("Category", itemSelected);
                    bundle.putString("TLC", tlc);
                    Level1_Categories fragment = new Level1_Categories();
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, fragment);
                    transaction.addToBackStack(null);
                    Toast.makeText(getContext(), itemSelected, Toast.LENGTH_SHORT).show();
                    transaction.commit();
                }
                else{
                    if(itemSelected.equals("Taxi")){
                        Bundle bundle = new Bundle();
                        bundle.putString("Category", itemSelected);
                        bundle.putString("TLC", "Taxi");
                        TaxiLocation fragment = new TaxiLocation();
                        fragment.setArguments(bundle);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    else {
                        Bundle bundle = new Bundle();
                        bundle.putString("Category", itemSelected);
                        bundle.putString("TLC", tlc);
                        Intent_publish fragment = new Intent_publish();
                        fragment.setArguments(bundle);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        // Inflate the layout for this fragment

        progressDialog=new ProgressDialog(getContext());
        // Showing progress dialog before making http request
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // Creating volley request obj
        JsonArrayRequest itemsReq = new JsonArrayRequest(url+"static/json/"+endpoint+".json",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                CategoryItem item=new CategoryItem();
                                item.setName(obj.getString("title"));
                                item.setImageUrl(url+obj.getString("image"));
                                item.setMore(obj.getBoolean("more"));
                                mItems.add(item);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });
        itemsReq.setShouldCache(false);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(itemsReq);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
