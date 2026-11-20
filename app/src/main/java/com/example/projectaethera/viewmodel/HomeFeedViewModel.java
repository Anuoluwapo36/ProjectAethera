package com.example.projectaethera.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectaethera.model.remote.HomeFeedRequest;
import com.example.projectaethera.model.remote.HomeFeedResponse;
import com.example.projectaethera.repository.HomeFeedRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for the home feed with pagination and stale-request protection.
 * <p>
 * I bump a {@link #feedToken} when the user pulls to refresh so old page
 * responses do not overwrite newer data. Each page result is wrapped in
 * {@link FeedPageResult} so the RecyclerView knows page number and errors.
 */
public class HomeFeedViewModel extends ViewModel {

    /**
     * One page of feed data (or an error) delivered to the UI.
     * I use an inner class so posts, page index, and token travel together.
     */
    public static class FeedPageResult {
        private final List<HomeFeedRequest> posts;
        private final int page;
        private final int token;
        private final boolean failed;
        private final String errorMessage;

        /**
         * Success wrapper with posts for this page.
         *
         * @param posts list from the API (may be empty)
         * @param page page number that was requested
         * @param token session token when this request started
         */
        public FeedPageResult(List<HomeFeedRequest> posts, int page, int token) {
            this.posts = posts;
            this.page = page;
            this.token = token;
            this.failed = false;
            this.errorMessage = null;
        }

        /**
         * Failure wrapper when the page could not load.
         *
         * @param page page number that failed
         * @param token session token when this request started
         * @param errorMessage text to show the user
         */
        public FeedPageResult(int page, int token, String errorMessage) {
            this.posts = null;
            this.page = page;
            this.token = token;
            this.failed = true;
            this.errorMessage = errorMessage;
        }

        /** Posts for this page, or null if {@link #isFailed()}. */
        public List<HomeFeedRequest> getPosts() {
            return posts;
        }

        /** Which page this result belongs to. */
        public int getPage() {
            return page;
        }

        /** Feed session id — ignore results whose token does not match current. */
        public int getToken() {
            return token;
        }

        /** True if this page failed to load. */
        public boolean isFailed() {
            return failed;
        }

        /** User-facing error when failed. */
        public String getErrorMessage() {
            return errorMessage;
        }
    }

    /** Latest page result for observers (success or error). */
    private final MutableLiveData<FeedPageResult> feedPage = new MutableLiveData<>();
    private final HomeFeedRepository homeFeedRepository;
    /** Incremented on refresh so outdated responses are dropped. */
    private int feedToken = 0;

    /** Creates the ViewModel and home feed repository. */
    public HomeFeedViewModel() {
        this.homeFeedRepository = new HomeFeedRepository();
    }

    /**
     * Starts a new feed session (e.g. pull-to-refresh). Old in-flight calls are ignored.
     *
     * @return the new token value to pass into {@link #homeFeedContent}
     */
    public int startNewFeedSession() {
        feedToken++;
        return feedToken;
    }

    /**
     * Current feed token — UI can compare with {@link FeedPageResult#getToken()}.
     *
     * @return latest session token
     */
    public int getCurrentFeedToken() {
        return feedToken;
    }

    /**
     * Loads one page of home feed for the logged-in user.
     *
     * @param userId logged-in user
     * @param page page index for pagination
     * @param token token from {@link #startNewFeedSession()} when this request started
     */
    public void homeFeedContent(int userId, int page, int token) {
        homeFeedRepository.homeFeed(userId, page).enqueue(new Callback<HomeFeedResponse>() {
            @Override
            public void onResponse(Call<HomeFeedResponse> call, Response<HomeFeedResponse> response) {
                if (token != feedToken) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<HomeFeedRequest> data = response.body().getData();
                    List<HomeFeedRequest> safeData = data != null ? new ArrayList<>(data) : new ArrayList<>();
                    feedPage.postValue(new FeedPageResult(safeData, page, token));
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Unable to load feed";
                    feedPage.postValue(new FeedPageResult(page, token, message));
                }
            }

            @Override
            public void onFailure(Call<HomeFeedResponse> call, Throwable throwable) {
                if (token != feedToken) {
                    return;
                }
                feedPage.postValue(new FeedPageResult(page, token, "Connection error"));
            }
        });
    }

    /**
     * LiveData stream of feed pages — observe to update the home RecyclerView.
     *
     * @return LiveData with {@link FeedPageResult}
     */
    public MutableLiveData<FeedPageResult> getFeedPage() {
        return feedPage;
    }
}
