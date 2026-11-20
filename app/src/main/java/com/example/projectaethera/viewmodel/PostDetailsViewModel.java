package com.example.projectaethera.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectaethera.model.entity.Post;
import com.example.projectaethera.model.remote.CommentCountResponse;
import com.example.projectaethera.model.remote.PostResponse;
import com.example.projectaethera.repository.CommentRepository;
import com.example.projectaethera.repository.PostRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for a single post detail screen.
 * <p>
 * I fetch one post by id for the full-screen view (images, caption, author).
 * Comment count LiveData is here too so the detail header can show totals
 * without pulling in the full {@link CommentViewModel} unless needed.
 */
public class PostDetailsViewModel extends ViewModel {
    /** The post being displayed on the detail screen. */
    private  final MutableLiveData<Post> postDetails = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    /** Comment count shown under the post. */
    private final  MutableLiveData<Integer> commentCountSuccess = new MutableLiveData<>();

    /**
     * LiveData for comment count on this post.
     *
     * @return LiveData with total comments
     */
    public LiveData<Integer> getCommentCount(){
        return commentCountSuccess;
    }
    private PostRepository postRepository;
    private CommentRepository commentRepository;

    /** Creates the ViewModel and post/comment repositories. */
    public PostDetailsViewModel( ) {
        this.postRepository = new PostRepository();
        this.commentRepository = new CommentRepository();
    }

    /**
     * Loads full post data from the backend for the detail screen.
     *
     * @param postId which post to show
     * @param userId logged-in user (some APIs use this for like state)
     */
    public void  fetchPostDetails(int postId, int userId){

        postRepository.getPostDetails(postId, userId).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    PostResponse postResponse = response.body();

                    if (postResponse.isSuccess()){
                        postDetails.setValue(postResponse.getData());
                    }
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable throwable) {
                errorMessage.setValue("failed tp load post");
            }
        });
    }

    /**
     * The post currently loaded for the UI.
     *
     * @return LiveData with {@link Post}
     */
    public MutableLiveData<Post> getPostDetails() {
        return postDetails;
    }

    /**
     * Error if post details failed to load.
     *
     * @return LiveData with error text
     */
    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
