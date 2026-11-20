package com.example.projectaethera.ui.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectaethera.R;

import java.util.List;

/**
 * RecyclerView adapter that shows a horizontal list of image previews from local URIs.
 * Each row loads one image with Glide and crops it to fill the slider item.
 */
public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ImageViewHolder> {
    private final List<Uri> uriList;

    /**
     * Creates the adapter with the list of image URIs to display.
     *
     * @param uriList local image URIs for the preview slider
     */
    public ViewPagerAdapter(List<Uri> uriList) {
        this.uriList = uriList;
    }

    /**
     * Inflates the preview slider layout for a new image row.
     */
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.preview_slider, parent, false);
        return new ImageViewHolder(view);
    }

    /**
     * Loads the image at this position into the row's ImageView using Glide.
     */
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri uri = uriList.get(position);
        Glide.with(holder.imageView.getContext())
                .load(uri)
                .centerCrop()
                .into(holder.imageView);
    }

    /**
     * Returns how many images are in the slider list.
     */
    @Override
    public int getItemCount() {
        return uriList.size();
    }

    /**
     * Holds the ImageView used to show one slider image.
     */
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        /**
         * Finds the slider ImageView inside the row layout.
         */
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_slider_image);
        }
    }
}
