package com.example.projectaethera.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectaethera.R;
import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.model.entity.Post;
import com.example.projectaethera.model.remote.UserResponse;
import com.example.projectaethera.ui.adapter.ProfileAdapter;
import com.example.projectaethera.ui.settings.SettingsFragment;
import com.example.projectaethera.ui.widgets.ProfileCircleWidgets;
import com.example.projectaethera.utils.ImageUrlUtils;
import com.example.projectaethera.utils.NavHelper;
import com.example.projectaethera.utils.ViewUtils;
import com.example.projectaethera.viewmodel.DeletePostViewModel;
import com.example.projectaethera.viewmodel.FollowViewModel;
import com.example.projectaethera.viewmodel.UserPostViewModel;
import com.example.projectaethera.viewmodel.UserViewModel;

import java.util.List;

/**
 * Profile screen — can show MY profile (bottom tab) or SOMEONE ELSE (opened from feed/search).
 * <p>
 * Important: my profile uses {@link UserViewModel#getLoggedInUserData()}.
 * Someone else's profile uses {@link UserViewModel#getViewedUserData()}.
 * That way Mercy opening from Home does not change my Profile tab.
 */
public class ProfileFragment extends Fragment {

    private ProfileAdapter adapter;
    private RecyclerView recyclerView;
    private UserPostViewModel userPostViewModel;
    private UserViewModel userViewModel;
    private TextView postCounts, usernameTv, fullNameTv, tv_profession, tv_bio, followers, following;
    private TextView profileEmptyPosts;

