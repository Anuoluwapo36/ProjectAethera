package com.example.projectaethera.model.remote;



import com.google.gson.annotations.SerializedName;



/**

 * One notification item from the notifications list API.

 * Describes who did what (like, follow, comment, etc.) and when.

 */

public class NotificationItem {

    private int notificationId;

    private int userId;

    private int actorId;

    private String type;

    private Integer postId;

    private Integer statusId;

    private String message;



    @SerializedName("isRead")

    private int readFlag;



    private String createdAt;

    private String actorUsername;



    @SerializedName("actorProfileImage")

    private String actorProfileImage;



    /** @return id of this notification */

    public int getNotificationId() {

        return notificationId;

    }



    /** @return id of the user who receives the notification */

    public int getUserId() {

        return userId;

    }



    /** @return id of the user who triggered the notification */

    public int getActorId() {

        return actorId;

    }



    /** @return type of event, such as like or follow */

    public String getType() {

        return type;

    }



    /** @return related post id, or null if not about a post */

    public Integer getPostId() {

        return postId;

    }



    /** @return related status id, or null if not about a status */

    public Integer getStatusId() {

        return statusId;

    }



    /** @return display message for the notification */

    public String getMessage() {

        return message;

    }



    /** @return 1 if read, 0 if still unread */

    public int getIsRead() {

        return readFlag;

    }



    /** @return when the notification was created */

    public String getCreatedAt() {

        return createdAt;

    }



    /** @return username of the user who caused the notification */

    public String getActorUsername() {

        return actorUsername;

    }



    /** @return profile image URL of the user who caused the notification */

    public String getActorProfileImage() {

        return actorProfileImage;

    }

}

