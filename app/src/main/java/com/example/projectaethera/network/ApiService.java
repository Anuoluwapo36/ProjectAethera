package com.example.projectaethera.network;

import com.example.projectaethera.model.remote.ChangePasswordResponse;
import com.example.projectaethera.model.remote.CheckEmailRequest;
import com.example.projectaethera.model.remote.CheckEmailResponse;
import com.example.projectaethera.model.remote.CommentCountResponse;
import com.example.projectaethera.model.remote.CommentResponse;
import com.example.projectaethera.model.remote.CountResponse;
import com.example.projectaethera.model.remote.FollowResponse;
import com.example.projectaethera.model.remote.GetCommentResponse;
import com.example.projectaethera.model.remote.HomeFeedResponse;
import com.example.projectaethera.model.remote.IsFollowingResponse;
import com.example.projectaethera.model.remote.LoginResponse;
import com.example.projectaethera.model.remote.MarkReadRequest;
import com.example.projectaethera.model.remote.NotificationsResponse;
import com.example.projectaethera.model.remote.UnreadCountResponse;
import com.example.projectaethera.model.remote.PasswordResetRequest;
import com.example.projectaethera.model.remote.PostResponse;
import com.example.projectaethera.model.remote.ProfileRequest;
import com.example.projectaethera.model.remote.RegisterResponse;
import com.example.projectaethera.model.remote.LoginRequest;
import com.example.projectaethera.model.remote.RegisterRequest;
import com.example.projectaethera.model.remote.SearchResponse;
import com.example.projectaethera.model.remote.StatusFeedResponse;
import com.example.projectaethera.model.remote.StatusResponse;
import com.example.projectaethera.model.remote.StatusUserResponse;
import com.example.projectaethera.model.remote.UserPostResponse;
import com.example.projectaethera.model.remote.UserResponse;
import com.example.projectaethera.model.remote.VerifyCodeRequest;
import com.example.projectaethera.model.remote.VerifyCodeResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit interface that defines all backend API endpoints for the app.
 * Each method maps to one HTTP call on the server.
 */
public interface ApiService {

