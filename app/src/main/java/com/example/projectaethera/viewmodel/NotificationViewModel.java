package com.example.projectaethera.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectaethera.model.remote.NotificationItem;
import com.example.projectaethera.model.remote.NotificationsResponse;
import com.example.projectaethera.model.remote.UnreadCountResponse;
import com.example.projectaethera.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for the notifications screen and unread badge on the bell icon.
 * Loads notifications, tracks unread count, and emits push alerts when count rises.
 */
public class NotificationViewModel extends ViewModel {

    private final NotificationRepository repository;
    private final MutableLiveData<List<NotificationItem>> notifications = new MutableLiveData<>();
    private final MutableLiveData<Integer> unreadCount = new MutableLiveData<>(0);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<NotificationItem> pushAlert = new MutableLiveData<>();

    /** Previous unread count so we can detect new alerts for phone notifications. */
    private int lastUnreadCount = -1;
    private int activeUserId = 0;

    public NotificationViewModel() {
        repository = new NotificationRepository();
    }

    /** Clears cached counts/list when a different user logs in on this device. */
    public void resetForUser(int userId) {
        if (userId <= 0 || userId == activeUserId) {
            return;
        }
        activeUserId = userId;
        lastUnreadCount = -1;
        unreadCount.setValue(0);
        notifications.setValue(new ArrayList<>());
        pushAlert.setValue(null);
    }

    public LiveData<List<NotificationItem>> getNotifications() {
        return notifications;
    }

    public LiveData<Integer> getUnreadCount() {
        return unreadCount;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    /** Fires when unread count increases — HomeFragment shows a system notification. */
    public LiveData<NotificationItem> getPushAlert() {
        return pushAlert;
    }

    public void loadNotifications(int userId) {
        if (userId <= 0) {
            return;
        }
        loading.setValue(true);
        errorMessage.setValue(null);
        repository.getNotifications(userId, 1, 50).enqueue(new Callback<NotificationsResponse>() {
            @Override
            public void onResponse(Call<NotificationsResponse> call, Response<NotificationsResponse> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<NotificationItem> data = response.body().getData();
                    notifications.setValue(data != null ? data : new ArrayList<>());
                } else {
                    notifications.setValue(new ArrayList<>());
                    errorMessage.setValue("Could not load notifications");
                }
            }

            @Override
            public void onFailure(Call<NotificationsResponse> call, Throwable t) {
                loading.setValue(false);
                notifications.setValue(new ArrayList<>());
                errorMessage.setValue("Network error loading notifications");
            }
        });
    }

    public void fetchUnreadCount(int userId) {
        if (userId <= 0) {
            return;
        }
        resetForUser(userId);
        repository.getUnreadCount(userId).enqueue(new Callback<UnreadCountResponse>() {
            @Override
            public void onResponse(Call<UnreadCountResponse> call, Response<UnreadCountResponse> response) {
                if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                    return;
                }
                int count = response.body().getUnreadCount();
                if (lastUnreadCount >= 0 && count > lastUnreadCount) {
                    fetchLatestForPush(userId);
                }
                lastUnreadCount = count;
                unreadCount.setValue(count);
            }

            @Override
            public void onFailure(Call<UnreadCountResponse> call, Throwable t) {
            }
        });
    }

    private void fetchLatestForPush(int userId) {
        repository.getNotifications(userId, 1, 5).enqueue(new Callback<NotificationsResponse>() {
            @Override
            public void onResponse(Call<NotificationsResponse> call, Response<NotificationsResponse> response) {
                if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                    return;
                }
                List<NotificationItem> data = response.body().getData();
                if (data == null) {
                    return;
                }
                for (NotificationItem item : data) {
                    if (item.getIsRead() == 0) {
                        pushAlert.setValue(item);
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationsResponse> call, Throwable t) {
            }
        });
    }

    public void markAllAsRead(int userId) {
        repository.markAllAsRead(userId).enqueue(new Callback<NotificationsResponse>() {
            @Override
            public void onResponse(Call<NotificationsResponse> call, Response<NotificationsResponse> response) {
                lastUnreadCount = 0;
                unreadCount.setValue(0);
            }

            @Override
            public void onFailure(Call<NotificationsResponse> call, Throwable t) {
            }
        });
    }
}
