package com.example.projectaethera.ui.status;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.projectaethera.R;
import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.model.remote.StatusItem;
import com.example.projectaethera.ui.adapter.StatusViewPagerAdapter;
import com.example.projectaethera.ui.post.GalleryFragment;
import com.example.projectaethera.ui.widgets.ProfileCircleWidgets;
import com.example.projectaethera.utils.ImageUrlUtils;
import com.example.projectaethera.utils.NavHelper;
import com.example.projectaethera.utils.TimeAgoUtils;
import com.example.projectaethera.viewmodel.StatusViewModel;

import java.util.List;

/**
 * Full-screen status/story viewer for one user, opened from the home strip or notifications.
 * Swipes between statuses with a progress bar; supports like, add (own), and delete (own).
 * Uses {@link StatusViewModel} for loading, likes, views, and deletion.
 */
public class ViewStatusFragment extends Fragment {

    private StatusViewModel statusViewModel;
    private StatusViewModel sharedStatusViewModel;
    private StatusViewPagerAdapter pagerAdapter;
    private ViewPager2 viewPager;
    private LinearLayout progressRow;
    private TextView usernameView;
    private TextView timeView;
    private TextView captionView;
    private TextView likeCountView;
    private ImageView likeButton;
    private ImageView addButton;
    private ImageView deleteButton;
    private ProfileCircleWidgets avatarView;

