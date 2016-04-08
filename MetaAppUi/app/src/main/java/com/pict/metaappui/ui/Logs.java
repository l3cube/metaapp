package com.pict.metaappui.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.pict.metaappui.R;
import com.pict.metaappui.modal.LogItem;
import com.pict.metaappui.util.DatabaseHelper;
import com.pict.metaappui.util.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Logs extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    LogListAdapter mAdapter;
    private static final String TAG="Logs";
    private List<LogItem> mItems=new ArrayList<LogItem>();
    private DatabaseHelper db;


    public Logs() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logs, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mItems.clear();
        //Do db fetch and store in mItems
        db=new DatabaseHelper(getActivity());
        mItems=db.getLogs();
        db.closeDB();
        Log.i(TAG, "List with items " + mItems.size());

        mRecyclerView=(RecyclerView)getView().findViewById(R.id.logs_rv);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.ItemDecoration itemDecoration=new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        mLayoutManager=new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter=new LogListAdapter();
        //mAdapter.setCus
        mRecyclerView.setAdapter(mAdapter);
    }

    public class LogListAdapter extends RecyclerView.Adapter<LogListAdapter.ViewHolder> {

        private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
        private TextDrawable.IBuilder mDrawableBuilder;

        public LogListAdapter() {
            super();
            // The method returns a MaterialDrawable, but as it is private to the builder you'll have to store it as a regular Drawable ;)
            this.mDrawableBuilder = TextDrawable.builder()
                    .round();
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.logs_list_item, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            LogItem item = mItems.get(i);
            TextDrawable drawable = mDrawableBuilder.build(String.valueOf(item.getName().charAt(0)), mColorGenerator.getColor(item.getName().charAt(0)));
            viewHolder.imageView.setImageDrawable(drawable);
            viewHolder.namelabel.setText(item.getLog());
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView namelabel;
            private ImageView imageView;
            private View view;

            public ViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                namelabel = (TextView)itemView.findViewById(R.id.textView);
                imageView = (ImageView)itemView.findViewById(R.id.imageView);

            }

        }
    }
}
