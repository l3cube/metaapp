package com.pict.metaappui.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.pict.metaappui.R;
import com.pict.metaappui.modal.RequestChild;
import com.pict.metaappui.modal.RequestParent;

import java.util.List;


/**
 * Created by tushar on 23/12/15.
 */
public class RequestListAdapter extends ExpandableRecyclerAdapter<RequestListAdapter.RequestParentViewHolder,RequestListAdapter.RequestChildViewHolder> {

    private List<Object> mItems;
    private LayoutInflater mInflater;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;

    public interface OnChildItemClickedListener{
        void onChildItemClicked(RequestChild obj);
    }

    private OnChildItemClickedListener onChildItemClickedListener;

    public void setOnChildItemClickedListener(OnChildItemClickedListener listener){
        onChildItemClickedListener=listener;
    }

    public RequestListAdapter(Context context, List<RequestParent> parentItemList) {
        super(parentItemList);
        mInflater=LayoutInflater.from(context);
        this.mDrawableBuilder = TextDrawable.builder().round();
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
        TextDrawable drawable = mDrawableBuilder.build(String.valueOf(obj.getTopic().charAt(0)), mColorGenerator.getColor(obj.getTopic().charAt(0)));
        parentViewHolder.topicImageView.setImageDrawable(drawable);
        parentViewHolder.intentText.setText("Intent Description : "+obj.getIntent_desc());
        parentViewHolder.deadlineText.setText("Deadline : "+obj.getDeadline());
    }

    @Override
    public void onBindChildViewHolder(RequestChildViewHolder childViewHolder, int position, Object childListItem) {
        RequestChild obj=(RequestChild)childListItem;
        TextDrawable drawable = mDrawableBuilder.build(String.valueOf(obj.getSeller().charAt(0)), mColorGenerator.getColor(obj.getSeller().charAt(0)));
        childViewHolder.child = obj;
        childViewHolder.sellerImageView.setImageDrawable(drawable);
        childViewHolder.serviceText.setText("Service Description : "+obj.getService_desc());
        childViewHolder.costText.setText("Cost : "+obj.getCost());
        childViewHolder.timetocompleteText.setText("Time to Complete : "+obj.getTime_to_complete());
    }

    public class RequestParentViewHolder extends ParentViewHolder {

        public ImageView topicImageView;
        public TextView intentText;
        public TextView deadlineText;
        public Button changeDeadlineButton;
        public Button deleteRequestButton;
        public ImageButton parentDropDownArrow;

        private final float INITIAL_POSITION = 0.0f;
        private final float ROTATED_POSITION = 180f;
        private final float PIVOT_VALUE = 0.5f;
        private final long DEFAULT_ROTATE_DURATION_MS = 200;
        private final boolean HONEYCOMB_AND_ABOVE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

        /**
         * Default constructor.
         *
         * @param itemView The {@link View} being hosted in this ViewHolder
         */
        public RequestParentViewHolder(View itemView) {
            super(itemView);
            topicImageView=(ImageView)itemView.findViewById(R.id.topicImageView);
            intentText=(TextView)itemView.findViewById(R.id.intentText);
            deadlineText=(TextView)itemView.findViewById(R.id.deadlineText);
            changeDeadlineButton=(Button)itemView.findViewById(R.id.changeDeadlineButton);
            deleteRequestButton=(Button)itemView.findViewById(R.id.deleteRequestButton);
            parentDropDownArrow=(ImageButton)itemView.findViewById(R.id.parent_list_item_expand_arrow);
            parentDropDownArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isExpanded()){
                        collapseView();
                    }
                    else{
                        expandView();
                    }
                }
            });
        }

        @Override
        public boolean shouldItemViewClickToggleExpansion() {
            return super.shouldItemViewClickToggleExpansion();
        }

        @Override
        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);
            if (!HONEYCOMB_AND_ABOVE) {
                return;
            }

            if (expanded) {
                parentDropDownArrow.setRotation(ROTATED_POSITION);
            } else {
                parentDropDownArrow.setRotation(INITIAL_POSITION);
            }
        }

        @Override
        public void onExpansionToggled(boolean expanded) {
            super.onExpansionToggled(expanded);
            if (!HONEYCOMB_AND_ABOVE) {
                return;
            }

            RotateAnimation rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, PIVOT_VALUE,
                    RotateAnimation.RELATIVE_TO_SELF, PIVOT_VALUE);
            rotateAnimation.setDuration(DEFAULT_ROTATE_DURATION_MS);
            rotateAnimation.setFillAfter(true);
            parentDropDownArrow.startAnimation(rotateAnimation);
        }
    }


    public class RequestChildViewHolder extends ChildViewHolder {

        public ImageView sellerImageView;
        public TextView serviceText;
        public TextView costText;
        public TextView timetocompleteText;
        public RequestChild child;
        public RequestChildViewHolder(View itemView){
            super(itemView);

            sellerImageView=(ImageView)itemView.findViewById(R.id.sellerImageView);
            serviceText=(TextView)itemView.findViewById(R.id.serviceText);
            costText=(TextView)itemView.findViewById(R.id.costText);
            timetocompleteText=(TextView)itemView.findViewById(R.id.timeToCompleteText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onChildItemClickedListener.onChildItemClicked(child);
                }
            });
        }

    }

}