    private Button editprofile, btn_follow;
    private ProfileCircleWidgets profileImage;
    private View profileTopBar;
    private TextView profileTopUsername;
    private ImageView btnProfileBack;
    private int profileUserId;
    private boolean isMyProfile = true;
    private FollowViewModel followViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.proifle_fragment, container, false);
    }

    /**
     * Loads profile grid, header info, follow button, and settings entry.
     * Reads optional {@code userId} argument to show another user vs. the logged-in profile tab.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.profile_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setNestedScrollingEnabled(false);
        userPostViewModel = new ViewModelProvider(this).get(UserPostViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        followViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        postCounts = view.findViewById(R.id.post_count);
        usernameTv = view.findViewById(R.id.tv_username);
        fullNameTv = view.findViewById(R.id.tv_full_name);
        editprofile = view.findViewById(R.id.btn_edit_profile);
        tv_profession = view.findViewById(R.id.tv_profession);
        btn_follow = view.findViewById(R.id.btn_follow_user);
        tv_bio = view.findViewById(R.id.tv_bio);
        profileEmptyPosts = view.findViewById(R.id.profile_empty_posts);
        profileImage = view.findViewById(R.id.profileDp);
        followers = view.findViewById(R.id.follower_count);
        following = view.findViewById(R.id.following_count);
        profileTopBar = view.findViewById(R.id.profile_top_bar);
        profileTopUsername = view.findViewById(R.id.profile_top_username);
        btnProfileBack = view.findViewById(R.id.btn_profile_back);
        ImageView btnSettings = view.findViewById(R.id.btn_profile_settings);
        btnSettings.setOnClickListener(v -> NavHelper.openOverlay(this, new SettingsFragment()));
        btnProfileBack.setOnClickListener(v -> NavHelper.popOverlay(this));

        adapter = new ProfileAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnPostClickListener(post -> {
            PostDetailsViewFragment fragment = new PostDetailsViewFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("postId", post.getPostId());
            fragment.setArguments(bundle);
            NavHelper.openOverlay(this, fragment);
        });

        int loginUserId = AppConfig.getUserId(requireContext());

        if (getArguments() != null) {
            profileUserId = getArguments().getInt("userId", 0);
            isMyProfile = (profileUserId == loginUserId);
        } else {
            profileUserId = loginUserId;
            isMyProfile = true;
        }

        if (profileUserId != 0) {
            userPostViewModel.showUserPost(profileUserId);
            fetchProfileDetails();
        }

        if (isMyProfile) {
            editprofile.setVisibility(View.VISIBLE);
            btn_follow.setVisibility(View.GONE);
        } else {
            editprofile.setVisibility(View.GONE);
            btn_follow.setVisibility(View.VISIBLE);
        }

        boolean openedFromNavigation = getArguments() != null && getArguments().containsKey("userId");
        profileTopBar.setVisibility(View.VISIBLE);
        btnProfileBack.setVisibility(openedFromNavigation ? View.VISIBLE : View.GONE);
        btnSettings.setVisibility(isMyProfile ? View.VISIBLE : View.GONE);
        followViewModel.loadFollowersCount(profileUserId);
        followViewModel.loadFollowingCount(profileUserId);
        displayPost();
        showDetails();
        editProfileBtn();
        setupFollowButton();
        observerFollowCount();
        observePostDeleted();

        getParentFragmentManager().setFragmentResultListener(
                AppConfig.PROFILE_UPDATED_KEY,
                getViewLifecycleOwner(),
                (requestKey, result) -> refreshProfileData()
        );

        getParentFragmentManager().setFragmentResultListener(
                AppConfig.POST_CREATED_KEY,
                getViewLifecycleOwner(),
                (requestKey, result) -> refreshPostsAfterUpload()
        );
    }

    /** Reloads the post grid after the user uploads a new post. */
    public void refreshPostsAfterUpload() {
        if (profileUserId != 0) {
            userPostViewModel.showUserPost(profileUserId);
        }
    }

    /**
     * Picks the correct UserViewModel method depending on whose profile this screen shows.
     * My tab profile → logged-in bucket. Overlay from feed → viewed-user bucket.
     */
    private void fetchProfileDetails() {
        if (profileUserId == 0) {
            return;
        }
        if (isMyProfile) {
            userViewModel.fetchLoggedInUserDetails(profileUserId);
        } else {
            userViewModel.fetchViewedUserDetails(profileUserId);
        }
    }

    /** Reloads profile header and post grid after edit-profile saves. */
    private void refreshProfileData() {
        if (profileUserId != 0) {
            fetchProfileDetails();
            userPostViewModel.showUserPost(profileUserId);
        }
    }

    /** Refreshes the post grid when a post is deleted from an overlay on my profile. */
    private void observePostDeleted() {
        DeletePostViewModel deletePostViewModel = new ViewModelProvider(requireActivity()).get(DeletePostViewModel.class);
        deletePostViewModel.getDeletedPostId().observe(getViewLifecycleOwner(), postId -> {
            if (postId == null || !isMyProfile || profileUserId == 0) {
                return;
            }
            userPostViewModel.showUserPost(profileUserId);
            deletePostViewModel.clearDeletedPostId();
        });
    }

    private void editProfileBtn() {
        editprofile.setOnClickListener(v -> openEditProfile());
    }

    /** Opens {@link EditProfileFragment} as a main-activity overlay. */
    private void openEditProfile() {
        NavHelper.openOverlay(this, new EditProfileFragment());
    }

    /** Shows the grid of posts for profileUserId (separate ViewModel per ProfileFragment instance). */
    public void displayPost() {
        userPostViewModel.getGetUserPostSuccess().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> postList) {
                adapter.setUserPostList(postList);
                int count = postList != null ? postList.size() : 0;
                postCounts.setText(String.valueOf(count));
                if (profileEmptyPosts != null) {
                    profileEmptyPosts.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
                }
                if (recyclerView != null) {
                    recyclerView.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
                    recyclerView.post(recyclerView::requestLayout);
                }
            }
        });
    }

    /**
     * Listens to the right LiveData bucket and paints name, bio, photo on screen.
     * My profile → loggedInUserData. Other user → viewedUserData + id check.
     */
    public void showDetails() {
        if (isMyProfile) {
            userViewModel.getLoggedInUserData().observe(getViewLifecycleOwner(), this::bindUserDetailsToViews);
        } else {
            userViewModel.getViewedUserData().observe(getViewLifecycleOwner(), userResponse -> {
                if (userResponse == null || userResponse.getData() == null) {
                    return;
                }
                int responseUserId = userResponse.getData().getUserId();
                if (responseUserId != 0 && responseUserId != profileUserId) {
                    return;
                }
                bindUserDetailsToViews(userResponse);
            });
        }

        userViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                usernameTv.setText("User Not Found");
                fullNameTv.setText("No name");
            }
        });
    }

    /** Puts username, full name, bio, profession and profile image on the layout. */
    private void bindUserDetailsToViews(UserResponse userResponse) {
        if (userResponse == null || userResponse.getData() == null) {
            return;
        }

        usernameTv.setText(userResponse.getData().getUsername());
        profileTopUsername.setText(userResponse.getData().getUsername());
        bindOptionalField(fullNameTv, userResponse.getData().getFullname());
        bindOptionalField(tv_profession, userResponse.getData().getProfession());
        bindOptionalField(tv_bio, userResponse.getData().getBio());

        String username = userResponse.getData().getUsername();
        String fullName = userResponse.getData().getFullname();
        if (fullName != null && username != null
                && fullName.trim().equalsIgnoreCase(username.trim())) {
            fullNameTv.setVisibility(View.GONE);
        }

        String profileImagePath = userResponse.getData().getImage_uri();
        String fullImage = ImageUrlUtils.toFullUrl(profileImagePath);

        Glide.with(requireContext())
                .load(fullImage)
                .placeholder(AppConfig.DEFAULT_PROFILE_IMAGE)
                .fallback(AppConfig.DEFAULT_PROFILE_IMAGE)
                .circleCrop()
                .into(profileImage);
    }

    private void bindOptionalField(TextView view, String value) {
        if (view == null) {
            return;
        }
        if (value == null || value.trim().isEmpty()) {
            view.setVisibility(View.GONE);
            view.setText("");
        } else {
            view.setVisibility(View.VISIBLE);
            view.setText(value.trim());
        }
    }

    private void observerFollowCount() {
        followViewModel.getFollowersCount().observe(getViewLifecycleOwner(), count -> {
            followers.setText(String.valueOf(count));
        });

        followViewModel.getFollowingCount().observe(getViewLifecycleOwner(), count -> {
            following.setText(String.valueOf(count));
        });
    }

    /**
     * Sets up follow/unfollow for another user's profile using {@link FollowViewModel}.
     */
    private void setupFollowButton() {
        if (isMyProfile) {
            return;
        }

        followViewModel.getIsFollowing().observe(getViewLifecycleOwner(), isFollowing -> {
            ViewUtils.applyFollowButtonState(btn_follow, Boolean.TRUE.equals(isFollowing));
        });

        btn_follow.setOnClickListener(v -> {
            btn_follow.setEnabled(false);
            int myId = AppConfig.getUserId(requireContext());
            int theirId = profileUserId;

            followViewModel.toggleFollow(myId, theirId);
            btn_follow.postDelayed(() -> btn_follow.setEnabled(true), 400);
        });

        int myId = AppConfig.getUserId(requireContext());
        int theirId = profileUserId;

        followViewModel.checkIfFollowing(myId, theirId);

        followViewModel.getFollowResponse().observe(getViewLifecycleOwner(), response -> {
            if (response == null) {
                return;
            }
            if (response.isSuccess()) {
                ViewUtils.applyFollowButtonState(btn_follow, "follow".equals(response.getAction()));
            }
            if (response.getMessage() != null) {
                Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Refreshes follower counts and profile details when returning to this screen.
     * Re-checks follow state when viewing someone else's profile.
     */
    @Override
    public void onResume() {
        super.onResume();

        if (profileUserId != 0) {
            followViewModel.refreshCounts(profileUserId);
            userPostViewModel.showUserPost(profileUserId);
        }

        if (isMyProfile) {
            fetchProfileDetails();
        } else {
            fetchProfileDetails();
            int myId = AppConfig.getUserId(requireContext());
            followViewModel.checkIfFollowing(myId, profileUserId);
        }
    }
}
