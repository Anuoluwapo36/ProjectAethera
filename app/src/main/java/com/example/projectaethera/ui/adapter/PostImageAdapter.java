package com.example.projectaethera.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.projectaethera.R;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * RecyclerView adapter that shows post images inside a ViewPager2 slider.
 * Each page loads one image URL with Glide and notifies when the first image finishes loading.
 */
public class PostImageAdapter extends RecyclerView.Adapter<PostImageAdapter.viewholder> {

    /**
     * Called once when the first image in the slider has been bound and loaded.
     */
    public interface OnFirstImageLoadedListener {
        void onFirstImageLoaded();
    }

    private final Context context;
    private final List<String> imageUri;
    private final int targetSize;
    private OnFirstImageLoadedListener firstImageLoadedListener;
    private boolean firstImageLoadedSignaled;

    /**
     * Creates the adapter with image URL strings and a default load size of 1080px.
     *
     * @param imageUri list of image URL strings for the post
     */
    public PostImageAdapter(List<String> imageUri) {
        this.imageUri = imageUri;
        this.context = null;
        this.targetSize = 1080;
    }

    /**
     * Creates the adapter with a context and custom target size for Glide loading.
     *
     * @param context    context used for Glide requests
     * @param imageUri   list of image URL strings for the post
     * @param targetSize max width and height to load each image at
     */
    public PostImageAdapter(Context context, List<String> imageUri, int targetSize) {
        this.context = context;
        this.imageUri = imageUri;
        this.targetSize = targetSize;
    }

    /**
     * Sets a listener that runs when the first image page is loaded (used to hide shimmer).
     *
     * @param listener callback for first image load, or null to clear
     */
    public void setOnFirstImageLoadedListener(OnFirstImageLoadedListener listener) {
        this.firstImageLoadedListener = listener;
    }

    /**
     * Inflates the preview slider layout for one image page.
     */
    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.preview_slider, viewGroup, false);
        return new viewholder(view);
    }

    /**
     * Loads the image at this position with Glide and signals the first-image listener for position 0.
     */
    @Override
    public void onBindViewHolder(@NonNull viewholder viewholder, int position) {
        String uri = imageUri.get(position);
        Context loadContext = context != null ? context : viewholder.imageView.getContext();
        int size = targetSize > 0 ? targetSize : 1080;

        Glide.with(loadContext)
                .load(uri)
                .override(size, size)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        if (position == 0) {
                            signalFirstImageLoaded();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        if (position == 0) {
                            signalFirstImageLoaded();
                        }
                        return false;
                    }
                })
                .into(viewholder.imageView);
    }

    private void signalFirstImageLoaded() {
        if (firstImageLoadedSignaled || firstImageLoadedListener == null) {
            return;
        }
        firstImageLoadedSignaled = true;
        firstImageLoadedListener.onFirstImageLoaded();
    }

    /**
     * Clears the Glide request from the ImageView when the row is recycled.
     */
    @Override
    public void onViewRecycled(@NonNull viewholder holder) {
        super.onViewRecycled(holder);
        Context loadContext = context != null ? context : holder.imageView.getContext();
        Glide.with(loadContext).clear(holder.imageView);
    }

    /**
     * Returns how many images belong to this post.
     */
    @Override
    public int getItemCount() {
        return imageUri.size();
    }

    /**
     * Holds the ImageView for one post image page in the slider.
     */
    static class viewholder extends RecyclerView.ViewHolder {
        ImageView imageView;

        viewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_slider_image);
        }
    }
}
