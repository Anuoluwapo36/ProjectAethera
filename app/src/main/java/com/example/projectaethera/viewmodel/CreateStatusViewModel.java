package com.example.projectaethera.viewmodel;







import android.content.Context;



import android.net.Uri;







import androidx.lifecycle.MutableLiveData;



import androidx.lifecycle.ViewModel;







import com.example.projectaethera.model.remote.StatusResponse;



import com.example.projectaethera.repository.StatusRepository;



import com.example.projectaethera.utils.FileUtils;







import java.io.File;







import okhttp3.MediaType;



import okhttp3.MultipartBody;



import okhttp3.RequestBody;



import retrofit2.Call;



import retrofit2.Callback;



import retrofit2.Response;







/**

 * ViewModel for creating and uploading a new story/status (image + caption).

 * <p>

 * I use this on the "add status" screen. It builds a multipart request,

 * shows uploading state, and tells the UI when to close or show an error.

 */

public class CreateStatusViewModel extends ViewModel {







    private final StatusRepository repository;



    /** True when the status was posted successfully. */

    private final MutableLiveData<Boolean> success = new MutableLiveData<>();



    /** User-facing error if upload fails. */

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();



    /** True while the multipart upload is in progress. */

    private final MutableLiveData<Boolean> uploading = new MutableLiveData<>(false);







    /** Creates the ViewModel and status repository. */

    public CreateStatusViewModel() {



        repository = new StatusRepository();



    }







    /**

     * Whether the last status upload succeeded.

     *

     * @return LiveData true on success

     */

    public MutableLiveData<Boolean> getSuccess() {



        return success;



    }







    /**

     * Error message from validation or the server.

     *

     * @return LiveData with error text

     */

    public MutableLiveData<String> getErrorMessage() {



        return errorMessage;



    }







    /**

     * Whether an upload is currently running (for progress UI).

     *

     * @return LiveData true while uploading

     */

    public MutableLiveData<Boolean> getUploading() {



        return uploading;



    }







    /**

     * Converts the image Uri to a file and uploads status to the backend.

     *

     * @param context needed to read the image Uri

     * @param userId who is posting the status

     * @param imageUri picked image from gallery or camera

     * @param caption optional text under the status

     */

    public void uploadStatus(Context context, int userId, Uri imageUri, String caption) {



        if (userId <= 0 || imageUri == null) {



            errorMessage.setValue("Invalid status");



            return;



        }







        File imageFile = FileUtils.uriToFile(context, imageUri);



        if (imageFile == null) {



            errorMessage.setValue("Image failed to load");



            return;



        }







        uploading.setValue(true);



        RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));



        RequestBody captionPart = RequestBody.create(



                MediaType.parse("text/plain"),



                caption != null ? caption : ""



        );



        RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageFile);



        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), imageBody);







        repository.createStatus(userIdPart, captionPart, imagePart).enqueue(new Callback<StatusResponse>() {



            @Override



            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {



                uploading.setValue(false);



                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {



                    success.setValue(true);



                } else {



                    success.setValue(false);



                    errorMessage.setValue(response.body() != null ? response.body().getMessage() : "Upload failed");



                }



            }







            @Override



            public void onFailure(Call<StatusResponse> call, Throwable t) {



                uploading.setValue(false);



                success.setValue(false);



                errorMessage.setValue("Network error");



            }



        });



    }



}



