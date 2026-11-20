package com.example.projectaethera.ui.adapter;



import android.graphics.drawable.Drawable;

import android.net.Uri;

import android.os.Bundle;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.TextView;



import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;

import androidx.recyclerview.widget.RecyclerView;



import com.bumptech.glide.Glide;

import com.bumptech.glide.load.DataSource;

import com.bumptech.glide.load.engine.GlideException;

import com.bumptech.glide.request.RequestListener;

import com.bumptech.glide.request.target.Target;

import com.example.projectaethera.R;

import com.example.projectaethera.model.entity.Post;

import com.example.projectaethera.model.remote.UserPostResponse;

import com.example.projectaethera.ui.profile.PostDetailsViewFragment;

import com.example.projectaethera.utils.ImageUrlUtils;

import com.facebook.shimmer.ShimmerFrameLayout;



import java.io.Serializable;

import java.util.ArrayList;

import java.util.Collections;

import java.util.List;



/**

 * RecyclerView adapter that shows a grid of the user's posts on their profile.

 * Each cell shows the first image of a post with a shimmer while Glide loads it.

 */

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private List<Post> postList;





    /**

     * Called when the user taps a post thumbnail in the profile grid.

     */

    public interface  OnPostClickListener{

        void  onPostClick(Post post);

    }

    private OnPostClickListener onPostClickListener;





    /**

     * Creates an empty profile adapter; call setUserPostList to fill it with posts.

     */

    public ProfileAdapter( ) {

    }



    /**

     * Replaces the post list without sorting (legacy setter).

     *

     * @param postList posts to show in the grid

     */

    public void setPostList(List<Post> postList) {

        this.postList = postList;

    }



    /**

     * Inflates one profile grid cell layout.

     */

    @NonNull

    @Override

    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_items, viewGroup, false);

        return new ViewHolder(view);

    }



    /**

     * Loads the first image of the post, hides shimmer when done, and wires tap to open post details.

     */

    @Override

    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder viewHolder, int position) {

        if (postList == null || position >= postList.size()) {

            return;

        }

        Post post = postList.get(position);

        String fullImageUrl = null;

        if (post.getAll_images() != null && !post.getAll_images().isEmpty()) {

            String firstImage = post.getAll_images().split(",")[0];

            fullImageUrl = ImageUrlUtils.toFullUrl(firstImage);

        }



        viewHolder.shimmerFrameLayout.startShimmer();

        viewHolder.shimmerFrameLayout.setVisibility(View.VISIBLE);

        viewHolder.imageContent.setVisibility(View.INVISIBLE);



        if (fullImageUrl != null){

            Glide.with(viewHolder.itemView.getContext())

                    .load(fullImageUrl)

                    .centerCrop()

                    .listener(new RequestListener<Drawable>() {

                        @Override

                        public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {

                            viewHolder.shimmerFrameLayout.stopShimmer();

                            viewHolder.shimmerFrameLayout.setVisibility(View.GONE);

                            viewHolder.imageContent.setVisibility(View.VISIBLE);

                            return false;

                        }



                        @Override

                        public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {

                            viewHolder.shimmerFrameLayout.stopShimmer();

                            viewHolder.shimmerFrameLayout.setVisibility(View.GONE);

                            viewHolder.imageContent.setVisibility(View.VISIBLE);

                            return false;

                        }

                    })

                    .into(viewHolder.imageContent);

        }else {

            viewHolder.shimmerFrameLayout.stopShimmer();

            viewHolder.shimmerFrameLayout.setVisibility(View.GONE);

            viewHolder.imageContent.setVisibility(View.VISIBLE);

        }

        viewHolder.imageContent.setOnClickListener( v -> {



            if (onPostClickListener !=  null){

                onPostClickListener.onPostClick(post);

            }





        });









    }



    /**

     * Returns how many posts are in the grid, or zero if the list is null.

     */

    @Override

    public int getItemCount() {

        return postList != null ? postList.size() : 0;

    }





    /**

     * Holds the thumbnail image and shimmer placeholder for one profile post cell.

     */

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView view;

        ImageView imageContent;

        ShimmerFrameLayout shimmerFrameLayout;



        /**

         * Finds views inside one profile grid item layout.

         */

        public ViewHolder(@NonNull View itemView) {

            super(itemView);



            imageContent = itemView.findViewById(R.id.image_content_id);

            shimmerFrameLayout= itemView.findViewById(R.id.shimmer_layout);

        }





    }



    /**

     * Sets the listener that runs when a post thumbnail is tapped.

     *

     * @param onPostClickListener callback for post taps

     */

    public void setOnPostClickListener(OnPostClickListener onPostClickListener) {

        this.onPostClickListener = onPostClickListener;

    }



    /**

     * Sets posts sorted newest first and refreshes the whole grid.

     *

     * @param postList posts to display on the profile

     */

    public  void setUserPostList(List<Post> postList){

        if (postList != null) {

            List<Post> copy = new ArrayList<>(postList);

            copy.sort((a, b) -> b.getCreateAt().compareTo(a.getCreateAt()));

            this.postList = copy;

        } else {

            this.postList = null;

        }

        notifyDataSetChanged();

    }





}

