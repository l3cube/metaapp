package com.pict.metaappui.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.pict.metaappui.R;
import com.pict.metaappui.modal.FileItem;
import com.pict.metaappui.util.DatabaseHelper;
import com.pict.metaappui.util.DividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;


public class Namespace_content extends AppCompatActivity implements View.OnClickListener {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ContentListAdapter mAdapter;
    private static final String TAG="Namespace_content";
    private List<FileItem> mItems=new ArrayList<FileItem>();
    private String contentType;
    private String pattern;
    private DatabaseHelper db;
    Intent intent;


    FloatingActionButton mFAB;
    FloatingActionMenu mFABMenu;
    private static final String TAG_ADD_FILE = "AddFile";
    private static final String TAG_REMOVE_FILE = "RemoveFile";

    //Multiselector for Recyclerview
    private MultiSelector mMultiSelector = new MultiSelector();
    private ModalMultiSelectorCallback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            super.onCreateActionMode(actionMode, menu);
            getMenuInflater().inflate(R.menu.namespace_content_context, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId()==  R.id.menu_item_delete_file){
                // Need to finish the action mode before doing the following,
                // not after. No idea why, but it crashes.
                actionMode.finish();
                db = new DatabaseHelper(getApplicationContext());

                for (int i = mItems.size(); i >= 0; i--) {
                    if (mMultiSelector.isSelected(i, 0)) {
                        FileItem item = mItems.get(i);
                        boolean ret = db.deleteFile(item.getLocation());
                        Log.i(TAG,"Return value from delete "+ ret);
                        mItems.remove(item);
                        mRecyclerView.getAdapter().notifyItemRemoved(i);
                    }
                }

                mMultiSelector.clearSelections();
                return true;

            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            Log.i(TAG, "Inside Action item click");
            super.onDestroyActionMode(actionMode);
            for (int i = mItems.size(); i >= 0; i--) {
                if (mMultiSelector.isSelected(i, 0)) {
                    mItems.get(i).setIsChecked(false);
                    mRecyclerView.getAdapter().notifyItemChanged(i);
                }
            }
            // Plan1: isChecked field inside Fileitem and change it here and notifyofdatachanged
            // Plan2: No plan yet
            // Plan1 worked :)
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_namespace_content);
        mMultiSelector.setSelectable(false);

