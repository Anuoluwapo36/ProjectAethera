package com.example.projectaethera.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectaethera.R;
import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.model.SettingsItem;
import com.example.projectaethera.model.remote.UserResponse;
import com.example.projectaethera.ui.adapter.SettingsAdapter;
import com.example.projectaethera.ui.auth.AuthActivity;
import com.example.projectaethera.ui.profile.EditProfileFragment;
import com.example.projectaethera.utils.NavHelper;
import com.example.projectaethera.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Settings screen opened from the profile gear icon.
 * Only shows the logged-in user's account info at the top (not someone I viewed from feed).
 */
public class SettingsFragment extends Fragment {

    private SettingsAdapter adapter;
    private List<SettingsItem> allItems = new ArrayList<>();
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    /**
     * Builds the settings list, search filter, account header, and action handler.
     * Account row uses {@link UserViewModel#getLoggedInUserData()} only.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView back = view.findViewById(R.id.settings_back);
        EditText search = view.findViewById(R.id.settings_search);
        RecyclerView recyclerView = view.findViewById(R.id.settings_recycler);

        back.setOnClickListener(v -> requireActivity().onBackPressed());

        adapter = new SettingsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        allItems = buildItems();
        adapter.setItems(allItems);

        adapter.setListener(this::handleAction);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        int userId = AppConfig.getUserId(requireContext());
        userViewModel.fetchLoggedInUserDetails(userId);
        userViewModel.getLoggedInUserData().observe(getViewLifecycleOwner(), this::bindAccount);

        getParentFragmentManager().setFragmentResultListener(
                AppConfig.PROFILE_UPDATED_KEY,
                getViewLifecycleOwner(),
                (requestKey, result) -> userViewModel.fetchLoggedInUserDetails(userId)
        );

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /** Puts my photo, name and username in the account row at the top of settings. */
    private void bindAccount(UserResponse response) {
        if (response == null || response.getData() == null) {
            return;
        }
        adapter.setAccountInfo(
                response.getData().getImage_uri(),
                response.getData().getFullname(),
                response.getData().getUsername()
        );
    }

    private List<SettingsItem> buildItems() {
        List<SettingsItem> items = new ArrayList<>();
        items.add(SettingsItem.account());
        items.add(SettingsItem.section(getString(R.string.settings_section_how_you_use)));
        items.add(SettingsItem.nav(getString(R.string.settings_edit_profile), getString(R.string.settings_edit_profile_sub), SettingsItem.Action.EDIT_PROFILE));
        items.add(SettingsItem.nav(getString(R.string.settings_notifications), getString(R.string.settings_notifications_sub), SettingsItem.Action.NOTIFICATIONS));
        items.add(SettingsItem.section(getString(R.string.settings_section_who_can_see)));
        items.add(SettingsItem.nav(getString(R.string.settings_privacy), getString(R.string.settings_privacy_sub), SettingsItem.Action.PRIVACY));
        items.add(SettingsItem.section(getString(R.string.settings_section_what_you_see)));
        items.add(SettingsItem.nav(getString(R.string.settings_appearance), getString(R.string.settings_appearance_sub), SettingsItem.Action.APPEARANCE));
        items.add(SettingsItem.section(getString(R.string.settings_section_more)));
        items.add(SettingsItem.nav(getString(R.string.settings_security), getString(R.string.settings_security_sub), SettingsItem.Action.SECURITY));
        items.add(SettingsItem.nav(getString(R.string.settings_about), getString(R.string.settings_about_sub), SettingsItem.Action.ABOUT));
        items.add(SettingsItem.nav(getString(R.string.settings_help), getString(R.string.settings_help_sub), SettingsItem.Action.HELP));
        items.add(SettingsItem.logout());
        return items;
    }

    private void filterItems(String query) {
        if (query.isEmpty()) {
            adapter.setItems(allItems);
            return;
        }
        List<SettingsItem> filtered = new ArrayList<>();
        for (SettingsItem item : allItems) {
            if (item.matchesQuery(query)) {
                filtered.add(item);
            }
        }
        if (!filtered.isEmpty() && filtered.get(0).getType() != SettingsItem.TYPE_ACCOUNT) {
            filtered.add(0, SettingsItem.account());
        }
        adapter.setItems(filtered);
    }

    /** Navigates to edit profile, opens a sub-screen, shows help toast, or starts logout confirmation. */
    private void handleAction(SettingsItem.Action action) {
        if (action == SettingsItem.Action.EDIT_PROFILE) {
            NavHelper.openOverlay(this, new EditProfileFragment());
            return;
        }
        if (action == SettingsItem.Action.LOGOUT) {
            confirmLogout();
            return;
        }
        if (action == SettingsItem.Action.HELP) {
            Toast.makeText(getContext(), R.string.settings_help_toast, Toast.LENGTH_SHORT).show();
            return;
        }
        SettingsSubFragment sub = new SettingsSubFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SettingsSubFragment.ARG_SCREEN, action.name());
        sub.setArguments(bundle);
        NavHelper.openOverlay(this, sub);
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireContext())
                .setMessage(R.string.settings_logout_confirm)
                .setPositiveButton(R.string.settings_logout, (d, w) -> logout())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /** Clears session and returns to {@link AuthActivity}. */
    private void logout() {
        AppConfig.clearSession(requireContext());
        Intent intent = new Intent(requireContext(), AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
