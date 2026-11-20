package com.example.projectaethera.model.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

/**
 * Represents a social media post with images, author profile, and engagement counts.
 * Fields: postId, userId, caption, createAt, postImageList, userProfile, all_images,
 * likeCount, isLiked, commentCount, followingStatus.
 */
public class Post implements Serializable {
    /** Unique id of this post. */
    private int postId;
    /** Id of the user who created the post. */
    private  int userId;
    /** Text caption under the images. */
    private String caption;
    /** When the post was created. */
    private Timestamp createAt;
    /** List of images attached to this post. */
    private List<PostImage> postImageList;
    /** Profile info of the post author. */
    private UserProfile userProfile;
    /** Comma-separated image paths from API (alternate format). */
    @SerializedName("all_images")
    private String all_images;

    /** Total number of likes on this post. */
    private int likeCount;
    /** 1 if the current user liked this post, 0 if not. */
    @SerializedName("isLiked")
    private int isLiked;
    /** Total number of comments. */
    private int commentCount;
    /** 1 if the viewer follows the author, 0 if not. */
    @SerializedName(value = "followingStatus", alternate = {"isFollowing"})
    private int followingStatus;

    /**
     * Creates a post with core fields and author profile.
     *
     * @param postId      post id
     * @param userId      author id
     * @param caption     caption text
     * @param createAt    creation time
     * @param userProfile author profile
     */
    public Post(int postId, int userId, String caption, Timestamp createAt, UserProfile userProfile) {
        this.postId = postId;
        this.userId = userId;
        this.caption = caption;
        this.createAt = createAt;
        this.userProfile = userProfile;
    }

    /** Empty constructor for Gson. */
    public Post( ) {
    }

    /**
     * @return post id
     */
    public int getPostId() {
        return postId;
    }

    /**
     * @param postId new post id
     */
    public void setPostId(int postId) {
        this.postId = postId;
    }

    /**
     * @return author user id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId new author id
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return caption text
     */
    public String getCaption() {
        return caption;
    }

    /**
     * @param caption new caption
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * @return when the post was created
     */
    public Timestamp getCreateAt() {
        return createAt;
    }

    /**
     * @param createAt new timestamp
     */
    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    /**
     * @return list of post images
     */
    public List<PostImage> getPostImageList() {
        return postImageList;
    }

    /**
     * @return comma-separated image paths string from API
     */
    public String getAll_images() {
        return all_images;
    }

    /**
     * @param postImageList new image list
     */
    public void setPostImageList(List<PostImage> postImageList) {
        this.postImageList = postImageList;
    }

    /**
     * @return author profile
     */
    public UserProfile getUserProfile() {
        return userProfile;
    }

    /**
     * @return total likes
     */
    public int getLikeCount() {
        return likeCount;
    }

    /**
     * @param likeCount new like count
     */
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * @return 1 if liked by current user
     */
    public int getIsLiked() {
        return isLiked;
    }

    /**
     * @param isLiked liked flag (0 or 1)
     */
    public void setIsLiked(int isLiked) {
        this.isLiked = isLiked;
    }

    /**
     * @param userProfile new author profile
     */
    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    /**
     * @return total comments
     */
    public int getCommentCount() {
        return commentCount;
    }

    /**
     * @param commentCount new comment count
     */
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    /**
     * @return 1 if viewer follows the author
     */
    public int getIsFollowing() {
        return followingStatus;
    }

    /**
     * @param isFollowing follow flag (0 or 1)
     */
    public void setIsFollowing(int isFollowing) {
        this.followingStatus = isFollowing;
    }

    /**
     * One image row linked to a post in the database.
     * Fields: post_imageId, userId, postId, imageUri, createAt.
     */
    public  static  class PostImage{
        /** Unique id of this image record. */
        private int post_imageId;
        /** Owner user id. */
        private int userId;
        /** Post this image belongs to. */
        private int postId;
        /** URL or path to the image file. */
        private String imageUri;
        /** When the image was uploaded. */
        private Timestamp createAt;

        /**
         * Full constructor for a post image.
         *
         * @param post_imageId image record id
         * @param userId       owner id
         * @param postId       post id
         * @param imageUri     image URL
         * @param createAt     upload time
         */
        public PostImage(int post_imageId, int userId, int postId, String imageUri, Timestamp createAt) {
            this.post_imageId = post_imageId;
            this.userId = userId;
            this.postId = postId;
            this.imageUri = imageUri;
            this.createAt = createAt;
        }

        /** Empty constructor for Gson. */
        public PostImage() {
        }

        /**
         * @return image record id
         */
        public int getPost_imageId() {
            return post_imageId;
        }

        /**
         * @param post_imageId new id
         */
        public void setPost_imageId(int post_imageId) {
            this.post_imageId = post_imageId;
        }

        /**
         * @return owner user id
         */
        public int getUserId() {
            return userId;
        }

        /**
         * @param userId new user id
         */
        public void setUserId(int userId) {
            this.userId = userId;
        }

        /**
         * @return post id
         */
        public int getPostId() {
            return postId;
        }

        /**
         * @param postId new post id
         */
        public void setPostId(int postId) {
            this.postId = postId;
        }

        /**
         * @return image URI
         */
        public String getImageUri() {
            return imageUri;
        }

        /**
         * @param imageUri new image URI
         */
        public void setImageUri(String imageUri) {
            this.imageUri = imageUri;
        }

        /**
         * @return upload timestamp
         */
        public Timestamp getCreateAt() {
            return createAt;
        }

        /**
         * @param createAt new timestamp
         */
        public void setCreateAt(Timestamp createAt) {
            this.createAt = createAt;
        }
    }

}
