package com.pict.metaappui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.pict.metaappui.R;
import com.pict.metaappui.modal.FulfilledRequest;
import com.pict.metaappui.modal.PendingRequest;

import java.util.List;


/**
 * Created by tushar on 23/12/15.
 */
public class RequestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> mItems;
    private final int PENDING=0, FULFILLED=1;

    public RequestListAdapter(List<Object> mItems){
        super();
        this.mItems=mItems;
    }

    @Override
    public int getItemViewType(int position) {
        if(mItems.get(position) instanceof PendingRequest){
            return PENDING;
        }
        else if(mItems.get(position) instanceof FulfilledRequest){
            return FULFILLED;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        if(viewType==PENDING){
            View v1=inflater.inflate(R.layout.list_pendingrequest_item,parent,false);
            viewHolder=new ViewHolder1(v1);
        }
        else{
            View v2=inflater.inflate(R.layout.list_fulfilledrequest_item,parent,false);
            viewHolder=new ViewHolder2(v2);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType()==PENDING){
            ViewHolder1 vh1=(ViewHolder1)holder;
            PendingRequest obj=(PendingRequest)mItems.get(position);
            vh1.topicText.setText("Topic : "+obj.getTopic());
            vh1.intentText.setText("Intent Description : "+obj.getIntent_desc());
            vh1.deadlineText.setText("Deadline : "+obj.getDeadline());
        }
        else{
            ViewHolder2 vh2=(ViewHolder2)holder;
            FulfilledRequest obj=(FulfilledRequest)mItems.get(position);
            vh2.topicText.setText("Topic : "+obj.getTopic());
            vh2.intentText.setText("Intent Description : "+obj.getIntent_desc());
            vh2.deadlineText.setText("Deadline : "+obj.getDeadline());
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder1 extends RecyclerView.ViewHolder{
        public TextView topicText;
        public TextView intentText;
        public TextView deadlineText;
        public Button extendDeadlineButton;
        public Button cancelRequestButton;

        public ViewHolder1(View itemView) {
            super(itemView);
            topicText=(TextView)itemView.findViewById(R.id.topicText1);
            intentText=(TextView)itemView.findViewById(R.id.intentText1);
            deadlineText=(TextView)itemView.findViewById(R.id.deadlineText1);
            extendDeadlineButton=(Button)itemView.findViewById(R.id.extendDeadlineButton);
            cancelRequestButton=(Button)itemView.findViewById(R.id.cancelRequestButton);
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {
        public TextView topicText;
        public TextView intentText;
        public TextView deadlineText;
        public Button viewResponsesButton;
        public Button deleteRequestButton;

        public ViewHolder2(View itemView) {
            super(itemView);
            topicText = (TextView) itemView.findViewById(R.id.topicText2);
            intentText = (TextView) itemView.findViewById(R.id.intentText2);
            deadlineText = (TextView) itemView.findViewById(R.id.deadlineText2);
            viewResponsesButton = (Button) itemView.findViewById(R.id.viewResponsesButton);
            deleteRequestButton = (Button) itemView.findViewById(R.id.deleteRequestButton);
        }
    }


}
