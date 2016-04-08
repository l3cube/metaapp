package com.pict.metaappui.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.pict.metaappui.modal.LogItem;
import com.pict.metaappui.util.DatabaseHelper;
import com.pict.metaappui.util.Preferences;
import com.pict.metaappui.util.postAsync2;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.List;

/**
 * Created by tushar on 16/3/16.
 */
public class FileAccessDialog extends DialogFragment{
    private String seller;
    private String filetype;
    private String uuid;
    private List<String> fileid;
    private String[] sellers;
    DatabaseHelper db;
    String TAG = "FileAccessDialog";

    public interface FileAccessDialogListener{
        void sendFiles(String seller);
    }

    FileAccessDialogListener listener;

    public void setData(String filetype,List<String> fileid,String[] sellers,FileAccessDialogListener listener){
        this.filetype = filetype;
        this.fileid = fileid;
        this.sellers = sellers;
        this.uuid = Preferences.getString(Preferences.PHONE_NUMBER);
        this.listener = listener;

    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select a seller to provide access:")
                .setItems(sellers, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            seller = sellers[which];
                            listener.sendFiles(seller);
                        }
                });
        return builder.create();
    }

}
