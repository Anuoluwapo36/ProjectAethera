package com.example.projectaethera.ui.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.projectaethera.model.remote.FollowResponse;
import com.example.projectaethera.model.remote.HomeFeedRequest;
import com.example.projectaethera.repository.FollowRepository;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * RecyclerView adapter for the main home feed of posts.
 * Each row shows author info, follow button, image slider, likes, comments, and post options.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private static final String PAYLOAD_FOLLOW = "payload_follow";
    private static final String PAYLOAD_LIKE = "payload_like";

    private List<HomeFeedRequest> postList = new ArrayList<>();
    private final LikeViewModel likeViewModel;
    private final FollowRepository followRepository;
    private final int loggedInUserId;
    private final Map<Integer, Boolean> followCache = new HashMap<>();
    private OnPostOptionsListener optionsListener;

    /**
     * Called when the post owner opens the three-dot options menu on their post.
     */
    public interface OnPostOptionsListener {
        void onPostOptions(int postId, int ownerUserId);
    }

    /**
     * Sets the listener for post options menu taps.
     *
     * @param listener callback with post id and owner user id
     */
    public void setOnPostOptionsListener(OnPostOptionsListener listener) {
        this.optionsListener = listener;
    }

    /**
     * Removes one post from the feed by id and animates the row removal.
     *
     * @param postId id of the post to remove from the list
     */
    public void removePost(int postId) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getPostId() == postId) {
                postList.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    /**
     * Creates the home feed adapter with dependencies for likes and follow toggles.
     *
     * @param likeViewModel     handles like and unlike API calls
     * @param followRepository  handles follow toggle API calls
     * @param loggedInUserId    current user's id
     */
    public HomeAdapter(LikeViewModel likeViewModel, FollowRepository followRepository, int loggedInUserId) {
        this.likeViewModel = likeViewModel;
        this.followRepository = followRepository;
        this.loggedInUserId = loggedInUserId;
    }

    /**
     * Replaces the feed, sorts newest first, rebuilds follow cache, and refreshes all rows.
     *
     * @param postList full home feed from the server
     */
    public void setPostList(List<HomeFeedRequest> postList) {
        List<HomeFeedRequest> sorted = postList != null ? new ArrayList<>(postList) : new ArrayList<>();
        Collections.sort(sorted, (a, b) -> {
            if (a.getCreateAt() == null || b.getCreateAt() == null) {
                return 0;
            }
            return b.getCreateAt().compareTo(a.getCreateAt());
        });
        this.postList = sorted;
        followCache.clear();
        for (HomeFeedRequest post : this.postList) {
            if (post.getUserId() != loggedInUserId) {
                followCache.put(post.getUserId(), post.getIsFollowing() != 0);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Appends more posts for pagination without clearing existing items.
     *
     * @param newPosts additional feed page from the server
     */
    public void addPosts(List<HomeFeedRequest> newPosts) {
        if (newPosts == null || newPosts.isEmpty()) {
            return;
        }
        int start = this.postList.size();
        for (HomeFeedRequest post : newPosts) {
            if (post.getUserId() != loggedInUserId) {
                followCache.put(post.getUserId(), post.getIsFollowing() != 0);
            }
        }
        this.postList.addAll(newPosts);
        notifyItemRangeInserted(start, newPosts.size());
    }

    /**
     * Inflates one home feed post row layout.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_item, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * Handles partial updates for follow button or like state when payloads are sent.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
            return;
        }

        HomeFeedRequest post = postList.get(position);
        for (Object payload : payloads) {
            if (PAYLOAD_FOLLOW.equals(payload)) {
                bindFollowButton(holder, post);
            } else if (PAYLOAD_LIKE.equals(payload)) {
                bindLikeState(holder, post);
            }
        }
    }

    /**
     * Binds profile, caption, likes, images, follow, comments, and navigation for one feed post.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeFeedRequest post = postList.get(position);

        holder.commentCount.setText(String.valueOf(post.getCommentCount()));

        if (post.getUserProfile() != null) {
            holder.username.setText(post.getUserProfile().getUsername());
            holder.user_name_bottom.setText(post.getUserProfile().getUsername());

            Glide.with(holder.itemView.getContext())
                    .load(ImageUrlUtils.toFullUrl(post.getUserProfile().getImage_uri()))
                    .placeholder(AppConfig.DEFAULT_PROFILE_IMAGE)
                    .override(132, 132)
                    .circleCrop()
                    .into(holder.profileHomeFeed);
        }

        bindFollowButton(holder, post);
        bindPostOptions(holder, post);

        String captionText = post.getCaption();
        if (captionText != null && !captionText.trim().isEmpty()) {
            holder.caption.setText(captionText);
            holder.caption.setVisibility(View.VISIBLE);
        } else {
            holder.caption.setVisibility(View.GONE);
        }

        bindLikeState(holder, post);
        setupLikeInteractions(holder, post);

        holder.timeAgo.setText(TimeAgoUtils.format(post.getCreateAt()));

        bindImages(holder, post);

        holder.comment.setOnClickListener(v -> {
            CommentSheetFragment sheetFragment = new CommentSheetFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("postId", post.getPostId());
            sheetFragment.setArguments(bundle);
            sheetFragment.setCountListener(totalCount -> {
                holder.commentCount.setText(String.valueOf(totalCount));
                post.setCommentCount(totalCount);
            });
            sheetFragment.show(((FragmentActivity) holder.itemView.getContext()).getSupportFragmentManager(), "SHEET_COMET");
        });

        View.OnClickListener profileClick = v -> {
            ProfileFragment profileFragment = new ProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("userId", post.getUserId());
            profileFragment.setArguments(bundle);
            ((MainActivity) holder.itemView.getContext()).loadFragment(profileFragment);
        };
        holder.username.setOnClickListener(profileClick);
        holder.user_name_bottom.setOnClickListener(profileClick);
    }

    private void bindLikeState(ViewHolder holder, HomeFeedRequest post) {
        holder.likeCountTv.setText(String.valueOf(post.getLikeCount()));
        if (post.getIsLiked() == 1) {
            holder.likeHeart.setBackgroundResource(R.drawable.heart_solid_full);
        } else {
            holder.likeHeart.setBackgroundResource(R.drawable.heart_regular_full);
        }
    }

    private void setupLikeInteractions(ViewHolder holder, HomeFeedRequest post) {
        holder.likeHeart.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }

            HomeFeedRequest clickedPost = postList.get(adapterPosition);
            if (clickedPost.getIsLiked() == 0) {
                likePost(clickedPost);
                LikeAnimationHelper.animateLikeButton(holder.likeHeart);
            } else {
                unlikePost(clickedPost);
            }
            bindLikeState(holder, clickedPost);
        });

        LikeAnimationHelper.attachDoubleTapLike(holder.postMediaContainer, (x, y) -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }

            HomeFeedRequest tappedPost = postList.get(adapterPosition);
            LikeAnimationHelper.showHeartBurst(holder.postMediaContainer, x, y);

            if (tappedPost.getIsLiked() == 0) {
                likePost(tappedPost);
                bindLikeState(holder, tappedPost);
                LikeAnimationHelper.animateLikeButton(holder.likeHeart);
            }
        });
    }

    private void likePost(HomeFeedRequest post) {
        likeViewModel.likePost(loggedInUserId, post.getPostId());
        post.setIsLiked(1);
        post.setLikeCount(post.getLikeCount() + 1);
    }

    private void unlikePost(HomeFeedRequest post) {
        likeViewModel.unlikePost(loggedInUserId, post.getPostId());
        post.setIsLiked(0);
        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
    }

    private void bindImages(ViewHolder holder, HomeFeedRequest post) {
        List<String> imageList = new ArrayList<>();
        if (post.getAll_images() != null && !post.getAll_images().trim().isEmpty()) {
            for (String path : post.getAll_images().split(",")) {
                String url = ImageUrlUtils.toFullUrl(path.trim());
                if (url != null) {
                    imageList.add(url);
                }
            }
        }

        if (imageList.isEmpty()) {
            holder.viewPager_post_images.setAdapter(null);
            holder.viewPager_post_images.setVisibility(View.GONE);
            holder.dots_conatiner.setVisibility(View.GONE);
            holder.shimmerFrameLayout.setVisibility(View.GONE);
            holder.boundPostId = -1;
            return;
        }

        holder.shimmerFrameLayout.setVisibility(View.VISIBLE);
        holder.shimmerFrameLayout.startShimmer();
        holder.viewPager_post_images.setVisibility(View.VISIBLE);
        holder.dots_conatiner.setVisibility(imageList.size() > 1 ? View.VISIBLE : View.GONE);
        holder.imageCount = imageList.size();

        if (holder.boundPostId != post.getPostId()) {
            holder.boundPostId = post.getPostId();
            holder.viewPager_post_images.setAdapter(null);
            PostImageAdapter pagerAdapter = new PostImageAdapter(
                    holder.itemView.getContext(), imageList, holder.imageTargetSize
            );
            pagerAdapter.setOnFirstImageLoadedListener(() -> {
                holder.shimmerFrameLayout.stopShimmer();
                holder.shimmerFrameLayout.setVisibility(View.GONE);
            });
            holder.viewPager_post_images.setAdapter(pagerAdapter);
            holder.viewPager_post_images.setCurrentItem(0, false);
        } else {
            holder.shimmerFrameLayout.stopShimmer();
            holder.shimmerFrameLayout.setVisibility(View.GONE);
        }

        ViewUtils.updateDots(holder.dots_conatiner, holder.imageCount, holder.viewPager_post_images.getCurrentItem(), holder.itemView.getContext());
    }

    private void bindPostOptions(ViewHolder holder, HomeFeedRequest post) {
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
    }

    private void bindFollowButton(ViewHolder holder, HomeFeedRequest post) {
        int authorId = post.getUserId();

        if (authorId == loggedInUserId) {
            holder.followBtn.setVisibility(View.GONE);
            holder.followBtn.setOnClickListener(null);
            return;
        }

        holder.followBtn.setVisibility(View.VISIBLE);

        Boolean cachedFollowing = followCache.get(authorId);
        if (cachedFollowing != null) {
            ViewUtils.applyFollowButtonState(holder.followBtn, cachedFollowing);
        } else {
            boolean following = post.getIsFollowing() != 0;
            followCache.put(authorId, following);
            ViewUtils.applyFollowButtonState(holder.followBtn, following);
        }

        holder.followBtn.setOnClickListener(v -> {
            holder.followBtn.setEnabled(false);
            followRepository.followToggle(loggedInUserId, authorId).enqueue(new Callback<FollowResponse>() {
                @Override
                public void onResponse(@NonNull Call<FollowResponse> call, @NonNull Response<FollowResponse> response) {
                    holder.followBtn.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        boolean nowFollowing = "follow".equals(response.body().getAction());
                        updateFollowStateForUser(authorId, nowFollowing);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<FollowResponse> call, @NonNull Throwable t) {
                    holder.followBtn.setEnabled(true);
                }
            });
        });
    }

    private void applyFollowStateForUser(int authorUserId, boolean following) {
        int followingValue = following ? 1 : 0;
        for (HomeFeedRequest item : postList) {
            if (item.getUserId() == authorUserId) {
                item.setIsFollowing(followingValue);
            }
        }
    }

    private void updateFollowStateForUser(int authorUserId, boolean following) {
        followCache.put(authorUserId, following);
        applyFollowStateForUser(authorUserId, following);

        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getUserId() == authorUserId) {
                notifyItemChanged(i, PAYLOAD_FOLLOW);
            }
        }
    }

    /**
     * Rebuilds the follow cache from current post data without refreshing the UI.
     */
    public void invalidateFollowCache() {
        for (HomeFeedRequest post : postList) {
            if (post.getUserId() != loggedInUserId) {
                followCache.put(post.getUserId(), post.getIsFollowing() != 0);
            }
        }
    }

    /**
     * Refreshes follow button state on every visible post row.
     */
    public void refreshFollowUi() {
        invalidateFollowCache();
        for (int i = 0; i < postList.size(); i++) {
            notifyItemChanged(i, PAYLOAD_FOLLOW);
        }
    }

    /**
     * Registers the page-change callback when a row becomes visible for dot indicators.
     */
    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.viewPager_post_images.registerOnPageChangeCallback(holder.pageChangeCallback);
    }

    /**
     * Unregisters the page-change callback when a row scrolls off screen.
     */
    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        holder.viewPager_post_images.unregisterOnPageChangeCallback(holder.pageChangeCallback);
        super.onViewDetachedFromWindow(holder);
    }

    /**
     * Clears Glide and ViewPager state when a row is recycled.
     */
    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.with(holder.itemView.getContext()).clear(holder.profileHomeFeed);
        holder.viewPager_post_images.setAdapter(null);
        holder.boundPostId = -1;
        holder.imageCount = 0;
        holder.dots_conatiner.removeAllViews();
    }

    /**
     * Holds all views for one home feed post including nested image ViewPager2 and follow button.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        ProfileCircleWidgets profileHomeFeed;
        TextView username, user_name_bottom, caption, timeAgo, likeCountTv, commentCount;
        ImageView likeHeart, comment, optionBar;
        Button followBtn;
        FrameLayout postMediaContainer;
        ViewPager2 viewPager_post_images;
        ShimmerFrameLayout shimmerFrameLayout;
        LinearLayout dots_conatiner;

        int boundPostId = -1;
        int imageCount = 0;
        final int imageTargetSize;
        final ViewPager2.OnPageChangeCallback pageChangeCallback;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileHomeFeed = itemView.findViewById(R.id.imageProfile);
            username = itemView.findViewById(R.id.username);
            user_name_bottom = itemView.findViewById(R.id.user_name);
            caption = itemView.findViewById(R.id.caption);
            timeAgo = itemView.findViewById(R.id.time);
            likeHeart = itemView.findViewById(R.id.like);
            likeCountTv = itemView.findViewById(R.id.likeCount);
            dots_conatiner = itemView.findViewById(R.id.dots_container);
            viewPager_post_images = itemView.findViewById(R.id.viewPager_post_images);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmer_layout);
            comment = itemView.findViewById(R.id.comment);
            commentCount = itemView.findViewById(R.id.commentCount);
            optionBar = itemView.findViewById(R.id.optionBar);
            followBtn = itemView.findViewById(R.id.follow_btn);
            postMediaContainer = itemView.findViewById(R.id.post_media_container);

            int screenWidth = itemView.getResources().getDisplayMetrics().widthPixels;
            imageTargetSize = Math.min(screenWidth, 1080);

            viewPager_post_images.setOffscreenPageLimit(1);
            viewPager_post_images.setOverScrollMode(View.OVER_SCROLL_NEVER);
            viewPager_post_images.setNestedScrollingEnabled(false);

            pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    ViewUtils.updateDots(dots_conatiner, imageCount, position, itemView.getContext());
                }
            };
        }
    }

    /**
     * Returns how many posts are currently in the home feed.
     */
    @Override
    public int getItemCount() {
        return postList.size();
    }
}
