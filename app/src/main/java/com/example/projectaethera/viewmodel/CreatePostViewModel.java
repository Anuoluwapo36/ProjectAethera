package com.example.projectaethera.viewmodel;



import android.content.Context;

import android.net.Uri;



import androidx.lifecycle.MutableLiveData;

import androidx.lifecycle.ViewModel;



import com.example.projectaethera.model.remote.PostResponse;

import com.example.projectaethera.repository.PostRepository;

import com.example.projectaethera.utils.FileUtils;



import java.io.File;

import java.util.ArrayList;

import java.util.List;



import okhttp3.MediaType;

import okhttp3.MultipartBody;

import okhttp3.RequestBody;

import retrofit2.Call;

import retrofit2.Callback;

import retrofit2.Response;



/**

 * ViewModel for the "create post" screen — caption plus one or many images.

 * <p>

 * I accept either a single {@link Uri} or a {@link List} of Uris so the same

 * ViewModel works for one photo or a carousel. Results go to LiveData for

 * success toast and error messages.

 */

public class CreatePostViewModel extends ViewModel {

    /** True when the server accepted the new post. */

    private final MutableLiveData<Boolean> PostSuccess = new MutableLiveData<>();

    /** Server or local error message for the UI. */

    private final MutableLiveData<String> PostErrorMessage = new MutableLiveData<>();



    private PostRepository postRepository;



    /** Creates the ViewModel and post repository. */

    public CreatePostViewModel() {

        this.postRepository = new PostRepository();

    }



    /**

     * Builds multipart parts from image(s) and uploads a new post.

     *

     * @param context needed to turn Uris into files

     * @param userId logged-in user creating the post

     * @param caption text the user wrote

     * @param imageData either one {@link Uri} or a {@link List} of Uris

     */

    public void postCreated(Context context, int userId, String caption, Object imageData) {

        RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));

        RequestBody captionPart = RequestBody.create(MediaType.parse("text/plain"), caption);



        List<MultipartBody.Part> imageParts = new ArrayList<>();

        if (imageData instanceof Uri) {

            Uri imageUri = (Uri) imageData;

            File imageFile = FileUtils.uriToFile(context, imageUri);

            if (imageFile == null) {

                PostErrorMessage.postValue("image failed to load");

                return;

            }

            RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageFile);

            MultipartBody.Part Part = MultipartBody.Part.createFormData("image", imageFile.getName(), imageBody);

            imageParts.add(Part);

        } else if (imageData instanceof List<?>) {

            List<Uri> imageList = (List<Uri>) imageData;

            for (Uri uri : imageList) {

                File file = FileUtils.uriToFile(context, uri);

                if (file != null) {

                    RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), file);

                    MultipartBody.Part Part = MultipartBody.Part.createFormData("image", file.getName(), imageBody);

                    imageParts.add(Part);

                }

            }

        }



        postRepository.uploadPost(userIdPart, captionPart, imageParts).enqueue(new Callback<PostResponse>() {

            @Override

            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    PostResponse postResponse = response.body();

                    PostSuccess.postValue(postResponse.isSuccess());

                    PostErrorMessage.postValue(postResponse.getMessage());

                } else {

                    PostSuccess.postValue(false);

                    PostErrorMessage.postValue("something went wrong");

                }

            }



            @Override

            public void onFailure(Call<PostResponse> call, Throwable throwable) {

                PostSuccess.postValue(false);

                PostErrorMessage.postValue("No internet connection" );

            }

        });

    }



    /**

     * Whether the post upload succeeded.

     *

     * @return LiveData true on success

     */

    public MutableLiveData<Boolean> getPostSuccess() {

        return PostSuccess;

    }



    /**

     * Error or info message from create post.

     *

     * @return LiveData with user-facing text

     */

    public MutableLiveData<String> getPostErrorMessage() {

        return PostErrorMessage;

    }

}

