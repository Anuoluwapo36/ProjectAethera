package com.example.projectaethera.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectaethera.model.entity.Post;
import com.example.projectaethera.model.remote.PostResponse;
import com.example.projectaethera.model.remote.UserPostResponse;
import com.example.projectaethera.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for showing all posts on a user's profile grid.
 * <p>
 * I call this from ProfileFragment (mine or someone else's) to fill the
 * photo grid. Results are a list of {@link Post} objects the adapter can bind.
 */
public class UserPostViewModel extends ViewModel {
    private PostRepository returnPost;
    /** Posts belonging to the profile we requested. */
    private final MutableLiveData<List<Post>> getUserPostSuccess = new MutableLiveData<List<Post>>();
    private final  MutableLiveData< String>  userResponsePostErrorMessage = new MutableLiveData<>();

    /** Creates the ViewModel and post repository. */
    public UserPostViewModel( ) {
        this.returnPost = new PostRepository();
    }

    /**
     * Loads every post created by this user for the profile grid.
     *
     * @param userId profile owner whose posts we want
     */
    public void showUserPost(int userId){
        returnPost.getUserPost(userId).enqueue(new Callback<UserPostResponse>() {
            @Override
            public void onResponse(Call<UserPostResponse> call, Response<UserPostResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    List<Post> posts = response.body().getData();
                    if (posts == null) posts = new ArrayList<>();
                    getUserPostSuccess.setValue(posts);

                }else {
                    userResponsePostErrorMessage.setValue("something went wrong");

                }
            }

            @Override
            public void onFailure(Call<UserPostResponse> call, Throwable throwable) {
                userResponsePostErrorMessage.setValue("No internet Connection");

            }
        });
    }

    /**
     * Posts for the profile RecyclerView/grid.
     *
     * @return LiveData list of {@link Post}
     */
    public MutableLiveData<List<Post>> getGetUserPostSuccess() {
        return getUserPostSuccess;
    }

    /**
     * Error if loading profile posts failed.
     *
     * @return LiveData with error text
     */
    public MutableLiveData<String> getUserResponsePostErrorMessage() {
        return userResponsePostErrorMessage;
    }
}
