package com.example.projectaethera.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectaethera.model.remote.PostResponse;
import com.example.projectaethera.repository.LikeRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for liking and unliking posts.
 * <p>
 * I use this from feed cards and post detail. It can check if I already liked
 * a post when the screen opens, then like/unlike and expose success/errors
 * so the heart icon and count stay in sync with the server.
 */
public class LikeViewModel extends ViewModel {

    /** True when the last like/unlike API call succeeded. */
    private final MutableLiveData<Boolean> likeSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    /** Whether the current user has liked this post (from isLiked API). */
    private final MutableLiveData<Boolean> isLiked = new MutableLiveData<>();


    private LikeRepository likeRepository;

    /** Creates the ViewModel and like repository. */
    public LikeViewModel() {
        this.likeRepository = new LikeRepository();

    }

    /**
     * Whether the logged-in user already liked the post.
     *
     * @return LiveData true if liked
     */
    public MutableLiveData<Boolean> getIsLiked() {
        return isLiked;
    }

    /**
     * Sends a like for this post.
     *
     * @param userId logged-in user
     * @param postId post to like
     */
    public void likePost(int userId, int postId) {

        likeRepository.likePost(userId, postId).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    PostResponse postResponse = response.body();
                    if (postResponse.isSuccess()) {
                        likeSuccess.setValue(true);
                    } else {
                        errorMessage.setValue(postResponse.getMessage());
                        likeSuccess.setValue(false);


                    }
                }else {
                    errorMessage.setValue("something went wrong");
                    likeSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable throwable) {

                errorMessage.setValue("network error");
                likeSuccess.setValue(false);

            }
        });
    }

    /**
     * Removes a like from this post.
     *
     * @param userId logged-in user
     * @param postId post to unlike
     */
    public void unlikePost(int userId, int postId) {

        likeRepository.unlikePost(userId, postId).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    PostResponse postResponse = response.body();
                    if (postResponse.isSuccess()) {
                        likeSuccess.setValue(true);
                    } else {
                        errorMessage.setValue(postResponse.getMessage());
                        likeSuccess.setValue(false);


                    }
                }else {
                    errorMessage.setValue("something went wrong");
                    likeSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable throwable) {

                errorMessage.setValue("network error");
                likeSuccess.setValue(false);

            }
        });
    }

    /**
     * Asks the server if this user already liked the post — call when opening detail/feed item.
     *
     * @param userId logged-in user
     * @param postId post to check
     */
    public void checkLiked(int userId , int postId){
        likeRepository.isLiked(userId, postId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null){
                    isLiked.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {

            }
        });
    }

    /**
     * Whether the last like/unlike action succeeded.
     *
     * @return LiveData true on success
     */
    public MutableLiveData<Boolean> getLikeSuccess() {
        return likeSuccess;
    }

    /**
     * Error from like/unlike or check.
     *
     * @return LiveData with error text
     */
    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
