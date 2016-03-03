package com.pict.metaappui.adapter;

import android.os.Build;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.pict.metaappui.R;

/**
 * Created by tushar on 26/12/15.
 */

public class RequestParentViewHolder extends ParentViewHolder{

    public TextView topicText;
    public TextView intentText;
    public TextView deadlineText;
    public Button changeDeadlineButton;
    public Button deleteRequestButton;
    public ImageButton parentDropDownArrow;

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;
    private static final float PIVOT_VALUE = 0.5f;
    private static final long DEFAULT_ROTATE_DURATION_MS = 200;
    private static final boolean HONEYCOMB_AND_ABOVE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    /**
     * Default constructor.
     *
     * @param itemView The {@link View} being hosted in this ViewHolder
     */
    public RequestParentViewHolder(View itemView) {
        super(itemView);
        topicText=(TextView)itemView.findViewById(R.id.topicText);
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
