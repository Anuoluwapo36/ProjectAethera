package com.example.projectaethera.ui.adapter;



import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.TextView;



import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;



import com.bumptech.glide.Glide;

import com.example.projectaethera.R;

import com.example.projectaethera.config.AppConfig;

import com.example.projectaethera.model.remote.NotificationItem;

import com.example.projectaethera.ui.widgets.ProfileCircleWidgets;

import com.example.projectaethera.utils.ImageUrlUtils;

import com.example.projectaethera.utils.TimeAgoUtils;



import java.util.ArrayList;

import java.util.List;



/**

 * RecyclerView adapter that shows the user's in-app notifications list.

 * Each row shows who acted, what happened, time ago, read/unread background, and profile photo.

 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {



    /**

     * Called when the user taps a notification row.

     */

    public interface OnNotificationClickListener {

        void onNotificationClick(NotificationItem item);

    }



    private List<NotificationItem> items = new ArrayList<>();

    private OnNotificationClickListener listener;



    /**

     * Replaces all notifications and refreshes the list.

     *

     * @param items notification items from the server, or null for empty

     */

    public void setItems(List<NotificationItem> items) {

        this.items = items != null ? items : new ArrayList<>();

        notifyDataSetChanged();

    }



    /**

     * Sets the listener for when a notification row is tapped.

     *

     * @param listener callback with the tapped notification

     */

    public void setOnNotificationClickListener(OnNotificationClickListener listener) {

        this.listener = listener;

    }



    /**

     * Inflates one notification row layout.

     */

    @NonNull

    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())

                .inflate(R.layout.notification_item, parent, false);

        return new ViewHolder(view);

    }



    /**

     * Builds message text from type, shows time and avatar, highlights unread rows, and wires click.

     */

    @Override

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NotificationItem item = items.get(position);

        if ("like_milestone".equals(item.getType())) {

            holder.message.setText(buildActionText(item.getType(), item.getMessage()));

        } else {

            String username = item.getActorUsername() != null ? item.getActorUsername() : "Someone";

            String actionText = buildActionText(item.getType(), item.getMessage());

            holder.message.setText(username + " " + actionText);

        }

        holder.time.setText(TimeAgoUtils.formatIso(item.getCreatedAt()));



        int bgColor = item.getIsRead() == 0

                ? holder.itemView.getContext().getColor(R.color.notification_unread_bg)

                : holder.itemView.getContext().getColor(R.color.white);

        holder.itemView.setBackgroundColor(bgColor);



        String imagePath = item.getActorProfileImage();

        String imageUrl = ImageUrlUtils.toFullUrl(imagePath);

        if (imageUrl != null) {

            Glide.with(holder.itemView.getContext())

                    .load(imageUrl)

                    .placeholder(AppConfig.DEFAULT_PROFILE_IMAGE)

                    .error(AppConfig.DEFAULT_PROFILE_IMAGE)

                    .circleCrop()

                    .into(holder.avatar);

        } else {

            holder.avatar.setImageResource(AppConfig.DEFAULT_PROFILE_IMAGE);

        }



        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {

                listener.onNotificationClick(item);

            }

        });

    }



    /**

     * Returns how many notifications are in the list.

     */

    @Override

    public int getItemCount() {

        return items.size();

    }



    private String buildActionText(String type, String message) {

        if (message != null && !message.isEmpty()) {

            return message;

        }

        if ("like".equals(type)) {

            return "liked your post";

        }

        if ("comment".equals(type)) {

            return "commented on your post";

        }

        if ("comment_reply".equals(type)) {

            return "replied to your comment";

        }

        if ("follow".equals(type)) {

            return "started following you";

        }

        if ("status_like".equals(type)) {

            return "liked your status";

        }

        if ("like_milestone".equals(type)) {

            return message != null && !message.isEmpty() ? message : "Your post is getting popular";

        }

        return "sent you a notification";

    }



    /**

     * Holds avatar, message, and time for one notification row.

     */

    static class ViewHolder extends RecyclerView.ViewHolder {

        ProfileCircleWidgets avatar;

        TextView message;

        TextView time;



        ViewHolder(@NonNull View itemView) {

            super(itemView);

            avatar = itemView.findViewById(R.id.notification_avatar);

            message = itemView.findViewById(R.id.notification_message);

            time = itemView.findViewById(R.id.notification_time);

        }

    }

}

