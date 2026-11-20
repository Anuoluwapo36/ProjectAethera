package com.example.projectaethera.viewmodel;







import android.content.Context;



import android.net.Uri;







import androidx.lifecycle.MutableLiveData;



import androidx.lifecycle.ViewModel;







import com.example.projectaethera.model.remote.ProfileRequest;



import com.example.projectaethera.model.remote.UserResponse;



import com.example.projectaethera.repository.UserRepository;



import com.example.projectaethera.utils.FileUtils;







import java.io.File;







import okhttp3.MediaType;



import okhttp3.MultipartBody;



import okhttp3.RequestBody;



import retrofit2.Call;



import retrofit2.Callback;



import retrofit2.Response;







/**

 * ViewModel for editing profile text fields and uploading a new profile photo.

 * <p>

 * I split text edits ({@link #editProfile}) and image upload ({@link #updateProfile})

 * because the API uses different endpoints. The fragment observes separate success

 * flags so it knows whether username/bio saved or just the picture.

 */

public class EditProfileViewModel extends ViewModel {







    /** True after username/bio/etc. saved successfully. */

    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();



    /** Error from the text profile update call. */

    private final MutableLiveData<String> updateErrorMessage = new MutableLiveData<>();



    /** Latest user payload from the server — used to refresh UI after save. */

    private final MutableLiveData<UserResponse> updatedUser = new MutableLiveData<>();







    /** True after profile image upload succeeded. */

    private final MutableLiveData<Boolean> updateProfileSuccess = new MutableLiveData<>();



    /** Error from the image upload call. */

    private final MutableLiveData<String> updateProfileErrorMessage = new MutableLiveData<>();



    private UserRepository userRepository;







    /** Creates the ViewModel and user repository. */

    public EditProfileViewModel() {



        this.userRepository = new UserRepository();



    }







    /**

     * Saves text profile fields (username, bio, etc.) for the given user.

     *

     * @param userId logged-in user's id

     * @param profileRequest fields the user changed on the form

     */

    public void editProfile(int userId, ProfileRequest profileRequest) {



        userRepository.editProfile(userId, profileRequest).enqueue(new Callback<UserResponse>() {



            @Override



            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {



                if (response.isSuccessful() && response.body() != null) {



                    updatedUser.postValue(response.body());



                    updateSuccess.postValue(true);



                } else {



                    updateErrorMessage.postValue("update failed");



                }



            }







            @Override



            public void onFailure(Call<UserResponse> call, Throwable throwable) {



                updateErrorMessage.postValue("Network error");



            }



        });



    }







    /**

     * Uploads a new profile picture from a gallery/camera Uri.

     * <p>

     * I convert the Uri to a File first because Retrofit multipart needs a file on disk.

     *

     * @param context needed for {@link FileUtils#uriToFile}

     * @param userId logged-in user's id

     * @param imageUri string form of the picked image Uri

     */

    public void updateProfile(Context context, int userId, String imageUri) {



        File imageFile = FileUtils.uriToFile(context, Uri.parse(imageUri));



        if (imageFile == null) {



            updateProfileErrorMessage.postValue("Image failed to load");



            return;



        }







        MultipartBody.Part filePart = MultipartBody.Part.createFormData(



                "profileImage",



                imageFile.getName(),



                RequestBody.create(MediaType.parse("image/*"), imageFile)



        );







        userRepository.updateProfile(userId, filePart).enqueue(new Callback<UserResponse>() {



            @Override



            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {



                if (response.isSuccessful()) {



                    if (response.body() != null) {



                        updatedUser.postValue(response.body());



                    }



                    updateProfileSuccess.postValue(true);



                } else {



                    updateProfileErrorMessage.postValue("upload profile picture failed");



                }



            }







            @Override



            public void onFailure(Call<UserResponse> call, Throwable throwable) {



                updateProfileErrorMessage.postValue("Network error");



            }



        });



    }







    /**

     * Latest user data after a successful edit or photo upload.

     *

     * @return LiveData with updated profile from server

     */

    public MutableLiveData<UserResponse> getUpdatedUser() {



        return updatedUser;



    }







    /**

     * Resets success flags so old events do not re-trigger observers.

     */

    public void clearUpdateFlags() {



        updateSuccess.setValue(false);



        updateProfileSuccess.setValue(false);



    }







    /**

     * Whether the text profile update succeeded.

     *

     * @return LiveData true after editProfile succeeds

     */

    public MutableLiveData<Boolean> getUpdateSuccess() {



        return updateSuccess;



    }







    /**

     * Error message from the text profile update.

     *

     * @return LiveData with error text

     */

    public MutableLiveData<String> getUpdateErrorMessage() {



        return updateErrorMessage;



    }







    /**

     * Whether the profile photo upload succeeded.

     *

     * @return LiveData true after updateProfile succeeds

     */

    public MutableLiveData<Boolean> getUpdateProfileSuccess() {



        return updateProfileSuccess;



    }







    /**

     * Error message from the profile photo upload.

     *

     * @return LiveData with error text

     */

    public MutableLiveData<String> getUpdateProfileErrorMessage() {



        return updateProfileErrorMessage;



    }



}



