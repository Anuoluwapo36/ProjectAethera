package com.example.projectaethera.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectaethera.model.remote.CommentCountResponse;
import com.example.projectaethera.model.remote.CommentResponse;
import com.example.projectaethera.model.remote.GetCommentResponse;
import com.example.projectaethera.repository.CommentRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for comments on a post — add, list, and count.
 * <p>
 * I use this on post detail and comment bottom sheets. Separate LiveData for
 * success, errors, full comment tree, and total count so the UI can update
 * each piece independently.
 */
public class CommentViewModel extends ViewModel {

    /** True after a new comment or reply was saved. */
    private final MutableLiveData<Boolean> commentSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    /** Full comment list payload from the API. */
    private final MutableLiveData<GetCommentResponse> commentsList = new MutableLiveData<>();

    /** Total comment count for the post header. */
    private final  MutableLiveData<Integer> commentCountSuccess = new MutableLiveData<>();

    /**
     * LiveData for comment count on the post.
     *
     * @return LiveData with total comments
     */
    public LiveData<Integer> getCommentCount(){
        return commentCountSuccess;
    }
    private CommentRepository commentRepository;

    /** Creates the ViewModel and comment repository. */
    public CommentViewModel( ) {
        this.commentRepository = new CommentRepository();
    }

    /**
     * Posts a new comment or reply on a post.
     *
     * @param postId post being commented on
     * @param userId logged-in user
     * @param comment text the user typed
     * @param reply_to parent comment id for replies, or null for top-level
     */
    public void  addComment(int postId, int userId, String comment, Integer reply_to){


        commentRepository.addComment(postId, userId, comment, reply_to).enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    CommentResponse commentResponse = response.body();
                    if (commentResponse.isSuccess()){
                        commentSuccess.postValue(true);

                    }else {
                        errorMessage.postValue(response.body().getMessage());
                        commentSuccess.postValue(false);
                    }
                }else {
                    errorMessage.postValue(response.message());
                    commentSuccess.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable throwable) {
                errorMessage.postValue(throwable.getMessage());
                commentSuccess.postValue(false);
            }
        });

    }


    /**
     * Loads all comments for a post (including nested replies if API returns them).
     *
     * @param postId post to load comments for
     */
    public void getComments(int postId) {
        commentRepository.getComment(postId).enqueue(new Callback<GetCommentResponse>() {
            @Override
            public void onResponse(Call<GetCommentResponse> call, Response<GetCommentResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    commentsList.postValue(response.body());
                }else {
                    errorMessage.postValue(response.message());
                }
            }

            @Override
            public void onFailure(Call<GetCommentResponse> call, Throwable throwable) {
                errorMessage.postValue(throwable.getMessage());
            }
        });
    }

    /**
     * Loads just the comment count — lighter than fetching the full list.
     *
     * @param postId post to count comments for
     */
    public void loadCommentCount(int postId) {
        commentRepository.getCommentCount(postId).enqueue(new Callback<CommentCountResponse>() {
            @Override
            public void onResponse(Call<CommentCountResponse> call, Response<CommentCountResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    commentCountSuccess.postValue(response.body().getTotal());
                }
            }

            @Override
            public void onFailure(Call<CommentCountResponse> call, Throwable throwable) {
                commentCountSuccess.postValue(0);
            }
        });
    }

    /**
     * Full comment response for the RecyclerView.
     *
     * @return LiveData with {@link GetCommentResponse}
     */
    public MutableLiveData<GetCommentResponse> getCommentsList() {
        return commentsList;
    }

    /**
     * Whether the last add-comment call succeeded.
     *
     * @return LiveData true on success
     */
    public MutableLiveData<Boolean> getCommentSuccess() {
        return commentSuccess;
    }

    /**
     * Error from add or load comments.
     *
     * @return LiveData with error text
     */
    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
