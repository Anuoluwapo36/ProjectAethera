package com.example.projectaethera.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.projectaethera.R;
import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.repository.FollowRepository;
import com.example.projectaethera.ui.adapter.HomeAdapter;
import com.example.projectaethera.model.remote.StatusGroup;
import com.example.projectaethera.ui.adapter.StatusStripHeaderAdapter;
import com.example.projectaethera.ui.adapter.StatusStripAdapter;
import com.example.projectaethera.ui.notifications.NotificationsFragment;
import com.example.projectaethera.ui.post.GalleryFragment;
import com.example.projectaethera.ui.status.ViewStatusFragment;
import com.example.projectaethera.utils.NavHelper;
import com.example.projectaethera.utils.PostOptionsHelper;
import com.example.projectaethera.viewmodel.DeletePostViewModel;
import com.example.projectaethera.viewmodel.HomeFeedViewModel;
import com.example.projectaethera.viewmodel.LikeViewModel;
import com.example.projectaethera.viewmodel.NotificationViewModel;
import com.example.projectaethera.viewmodel.StatusViewModel;
import com.example.projectaethera.viewmodel.UserViewModel;

/**
 * Main home screen: status/story strip at the top + scrollable post feed.
 * <p>
 * I use {@link UserViewModel#getLoggedInUserData()} for the "Your story" circle
 * so opening someone else's profile does not change my name/photo here.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private HomeFeedViewModel homeFeedViewModel;
    private LikeViewModel likeViewModel;
    private NotificationViewModel notificationViewModel;
    private StatusViewModel statusViewModel;
    private UserViewModel userViewModel;
    private StatusStripHeaderAdapter statusHeaderAdapter;
    private StatusStripAdapter statusStripAdapter;
    private ConcatAdapter concatAdapter;
    private DeletePostViewModel deletePostViewModel;
    private TextView badgeUnread;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int currentPage = 1;
    private int feedToken = 0;
    private boolean isLoading = false;
    /** Only scroll to the first post on the very first feed load, not after likes or refresh. */
    private boolean scrollToFirstPostOnNextPage1 = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    /**
     * Sets up the feed RecyclerView, status strip, pull-to-refresh, pagination, and notification badge.
     * Wires {@link HomeFeedViewModel}, {@link LikeViewModel}, {@link StatusViewModel}, and related observers.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_home);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.ig_blue);
        ImageView btnNotifications = view.findViewById(R.id.btn_notifications);
        badgeUnread = view.findViewById(R.id.badge_unread);
        btnNotifications.setOnClickListener(v ->
                NavHelper.openOverlay(this, new NotificationsFragment())
        );

        int userId = AppConfig.getUserId(requireContext());

        homeFeedViewModel = new ViewModelProvider(this).get(HomeFeedViewModel.class);
        likeViewModel = new ViewModelProvider(this).get(LikeViewModel.class);
        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);
        deletePostViewModel = new ViewModelProvider(requireActivity()).get(DeletePostViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemViewCacheSize(2);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        homeAdapter = new HomeAdapter(likeViewModel, new FollowRepository(), userId);
        homeAdapter.setOnPostOptionsListener((postId, ownerUserId) ->
                PostOptionsHelper.showDeleteOption(this, postId, ownerUserId, deletePostViewModel));

        statusHeaderAdapter = new StatusStripHeaderAdapter();
        statusStripAdapter = statusHeaderAdapter.getStripAdapter();
        concatAdapter = new ConcatAdapter(statusHeaderAdapter, homeAdapter);
        recyclerView.setAdapter(concatAdapter);
        bindStatusStripListener();
        setupStatusStrip(userId);
        observeLoggedInUserForStatusStrip();
        loadOwnProfileForStatusStrip(userId);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null || isLoading) {
                    return;
                }

                int lastPost = layoutManager.findLastCompletelyVisibleItemPosition();
                int total = concatAdapter.getItemCount();

                if (lastPost >= total - 1 && homeAdapter.getItemCount() > 0) {
                    isLoading = true;
                    currentPage++;
                    loadHomeFeed(userId, currentPage);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            scrollToFirstPostOnNextPage1 = true;
            refreshHomeFeed(userId);
            if (statusViewModel != null) {
                statusViewModel.loadStatusFeed(userId);
            }
        });

        feedToken = homeFeedViewModel.startNewFeedSession();
        swipeRefreshLayout.setRefreshing(true);
        loadHomeFeed(userId, currentPage);

        observerHomeFeed();
        observeUnreadBadge(userId);
        observePostDeleted();

        getParentFragmentManager().setFragmentResultListener(
                AppConfig.PROFILE_UPDATED_KEY,
                getViewLifecycleOwner(),
                (requestKey, result) -> {
                    int user_Id = AppConfig.getUserId(requireContext());
                    if (user_Id > 0) {
                        loadOwnProfileForStatusStrip(user_Id);
                    }
                }
        );
    }

    /**
     * Watches ONLY the logged-in user's data for the story strip at the top.
     * This is separate from viewedUserData so Mercy (or anyone else) cannot overwrite my circle.
     */
    private void observeLoggedInUserForStatusStrip() {
        userViewModel.getLoggedInUserData().observe(getViewLifecycleOwner(), response -> {
            if (response == null || !response.isSuccess() || response.getData() == null || statusStripAdapter == null) {
                return;
            }
            statusStripAdapter.setOwnProfile(
                    response.getData().getUsername(),
                    response.getData().getImage_uri()
            );
        });
    }

    /** Removes a deleted post from the feed and shows a confirmation toast. */
    private void observePostDeleted() {
        deletePostViewModel.getDeletedPostId().observe(getViewLifecycleOwner(), postId -> {
            if (postId == null) {
                return;
            }
            homeAdapter.removePost(postId);
            Toast.makeText(getContext(), R.string.delete_post_success, Toast.LENGTH_SHORT).show();
            deletePostViewModel.clearDeletedPostId();
        });

        deletePostViewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Refreshes notification badge, status feed, and follow UI when the user returns to Home.
     * Reloads the feed if it was empty (e.g. after a long pause).
     */
    @Override
    public void onResume() {
        super.onResume();
        int userId = AppConfig.getUserId(requireContext());
        if (notificationViewModel != null) {
            notificationViewModel.fetchUnreadCount(userId);
        }
        if (statusViewModel != null) {
            statusViewModel.loadStatusFeed(userId);
        }
        loadOwnProfileForStatusStrip(userId);
        if (homeAdapter != null && homeAdapter.getItemCount() == 0) {
            refreshHomeFeed(userId);
        }
        if (homeAdapter != null) {
            homeAdapter.refreshFollowUi();
        }
    }

    /** Loads my profile photo/name for the first circle in the status row. */
    private void loadOwnProfileForStatusStrip(int userId) {
        if (userId <= 0 || statusStripAdapter == null || userViewModel == null) {
            return;
        }
        userViewModel.fetchLoggedInUserDetails(userId);
    }

    /** Loads status groups into the horizontal story strip via {@link StatusViewModel}. */
    private void setupStatusStrip(int userId) {
        statusViewModel = new ViewModelProvider(requireActivity()).get(StatusViewModel.class);
        statusViewModel.getStatusFeed().observe(getViewLifecycleOwner(), groups -> {
            if (groups != null && statusStripAdapter != null) {
                statusStripAdapter.setGroups(groups);
            }
        });
        statusViewModel.loadStatusFeed(userId);
    }

    private void bindStatusStripListener() {
        if (statusStripAdapter == null) {
            return;
        }
        statusStripAdapter.setListener(new StatusStripAdapter.Listener() {
            @Override
            public void onAddStatusClick() {
                openStatusGallery();
            }

            @Override
            public void onStatusGroupClick(StatusGroup group) {
                openStatusViewer(group.getUserId());
            }
        });
    }

    /** Opens the gallery overlay so the user can pick a photo for a new status/story. */
    private void openStatusGallery() {
        GalleryFragment galleryFragment = new GalleryFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("FROM_STATUS", true);
        galleryFragment.setArguments(bundle);
        NavHelper.openOverlay(this, galleryFragment);
    }

    /** Opens {@link ViewStatusFragment} as an overlay to watch someone's statuses. */
    private void openStatusViewer(int targetUserId) {
        ViewStatusFragment fragment = new ViewStatusFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("targetUserId", targetUserId);
        fragment.setArguments(bundle);
        NavHelper.openOverlay(this, fragment);
    }

    /** Subscribes to {@link NotificationViewModel} and updates the red unread badge on the bell icon. */
    private void observeUnreadBadge(int userId) {
        notificationViewModel.fetchUnreadCount(userId);
        notificationViewModel.getUnreadCount().observe(getViewLifecycleOwner(), this::updateUnreadBadge);
    }

    /** Updates the bell badge — also called from {@link MainActivity} while other tabs are open. */
    public void updateUnreadBadge(int count) {
        if (badgeUnread == null) {
            return;
        }
        if (count <= 0) {
            badgeUnread.setVisibility(View.GONE);
            return;
        }
        badgeUnread.setVisibility(View.VISIBLE);
        badgeUnread.setText(count > 9 ? "9+" : String.valueOf(count));
    }

    /** Called from MainActivity after posting so the feed shows the new post. */
    public void refreshAfterNewPost() {
        int userId = AppConfig.getUserId(requireContext());
        if (userId > 0) {
            scrollToFirstPostOnNextPage1 = true;
            refreshHomeFeed(userId);
        }
    }

    /** Resets pagination and reloads page 1 from {@link HomeFeedViewModel}. */
    private void refreshHomeFeed(int userId) {
        currentPage = 1;
        isLoading = true;
        swipeRefreshLayout.setRefreshing(true);
        feedToken = homeFeedViewModel.startNewFeedSession();
        loadHomeFeed(userId, currentPage);
    }

    private void loadHomeFeed(int userId, int page) {
        homeFeedViewModel.homeFeedContent(userId, page, feedToken);
    }

    /** Observes paginated feed pages from {@link HomeFeedViewModel} and updates the adapter. */
    private void observerHomeFeed() {
        homeFeedViewModel.getFeedPage().observe(getViewLifecycleOwner(), result -> {
            if (result.getToken() != feedToken) {
                return;
            }

            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);

            if (result.isFailed()) {
                if (currentPage > 1) {
                    currentPage--;
                }
                Toast.makeText(getContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (result.getPage() == 1) {
                homeAdapter.setPostList(result.getPosts());
                if (scrollToFirstPostOnNextPage1) {
                    scrollToFirstPostOnNextPage1 = false;
                    recyclerView.post(() -> {
                        if (concatAdapter.getItemCount() > 1) {
                            recyclerView.scrollToPosition(1);
                        } else {
                            recyclerView.scrollToPosition(0);
                        }
                    });
                }
            } else {
                homeAdapter.addPosts(result.getPosts());
            }
        });
    }
}