    /**
     * Calls POST /api/login to sign in a user with email and password.
     *
     * @param loginRequest the email and password to send to the server
     * @return a Call that delivers the login result (success, message, userId)
     */
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    /**
     * Calls POST /api/register to create a new user account.
     *
     * @param registerRequest the username, email, password, and terms flag
     * @return a Call that delivers the registration result
     */
    @POST("register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    /**
     * Calls POST /api/checkEmail to see if an email is already registered.
     *
     * @param checkEmailRequest the email address to check
     * @return a Call that tells us if the email exists or not
     */
    @POST("checkEmail")
    Call<CheckEmailResponse> checkEmail(@Body CheckEmailRequest checkEmailRequest);

    /**
     * Calls POST /api/verifyCode to check the password-reset code the user entered.
     *
     * @param verifyCodeRequest the verification code from the email
     * @return a Call that delivers whether the code is valid
     */
    @POST("verifyCode")
    Call<VerifyCodeResponse> verifyCode(@Body VerifyCodeRequest verifyCodeRequest);

    /**
     * Calls POST /api/resetPassword to set a new password after code verification.
     *
     * @param resetRequest the user id and new password details
     * @return a Call that delivers the password change result
     */
    @POST("resetPassword")
    Call<ChangePasswordResponse> resetPassword(@Body PasswordResetRequest resetRequest);

    /**
     * Calls POST /api/createPost to upload a new post with images.
     *
     * @param userId  the id of the user creating the post
     * @param caption the text caption for the post
     * @param image   one or more image files to attach
     * @return a Call that delivers the created post data
     */
    @Multipart
    @POST("createPost")
    Call<PostResponse> uploadPost(
            @Part("userId") RequestBody userId,
            @Part("caption") RequestBody caption,
            @Part List<MultipartBody.Part> image
    );

    /**
     * Calls GET /api/user/{userId} to fetch all posts for a user profile.
     *
     * @param userId the id of the user whose posts we want
     * @return a Call that delivers a list of posts
     */
    @GET("user/{userId}")
    Call<UserPostResponse> getUserPost(@Path("userId") int userId);

    /**
     * Calls GET /api/userDetails/{userId} to get a user's profile information.
     *
     * @param userId the id of the user to look up
     * @return a Call that delivers profile details (username, bio, image, etc.)
     */
    @GET("userDetails/{userId}")
    Call<UserResponse> getUserDetails(@Path("userId") int userId);

    /**
     * Calls PUT /api/updateUserDetails/{userId} to update profile text fields.
     *
     * @param userId         the id of the user being edited
     * @param profileRequest the new username, bio, profession, etc.
     * @return a Call that delivers the updated profile
     */
    @PUT("updateUserDetails/{userId}")
    Call<UserResponse> editProfile(
            @Path("userId")int userId,
        @Body ProfileRequest profileRequest);

    /**
     * Calls PUT /api/uploadProfilePicture/{userId} to upload a new profile photo.
     *
     * @param userId       the id of the user
     * @param profileImage the image file to upload
     * @return a Call that delivers the updated profile with new image URL
     */
    @Multipart
    @PUT("uploadProfilePicture/{userId}")
    Call<UserResponse> updateProfile(
            @Path("userId") int userId,
            @Part MultipartBody.Part profileImage
    );

    /**
     * Calls POST /api/like to like a post.
     *
     * @param userId the id of the user who is liking
     * @param postId the id of the post to like
     * @return a Call that delivers the updated post (like count, etc.)
     */
    @FormUrlEncoded
    @POST("like")
    Call<PostResponse> likePost(
            @Field("userId") int userId,
            @Field("postId") int postId);

    /**
     * Calls POST /api/unlike to remove a like from a post.
     *
     * @param userId the id of the user who is unliking
     * @param postId the id of the post to unlike
     * @return a Call that delivers the updated post
     */
    @FormUrlEncoded
    @POST("unlike")
    Call<PostResponse> unlikePost(
            @Field("userId") int userId,
            @Field("postId") int postId
    );

    /**
     * Calls GET /api/isLiked to check if a user already liked a post.
     *
     * @param userId the id of the user
     * @param postId the id of the post
     * @return a Call that delivers true if liked, false if not
     */
   @GET("isLiked")
    Call<Boolean> isLiked(
            @Query("userId") int userId,
            @Query("postId") int postId
   );

    /**
     * Calls GET /api/postDetails/{postId} to get full details of one post.
     *
     * @param postId the id of the post
     * @param userId the id of the viewer (used for like/follow status)
     * @return a Call that delivers the post with images and counts
     */
   @GET("postDetails/{postId}")
    Call<PostResponse> postDetails(
            @Path("postId") int postId,
            @Query("userId") int userId
            );

    /**
     * Calls POST /api/deletePost to remove a post owned by the user.
     *
     * @param userId the id of the user deleting the post
     * @param postId the id of the post to delete
     * @return a Call that delivers the delete result
     */
   @FormUrlEncoded
   @POST("deletePost")
   Call<PostResponse> deletePost(
           @Field("userId") int userId,
           @Field("postId") int postId
   );

    /**
     * Calls GET /api/homeFeed to load the main feed of posts from followed users.
     *
     * @param userId the id of the logged-in user
     * @param page   the page number for pagination
     * @return a Call that delivers a page of feed posts
     */
   @GET("homeFeed")
    Call<HomeFeedResponse> homeFeed(
            @Query("userId") int userId,
            @Query("page") int page
    );

    /**
     * Calls POST /api/addComment to add a comment (or reply) on a post.
     *
     * @param postId   the id of the post being commented on
     * @param userId   the id of the user writing the comment
     * @param comment  the comment text
     * @param reply_to the id of the parent comment if this is a reply, or null
     * @return a Call that delivers the comment result
     */
    @FormUrlEncoded
    @POST("addComment")
    Call<CommentResponse> addComment(
            @Field("postId") int postId,
            @Field("userId") int userId,
            @Field("comment") String comment,
            @Field("reply_to") String reply_to
    );

    /**
     * Calls GET /api/getComments to fetch all comments for a post.
     *
     * @param postId the id of the post
     * @return a Call that delivers the list of comments
     */
    @GET("getComments")
    Call<GetCommentResponse> getComments(
            @Query("postId") int postId
    );

    /**
     * Calls GET /api/commentsCount to get how many comments a post has.
     *
     * @param postId the id of the post
     * @return a Call that delivers the total comment count
     */
    @GET("commentsCount")
    Call<CommentCountResponse> getCommentCount (
            @Query("postId") int postId
    );

    /**
     * Calls GET /api/search to find users by username or name.
     *
     * @param userId the id of the user doing the search
     * @param query  the search text typed by the user
     * @return a Call that delivers matching user profiles
     */
    @GET("search")
    Call<SearchResponse> searching(
            @Query("userId") int userId,
            @Query("query")String query
    );

    /**
     * Calls POST /api/follow to follow or unfollow another user (toggle).
     *
     * @param followerId  the id of the user who clicks follow
     * @param followingId the id of the user being followed/unfollowed
     * @return a Call that delivers the action taken (followed or unfollowed)
     */
    @FormUrlEncoded
    @POST("follow")
    Call<FollowResponse> toggleFollow(
            @Field("followerId") int followerId,
            @Field("followingId") int followingId
    );

    /**
     * Calls GET /api/isFollowing/{followerId}/{followingId} to check follow status.
     *
     * @param followerId  the id of the potential follower
     * @param followingId the id of the user being checked
     * @return a Call that delivers whether they are following
     */
    @GET("isFollowing/{followerId}/{followingId}")
    Call<IsFollowingResponse> isFollowing(
            @Path("followerId") int followerId,
            @Path("followingId") int followingId
    );

    /**
     * Calls GET /api/followers/{userId} to count how many followers a user has.
     *
     * @param userId the id of the user
     * @return a Call that delivers the follower count
     */
    @GET("followers/{userId}")
    Call<CountResponse> getFollowersCount(@Path("userId") int userId);

    /**
     * Calls GET /api/following/{userId} to count how many users someone follows.
     *
     * @param userId the id of the user
     * @return a Call that delivers the following count
     */
    @GET("following/{userId}")
    Call<CountResponse> getFollowingCount(@Path("userId") int userId);

    /**
     * Calls GET /api/notifications/{userId} to load a page of notifications.
     *
     * @param userId the id of the user
     * @param page   the page number
     * @param limit  how many notifications per page
     * @return a Call that delivers the notification list
     */
    @GET("notifications/{userId}")
    Call<NotificationsResponse> getNotifications(
            @Path("userId") int userId,
            @Query("page") int page,
            @Query("limit") int limit
    );

    /**
     * Calls GET /api/notifications/{userId}/unread-count for the badge count.
     *
     * @param userId the id of the user
     * @return a Call that delivers how many unread notifications exist
     */
    @GET("notifications/{userId}/unread-count")
    Call<UnreadCountResponse> getUnreadCount(@Path("userId") int userId);

    /**
     * Calls PUT /api/notifications/read/{notificationId} to mark one as read.
     *
     * @param notificationId the id of the notification
     * @param request          contains the userId for verification
     * @return a Call that delivers the updated notification list
     */
    @PUT("notifications/read/{notificationId}")
    Call<NotificationsResponse> markNotificationRead(
            @Path("notificationId") int notificationId,
            @Body MarkReadRequest request
    );

    /**
     * Calls PUT /api/notifications/{userId}/read-all to mark every notification read.
     *
     * @param userId the id of the user
     * @return a Call that delivers the result after marking all read
     */
    @PUT("notifications/{userId}/read-all")
    Call<NotificationsResponse> markAllNotificationsRead(@Path("userId") int userId);

    /**
     * Calls GET /api/statusFeed to load the stories/status bar for the home screen.
     *
     * @param userId the id of the logged-in user
     * @return a Call that delivers status groups grouped by user
     */
    @GET("statusFeed")
    Call<StatusFeedResponse> getStatusFeed(@Query("userId") int userId);

    /**
     * Calls GET /api/statuses/user/{userId} to load one user's status stories.
     *
     * @param targetUserId the id of the user whose statuses we want
     * @param viewerId     the id of the person viewing (for seen/liked flags)
     * @return a Call that delivers that user's status list
     */
    @GET("statuses/user/{userId}")
    Call<StatusUserResponse> getUserStatuses(
            @Path("userId") int targetUserId,
            @Query("viewerId") int viewerId
    );

    /**
     * Calls POST /api/createStatus to upload a new status/story image.
     *
     * @param userId  the id of the user posting the status
     * @param caption optional text on the status
     * @param image   the image file for the status
     * @return a Call that delivers the create result
     */
    @Multipart
    @POST("createStatus")
    Call<StatusResponse> createStatus(
            @Part("userId") RequestBody userId,
            @Part("caption") RequestBody caption,
            @Part MultipartBody.Part image
    );

    /**
     * Calls POST /api/status/view to record that a user viewed a status.
     *
     * @param statusId the id of the status that was viewed
     * @param viewerId the id of the user who viewed it
     * @return a Call that delivers the view result
     */
    @FormUrlEncoded
    @POST("status/view")
    Call<StatusResponse> markStatusViewed(
            @Field("statusId") int statusId,
            @Field("viewerId") int viewerId
    );

    /**
     * Calls POST /api/status/like to like a status/story.
     *
     * @param userId   the id of the user liking
     * @param statusId the id of the status
     * @return a Call that delivers the updated like count
     */
    @FormUrlEncoded
    @POST("status/like")
    Call<StatusResponse> likeStatus(
            @Field("userId") int userId,
            @Field("statusId") int statusId
    );

    /**
     * Calls POST /api/status/unlike to remove a like from a status.
     *
     * @param userId   the id of the user unliking
     * @param statusId the id of the status
     * @return a Call that delivers the updated like count
     */
    @FormUrlEncoded
    @POST("status/unlike")
    Call<StatusResponse> unlikeStatus(
            @Field("userId") int userId,
            @Field("statusId") int statusId
    );

    /**
     * Calls POST /api/deleteStatus to remove a status owned by the user.
     *
     * @param userId   the id of the user deleting
     * @param statusId the id of the status to delete
     * @return a Call that delivers the delete result
     */
    @FormUrlEncoded
    @POST("deleteStatus")
    Call<StatusResponse> deleteStatus(
            @Field("userId") int userId,
            @Field("statusId") int statusId
    );
}
