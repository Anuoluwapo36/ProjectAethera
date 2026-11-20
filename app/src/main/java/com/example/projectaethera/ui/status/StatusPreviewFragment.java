package com.example.projectaethera.ui.status;



import android.net.Uri;

import android.os.Bundle;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.EditText;

import android.widget.ImageView;

import android.widget.TextView;

import android.widget.Toast;



import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;



import com.bumptech.glide.Glide;

import com.example.projectaethera.R;

import com.example.projectaethera.config.AppConfig;

import com.example.projectaethera.viewmodel.CreateStatusViewModel;

import com.example.projectaethera.viewmodel.StatusViewModel;



/**
 * Preview and share screen for a new status/story image picked from {@link GalleryFragment}.
 * Uploads through {@link CreateStatusViewModel} and refreshes the home status strip on success.
 */
public class StatusPreviewFragment extends Fragment {



    private Uri imageUri;

    private CreateStatusViewModel createStatusViewModel;



    @Nullable

    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.status_preview_fragment, container, false);

    }



    /**
     * Displays the chosen image, optional caption, and Share button wired to {@link CreateStatusViewModel}.
     */
    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);



        if (getArguments() != null) {

            imageUri = getArguments().getParcelable(AppConfig.IMAGE_URI_PATH);

        }



        ImageView previewImage = view.findViewById(R.id.status_preview_image);

        ImageView back = view.findViewById(R.id.status_preview_back);

        TextView shareBtn = view.findViewById(R.id.status_share_btn);

        EditText captionInput = view.findViewById(R.id.status_caption_input);



        if (imageUri != null) {

            Glide.with(this).load(imageUri).into(previewImage);

        }



        back.setOnClickListener(v -> requireActivity().onBackPressed());



        createStatusViewModel = new ViewModelProvider(this).get(CreateStatusViewModel.class);

        createStatusViewModel.getSuccess().observe(getViewLifecycleOwner(), success -> {

            if (Boolean.TRUE.equals(success)) {

                int userId = AppConfig.getUserId(requireContext());

                if (userId > 0) {

                    new ViewModelProvider(requireActivity()).get(StatusViewModel.class).loadStatusFeed(userId);

                }

                Toast.makeText(getContext(), R.string.status_posted, Toast.LENGTH_SHORT).show();

                requireActivity().getSupportFragmentManager().popBackStack();

                requireActivity().getSupportFragmentManager().popBackStack();

            }

        });

        createStatusViewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {

            if (message != null) {

                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

            }

        });

        createStatusViewModel.getUploading().observe(getViewLifecycleOwner(), uploading -> {

            shareBtn.setEnabled(!Boolean.TRUE.equals(uploading));

            shareBtn.setAlpha(Boolean.TRUE.equals(uploading) ? 0.5f : 1f);

        });



        shareBtn.setOnClickListener(v -> {

            int userId = AppConfig.getUserId(requireContext());

            createStatusViewModel.uploadStatus(

                    requireContext(),

                    userId,

                    imageUri,

                    captionInput.getText().toString().trim()

            );

        });

    }

}

