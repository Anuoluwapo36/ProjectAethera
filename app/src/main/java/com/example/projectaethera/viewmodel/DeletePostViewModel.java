package com.example.projectaethera.viewmodel;



import androidx.lifecycle.LiveData;

import androidx.lifecycle.MutableLiveData;

import androidx.lifecycle.ViewModel;



import com.example.projectaethera.model.remote.PostResponse;

import com.example.projectaethera.repository.PostRepository;



import java.io.IOException;



import retrofit2.Call;

import retrofit2.Callback;

import retrofit2.Response;



/**

 * ViewModel for deleting a post owned by the logged-in user.

 * <p>

 * I expose the deleted post id so the feed or profile grid can remove that item

 * without a full reload. Also handles loading state and helpful error messages

 * (e.g. backend not running).

 */

public class DeletePostViewModel extends ViewModel {



    private final PostRepository postRepository;

    /** Id of the post that was successfully deleted — observe to update lists. */

    private final MutableLiveData<Integer> deletedPostId = new MutableLiveData<>();

    /** Error message if delete failed. */

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    /** True while the delete API call is running. */

    private final MutableLiveData<Boolean> deleting = new MutableLiveData<>(false);



    /** Creates the ViewModel and post repository. */

    public DeletePostViewModel() {

        postRepository = new PostRepository();

    }



    /**

     * Id of the post removed from the server, if any.

     *

     * @return LiveData with post id after success

     */

    public LiveData<Integer> getDeletedPostId() {

        return deletedPostId;

    }



    /**

     * Last delete error for Toast or dialog.

     *

     * @return LiveData with error text

     */

    public LiveData<String> getErrorMessage() {

        return errorMessage;

    }



    /**

     * Whether a delete is in progress (disable button / show spinner).

     *

     * @return LiveData true while deleting

     */
    public LiveData<Boolean> getDeleting() {

        return deleting;

    }



    /**

     * Deletes a post if userId and postId are valid.

     *

     * @param userId owner of the post (must match logged-in user on server)

     * @param postId post to remove

     */

    public void deletePost(int userId, int postId) {

        if (userId <= 0 || postId <= 0) {

            errorMessage.setValue("Invalid user or post");

            return;

        }



        deleting.setValue(true);

        errorMessage.setValue(null);



        postRepository.deletePost(userId, postId).enqueue(new Callback<PostResponse>() {

            @Override

            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {

                deleting.setValue(false);



                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    deletedPostId.setValue(postId);

                    return;

                }



                if (response.body() != null && response.body().getMessage() != null) {

                    errorMessage.setValue(response.body().getMessage());

                    return;

                }



                if (response.code() == 404) {

                    errorMessage.setValue("Delete API not found. Restart your backend server.");

                    return;

                }



                errorMessage.setValue("Could not delete post");

            }



            @Override

            public void onFailure(Call<PostResponse> call, Throwable t) {

                deleting.setValue(false);

                if (t instanceof IOException) {

                    errorMessage.setValue("Network error. Is the backend running?");

                } else {

                    errorMessage.setValue("Could not delete post");

                }

            }

        });

    }



    /** Clears deleted id so the same event does not fire twice after rotation. */

    public void clearDeletedPostId() {

        deletedPostId.setValue(null);

    }



    /** Clears error LiveData after the user dismisses a message. */

    public void clearErrorMessage() {

        errorMessage.setValue(null);

    }

}

