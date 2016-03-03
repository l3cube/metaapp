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

import com.pict.metaappui.adapter.ContentGridAdapter;
import com.pict.metaappui.modal.ContentItem;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Namespace extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ContentGridAdapter mAdapter;
    private static final String TAG="Namespace";
    private List<ContentItem> mItems=new ArrayList<ContentItem>();

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

}
