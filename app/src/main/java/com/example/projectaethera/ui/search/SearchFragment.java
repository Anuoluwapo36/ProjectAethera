package com.example.projectaethera.ui.search;



import android.os.Bundle;

import android.text.Editable;

import android.text.TextWatcher;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;

import android.widget.EditText;

import android.widget.TextView;



import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;



import com.example.projectaethera.R;

import com.example.projectaethera.config.AppConfig;

import com.example.projectaethera.model.entity.UserProfile;

import com.example.projectaethera.ui.MainActivity;

import com.example.projectaethera.ui.adapter.SearchAdapter;

import com.example.projectaethera.ui.profile.ProfileFragment;

import com.example.projectaethera.utils.NavHelper;

import com.example.projectaethera.viewmodel.SearchingViewModel;



import java.util.ArrayList;



/**
 * Search tab: real-time user lookup as the user types.
 * Uses {@link SearchingViewModel}; tapping a result opens {@link ProfileFragment} as an overlay.
 * Cancel returns to the Home tab via {@link MainActivity#selectTab(int)}.
 */
public class SearchFragment extends Fragment {



    private RecyclerView recyclerView;

    private SearchAdapter searchAdapter;

    private SearchingViewModel searchingViewModel;

    private EditText searchInput;

    private TextView statusText;

    private int userId;



    @Nullable

    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.search_fragment, container, false);

    }



    /**
     * Wires the search field, RecyclerView, cancel button, and {@link SearchingViewModel} observers.
     */
    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);



        recyclerView = view.findViewById(R.id.search_recyclerview);

        searchInput = view.findViewById(R.id.search_edittext);

        statusText = view.findViewById(R.id.status_text);

        TextView cancelBack = view.findViewById(R.id.cancelBackPress);



        userId = AppConfig.getUserId(requireContext());



        searchAdapter = new SearchAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        recyclerView.setAdapter(searchAdapter);



        searchingViewModel = new ViewModelProvider(this).get(SearchingViewModel.class);



        cancelBack.setOnClickListener(v -> {

            searchInput.setText("");

            hideKeyboard();

            if (requireActivity() instanceof MainActivity) {

                ((MainActivity) requireActivity()).selectTab(R.id.nav_home);

            } else {

                requireActivity().onBackPressed();

            }

        });



        realTimeSearch();

        observeSearchUser();

    }



    /**
     * Focuses the search box and opens the keyboard when the Search tab becomes visible.
     */
    @Override

    public void onResume() {

        super.onResume();

        if (!isVisible() || isHidden() || searchInput == null) {

            return;

        }

        searchInput.post(() -> {

            if (isVisible() && !isHidden()) {

                searchInput.requestFocus();

                showKeyboard();

            }

        });

    }



    @Override

    public void onPause() {

        hideKeyboard();

        super.onPause();

    }



    private void showKeyboard() {

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);

        if (imm != null) {

            imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);

        }

    }



    private void hideKeyboard() {

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);

        if (imm != null && searchInput != null) {

            imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);

        }

    }



    /** Calls the search API on each text change via {@link SearchingViewModel#searchUser}. */
    private void realTimeSearch() {

        searchInput.addTextChangedListener(new TextWatcher() {

            @Override

            public void afterTextChanged(Editable s) {

            }



            @Override

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }



            @Override

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String query = s.toString().trim();

                if (!query.isEmpty()) {

                    searchingViewModel.searchUser(userId, query);

                } else {

                    searchAdapter.setUserList(new ArrayList<>());

                    statusText.setText("");

                }

            }

        });

    }



    /** Updates the result list and opens {@link ProfileFragment} when a user row is tapped. */
    private void observeSearchUser() {

        searchingViewModel.getSearchResult().observe(getViewLifecycleOwner(), searchResponse -> {

            if (searchResponse != null) {

                searchAdapter.setUserList(searchResponse.getData());

                statusText.setText(searchResponse.getMessage());

            }

        });



        searchingViewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> statusText.setText(msg));



        searchAdapter.setOnUserClickListener(user -> {

            ProfileFragment profileFragment = new ProfileFragment();

            Bundle bundle = new Bundle();

            bundle.putInt("userId", user.getUserId());

            profileFragment.setArguments(bundle);

            NavHelper.openOverlay(this, profileFragment);

        });

    }

}

