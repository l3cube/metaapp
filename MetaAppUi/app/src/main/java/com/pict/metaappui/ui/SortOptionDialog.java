package com.pict.metaappui.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.pict.metaappui.util.Preferences;
import com.pict.metaappui.util.postAsync2;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by tushar on 16/3/16.
 */
public class SortOptionDialog extends DialogFragment{
    private String selectedOption;
    private String[] sortOption;

    public interface sortOptionListener {
        public void sortFunction(String sortby);
    }

    private sortOptionListener mSortOptionListener;

    public void setData(String[] sortOption, sortOptionListener mSortOptionListener){
        this.sortOption = sortOption;
        this.mSortOptionListener = mSortOptionListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sort By:")
                .setItems(sortOption, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedOption = sortOption[which];
                        mSortOptionListener.sortFunction(selectedOption);
                    }
                });
        return builder.create();
    }


}
