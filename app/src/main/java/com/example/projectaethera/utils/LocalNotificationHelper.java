package com.example.projectaethera.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.projectaethera.R;
import com.example.projectaethera.model.remote.NotificationItem;

/**
 * Shows system tray notifications when someone likes, comments, or follows.
 * Uses local notifications (no Firebase) when the app detects new unread alerts.
 */
public final class LocalNotificationHelper {

    private static final String CHANNEL_ID = "social_alerts";
    private static final String CHANNEL_NAME = "Social activity";

    private LocalNotificationHelper() {
    }

    public static void ensureChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager == null || manager.getNotificationChannel(CHANNEL_ID) != null) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("Likes, comments, and follows on your posts");
        manager.createNotificationChannel(channel);
    }

    public static void show(Context context, String title, String body) {
        ensureChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bell_solid_full)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat.from(context).notify(
                    (int) System.currentTimeMillis(),
                    builder.build()
            );
        } catch (SecurityException ignored) {

        }
    }

    public static void showForItem(Context context, NotificationItem item) {
        if (item == null) {
            return;
        }
        String username = item.getActorUsername() != null ? item.getActorUsername() : "Someone";
        String title = "Project Aethera";
        String body;

        if ("like_milestone".equals(item.getType())) {
            body = item.getMessage() != null && !item.getMessage().isEmpty()
                    ? item.getMessage()
                    : "Your post is getting popular";
        } else if ("like".equals(item.getType())) {
            body = username + " liked your post";
        } else if ("comment".equals(item.getType())) {
            body = username + " commented on your post";
        } else if ("follow".equals(item.getType())) {
            body = username + " started following you";
        } else if ("status_like".equals(item.getType())) {
            body = username + " liked your status";
        } else if (item.getMessage() != null && !item.getMessage().isEmpty()) {
            body = item.getMessage();
        } else {
            body = username + " sent you a notification";
        }

        show(context, title, body);
    }
}
