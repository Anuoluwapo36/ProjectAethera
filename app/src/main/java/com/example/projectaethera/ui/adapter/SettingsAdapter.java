package com.example.projectaethera.ui.adapter;



import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.TextView;



import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;



import com.bumptech.glide.Glide;

import com.example.projectaethera.R;

import com.example.projectaethera.config.AppConfig;

import com.example.projectaethera.utils.ImageUrlUtils;

import com.example.projectaethera.model.SettingsItem;

import com.example.projectaethera.ui.widgets.ProfileCircleWidgets;



import java.util.ArrayList;

import java.util.List;



/**

 * RecyclerView adapter for the settings screen with mixed row types.

 * Shows account header, section titles, navigation rows, and a logout row.

 */

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    /**

     * Called when the user taps a settings row that has an action (nav item, account, or logout).

     */

    public interface Listener {

        void onAction(SettingsItem.Action action);

    }



    private final List<SettingsItem> items = new ArrayList<>();

    private Listener listener;

    private String avatarUrl;

    private String displayName;

    private String username;



    /**

     * Sets the listener for settings menu actions.

     *

     * @param listener callback with the action enum for the tapped row

     */

    public void setListener(Listener listener) {

        this.listener = listener;

    }



    /**

     * Updates the account header row with the logged-in user's info.

     *

     * @param avatarUrl   server path to profile photo

     * @param displayName full name shown on the header

     * @param username    @username shown under the name

     */

    public void setAccountInfo(String avatarUrl, String displayName, String username) {

        this.avatarUrl = avatarUrl;

        this.displayName = displayName;

        this.username = username;

        notifyDataSetChanged();

    }



    /**

     * Replaces all settings rows and refreshes the list.

     *

     * @param newItems ordered list of account, section, nav, and logout items

     */

    public void setItems(List<SettingsItem> newItems) {

        items.clear();

        if (newItems != null) {

            items.addAll(newItems);

        }

        notifyDataSetChanged();

    }



    /**

     * Picks the ViewHolder type from each SettingsItem type constant.

     */

    @Override

    public int getItemViewType(int position) {

        return items.get(position).getType();

    }



    /**

     * Inflates the layout matching the row type (account, section, nav, or logout).

     */

    @NonNull

    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == SettingsItem.TYPE_ACCOUNT) {

            return new AccountHolder(inflater.inflate(R.layout.item_settings_account, parent, false));

        }

        if (viewType == SettingsItem.TYPE_SECTION) {

            return new SectionHolder(inflater.inflate(R.layout.item_settings_section, parent, false));

        }

        if (viewType == SettingsItem.TYPE_LOGOUT) {

            return new LogoutHolder(inflater.inflate(R.layout.item_settings_logout, parent, false));

        }

        return new NavHolder(inflater.inflate(R.layout.item_settings_nav, parent, false));

    }



    /**

     * Binds title, subtitle, avatar, and click handlers depending on the row type.

     */

    @Override

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        SettingsItem item = items.get(position);

        if (holder instanceof AccountHolder) {

            bindAccount((AccountHolder) holder);

        } else if (holder instanceof SectionHolder) {

            ((SectionHolder) holder).title.setText(item.getTitle());

        } else if (holder instanceof NavHolder) {

            NavHolder nav = (NavHolder) holder;

            nav.title.setText(item.getTitle());

            if (item.getSubtitle() != null && !item.getSubtitle().isEmpty()) {

                nav.subtitle.setVisibility(View.VISIBLE);

                nav.subtitle.setText(item.getSubtitle());

            } else {

                nav.subtitle.setVisibility(View.GONE);

            }

            nav.itemView.setOnClickListener(v -> {

                if (listener != null && item.getAction() != null) {

                    listener.onAction(item.getAction());

                }

            });

        } else if (holder instanceof LogoutHolder) {

            holder.itemView.setOnClickListener(v -> {

                if (listener != null) {

                    listener.onAction(SettingsItem.Action.LOGOUT);

                }

            });

        }

    }



    private void bindAccount(AccountHolder holder) {

        holder.name.setText(displayName != null ? displayName : "");

        holder.username.setText(username != null ? "@" + username : "");

        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {

                listener.onAction(SettingsItem.Action.EDIT_PROFILE);

            }

        });

        if (avatarUrl != null && !avatarUrl.isEmpty()) {

            Glide.with(holder.itemView.getContext())

                    .load(ImageUrlUtils.toFullUrl(avatarUrl))

                    .placeholder(AppConfig.DEFAULT_PROFILE_IMAGE)

                    .circleCrop()

                    .into(holder.avatar);

        } else {

            holder.avatar.setImageResource(AppConfig.DEFAULT_PROFILE_IMAGE);

        }

    }



    /**

     * Returns how many settings rows are configured.

     */

    @Override

    public int getItemCount() {

        return items.size();

    }



    /**

     * ViewHolder for the account summary row at the top of settings.

     */

    static class AccountHolder extends RecyclerView.ViewHolder {

        ProfileCircleWidgets avatar;

        TextView name;

        TextView username;



        AccountHolder(@NonNull View itemView) {

            super(itemView);

            avatar = itemView.findViewById(R.id.settings_account_avatar);

            name = itemView.findViewById(R.id.settings_account_name);

            username = itemView.findViewById(R.id.settings_account_username);

        }

    }



    /**

     * ViewHolder for a non-clickable section title row.

     */

    static class SectionHolder extends RecyclerView.ViewHolder {

        TextView title;



        SectionHolder(@NonNull View itemView) {

            super(itemView);

            title = itemView.findViewById(R.id.settings_section_title);

        }

    }



    /**

     * ViewHolder for a tappable settings navigation row with optional subtitle.

     */

    static class NavHolder extends RecyclerView.ViewHolder {

        TextView title;

        TextView subtitle;



        NavHolder(@NonNull View itemView) {

            super(itemView);

            title = itemView.findViewById(R.id.settings_nav_title);

            subtitle = itemView.findViewById(R.id.settings_nav_subtitle);

        }

    }



    /**

     * ViewHolder for the logout row at the bottom of settings.

     */

    static class LogoutHolder extends RecyclerView.ViewHolder {

        LogoutHolder(@NonNull View itemView) {

            super(itemView);

        }

    }

}

