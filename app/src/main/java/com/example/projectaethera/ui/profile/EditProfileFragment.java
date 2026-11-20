package com.example.projectaethera.ui.profile;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.projectaethera.R;
import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.model.remote.ProfileRequest;
import com.example.projectaethera.model.remote.UserResponse;
import com.example.projectaethera.ui.post.GalleryFragment;
import com.example.projectaethera.utils.ImageUrlUtils;
import com.example.projectaethera.utils.NavHelper;
import com.example.projectaethera.viewmodel.EditProfileViewModel;
import com.example.projectaethera.viewmodel.UserViewModel;
import com.google.android.material.snackbar.Snackbar;

/**
 * Screen where the logged-in user edits username, bio, profession and profile photo.
 * Always uses {@link UserViewModel#getLoggedInUserData()} — never the "viewed user" bucket.
 */
public class EditProfileFragment extends Fragment {

    private AppCompatEditText usernameEdit, fullNameEdit, bioEdit, professionEdit;
    private ImageView back_btn;
    private EditProfileViewModel editProfileViewModel;
    private UserViewModel userViewModel;
    private TextView imageBtn;
    private int userId;
    private Button save_btn;

    private String selectedProfileImagePath;
    private ImageView imageDp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_edit_fragment, container, false);
    }

    /**
     * Binds edit fields, save/upload actions, and observers for {@link EditProfileViewModel}.
     * Listens for a profile photo picked from {@link GalleryFragment}.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userId = AppConfig.getUserId(requireContext());

        usernameEdit = view.findViewById(R.id.et_username);
        fullNameEdit = view.findViewById(R.id.et_fullname);
        bioEdit = view.findViewById(R.id.et_bio);
        professionEdit = view.findViewById(R.id.et_profession);
        back_btn = view.findViewById(R.id.back_press);
        save_btn = view.findViewById(R.id.btn_save);
        imageBtn = view.findViewById(R.id.edit_dp_image);
        imageDp = view.findViewById(R.id.imageDp);

        back_btn.setOnClickListener(v -> requireActivity().onBackPressed());
        editProfileViewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        save_btn.setOnClickListener(v -> saveProfile());
        loadCurrentDate();

        setUpLoadButton();
        observerProfileUpload();
        observerProfilePictureUpdate();

        getParentFragmentManager().setFragmentResultListener(
                "PROFILE_IMAGE_RESULT",
                getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    selectedProfileImagePath = bundle.getString("SELECTED_PROFILE_IMAGE");
                    if (selectedProfileImagePath != null) {
                        Glide.with(requireContext())
                                .load(Uri.parse(selectedProfileImagePath))
                                .circleCrop()
                                .into(imageDp);
                        imageBtn.setText("upload profile");
                        imageBtn.setTextColor(getResources().getColor(R.color.forest_green));
                    }
                }
        );
    }

    private void setUpLoadButton() {
        imageBtn.setOnClickListener(v -> {
            if (selectedProfileImagePath != null) {
                editProfileViewModel.updateProfile(requireContext(), userId, selectedProfileImagePath);
            } else {
                openProfileGallery();
            }
        });
    }

    /** Opens gallery in single-select mode to choose a new profile picture. */
    private void openProfileGallery() {
        GalleryFragment galleryFragment = new GalleryFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("FROM_PROFILE", true);
        galleryFragment.setArguments(bundle);
        NavHelper.openOverlay(this, galleryFragment);
    }

    /** Builds a {@link ProfileRequest} from the form and sends it to {@link EditProfileViewModel}. */
    private void saveProfile() {
        String username = usernameEdit.getText().toString().trim();
        String fullname = fullNameEdit.getText().toString().trim();
        String bio = bioEdit.getText().toString().trim();
        String profession = professionEdit.getText().toString().trim();

        if (username.isEmpty()) {
            Snackbar.make(requireView(), "Username cannot be empty", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.parseColor("#FF8C00"))
                    .show();
            return;
        }

        ProfileRequest request = new ProfileRequest();
        request.setUsername(username);
        request.setFullname(fullname);
        request.setBio(bio);
        request.setProfession(profession);

        if (userId != 0) {
            editProfileViewModel.editProfile(userId, request);
        }
    }

    /**
     * Tells Home and Profile tab to refresh after a successful save.
     * Uses logged-in user bucket only.
     */
    private void notifyProfileUpdated() {
        UserViewModel sharedUserViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        UserResponse updated = editProfileViewModel.getUpdatedUser().getValue();
        sharedUserViewModel.notifyProfileUpdated(userId, updated);
        requireActivity().getSupportFragmentManager()
                .setFragmentResult(AppConfig.PROFILE_UPDATED_KEY, new Bundle());
        editProfileViewModel.clearUpdateFlags();
    }

    private void observerProfileUpload() {
        editProfileViewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), isSuccessful -> {
            if (Boolean.TRUE.equals(isSuccessful)) {
                notifyProfileUpdated();
                Snackbar.make(requireView(), "profile updated", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.chocolate_brown))
                        .show();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (isAdded() && getActivity() != null) {
                        requireActivity().onBackPressed();
                    }
                }, 1200);
            }
        });

        editProfileViewModel.getUpdateErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Snackbar.make(requireView(), error, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.parseColor("#FF8C00"))
                        .show();
            }
        });
    }

    public void observerProfilePictureUpdate() {
        editProfileViewModel.getUpdateProfileSuccess().observe(getViewLifecycleOwner(), isSuccessful -> {
            if (Boolean.TRUE.equals(isSuccessful)) {
                notifyProfileUpdated();
                Snackbar.make(requireView(), "Profile updated successfully!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.teal_700))
                        .show();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (isAdded() && getActivity() != null) {
                        requireActivity().onBackPressed();
                    }
                }, 1200);
            }
        });

        editProfileViewModel.getUpdateProfileErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Snackbar.make(requireView(), error, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.parseColor("#FF8C00"))
                        .show();
            }
        });
    }

    /** Loads my current profile into the edit fields from the logged-in user bucket. */
    private void loadCurrentDate() {
        userViewModel.fetchLoggedInUserDetails(userId);
        userViewModel.getLoggedInUserData().observe(getViewLifecycleOwner(), userResponse -> {
            if (userResponse != null && userResponse.getData() != null) {
                usernameEdit.setText(userResponse.getData().getUsername());
                fullNameEdit.setText(userResponse.getData().getFullname());
                bioEdit.setText(userResponse.getData().getBio());
                professionEdit.setText(userResponse.getData().getProfession());

                if (selectedProfileImagePath == null) {
                    String imageUri = userResponse.getData().getImage_uri();
                    String profileImage = ImageUrlUtils.toFullUrl(imageUri);
                    Glide.with(requireContext())
                            .load(profileImage)
                            .placeholder(AppConfig.DEFAULT_PROFILE_IMAGE)
                            .fallback(AppConfig.DEFAULT_PROFILE_IMAGE)
                            .circleCrop()
                            .into(imageDp);
                }
            }
        });
    }
}
