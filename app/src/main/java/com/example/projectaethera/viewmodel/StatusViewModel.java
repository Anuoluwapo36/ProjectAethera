package com.example.projectaethera.viewmodel;



import androidx.lifecycle.LiveData;

import androidx.lifecycle.MutableLiveData;

import androidx.lifecycle.ViewModel;



import com.example.projectaethera.model.remote.StatusFeedResponse;

import com.example.projectaethera.model.remote.StatusGroup;

import com.example.projectaethera.model.remote.StatusItem;

import com.example.projectaethera.model.remote.StatusResponse;

import com.example.projectaethera.model.remote.StatusUserResponse;

import com.example.projectaethera.repository.StatusRepository;



import java.util.ArrayList;

import java.util.List;



import retrofit2.Call;

import retrofit2.Callback;

import retrofit2.Response;



/**

 * ViewModel for stories/status — home row, viewer, likes, and delete.

 * <p>

 * I use this for the horizontal story list on Home, opening someone's statuses,

 * marking them viewed, and optimistic like toggles so the heart updates instantly.

 */

public class StatusViewModel extends ViewModel {



    private final StatusRepository repository;

    /** Grouped statuses for the home story row (one bubble per user). */

    private final MutableLiveData<List<StatusGroup>> statusFeed = new MutableLiveData<>(new ArrayList<>());

    /** All status slides for one user when their story is open. */

    private final MutableLiveData<List<StatusItem>> userStatuses = new MutableLiveData<>();

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    /** Fires after delete or post so UI can refresh the feed. */

    private final MutableLiveData<Boolean> statusPosted = new MutableLiveData<>();



    /** Creates the ViewModel and status repository. */

    public StatusViewModel() {

        repository = new StatusRepository();

    }



    /**

     * Story groups for the home horizontal list.

     *

     * @return LiveData list of {@link StatusGroup}

     */

    public LiveData<List<StatusGroup>> getStatusFeed() {

        return statusFeed;

    }



    /**

     * Status items for the currently opened user's story viewer.

     *

     * @return LiveData list of {@link StatusItem}

     */

    public LiveData<List<StatusItem>> getUserStatuses() {

        return userStatuses;

    }



    /**

     * Last error loading or deleting a status.

     *

     * @return LiveData with error text

     */

    public LiveData<String> getErrorMessage() {

        return errorMessage;

    }



    /**

     * True while loading one user's statuses in the viewer.

     *

     * @return LiveData loading flag

     */

    public LiveData<Boolean> getLoading() {

        return loading;

    }



    /**

     * Signal to refresh after delete or new status — observe then call {@link #clearStatusPosted()}.

     *

     * @return LiveData true when feed should reload

     */

    public LiveData<Boolean> getStatusPosted() {

        return statusPosted;

    }



    /**

     * Loads the story row for Home (people I follow + my story).

     *

     * @param userId logged-in user

     */

    public void loadStatusFeed(int userId) {

        if (userId <= 0) {

            return;

        }



        repository.getStatusFeed(userId).enqueue(new Callback<StatusFeedResponse>() {

            @Override

            public void onResponse(Call<StatusFeedResponse> call, Response<StatusFeedResponse> response) {

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    List<StatusGroup> data = response.body().getData();

                    statusFeed.setValue(data != null ? data : new ArrayList<>());

                } else {

                    statusFeed.setValue(new ArrayList<>());

                }

            }



            @Override

            public void onFailure(Call<StatusFeedResponse> call, Throwable t) {

                statusFeed.setValue(new ArrayList<>());

            }

        });

    }



    /**

     * Loads all status slides for one user when their story bubble is tapped.

     *

     * @param targetUserId whose story we are viewing

     * @param viewerId logged-in user (for seen/unseen and privacy)

     */

    public void loadUserStatuses(int targetUserId, int viewerId) {

        loading.setValue(true);

        repository.getUserStatuses(targetUserId, viewerId).enqueue(new Callback<StatusUserResponse>() {

            @Override

            public void onResponse(Call<StatusUserResponse> call, Response<StatusUserResponse> response) {

                loading.setValue(false);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()

                        && response.body().getData() != null) {

                    List<StatusItem> items = response.body().getData().getStatuses();

                    userStatuses.setValue(items != null ? items : new ArrayList<>());

                } else {

                    userStatuses.setValue(new ArrayList<>());

                    if (response.body() != null && response.body().getMessage() != null) {

                        errorMessage.setValue(response.body().getMessage());

                    }

                }

            }



            @Override

            public void onFailure(Call<StatusUserResponse> call, Throwable t) {

                loading.setValue(false);

                userStatuses.setValue(new ArrayList<>());

                errorMessage.setValue("Could not load status");

            }

        });

    }



    /**

     * Tells the server this status was viewed (fire-and-forget).

     *

     * @param statusId status slide that was shown

     * @param viewerId logged-in user who watched it

     */

    public void markViewed(int statusId, int viewerId) {

        repository.markStatusViewed(statusId, viewerId).enqueue(new Callback<StatusResponse>() {

            @Override

            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {

            }



            @Override

            public void onFailure(Call<StatusResponse> call, Throwable t) {

            }

        });

    }



    /**

     * Likes or unlikes a status with optimistic UI update on the item.

     *

     * @param userId logged-in user

     * @param item status slide being liked — updated in place before API returns

     */

    public void toggleLike(int userId, StatusItem item) {

        if (item == null) {

            return;

        }



        boolean liked = item.getIsLiked() == 1;

        if (liked) {

            item.setIsLiked(0);

            item.setLikeCount(Math.max(0, item.getLikeCount() - 1));

            repository.unlikeStatus(userId, item.getStatusId()).enqueue(emptyCallback());

        } else {

            item.setIsLiked(1);

            item.setLikeCount(item.getLikeCount() + 1);

            repository.likeStatus(userId, item.getStatusId()).enqueue(emptyCallback());

        }

    }



    /**

     * Deletes one of my statuses and runs optional UI cleanup on success.

     *

     * @param userId owner of the status

     * @param statusId status to delete

     * @param onSuccess runnable from the fragment (e.g. close viewer)

     */

    public void deleteStatus(int userId, int statusId, Runnable onSuccess) {

        repository.deleteStatus(userId, statusId).enqueue(new Callback<StatusResponse>() {

            @Override

            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    statusPosted.setValue(true);

                    if (onSuccess != null) {

                        onSuccess.run();

                    }

                } else if (response.body() != null) {

                    errorMessage.setValue(response.body().getMessage());

                }

            }



            @Override

            public void onFailure(Call<StatusResponse> call, Throwable t) {

                errorMessage.setValue("Could not delete status");

            }

        });

    }



    /** Resets {@link #statusPosted} so observers do not refresh twice. */

    public void clearStatusPosted() {

        statusPosted.setValue(false);

    }



    /** Empty callback for like/unlike — I only care about optimistic UI here. */

    private Callback<StatusResponse> emptyCallback() {

        return new Callback<StatusResponse>() {

            @Override

            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {

            }



            @Override

            public void onFailure(Call<StatusResponse> call, Throwable t) {

            }

        };

    }

}

