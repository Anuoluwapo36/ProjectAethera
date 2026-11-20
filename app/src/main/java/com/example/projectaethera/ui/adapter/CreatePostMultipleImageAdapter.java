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

import com.facebook.shimmer.Shimmer;

import com.facebook.shimmer.ShimmerFrameLayout;



import java.util.List;



/**

 * RecyclerView adapter that previews up to five images picked for a new post.

 * If more than five images were chosen, the fifth cell shows a "+N" overlay for the rest.

 */

public class CreatePostMultipleImageAdapter extends RecyclerView.Adapter<CreatePostMultipleImageAdapter.ViewHolder> {



    private List<Uri> imageList;



    /**

     * Creates the adapter with the selected image URIs for the create-post screen.

     *

     * @param imageList local URIs of images the user picked

     */

    public CreatePostMultipleImageAdapter(List<Uri> imageList) {

        this.imageList = imageList;

    }



    /**

     * Inflates one thumbnail cell in the create-post image row.

     */

    @NonNull

    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.create_post_multipel_view_fragment,viewGroup,false);



        return new ViewHolder(view);

    }



    /**

     * Loads each thumbnail with shimmer; on the fifth slot shows "+extra" when there are more than five images.

     */

    @Override

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

         Uri currentImage = imageList.get(position);

         viewHolder.extraText.setVisibility(View.GONE);



        viewHolder.shimmer.startShimmer();

        viewHolder.shimmer.setVisibility(View.VISIBLE);

        viewHolder.imageView.setVisibility(View.INVISIBLE);



        //glide load image

        Glide.with(viewHolder.itemView.getContext())

                .load(currentImage)

                .centerCrop()

                .listener(new RequestListener<Drawable>() {

                    @Override

                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {

                        viewHolder.shimmer.stopShimmer();

                        viewHolder.shimmer.setVisibility(View.GONE);

                        viewHolder.imageView.setVisibility(View.VISIBLE);

                        return false;

                    }



                    @Override

                    public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {

                        viewHolder.shimmer.stopShimmer();

                        viewHolder.shimmer.setVisibility(View.GONE);

                        viewHolder.imageView.setVisibility(View.VISIBLE);

                        if (position == 4 && imageList.size() > 5) {

                            int extra = imageList.size() - 5;

                            viewHolder.extraText.setVisibility(View.VISIBLE);

                            viewHolder.extraText.setText("+" + extra);

                            viewHolder.extraText.setBackgroundColor(0xCC000000);

                        } else {

                            viewHolder.extraText.setVisibility(View.GONE);

                        }



                        return false;

                    }

                })

                .into(viewHolder.imageView);



    }



    /**

     * Shows at most five preview thumbnails even if more images were selected.

     */

    @Override

    public int getItemCount() {

        return Math.min(imageList.size(), 5);

    }



    /**

     * Holds image, shimmer, and optional "+N more" text for one preview cell.

     */

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        ShimmerFrameLayout shimmer;

        TextView extraText;



        /**

         * Finds views inside one create-post preview item layout.

         */

        public ViewHolder(@NonNull View itemView) {

            super(itemView);



            imageView = itemView.findViewById(R.id.multi_iv_image);

            shimmer=itemView.findViewById(R.id.shimmer_layout);

            extraText = itemView.findViewById(R.id.extra_text);

        }

    }

}

