package com.pict.metaappui.adapter;

/**
 * Created by tushar on 6/11/15.
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.pict.metaappui.R;
import com.pict.metaappui.app.AppController;
import com.pict.metaappui.modal.CategoryItem;
import com.pict.metaappui.ui.Level1_Categories;

import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    public interface OnItemClickedListener {
        void onItemClicked(String itemSelected,boolean more);
    }

    private OnItemClickedListener mItemClickedListener;
    private List<CategoryItem> mItems;
    ImageLoader imageLoader= AppController.getInstance().getImageLoader();
    public GridAdapter(List<CategoryItem> mItems) {
        super();
        this.mItems = mItems;
    }

    public void setOnItemClickedListener(OnItemClickedListener l){
        mItemClickedListener=l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if(imageLoader==null)
            imageLoader=AppController.getInstance().getImageLoader();

        CategoryItem item = mItems.get(i);
        viewHolder.tvlabel.setText(item.getName());
        viewHolder.imgThumbnail.setImageUrl(item.getImageUrl(),imageLoader);
        viewHolder.more=item.getMore();
    }

    @Override
    public int getItemCount() {

        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public NetworkImageView imgThumbnail;
        public TextView tvlabel;
        public boolean more;
        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (NetworkImageView)itemView.findViewById(R.id.img_thumbnail);
            tvlabel = (TextView)itemView.findViewById(R.id.tv_label);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickedListener!=null){
                        String itemSelected=tvlabel.getText().toString();
                        mItemClickedListener.onItemClicked(itemSelected,more);
                    }

                }
            });
        }
    }
}
