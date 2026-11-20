package com.example.projectaethera.ui.post;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.projectaethera.R;
import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.ui.widgets.CameraPressCircleView;
import com.example.projectaethera.utils.GalleryUtil;
import com.example.projectaethera.utils.NavHelper;
import com.example.projectaethera.utils.PermissionUtils;
import com.example.projectaethera.utils.PreviewViewUtils;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;

/**
 * Add Post tab: live camera preview, capture button, and shortcut to the gallery.
 * Lives in {@code camera_host} on {@link com.example.projectaethera.ui.MainActivity}.
 * After capture, navigation continues through preview and {@link CreatePostFragment}.
 */
public class AddPostFragment extends Fragment {

    private CameraPressCircleView captureBtn;
    private RelativeLayout cameraContainer;
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ProcessCameraProvider cameraProvider;
    private ImageCapture imageCapture;
    private TextView backActivity;
    private ImageView btnGallery;

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startCamera();
                } else {
                    Toast.makeText(getContext(), "Camera permission required", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> STORAGE_PERM =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    loadFirstGalleryThumbnail();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.post_fragment, container, false);
    }

    /**
     * Wires capture, back, and gallery buttons; requests storage permission for the gallery thumbnail.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        captureBtn = view.findViewById(R.id.cam_id);
        cameraContainer = view.findViewById(R.id.cameraLayout);
        previewView = view.findViewById(R.id.previewView);
        btnGallery = view.findViewById(R.id.btn_gallery);
        backActivity = view.findViewById(R.id.btn_back);

        captureBtn.setOnClickListener(v -> takePhoto());
        backActivity.setOnClickListener(v -> requireActivity().onBackPressed());

        galleryOpen();
        loadFirstGalleryThumbnail();
    }

    /** Starts the camera when this tab is visible and permission is granted. */
    @Override
    public void onResume() {
        super.onResume();
        if (isVisible() && !isHidden()) {
            CheckCameraPermission();
        }
    }

    @Override
    public void onPause() {
        releaseCamera();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        releaseCamera();
        previewView = null;
        super.onDestroyView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            releaseCamera();
        } else if (isResumed() && isVisible()) {
            CheckCameraPermission();
        }
    }

    /** Unbinds CameraX so other tabs or overlays can use the camera. Called from MainActivity too. */
    public void releaseCamera() {
        imageCapture = null;
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }

    /** Requests camera permission if needed, then calls {@link #startCamera()}. */
    private void CheckCameraPermission() {
        if (!isAdded() || isHidden() || !isVisible()) {
            return;
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
            loadFirstGalleryThumbnail();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /** Binds CameraX preview and {@link ImageCapture} to the lifecycle of this fragment. */
    private void startCamera() {
        if (!isAdded() || isHidden() || !isVisible() || previewView == null) {
            return;
        }

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                if (!isAdded() || isHidden() || !isVisible() || previewView == null) {
                    return;
                }

                cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageCapture);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    /** Saves a JPEG to cache and opens the preview overlay via {@link com.example.projectaethera.utils.PreviewViewUtils}. */
    private void takePhoto() {
        if (imageCapture == null) {
            Toast.makeText(getContext(), "Camera not ready", Toast.LENGTH_SHORT).show();
            CheckCameraPermission();
            return;
        }

        try {
            File photoFile = new File(requireContext().getCacheDir(), "photo.jpg");
            ImageCapture.OutputFileOptions options =
                    new ImageCapture.OutputFileOptions.Builder(photoFile).build();

            imageCapture.takePicture(
                    options,
                    ContextCompat.getMainExecutor(requireContext()),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            PreviewViewUtils.previewReview(AddPostFragment.this, Uri.fromFile(photoFile));
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException e) {
                            Toast.makeText(getContext(), "Photo failed", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        } catch (Exception e) {
            Toast.makeText(getContext(), "Photo failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void galleryOpen() {
        btnGallery.setOnClickListener(v -> NavHelper.openOverlay(this, new GalleryFragment()));
    }

    private void loadFirstGalleryThumbnail() {
        if (!PermissionUtils.hasPermission(requireContext(), AppConfig.PHOTO_PERMISSION)) {
            STORAGE_PERM.launch(AppConfig.PHOTO_PERMISSION);
            return;
        }

        Uri firstImage = GalleryUtil.getFirstImage(requireContext());
        if (firstImage != null) {
            Glide.with(this)
                    .load(firstImage)
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(btnGallery);
        } else {
            btnGallery.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
}
