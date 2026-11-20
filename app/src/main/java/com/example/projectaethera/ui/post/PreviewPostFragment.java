package com.example.projectaethera.ui.post;

import android.net.Uri;
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
import androidx.viewpager2.widget.ViewPager2;

import com.example.projectaethera.R;
import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.ui.adapter.ViewPagerAdapter;
import com.example.projectaethera.utils.PreviewViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Swipeable preview of selected photo(s) before posting.
 * Next navigates to {@link CreatePostFragment} with the chosen URI(s).
 */
public class PreviewPostFragment extends Fragment {

    private TextView next;
    private TextView pageIndicator;
    private ImageView btnBackImg;
    private ViewPager2 viewPager;
    private List<Uri> imageList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.preview_post_fragment, container, false);
    }

    /**
     * Shows images in a ViewPager, page indicator for multiples, and Next to continue posting.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBackImg = view.findViewById(R.id.btn_back_icon);
        viewPager = view.findViewById(R.id.viewPager_swipe);
        next = view.findViewById(R.id.btn_next);
        pageIndicator = view.findViewById(R.id.preview_page_indicator);

        if (getArguments() != null) {
            if (getArguments().containsKey(AppConfig.IMAGE_LIST_PATH)) {
                imageList = getArguments().getParcelableArrayList(AppConfig.IMAGE_LIST_PATH);
            } else {
                Uri singleUri = getArguments().getParcelable(AppConfig.IMAGE_URI_PATH);
                if (singleUri != null) {
                    imageList.add(singleUri);
                }
            }
        }

        if (imageList != null && !imageList.isEmpty()) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(imageList);
            viewPager.setAdapter(adapter);
            updatePageIndicator(0, imageList.size());

            if (imageList.size() > 1) {
                pageIndicator.setVisibility(View.VISIBLE);
                viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        updatePageIndicator(position, imageList.size());
                    }
                });
            } else {
                pageIndicator.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(getContext(), "no image", Toast.LENGTH_SHORT).show();
        }

        btnBackImg.setOnClickListener(v -> requireActivity().onBackPressed());
        next.setOnClickListener(v -> {
            if (imageList == null || imageList.isEmpty()) {
                return;
            }
            if (imageList.size() == 1) {
                PreviewViewUtils.goToCreatePostView(PreviewPostFragment.this, imageList.get(0));
            } else {
                PreviewViewUtils.goToCreatePostForMultipleImage(PreviewPostFragment.this, imageList);
            }
        });
    }

    private void updatePageIndicator(int index, int total) {
        pageIndicator.setText(getString(R.string.post_preview_page, index + 1, total));
    }
}
