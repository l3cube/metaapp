package com.pict.metaappui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.pict.metaappui.R;
import com.pict.metaappui.modal.RequestChild;
import com.pict.metaappui.modal.RequestParent;

import java.util.List;


/**
 * Created by tushar on 23/12/15.
 */
public class RequestListAdapter extends ExpandableRecyclerAdapter<RequestParentViewHolder,RequestChildViewHolder> {

    private List<Object> mItems;
    private LayoutInflater mInflater;

    public RequestListAdapter(Context context, List<RequestParent> parentItemList) {
        super(parentItemList);
        mInflater=LayoutInflater.from(context);
    }


    @Override
    public RequestParentViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View view=mInflater.inflate(R.layout.list_request_parent_item,parentViewGroup,false);
        return new RequestParentViewHolder(view);
    }

    @Override
    public RequestChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View view=mInflater.inflate(R.layout.list_request_child_item,childViewGroup,false);
        return new RequestChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(RequestParentViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        RequestParent obj=(RequestParent)parentListItem;
        parentViewHolder.topicText.setText("Topic : "+obj.getTopic());
        parentViewHolder.intentText.setText("Intent Description : "+obj.getIntent_desc());
        parentViewHolder.deadlineText.setText("Deadline : "+obj.getDeadline());
    }

    @Override
    public void onBindChildViewHolder(RequestChildViewHolder childViewHolder, int position, Object childListItem) {
        RequestChild obj=(RequestChild)childListItem;
        childViewHolder.serviceText.setText("Service Description : "+obj.getService_desc());
        childViewHolder.costText.setText("Cost : "+obj.getCost());
        childViewHolder.timetocompleteText.setText("Time to Complete : "+obj.getTime_to_complete());
    }
}
