package com.pict.metaappui.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pict.metaappui.R;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.pict.metaappui.adapter.ContentGridAdapter;
import com.pict.metaappui.modal.ContentItem;
import com.pict.metaappui.modal.LogItem;
import com.pict.metaappui.modal.SellerRank;
import com.pict.metaappui.util.DatabaseHelper;
import com.pict.metaappui.util.Preferences;
import com.pict.metaappui.util.postAsync2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Namespace extends Fragment implements postAsync2.PostExecuteInterface {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ContentGridAdapter mAdapter;
    private static final String TAG="Namespace";
    private List<ContentItem> mItems=new ArrayList<ContentItem>();
    DatabaseHelper db;

    public Namespace() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_namespace, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new postAsync2("Fetching notifications...",getActivity(),this,1).execute("2", "Uuid", Preferences.getString(Preferences.PHONE_NUMBER), Preferences.proxyurl + "namespace/notification/");
        mItems.clear();
        //Extend the patterns combination
        mItems.add(new ContentItem("Photo", R.drawable.icon_photo, ".*\\.jpg$"));
        mItems.add(new ContentItem("Video", R.drawable.icon_video, ".*\\.mp4$"));
        mItems.add(new ContentItem("Text Files", R.drawable.icon_files, ".*\\.txt$"));
        mItems.add(new ContentItem("Contacts", R.drawable.icon_contacts, "pat"));
        mItems.add(new ContentItem("Messages",R.drawable.icon_messages,"pat"));


        mRecyclerView=(RecyclerView)getView().findViewById(R.id.content_type_rv);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager=new GridLayoutManager(getActivity(),2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter=new ContentGridAdapter(mItems);
        mAdapter.setOnItemClickedListener(new ContentGridAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(String contentType, String pattern) {
                Log.i(TAG, "Content Type: " + contentType + "Pattern: " + pattern);
                Intent intent = new Intent(getContext(), Namespace_content.class);
                intent.putExtra("ContentType", contentType);
                intent.putExtra("Pattern", pattern);
                startActivity(intent);
            }
        });
        //mAdapter.setCus
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void postExecute(int responseCode, String response, int tag) {
        if(tag==1){
            if(responseCode == 200){
                try {
                    db = new DatabaseHelper(getActivity());
                    LogItem obj=new LogItem();
                    SellerRank seller_obj;
                    JSONObject jsonObject;
                    JSONArray jsonArray=new JSONArray(response);
                    for(int i=0; i<jsonArray.length(); i++){
                        jsonObject = jsonArray.getJSONObject(i);
                        String seller = jsonObject.getString("Seller");
                        String filetype = jsonObject.getString("FileType");

                        obj.setName(seller);
                        obj.setLog(seller + " wants to access files of type " + filetype);
                        obj.setTimestamp(Calendar.getInstance().getTime().toString());
                        long id = db.createLog(obj);
                        Toast.makeText(getContext(),seller+" wants to access files of type "+filetype,Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "record created with id " + id);
                        seller_obj =  new SellerRank();
                        seller_obj.setName(jsonObject.getString("Seller"));
                        seller_obj.setRank(jsonObject.getDouble("Rank"));
                        seller_obj.setRating(jsonObject.getInt("Rating"));
                        db.createSeller(seller_obj);
                    }
                    db.closeDB();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
