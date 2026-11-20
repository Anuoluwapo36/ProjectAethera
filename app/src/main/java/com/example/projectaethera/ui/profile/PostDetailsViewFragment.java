package com.example.projectaethera.ui.profile;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectaethera.R;
import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.model.entity.Post;
import com.example.projectaethera.model.remote.UserResponse;
import com.example.projectaethera.ui.adapter.PostDetailAdapter;
import com.example.projectaethera.utils.PostOptionsHelper;
import com.example.projectaethera.viewmodel.DeletePostViewModel;
import com.example.projectaethera.viewmodel.LikeViewModel;
import com.example.projectaethera.viewmodel.PostDetailsViewModel;
import com.example.projectaethera.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Full-screen view of a single post (opened from profile grid or notifications).
 * Uses fragment-scoped {@link PostDetailsViewModel} and {@link UserViewModel} so it does not
 * clash with Home or the Profile tab.
 */
public class PostDetailsViewFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostDetailAdapter adapter;
    private List<Post> postList;
    private TextView usernameText;
    private ImageView backPress;
    private UserViewModel userViewModel;
    private LikeViewModel likeViewModel;
    private PostDetailsViewModel postDetailsViewModel;
    private DeletePostViewModel deletePostViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_details_fragment, container, false);
    }

    /**
     * Loads post id from arguments, sets up {@link PostDetailAdapter}, and observes delete events.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.profileDetailsPost);
        backPress = view.findViewById(R.id.backPressId);
        usernameText = view.findViewById(R.id.usernameId);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.setNestedScrollingEnabled(false);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        likeViewModel = new ViewModelProvider(this).get(LikeViewModel.class);
        postDetailsViewModel = new ViewModelProvider(this).get(PostDetailsViewModel.class);
        deletePostViewModel = new ViewModelProvider(requireActivity()).get(DeletePostViewModel.class);

        backPress.setOnClickListener(v -> requireActivity().onBackPressed());

        int loggedInUser = AppConfig.getUserId(requireContext());
        postList = new ArrayList<>();
        adapter = new PostDetailAdapter(postList, loggedInUser, likeViewModel);
        adapter.setOnPostOptionsListener((postId, ownerUserId) ->
                PostOptionsHelper.showDeleteOption(this, postId, ownerUserId, deletePostViewModel));
        recyclerView.setAdapter(adapter);

        Bundle args = getArguments();
        if (args != null) {
            int postId = args.getInt("postId");
            postDetailsViewModel.fetchPostDetails(postId, loggedInUser);
            observePostData();
        }

        showDetails();
        observePostDeleted();
    }

    private void observePostDeleted() {
        deletePostViewModel.getDeletedPostId().observe(getViewLifecycleOwner(), postId -> {
            if (postId == null) {
                return;
            }
            Toast.makeText(getContext(), R.string.delete_post_success, Toast.LENGTH_SHORT).show();
            deletePostViewModel.clearDeletedPostId();
            requireActivity().onBackPressed();
        });

        deletePostViewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Fetches post details and refreshes the adapter when {@link PostDetailsViewModel} returns data. */
    private void observePostData() {
        postDetailsViewModel.getPostDetails().observe(getViewLifecycleOwner(), new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                if (post != null) {
                    postList.clear();
                    postList.add(post);
                    adapter.notifyDataSetChanged();
                    userViewModel.fetchViewedUserDetails(post.getUserId());
                }
            }
        });

        postDetailsViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Shows the post owner's username and profile info in the header. */
    public void showDetails() {
        userViewModel.getViewedUserData().observe(getViewLifecycleOwner(), new Observer<UserResponse>() {
            @Override
            public void onChanged(UserResponse userResponse) {
                if (userResponse != null && userResponse.getData() != null) {
                    usernameText.setText(userResponse.getData().getUsername());
                    adapter.setUserProfile(userResponse.getData());
                }
            }
        });

        userViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                usernameText.setText("User Not Found");
            }
        });
    }
}
