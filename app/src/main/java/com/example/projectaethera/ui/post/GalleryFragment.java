package com.example.projectaethera.ui.post;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectaethera.R;
import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.ui.adapter.GalleryAdapter;
import com.example.projectaethera.utils.GalleryUtil;
import com.example.projectaethera.utils.PermissionUtils;
import com.example.projectaethera.utils.PreviewViewUtils;

import java.util.List;

/**
 * Device photo picker grid. Used from Add Post, status creation, and edit-profile flows.
 * Supports multi-select for posts, single-tap for status/profile depending on fragment arguments.
 */
public class GalleryFragment extends Fragment implements GalleryAdapter.GalleryListener {

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private View btnNext;
    private View btnBack;
    private LinearLayout bottomBar;
    private TextView selectedCountText;
    private List<Uri> imageList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gallery_fragment, container, false);
    }

    /**
     * Loads device images, sets up back/next bar, and switches behavior for status or profile mode.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.gallery_recycler_container);
        btnNext = view.findViewById(R.id.next_id);
        btnBack = view.findViewById(R.id.back_X);
        bottomBar = view.findViewById(R.id.gallery_bottom_bar);
        selectedCountText = view.findViewById(R.id.gallery_selected_count);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        sendBack();
        sendNext();

        if (getArguments() != null && getArguments().getBoolean("FROM_STATUS", false)) {
            bottomBar.setVisibility(View.GONE);
        }

        if (PermissionUtils.hasPermission(getContext(), AppConfig.PHOTO_PERMISSION)) {
            loadImagesFromPhone();
        } else {
            askPhotoPermission();
        }

        if (getArguments() != null && getArguments().getBoolean("FROM_PROFILE", false)) {
            setupProfileGalleryMode();
        }
    }

    /** Single-select mode: one tap picks a profile photo and returns result to {@link EditProfileFragment}. */
    private void setupProfileGalleryMode() {
        if (galleryAdapter == null) {
            return;
        }
        galleryAdapter.setMultiSelectMode(false);
        bottomBar.setVisibility(View.GONE);
        galleryAdapter.setListener(new GalleryAdapter.GalleryListener() {
            @Override
            public void onSingleTap(Uri uri) {
                Bundle bundle = new Bundle();
                bundle.putString("SELECTED_PROFILE_IMAGE", uri.toString());
                getParentFragmentManager().setFragmentResult("PROFILE_IMAGE_RESULT", bundle);
                getParentFragmentManager().popBackStack();
            }

            @Override
            public void onLongPress() {
            }
        });
    }

    private void askPhotoPermission() {
        requestPermissionLauncher.launch(AppConfig.PHOTO_PERMISSION);
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    loadImagesFromPhone();
                    if (getArguments() != null && getArguments().getBoolean("FROM_PROFILE", false)) {
                        setupProfileGalleryMode();
                    }
                } else {
                    Toast.makeText(getContext(), "Permission needed to show photos", Toast.LENGTH_SHORT).show();
                }
            });

    private void sendBack() {
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void sendNext() {
        btnNext.setOnClickListener(v -> {
            if (galleryAdapter == null) {
                return;
            }
            List<Uri> selectedImages = galleryAdapter.getSelectedImage();
            if (selectedImages == null || selectedImages.isEmpty()) {
                Toast.makeText(getContext(), R.string.post_select_image, Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedImages.size() == 1) {
                boolean fromStatus = getArguments() != null
                        && getArguments().getBoolean("FROM_STATUS", false);
                if (fromStatus) {
                    PreviewViewUtils.goToCreateStatusView(GalleryFragment.this, selectedImages.get(0));
                } else {
                    PreviewViewUtils.previewReview(GalleryFragment.this, selectedImages.get(0));
                }
            } else {
                PreviewViewUtils.previewReviewMultiple(GalleryFragment.this, selectedImages);
            }
        });
    }

    /** Reads all images from the device and attaches {@link GalleryAdapter}. */
    private void loadImagesFromPhone() {
        imageList = GalleryUtil.getAllImages(getContext());
        galleryAdapter = new GalleryAdapter(imageList);
        galleryAdapter.setListener(this);
        galleryAdapter.setSelectionChangeListener(this::updateSelectionBar);
        recyclerView.setAdapter(galleryAdapter);
    }

    private void updateSelectionBar(int count) {
        if (bottomBar == null || selectedCountText == null) {
            return;
        }
        if (getArguments() != null && getArguments().getBoolean("FROM_STATUS", false)) {
            return;
        }
        if (count > 0) {
            bottomBar.setVisibility(View.VISIBLE);
            selectedCountText.setText(getString(R.string.post_gallery_selected, count));
        } else {
            bottomBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSingleTap(Uri uri) {
        if (getArguments() != null && getArguments().getBoolean("FROM_STATUS", false)) {
            PreviewViewUtils.goToCreateStatusView(this, uri);
            return;
        }
        PreviewViewUtils.previewReview(this, uri);
    }

    @Override
    public void onLongPress() {
        galleryAdapter.setMultiSelectMode(true);
        bottomBar.setVisibility(View.VISIBLE);
        updateSelectionBar(galleryAdapter.getSelectedImage().size());
    }
}