        //Add FAB
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.ic_action_new);

        mFAB = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .setBackgroundDrawable(R.drawable.selector_button_red)
                .build();

        //define the icons for the sub action buttons
        ImageView iconAddFile = new ImageView(this);
        iconAddFile.setImageResource(R.drawable.ic_create_file);
        ImageView iconRemoveFile = new ImageView(this);
        iconRemoveFile.setImageResource(R.drawable.ic_remove_file);


        //set the background for all the sub buttons
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_sub_button_gray));


        //build the sub buttons
        SubActionButton buttonAddFile = itemBuilder.setContentView(iconAddFile).build();
        SubActionButton buttonRemoveFile = itemBuilder.setContentView(iconRemoveFile).build();
        buttonAddFile.setTag(TAG_ADD_FILE);
        buttonRemoveFile.setTag(TAG_REMOVE_FILE);

        //Add actions to buttons
        buttonAddFile.setOnClickListener(this);
        buttonRemoveFile.setOnClickListener(this);

        //add the sub buttons to the main floating action button
        mFABMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(buttonAddFile)
                .addSubActionView(buttonRemoveFile)
                .attachTo(mFAB)
                .build();

        //Build RecyclerView
        intent = getIntent();
        contentType = intent.getStringExtra("ContentType");
        pattern = intent.getStringExtra("Pattern");
        Log.i(TAG,"ContentType: "+contentType+" Pattern: "+pattern);
        mItems.clear();
        //Do db fetch and store in mItems
        db=new DatabaseHelper(this);
        mItems = db.getAllUserFiles(contentType);
        db.closeDB();
        Log.i(TAG, "List with items " + mItems.size());
        Toast.makeText(this, "Content Type : " + contentType, Toast.LENGTH_LONG).show();

        mRecyclerView=(RecyclerView)findViewById(R.id.content_rv);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.ItemDecoration itemDecoration=new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        mLayoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter=new ContentListAdapter();
        //mAdapter.setCus
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu,v,menuInfo);
        getMenuInflater().inflate(R.menu.namespace_content_context,menu);
    }

    @Override
    public void onClick(View v) {
        if(v.getTag().equals(TAG_ADD_FILE)){
            Toast.makeText(getApplicationContext(), "Add File", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.ARG_FILE_FILTER, Pattern.compile(pattern));
            intent.putExtra(FilePickerActivity.ARG_DIRECTORIES_FILTER, false);
            startActivityForResult(intent, 1);
        }
        else if(v.getTag().equals(TAG_REMOVE_FILE)){
            Toast.makeText(getApplicationContext(),"Remove File", Toast.LENGTH_SHORT).show();
            //Implement removal of files
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            Log.i(TAG,"Result from filechooser: "+filePath);
            File f = new File(filePath);
            FileItem obj=new FileItem();
            obj.setName(f.getName());
            obj.setLocation(f.getAbsolutePath());
            db = new DatabaseHelper(this);
            long id = db.createUserFiles(obj, contentType);
            if(id==-1)
            {
                Toast.makeText(getApplicationContext(),"File already exists", Toast.LENGTH_SHORT).show();
                db.closeDB();
                return;
            }
            mItems = db.getAllUserFiles(contentType);
            mAdapter.swap(mItems);
            db.closeDB();
            Toast.makeText(getApplicationContext(),"File Added Successfully with id: "+id, Toast.LENGTH_SHORT).show();

        }
    }

    public void onItemClicked(String fileLocation) {
        Log.i(TAG,"Opening: "+fileLocation);
        File file = new File(fileLocation);
        String extension = extensionFromName(file.getName());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Intent viewFileIntent = new Intent();
        viewFileIntent.setAction(Intent.ACTION_VIEW);
        viewFileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        viewFileIntent.setDataAndType(Uri.fromFile(file),mimeType);

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(viewFileIntent, 0);
        boolean isIntentSafe = activities.size() > 0;
        if(isIntentSafe){
            Intent chooser = Intent.createChooser(viewFileIntent,"Open file with:");
            startActivity(chooser);
        }
        else {
            Toast.makeText(getApplicationContext(),"No App to open file", Toast.LENGTH_SHORT).show();;
        }
    }

    public static String extensionFromName(String fileName) {
        int dotPosition = fileName.lastIndexOf('.');

        // If extension not present or empty
        if (dotPosition == -1 || dotPosition == fileName.length() - 1) {
            return "";
        } else {
            return fileName.substring(dotPosition + 1).toLowerCase(Locale.getDefault());
        }
    }

    public class ContentListAdapter extends RecyclerView.Adapter<ContentListAdapter.ViewHolder> {

        private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
        private TextDrawable.IBuilder mDrawableBuilder;

        public ContentListAdapter() {
            super();
            // The method returns a MaterialDrawable, but as it is private to the builder you'll have to store it as a regular Drawable ;)
            this.mDrawableBuilder = TextDrawable.builder()
                    .round();
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.content_list_item, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            final FileItem item = mItems.get(i);
            viewHolder.namelabel.setText(item.getName());
            viewHolder.location = item.getLocation();
            updateCheckedState(viewHolder,item.isChecked());
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when the image is clicked, update the selected state
                    if(!mMultiSelector.isSelectable()) {
                        startSupportActionMode(mDeleteMode);
                        mMultiSelector.setSelectable(true);
                    }
                    item.setIsChecked(!item.isChecked());
                    updateCheckedState(viewHolder,item.isChecked());
                }
            });
        }

        private void updateCheckedState(ViewHolder holder,boolean state) {
            if (state) {
                holder.imageView.setImageDrawable(mDrawableBuilder.build(" ", 0xff616161));
                holder.checkIcon.setVisibility(View.VISIBLE);
                mMultiSelector.setSelected(holder, true);
            }
            else {
                TextDrawable drawable = mDrawableBuilder.build(String.valueOf(holder.namelabel.getText().charAt(0)), mColorGenerator.getColor(holder.namelabel.getText().charAt(0)));
                holder.imageView.setImageDrawable(drawable);
                holder.checkIcon.setVisibility(View.GONE);
                mMultiSelector.setSelected(holder,false);
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public void swap(List<FileItem> mItems){
            notifyDataSetChanged();
        }

        public class ViewHolder extends SwappingHolder {
            private TextView namelabel;
            private ImageView imageView;
            private ImageView checkIcon;
            private String location;
            private View view;

            public ViewHolder(View itemView) {
                super(itemView, mMultiSelector);
                view = itemView;
                namelabel = (TextView)itemView.findViewById(R.id.textView);
                imageView = (ImageView)itemView.findViewById(R.id.imageView);
                checkIcon = (ImageView)itemView.findViewById(R.id.check_icon);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClicked(location);
                    }
                });
            }


        }
    }
}
