package com.pict.metaappui.adapter;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.pict.metaappui.R;

/**
 * Created by tushar on 26/12/15.
 */
public class RequestChildViewHolder extends ChildViewHolder{

    public TextView serviceText;
    public TextView costText;
    public TextView timetocompleteText;

    public RequestChildViewHolder(View itemView){
        super(itemView);
        serviceText=(TextView)itemView.findViewById(R.id.serviceText);
        costText=(TextView)itemView.findViewById(R.id.costText);
        timetocompleteText=(TextView)itemView.findViewById(R.id.timeToCompleteText);

    }

}