    private int targetUserId;
    private int viewerId;
    private boolean isOwn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_status_fragment, container, false);
    }

    /**
     * Sets up ViewPager, progress bars, like/add/delete actions, and loads statuses for {@code targetUserId}.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        targetUserId = getArguments() != null ? getArguments().getInt("targetUserId", 0) : 0;
        viewerId = AppConfig.getUserId(requireContext());
        isOwn = targetUserId == viewerId;

        viewPager = view.findViewById(R.id.status_view_pager);
        progressRow = view.findViewById(R.id.status_progress_row);
        usernameView = view.findViewById(R.id.status_view_username);
        timeView = view.findViewById(R.id.status_view_time);
        captionView = view.findViewById(R.id.status_view_caption);
        likeCountView = view.findViewById(R.id.status_view_like_count);
        likeButton = view.findViewById(R.id.status_view_like);
        addButton = view.findViewById(R.id.status_view_add);
        deleteButton = view.findViewById(R.id.status_view_delete);
        avatarView = view.findViewById(R.id.status_view_avatar);

        view.findViewById(R.id.status_view_back).setOnClickListener(v -> requireActivity().onBackPressed());

        pagerAdapter = new StatusViewPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateProgress(position);
                bindCurrentStatus(position);
            }
        });

        likeButton.setOnClickListener(v -> {
            StatusItem item = pagerAdapter.getItem(viewPager.getCurrentItem());
            if (item != null) {
                statusViewModel.toggleLike(viewerId, item);
                bindLikeState(item);
            }
        });

        if (isOwn) {
            addButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            addButton.setOnClickListener(v -> openStatusGallery());
            deleteButton.setOnClickListener(v -> {
                StatusItem item = pagerAdapter.getItem(viewPager.getCurrentItem());
                if (item != null) {
                    confirmDelete(item);
                }
            });
        }

        statusViewModel = new ViewModelProvider(this).get(StatusViewModel.class);
        sharedStatusViewModel = new ViewModelProvider(requireActivity()).get(StatusViewModel.class);
        statusViewModel.getUserStatuses().observe(getViewLifecycleOwner(), this::onStatusesLoaded);
        statusViewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        statusViewModel.loadUserStatuses(targetUserId, viewerId);
    }

    /** Opens gallery in status mode so the owner can add another story. */
    private void openStatusGallery() {
        GalleryFragment galleryFragment = new GalleryFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("FROM_STATUS", true);
        galleryFragment.setArguments(bundle);
        NavHelper.openOverlay(this, galleryFragment);
    }

    /** Tells the shared activity {@link StatusViewModel} to reload the home strip after changes. */
    private void refreshStatusFeed() {
        if (viewerId > 0 && sharedStatusViewModel != null) {
            sharedStatusViewModel.loadStatusFeed(viewerId);
        }
    }

    /** Populates the pager after {@link StatusViewModel} returns the user's status list. */
    private void onStatusesLoaded(List<StatusItem> items) {
        if (items == null || items.isEmpty()) {
            Toast.makeText(getContext(), R.string.status_none, Toast.LENGTH_SHORT).show();
            refreshStatusFeed();
            requireActivity().onBackPressed();
            return;
        }

        pagerAdapter.setItems(items);
        buildProgressBars(items.size());
        bindHeader(items.get(0));
        bindCurrentStatus(0);
    }

    private void buildProgressBars(int count) {
        progressRow.removeAllViews();
        for (int i = 0; i < count; i++) {
            View bar = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 6, 1f);
            params.setMargins(4, 0, 4, 0);
            bar.setLayoutParams(params);
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(8f);
            bg.setColor(i == 0 ? 0xFFFFFFFF : 0x66FFFFFF);
            bar.setBackground(bg);
            progressRow.addView(bar);
        }
    }

    private void updateProgress(int position) {
        for (int i = 0; i < progressRow.getChildCount(); i++) {
            View bar = progressRow.getChildAt(i);
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(8f);
            bg.setColor(i <= position ? 0xFFFFFFFF : 0x66FFFFFF);
            bar.setBackground(bg);
        }
    }

    private void bindHeader(StatusItem item) {
        usernameView.setText(item.getUsername() != null ? item.getUsername() : "User");
        if (item.getProfileImage() != null && !item.getProfileImage().isEmpty()) {
            Glide.with(this)
                    .load(ImageUrlUtils.toFullUrl(item.getProfileImage()))
                    .placeholder(AppConfig.DEFAULT_PROFILE_IMAGE)
                    .circleCrop()
                    .into(avatarView);
        } else {
            avatarView.setImageResource(AppConfig.DEFAULT_PROFILE_IMAGE);
        }
    }

    /** Updates username, avatar, time, caption, and like state for the status at {@code position}. */
    private void bindCurrentStatus(int position) {
        StatusItem item = pagerAdapter.getItem(position);
        if (item == null) {
            return;
        }

        timeView.setText(TimeAgoUtils.formatIso(item.getCreatedAt()));
        captionView.setText(item.getCaption() != null ? item.getCaption() : "");
        bindLikeState(item);
        statusViewModel.markViewed(item.getStatusId(), viewerId);
    }

    private void bindLikeState(StatusItem item) {
        likeCountView.setText(String.valueOf(item.getLikeCount()));
        likeButton.setImageResource(item.getIsLiked() == 1
                ? R.drawable.heart_solid_full
                : R.drawable.heart_regular_full);
    }

    private void confirmDelete(StatusItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.status_delete_title)
                .setMessage(R.string.status_delete_message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) ->
                        statusViewModel.deleteStatus(viewerId, item.getStatusId(), () ->
                                handleDeleted(viewPager.getCurrentItem())))
                .show();
    }

    /** Removes the deleted status from the pager or closes if none remain. */
    private void handleDeleted(int deletedPosition) {
        Toast.makeText(getContext(), R.string.status_deleted, Toast.LENGTH_SHORT).show();
        refreshStatusFeed();

        List<StatusItem> remaining = pagerAdapter.removeItem(deletedPosition);
        if (remaining.isEmpty()) {
            requireActivity().onBackPressed();
            return;
        }

        buildProgressBars(remaining.size());
        int newPosition = Math.min(deletedPosition, remaining.size() - 1);
        viewPager.setCurrentItem(newPosition, false);
        updateProgress(newPosition);
        bindCurrentStatus(newPosition);
    }
}
