package com.example.projectaethera.model.remote;



import com.google.gson.annotations.SerializedName;



/**

 * JSON response that returns a single numeric count,

 * used for follower count and following count APIs.

 */

public class CountResponse {

    @SerializedName("count")

    private Number count;



    /** @return the count value, or 0 if the server sent null */

    public int getCount() {

        return count != null ? count.intValue() : 0;

    }

}

