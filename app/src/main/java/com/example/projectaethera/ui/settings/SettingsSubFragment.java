package com.example.projectaethera.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.projectaethera.R;
import com.example.projectaethera.config.SettingsPrefs;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Detail screen for a single settings category (notifications, appearance, privacy, etc.).
 * Opened as an overlay from {@link SettingsFragment}. Reads {@link #ARG_SCREEN} to decide
 * which toggles or text to show; persists values through {@link SettingsPrefs}.
 */
public class SettingsSubFragment extends Fragment {

    public static final String ARG_SCREEN = "screen";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_sub_fragment, container, false);
    }

    /**
     * Reads the screen key from arguments and builds the matching settings content dynamically.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView back = view.findViewById(R.id.settings_sub_back);
        TextView title = view.findViewById(R.id.settings_sub_title);
        LinearLayout content = view.findViewById(R.id.settings_sub_content);

        back.setOnClickListener(v -> requireActivity().onBackPressed());

        String screen = getArguments() != null
                ? getArguments().getString(ARG_SCREEN, "")
                : "";

        switch (screen) {
            case "NOTIFICATIONS":
                title.setText(R.string.settings_notifications_title);
                buildNotifications(content);
                break;
            case "APPEARANCE":
                title.setText(R.string.settings_appearance_title);
                buildAppearance(content);
                break;
            case "PRIVACY":
                title.setText(R.string.settings_privacy_title);
                buildPrivacy(content);
                break;
            case "ABOUT":
                title.setText(R.string.settings_about_title);
                buildAbout(content);
                break;
            case "SECURITY":
                title.setText(R.string.settings_security);
                buildSecurity(content);
                break;
            default:
                title.setText(R.string.settings_title);
                break;
        }
    }

    /** Inflates push/like/comment/follow notification toggles backed by {@link SettingsPrefs}. */
    private void buildNotifications(LinearLayout content) {
        addSwitch(content, R.string.settings_push, R.string.settings_push_sub,
                SettingsPrefs.isNotifyPush(requireContext()),
                (checked) -> SettingsPrefs.setNotifyPush(requireContext(), checked));
        addDivider(content);
        addSwitch(content, R.string.settings_notify_likes, R.string.settings_notify_likes_sub,
                SettingsPrefs.isNotifyLikes(requireContext()),
                (checked) -> SettingsPrefs.setNotifyLikes(requireContext(), checked));
        addSwitch(content, R.string.settings_notify_comments, R.string.settings_notify_comments_sub,
                SettingsPrefs.isNotifyComments(requireContext()),
                (checked) -> SettingsPrefs.setNotifyComments(requireContext(), checked));
        addSwitch(content, R.string.settings_notify_follows, R.string.settings_notify_follows_sub,
                SettingsPrefs.isNotifyFollows(requireContext()),
                (checked) -> SettingsPrefs.setNotifyFollows(requireContext(), checked));
    }

    /** Dark mode switch; recreates the activity so the theme applies immediately. */
    private void buildAppearance(LinearLayout content) {
        addSwitch(content, R.string.settings_dark_mode, R.string.settings_dark_mode_sub,
                SettingsPrefs.isDarkMode(requireContext()),
                (checked) -> {
                    SettingsPrefs.setDarkMode(requireContext(), checked);
                    SettingsPrefs.applyTheme(requireContext());
                    requireActivity().recreate();
                });
    }

    private void buildPrivacy(LinearLayout content) {
        TextView text = new TextView(getContext());
        text.setText(R.string.settings_privacy_coming);
        text.setTextColor(getResources().getColor(R.color.settings_subtitle_text, null));
        text.setTextSize(15f);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        text.setPadding(pad, pad * 2, pad, pad);
        content.addView(text);
    }

    private void buildAbout(LinearLayout content) {
        TextView text = new TextView(getContext());
        text.setText(R.string.settings_about_body);
        text.setTextColor(getResources().getColor(R.color.midnight_black, null));
        text.setTextSize(16f);
        text.setLineSpacing(0f, 1.3f);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        text.setPadding(pad, pad * 2, pad, pad);
        content.addView(text);
    }

    private void buildSecurity(LinearLayout content) {
        View row = LayoutInflater.from(getContext())
                .inflate(R.layout.item_settings_nav, content, false);
        TextView navTitle = row.findViewById(R.id.settings_nav_title);
        TextView navSubtitle = row.findViewById(R.id.settings_nav_subtitle);
        navTitle.setText(R.string.settings_security);
        navSubtitle.setVisibility(View.VISIBLE);
        navSubtitle.setText(R.string.settings_security_sub);
        row.setOnClickListener(v ->
                Toast.makeText(getContext(), R.string.settings_help_toast, Toast.LENGTH_SHORT).show()
        );
        content.addView(row);
    }

    private interface SwitchListener {
        void onChanged(boolean checked);
    }

    private void addSwitch(LinearLayout content, int titleRes, int subtitleRes, boolean checked, SwitchListener listener) {
        View row = LayoutInflater.from(getContext())
                .inflate(R.layout.item_settings_switch, content, false);
        TextView title = row.findViewById(R.id.settings_switch_title);
        TextView subtitle = row.findViewById(R.id.settings_switch_subtitle);
        SwitchMaterial switchView = row.findViewById(R.id.settings_switch);

        title.setText(titleRes);
        subtitle.setText(subtitleRes);
        switchView.setChecked(checked);
        switchView.setOnCheckedChangeListener((button, isChecked) -> listener.onChanged(isChecked));
        content.addView(row);
    }

    private void addDivider(LinearLayout content) {
        View divider = new View(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        int margin = (int) (16 * getResources().getDisplayMetrics().density);
        params.setMargins(margin, 0, margin, 0);
        divider.setLayoutParams(params);
        divider.setBackgroundColor(getResources().getColor(R.color.ig_following_border, null));
        content.addView(divider);
    }
}
