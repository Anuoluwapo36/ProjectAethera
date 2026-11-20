package com.example.projectaethera.repository;

import com.example.projectaethera.model.remote.CommentCountResponse;
import com.example.projectaethera.model.remote.CommentResponse;
import com.example.projectaethera.model.remote.GetCommentResponse;
import com.example.projectaethera.network.ApiService;
import com.example.projectaethera.network.RetrofitClient;

import retrofit2.Call;

/**
 * Wraps comment API calls for adding comments, loading comment lists,
 * and getting comment counts on a post.
 */
public class CommentRepository {



    private ApiService apiService;

    /** Creates the repository and gets the shared Retrofit API client. */
    public CommentRepository( ) {
        this.apiService = RetrofitClient.getClient();
    }

    /**
     * Adds a new comment (or reply) to a post.
     *
     * @param postId   id of the post being commented on
     * @param userId   id of the user writing the comment
     * @param comment  text of the comment
     * @param reply_to id of parent comment when replying, or null for top-level
     * @return Retrofit call with add-comment result
     */
    public Call<CommentResponse> addComment(int postId, int userId, String comment, Integer reply_to){
        String replyField = (reply_to != null && reply_to > 0) ? String.valueOf(reply_to) : "";
        return apiService.addComment(postId, userId, comment, replyField);
    }

    /**
     * Loads all comments for a post.
     *
     * @param postId id of the post whose comments to fetch
     * @return Retrofit call with the comment list
     */
    public  Call<GetCommentResponse> getComment(int postId){
        return apiService.getComments(postId);
    }

    /**
     * Gets the total number of comments on a post.
     *
     * @param postId id of the post to count comments for
     * @return Retrofit call with the comment count
     */
    public Call<CommentCountResponse> getCommentCount(int postId){
        return apiService.getCommentCount(postId);
    }
}
