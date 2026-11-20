package com.example.projectaethera.model.entity;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

/**
 * A comment (or reply) on a post, including author info from the API.
 * Fields: commentId, postId, userId, comment, reply_to, createAt, username, profile_image.
 */
public class Comment {

    /** Unique id of this comment. */
    @SerializedName("commentId")
    private int commentId;
    /** Post this comment belongs to. */
    @SerializedName("postId")
    private int postId;
    /** User who wrote the comment. */
    @SerializedName("userId")
    private int userId;
    /** The comment text. */
    @SerializedName("comment")
    private String comment;
    /** Parent comment id if this is a reply; null for top-level comments. */
    @SerializedName("reply_to")
    private Integer reply_to;
    /** When the comment was created. */
    @SerializedName("createAt")
    private Timestamp createAt;
    /** Username of the comment author (from API join). */
    @SerializedName("username")
    private String username;
    /** Profile image URL of the comment author. */
    @SerializedName("profile_image")
    private String profile_image;

    /**
     * Full constructor with all main fields.
     *
     * @param commentId id of the comment
     * @param postId    id of the post
     * @param userId    id of the author
     * @param comment   comment text
     * @param reply_to  parent comment id or 0
     * @param createAt  creation time
     */
    public Comment(int commentId, int postId, int userId, String comment, int reply_to, Timestamp createAt) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.comment = comment;
        this.reply_to = reply_to;
        this.createAt = createAt;
    }

    /**
     * Builds a small UserProfile from username and profile_image for the UI.
     *
     * @return profile object for showing the comment author
     */
    public UserProfile getUserProfile() {
        UserProfile profile = new UserProfile();
        profile.setUsername(username);
        profile.setImage_uri(profile_image);
        return profile;
    }

    /**
     * @return comment id
     */
    public int getCommentId() {
        return commentId;
    }

    /**
     * @param commentId new comment id
     */
    public void setCommentId(int commentId) {
        this.commentId = commentId;
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
     * @return comment text
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment new comment text
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return parent comment id for replies
     */
    public Integer getReply_to() {
        return reply_to;
    }

    /**
     * @param reply_to parent comment id
     */
    public void setReply_to(Integer reply_to) {
        this.reply_to = reply_to;
    }

    /**
     * @return when the comment was posted
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
