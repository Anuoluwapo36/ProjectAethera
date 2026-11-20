package com.example.projectaethera.ui.post;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectaethera.R;
import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.ui.MainActivity;
import com.example.projectaethera.ui.adapter.CreatePostMultipleImageAdapter;
import com.example.projectaethera.ui.widgets.NonScrollableGridLayoutManager;
import com.example.projectaethera.viewmodel.CreatePostViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Final step of posting: caption entry and upload for one or many images.
 * Receives image URIs from {@link PreviewPostFragment}; uploads via {@link CreatePostViewModel}.
 * On success {@link com.example.projectaethera.ui.MainActivity#returnHomeAfterPost()} runs.
 */
public class CreatePostFragment extends Fragment {

    private ImageView backActivity, postImage;
    private EditText caption, textCaptionMultiId;
    private TextView upload;
    private TextView multiPhotoCount;
    private CreatePostViewModel postViewModel;
    private FrameLayout loadingOverLay;
    private Uri singleImageUri;
    private List<Uri> imageList = new ArrayList<>();
    private RelativeLayout middleBar;
    private View multipleImageLayout;
    private RecyclerView gridPost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_post_fragment, container, false);
    }

    /**
     * Reads image arguments, shows single or grid layout, and hooks up the Post button.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postImage = view.findViewById(R.id.postImage);
        backActivity = view.findViewById(R.id.btn_back);
        caption = view.findViewById(R.id.text_caption_id);
        upload = view.findViewById(R.id.btn_post);
        loadingOverLay = view.findViewById(R.id.loading_overlay);
        middleBar = view.findViewById(R.id.middle_bar);
        multipleImageLayout = view.findViewById(R.id.multiple_image_layout);
        textCaptionMultiId = view.findViewById(R.id.multi_img_caption);
        multiPhotoCount = view.findViewById(R.id.multi_photo_count);
        gridPost = view.findViewById(R.id.gridPostImage);

        postViewModel = new ViewModelProvider(this).get(CreatePostViewModel.class);

        backActivity.setOnClickListener(v -> requireActivity().onBackPressed());
        receiveContent();
        observerPost();
        setUpButtonClick();
    }

    /** Parses single-image or multi-image bundle args and configures the UI layout. */
    private void receiveContent() {
        singleImageUri = null;
        imageList.clear();

        if (getArguments() != null) {
            imageList = getArguments().getParcelableArrayList(AppConfig.IMAGE_LIST_PATH);

            if (imageList != null && !imageList.isEmpty()) {
                middleBar.setVisibility(View.GONE);
                multipleImageLayout.setVisibility(View.VISIBLE);
                multiPhotoCount.setText(getString(R.string.post_photos_count, imageList.size()));

                NonScrollableGridLayoutManager layoutManager = new NonScrollableGridLayoutManager(getContext(), 3);
                gridPost.setLayoutManager(layoutManager);
                gridPost.setOverScrollMode(View.OVER_SCROLL_NEVER);
                gridPost.setNestedScrollingEnabled(false);
                CreatePostMultipleImageAdapter adapter = new CreatePostMultipleImageAdapter(imageList);
                gridPost.setAdapter(adapter);
            } else {
                singleImageUri = getArguments().getParcelable(AppConfig.IMAGE_URI_PATH);
                if (singleImageUri != null) {
                    postImage.setImageURI(singleImageUri);
                    middleBar.setVisibility(View.VISIBLE);
                    multipleImageLayout.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "no image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setUpButtonClick() {
        upload.setOnClickListener(v -> {
            try {
                int userId = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    userId = AppConfig.getUserId(requireContext());
                }

                if (userId <= 0) {
                    Toast.makeText(requireContext(), "Login first!", Toast.LENGTH_LONG).show();
                    return;
                }

                loadingOverLay.setVisibility(View.VISIBLE);
                upload.setEnabled(false);

                if (multipleImageLayout.getVisibility() == View.VISIBLE) {
                    String cap = textCaptionMultiId.getText().toString().trim();
                    postViewModel.postCreated(requireContext(), userId, cap, imageList);
                } else {
                    String cap = caption.getText().toString().trim();
                    postViewModel.postCreated(requireContext(), userId, cap, singleImageUri);
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                loadingOverLay.setVisibility(View.GONE);
                upload.setEnabled(true);
            }
        });
    }

    /** Observes upload success/error from {@link CreatePostViewModel} and returns home on success. */
    private void observerPost() {
        postViewModel.getPostSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            if (!Boolean.TRUE.equals(isSuccess) || !isAdded()) {
                if (!Boolean.TRUE.equals(isSuccess) && loadingOverLay != null) {
                    loadingOverLay.setVisibility(View.GONE);
                    upload.setEnabled(true);
                }
                return;
            }

            if (loadingOverLay != null) {
                loadingOverLay.setVisibility(View.GONE);
            }
            upload.setEnabled(true);

            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).returnHomeAfterPost();
            }
        });

        postViewModel.getPostErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (!isAdded()) {
                return;
            }
            loadingOverLay.setVisibility(View.GONE);
            upload.setEnabled(true);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }
}
