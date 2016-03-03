package com.pict.metaappui.adapter;

/**
 * Created by tushar on 6/11/15.
 */
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.pict.metaappui.R;
import com.pict.metaappui.app.AppController;
import com.pict.metaappui.modal.FileItem;
import java.util.List;

public class ContentListAdapter extends RecyclerView.Adapter<ContentListAdapter.ViewHolder> {

    private List<FileItem> mItems;
    public ContentListAdapter(List<FileItem> mItems) {
        super();
        this.mItems = mItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.content_list_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        FileItem item = mItems.get(i);
        viewHolder.namelabel.setText("Name : "+item.getName());
        viewHolder.locationlabel.setText("Location : "+item.getLocation());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView namelabel;
        public TextView locationlabel;
        public ViewHolder(View itemView) {
            super(itemView);
            namelabel = (TextView)itemView.findViewById(R.id.nameText);
            locationlabel = (TextView)itemView.findViewById(R.id.locationText);
        }
    }
}
