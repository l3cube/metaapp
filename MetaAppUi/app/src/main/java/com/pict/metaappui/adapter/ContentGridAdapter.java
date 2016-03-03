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
import com.pict.metaappui.modal.ContentItem;
import java.util.List;

public class ContentGridAdapter extends RecyclerView.Adapter<ContentGridAdapter.ViewHolder> {

    public interface OnItemClickedListener {
        void onItemClicked(String contentType,String pattern);
    }

    private OnItemClickedListener mItemClickedListener;
    private List<ContentItem> mItems;
    public ContentGridAdapter(List<ContentItem> mItems) {
        super();
        this.mItems = mItems;
    }

    public void setOnItemClickedListener(OnItemClickedListener l){
        mItemClickedListener=l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.content_grid_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ContentItem item = mItems.get(i);
        viewHolder.tvlabel.setText(item.getName());
        viewHolder.imgThumbnail.setImageResource(item.getImageResId());
        viewHolder.pattern = item.getPattern();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgThumbnail;
        public TextView tvlabel;
        public String pattern;
        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView)itemView.findViewById(R.id.content_img_thumbnail);
            tvlabel = (TextView)itemView.findViewById(R.id.content_tv_label);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickedListener!=null){
                        mItemClickedListener.onItemClicked(tvlabel.getText().toString(),pattern);
                    }

                }
            });
        }
    }
}
