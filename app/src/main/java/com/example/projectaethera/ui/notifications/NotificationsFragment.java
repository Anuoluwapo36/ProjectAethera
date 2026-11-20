package com.example.projectaethera.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.example.projectaethera.R;
import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.model.remote.NotificationItem;
import com.example.projectaethera.ui.MainActivity;
import com.example.projectaethera.ui.adapter.NotificationsAdapter;
import com.example.projectaethera.ui.profile.PostDetailsViewFragment;
import com.example.projectaethera.ui.profile.ProfileFragment;
import com.example.projectaethera.ui.status.ViewStatusFragment;
import com.example.projectaethera.utils.NavHelper;
import com.example.projectaethera.viewmodel.NotificationViewModel;

/**
 * Full-screen list of likes, follows, comments, and other alerts.
 * Opened from the bell icon on {@link com.example.projectaethera.ui.home.HomeFragment}.
 * Uses activity-scoped {@link NotificationViewModel}; marks all as read on open.
 */
public class NotificationsFragment extends Fragment {

    private NotificationViewModel notificationViewModel;
    private NotificationsAdapter adapter;
    private TextView emptyText;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notifications_fragment, container, false);
    }

    /**
     * Loads notifications, shows empty state, and wires row clicks to profile/post/status targets.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userId = AppConfig.getUserId(requireContext());
        RecyclerView recyclerView = view.findViewById(R.id.notifications_recycler);
        emptyText = view.findViewById(R.id.notifications_empty);
        emptyText.setVisibility(View.VISIBLE);
        ImageView backButton = view.findViewById(R.id.notifications_back);

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        adapter = new NotificationsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnNotificationClickListener(this::openNotificationTarget);

        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);

        notificationViewModel.getNotifications().observe(getViewLifecycleOwner(), items -> {
            if (items == null || items.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setItems(items);
            }
        });

        notificationViewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        notificationViewModel.loadNotifications(userId);
        notificationViewModel.markAllAsRead(userId);
    }

    @Override
    public void onResume() {
        super.onResume();
        int currentUserId = AppConfig.getUserId(requireContext());
        if (currentUserId > 0 && currentUserId != userId) {
            userId = currentUserId;
            if (notificationViewModel != null) {
                notificationViewModel.resetForUser(userId);
            }
        }
        if (userId > 0 && notificationViewModel != null) {
            notificationViewModel.loadNotifications(userId);
        }
    }

    /**
     * Routes a notification tap to the right screen: profile overlay, post detail, or status viewer.
     */
    private void openNotificationTarget(NotificationItem item) {
        if ("follow".equals(item.getType())) {
            ProfileFragment profileFragment = new ProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("userId", item.getActorId());
            profileFragment.setArguments(bundle);
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).loadFragment(profileFragment);
            }
            return;
        }

        if (item.getPostId() != null && item.getPostId() > 0) {
            PostDetailsViewFragment postFragment = new PostDetailsViewFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("postId", item.getPostId());
            postFragment.setArguments(bundle);
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).loadFragment(postFragment);
            }
            return;
        }

        if (item.getStatusId() != null && item.getStatusId() > 0) {
            ViewStatusFragment statusFragment = new ViewStatusFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("targetUserId", item.getUserId());
            statusFragment.setArguments(bundle);
            NavHelper.openOverlay(this, statusFragment);
        }
    }
}
