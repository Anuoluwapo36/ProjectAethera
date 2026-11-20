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

import com.example.projectaethera.model.remote.StatusGroup;

import com.example.projectaethera.utils.ImageUrlUtils;

import com.example.projectaethera.ui.widgets.ProfileCircleWidgets;



import java.util.ArrayList;

import java.util.List;



/**

 * Horizontal RecyclerView adapter for the stories/status strip on the home screen.

 * Position 0 is the user's own story slot; following positions are friends' status groups.

 */

public class StatusStripAdapter extends RecyclerView.Adapter<StatusStripAdapter.ViewHolder> {



    /**

     * Called when the user taps add story or opens a friend's (or own) status group.

     */

    public interface Listener {

        void onAddStatusClick();



        void onStatusGroupClick(StatusGroup group);

    }



    private final List<StatusGroup> friendGroups = new ArrayList<>();

    private StatusGroup ownGroup;

    private String ownUsername = "Your story";

    private String ownProfileImage;

    private Listener listener;



    /**

     * Sets the callback for add-story and open-status actions.

     *

     * @param listener strip click listener

     */

    public void setListener(Listener listener) {

        this.listener = listener;

    }



    /**

     * Updates the logged-in user's name and photo shown in the first slot.

     *

     * @param username     display name for own story slot

     * @param profileImage server path to profile image

     */

    public void setOwnProfile(String username, String profileImage) {

        if (username != null && !username.isEmpty()) {

            ownUsername = username;

        }

        ownProfileImage = profileImage;

        notifyItemChanged(0);

    }



    /**

     * Splits groups into own story and friends, then refreshes the strip.

     *

     * @param groups status groups from the API, including one marked as own

     */

    public void setGroups(List<StatusGroup> groups) {

        ownGroup = null;

        friendGroups.clear();



        if (groups != null) {

            for (StatusGroup group : groups) {

                if (group.getIsOwn() == 1) {

                    ownGroup = group;

                    if (group.getUsername() != null) {

                        ownUsername = group.getUsername();

                    }

                    if (group.getProfileImage() != null && !group.getProfileImage().isEmpty()) {

                        ownProfileImage = group.getProfileImage();

                    }

                } else {

                    friendGroups.add(group);

                }

            }

        }

        notifyDataSetChanged();

    }



    /**

     * Inflates one status circle item layout.

     */

    @NonNull

    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())

                .inflate(R.layout.status_strip_item, parent, false);

        return new ViewHolder(view);

    }



    /**

     * Binds the own-story slot at position 0, or a friend's avatar, ring color, and click handler.

     */

    @Override

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position == 0) {

            bindOwnSlot(holder);

            return;

        }



        StatusGroup group = friendGroups.get(position - 1);

        holder.username.setText(group.getUsername() != null ? group.getUsername() : "User");

        holder.addBadge.setVisibility(View.GONE);



        int ringColor = group.getHasUnseen() == 1

                ? holder.itemView.getContext().getColor(R.color.ig_blue)

                : holder.itemView.getContext().getColor(R.color.light_gray);

        holder.avatar.setRingColor(ringColor);

        loadAvatar(holder.avatar, group.getProfileImage());



        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {

                listener.onStatusGroupClick(group);

            }

        });

    }



    private void bindOwnSlot(@NonNull ViewHolder holder) {

        boolean hasOwnStatuses = ownGroup != null

                && ownGroup.getStatuses() != null

                && !ownGroup.getStatuses().isEmpty();



        holder.username.setText(hasOwnStatuses ? "Your story" : ownUsername);

        holder.addBadge.setVisibility(View.VISIBLE);



        int ringColor = hasOwnStatuses

                ? holder.itemView.getContext().getColor(R.color.ig_blue)

                : holder.itemView.getContext().getColor(R.color.light_gray);

        holder.avatar.setRingColor(ringColor);

        loadAvatar(holder.avatar, ownProfileImage);



        View.OnClickListener openViewer = v -> {

            if (listener != null && hasOwnStatuses) {

                listener.onStatusGroupClick(ownGroup);

            }

        };

        View.OnClickListener openGallery = v -> {

            if (listener != null) {

                listener.onAddStatusClick();

            }

        };



        holder.avatar.setOnClickListener(hasOwnStatuses ? openViewer : openGallery);

        holder.username.setOnClickListener(hasOwnStatuses ? openViewer : openGallery);

        holder.addBadge.setOnClickListener(openGallery);



        holder.itemView.setOnClickListener(hasOwnStatuses ? openViewer : openGallery);

    }



    private void loadAvatar(ProfileCircleWidgets avatar, String imagePath) {

        String url = ImageUrlUtils.toFullUrl(imagePath);

        if (url != null) {

            Glide.with(avatar.getContext())

                    .load(url)

                    .placeholder(AppConfig.DEFAULT_PROFILE_IMAGE)

                    .error(AppConfig.DEFAULT_PROFILE_IMAGE)

                    .circleCrop()

                    .into(avatar);

        } else {

            avatar.setImageResource(AppConfig.DEFAULT_PROFILE_IMAGE);

        }

    }



    /**

     * Returns one slot for own story plus one slot per friend group.

     */

    @Override

    public int getItemCount() {

        return 1 + friendGroups.size();

    }



    /**

     * Holds avatar, add badge, and username for one status strip circle.

     */

    static class ViewHolder extends RecyclerView.ViewHolder {

        ProfileCircleWidgets avatar;

        ImageView addBadge;

        TextView username;



        ViewHolder(@NonNull View itemView) {

            super(itemView);

            avatar = itemView.findViewById(R.id.status_avatar);

            addBadge = itemView.findViewById(R.id.status_add_badge);

            username = itemView.findViewById(R.id.status_username);

        }

    }

}

