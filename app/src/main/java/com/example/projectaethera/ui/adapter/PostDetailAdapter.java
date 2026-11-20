package com.example.projectaethera.ui.adapter;



import android.content.Context;

import android.os.Bundle;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.FrameLayout;

import android.widget.ImageView;

import android.widget.LinearLayout;

import android.widget.TextView;



import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentActivity;

import androidx.recyclerview.widget.RecyclerView;

import androidx.viewpager2.widget.ViewPager2;



import com.bumptech.glide.Glide;

import com.example.projectaethera.R;

import com.example.projectaethera.config.AppConfig;

import com.example.projectaethera.model.entity.Post;

import com.example.projectaethera.model.entity.UserProfile;

import com.example.projectaethera.ui.MainActivity;

import com.example.projectaethera.ui.comment.CommentSheetFragment;

import com.example.projectaethera.ui.profile.ProfileFragment;

import com.example.projectaethera.ui.widgets.ProfileCircleWidgets;

import com.example.projectaethera.utils.ImageUrlUtils;

import com.example.projectaethera.utils.LikeAnimationHelper;

import com.example.projectaethera.utils.TimeAgoUtils;

import com.example.projectaethera.utils.ViewUtils;

import com.example.projectaethera.viewmodel.LikeViewModel;

import com.facebook.shimmer.ShimmerFrameLayout;



import java.util.ArrayList;

import java.util.List;



/**

 * RecyclerView adapter that shows full post detail cards for a user's profile feed.

 * Each row looks like a home post: profile, images slider, likes, comments, and options menu.

 */

public class PostDetailAdapter extends RecyclerView.Adapter<PostDetailAdapter.PostViewHolder> {



    private List<Post> postList;

    private UserProfile userProfile;

    private LikeViewModel likeViewModel;

    private int loggedInUserId;

    private OnPostOptionsListener optionsListener;



    /**

     * Called when the post owner taps the options menu on their own post.

     */

    public interface OnPostOptionsListener {

        void onPostOptions(int postId, int ownerUserId);

    }



    /**

     * Sets the listener for the post options (three-dot) menu.

     *

     * @param listener callback with post id and owner user id

     */

    public void setOnPostOptionsListener(OnPostOptionsListener listener) {

        this.optionsListener = listener;

    }



    /**

     * Creates the adapter for a list of posts on a profile or detail screen.

     *

     * @param postList        posts to display

     * @param loggedInUserId  current user's id for like and options logic

     * @param likeViewModel   used to like and unlike posts

     */

    public PostDetailAdapter(List<Post> postList, int loggedInUserId, LikeViewModel likeViewModel) {

        this.postList = postList;

        this.loggedInUserId = loggedInUserId;

        this.likeViewModel = likeViewModel;

    }





    /**

     * Sets the profile shown in the header of each post card and refreshes the list.

     *

     * @param userProfile author profile for username and avatar

     */

    public void setUserProfile(UserProfile userProfile) {

        this.userProfile = userProfile;

        notifyDataSetChanged();

    }



    /**

     * Inflates the full post detail row layout.

     */

    @NonNull

