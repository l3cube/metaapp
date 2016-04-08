package com.pict.metaappui.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.pict.metaappui.R;
import com.pict.metaappui.modal.RequestChild;

/**
 * Created by tushar on 12/3/16.
 */
public class UserReviewDialog extends DialogFragment {
    private String seller;
    private String review;
    private int rating;

    private TextView sellerNameText;
    private EditText userReviewText;
    private RatingBar userRating;

    public void setData(String seller){
        this.seller=seller;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_user_review,null);
        sellerNameText = (TextView)view.findViewById(R.id.sellerNameText);
        userReviewText = (EditText)view.findViewById(R.id.userReviewText);
        userRating = (RatingBar)view.findViewById(R.id.userRating);
        sellerNameText.setText(seller);

        builder.setView(view)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        review = userReviewText.getText().toString();
                        rating = (int) userRating.getRating();
                        mListener.onDialogPositiveClick(UserReviewDialog.this, seller, review, rating);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(UserReviewDialog.this);
                    }
                });
        return builder.create();
    }


    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface UserReviewDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String seller, String review, int rating);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    UserReviewDialogListener mListener;

    public void setUserReviewDialogListener(UserReviewDialogListener listener){
        mListener=listener;
    }

}
