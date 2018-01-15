package com.example.images.features.search.ui.android;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.images.R;
import com.example.images.features.search.ui.ImageSearchView;
import com.example.images.util.ui.RemoteDrawable;

import java.util.Collections;
import java.util.List;

/**
 * Adapter which displays images.
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_LOADING = 1;

    private final LayoutInflater inflater;

    @NonNull
    private List<ImageSearchView.Item> items = Collections.emptyList();

    public ImagesAdapter(LayoutInflater inflater) {
        this.inflater = inflater;

        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_IMAGE:
                return new ImageViewHolder(inflater.inflate(R.layout.list_item_image, parent, false));
            case TYPE_LOADING:
                return new LoadingViewHolder(inflater.inflate(R.layout.list_item_loading, parent, false));
            default:
                throw new IllegalStateException("Unknown view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageSearchView.Item item = items.get(position);

        if (item instanceof ImageSearchView.Item.Image) {
            ((ImageViewHolder) holder).bind(
                    (ImageSearchView.Item.Image) item
            );
        }
    }

    @Override
    public int getItemViewType(int position) {
        ImageSearchView.Item item = items.get(position);

        if (item instanceof ImageSearchView.Item.Image) {
            return TYPE_IMAGE;
        } else if (item instanceof ImageSearchView.Item.Loading) {
            return TYPE_LOADING;
        } else {
            throw new IllegalStateException("Unknown item type: " + item.getClass().getSimpleName());
        }
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Updates items in the adapter.
     */
    public void setItems(List<ImageSearchView.Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class ImageViewHolder extends ViewHolder {

        private final ImageView image;

        public ImageViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
        }

        public void bind(ImageSearchView.Item.Image item) {
            image.setImageDrawable(
                    new RemoteDrawable(item.url)
            );
        }

    }

    private static class LoadingViewHolder extends ViewHolder {

        public LoadingViewHolder(View itemView) {
            super(itemView);
        }

    }

}