    @Override

    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())

                .inflate(R.layout.profile_details_item, parent, false);

        return new PostViewHolder(view);

    }



    /**

     * Binds profile, caption, likes, image pager with dots, comments sheet, options, and profile navigation.

     */

    @Override

    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        Post post = postList.get(position);



        //user profile details and name display

        if (userProfile != null) {

            holder.username.setText(userProfile.getUsername());

            holder.userNameBelow.setText( userProfile.getUsername());











            Glide.with(holder.itemView.getContext())

                    .load(ImageUrlUtils.toFullUrl(userProfile.getImage_uri()))

                    .placeholder(AppConfig.DEFAULT_PROFILE_IMAGE)

                    .circleCrop()

                    .into(holder.imageProfile);



        }



        //setting caption

        String captionText = post.getCaption();

        if (captionText != null && !captionText.trim().isEmpty()){

            holder.caption.setText(captionText);

            holder.caption.setVisibility(View.VISIBLE);

        }else{

            holder.caption.setVisibility(View.GONE);

        }

        //like count

        holder.likeCount.setText(String.valueOf(post.getLikeCount()));



        if (post.getIsLiked() == 1) {

            holder.like.setBackgroundResource(R.drawable.heart_solid_full);

        } else {

            holder.like.setBackgroundResource(R.drawable.heart_regular_full);

        }



        holder.like.setOnClickListener(v -> {

            if (post.getIsLiked() == 0) {

                likeViewModel.likePost(loggedInUserId, post.getPostId());

                post.setIsLiked(1);

                post.setLikeCount(post.getLikeCount() + 1);

                LikeAnimationHelper.animateLikeButton(holder.like);

            } else {

                likeViewModel.unlikePost(loggedInUserId, post.getPostId());

                post.setIsLiked(0);

                post.setLikeCount(Math.max(0, post.getLikeCount() - 1));

            }

            holder.likeCount.setText(String.valueOf(post.getLikeCount()));

            if (post.getIsLiked() == 1) {

                holder.like.setBackgroundResource(R.drawable.heart_solid_full);

            } else {

                holder.like.setBackgroundResource(R.drawable.heart_regular_full);

            }

        });



        LikeAnimationHelper.attachDoubleTapLike(holder.postMediaContainer, (x, y) -> {

            LikeAnimationHelper.showHeartBurst(holder.postMediaContainer, x, y);

            if (post.getIsLiked() == 0) {

                likeViewModel.likePost(loggedInUserId, post.getPostId());

                post.setIsLiked(1);

                post.setLikeCount(post.getLikeCount() + 1);

                holder.likeCount.setText(String.valueOf(post.getLikeCount()));

                holder.like.setBackgroundResource(R.drawable.heart_solid_full);

                LikeAnimationHelper.animateLikeButton(holder.like);

            }

        });



        //post created time

         String timeAgo = TimeAgoUtils.format(post.getCreateAt());

         holder.postTime.setText(timeAgo);



        holder.shimmerFrameLayout.setVisibility(View.VISIBLE);

        holder.shimmerFrameLayout.startShimmer();

        holder.shimmerFrameLayout.setVisibility(View.VISIBLE);

        holder.viewPager_post_images.setVisibility(View.INVISIBLE);

        holder.dots_conatiner.removeAllViews();

        holder.viewPager_post_images.setAdapter(null);

        ViewPager2.OnPageChangeCallback oldCallback = (ViewPager2.OnPageChangeCallback) holder.viewPager_post_images.getTag();

        if (oldCallback != null) {

            holder.viewPager_post_images.unregisterOnPageChangeCallback(oldCallback);

        }



        //display all post image

        //single post image and multiple image post content

        List<String> imageList = new ArrayList<>();

        if (post.getAll_images() != null && !post.getAll_images().trim().isEmpty()) {

            String[] parts = post.getAll_images().split(",");





            for (String path : parts) {

                String cleanPath = path.trim();

                String url = ImageUrlUtils.toFullUrl(cleanPath);

                if (url != null) {

                    imageList.add(url);

                }

            }

        }





        if (!imageList.isEmpty()) {



            PostImageAdapter pagerAdapter = new PostImageAdapter(imageList);

            holder.viewPager_post_images.setAdapter(pagerAdapter);



            //sliding view tracking

            ViewUtils.updateDots(holder.dots_conatiner, imageList.size(), 0, holder.itemView.getContext());

            holder.dots_conatiner.setVisibility(imageList.size() > 1 ? View.VISIBLE : View.GONE);

            //page change

            holder.viewPager_post_images.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

                @Override

                public void onPageSelected(int position) {

                    super.onPageSelected(position);

                    ViewUtils.updateDots(holder.dots_conatiner, imageList.size(), position, holder.itemView.getContext());



                }

            });



            if (imageList.size() == 1) {

                holder.dots_conatiner.setVisibility(View.GONE);

            } else {

                holder.dots_conatiner.setVisibility(View.VISIBLE);

            }

            holder.viewPager_post_images.requestLayout();



        }

        holder.shimmerFrameLayout.stopShimmer();

        holder.shimmerFrameLayout.setVisibility(View.GONE);

        holder.viewPager_post_images.setVisibility(View.VISIBLE);



        holder.commentCount.setText(String.valueOf(post.getCommentCount()));







        holder.comment.setOnClickListener(v -> {

            int postId = post.getPostId();





            CommentSheetFragment sheetFragment = new CommentSheetFragment();





            Bundle bundle = new Bundle();

            bundle.putInt("postId", postId);





            sheetFragment.setArguments(bundle);

            sheetFragment.setCountListener(totalCount -> {

                holder.commentCount.setText(String.valueOf(totalCount));

                post.setCommentCount(totalCount);

            });



            sheetFragment.show(((FragmentActivity)holder.itemView.getContext()).getSupportFragmentManager(),"SHEET_COMET");

        });



        if (post.getUserId() == loggedInUserId) {

            holder.optionBar.setVisibility(View.VISIBLE);

            holder.optionBar.setOnClickListener(v -> {

                if (optionsListener != null) {

                    optionsListener.onPostOptions(post.getPostId(), post.getUserId());

                }

            });

        } else {

            holder.optionBar.setVisibility(View.GONE);

            holder.optionBar.setOnClickListener(null);

        }





        /**

         * navigate to user profile by clicking

         */

        holder.username.setOnClickListener(v -> {

            ProfileFragment profileFragment = new ProfileFragment();

            Bundle bundle = new Bundle();

            bundle.putInt("userId", post.getUserId());

            profileFragment.setArguments(bundle);



            ((MainActivity) holder.itemView.getContext()).loadFragment(profileFragment);

        });



        holder.userNameBelow.setOnClickListener(v -> {

            ProfileFragment profileFragment = new ProfileFragment();

            Bundle bundle = new Bundle();

            bundle.putInt("userId", post.getUserId());

            profileFragment.setArguments(bundle);



            ((MainActivity) holder.itemView.getContext()).loadFragment(profileFragment);

        });

    }



    /**

     * Returns how many posts are in the detail list.

     */

    @Override

    public int getItemCount() {

        return postList != null ? postList.size() : 0;

    }





    /**

     * Holds all views for one full post detail card including image pager and action buttons.

     */

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        ProfileCircleWidgets imageProfile;

        TextView username, likeCount, commentCount, caption, userNameBelow, postTime;

        ImageView like, comment, optionBar, imagePlaceholder;

        FrameLayout postMediaContainer;

        ViewPager2 viewPager_post_images;

        ShimmerFrameLayout shimmerFrameLayout;

        LinearLayout dots_conatiner;



        /**

         * Finds views and configures the nested image ViewPager2 for smooth scrolling.

         */

        public PostViewHolder(@NonNull View itemView) {

            super(itemView);

            imageProfile = itemView.findViewById(R.id.imageProfile);

            username = itemView.findViewById(R.id.username);

            viewPager_post_images = itemView.findViewById(R.id.viewPager_post_images);

            shimmerFrameLayout = itemView.findViewById(R.id.shimmer_layout);

            like = itemView.findViewById(R.id.like);

            comment = itemView.findViewById(R.id.comment);

            optionBar = itemView.findViewById(R.id.optionBar);

            likeCount = itemView.findViewById(R.id.likeCount);

            commentCount = itemView.findViewById(R.id.commentCount);

            imagePlaceholder = new ImageView(itemView.getContext());

            caption = itemView.findViewById(R.id.caption);

            userNameBelow = itemView.findViewById(R.id.user_name);

            dots_conatiner = itemView.findViewById(R.id.dots_container);

            postTime = itemView.findViewById(R.id.time);

            postMediaContainer = itemView.findViewById(R.id.post_media_container);





            //stop viewpager from stretching while sliding

            viewPager_post_images.setOverScrollMode(View.OVER_SCROLL_NEVER);

            viewPager_post_images.setNestedScrollingEnabled(false);

            if (viewPager_post_images.getChildCount() > 0) {

                viewPager_post_images.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

            }

        }

    }







}

