package com.example.projectaethera.ui.adapter;



import android.graphics.drawable.Drawable;

import android.net.Uri;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.TextView;



import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import androidx.recyclerview.widget.RecyclerView;



import com.bumptech.glide.Glide;

import com.bumptech.glide.load.DataSource;

import com.bumptech.glide.load.engine.GlideException;

import com.bumptech.glide.request.RequestListener;

import com.bumptech.glide.request.target.Target;

import com.example.projectaethera.R;

import com.facebook.shimmer.ShimmerFrameLayout;



import java.util.ArrayList;

import java.util.List;



/**

 * RecyclerView adapter that shows device gallery images in a grid.

 * Supports single-tap preview, long-press to enter multi-select, and numbered selection badges.

 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {



    /**

     * Called for a normal tap or when the user long-presses to start multi-select.

     */

    public interface GalleryListener {

        void onSingleTap(Uri uri);



        void onLongPress();

    }



    /**

     * Called whenever the number of selected images changes in multi-select mode.

     */

    public interface SelectionChangeListener {

        void onSelectionChanged(int count);

    }



    private GalleryListener listener;

    private SelectionChangeListener selectionChangeListener;

    private final List<Uri> imageList;

    private final List<Integer> selectedPosition = new ArrayList<>();



    private boolean isMultiSelectMode = false;



    /**

     * Creates the adapter with the list of gallery image URIs to show.

     *

     * @param imageList local content URIs from the device gallery

     */

    public GalleryAdapter(List<Uri> imageList) {

        this.imageList = imageList;

    }



    /**

     * Sets the listener for single tap and long press gestures.

     *

     * @param listener gallery gesture callback

     */

    public void setListener(GalleryListener listener) {

        this.listener = listener;

    }



    /**

     * Sets the listener that receives updates when selection count changes.

     *

     * @param selectionChangeListener callback for selection count changes

     */

    public void setSelectionChangeListener(SelectionChangeListener selectionChangeListener) {

        this.selectionChangeListener = selectionChangeListener;

    }



    /**

     * Inflates one gallery thumbnail cell.

     */

    @NonNull

    @Override

    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_item, viewGroup, false);

        return new MyViewHolder(view);

    }



    /**

     * Loads the image with shimmer, shows selection overlay in multi-select mode,

     * and handles tap to select or open and long-press to enter multi-select.

     */

    @Override

    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Uri currentImage = imageList.get(position);



        holder.shimmerLayout.startShimmer();

        holder.shimmerLayout.setVisibility(View.VISIBLE);

        holder.galleryImg.setVisibility(View.INVISIBLE);



        Glide.with(holder.itemView.getContext())

                .load(currentImage)

                .centerCrop()

                .listener(new RequestListener<Drawable>() {

                    @Override

                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        holder.shimmerLayout.stopShimmer();

                        holder.shimmerLayout.setVisibility(View.GONE);

                        holder.galleryImg.setVisibility(View.VISIBLE);

                        return false;

                    }



                    @Override

                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        holder.shimmerLayout.stopShimmer();

                        holder.shimmerLayout.setVisibility(View.GONE);

                        holder.galleryImg.setVisibility(View.VISIBLE);

                        return false;

                    }

                })

                .into(holder.galleryImg);



        if (isMultiSelectMode && selectedPosition.contains(position)) {

            holder.selectedOverlay.setVisibility(View.VISIBLE);

            holder.selectionBadge.setVisibility(View.VISIBLE);

            holder.selectionBadge.setText(String.valueOf(selectedPosition.indexOf(position) + 1));

        } else {

            holder.selectedOverlay.setVisibility(View.GONE);

            holder.selectionBadge.setVisibility(View.GONE);

        }



        holder.itemView.setOnClickListener(v -> {

            int currentPick = holder.getBindingAdapterPosition();

            if (currentPick == RecyclerView.NO_POSITION) {

                return;

            }



            if (isMultiSelectMode) {

                if (selectedPosition.contains(currentPick)) {

                    selectedPosition.remove(Integer.valueOf(currentPick));

                } else {

                    selectedPosition.add(currentPick);

                }

                notifyDataSetChanged();

                notifySelectionChanged();

            } else if (listener != null) {

                listener.onSingleTap(currentImage);

            }

        });



        holder.itemView.setOnLongClickListener(v -> {

            if (!isMultiSelectMode && listener != null) {

                listener.onLongPress();

            }

            return true;

        });

    }



    private void notifySelectionChanged() {

        if (selectionChangeListener != null) {

            selectionChangeListener.onSelectionChanged(selectedPosition.size());

        }

    }



    /**

     * Returns how many gallery images are shown.

     */

    @Override

    public int getItemCount() {

        return imageList.size();

    }



    /**

     * Holds thumbnail, selection badge, overlay, and shimmer for one gallery cell.

     */

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView galleryImg;

        TextView selectionBadge;

        View selectedOverlay;

        ShimmerFrameLayout shimmerLayout;



        MyViewHolder(@NonNull View itemView) {

            super(itemView);

            galleryImg = itemView.findViewById(R.id.gallery_iv_image);

            selectionBadge = itemView.findViewById(R.id.selection_badge);

            selectedOverlay = itemView.findViewById(R.id.selected_overlay);

            shimmerLayout = itemView.findViewById(R.id.shimmer_layout);

        }

    }



    /**

     * Returns the URIs of all currently selected images in pick order.

     */

    public List<Uri> getSelectedImage() {

        List<Uri> selectedUris = new ArrayList<>();

        for (int position : selectedPosition) {

            selectedUris.add(imageList.get(position));

        }

        return selectedUris;

    }



    /**

     * Turns multi-select mode on or off and clears selection when turning off.

     *

     * @param mode true to allow picking multiple images

     */

    public void setMultiSelectMode(boolean mode) {

        isMultiSelectMode = mode;

        if (!mode) {

            selectedPosition.clear();

            notifySelectionChanged();

        }

        notifyDataSetChanged();

    }

}

